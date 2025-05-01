package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    // Original list given
    private List<Account> originalList;

    // A filtered version which copies from original
    private List<Account> filteredList;

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
        this.originalList = new ArrayList<>(accountList);
        this.filteredList = new ArrayList<>(accountList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Account account = filteredList.get(position);
        viewHolder.getTextView().setText(account.getAccountName());

        viewHolder.deleteButton.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Account toRemove = filteredList.get(pos);
                            originalList.remove(toRemove);
                            filteredList.remove(pos);
                            notifyItemRemoved(pos);
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        viewHolder.detailsButton.setOnClickListener(v -> {
            int pos = viewHolder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Account selectedAccount = filteredList.get(pos);
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
                Account selectedAccount = filteredList.get(pos);
                int originalIndex = originalList.indexOf(selectedAccount);
                Intent intent = new Intent(v.getContext(), AddAccountActivity.class);
                intent.putExtra("accountIndex", originalIndex);
                intent.putExtra("accountName", selectedAccount.getAccountName());
                intent.putExtra("username", selectedAccount.getUsername());
                intent.putExtra("password", selectedAccount.getPassword());
                ((MainActivity) v.getContext()).editAccountLauncher.launch(intent);
            }
        });
    }

    // A filter function for adapter that changes original list to filtered
    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (Account account : originalList) {
                if (account.getAccountName().toLowerCase().contains(text) ||
                        account.getUsername().toLowerCase().contains(text)) {
                    filteredList.add(account);
                }
            }
        }
        notifyDataSetChanged();
    }
    // Used to refresh list as it only uses a copy
    public void refresh(List<Account> newList) {
        originalList.clear();
        originalList.addAll(newList);

        filteredList.clear();
        filteredList.addAll(newList);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }
}
