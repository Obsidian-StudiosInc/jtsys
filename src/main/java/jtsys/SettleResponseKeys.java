/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

/**
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 */
public enum SettleResponseKeys {

    COUNT("Batch Record Count"),
    DEPOSIT("Batch Net Deposit"),
    CODE("Batch Response Code"),
    NUMBER("Batch Number"),
    TEXT("Batch Response Text");

    private String key;

    SettleResponseKeys(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }
}
