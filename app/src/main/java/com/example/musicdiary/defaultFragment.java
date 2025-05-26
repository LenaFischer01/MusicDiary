package com.example.musicdiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Default empty fragment used as a placeholder or fallback.
 */
public class defaultFragment extends Fragment {

    /**
     * Default constructor.
     */
    public defaultFragment(){
        // Empty
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the default empty layout
        View view = inflater.inflate(R.layout.default_empty, container, false);

        return view;
    }
}