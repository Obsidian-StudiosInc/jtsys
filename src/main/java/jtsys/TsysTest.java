/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

import java.util.LinkedHashMap;

/**
 * 
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 */
public class TsysTest {

    private final char ETX = 0x03;

    public static void main(String[] args) {
        Tsys tsys = new Tsys(true);
        TsysTest test = new TsysTest();
        test.authSettleTest(tsys);
    }

    private static Merchant merchant() {
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

    private LinkedHashMap<String,String> authTest(Tsys tsys) {
        LinkedHashMap<String,String> a = new LinkedHashMap<>();
        try {
            a = tsys.auth(merchant(),
                          "0001",
                          "4012888888881881",
                          "0218",
                          "8320",
                          "85284",
                          "1.00");
            if(a.size()==2) {
                    System.out.print("Error :\n");
                a.forEach((k,v) -> {
                    System.out.printf("\t%s : %s\n",k,v);
                });
                System.out.print("\n");
            } else {
                System.out.print("Auth response :\n");
                a.forEach((k,v) -> {
                    System.out.printf("\t%-25s : %s\n",k,v);
                });
                System.out.print("\n");
            }
        } catch(Exception e) {
            System.out.println(e);
        }
        return(a);
    }

    private void authSettleTest(Tsys tsys) {
        try {
            LinkedHashMap<String,String> a = authTest(tsys);
            if(!a.get(AuthResponseKeys.ART.key()).contains("MATCH"))
                return;
            System.out.print("\n");
            LinkedHashMap<String,String> s = tsys.settle(merchant(),
                              "4012888888881881",
                              "0001",
                              "001",
                              a.get(AuthResponseKeys.ACI.key()),
                              a.get(AuthResponseKeys.ASC.key()),
                              a.get(AuthResponseKeys.RC.key()),
                              a.get(AuthResponseKeys.AC.key()),
                              a.get(AuthResponseKeys.AVS.key()),
                              a.get(AuthResponseKeys.TI.key()),
                              a.get(AuthResponseKeys.VC.key()),
                              "1.00",
                              "0001");
            System.out.print("Settle response :\n");
            s.forEach((k,v) -> {
                System.out.printf("\t%-25s : %s\n",k,v);
            });
            System.out.print("\n");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Basic Test Request
     *
     * @return String containing the response
     * @throws Exception if an error occurs
     */
    private String testRequest(Tsys tsys) throws Exception {
        StringBuilder msg = new StringBuilder(); 
        try {
            String t = "D4.999995";   // D2. DO
            msg.append(tsys.separator("test",t,t.length(),ETX));
        } catch(Exception e) {
            System.out.println(e);
        }
        return(msg.toString());
    }

}
