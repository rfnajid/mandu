package com.nnn.mandu;

import com.google.gson.annotations.SerializedName;

/**
 * Created by seven on 5/11/2017.
 */

public class Currency {

    @SerializedName("cur_code")
    String code;
    @SerializedName("cur_name")
    String name;
    @SerializedName("cur_flag")
    String flag;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
