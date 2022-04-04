package com.wynacom.wynahealth.json_dashboard;

import com.google.gson.annotations.SerializedName;

public class Post {
    private Integer pending;
    private Integer success;
    private Integer expired;
    private Integer failed;

    @SerializedName("body")
    private String text;

    public Integer getPending() {
        return pending;
    }


    public Integer getSuccess() {
        return success;
    }


    public Integer getExpired() {
        return expired;
    }


    public Integer getFailed() {
        return failed;
    }

    public String getText() {
        return text;
    }
}
