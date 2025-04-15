package com.example.dicerealmandroid.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dicerealmandroid.R;
import com.example.dicerealmandroid.adapters.InventoryDialogAdapter;
import com.example.dicerealmandroid.player.wrapper.PlayerInventoryWrapper;
import com.example.dicerealmandroid.player.PlayerStateHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class InventoryDialogFragment extends androidx.fragment.app.DialogFragment {
    private PlayerInventoryWrapper playerInventoryWrapper;
    private InventoryDialogAdapter adapter;
    private TextView noItemsTextView;
    private PlayerStateHolder playerSh;

    public static InventoryDialogFragment newInstance(PlayerInventoryWrapper playerInventoryWrapper, PlayerStateHolder player) {
        InventoryDialogFragment fragment = new InventoryDialogFragment();
        fragment.playerInventoryWrapper = playerInventoryWrapper;
        fragment.playerSh = player;
        return fragment;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_inventory_dialog, null);

        RecyclerView recyclerView = view.findViewById(R.id.inventoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noItemsTextView = view.findViewById(R.id.noItemsTextview);
        adapter = new InventoryDialogAdapter(playerInventoryWrapper, playerSh);
        recyclerView.setAdapter(adapter);

        // Unpack and observe LiveData List from the wrapper
        if (playerInventoryWrapper != null) {
            // Observe equippable items
            playerInventoryWrapper.equippableItems.observe(this, equippableItems -> {
                if (equippableItems != null) {
                    adapter.updateEquippableItems(new ArrayList<>(equippableItems));
                    toggleEmptyView(recyclerView);
                }
            });

            // Observe equipped items
            playerInventoryWrapper.equippedItems.observe(this, equippedItems -> {
                if (equippedItems != null) {
                    adapter.updateEquippedItems(new ArrayList<>(equippedItems.values()));
                    toggleEmptyView(recyclerView);
                }
            });

            // Observe potions
            playerInventoryWrapper.potions.observe(this, potions -> {
                if (potions != null) {
                    adapter.updatePotions(new ArrayList<>(potions));
                    toggleEmptyView(recyclerView);
                }
            });

            // Observe scrolls
            playerInventoryWrapper.scrolls.observe(this, scrolls -> {
                if (scrolls != null) {
                    adapter.updateScrolls(new ArrayList<>(scrolls));
                    toggleEmptyView(recyclerView);
                }
            });
        }

        // Build using the Material 3 dialog builder
        Dialog inventoryDialog = new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_App)
                .setView(view)
                .setPositiveButton("Close", (dialog, which) -> dismiss())
                .create();

        // Apply window attributes before showing the dialog
        Window window = inventoryDialog.getWindow();
        if (window != null) {
            // Set window animations
            window.setWindowAnimations(R.style.DialogAnimation_App);

            // Apply gravity immediately
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            //TODO: if possible, wrapcontent until a maxheight of 0.5 of the screen
            window.setAttributes(params);
        }

        inventoryDialog.show();
        return inventoryDialog;
    }

    //Toggle no items in inventory if have no items
    private void toggleEmptyView(RecyclerView recyclerView) {
        if (adapter.getItemCount() == 0) {
            noItemsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noItemsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}