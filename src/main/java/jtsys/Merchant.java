/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

/**
 * Tsys Merchant Profile
 *
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 */
public class Merchant {

    private char industryCode;
    private String agent;
    private String bin;
    private String id;
    private String zip;
    private String v;
    private String chain;
    private String mcc;
    private String store;
    private String terminal;
    private String city;
    private String name;
    private String state;

    public String getAgent() {
        return agent;
    }

    public String getBin() {
        return bin;
    }

    public String getChain() {
        return chain;
    }

    public String getId() {
        return id;
    }

    public char getIndustryCode() {
        return industryCode;
    }

    public String getMcc() {
        return mcc;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getStore() {
        return store;
    }

    public String getTerminal() {
        return terminal;
    }

    public String getV() {
        return v;
    }

    public String getZip() {
        return zip;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }
    
    public void setChain(String chain) {
        this.chain = chain;
    }

    public void setCity(String merchantCity) {
        this.city = merchantCity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIndustryCode(char industryCode) {
        this.industryCode = industryCode;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public void setName(String merchantName) {
        this.name = merchantName;
    }

    public void setState(String merchantState) {
        this.state = merchantState;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public void setV(String v) {
        this.v = v;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
