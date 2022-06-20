package com.wynacom.wynahealth.adapter.carts;

public class adapter_carts {
    String id               = null;
    String name             = null;
    String status           = null;
    String invoiceNumber    = null;
    String telephone        = null;
    String total            = null;
    String service_date     = null;
    String address          = null;
    String gender           = null;
    String snap             = null;
    String company_name     = null;
    String company_address  = null;
    String data_patient_id  = null;
    String company_id       = null;
    String doctor           = null;
    String companies        = null;

    public adapter_carts(String id, String name, String invoiceNumber, String telephone, String address, String status, String total,String gender, String snap,String service_date, String company_name, String company_city, String data_patient_id,String company_id,String doctor,String companies) {
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
        this.service_date   = service_date;
        this.company_name   = company_name;
        this.company_address= company_city;
        this.data_patient_id= data_patient_id;
        this.company_id     = company_id;
        this.doctor         = doctor;
        this.companies      = companies;
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

    public void setService_date(String service_date) {
        this.service_date = service_date;
    }
    public String getService_date() {
        return service_date;
    }

    public String getCompany_name() {
        return company_name;
    }
    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_address() {
        return company_address;
    }
    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getData_patient_id() {
        return data_patient_id;
    }
    public void setData_patient_id(String data_patient_id) {
        this.data_patient_id = data_patient_id;
    }

    public String getCompany_id() {
        return company_id;
    }
    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDoctor() {
        return doctor;
    }
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getCompanies() {
        return companies;
    }
    public void setCompanies(String companies) {
        this.companies = companies;
    }
}//String id, String qty, String subtotal, String image, String title, String slug, String description, String product_price, String discount
