/*
 * Copyright 2017 Obsidian-Studios, Inc.
 * Distributed under the terms of the GNU General Public License v3
 *
 */

package jtsys;

/**
 * @author William L. Thomson Jr. <wlt@o-sinc.com>
 */
public enum AVSCodes {

    X("Exact match, 9 digit zip"),
    Y("Exact match, 5 digit zip"),
    A("Address match only"),
    W("9-digit zip match only"),
    Z("5-digit zip match only"),
    N("No address or zip match"),
    U("Address unavailable"),
    G("Non-U.S. Issuer does not participate"),
    R("Issuer system unavailable"),
    E("Not a mail/phone order"),
    S("Service not supported");

    private String value;

    AVSCodes(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
