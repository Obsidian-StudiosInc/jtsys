/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 * Based on VirtualNet.pm by Ivan Kohler <ivan-virtualnet@420.am>
 */
public class Tsys {

    private boolean DEBUG = true;
    private final char STX = 0x02;//0b1100100
    private final char ETX = 0x03;//0b0000011
    private final char FS  = 0x1c;//0b0011100
    private final char GS  = 0x1d;//0b
    private final char ETB = 0x17;//0b0010111

    // A/N Device Codes
    private final char[] DEVICE_CODES = {                   // Device Code (4.62)
        '0',                                                // 0="Unkown or Unsure"
        'C',                                                // C="P.C." 
        'D',                                                // D="Dial Terminal"
        'E',                                                // E="Electronic Cash Register"
        'I',                                                // I="In-Store Processor"
        'M',                                                // M="Main Frame"
        'P',                                                // P="Reserved POS-Port®"
        'Q',                                                // Q="Reserved Third party software developer"
        'R',                                                // R="POS-Port®"
        'S',                                                // S="POS Partner®"
        'Z'                                                 // Z="Suppress PS2000/Merit response fields"
    };
    private final short[] COUNTRY_CODES = { 840 };   // 840=US
    private final short[] CURRENCY_CODES = { 840 };  // 840=USD
    private final short[] TIME_ZONES = {
        705,                                                // 705=EST
        706,                                                // 706=CST
        707,                                                // 707=MST
        708,                                                // 708=PST
    };
    private final String[] MIME = {
        "x-Visa-II/x-auth",                                 // Auth mime
        "x-Visa-II/x-settle"                                // Settle mime
    };
    private final String[] LANGUAGES = { "00" };      // 00=English
 
    private final String[] ERROR_TYPES= {
        "B","Blocked Terminal",
        "C","Card Type Error",
        "D","Device Error",
        "E","Error in Batch",
        "S","Sequence Error",
        "T","Transmission Error",
        "U","Unknown Error",
        "V","Routing Error"
    };
    private final String[] ERROR_RECORD_TYPES= {
        "H","Header Record",
        "P","Parameter Record",
        "D","Detail Record",
        "T","Trailer Record"
    };

    public static void main(String[] args) {
//        System.setProperty("https.cipherSuites", "TLS_RSA_WITH_AES_128_CBC_SHA256");
        Tsys tsys = new Tsys();
        tsys.submit("Auth",testMerchant());
    }

    private static Merchant testMerchant() {
        Merchant m = new Merchant();
        m.setId("999999999911");
        m.setBin("999995");
        m.setAgent("000000");
        m.setChain("000000");
        m.setStore("0011");
        m.setTerminal("9911");
        m.setMcc("5999");
        m.setIndustryCode('D');
        m.setName("Internet Service Provider");
        m.setCity("Gloucester");
        m.setState("VA");
        m.setZip("543211420");
        m.setPhone("800-1234567");
        m.setV("00000001");
        return(m);
    }

    private String seperator(String obj,
                             String s,
                             int length,
                             char etbx) throws Exception {
        if(s.length()!=length)
            throw new Exception(obj+" length is "+s.length()+" and should be "+length);
        return (STX+s+etbx+lrc(s+etbx));
    }

    private String testRequest() throws Exception {
        String t = "D4.999995";   // D2. DO
        StringBuilder msg = new StringBuilder(); 
        msg.append(seperator("test",t,t.length(),ETX));
        return(msg.toString());
    }

    private void submit(String action,
                        Merchant merchant) {
        try {
            String mime = MIME[0];
            String request = new String();
            if(action.equals("settle")) {
                mime = MIME[1];
                request = settleRequest(merchant,
                                        "cardNumber",
                                        "transSequenceNumber",
                                        "aci",
                                        "authSourceCode",
                                        "authCode",
                                        "avsCode",
                                        "transId",
                                        "validationCode",
                                        "amount",
                                        "purchaseId",
                                        "batchNumber");
            } else {
                request = authRequest(merchant,
                                      "0001",
                                      "4012888888881881",
                                      "0218",
                                      "8320",
                                      "85284",
                                      "1.00");
            }
//            request = test();
            URL url = new URL("https://ssl1.tsysacquiring.net/scripts/gateway.dll?transact");
            HttpsURLConnection httpsCon = (HttpsURLConnection)url.openConnection();
            httpsCon.setRequestMethod("POST");
            httpsCon.setDoOutput(true);
            httpsCon.setDoInput(true);
            httpsCon.setUseCaches(false);
            httpsCon.setRequestProperty("Content-Type", mime);
            httpsCon.setRequestProperty("Content-Length", String.valueOf(request.length()));

            System.out.printf("\nRequest :\n\t%s\n\n",
                              request);
            DataOutputStream wr = new DataOutputStream(httpsCon.getOutputStream());
            wr.write(getEvenParity(request));
            wr.flush();
            System.out.printf("Cipher                   : %s\n"
                             +"IP                       : %s\n",
                              httpsCon.getCipherSuite(),
                              InetAddress.getByName(url.getHost()).getHostAddress());
            InputStream is = httpsCon.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1)
                result.write(buffer, 0, length);
            is.close();
            httpsCon.disconnect();
            String response = new String(removeParity(result.toByteArray()));
            String error_pattern = "^(\\d+)\\s+\\-\\s+(\\S.*)$";            // Error Pattern
            Matcher em = Pattern.compile(error_pattern).matcher(response);
            if(em.matches()) {
                    System.out.printf("Error :"
                                     +"\tCode : %s\n"
                                     +"\tText : %s\n\n\n",
                                      em.group(1),
                                      em.group(2));
            } else {
                Matcher code = Pattern.compile(authResponseRexEx()).matcher(response);
                if(code.matches()) {
                    System.out.printf("Response Code            : %s\n"
                                     +"Approval Code            : %s\n"
                                     +"Auth Response Text       : %s\n"
                                     +"AVS Result Code          : %s\n"
                                     +"Retrieval Reference Num  : %s\n"
                                     +"Transaction Identifier   : %s\n"
                                     +"Validation Code          : %s\n"
                                     +"Group III Version Number : %s\n\n",
                                      code.group(1),
                                      code.group(2),
                                      code.group(3).trim(),
                                      code.group(4),
                                      code.group(5),
                                      code.group(6),
                                      code.group(7),
                                      code.group(8));
                }
            }
            if(DEBUG)
                System.out.printf("\nFull : %s \n\n",response);
        } catch(IOException ioe) {
            System.out.println(ioe);
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    /**
     * Auth (1080) Response RegEx
     * D-Format Credit Card Authorization Response Message RegEx
     * 
     * Group 1. 2    A/N Response Code XX  4.71
     * Group 2. 6    A/N Approval Code 4.8
     * Group 3. 16   A/N Auth Response Text  4.11
     * Group 4. 1    A/N AVS Result Code 4.3
     * Group 5. 12   A/N Retrieval Reference Num 4.72
     * Group 6. 0-15 A/N Transaction Identifier 4.91
     * Group 7. 0-4  A/N Validation Code 4.96
     * Group 8. 3    NUM Group III Version Number 4.44
     * 
     * @return String containing regex pattern to parse auth response regex
     */
    private String authResponseRexEx() {
        // D-Format Credit Card Authorization Response Message
        StringBuilder r = new StringBuilder();
                                                // Byte Length Format Field Description Content Section
        r.append(STX);
        r.append('E');                          // 1     1    Record Format E 4.68
        r.append("[024]");                      // 2     1    NUM Application Type 0=Single Transaction 4.7
                                                //                                2=Multiple Transaction
                                                //                                4=Interleaved
        r.append("\\.");                        // 3     1    Message Delimiter 4.63
        r.append("[A-Z ]");                     // 4     1    Returned ACI 4.73
        r.append("[0-9 ]{4}");                  // 5-8   4    NUM Store Number 4.82
        r.append("[0-9 ]{4}");                  // 9-12  4    NUM Terminal Number 4.85
        r.append("[0-9]");                      // 13    1    Authorization Source Code 4.12
        r.append("[0-9 ]{4}");                  // 14-17 4    NUM Transaction Sequence Num 4.92
        r.append("([0-9]{2})");                 // 18-19 2    Response Code XX  4.71
        r.append("([0-9A-Za-z ]{6})");          // 20-25 6    Approval Code 4.8
        r.append("[0-9]{6}");                   // 26-31 6    NUM Local Transaction Date MMDDYY 4.55
        r.append("[0-9]{6}");                   // 32-37 6    NUM Local Transaction Time HHMMSS 4.56
        r.append("([0-9A-Za-z ]{16})");         // 38-53 16   Auth Response Text  4.11
        r.append("([0-9A-Z ])");                // 54    1    AVS Result Code 4.3
        r.append("([0-9A-Za-z ]{12})");         // 55-66 12   Retrieval Reference Num 4.72
        r.append("[0-9A-Za-z ]");               // 67    1    Market Data Identifier 4.57
        r.append("([0-9A-Za-z ]{0,15})");       // -     0-15 Transaction Identifier 4.91
        r.append(FS);                           // -     1    Field Separator <FS>  4.41
        r.append("([0-9A-Za-z ]{0,4})");        // -     0-4  Validation Code 4.96
        r.append(FS);                           // -     1    Field Separator <FS> 4.41
        r.append("([0-9]{3})?");                // -     3    NUM Group III Version Number 4.44
        r.append("(.*)");
        return(r.toString());
    }

    private String authRequest(Merchant merchant,
                               String transSequenceNumber,
                               String cardNumber,
                               String expiration,
                               String address,
                               String zip,
                               String amount) throws Exception {
        //Byte Length Field: Content
        StringBuilder c = new StringBuilder("D4.");         // 1     1    Record format: D
                                                            // 2     1    Application Type: 4=Interleaved
                                                            // 3     1    Message Delimiter: .
        c.append(merchant.getBin());                        // 4-9   6    Acquirer BIN
        c.append(merchant.getId());                         // 10-21 12   Merchant Number
        c.append(merchant.getStore());                      // 22-25 4    Store Number
        c.append(merchant.getTerminal());                   // 26-29 4    Terminal Number
        c.append(DEVICE_CODES[7]);                          // 30    1    Device Code 1 PC or 7 Third Party
        c.append(merchant.getIndustryCode());               // 31    1    Industry Code
        c.append(CURRENCY_CODES[0]);                        // 32-34 3    Currency Code: 840=U.S. Dollars
        c.append(COUNTRY_CODES[0]);                         // 35-37 3    Country Code: 840=United States
        c.append(String.format("%-9s",merchant.getZip()));  // 38-46 9    (Merchant) City Code(Zip);
        c.append(LANGUAGES[0]);                             // 47-48 2    Language Indicator: 00=English
        c.append(TIME_ZONES[0]);                            // 49-51 3    Time Zone Differential: 705=EST
        c.append(merchant.getMcc());                        // 52-55 4    Metchant Category Code
        c.append('Y');                                      // 56    1    Requested ACI (Authorization Characteristics Indicator):
                                                            //            Y=Device is CPS capable
        c.append(transSequenceNumber);                      // 57-60 4    Tran Sequence Number
        c.append("56");                                     // 61-62 2    Auth Transaction Code: 56=Card Not Present
        c.append('N');                                      // 63    1    Cardholder ID Code: N=AVS
                                                            //            (Address Verification Data or
                                                            //             CPS/Card Not Present or
                                                            //             Electronic Commerce)
        c.append('@');                                      // 64    1    Account Data Source: @=No Cardreader

        if(c.length()!=64)
            throw new Exception("Content length is "+c.length()+" and should be 64\n"+c.toString());

        c.append(cardNumber).append(FS);                    // - 5-76  Customer Data Field: Acct#<FS>
        c.append(expiration).append(FS);                    //                              ExpDate<FS>
        c.append(FS);                                       // - 1 Field Separator
        address = address.replaceAll("[^a-zA-Z0-9]","");
        if(address.length()+zip.length()>28)
            address = address.substring(0,28-zip.length());
        c.append(address).append(' ').append(zip);          // - 0-29 Address Verification Data
        c.append(FS).append(FS);                            // - 2 Field Separator
        //String.format("%12s",amount.replace(".","")).replace(" ","0")
        c.append(amount.replace(".",""));                   // - 1-12 Transaction Amount
        c.append(FS).append(FS).append(FS);                 // - 3 Field Separator
        c.append(String.format("%-25s",merchant.getName())); // - 25 Merchant Name Left-Justified/Space-Filled
        c.append(String.format("%-13s",merchant.getCity()));                      // - 13 Customer Service Phone Number NNN-NNNNNNN (dash is required)
//        c.append(merchant.getPhone());                      // - 13 Customer Service Phone Number NNN-NNNNNNN (dash is required)
        c.append(merchant.getState());                      // - 2 Merchant State
        c.append(FS).append(FS).append(FS);                 // - 3 Field Separator
//        c.append("007");                                    // - 3 Group III Version Number: 014=MOTO/Electronic Commerce
//        String cvv2 = "";
                                                            // - 6 VISA CVV2, Mastercard CVC2, AMEX CID
                                                            // Position - Value Description
                                                            // 1 - 0 Card Verification Value is intentionally not provided
                                                            // 1 - 1 Card Verification Value is Present
        
                                                            // 2 - 0 Only the normal Response Code should be returned
                                                            // 2 - 1 Response Code and the CVV2 / CVC2 Result Code should be returned
                                                            // 3-6 - Card Verification Value as printed on card (right-justify/space-fill entry)
                                                            // If position 1 = 0, 2, or 9, positions 3-6 should be space-filled.
//        if(cvv2!=null &&
//           !cvv2.isEmpty() &&
//           !cvv2.startsWith("0") &&
//           !cvv2.startsWith("2") &&
//           !cvv2.startsWith("9"))
//            c.append("11").append(String.format("%4s",cvv2)); // CVV2 Present
//        else
//            c.append("00    ");                             // - CVV2 Not Present 
//        c.append(GS);                                       // - 1 Group Separator
        c.append("014");                                    // - 3 Group III Version Number: 014=MOTO/Electronic Commerce
        c.append('7');                                      // - 1 MOTO/Electronic Com. Ind: 7= Non-Authenticated
//        c.append(GS); 
//        c.append("020");                                    // - 3 Group III Version Number: 014=MOTO/Electronic Commerce
//        c.append("001131B002");                             // - 1 MOTO/Electronic Com. Ind: 7= Non-Authenticated
                                                            //     Security transaction, such as a channel-encrypted
                                                            //     transaction (e.g., ssl, DES or RSA)
//        c.append(GS);                            // - 2 Field Separator
        return(STX+c.toString()+ETX+lrc(c.toString()+ETX));
    }

    /* K 1081 Settle
        
    */
    private String settleRequest(Merchant merchant,
                                 String cardNumber,
                                 String transSequenceNumber,
                                 String aci,
                                 String authSourceCode,
                                 String authCode,
                                 String avsCode,
                                 String transId,
                                 String validationCode,
                                 String amount,
                                 String purchaseId,
                                 String batchNumber) throws Exception {
        /* K-Format Header Record (Base Group)
         * Byte Length Frmt Field description Content Section
         * Byte Length Field: Content (section)
         */
        StringBuilder h = new StringBuilder("K1.ZH@@@@");   // 1     1  A/N Record Format: K (4.154)
                                                            // 2     1  NUM Application Type: 1=Single Batch (4.10)
                                                            // 3     1  A/N Message Delimiter: . (4.123)
                                                            // 4     1  A/N X.25 Routing ID: Z (4.226)
                                                            // 5-9   5  A/N Record Type: H@@@@ (4.155)
        h.append(merchant.getBin());                        // 10-15 6  NUM Acquirer BIN  (4.2)
        h.append(merchant.getAgent());                      // 16-21 6  NUM Agent Bank Number (4.5)
        h.append(merchant.getChain());                      // 22-27 6  NUM Agent Chain Number (4.6)
        h.append(merchant.getId());                         // 28-39 12 NUM Merchant Number (4.121)
        h.append(merchant.getStore());                      // 40-43 4  NUM Store Number (4.187)
        h.append(merchant.getTerminal());                   // 44-47 4  NUM Terminal Number 9911 (4.195)
        h.append(DEVICE_CODES[0]);                          // 48    1  A/N Device Code: Q="Third party software developer" (4.62)
        h.append(merchant.getIndustryCode());               // 49    1  A/N Industry Code (4.94)
        h.append(CURRENCY_CODES[0]);                        // 50-52 3  NUM Currency Code (4.52)
        h.append(LANGUAGES[0]);                             // 53-54 2  NUM Language Indicator: 00=English (4.104)
        h.append(TIME_ZONES[0]);                            // 55-57 3  NUM Time Zone Differential (4.200)
        Date date = new Date();
        h.append(new SimpleDateFormat("MMdd").format(date)); // 58-61 4  NUM Batch Transmission Date MMDD (4.22)
        h.append("batchnum");                               // 62-64 3  NUM Batch Number 001 - 999 (4.18)
        h.append('0');                                      // 65    1  NUM Blocking Indicator 0=Not Blocked (4.23)

        StringBuilder msg = new StringBuilder();
        msg.append(seperator("Header",h.toString(),65,ETB));

        // K-Format Parameter Record (Base Group)
        // Byte Length Frmt Field Description Content Section
        StringBuilder p = new StringBuilder("K1.ZP@@@@");   // 1   1 A/N Record Format: K (4.154)
                                                            // 2   1 NUM Application Type: 1=Single Batch (4.10)
                                                            // 3   1 A/N Message Delimiter: . (4.123)
                                                            // 4   1 A/N X.25 Routing ID: Z (4.226)
                                                            // 5-9 5 A/N Record Type: P@@@@ (4.155)
        p.append("840");                                    // 10-12 3 NUM Country Code 840 (4.47)
        p.append(String.format("%-9s",merchant.getZip()));  // 13-21 9 A/N City Code Left-Justified/Space-Filled (4.43)
        p.append(merchant.getMcc());                        // 22-25 4 NUM Merchant Category Code (4.116)
        p.append(String.format("%-25s",merchant.getName()));// 26-50 25 A/N Merchant Name Left-Justified/Space-Filled (4.27.1)
        p.append(String.format("%-13s",merchant.getCity()));// 51-63 13 A/N Merchant City Left-Justified/Space-Filled (4.27.2)
        p.append(merchant.getState());                      // 64-65 2 A/N Merchant State (4.27.3)
        p.append("00001");                                  // 66-70 5 A/N Merchant Location Number 00001 (4.120)
        p.append(merchant.getV());                          // 71-78 8 NUM V Number 00000001 (4.194)

        msg.append(seperator("Parameters",p.toString(),78,ETB));

        /* K-Format Detail Record (Electronic Commerce)
         * Byte Size Frmt Field Description Content Section
         * D@@'D'  `
         */
        StringBuilder d = new StringBuilder("K1.ZD@@`D");   // 1   1 A/N Record Format: K (4.154)
                                                            // 2   1 NUM Application Type 1=Single Batch (4.10)
                                                            // 3   1 A/N Message Delimiter: . (4.123)
                                                            // 4   1 A/N X.25 Routing ID: Z (4.226)
                                                            // 5-9 5 A/N Record Type: D@@`D (4.155)
        d.append("56");                                     // 10-11 2 A/N Transaction Code: 56 = Card Not Present (4.205)
        d.append('N');                                      // 12  1 A/N Cardholder Identification Code N (4.32)
                                                            //       (Address Verification Data or
                                                            //        CPS/Card Not Present or
                                                            //        Electronic Commerce)
        d.append('@');                                      // 13  1 A/N Account Data Source Code @ = No Cardreader (4.1)
        d.append(String.format("%-22s",cardNumber));        // 14-35 22 A/N Cardholder Account Number Left-Justified/Space-Filled (4.30)
        d.append('Y');                                      // 36  1 Requested ACI (Authorization Characteristics Indicator): N (4.163)
        if(aci==null || aci.isEmpty())
            aci = " ";
        d.append(aci);                                      // 37  1 A/N Returned ACI (4.168)
        if(authSourceCode==null || authSourceCode.isEmpty())
            authSourceCode = "6";
        d.append(authSourceCode);                           // 38 1 A/N Authorization Source Code (4.13)
        if(transSequenceNumber==null || transSequenceNumber.isEmpty())
            throw new Exception("Transaction Sequence Number missing");
        d.append(String.format("%4s",transSequenceNumber).replace(" ","0")); // 39-42 4 NUM Transaction Sequence Number Right-Justified/Zero-Filled (4.207)
// ###FIXME (from auth)*** 
        d.append("00");                                     // 43-44 2 A/N Response Code (4.164)
        d.append(String.format("%-6s",authCode));           // 45-50 6 A/N Authorization Code Left-Justified/Space-Filled (4.12)
        d.append(new SimpleDateFormat("MMdd").format(date)); // 51-54 4 NUM Local Transaction Date MMDD (4.113)
        d.append(new SimpleDateFormat("HHMMSS").format(date)); // 55-60 6 NUM Local Transaction Time HHMMSS (4.114)
        // From auth
        if(avsCode==null || avsCode.isEmpty())
            throw new Exception("Address Verification Result Code missing");
        d.append(authSourceCode);                           // 61  1 A/N AVS Result Code (4.3)
        if(transId==null || transId.isEmpty())
            transId = "000000000000000";
        d.append(String.format("%-15s",transId));           // 62-76 15 A/N Transaction Identifier Left-Justified/Space-Filled (4.206)
        if(validationCode==null || validationCode.isEmpty())
            validationCode = "    ";
        d.append(validationCode);                           // 77-80 4 A/N Validation Code (4.218)
        d.append(' ');                                      // 81   1 A/N Void Indicator <SPACE> = Not Voided (4.224)
        d.append("00");                                     // 82-83 2 NUM Transaction Status Code 00 (4.208)
        d.append('0');                                      // 84   1 A/N Reimbursement Attribute 0 (4.157)
        
        String my_amount = String.format("%12s",amount.replace(".","")).replace(" ","0");
        d.append(my_amount);                                // 85-96 12 NUM Settlement Amount Right-Justified/Zero-Filled (4.175)
        d.append(my_amount);                                // 97-108 12 NUM Authorized Amount Right-Justified/Zero-Filled (4.14)
        d.append(my_amount);                                // 109-120 12 NUM Total Authorized Amount Right-Justified/Zero-Filled (4.201)
        // d.append('1');
        d.append('0');                                      // 121   1 A/N Purchase Identifier Format Code 1 (4.150)
        d.append(String.format("%-25s",purchaseId));        // 122-146 25 A/N Purchase Identifier Left-Justified/Space-Filled (4.149)
// ???
        d.append("01");                                     // 147-148 2 NUM Multiple Clearing Sequence Number (4.129)
// ???
        d.append("01");                                     // 149-150 2 NUM Multiple Clearing Sequence Count (1.128)
        d.append('7');                                      // 151 1 A/N MOTO/Electronic Commerce Indicator 7 = Channel Encrypted (4.127)

        msg.append(seperator("Detail",d.toString(),151,ETB));

        // K-Format Trailer Record
        // Byte Length Frmt Field Description Content Section
        StringBuilder t = new StringBuilder("K1.ZT@@@@");   // 1    1 A/N Record Format K (4.154)
                                                            // 2    1 NUM Application Type 1=Single 3=Multiple Batch (4.10)
                                                            // 3    1 A/N Message Delimiter . (4.123)
                                                            // 4    1 A/N X.25 Routing ID Z (4.226)
                                                            // 5-9  5 A/N Record Type T@@@@ (4.155)
        t.append(new SimpleDateFormat("MMdd").format(date)); // 10-13  4 NUM Batch Transmission Date MMDD (4.22)
        t.append(batchNumber);                              // 14-16  3 NUM Batch Number 001 - 999 (4.18)
        t.append(String.format("%9s","1").replace(" ","0")); // 17-25  9 NUM Batch Record Count Right-Justified/Zero-Filled (4.19)
        my_amount = String.format("%16s",my_amount).replace(" ","0");
        t.append(my_amount);                                // 26-41 16 NUM Batch Hashing Total Purchases + Returns (4.16)
        t.append("0000000000000000");                       // 42-57 16 NUM Cashback Total (4.38)
        t.append(my_amount);                                // 58-73 16 NUM Batch Net Deposit Purchases - Returns (4.17)

        msg.append(seperator("Trailer",t.toString(),73,ETX));

        return(msg.toString());
    }

    private String settleResponse() throws Exception {
        StringBuilder r = new StringBuilder();
 
 
        return(r.toString());
    }

    /**
     * Generate Longitudinal Redundancy Check (LRC)
     *
     * @param s String to generate LRC
     * @return char representing the LRC
     */
    private char lrc(String s) {
        char lrc = 0;
        char[] chars = s.toCharArray();
        for (char c:chars)
            lrc ^= c;
        return(lrc);
    }

    /**
     * Get even parity, returns string as a byte array with even parity
     *
     * @param s string to be converted
     * @return byte[] with even parity bytes
     */
    public static byte[] getEvenParity(String s) {
        byte[] a = s.getBytes(StandardCharsets.US_ASCII);
        byte[] b = new byte[a.length];
        for (int i = 0; i < a.length; i++)
            b[i] = setEvenParity(a[i]);
        return b;
    }

    /**
     * Set even parity bit
     *
     * @param b byte to set even parity
     * @return byte with even parity
     */
    public static byte setEvenParity(byte b) {
        short c = 0;
        if (0 != (b & 0x01)) c++;
        if (0 != (b & 0x02)) c++;
        if (0 != (b & 0x04)) c++;
        if (0 != (b & 0x08)) c++;
        if (0 != (b & 0x10)) c++;
        if (0 != (b & 0x20)) c++;
        if (0 != (b & 0x40)) c++;
        return (1 == (c % 2)) ? (byte)(b | 0x80) : b;
    }

    /**
     * Remove parity bit, presently only 8th bit set to 0 if 1
     *
     * @param a byte[] with parity bit set
     * @return byte[] with 8th parity it removed
     */
    public static byte[] removeParity(byte[] a) {
        byte[] b = new byte[a.length];
        for (int i = 0; i < a.length; i++)
            b[i] = (byte)(a[i] & (byte)0x7f);
        return b;
    }
}
