package com.example.disconrecon_library.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.disconrecon_library.values.TimestampConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

@Entity
public class DisconData implements Serializable {
    public String getACCT_ID() {
        return ACCT_ID;
    }

    public String getARREARS() {
        return ARREARS;
    }

    public String getRE_DATE() {
        return RE_DATE;
    }

    public String getDIS_DATE() {
        return DIS_DATE;
    }

    public String getPREVREAD() {
        return PREVREAD;
    }

    public String getCONSUMER_NAME() {
        return CONSUMER_NAME;
    }

    public String getADD1() {
        return ADD1;
    }

    public String getLAT() {
        return LAT;
    }

    public String getLON() {
        return LON;
    }

    public String getMTR_READ() {
        return MTR_READ;
    }

    public String getCOMMENTS() {
        return COMMENTS;
    }

    public String getREMARKS() {
        return REMARKS;
    }

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public void setACCT_ID(String ACCT_ID) {
        this.ACCT_ID = ACCT_ID;
    }

    public void setARREARS(String ARREARS) {
        this.ARREARS = ARREARS;
    }

    public void setRE_DATE(String RE_DATE) {
        this.RE_DATE = RE_DATE;
    }

    public void setDIS_DATE(String DIS_DATE) {
        this.DIS_DATE = DIS_DATE;
    }

    public void setPREVREAD(String PREVREAD) {
        this.PREVREAD = PREVREAD;
    }

    public void setCONSUMER_NAME(String CONSUMER_NAME) {
        this.CONSUMER_NAME = CONSUMER_NAME;
    }

    public void setADD1(String ADD1) {
        this.ADD1 = ADD1;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public void setLON(String LON) {
        this.LON = LON;
    }

    public void setMTR_READ(String MTR_READ) {
        this.MTR_READ = MTR_READ;
    }

    public void setCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }

    public void setFLAG(String FLAG) {
        this.FLAG = FLAG;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "DATE")
    @TypeConverters({TimestampConverter.class})
    private Date DATE;

    @ColumnInfo(name = "ACCT_ID")
    @SerializedName("ACCT_ID")
    @Expose
    private String ACCT_ID;
    @ColumnInfo(name = "ARREARS")
    @SerializedName("ARREARS")
    @Expose
    private String ARREARS;
    @ColumnInfo(name = "RE_DATE")
    @SerializedName("RE_DATE")
    @Expose
    private String RE_DATE;
    @ColumnInfo(name = "DIS_DATE")
    @SerializedName("DIS_DATE")
    @Expose
    private String DIS_DATE;
    @ColumnInfo(name = "PREVREAD")
    @SerializedName("PREVREAD")
    @Expose
    private String PREVREAD;
    @ColumnInfo(name = "CONSUMER_NAME")
    @SerializedName("CONSUMER_NAME")
    @Expose
    private String CONSUMER_NAME;
    @ColumnInfo(name = "ADD1")
    @SerializedName("ADD1")
    @Expose
    private String ADD1;
    @ColumnInfo(name = "LAT")
    @SerializedName("LAT")
    @Expose
    private String LAT;
    @ColumnInfo(name = "LON")
    @SerializedName("LON")
    @Expose
    private String LON;
    @ColumnInfo(name = "MTR_READ")
    @SerializedName("MTR_READ")
    @Expose
    private String MTR_READ;
    @ColumnInfo(name = "COMMENTS")
    @SerializedName("COMMENTS")
    @Expose
    private String COMMENTS;
    @ColumnInfo(name = "REMARKS")
    @SerializedName("REMARKS")
    @Expose
    private String REMARKS;

    public String getFLAG() {
        return FLAG;
    }

    @ColumnInfo(name = "FLAG")
    @SerializedName("FLAG")
    @Expose
    private String FLAG;

    public int getId() {
        return id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    public String getUSER_ROLE() {
        return USER_ROLE;
    }

    public void setUSER_ROLE(String USER_ROLE) {
        this.USER_ROLE = USER_ROLE;
    }

    private String USER_ROLE;

   /* public UpdateResult getUpdateResult() {
        return updateResult;
    }

    public void setUpdateResult(UpdateResult updateResult) {
        this.updateResult = updateResult;
    }

    UpdateResult updateResult;*/
}

