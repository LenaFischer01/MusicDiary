package com.example.musicdiary.Friends.Overview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.musicdiary.R;

/**
 * DialogFragment to confirm the removal of a friend.
 */
public class RemoveFriendDialog extends DialogFragment {

    /**
     * Listener interface to handle dialog submission for friend removal.
     */
    public interface RemoveFriendDialogListener {
        /**
         * Called when the user confirms friend removal.
         * @param friendUserId ID of the friend to remove
         * @param position Position of the friend in the list
         */
        void onDialogSubmit(String friendUserId, int position);
    }

    private static final String ARG_FRIEND_USER_ID = "friend_user_id";
    private static final String ARG_POSITION = "position";

    private RemoveFriendDialogListener listener;
    private String friendUserId;
    private int position;

    /**
     * Sets the listener to handle dialog result callbacks.
     * @param listener The listener instance
     */
    public void setListener(RemoveFriendDialogListener listener) {
        this.listener = listener;
    }

    /**
     * Creates a new instance of the dialog with the specified friend ID and position.
     * @param friendUserId Friend's user ID
     * @param position Position in the list
     * @return New RemoveFriendDialog instance
     */
    public static RemoveFriendDialog newInstance(String friendUserId, int position) {
        RemoveFriendDialog dialog = new RemoveFriendDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FRIEND_USER_ID, friendUserId);
        args.putInt(ARG_POSITION, position);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Try to attach listener from parent fragment or context
        if (getParentFragment() instanceof RemoveFriendDialogListener) {
            listener = (RemoveFriendDialogListener) getParentFragment();
        } else if (context instanceof RemoveFriendDialogListener) {
            listener = (RemoveFriendDialogListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            friendUserId = getArguments().getString(ARG_FRIEND_USER_ID);
            position = getArguments().getInt(ARG_POSITION);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you really want to remove your friend??")
                .setPositiveButton("Remove friend", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onDialogSubmit(friendUserId, position);
                        }
                    }
                })
                .setNegativeButton("Keep friend", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RemoveFriendDialog.this.getDialog().cancel();
                    }
                });

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