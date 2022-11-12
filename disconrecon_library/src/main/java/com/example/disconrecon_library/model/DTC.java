package com.example.disconrecon_library.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DTC implements Serializable {
    @SerializedName("TCCODE")
    private String TCCODE;

    @SerializedName("TCIR")
    private String TCIR;

    @SerializedName("TCFR")
    private String TCFR;

    @SerializedName("TCMF")
    private String TCMF;

    @SerializedName("DTCNAME")
    private String DTCNAME;

    @SerializedName("DTC CODE")
    private String DTCCODE;

    @SerializedName("MRCODE")
    private String MRCODE;

    public String getTCCODE() {
        return TCCODE;
    }

    public String getTCIR() {
        return TCIR;
    }

    public String getTCFR() {
        return TCFR;
    }

    public String getTCMF() {
        return TCMF;
    }

    public String getDTCNAME() {
        return DTCNAME;
    }

    public String getDTCCODE() {
        return DTCCODE;
    }

    public String getMRCODE() {
        return MRCODE;
    }

    public String getDATE() {
        return DATE;
    }

    @SerializedName("DATE")
    private String DATE;
}
