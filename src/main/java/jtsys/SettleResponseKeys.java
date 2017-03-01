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

    Batch_Record_Count,
    Batch_Net_Deposit,
    Batch_Number,
    Batch_Response_Text;

    public String key() {
        return(this.name().replace("_"," "));
    }
}
