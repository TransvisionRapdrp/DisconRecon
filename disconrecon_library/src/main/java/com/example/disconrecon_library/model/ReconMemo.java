package com.example.disconrecon_library.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReconMemo implements Serializable {
    @SerializedName("ACCT_ID")
    private String ACCT_ID;

    @SerializedName("LEG_RRNO")
    private String LEG_RRNO;

    @SerializedName("TARIFF")
    private String TARIFF;

    @SerializedName("SO")
    private String SO;

    @SerializedName("RE_DATE")
    private String RE_DATE;

    @SerializedName("CONSUMER_NAME")
    private String CONSUMER_NAME;

    @SerializedName("ADD1")
    private String ADD1;

    @SerializedName("subdivcode")
    private String subdivcode;

    @SerializedName("ARREARS")
    private String ARREARS;

    @SerializedName("DR_FEE")
    private String DR_FEE;

    @SerializedName("MRCODE")
    private String MRCODE;

    @SerializedName("RECEIPT_NO")
    private String RECEIPT_NO;
    @SerializedName("MOBILE_NO")
    private String MOBILE_NO;

    @SerializedName("ADDRESS1")
    private String ADDRESS1;

    @SerializedName("ADDRESS2")
    private String ADDRESS2;

    public String getBTPRINTER() {
        return BTPRINTER;
    }

    public void setBTPRINTER(String BTPRINTER) {
        this.BTPRINTER = BTPRINTER;
    }

    @SerializedName("BTPRINTER")
    private String BTPRINTER;
    public String getADDRESS1() {
        return ADDRESS1;
    }

    public void setADDRESS1(String ADDRESS1) {
        this.ADDRESS1 = ADDRESS1;
    }

    public String getADDRESS2() {
        return ADDRESS2;
    }

    public void setADDRESS2(String ADDRESS2) {
        this.ADDRESS2 = ADDRESS2;
    }

    public String getRECEIPT_NO() {
        return RECEIPT_NO;
    }

    public void setRECEIPT_NO(String RECEIPT_NO) {
        this.RECEIPT_NO = RECEIPT_NO;
    }

    public String getMOBILE_NO() {
        return MOBILE_NO;
    }

    public void setMOBILE_NO(String MOBILE_NO) {
        this.MOBILE_NO = MOBILE_NO;
    }

    public String getPAID_AMOUNT() {
        return PAID_AMOUNT;
    }

    public void setPAID_AMOUNT(String PAID_AMOUNT) {
        this.PAID_AMOUNT = PAID_AMOUNT;
    }

    public String getRECEIPT_DATE() {
        return RECEIPT_DATE;
    }

    public void setRECEIPT_DATE(String RECEIPT_DATE) {
        this.RECEIPT_DATE = RECEIPT_DATE;
    }

    @SerializedName("PAID_AMOUNT")
    private  String PAID_AMOUNT;
    @SerializedName("RECEIPT_DATE")
    private  String RECEIPT_DATE;

    public String getACCT_ID() {
        return ACCT_ID;
    }

    public String getLEG_RRNO() {
        return LEG_RRNO;
    }

    public String getTARIFF() {
        return TARIFF;
    }

    public String getSO() {
        return SO;
    }

    public String getRE_DATE() {
        return RE_DATE;
    }

    public String getCONSUMER_NAME() {
        return CONSUMER_NAME;
    }

    public String getADD1() {
        return ADD1;
    }

    public String getSubdivcode() {
        return subdivcode;
    }

    public String getARREARS() {
        return ARREARS;
    }

    public String getDR_FEE() {
        return DR_FEE;
    }

    public String getMRCODE() {
        return MRCODE;
    }

    public String getDate1() {
        return date1;
    }

    @SerializedName("date1")
    private String date1;
}
