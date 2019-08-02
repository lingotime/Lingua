package com.lingua.lingua.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
                // ensure current card number is associated with a card
                if (position != RecyclerView.NO_POSITION) {
                    // get user associated with current card number
                    User clickedUser = usersList.get(position);

                    // create a confirm dialog with an optional message field
                    View confirmDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_friend_request, null);
                    EditText confirmDialogMessageField = confirmDialogView.findViewById(R.id.dialog_friend_request_et);

                    // build the confirm dialog
                    AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(context);
                    confirmDialogBuilder.setView(confirmDialogView);
                    confirmDialogBuilder.setTitle("Confirm Friend Request to " + clickedUser.getUserName());
                    confirmDialogBuilder.setMessage("Send a message with your friend request.");
                    confirmDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String friendRequestMessage = confirmDialogMessageField.getText().toString();

                            if (!friendRequestMessage.equals("")) {
                                sendFriendRequest(currentUser, clickedUser, friendRequestMessage);
                            }

                            dialogInterface.cancel();
                        }
                    });
                    confirmDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    // display the confirm dialog
                    AlertDialog confirmDialog = confirmDialogBuilder.create();
                    confirmDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    private void sendFriendRequest(User currentUser, User clickedUser, String friendRequestMessage) {
        // disable the button and change its text
        sendRequestButton.setText("Friend Request Sent");
        sendRequestButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        sendRequestButton.setEnabled(false);

        Firebase.setAndroidContext(context);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com");

        // save friend request
        String friendRequestId = reference.child("friend-requests").push().getKey();

        Map<String, String> map = new HashMap<>();
        map.put("message", friendRequestMessage);
        map.put("senderId", currentUser.getUserID());
        map.put("receiverId", clickedUser.getUserID());
        map.put("receiverName", clickedUser.getUserName());
        map.put("senderName", currentUser.getUserName());
        map.put("timestamp", new Date().toString());
        map.put("id", friendRequestId);

        reference.child("friend-requests").child(friendRequestId).setValue(map);
        if (currentUser.getExploreLanguages() != null) {
            ArrayList<String> languages = currentUser.getExploreLanguages();
            reference.child("friend-requests").child(friendRequestId).child("exploreLanguages").setValue(languages);
        }

        // save friend request reference in user objects
        reference.child("users").child(currentUser.getUserID()).child("sent-friend-requests").child(friendRequestId).setValue(true);
        reference.child("users").child(clickedUser.getUserID()).child("received-friend-requests").child(friendRequestId).setValue(true);

        // send notification to other user
        sendFriendRequestNotification(clickedUser.getUserID());

        Toast.makeText(context, "Friend request sent!", Toast.LENGTH_SHORT).show();

        //TODO: change button to "friend request sent"
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
        }
    }
}