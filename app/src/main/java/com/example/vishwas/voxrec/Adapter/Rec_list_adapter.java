package com.example.vishwas.voxrec.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vishwas.voxrec.R;
import com.example.vishwas.voxrec.TimeAgo;

import java.io.File;

public class Rec_list_adapter extends RecyclerView.Adapter<Rec_list_adapter.Rec_ViewHolder>
{
    private File[] allFiles;
    private TimeAgo timeAgo;
    private onItemList_click onitemList_click;

    public Rec_list_adapter(File[] allFiles, onItemList_click onitemList_click)
    {
        this.allFiles = allFiles;
        this.onitemList_click = onitemList_click;
    }

    @NonNull
    @Override
    public Rec_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item,parent,false);
        timeAgo = new TimeAgo();
        return new Rec_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Rec_ViewHolder holder, int position) {

        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));

    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class Rec_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView list_image_view;
        private TextView list_title;
        private TextView list_date;

        public Rec_ViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image_view = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            onitemList_click.onClick_Listener(allFiles[getAdapterPosition()],getAdapterPosition());

        }
    }

    public interface onItemList_click
    {
        void onClick_Listener(File file,int position);
    }
}
