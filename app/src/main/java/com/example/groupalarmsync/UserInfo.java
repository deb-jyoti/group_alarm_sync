package com.example.groupalarmsync;

public class UserInfo {
    private String name, status, email;

    public UserInfo(){

    }

    public UserInfo(String name, String status, String email){
        this.name = name;
        this.status = status;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }
}
