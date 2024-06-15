package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private SimpleDateFormat dateFormat;

    public MessagesAdapter(List<Message> messageList) {
        this.messageList = messageList;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageContent.setText(message.getMessageContent());
        holder.messageSender.setText(message.getSenderId());
        holder.messageTimestamp.setText(dateFormat.format(message.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateData(List<Message> newMessageList) {
        this.messageList = newMessageList;
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, messageSender, messageTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.messageContent);
            messageSender = itemView.findViewById(R.id.messageSender);
            messageTimestamp = itemView.findViewById(R.id.messageTimestamp);
        }
    }
}
