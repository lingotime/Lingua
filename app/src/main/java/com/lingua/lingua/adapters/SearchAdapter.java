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
import com.lingua.lingua.R;
import com.lingua.lingua.models.FriendRequest;
import com.lingua.lingua.models.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        private TextView ageText;
        private TextView countryText;
        private TextView genderText;
        private Button sendRequestButton;

        public ViewHolder(View userItemView) {
            super(userItemView);

            card = userItemView.findViewById(R.id.item_user_search_card);
            profilePhotoImage = userItemView.findViewById(R.id.item_user_search_profile_image);
            nameText = userItemView.findViewById(R.id.item_user_search_name_text);
            ageText = userItemView.findViewById(R.id.item_user_search_age_text);
            countryText = userItemView.findViewById(R.id.item_user_search_country_text);
            genderText = userItemView.findViewById(R.id.item_user_search_gender_text);
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
                        EditText confirmDialogMessageField = confirmDialogView.findViewById(R.id.dialog_friend_request_message_field);

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
            // load user profile photo into place
            Glide.with(context).load(user.getUserProfilePhotoURL()).placeholder(R.drawable.man).apply(RequestOptions.circleCropTransform()).into(profilePhotoImage);

            // load other user information into place
            // use getAge() helper method to convert birth date to age
            nameText.setText(user.getUserName());
            ageText.setText(getAge(user.getUserBirthDate()) + " years old");
            countryText.setText("from " + user.getUserOriginCountry());
            genderText.setText(user.getUserGender());
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
                        Toast.makeText(context, "You already received a friend request from this user.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

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

            // create a new friend request object
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setFriendRequestID(databaseReference.child("friend-requests").push().getKey());
            friendRequest.setFriendRequestStatus("Pending");
            friendRequest.setFriendRequestMessage(friendRequestMessage);
            friendRequest.setSenderUser(currentUser.getUserID());
            friendRequest.setReceiverUser(clickedUser.getUserID());
            friendRequest.setCreatedTime((new Date()).toString());
            friendRequest.setRespondedTime("Not Responded");

            // save new friend request data to database
            databaseReference.child("users").child(currentUser.getUserID()).setValue(currentUser);
            databaseReference.child("users").child(clickedUser.getUserID()).setValue(clickedUser);
            databaseReference.child("friend-requests").child(friendRequest.getFriendRequestID()).setValue(friendRequest);

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
    }
}