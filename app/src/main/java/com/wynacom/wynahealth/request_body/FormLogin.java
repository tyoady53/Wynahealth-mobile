package com.wynacom.wynahealth.request_body;

public class FormLogin {
    private String email;
    private String password;

    public FormLogin(String email, String password) {
        this.email      = email;
        this.password   = password;
    }
}
