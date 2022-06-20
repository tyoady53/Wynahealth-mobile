package com.wynacom.wynahealth.adapter.outlets;

public class adapter_provinces {
    String id               = null;
    String name             = null;

    public adapter_provinces(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
