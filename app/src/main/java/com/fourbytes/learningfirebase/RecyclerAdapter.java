package com.fourbytes.learningfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<Message> messageList;

    public RecyclerAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvMessage;

        public MyViewHolder(final View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvMessage = view.findViewById(R.id.tv_message);
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        String name = messageList.get(position).getName();
        String message = messageList.get(position).getMessage();

        holder.tvName.setText(name);
        holder.tvMessage.setText(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
