/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

/**
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 */
public enum SettleErrorTypes {

    B("Blocked Terminal"),
    C("Card Type Error"),
    D("Device Error"),
    E("Error in Batch"),
    S("Sequence Error"),
    T("Transmission Error"),
    U("Unknown Error"),
    V("Routing Error");

    private String value;

    SettleErrorTypes(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
