package com.wynacom.wynahealth.adapter.invoices;

public class adapter_invoice {
    String id               = null;
    String name             = null;
    String status           = null;
    String payment          = null;
    String invoiceNumber    = null;
    String telephone        = null;
    String total            = null;
    String address          = null;
    String snap             = null;
    String service_date     = null;
    String booked           = null;
    String outlet_name      = null;
    String outlet_address   = null;
    boolean selected        = false;

    public adapter_invoice(String id, String name, String invoiceNumber, String telephone, String address, String status, String total,
                           String snap, String payment, String service_date, String booked,String outlet_name,String outlet_address) {
        super();
        this.id             = id;
        this.name           = name;
        this.invoiceNumber  = invoiceNumber;
        this.telephone      = telephone;
        this.total          = total;
        this.address        = address;
        this.status         = status;
        this.snap           = snap;
        this.payment        = payment;
        this.service_date   = service_date;
        this.booked         = booked;
        this.outlet_name    = outlet_name;
        this.outlet_address = outlet_address;
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

    public String getSnap() {
        return snap;
    }
    public void setSnap(String snap) {
        this.snap = snap;
    }

    public String getPayment() {
        return payment;
    }
    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getService_date() {
        return service_date;
    }
    public void setService_date(String service_date) {
        this.service_date = service_date;
    }

    public String getBooked() {
        return booked;
    }
    public void setBooked(String booked) {
        this.booked = booked;
    }

    public String getOutlet_name() {
        return outlet_name;
    }
    public void setOutlet_name(String outlet_name) {
        this.outlet_name = outlet_name;
    }

    public String getOutlet_address() {
        return outlet_address;
    }
    public void setOutlet_address(String outlet_address) {
        this.outlet_address = outlet_address;
    }
}
