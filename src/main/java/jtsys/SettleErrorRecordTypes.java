/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

/**
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 */
public enum SettleErrorRecordTypes {

    B("Header Record"),
    C("Parameter Record"),
    D("Detail Record"),
    T("Trailer Record");

    private String value;

    SettleErrorRecordTypes(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
