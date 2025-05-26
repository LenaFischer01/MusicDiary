package com.example.musicdiary.Feed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.musicdiary.R;

/**
 * Dialog fragment to prompt user to enter a song name.
 */
public class ChooseSongDialogFragment extends DialogFragment {

    /**
     * Listener interface to handle the submit action with the entered song name.
     */
    public interface ChooseSongDialogListener {
        void onDialogSubmit(String songName);
    }

    private EditText inputEditText;
    private ChooseSongDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Try to get listener from the target fragment
            listener = (ChooseSongDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ChooseSongDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.input_song_dialog, null);
        inputEditText = view.findViewById(R.id.insertSongEditText);

        builder.setView(view)
                .setTitle("Enter song details")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            String songName = inputEditText.getText().toString();
                            listener.onDialogSubmit(songName);
                        }
                        else{
                            // Listener is null
                            Toast.makeText(getContext(), "Listener ist null" , Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChooseSongDialogFragment.this.getDialog().cancel();
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
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = requireContext().getTheme();
            theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true);
            int color = typedValue.data;

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
        }
    }
}