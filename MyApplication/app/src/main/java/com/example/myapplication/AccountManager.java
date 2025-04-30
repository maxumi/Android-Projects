package com.example.myapplication;

import java.util.ArrayList;

// Holds the list of data
public class AccountManager {
    public static ArrayList<Account> accounts = new ArrayList<>();

    static {
        accounts.add(new Account("Gmail", "user1@gmail.com", "password123"));
        accounts.add(new Account("Facebook", "john.doe", "fb@2024!"));
        accounts.add(new Account("Twitter", "jane_doe", "tweet1234"));
        accounts.add(new Account("GitHub", "coderjoe", "gh_pass_789"));
        accounts.add(new Account("Netflix", "streamlover", "netflix2025"));
    }
}
