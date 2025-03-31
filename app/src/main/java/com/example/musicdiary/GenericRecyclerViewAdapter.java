package com.example.musicdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenericRecyclerViewAdapter<T> extends RecyclerView.Adapter<GenericRecyclerViewAdapter.ViewHolder> {

    private List<T> items;
    private int layoutID;
    private BindViewListener<T> bindViewListener;

    // INTERFACE VIEWLISTENER ----------------------------------------------------------------------
    public interface BindViewListener<T> {
        void onBindView(ViewHolder holder, T item);
    }

    // ---------------------------------------------------------------------------------------------

    public GenericRecyclerViewAdapter(List<T> items, int layoutID, BindViewListener<T> bindViewListener){
        this.items = items;
        this.layoutID = layoutID;
        this.bindViewListener = bindViewListener;
    }

    // OVERRIDE METHODS ----------------------------------------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bindViewListener.onBindView(holder, items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // VIEWHOLDER CLASS ----------------------------------------------------------------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
