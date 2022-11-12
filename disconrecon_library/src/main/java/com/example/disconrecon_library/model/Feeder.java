package com.example.disconrecon_library.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Feeder implements Serializable {
    @SerializedName("FDRCODE")
    private String FDRCODE;

    @SerializedName("FDRIR")
    private String FDRIR;

    @SerializedName("FDRFR")
    private String FDRFR;

    @SerializedName("FDRMF")
    private String FDRMF;

    @SerializedName("FDRNAME")
    private String FDRNAME;

    public String getFDRCODE() {
        return FDRCODE;
    }

    public String getFDRIR() {
        return FDRIR;
    }

    public String getFDRFR() {
        return FDRFR;
    }

    public String getFDRMF() {
        return FDRMF;
    }

    public String getFDRNAME() {
        return FDRNAME;
    }

    public String getSRTPV_INPUT() {
        return SRTPV_INPUT;
    }

    public String getBoundary_Mtr_Export() {
        return Boundary_Mtr_Export;
    }

    @SerializedName("SRTPV_INPUT")
    private String SRTPV_INPUT;
    @SerializedName("Boundary_Mtr_Export")
    private String Boundary_Mtr_Export;
    @SerializedName("USER_ROLE")
    private String USER_ROLE;

    public String getUSER_ROLE() {
        return USER_ROLE;
    }

    public void setUSER_ROLE(String USER_ROLE) {
        this.USER_ROLE = USER_ROLE;
    }
}
