package com.example.musicdiary.Feed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.musicdiary.R;

public class ChooseSongDialogFragment extends DialogFragment {

    public interface ChooseSongDialogListener {
        void onDialogSubmit(String songName);
    }

    private EditText inputEditText;
    private ChooseSongDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
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
                            // Add a check here, if syntax of song is correct, if not, do not submit but wait for it to be edited
                            listener.onDialogSubmit(songName);
                        }
                        else{
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
}
