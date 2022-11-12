package com.example.disconrecon_library.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MRcode implements Serializable {

    public String getMRCODE() {
        return MRCODE;
    }

    public void setMRCODE(String MRCODE) {
        this.MRCODE = MRCODE;
    }

    @SerializedName("MRCODE")
    private String MRCODE;
}
