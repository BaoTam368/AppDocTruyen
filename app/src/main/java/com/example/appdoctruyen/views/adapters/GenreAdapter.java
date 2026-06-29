package com.example.appdoctruyen.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdoctruyen.R;
import com.example.appdoctruyen.models.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private List<Genre> list;

    public GenreAdapter(List<Genre> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_checkbox, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Genre genre = list.get(position);

        holder.cbGenre.setText(genre.getName());
        holder.cbGenre.setChecked(genre.isSelected());
        holder.cbGenre.setOnClickListener(v -> {

            genre.setSelected(holder.cbGenre.isChecked());

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<String> getSelectedGenres() {
        List<String> selected = new ArrayList<>();
        for (Genre genre : list) {
            if (genre.isSelected()) {
                selected.add(genre.getName());
            }
        }
        return selected;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbGenre;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbGenre = itemView.findViewById(R.id.cb_genre);
        }
    }
}