package com.example.myapplication;

public class Account {
    private String accountName;
    private String username;
    private String password;

    public Account(String accountName, String username, String password) {
        this.accountName = accountName;
        this.username = username;
        this.password = password;
    }

    public String getAccountName() {
        return accountName;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword(){
        return password;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public void setAccountPassword(String password){
        this.password = password;
    }

}
