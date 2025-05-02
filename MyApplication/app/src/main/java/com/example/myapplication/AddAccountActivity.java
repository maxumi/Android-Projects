package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddAccountActivity extends AppCompatActivity {
    private EditText accountNameField, usernameField, passwordField;

    private int editIndex = -1; // -1 means add, not edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar means the top bar and makes it possible to go back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        accountNameField = findViewById(R.id.editAccountName);
        usernameField = findViewById(R.id.editUsername);
        passwordField = findViewById(R.id.editPassword);
        // Adds a checkbox that shows password
        CheckBox showPasswordCheck = findViewById(R.id.checkShowPassword);

        showPasswordCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordField.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordField.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordField.setSelection(passwordField.getText().length()); // move cursor to end
        });


        // Check if we're editing an existing account. accountIndex means edit
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("account")) {
            Account account = (Account) intent.getSerializableExtra("account");
            editIndex = intent.getIntExtra("accountIndex", -1);

            if (account != null) {
                accountNameField.setText(account.getAccountName());
                usernameField.setText(account.getUsername());
                passwordField.setText(account.getPassword());

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Account");
                }
            }
        }
        else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Account");
            }
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = accountNameField.getText().toString();
            String user = usernameField.getText().toString();
            String pass = passwordField.getText().toString();

            // Creates dialogue menu
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you want to save?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent resultIntent = new Intent();
                        Account account = new Account(name, user, pass);
                        resultIntent.putExtra("account", account);
                        resultIntent.putExtra("accountIndex", editIndex);

                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}