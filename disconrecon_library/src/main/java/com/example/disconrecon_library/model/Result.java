package com.example.disconrecon_library.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Result implements Serializable {
    public String getMessage() {
        return message;
    }

    @SerializedName("message")
    private String message;
}
