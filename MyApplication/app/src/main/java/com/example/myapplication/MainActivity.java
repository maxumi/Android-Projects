package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccountAdapter adapter;

    // Launcher and Method that controls how data returned is handled for creating new password
    private final ActivityResultLauncher<Intent> addAccountLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String name = data.getStringExtra("accountName");
                        String user = data.getStringExtra("username");
                        String pass = data.getStringExtra("password");

                        Account newAccount = new Account(name, user, pass);
                        AccountManager.accounts.add(newAccount);
                        adapter.notifyItemInserted(AccountManager.accounts.size() - 1);
                    }
                }
            });
    // Launcher and Method that controls how data returned is handled editing

    public final ActivityResultLauncher<Intent> editAccountLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String name = data.getStringExtra("accountName");
                        String user = data.getStringExtra("username");
                        String pass = data.getStringExtra("password");
                        int index = data.getIntExtra("accountIndex", -1);

                        if (index >= 0 && index < AccountManager.accounts.size()) {
                            Account updatedAccount = new Account(name, user, pass);
                            AccountManager.accounts.set(index, updatedAccount);
                            adapter.notifyItemChanged(index);
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gives the adapter the list to show.
        adapter = new AccountAdapter(AccountManager.accounts);

        // setup recyclerView for adapter
        recyclerView = findViewById(R.id.recyclerViewAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            addAccountLauncher.launch(new Intent(this, AddAccountActivity.class));

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged(); // Refresh list on return
    }

}