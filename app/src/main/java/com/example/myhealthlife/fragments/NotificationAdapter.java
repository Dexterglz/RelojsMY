package com.example.myhealthlife.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhealthlife.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> list;

    public NotificationAdapter(List<NotificationItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());

        // Fecha/hora si la necesitas
        Date date = new Date(item.getTimestamp());
        holder.time.setText(DateFormat.getDateTimeInstance().format(date));

        holder.btnDelete.setOnClickListener(v -> {
            list.remove(position);
            notifyItemRemoved(position);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            message = itemView.findViewById(R.id.txtMessage);
            time = itemView.findViewById(R.id.txtTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

