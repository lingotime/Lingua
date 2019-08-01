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
                                sendFriendRequest(currentUser, clickedUser, friendRequestMessage, position);
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

    private void sendFriendRequest(User currentUser, User clickedUser, String friendRequestMessage, int position) {
        // ensure clicked user did not send friend request to current user while current user was typing
        if (currentUser.getPendingReceivedFriendRequests() != null) {
            for (String pendingReceivedFriendRequest : currentUser.getPendingReceivedFriendRequests()) {
                if (pendingReceivedFriendRequest.equals(clickedUser.getUserID())) {
                    Toast.makeText(context, "You already received a friend request from this user.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        Firebase.setAndroidContext(context);
        Firebase reference = new Firebase("https://lingua-project.firebaseio.com");

        // add clicked user to current user's sent friend request user list...Fausto's way
        if (currentUser.getPendingSentFriendRequests() == null) {
            currentUser.setPendingSentFriendRequests(new ArrayList<String>(Arrays.asList(clickedUser.getUserID())));
        } else {
            currentUser.getPendingSentFriendRequests().add(clickedUser.getUserID());
        }

        // add current user to clicked user's received friend request user list...Fausto's way
        if (clickedUser.getPendingReceivedFriendRequests() == null) {
            clickedUser.setPendingReceivedFriendRequests(new ArrayList<String>(Arrays.asList(currentUser.getUserID())));
        } else {
            clickedUser.getPendingReceivedFriendRequests().add(currentUser.getUserID());
        }

        // save new friend request data to database...Fausto's way
        reference.child("users").child(currentUser.getUserID()).setValue(currentUser);
        reference.child("users").child(clickedUser.getUserID()).setValue(clickedUser);

        // save friend request...Marta's way
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

        // save friend request reference in user objects...Marta's way
        reference.child("users").child(currentUser.getUserID()).child("sent-friend-requests").child(friendRequestId).setValue(true);
        reference.child("users").child(clickedUser.getUserID()).child("received-friend-requests").child(friendRequestId).setValue(true);

        // send notification to other user
        sendFriendRequestNotification(clickedUser.getUserID());

        Toast.makeText(context, "Friend request sent!", Toast.LENGTH_SHORT).show();

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
            removeButton = userItemView.findViewById(R.id.item_user_remove_button);
        }
    }
}