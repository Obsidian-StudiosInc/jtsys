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
    ACI,
    Auth_Source_Code,
    Response_Code,
    Approval_Code,
    Auth_Response_Text,
    AVS_Result_Code,
    Retrieval_Reference_Num,
    Transaction_Identifier,
    Validation_Code,
    Group_III_Version_Number;

    public String key() {
        return(this.name().replace("_"," "));
    }
}
