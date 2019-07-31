package com.lingua.lingua.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.lingua.lingua.R;
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
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
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
    }
}