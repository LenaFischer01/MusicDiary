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
 * DialogFragment to confirm permanent deletion of the user account.
 */
public class DeleteAccountDialog extends DialogFragment {

    /**
     * Listener interface to handle confirmation of account deletion.
     */
    public interface DeleteAccountDialogListener {
        void onDialogSubmit();
    }

    private DeleteAccountDialog.DeleteAccountDialogListener listener;

    /**
     * Sets the listener to receive dialog submission callbacks.
     * @param listener The listener instance.
     */
    public void setListener(DeleteAccountDialog.DeleteAccountDialogListener listener) {
        this.listener = listener;
    }

    /**
     * Creates a new instance of DeleteAccountDialog.
     * @return New DeleteAccountDialog instance.
     */
    public static DeleteAccountDialog newInstance() {
        DeleteAccountDialog dialog = new DeleteAccountDialog();
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try to attach listener from parent fragment or activity context
        if (getParentFragment() instanceof DeleteAccountDialog.DeleteAccountDialogListener) {
            listener = (DeleteAccountDialog.DeleteAccountDialogListener) getParentFragment();
        } else if (context instanceof DeleteAccountDialog.DeleteAccountDialogListener) {
            listener = (DeleteAccountDialog.DeleteAccountDialogListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you really want to permanently delete your account??")
                .setPositiveButton("Yes", (dialog, id) -> {
                    if (listener != null) {
                        listener.onDialogSubmit();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> DeleteAccountDialog.this.getDialog().cancel());

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