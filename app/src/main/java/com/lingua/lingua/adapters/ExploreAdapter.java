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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.lingua.lingua.CountryInformation;
import com.lingua.lingua.R;
import com.lingua.lingua.models.User;
import com.lingua.lingua.notifyAPI.Notification;
import com.lingua.lingua.notifyAPI.TwilioFunctionsAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {
    private User currentUser;

    Context context;
    List<User> usersList;
    List<User> hiddenUsersList;

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
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView flagImage;
        private ImageView profilePhotoImage;
        private ImageView liveStatusSignal;
        private TextView nameText;
        private TextView countryText;
        private TextView ageText;
        private TextView biographyText;
        private ChipGroup knownLanguagesChips;
        private Button sendRequestButton;

        public ViewHolder(View userItemView) {
            super(userItemView);

            flagImage = userItemView.findViewById(R.id.item_user_flag);
            profilePhotoImage = userItemView.findViewById(R.id.item_user_profile_image);
            liveStatusSignal = userItemView.findViewById(R.id.item_user_live_signal_image);
            nameText = userItemView.findViewById(R.id.item_user_name_text);
            countryText = userItemView.findViewById(R.id.item_user_country_text);
            ageText = userItemView.findViewById(R.id.item_user_age_text);
            biographyText = userItemView.findViewById(R.id.item_user_biography_text);
            knownLanguagesChips = userItemView.findViewById(R.id.item_user_known_languages_chip_group);
            sendRequestButton = userItemView.findViewById(R.id.item_user_send_request_button);

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

                                if (!friendRequestMessage.equals("")) {
                                    checkIfPossibleAndSendFriendRequest(currentUser, clickedUser, friendRequestMessage, position);
                                } else {
                                    Toast.makeText(context, "You can't send a friend request with no message, say hi!", Toast.LENGTH_SHORT).show();
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

        public void bind(User user) {
            // load user flag and profile photo into place
            Glide.with(context).load(context.getResources().getIdentifier(CountryInformation.COUNTRY_CODES.get(user.getUserOriginCountry()), "drawable", context.getPackageName())).into(flagImage);
            Glide.with(context).load(user.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePhotoImage);

            // load user live status into place
            if (user.isOnline()) {
                liveStatusSignal.setVisibility(View.VISIBLE);
            } else {
                liveStatusSignal.setVisibility(View.GONE);
            }

            // load other user information into place
            // use getAge() helper method to convert birth date to age
            nameText.setText(user.getUserName());
            countryText.setText("from " + user.getUserOriginCountry());
            ageText.setText(getAge(user.getUserBirthDate()) + " years old");
            biographyText.setText(user.getUserBiographyText());

            // load chips of known languages
            for (String knownLanguage : user.getKnownLanguages()) {
                Chip knownLanguageChip = new Chip(context);
                knownLanguageChip.setText(knownLanguage);
                knownLanguagesChips.addView(knownLanguageChip);
            }
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

        private void checkIfPossibleAndSendFriendRequest(User currentUser, User clickedUser, String friendRequestMessage, int position) {
            String url = "https://lingua-project.firebaseio.com/users/" + currentUser.getUserID() + ".json";
            StringRequest request = new StringRequest(Request.Method.GET, url, s -> {
                try {
                    JSONObject userObject = new JSONObject(s);

                    // get received friend requests
                    if (userObject.has("receivedFriendRequests")) {
                        JSONObject receivedFriendRequests = userObject.getJSONObject("receivedFriendRequests");
                        Iterator receivedFriendRequestKeys = receivedFriendRequests.keys();
                        ArrayList<String> receivedFriendRequestUserIDs = new ArrayList<>();
                        while (receivedFriendRequestKeys.hasNext()) {
                            String key = receivedFriendRequestKeys.next().toString();
                            String userID = key.split("@")[0];
                            receivedFriendRequestUserIDs.add(userID);
                        }
                        currentUser.setPendingReceivedFriendRequests(receivedFriendRequestUserIDs);
                    }

                    sendFriendRequest(currentUser, clickedUser, friendRequestMessage, position);

                } catch (JSONException e) {
                    Log.e("ExploreFragment", e.toString());
                }
            }, volleyError -> {
                Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
                Log.e("ExploreFragment", "" + volleyError);
            });

            RequestQueue rQueue = Volley.newRequestQueue(context);
            rQueue.add(request);
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
                        Toast.makeText(context, "You already received a friend request from this user.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            // disable the button and change its text
            sendRequestButton.setText("Friend Request Sent");
            sendRequestButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            sendRequestButton.setEnabled(false);

            // create a new database reference
            Firebase databaseReference = new Firebase("https://lingua-project.firebaseio.com");

            // create friend request
            String senderId = currentUser.getUserID();
            String receiverId = clickedUser.getUserID();
            String friendRequestId = senderId + "@" + receiverId;

            Map<String, String> map = new HashMap<>();
            map.put("message", friendRequestMessage);
            map.put("receiverId", receiverId);
            map.put("receiverName", clickedUser.getUserName());
            map.put("receiverPhotoUrl", clickedUser.getUserProfilePhotoURL());
            map.put("senderId", senderId);
            map.put("senderName", currentUser.getUserName());
            map.put("senderPhotoUrl", currentUser.getUserProfilePhotoURL());
            map.put("timestamp", new Date().toString());
            map.put("id", friendRequestId);

            // add friend request id to local current user
            if (currentUser.getPendingSentFriendRequests() == null) {
                currentUser.setPendingSentFriendRequests(new ArrayList<>(Arrays.asList(friendRequestId)));
            } else {
                currentUser.getPendingSentFriendRequests().add(friendRequestId);
            }

            // save new friend request data to database
            databaseReference.child("users").child(currentUser.getUserID()).child("sentFriendRequests").child(friendRequestId).setValue(true);
            databaseReference.child("users").child(clickedUser.getUserID()).child("receivedFriendRequests").child(friendRequestId).setValue(true);

            databaseReference.child("friendRequests").child(friendRequestId).setValue(map);
            databaseReference.child("friendRequests").child(friendRequestId).child("exploreLanguages").setValue(currentUser.getExploreLanguages());

            // send notification to other user
            sendFriendRequestNotification(clickedUser.getUserID());
        }
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