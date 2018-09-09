package com.jostlingjacks.craftlife;

public class UserInfo {

    private String email_address;
    private String password;

    public UserInfo(){

    }

    public  UserInfo(String email_address, String password){
        this.email_address = email_address;
        this.password = password;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
