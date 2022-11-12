package com.example.disconrecon_library.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginDetails implements Serializable {
    public String getSUBDIVCODE() {
        return SUBDIVCODE;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    @SerializedName("DISRECONN_VER")
    private String DISRECONN_VER;

    public String getDISRECONN_VER() {
        return DISRECONN_VER;
    }

    @SerializedName("SUBDIVCODE")
    @Expose
    private String SUBDIVCODE;
    @SerializedName("USERNAME")
    @Expose
    private String USERNAME;

    public String getCOMPANY_LEVEL_ID() {
        return COMPANY_LEVEL_ID;
    }

    @SerializedName("COMPANY_LEVEL_ID")
    @Expose
    private String COMPANY_LEVEL_ID;

    public String getMRCODE() {
        return MRCODE;
    }

    @SerializedName("MRCODE")
    @Expose
    private String MRCODE;

    public String getROLE() {
        return ROLE;
    }

    @SerializedName("ROLE")
    @Expose
    private String ROLE;
    @SerializedName("PASSWORD")
    @Expose
    private String PASSWORD;

    public String getPASSWORD() {
        return PASSWORD;
    }
}
