package com.example.lenovo.getit;

import java.io.Serializable;

class Info implements Serializable {
    String mEmail,tMail;
    public Info(String mEmail, String tMail) {
        this.mEmail = mEmail;
        this.tMail = tMail;
    }
}
