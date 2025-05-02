package com.example.myapplication;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountName;
    private String username;
    private String password;
    public Account() {}
    public Account(String accountName, String username, String password) {
        this.accountName = accountName;
        this.username = username;
        this.password = password;
    }

    public String getAccountName() { return accountName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setAccountName(String accountName) { this.accountName = accountName; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }


    @NonNull
    @Override
    public String toString() {
        return "Account Name: '"+accountName+", Account UserName: '"+username;
    }
}
