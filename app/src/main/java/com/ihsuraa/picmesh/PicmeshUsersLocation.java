package com.ihsuraa.picmesh;

public class PicmeshUsersLocation {
    String fullName;
    String propicUrl;
    String year;

    public String getFullName() {
        return fullName;
    }

    public String getPropicUrl() {
        return propicUrl;
    }

    public String getYear() {
        return year;
    }

    public PicmeshUsersLocation(String fullName, String propicUrl, String year) {
        this.fullName = fullName;
        this.propicUrl = propicUrl;
        this.year = year;
    }

    public PicmeshUsersLocation() {
    }
}
