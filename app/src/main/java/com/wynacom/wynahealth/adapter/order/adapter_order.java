package com.wynacom.wynahealth.adapter.order;

public class adapter_order {
    String id               = null;
    String name             = null;
    String status           = null;
    String invoiceNumber    = null;
    String telephone        = null;
    String total            = null;
    String address          = null;

    boolean selected        = false;

    public adapter_order(String id, String name, String invoiceNumber, String telephone, String address, String status,String total) {
        super();
        this.id             = id;
        this.name           = name;
        this.invoiceNumber  = invoiceNumber;
        this.telephone      = telephone;
        this.total          = total;
        this.address        = address;
        this.status         = status;
    }

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getNames() {
        return name;
    }
    public void setNames(String name) {
        this.name = name;
    }

    public String getInvoice() {
        return invoiceNumber;
    }
    public void setInvoice(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }
    public void setTotal(String total) {
        this.total = total;
    }
}
