package com.wynacom.wynahealth.adapter.patient;

public class adapter_patient {
    String id           = null;
    String nama         = null;
    String handphone    = null;
    String sex          = null;
    String dob          = null;
    String nik          = null;
    String city         = null;
    String postal_code  = null;
    String number       = null;

    public adapter_patient(String ID,String nama, String handphone, String sex, String dob,String nik, String city, String postal_code,String number) {
        super();
        this.id         = ID;
        this.nama       = nama;
        this.handphone  = handphone;
        this.sex        = sex;
        this.dob        = dob;
        this.nik        = nik;
        this.city       = city;
        this.postal_code= postal_code;
        this.number     = number;
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPhone() {
        return handphone;
    }

    public void setPhone(String handphone) {
        this.handphone = handphone;
    }

    public String getGender() {
        return sex;
    }

    public void setGender(String sex) {
        this.sex = sex;
    }

    public String getDOB() {
        return dob;
    }

    public void setDOB(String lat) {
        this.dob = dob;
    }

    public String getNIK() {
        return nik;
    }

    public void setNIK(String nik) {
        this.nik = nik;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal() {
        return postal_code;
    }

    public void setPostal(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String getnumber) {
        this.number = getnumber;
    }
}
