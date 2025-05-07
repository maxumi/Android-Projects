package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    String url = "http://10.131.202.230:8080/api/accounts";
    RequestQueue queue;

    private static final String PREF_NAME = "account_prefs";
    private static final String ACCOUNTS_KEY = "account_list";

    private static AccountRepository instance;
    private ArrayList<Account> accounts;
    private final Gson gson = new Gson();
    private final Type ACCOUNT_LIST_TYPE = new TypeToken<ArrayList<Account>>() {}.getType();

    private AccountRepository() {
        accounts = new ArrayList<>();
    }

    public static synchronized AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(ACCOUNTS_KEY, null);
        ArrayList<Account> loaded = gson.fromJson(json, ACCOUNT_LIST_TYPE);
        accounts.clear();
        if (loaded != null) accounts.addAll(loaded);
    }

    public void getAccountsFromApi(){
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    if (response != null) {
                        Type type = new TypeToken<List<Account>>() {
                        }.getType();
                        accounts = gson.fromJson(response.toString(), type);
                    }
                },
                error -> {
                    Log.e("Volley", "getAccountsFromApi: " + error.getMessage());
                }
        );
        queue.add(jsonRequest);
    }

    public void save(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCOUNTS_KEY, gson.toJson(accounts));
        editor.apply();
    }

    public void addAccount(Account account, Context context) {
        if (account != null) {
            accounts.add(account);
            save(context);
        }
    }

    public boolean updateAccount(int index, Account updatedAccount, Context context) {
        if (index >= 0 && index < accounts.size() && updatedAccount != null) {
            accounts.set(index, updatedAccount);
            save(context); // persist the update
            return true;
        }
        return false;
    }
}
