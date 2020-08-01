package com.ihsuraa.picmesh;

public class PicmeshUser {
    private String userName;
    private String fullName;
    private String userContact;
    private String userEmail;
    private String gender;
    private String location;
    private String birthday;
    private String propicUrl;

    public String getPropicUrl() {
        return propicUrl;
    }

    public PicmeshUser() {
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserContact() {
        return userContact;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }


    public String getBirthday() {
        return birthday;
    }

    public PicmeshUser(String userName, String fullName, String userContact, String userEmail, String gender, String location,  String birthday , String propicUrl) {
        this.userName = userName;
        this.fullName = fullName;
        this.userContact = userContact;
        this.userEmail = userEmail;
        this.gender = gender;
        this.location = location;
        this.birthday = birthday;
        this.propicUrl = propicUrl;
    }
}
