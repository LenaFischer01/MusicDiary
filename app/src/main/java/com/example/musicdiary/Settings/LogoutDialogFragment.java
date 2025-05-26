package com.example.musicdiary.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.musicdiary.R;

public class LogoutDialogFragment extends DialogFragment {


    public interface LogoutDialogListener {
        void onDialogSubmitLogout();
    }

    private LogoutDialogFragment.LogoutDialogListener listener;


    public void setListener(com.example.musicdiary.Settings.LogoutDialogFragment.LogoutDialogListener listener) {
        this.listener = listener;
    }

    public static com.example.musicdiary.Settings.LogoutDialogFragment newInstance() {
        com.example.musicdiary.Settings.LogoutDialogFragment dialog = new com.example.musicdiary.Settings.LogoutDialogFragment();
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof com.example.musicdiary.Settings.DeleteAccountDialog.DeleteAccountDialogListener) {
            listener = (com.example.musicdiary.Settings.LogoutDialogFragment.LogoutDialogListener) getParentFragment();
        } else if (context instanceof com.example.musicdiary.Settings.DeleteAccountDialog.DeleteAccountDialogListener) {
            listener = (com.example.musicdiary.Settings.LogoutDialogFragment.LogoutDialogListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you really want to Logout of your account??")
                .setPositiveButton("Yes", (dialog, id) -> {
                    if (listener != null) {
                        listener.onDialogSubmitLogout();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> com.example.musicdiary.Settings.LogoutDialogFragment.this.getDialog().cancel());

        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_layout_background);


        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            int color = ContextCompat.getColor(requireContext(), R.color.colorAccentBlue);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
        }
    }
}


