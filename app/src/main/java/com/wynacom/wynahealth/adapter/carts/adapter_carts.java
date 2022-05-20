package com.wynacom.wynahealth.adapter.carts;

public class adapter_carts {
    String id               = null;
    String name             = null;
    String status           = null;
    String invoiceNumber    = null;
    String telephone        = null;
    String total            = null;
    String address          = null;
    String gender           = null;
    String snap             = null;

    public adapter_carts(String id, String name, String invoiceNumber, String telephone, String address, String status, String total,String gender, String snap) {
        super();
        this.id             = id;
        this.name           = name;
        this.invoiceNumber  = invoiceNumber;
        this.telephone      = telephone;
        this.total          = total;
        this.address        = address;
        this.status         = status;
        this.gender         = gender;
        this.snap           = snap;
    }

    public String getID() {
        return id;
    }
    public void setID(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
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

    public String getSnap() {
        return snap;
    }
    public void setSnap(String snap) {
        this.snap = snap;
    }
}//String id, String qty, String subtotal, String image, String title, String slug, String description, String product_price, String discount
