package com.example.disconrecon_library.model;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class UpdateResult implements Serializable {
    @SerializedName("MTR_READ")
    @Expose
    private String MTR_READ;

    public void setMTR_READ(String MTR_READ) {
        this.MTR_READ = MTR_READ;
    }

    public void setCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }

    public String getMTR_READ() {
        return MTR_READ;
    }

    public String getCOMMENTS() {
        return COMMENTS;
    }

    @SerializedName("COMMENTS")
    @Expose
    private String COMMENTS;

    public String getREMARKS() {
        return REMARKS;
    }

    @SerializedName("REMARKS")
    @Expose
    private String REMARKS;
}
