package com.lingua.lingua;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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
    private Button friendRequestButton;

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

        friendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_friend_request, null);
                dialogBuilder.setView(dialogView);

                dialogBuilder.setTitle("Send friend request to " + user.getFirstName());
                dialogBuilder.setMessage("Tell " + user.getFirstName() + " a little about yourself!");
                dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: send friend request
                                }
                            });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("ExploreAdapter", "Cancelled friend request");
                                }
                            });
                AlertDialog dialog = dialogBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.item_user_iv_profile);
            ivFlag = itemView.findViewById(R.id.item_user_flag);
            tvName = itemView.findViewById(R.id.item_user_tv_name);
            tvAge = itemView.findViewById(R.id.item_user_tv_age);
            tvBio = itemView.findViewById(R.id.item_user_tv_bio);
            tvGender = itemView.findViewById(R.id.item_user_tv_gender);
            tvFrom = itemView.findViewById(R.id.item_user_tv_from);
            friendRequestButton = itemView.findViewById(R.id.item_user_friend_request_button);
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
