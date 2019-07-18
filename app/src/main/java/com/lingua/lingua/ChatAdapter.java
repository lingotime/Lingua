package com.lingua.lingua;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<Object> chats;

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvText;
    private TextView tvTimestamp;

    public ChatAdapter(Context context, List<Object> chats) {
        this.context = context;
//        this.users = users;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {
//        final User user = users.get(position);

    }

    @Override
    public int getItemCount() {
        //       return users.size();
        return 5;
    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.item_user_iv_profile);
            tvName = itemView.findViewById(R.id.item_chat_tv_name);
            tvText = itemView.findViewById(R.id.item_chat_tv_text);
            tvTimestamp = itemView.findViewById(R.id.item_chat_tv_timestamp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                context.startActivity(intent);
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        chats.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Object> list) {
        chats.addAll(list);
        notifyDataSetChanged();
    }
}
