package com.lingua.lingua.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.lingua.lingua.CountryInformation;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;
import com.lingua.lingua.notifyAPI.Notification;
import com.lingua.lingua.notifyAPI.TwilioFunctionsAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
RecyclerView Adapter that adapts User objects to the viewholders in the recyclerview
*/

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {
    private User currentUser;

    Context context;
    List<User> usersList;
    List<User> hiddenUsersList;
    private CardView card;
    private ImageView flagImage;
    private ImageView profilePhotoImage;
    private ImageView liveStatusSignal;
    private TextView nameText;
    private TextView countryText;
    private TextView ageText;
    private TextView biographyText;
    private Button sendRequestButton;
    private Button removeButton;

    public ExploreAdapter(Context context, List<User> usersList, List<User> hiddenUsersList, User currentUser) {
        this.context = context;
        this.usersList = usersList;
        this.hiddenUsersList = hiddenUsersList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = usersList.get(position);

        // load user flag and profile photo into place
        Glide.with(context).load(context.getResources().getIdentifier(CountryInformation.COUNTRY_CODES.get(user.getUserOriginCountry()), "drawable", context.getPackageName())).into(flagImage);
        Glide.with(context).load(user.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePhotoImage);

        // load user live status into place
        if (user.isOnline()) {
            liveStatusSignal.setVisibility(View.VISIBLE);
        } else {
            liveStatusSignal.setVisibility(View.INVISIBLE);
        }

        // load other user information into place
        // use getAge() helper method to convert birth date to age
        nameText.setText(user.getUserName());
        countryText.setText("from " + user.getUserOriginCountry());
        ageText.setText(getAge(user.getUserBirthDate()) + " years old");
        biographyText.setText(user.getUserBiographyText());

        // send user a friend request, remove them from view, and add a new user to timeline
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // process send friend request ... Marta's code
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_friend_request, null);
                dialogBuilder.setView(dialogView);

                dialogBuilder.setTitle("Send friend request to " + user.getUserName());
                dialogBuilder.setMessage("Say hi and tell " + user.getUserName() + " a little about yourself!");
                dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = dialogView.findViewById(R.id.dialog_friend_request_et);
                        String message = editText.getText().toString();
                        if (!message.equals("")) {
                            sendFriendRequest(message, user.getUserID(), user.getUserName());
                        } else {
                            Toast.makeText(context, "Can't send a friend request without any text, say hi!", Toast.LENGTH_LONG).show();
                        }
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

                // add user to sent friend request user list
                if (currentUser.getPendingSentFriendRequests() == null) {
                    currentUser.setPendingSentFriendRequests(new ArrayList<String>(Arrays.asList(usersList.get(position).getUserID())));
                } else {
                    currentUser.getPendingSentFriendRequests().add(usersList.get(position).getUserID());
                }

                // save updated sent friend request user list to database
                Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
                databaseReference.child(currentUser.getUserID()).setValue(currentUser);

                // remove user from displayed user list
                usersList.remove(position);

                // check if there are more users to load
                if (!hiddenUsersList.isEmpty()) {
                    usersList.add(hiddenUsersList.get(0));
                    hiddenUsersList.remove(0);
                }

                // notify adapter of changes in data
                notifyDataSetChanged();
            }
        });

        // remove user from view without friend request and add a new user to timeline
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add user to declined user list
                if (currentUser.getDeclinedUsers() == null) {
                    currentUser.setDeclinedUsers(new ArrayList<String>(Arrays.asList(usersList.get(position).getUserID())));
                } else {
                    currentUser.getDeclinedUsers().add(usersList.get(position).getUserID());
                }

                // save updated declined user list to database
                Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com/users");
                databaseReference.child(currentUser.getUserID()).setValue(currentUser);

                // remove user from displayed user list
                usersList.remove(position);

                // check if there are more users to load
                if (!hiddenUsersList.isEmpty()) {
                    usersList.add(hiddenUsersList.get(0));
                    hiddenUsersList.remove(0);
                }

                // notify adapter of changes in data
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    private void sendFriendRequest(String message, String receiverId, String receiverName) {

        Firebase.setAndroidContext(context);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com");

        // save friend request
        String friendRequestId = reference.child("friend-requests").push().getKey();

        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        map.put("senderId", currentUser.getUserID());
        map.put("receiverId", receiverId);
        map.put("receiverName", receiverName);
        map.put("senderName", currentUser.getUserName());
        map.put("timestamp", new Date().toString());
        map.put("id", friendRequestId);

        reference.child("friend-requests").child(friendRequestId).setValue(map);

        // save friend request reference in user objects
        reference.child("users").child(currentUser.getUserID()).child("sent-friend-requests").child(friendRequestId).setValue(true);
        reference.child("users").child(receiverId).child("received-friend-requests").child(friendRequestId).setValue(true);

        // send notification to other user
        sendFriendRequestNotification(receiverId);

        Toast.makeText(context, "Friend request sent!", Toast.LENGTH_SHORT).show();
    }

    private int getAge(String birthDateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

            Date birthDate = dateFormat.parse(birthDateString);
            Date currentDate = new Date();

            return Period.between(birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getYears();
        } catch (ParseException exception) {
            Log.e("ExploreAdapter", "There was an issue parsing a user's registered birth date.");
        }

        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View userItemView) {
            super(userItemView);

            card = userItemView.findViewById(R.id.item_user_card);
            flagImage = userItemView.findViewById(R.id.item_user_flag);
            profilePhotoImage = userItemView.findViewById(R.id.item_user_profile_image);
            liveStatusSignal = userItemView.findViewById(R.id.item_user_live_signal_image);
            nameText = userItemView.findViewById(R.id.item_user_name_text);
            countryText = userItemView.findViewById(R.id.item_user_country_text);
            ageText = userItemView.findViewById(R.id.item_user_age_text);
            biographyText = userItemView.findViewById(R.id.item_user_biography_text);
            sendRequestButton = userItemView.findViewById(R.id.item_user_send_request_button);
            removeButton = userItemView.findViewById(R.id.item_user_remove_button);
        }
    }

    private void sendFriendRequestNotification(String userId) {
        // send notification
        Notification notification = new Notification(currentUser.getUserName() + " sent you a friend request!", userId);

        TwilioFunctionsAPI.notify(notification).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccess()) {
                    String message = "Sending notification failed: " + response.code() + " " + response.message();
                    Log.e("ExploreAdapter", message);
                } else {
                    Log.i("ExploreAdapter", "Sending notification success: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String message = "Sending notification failed: " + t.getMessage();
                Log.e("ExploreAdapter", message);
            }
        });
    }
}