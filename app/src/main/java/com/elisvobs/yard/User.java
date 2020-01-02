package com.elisvobs.yard;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name, age, gender, idNumber;
    public String occupation, education, village;
    public String ward, constituency, district;
    public String province, email, phone;

    public User() {
    }

    public User(String name, String age, String gender, String idNumber, String occupation, String education, String village, String ward, String constituency, String district, String province, String email, String phone) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.idNumber = idNumber;
        this.occupation = occupation;
        this.education = education;
        this.village = village;
        this.ward = ward;
        this.constituency = constituency;
        this.district = district;
        this.province = province;
        this.email = email;
        this.phone = phone;
    }
}