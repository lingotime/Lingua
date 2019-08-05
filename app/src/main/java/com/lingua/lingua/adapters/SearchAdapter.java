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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private User currentUser;

    Context context;
    List<User> usersList;
    List<User> hiddenUsersList;

    public SearchAdapter(Context context, List<User> usersList, List<User> hiddenUsersList, User currentUser) {
        this.context = context;
        this.usersList = usersList;
        this.hiddenUsersList = hiddenUsersList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private ImageView profilePhotoImage;
        private TextView nameText;
        private TextView countryAndAgeText;
        private ImageView flagImage;
        private Button sendRequestButton;

        public ViewHolder(View userItemView) {
            super(userItemView);

            card = userItemView.findViewById(R.id.item_user_search_card);
            profilePhotoImage = userItemView.findViewById(R.id.item_user_search_profile_image);
            nameText = userItemView.findViewById(R.id.item_user_search_name_text);
            countryAndAgeText = userItemView.findViewById(R.id.item_user_search_country_and_age_text);
            flagImage = userItemView.findViewById(R.id.item_user_search_flag);
            sendRequestButton = userItemView.findViewById(R.id.item_user_search_send_request_button);

            // send user a friend request, remove them from view, and add a new user to timeline
            sendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get current card number
                    int position = getAdapterPosition();

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

                                sendFriendRequest(currentUser, clickedUser, friendRequestMessage, position);

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

        public void bind(User user) {
            // load user flag and profile photo into place
            Glide.with(context).load(context.getResources().getIdentifier(CountryInformation.COUNTRY_CODES.get(user.getUserOriginCountry()), "drawable", context.getPackageName())).apply(RequestOptions.circleCropTransform()).into(flagImage);
            Glide.with(context).load(user.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePhotoImage);

            // load other user information into place
            // use getAge() helper method to convert birth date to age
            nameText.setText(user.getUserName());
            countryAndAgeText.setText("from " + user.getUserOriginCountry() + ", " + getAge(user.getUserBirthDate()) + " years old");
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

        private void sendFriendRequest(User currentUser, User clickedUser, String friendRequestMessage, int position) {
            // ensure clicked user did not send friend request to current user while current user was typing
            if (currentUser.getPendingReceivedFriendRequests() != null) {
                for (String pendingReceivedFriendRequest : currentUser.getPendingReceivedFriendRequests()) {
                    if (pendingReceivedFriendRequest.equals(clickedUser.getUserID())) {
                        // change the button to reflect new info
                        sendRequestButton.setText("Friend Request Received");
                        sendRequestButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                        sendRequestButton.setEnabled(false);

                        Toast.makeText(context, "You already received a friend request from this user.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            // disable the button and change its text
            sendRequestButton.setText("Friend Request Sent");
            sendRequestButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            sendRequestButton.setEnabled(false);

            // add clicked user to current user's sent friend request user list
            if (currentUser.getPendingSentFriendRequests() == null) {
                currentUser.setPendingSentFriendRequests(new ArrayList<String>(Arrays.asList(clickedUser.getUserID())));
            } else {
                currentUser.getPendingSentFriendRequests().add(clickedUser.getUserID());
            }

            // add current user to clicked user's received friend request user list
            if (clickedUser.getPendingReceivedFriendRequests() == null) {
                clickedUser.setPendingReceivedFriendRequests(new ArrayList<String>(Arrays.asList(currentUser.getUserID())));
            } else {
                clickedUser.getPendingReceivedFriendRequests().add(currentUser.getUserID());
            }

            // create a new database reference
            Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

            // create friend request
            String friendRequestId = databaseReference.child("friendRequests").push().getKey();

            Map<String, String> map = new HashMap<>();
            map.put("message", friendRequestMessage);
            map.put("receiverId", clickedUser.getUserID());
            map.put("receiverName", clickedUser.getUserName());
            map.put("receiverPhotoUrl", clickedUser.getUserProfilePhotoURL());
            map.put("senderId", currentUser.getUserID());
            map.put("senderName", currentUser.getUserName());
            map.put("senderPhotoUrl", currentUser.getUserProfilePhotoURL());
            map.put("timestamp", new Date().toString());
            map.put("id", friendRequestId);

            // save new friend request data to database
            databaseReference.child("users").child(currentUser.getUserID()).child("sentFriendRequests").child(friendRequestId).setValue(true);
            databaseReference.child("users").child(clickedUser.getUserID()).child("receivedFriendRequests").child(friendRequestId).setValue(true);

            databaseReference.child("friendRequests").child(friendRequestId).setValue(map);
            databaseReference.child("friendRequests").child(friendRequestId).child("exploreLanguages").setValue(currentUser.getExploreLanguages());

            // send notification to other user
            sendFriendRequestNotification(clickedUser.getUserID());
        }

        private void sendFriendRequestNotification(String userId) {
            // send notification
            Notification notification = new Notification("Friend request from " + currentUser.getUserID(), currentUser.getUserName() + " sent you a friend request!", userId, null); // null sent as the final parameter since this is not a video chat notification

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
}