package com.wynacom.wynahealth.adapter.outlets;

public class adapter_outlets {
    String id               = null;
    String name             = null;
    String address          = null;
    String province         = null;

    public adapter_outlets(String id, String name,String address,String province) {
        super();
        this.id         = id;
        this.name       = name;
        this.address    = address;
        this.province   = province;
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

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
}
