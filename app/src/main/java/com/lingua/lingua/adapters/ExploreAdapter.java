package com.lingua.lingua.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lingua.lingua.R;
import com.lingua.lingua.models.User;

import java.util.List;

/*
RecyclerView Adapter that adapts User objects to the viewholders in the recyclerview
*/

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private Context context;
    private List<User> users;

    private ImageView ivProfile;
    private ImageView ivFlag;
    private TextView tvBio;
    private TextView tvName;
    private TextView tvAge;
    private TextView tvGender;
    private TextView tvFrom;

    public ExploreAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final User user = users.get(position);
        tvName.setText(user.getFirstName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.item_user_profile_image);
            ivFlag = itemView.findViewById(R.id.item_user_flag);
            tvName = itemView.findViewById(R.id.item_user_name_text);
            tvAge = itemView.findViewById(R.id.item_user_age_text);
            tvBio = itemView.findViewById(R.id.item_user_biography_text);
            tvGender = itemView.findViewById(R.id.item_user_gender_text);
            tvFrom = itemView.findViewById(R.id.item_user_country_text);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<User> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }
}
