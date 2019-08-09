package com.lingua.lingua.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lingua.lingua.DateUtil;
import com.lingua.lingua.R;
import com.lingua.lingua.models.Chat;
import com.lingua.lingua.models.Message;

import java.util.List;

/**
RecyclerView Adapter that adapts Message objects to the viewholders in the recyclerview
*/

public class TextChatAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Message> messages;
    private Chat chat;

    private static final int TYPE_MESSAGE_SENT = 1;
    private static final int TYPE_MESSAGE_RECEIVED = 2;

    TextView tvMessage, tvTimestamp, tvSender;

    String userId;

    public TextChatAdapter(Context context, List<Message> messages, Chat chat) {
        this.context = context;
        this.messages = messages;
        this.chat = chat;

        SharedPreferences prefs = context.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new ViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Message message = messages.get(position);
        tvMessage.setText(message.getMessageText());
        String timestamp = DateUtil.getHourAndMinuteFormat(message.getCreatedTime());
        tvTimestamp.setText(timestamp);
        if (chat.isGroup() && getItemViewType(position) == TYPE_MESSAGE_RECEIVED) {
            tvSender.setVisibility(View.VISIBLE);
            tvSender.setText(message.getSenderName());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderId().equals(userId)) {
            // If the current user is the sender of the message
            return TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return TYPE_MESSAGE_RECEIVED;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.message_tv);
            tvTimestamp = itemView.findViewById(R.id.message_timestamp);
            tvSender = itemView.findViewById(R.id.message_sender);
        }
    }
}