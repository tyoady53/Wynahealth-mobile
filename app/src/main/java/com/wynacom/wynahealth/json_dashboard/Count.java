package com.wynacom.wynahealth.json_dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Count {

    @SerializedName("pending")
    @Expose
    private Integer pending;
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("expired")
    @Expose
    private Integer expired;
    @SerializedName("failed")
    @Expose
    private Integer failed;

    public Integer getPending() {
        return pending;
    }

    public void setPending(Integer pending) {
        this.pending = pending;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getExpired() {
        return expired;
    }

    public void setExpired(Integer expired) {
        this.expired = expired;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

}
