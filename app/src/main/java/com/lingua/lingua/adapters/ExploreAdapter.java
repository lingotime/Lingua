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
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lingua.lingua.R;
import com.lingua.lingua.models.Country;
import com.lingua.lingua.models.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {
    Context context;
    List<User> userList;

    private ImageView flagImage;
    private ImageView profilePhotoImage;
    private ImageView liveStatusSignal;
    private TextView nameText;
    private TextView countryText;
    private TextView ageText;
    private TextView genderText;
    private TextView biographyText;
    private Button sendRequestButton;
    private Button removeButton;

    public ExploreAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // load user flag and profile photo into place
        Glide.with(context).load(context.getResources().getIdentifier(Country.COUNTRY_CODES.get(user.getUserOriginCountry()), "drawable", context.getPackageName())).into(flagImage);
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
        genderText.setText(user.getUserGender());
        biographyText.setText(user.getUserBiographyText());

        // send user a friend request and remove them from view
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: fire a friend request, remove user from list, add new user to list
            }
        });

        // remove user from view without friend request
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: remove user from list, add new user to list
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void clear() {
        userList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> newUserList) {
        userList.addAll(newUserList);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View userItemView) {
            super(userItemView);

            flagImage = userItemView.findViewById(R.id.item_user_flag);
            profilePhotoImage = userItemView.findViewById(R.id.item_user_profile_image);
            liveStatusSignal = userItemView.findViewById(R.id.item_user_live_signal_image);
            nameText = userItemView.findViewById(R.id.item_user_name_text);
            countryText = userItemView.findViewById(R.id.item_user_country_text);
            ageText = userItemView.findViewById(R.id.item_user_age_text);
            genderText = userItemView.findViewById(R.id.item_user_gender_text);
            biographyText = userItemView.findViewById(R.id.item_user_biography_text);
            sendRequestButton = userItemView.findViewById(R.id.item_user_send_request_button);
            removeButton = userItemView.findViewById(R.id.item_user_remove_button);
        }
    }
}