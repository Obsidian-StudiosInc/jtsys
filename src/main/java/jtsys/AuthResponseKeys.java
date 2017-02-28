/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

/**
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 */
public enum AuthResponseKeys {

    ACI("ACI"),
    ASC("Auth Source Code"),
    RC("Response Code"),
    AC("Approval Code"),
    ART("Auth Response Text"),
    AVS("AVS Result Code"),
    RRN("Retrieval Reference Num"),
    TI("Transaction Identifier"),
    VC("Validation Code"),
    GVN("Group III Version Number");

    private String key;
    
    AuthResponseKeys(String key) {
        this.key = key;
    }
    
    public String key() {
        return this.key;
    }
}
