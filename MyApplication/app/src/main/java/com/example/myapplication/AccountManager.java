package com.example.myapplication;

import java.util.ArrayList;

// Holds the list of data
public class AccountManager {
    private static AccountManager instance;
    private final ArrayList<Account> accounts;

    private AccountManager() {
        accounts = new ArrayList<>();
    }

    public static synchronized AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}
