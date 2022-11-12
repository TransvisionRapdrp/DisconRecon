package com.example.disconrecon_library.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class DateTime implements Serializable {
    @DatabaseField(columnName = "Date")
    @SerializedName("Date")
    @Expose
    private String Date;

    public String getTime() {
        return Time;
    }

    public String getDate() {
        return Date;
    }

    @DatabaseField(columnName = "Time")
    @SerializedName("Time")
    @Expose
    private String Time;
}
