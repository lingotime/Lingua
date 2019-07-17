package com.lingua.lingua;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.lingua.lingua.models.LocalUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProfileCreationActivity extends AppCompatActivity {


    private ImageView profilePicture;
    private TextView fullName;
    private TextView username;
    private AutoCompleteTextView originCountry;
    private MultiAutoCompleteTextView currentLanguages;
    private MultiAutoCompleteTextView targetLanguages;
    private MultiAutoCompleteTextView targetCountries;
    private Button btnSubmit;
    private EditText bio;
    private LocalUser user;
    private ArrayList<String> languages;
    private ArrayList<String> targetL;
    private ArrayList<String> targetC;
    private ArrayAdapter<String> adapter;

    private static final String[] COUNTRIES = new String[] {
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
};

    private static final String[] LANGUAGES = new String[] {
            "Acholi", "Afrikaans", "Akan", "Albanian", "Amharic", "Arabic", "Ashante", "Asl", "Assyrian", "Azerbaijani", "Azeri", "Bajuni", "Basque", "Behdini", "Belorussian", "Bengali", "Berber", "Bosnian", "Bravanese", "Bulgarian", "Burmese", "Cakchiquel", "Cambodian", "Cantonese", "Catalan", "Chaldean", "Chamorro", "Chao-chow", "Chavacano", "Chin", "Chuukese", "Cree", "Croatian", "Czech", "Dakota", "Danish", "Dari", "Dinka", "Diula", "Dutch", "Edo", "English", "Estonian", "Ewe", "Fante", "Farsi", "Fijian Hindi", "Finnish", "Flemish", "French", "French Canadian", "Fukienese", "Fula", "Fulani", "Fuzhou", "Ga", "Gaddang", "Gaelic", "Gaelic-irish", "Gaelic-scottish", "Georgian", "German", "Gorani", "Greek", "Gujarati", "Haitian Creole", "Hakka", "Hakka-chinese", "Hausa", "Hebrew", "Hindi", "Hmong", "Hungarian", "Ibanag", "Ibo", "Icelandic", "Igbo", "Ilocano", "Indonesian", "Inuktitut", "Italian", "Jakartanese", "Japanese", "Javanese", "Kanjobal", "Karen", "Karenni", "Kashmiri", "Kazakh", "Kikuyu", "Kinyarwanda", "Kirundi", "Korean", "Kosovan", "Kotokoli", "Krio", "Kurdish", "Kurmanji", "Kyrgyz", "Lakota", "Laotian", "Latvian", "Lingala", "Lithuanian", "Luganda", "Luo", "Maay", "Macedonian", "Malay", "Malayalam", "Maltese", "Mandarin", "Mandingo", "Mandinka", "Marathi", "Marshallese", "Mien", "Mina", "Mirpuri", "Mixteco", "Moldavan", "Mongolian", "Montenegrin", "Navajo", "Neapolitan", "Nepali", "Nigerian Pidgin", "Norwegian", "Oromo", "Pahari", "Papago", "Papiamento", "Pashto", "Patois", "Pidgin English", "Polish", "Portug.creole", "Portuguese", "Pothwari", "Pulaar", "Punjabi", "Putian", "Quichua", "Romanian", "Russian", "Samoan", "Serbian", "Shanghainese", "Shona", "Sichuan", "Sicilian", "Sinhalese", "Slovak", "Somali", "Sorani", "Spanish", "Sudanese Arabic", "Sundanese", "Susu", "Swahili", "Swedish", "Sylhetti", "Tagalog", "Taiwanese", "Tajik", "Tamil", "Telugu", "Thai", "Tibetan", "Tigre", "Tigrinya", "Toishanese", "Tongan", "Toucouleur", "Trique", "Tshiluba", "Turkish", "Twi", "Ukrainian", "Urdu", "Uyghur", "Uzbek", "Vietnamese", "Visayan", "Welsh", "Wolof", "Yiddish", "Yoruba", "Yupik"
    };



    // target languages will be an EditText and then separate them to form an ArrayList
    // their bio
    // target countries will be the same as target languages


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        user = new LocalUser();
        //TODO: add a button to allow for the change in the profile image
        profilePicture = (ImageView) findViewById(R.id.activity_profile_creation_profileImage);
        // TODO: get the full name, username, and maybe profile picture from the sign up or login through Google or Facebook
        fullName = (TextView) findViewById(R.id.activity_profile_creation_fullName);
        username = (TextView) findViewById(R.id.activity_profile_creation_username);
        originCountry = (AutoCompleteTextView) findViewById(R.id.activity_profile_creation_originCountry);
        // TODO: split the strings by commas and place them into an array list of the same fields in the user
        currentLanguages = (MultiAutoCompleteTextView) findViewById(R.id.activity_profile_creation_currentLanguages);
        targetLanguages = (MultiAutoCompleteTextView) findViewById(R.id.activity_profile_creation_targetLanguages);
        targetCountries = (MultiAutoCompleteTextView) findViewById(R.id.activity_profile_creation_targetCountries);
        bio = (EditText) findViewById(R.id.activity_profile_creation_bio);
        btnSubmit = (Button) findViewById(R.id.activity_profile_creation_submit);

        // TODO: create adapters with a list of the possibilities for autocompletion and set it upon creation

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        targetCountries.setAdapter(adapter);
        targetCountries.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, LANGUAGES);
        currentLanguages.setAdapter(adapter);
        currentLanguages.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, LANGUAGES);
        targetLanguages.setAdapter(adapter);
        targetLanguages.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());



        // set the User fields upon submission
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setOriginCountry(originCountry.getText().toString());
                String[] current = currentLanguages.getText().toString().split(",");

                // creating the array list of primary languages
                for (int index=0; index < current.length; index++) {
                    languages.add(current[index].trim());
                }

                user.setCurrentLanguages(languages);

                // creating the array list of target languages
                String[] targetl = targetLanguages.getText().toString().split(",");
                for (int index=0; index < targetl.length; index++) {
                    targetL.add(targetl[index].trim());
                }

                user.setTargetLanguages(targetL);

                // creating the array list of target countries
                String[] targetc = targetCountries.getText().toString().split(",");
                for (int index=0; index < targetc.length; index++) {
                    targetC.add(targetl[index].trim());
                }

                user.setTargetCountries(targetC);

                user.setBio(bio.getText().toString());
            }
        });
    }
}
