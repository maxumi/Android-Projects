package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<Account> accountList;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        Button deleteButton;
        Button editButton;
        Button detailsButton;
        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.accountNameTextView);
            deleteButton = view.findViewById(R.id.btnDelete);
            editButton = view.findViewById(R.id.btnEdit);
            detailsButton = view.findViewById(R.id.btnDetails);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public AccountAdapter(List<Account> accountList) {
        this.accountList = accountList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Account account = accountList.get(position);
        viewHolder.getTextView().setText(account.getAccountName());

        viewHolder.deleteButton.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            accountList.remove(pos);
                            notifyItemRemoved(pos);
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        viewHolder.detailsButton.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Account selectedAccount = accountList.get(pos);
                Intent intent = new Intent(v.getContext(), AccountDetailsActivity.class);
                intent.putExtra("account_name", selectedAccount.getAccountName());
                intent.putExtra("username", selectedAccount.getUsername());
                intent.putExtra("password", selectedAccount.getPassword());
                v.getContext().startActivity(intent);
            }
        });
        viewHolder.editButton.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Account selectedAccount = accountList.get(pos);
                Intent intent = new Intent(v.getContext(), AddAccountActivity.class);
                intent.putExtra("accountIndex", pos);
                intent.putExtra("accountName", account.getAccountName());
                intent.putExtra("username", account.getUsername());
                intent.putExtra("password", account.getPassword());

                ((MainActivity)v.getContext()).editAccountLauncher.launch(intent); // Cast context for ActivityResultLauncher
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return accountList.size();
    }
}