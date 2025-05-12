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

public class RemoveFriendDialog extends DialogFragment {

    public interface RemoveFriendDialogListener {
        void onDialogSubmit(String friendUserId, int position);
    }

    private static final String ARG_FRIEND_USER_ID = "friend_user_id";
    private static final String ARG_POSITION = "position";

    private RemoveFriendDialogListener listener;
    private String friendUserId;
    private int position;

    public void setListener(RemoveFriendDialogListener listener) {
        this.listener = listener;
    }

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
            int color = ContextCompat.getColor(requireContext(), R.color.colorAccent);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
        }
    }
}