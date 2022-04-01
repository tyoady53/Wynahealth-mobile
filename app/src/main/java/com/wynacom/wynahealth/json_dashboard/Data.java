package com.wynacom.wynahealth.json_dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("count")
    @Expose
    private Count count;

    public Count getCount() {
        return count;
    }

    public void setCount(Count count) {
        this.count = count;
    }

}
