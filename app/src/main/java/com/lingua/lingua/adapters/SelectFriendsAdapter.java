package com.lingua.lingua.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;

import java.util.List;

public class SelectFriendsAdapter extends RecyclerView.Adapter<SelectFriendsAdapter.ViewHolder> {

    private Context context;
    public List<User> friends;

    private TextView tvName, tvBio;
    private ImageView ivProfile, ivCheck;

    String activityName, userId, userName;

    public SelectFriendsAdapter(Context context, List<User> friends, String activityName) {
        this.context = context;
        this.friends = friends;
        this.activityName = activityName;

        SharedPreferences prefs = context.getSharedPreferences("com.lingua.lingua", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userName = prefs.getString("userName", "");
    }

    @NonNull
    @Override
    public SelectFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_small, parent, false);
        return new SelectFriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectFriendsAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        User user = friends.get(position);
        tvName.setText(user.getUserName());
        tvBio.setText(user.getUserBiographyText());
        // load profile pic
        RequestOptions requestOptionsMedia = new RequestOptions();
        requestOptionsMedia = requestOptionsMedia.transforms(new CenterCrop(), new RoundedCorners(400));
        Glide.with(context)
                .load(user.getUserProfilePhotoURL())
                .apply(requestOptionsMedia)
                .into(ivProfile);
        if (activityName.equals("CreateGroupActivity") || !user.isSelected()) {
            ivCheck.setVisibility(View.GONE);
        } else {
            ivCheck.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.user_small_iv);
            tvName = itemView.findViewById(R.id.user_small_name_tv);
            tvBio = itemView.findViewById(R.id.user_small_bio_tv);
            ivCheck = itemView.findViewById(R.id.user_small_check_iv);

            if (!activityName.equals("CreateGroupActivity")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            User user = friends.get(position);
                            if (user.isSelected()) {
                                user.setSelected(false);
                            } else {
                                user.setSelected(true);
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }

    }

}