package com.lingua.lingua.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Country {
    private String countryID;
    private String countryName;
    private String countryFlagPhotoURL;
    private ArrayList<String> usersWithKnownSelected;
    private ArrayList<String> usersWithExploreSelected;

    public Country() {
        countryID = generateRandomID();
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryFlagPhotoURL() {
        return countryFlagPhotoURL;
    }

    public void setCountryFlagPhotoURL(String countryFlagPhotoURL) {
        this.countryFlagPhotoURL = countryFlagPhotoURL;
    }

    public ArrayList<String> getUsersWithKnownSelected() {
        return usersWithKnownSelected;
    }

    public void setUsersWithKnownSelected(ArrayList<String> usersWithKnownSelected) {
        this.usersWithKnownSelected = usersWithKnownSelected;
    }

    public ArrayList<String> getUsersWithExploreSelected() {
        return usersWithExploreSelected;
    }

    public void setUsersWithExploreSelected(ArrayList<String> usersWithExploreSelected) {
        this.usersWithExploreSelected = usersWithExploreSelected;
    }

    private String generateRandomID() {
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int numberOfCharacters = 15;

        StringBuilder stringBuilder = new StringBuilder();

        while (numberOfCharacters != 0) {
            int character = (int) (Math.random() * allowedCharacters.length());

            stringBuilder.append(allowedCharacters.charAt(character));

            numberOfCharacters--;
        }

        return stringBuilder.toString();
    }

    public static final String[] COUNTRIES = new String[] {
            "Afghanistan",
            "Aland Islands",
            "Albania",
            "Algeria",
            "American Samoa",
            "Andorra",
            "Angola",
            "Anguilla",
            "Antarctica",
            "Antigua and Barbuda",
            "Argentina",
            "Armenia",
            "Aruba",
            "Australia",
            "Austria",
            "Azerbaijan",
            "Bahamas",
            "Bahrain",
            "Bangladesh",
            "Barbados",
            "Belarus",
            "Belgium",
            "Belize",
            "Benin",
            "Bermuda",
            "Bhutan",
            "Bolivia, Plurinational State of",
            "Bosnia and Herzegovina",
            "Botswana",
            "Bouvet Island",
            "Brazil",
            "British Indian Ocean Territory",
            "Brunei Darussalam",
            "Bulgaria",
            "Burkina Faso",
            "Burundi",
            "Cambodia",
            "Cameroon",
            "Canada",
            "Cape Verde",
            "Caribbean Netherlands",
            "Cayman Islands",
            "Central African Republic",
            "Chad",
            "Chile",
            "China",
            "Christmas Island",
            "Cocos (Keeling) Islands",
            "Colombia",
            "Comoros",
            "Congo",
            "Congo, the Democratic Republic of the",
            "Cook Islands",
            "Costa Rica",
            "Cote d'Ivoire",
            "Croatia",
            "Cuba",
            "Curacao",
            "Cyprus",
            "Czech Republic",
            "Denmark",
            "Djibouti",
            "Dominica",
            "Dominican Republic",
            "Ecuador",
            "Egypt",
            "El Salvador",
            "England",
            "Equatorial Guinea",
            "Eritrea",
            "Estonia",
            "Ethiopia",
            "Europe",
            "Falkland Islands (Malvinas)",
            "Faroe Islands",
            "Fiji",
            "Finland",
            "France",
            "French Guiana",
            "French Polynesia",
            "French Southern Territories",
            "Gabon",
            "Gambia",
            "Georgia",
            "Germany",
            "Ghana",
            "Gibraltar",
            "Greece",
            "Greenland",
            "Grenada",
            "Guadeloupe",
            "Guam",
            "Guatemala",
            "Guernsey",
            "Guinea",
            "Guinea-Bissau",
            "Guyana",
            "Haiti",
            "Heard Island and McDonald Islands",
            "Holy See (Vatican City State)",
            "Honduras",
            "Hong Kong",
            "Hungary",
            "Iceland",
            "India",
            "Indonesia",
            "Iran, Islamic Republic of",
            "Iraq",
            "Ireland",
            "Isle of Man",
            "Israel",
            "Italy",
            "Jamaica",
            "Japan",
            "Jersey",
            "Jordan",
            "Kazakhstan",
            "Kenya",
            "Kiribati",
            "Korea, Democratic People's Republic of",
            "Korea, Republic of",
            "Kosovo",
            "Kuwait",
            "Kyrgyzstan",
            "Lao People's Democratic Republic",
            "Latvia",
            "Lebanon",
            "Lesotho",
            "Liberia",
            "Libya",
            "Liechtenstein",
            "Lithuania",
            "Luxembourg",
            "Macao",
            "Macedonia, the former Yugoslav Republic of",
            "Madagascar",
            "Malawi",
            "Malaysia",
            "Maldives",
            "Mali",
            "Malta",
            "Marshall Islands",
            "Martinique",
            "Mauritania",
            "Mauritius",
            "Mayotte",
            "Mexico",
            "Micronesia, Federated States of",
            "Moldova, Republic of",
            "Monaco",
            "Mongolia",
            "Montenegro",
            "Montserrat",
            "Morocco",
            "Mozambique",
            "Myanmar",
            "Namibia",
            "Nauru",
            "Nepal",
            "Netherlands Antilles",
            "Netherlands",
            "New Caledonia",
            "New Zealand",
            "Nicaragua",
            "Niger",
            "Nigeria",
            "Niue",
            "Norfolk Island",
            "Northern Ireland",
            "Northern Mariana Islands",
            "Norway",
            "Oman",
            "Pakistan",
            "Palau",
            "Palestine",
            "Panama",
            "Papua New Guinea",
            "Paraguay",
            "Peru",
            "Philippines",
            "Pitcairn",
            "Poland",
            "Portugal",
            "Puerto Rico",
            "Qatar",
            "Reunion",
            "Romania",
            "Russian Federation",
            "Rwanda",
            "Saint Barthelemy",
            "Saint Helena Ascension and Tristan da Cunha",
            "Saint Kitts and Nevis",
            "Saint Lucia",
            "Saint Martin",
            "Saint Pierre and Miquelon",
            "Saint Vincent and the Grenadines",
            "Samoa",
            "San Marino",
            "Sao Tome and Principe",
            "Saudi Arabia",
            "Scotland",
            "Senegal",
            "Serbia",
            "Seychelles",
            "Sierra Leone",
            "Singapore",
            "Sint Maarten (Dutch part)",
            "Slovakia",
            "Slovenia",
            "Solomon Islands",
            "Somalia",
            "South Africa",
            "South Georgia and the South Sandwich Islands",
            "South Sudan",
            "Spain",
            "Sri Lanka",
            "Sudan",
            "Suriname",
            "Svalbard and Jan Mayen Islands",
            "Swaziland",
            "Sweden",
            "Switzerland",
            "Syrian Arab Republic",
            "Taiwan",
            "Tajikistan",
            "Tanzania, United Republic of",
            "Thailand",
            "Timor-Leste",
            "Togo",
            "Tokelau",
            "Tonga",
            "Trinidad and Tobago",
            "Tunisia",
            "Turkey",
            "Turkmenistan",
            "Turks and Caicos Islands",
            "Tuvalu",
            "Uganda",
            "Ukraine",
            "United Arab Emirates",
            "United Kingdom",
            "United States",
            "Uruguay",
            "US Minor Outlying Islands",
            "Uzbekistan",
            "Vanuatu",
            "Venezuela, Bolivarian Republic of",
            "Viet Nam",
            "Virgin Islands, British",
            "Virgin Islands, U.S.",
            "Wales",
            "Wallis and Futuna Islands",
            "Western Sahara",
            "Yemen",
            "Zambia",
            "Zimbabwe"
    };

    public static final Map<String, String> COUNTRY_CODES = new HashMap<String, String>() {{
        put("Afghanistan", "af");
        put("Aland Islands", "ax");
        put("Albania", "al");
        put("Algeria", "dz");
        put("American Samoa", "as");
        put("Andorra", "ad");
        put("Angola", "ao");
        put("Anguilla", "ai");
        put("Antarctica", "aq");
        put("Antigua and Barbuda", "ag");
        put("Argentina", "ar");
        put("Armenia", "am");
        put("Aruba", "aw");
        put("Australia", "au");
        put("Austria", "at");
        put("Azerbaijan", "az");
        put("Bahamas", "bs");
        put("Bahrain", "bh");
        put("Bangladesh", "bd");
        put("Barbados", "bb");
        put("Belarus", "by");
        put("Belgium", "be");
        put("Belize", "bz");
        put("Benin", "bj");
        put("Bermuda", "bm");
        put("Bhutan", "bt");
        put("Bolivia, Plurinational State of", "bo");
        put("Bosnia and Herzegovina", "ba");
        put("Botswana", "bw");
        put("Bouvet Island", "bv");
        put("Brazil", "br");
        put("British Indian Ocean Territory", "io");
        put("Brunei Darussalam", "bn");
        put("Bulgaria", "bg");
        put("Burkina Faso", "bf");
        put("Burundi", "bi");
        put("Cambodia", "kh");
        put("Cameroon", "cm");
        put("Canada", "ca");
        put("Cape Verde", "cv");
        put("Caribbean Netherlands", "bq");
        put("Cayman Islands", "ky");
        put("Central African Republic", "cf");
        put("Chad", "td");
        put("Chile", "cl");
        put("China", "cn");
        put("Christmas Island", "cx");
        put("Cocos (Keeling) Islands", "cc");
        put("Colombia", "co");
        put("Comoros", "km");
        put("Congo", "cg");
        put("Congo, the Democratic Republic of the", "cd");
        put("Cook Islands", "ck");
        put("Costa Rica", "cr");
        put("Cote d'Ivoire", "ci");
        put("Croatia", "hr");
        put("Cuba", "cu");
        put("Curacao", "cw");
        put("Cyprus", "cy");
        put("Czech Republic", "cz");
        put("Denmark", "dk");
        put("Djibouti", "dj");
        put("Dominica", "dm");
        put("Dominican Republic", "domr");
        put("Ecuador", "ec");
        put("Egypt", "eg");
        put("El Salvador", "sv");
        put("England", "gb_eng");
        put("Equatorial Guinea", "gq");
        put("Eritrea", "er");
        put("Estonia", "ee");
        put("Ethiopia", "et");
        put("Europe", "eu");
        put("Falkland Islands (Malvinas)", "fk");
        put("Faroe Islands", "fo");
        put("Fiji", "fj");
        put("Finland", "fi");
        put("France", "fr");
        put("French Guiana", "gf");
        put("French Polynesia", "pf");
        put("French Southern Territories", "tf");
        put("Gabon", "ga");
        put("Gambia", "gm");
        put("Georgia", "ge");
        put("Germany", "de");
        put("Ghana", "gh");
        put("Gibraltar", "gi");
        put("Greece", "gr");
        put("Greenland", "gl");
        put("Grenada", "gd");
        put("Guadeloupe", "gp");
        put("Guam", "gu");
        put("Guatemala", "gt");
        put("Guernsey", "gg");
        put("Guinea", "gn");
        put("Guinea-Bissau", "gw");
        put("Guyana", "gy");
        put("Haiti", "ht");
        put("Heard Island and McDonald Islands", "hm");
        put("Holy See (Vatican City State)", "va");
        put("Honduras", "hn");
        put("Hong Kong", "hk");
        put("Hungary", "hu");
        put("Iceland", "is");
        put("India", "in");
        put("Indonesia", "id");
        put("Iran, Islamic Republic of", "ir");
        put("Iraq", "iq");
        put("Ireland", "ie");
        put("Isle of Man", "im");
        put("Israel", "il");
        put("Italy", "it");
        put("Jamaica", "jm");
        put("Japan", "jp");
        put("Jersey", "je");
        put("Jordan", "jo");
        put("Kazakhstan", "kz");
        put("Kenya", "ke");
        put("Kiribati", "ki");
        put("Korea, Democratic People's Republic of", "kp");
        put("Korea, Republic of", "kr");
        put("Kosovo", "xk");
        put("Kuwait", "kw");
        put("Kyrgyzstan", "kg");
        put("Lao People's Democratic Republic", "la");
        put("Latvia", "lv");
        put("Lebanon", "lb");
        put("Lesotho", "ls");
        put("Liberia", "lr");
        put("Libya", "ly");
        put("Liechtenstein", "li");
        put("Lithuania", "lt");
        put("Luxembourg", "lu");
        put("Macao", "mo");
        put("Macedonia, the former Yugoslav Republic of", "mk");
        put("Madagascar", "mg");
        put("Malawi", "mw");
        put("Malaysia", "my");
        put("Maldives", "mv");
        put("Mali", "ml");
        put("Malta", "mt");
        put("Marshall Islands", "mh");
        put("Martinique", "mq");
        put("Mauritania", "mr");
        put("Mauritius", "mu");
        put("Mayotte", "yt");
        put("Mexico", "mx");
        put("Micronesia, Federated States of", "fm");
        put("Moldova, Republic of", "md");
        put("Monaco", "mc");
        put("Mongolia", "mn");
        put("Montenegro", "me");
        put("Montserrat", "ms");
        put("Morocco", "ma");
        put("Mozambique", "mz");
        put("Myanmar", "mm");
        put("Namibia", "na");
        put("Nauru", "nr");
        put("Nepal", "np");
        put("Netherlands Antilles", "an");
        put("Netherlands", "nl");
        put("New Caledonia", "nc");
        put("New Zealand", "nz");
        put("Nicaragua", "ni");
        put("Niger", "ne");
        put("Nigeria", "ng");
        put("Niue", "nu");
        put("Norfolk Island", "nf");
        put("Northern Ireland", "gb_nir");
        put("Northern Mariana Islands", "mp");
        put("Norway", "no");
        put("Oman", "om");
        put("Pakistan", "pk");
        put("Palau", "pw");
        put("Palestine", "ps");
        put("Panama", "pa");
        put("Papua New Guinea", "pg");
        put("Paraguay", "py");
        put("Peru", "pe");
        put("Philippines", "ph");
        put("Pitcairn", "pn");
        put("Poland", "pl");
        put("Portugal", "pt");
        put("Puerto Rico", "pr");
        put("Qatar", "qa");
        put("Reunion", "re");
        put("Romania", "ro");
        put("Russian Federation", "ru");
        put("Rwanda", "rw");
        put("Saint Barthelemy", "bl");
        put("Saint Helena, Ascension and Tristan da Cunha", "sh");
        put("Saint Kitts and Nevis", "kn");
        put("Saint Lucia", "lc");
        put("Saint Martin", "mf");
        put("Saint Pierre and Miquelon", "pm");
        put("Saint Vincent and the Grenadines", "vc");
        put("Samoa", "ws");
        put("San Marino", "sm");
        put("Sao Tome and Principe", "st");
        put("Saudi Arabia", "sa");
        put("Scotland", "gb_sct");
        put("Senegal", "sn");
        put("Serbia", "rs");
        put("Seychelles", "sc");
        put("Sierra Leone", "sl");
        put("Singapore", "sg");
        put("Sint Maarten (Dutch part)", "sx");
        put("Slovakia", "sk");
        put("Slovenia", "si");
        put("Solomon Islands", "sb");
        put("Somalia", "so");
        put("South Africa", "za");
        put("South Georgia and the South Sandwich Islands", "gs");
        put("South Sudan", "ss");
        put("Spain", "es");
        put("Sri Lanka", "lk");
        put("Sudan", "sd");
        put("Suriname", "sr");
        put("Svalbard and Jan Mayen Islands", "sj");
        put("Swaziland", "sz");
        put("Sweden", "se");
        put("Switzerland", "ch");
        put("Syrian Arab Republic", "sy");
        put("Taiwan", "tw");
        put("Tajikistan", "tj");
        put("Tanzania, United Republic of", "tz");
        put("Thailand", "th");
        put("Timor-Leste", "tl");
        put("Togo", "tg");
        put("Tokelau", "tk");
        put("Tonga", "to");
        put("Trinidad and Tobago", "tt");
        put("Tunisia", "tn");
        put("Turkey", "tr");
        put("Turkmenistan", "tm");
        put("Turks and Caicos Islands", "tc");
        put("Tuvalu", "tv");
        put("Uganda", "ug");
        put("Ukraine", "ua");
        put("United Arab Emirates", "ae");
        put("United Kingdom", "gb");
        put("United States", "us");
        put("Uruguay", "uy");
        put("US Minor Outlying Islands", "um");
        put("Uzbekistan", "uz");
        put("Vanuatu", "vu");
        put("Venezuela, Bolivarian Republic of", "ve");
        put("Viet Nam", "vn");
        put("Virgin Islands, British", "vg");
        put("Virgin Islands, U.S.", "vi");
        put("Wales", "gb_wls");
        put("Wallis and Futuna Islands", "wf");
        put("Western Sahara", "eh");
        put("Yemen", "ye");
        put("Zambia", "zm");
        put("Zimbabwe", "zw");
    }};
}