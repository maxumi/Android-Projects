package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccountAdapter adapter;

    // Static for performance
    private static final Gson GSON = new Gson();
    private static final Type ACCOUNT_LIST_TYPE =
            new TypeToken<ArrayList<Account>>(){}.getType();
    private static final String  ACCOUNTS_KEY = "account_list";
    private static final String PIN_CODE = "12345";
    // Launcher and Method that controls how data returned is handled for creating new password
    private final ActivityResultLauncher<Intent> addAccountLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String name = data.getStringExtra("accountName");
                        String user = data.getStringExtra("username");
                        String pass = data.getStringExtra("password");

                        Account newAccount = (Account) data.getSerializableExtra("account");
                        if (newAccount != null) {
                            AccountManager.getInstance().getAccounts().add(newAccount);
                            adapter.refresh(AccountManager.getInstance().getAccounts());
                            saveAccounts();
                            // Toast for flavor
                            Toast.makeText(this, "Account added", Toast.LENGTH_SHORT).show();  // <-- Added
                        }
                    }
                }
            });
    // Launcher and Method that controls how data returned is handled editing

    public final ActivityResultLauncher<Intent> editAccountLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Account updatedAccount = (Account) data.getSerializableExtra("account");
                        int index = data.getIntExtra("accountIndex", -1);

                        if (updatedAccount != null && index >= 0 && index < AccountManager.getInstance().getAccounts().size()) {
                            AccountManager.getInstance().getAccounts().set(index, updatedAccount);
                            adapter.refresh(AccountManager.getInstance().getAccounts());
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Prompt PIN before initializing the rest
        EditText pinInput = new EditText(this);
        pinInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new android.app.AlertDialog.Builder(this)
                .setTitle("Enter PIN")
                .setView(pinInput)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    String enteredPin = pinInput.getText().toString();
                    if (!PIN_CODE.equals(enteredPin)) {
                        Toast.makeText(this, "WRONG CODE, CLOSING APP.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        initApp(); // Proceed only if PIN is correct
                    }
                })
                .show();
    }
    private void initApp() {
        loadAccounts();

        adapter = new AccountAdapter(AccountManager.getInstance().getAccounts(), new OnAccountActionListener() {
            @Override
            public void onDelete(Account account) {
                AccountManager.getInstance().getAccounts().remove(account);
                adapter.refresh(AccountManager.getInstance().getAccounts());
                saveAccounts();
                Toast.makeText(MainActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEdit(Account account, int index) {
                Intent intent = new Intent(MainActivity.this, AddAccountActivity.class);
                intent.putExtra("accountIndex", index);
                intent.putExtra("account", account);
                editAccountLauncher.launch(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerViewAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            addAccountLauncher.launch(new Intent(this, AddAccountActivity.class));
        });

        EditText searchField = findViewById(R.id.editSearch);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
    private void saveAccounts() {
        SharedPreferences  prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = GSON.toJson(AccountManager.getInstance().getAccounts());
        editor.putString(ACCOUNTS_KEY, json);
        editor.apply();
    }
    private void loadAccounts() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String json = prefs.getString(ACCOUNTS_KEY, null);

        ArrayList<Account> loadedAccounts = GSON.fromJson(json, ACCOUNT_LIST_TYPE);
        if (loadedAccounts != null) {
            AccountManager.getInstance().getAccounts().clear();
            AccountManager.getInstance().getAccounts().addAll(loadedAccounts);
        }
    }
}