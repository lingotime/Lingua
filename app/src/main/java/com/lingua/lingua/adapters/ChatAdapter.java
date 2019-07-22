package com.lingua.lingua.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lingua.lingua.R;
import com.lingua.lingua.activities.TextChatActivity;
import com.lingua.lingua.models.Conversation;
import com.lingua.lingua.models.User;

import java.util.List;

/*
RecyclerView Adapter that adapts Conversation objects to the viewholders in the recyclerview
*/

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<Conversation> chats;

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvText;
    private TextView tvTimestamp;

    public ChatAdapter(Context context, List<Conversation> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {
        final Conversation chat = chats.get(position);
        final User user = chat.getUsers().get(0);
        tvName.setText(user.getFirstName());
        tvText.setText(chat.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.item_user_profile_image);
            tvName = itemView.findViewById(R.id.item_conversation_name_text);
            tvText = itemView.findViewById(R.id.item_conversation_message_text);
            tvTimestamp = itemView.findViewById(R.id.item_conversation_timestamp_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, TextChatActivity.class);
                context.startActivity(intent);
            }
        }
    }
}
