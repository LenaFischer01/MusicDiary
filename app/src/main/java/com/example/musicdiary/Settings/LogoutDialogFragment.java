package com.example.musicdiary.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.musicdiary.R;

/**
 * DialogFragment to confirm user logout.
 */
public class LogoutDialogFragment extends DialogFragment {

    /**
     * Listener interface to handle logout confirmation.
     */
    public interface LogoutDialogListener {
        void onDialogSubmitLogout();
    }

    private LogoutDialogFragment.LogoutDialogListener listener;

    /**
     * Sets the listener to handle dialog submission.
     * @param listener The listener instance.
     */
    public void setListener(LogoutDialogFragment.LogoutDialogListener listener) {
        this.listener = listener;
    }

    /**
     * Creates a new instance of LogoutDialogFragment.
     * @return New LogoutDialogFragment instance.
     */
    public static LogoutDialogFragment newInstance() {
        return new LogoutDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Attach listener from parent fragment or activity if it implements the listener interface
        if (getParentFragment() instanceof LogoutDialogListener) {
            listener = (LogoutDialogListener) getParentFragment();
        } else if (context instanceof LogoutDialogListener) {
            listener = (LogoutDialogListener) context;
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
                .setNegativeButton("Cancel", (dialog, id) -> LogoutDialogFragment.this.getDialog().cancel());

        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_layout_background);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = requireContext().getTheme();
            theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true);
            int color = typedValue.data;

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
        }
    }
}