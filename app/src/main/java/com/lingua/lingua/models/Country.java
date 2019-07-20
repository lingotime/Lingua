package com.lingua.lingua.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Country {
    private String name;
    private String flagPhotoURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagPhotoURL() {
        return flagPhotoURL;
    }

    public void setFlagPhotoURL(String flagPhotoURL) {
        this.flagPhotoURL = flagPhotoURL;
    }

    public ArrayList<User> getNativeUsers() {
        return nativeUsers;
    }

    public void setNativeUsers(ArrayList<User> nativeUsers) {
        this.nativeUsers = nativeUsers;
    }

    private ArrayList<User> nativeUsers;

    public static final String[] COUNTRIES = new String[] {
            "Canada", "Saint Martin", "Sao Tome and Principe", "Guinea-Bissau", "Iran, Islamic Republic of", "Lithuania", "Saint Pierre and Miquelon", "Saint Helena, Ascension and Tristan da Cunha", "Switzerland", "Ethiopia", "Aruba", "Sri Lanka", "Swaziland", "Svalbard and Jan Mayen Islands", "Palestine", "Argentina", "Cameroon", "Burkina Faso", "Turkmenistan", "Bahrain", "Saudi Arabia", "Togo", "Japan", "Cape Verde", "Cocos (Keeling) Islands", "Faroe Islands", "Guatemala", "Bosnia and Herzegovina", "Kuwait", "Russian Federation", "Germany", "Saint Barthelemy", "Virgin Islands, British", "Spain", "Liberia", "Maldives", "Armenia", "Jamaica", "Oman", "Isle of Man", "Gabon", "Niue", "Monaco", "England", "New Zealand", "Yemen", "Jersey", "Pakistan", "Greenland", "Samoa", "Norfolk Island", "Liechtenstein", "Guam", "Uruguay", "Viet Nam", "Azerbaijan", "Lesotho", "Saint Vincent and the Grenadines", "United Arab Emirates", "Cyprus", "Tajikistan", "Turkey", "Afghanistan", "Bangladesh", "Mauritania", "Solomon Islands", "Turks and Caicos Islands", "Saint Lucia", "San Marino", "French Polynesia", "Wallis and Futuna Islands", "Macedonia, the former Yugoslav Republic of", "Syrian Arab Republic", "Bermuda", "Slovakia", "Somalia", "Peru", "Wales", "Nauru", "Seychelles", "Norway", "Malawi", "Cook Islands", "Benin", "Congo, the Democratic Republic of the", "Libya", "Mexico", "Montenegro", "Saint Kitts and Nevis", "Mayotte", "Holy See (Vatican City State)", "China", "Micronesia, Federated States of", "Antigua and Barbuda", "Dominican Republic", "Ukraine", "Ghana", "Tonga", "Cayman Islands", "Qatar", "Western Sahara", "Finland", "Central African Republic", "Mauritius", "Sweden", "Australia", "Mali", "Cambodia", "American Samoa", "Bulgaria", "United States", "Romania", "Angola", "French Southern Territories", "Portugal", "South Africa", "Tokelau", "Macao", "Christmas Island", "South Georgia and the South Sandwich Islands", "Caribbean Netherlands", "Brunei Darussalam", "Venezuela, Bolivarian Republic of", "Malaysia", "Senegal", "Mozambique", "Uganda", "Hungary", "Niger", "Brazil", "Pitcairn", "Guinea", "Panama", "Korea, Republic of", "Scotland", "Costa Rica", "Luxembourg", "Virgin Islands, U.S.", "Bahamas", "Gibraltar", "Ireland", "Italy", "Nigeria", "Ecuador", "Northern Mariana Islands", "Europe", "Belarus", "Korea, Democratic People's Republic of", "Algeria", "Slovenia", "El Salvador", "Tuvalu", "Czech Republic", "Rwanda", "Chile", "Puerto Rico", "Belgium", "Marshall Islands", "Haiti", "Iraq", "Hong Kong", "Sierra Leone", "Georgia", "Lao People's Democratic Republic", "Gambia", "Philippines", "Morocco", "Albania", "Croatia", "Mongolia", "Guernsey", "Kiribati", "Namibia", "Grenada", "France", "Belize", "Tanzania, United Republic of", "Chad", "Estonia", "Kosovo", "Bouvet Island", "Lebanon", "India", "Uzbekistan", "Tunisia", "Falkland Islands (Malvinas)", "Heard Island and McDonald Islands", "Timor-Leste", "Dominica", "Colombia", "Reunion", "Burundi", "Taiwan", "Fiji", "Barbados", "Madagascar", "Palau", "Curacao", "Bhutan", "Kenya", "Sudan", "Bolivia, Plurinational State of", "Vanuatu", "Singapore", "Malta", "Netherlands", "Suriname", "Anguilla", "Thailand", "Netherlands Antilles", "Aland Islands", "Israel", "Indonesia", "Iceland", "Zambia", "Austria", "Papua New Guinea", "Cote d'Ivoire", "Zimbabwe", "Jordan", "Martinique", "Kazakhstan", "Poland", "Moldova, Republic of", "Djibouti", "Eritrea", "Kyrgyzstan", "Denmark", "Northern Ireland", "British Indian Ocean Territory", "Montserrat", "New Caledonia", "Andorra", "Trinidad and Tobago", "Latvia", "South Sudan", "Guyana", "Guadeloupe", "Nepal", "Honduras", "Myanmar", "Equatorial Guinea", "Egypt", "Nicaragua", "Cuba", "Serbia", "Comoros", "United Kingdom", "Antarctica", "Congo", "Sint Maarten (Dutch part)", "Greece", "Paraguay", "French Guiana", "Botswana", "US Minor Outlying Islands"
    };

    public static final String[] LANGUAGES = new String[] {
            "Acholi", "Afrikaans", "Akan", "Albanian", "Amharic", "Arabic", "Ashante", "Asl", "Assyrian", "Azerbaijani", "Azeri", "Bajuni", "Basque", "Behdini", "Belorussian", "Bengali", "Berber", "Bosnian", "Bravanese", "Bulgarian", "Burmese", "Cakchiquel", "Cambodian", "Cantonese", "Catalan", "Chaldean", "Chamorro", "Chao-chow", "Chavacano", "Chin", "Chuukese", "Cree", "Croatian", "Czech", "Dakota", "Danish", "Dari", "Dinka", "Diula", "Dutch", "Edo", "English", "Estonian", "Ewe", "Fante", "Farsi", "Fijian Hindi", "Finnish", "Flemish", "French", "French Canadian", "Fukienese", "Fula", "Fulani", "Fuzhou", "Ga", "Gaddang", "Gaelic", "Gaelic-irish", "Gaelic-scottish", "Georgian", "German", "Gorani", "Greek", "Gujarati", "Haitian Creole", "Hakka", "Hakka-chinese", "Hausa", "Hebrew", "Hindi", "Hmong", "Hungarian", "Ibanag", "Ibo", "Icelandic", "Igbo", "Ilocano", "Indonesian", "Inuktitut", "Italian", "Jakartanese", "Japanese", "Javanese", "Kanjobal", "Karen", "Karenni", "Kashmiri", "Kazakh", "Kikuyu", "Kinyarwanda", "Kirundi", "Korean", "Kosovan", "Kotokoli", "Krio", "Kurdish", "Kurmanji", "Kyrgyz", "Lakota", "Laotian", "Latvian", "Lingala", "Lithuanian", "Luganda", "Luo", "Maay", "Macedonian", "Malay", "Malayalam", "Maltese", "Mandarin", "Mandingo", "Mandinka", "Marathi", "Marshallese", "Mien", "Mina", "Mirpuri", "Mixteco", "Moldavan", "Mongolian", "Montenegrin", "Navajo", "Neapolitan", "Nepali", "Nigerian Pidgin", "Norwegian", "Oromo", "Pahari", "Papago", "Papiamento", "Pashto", "Patois", "Pidgin English", "Polish", "Portug.creole", "Portuguese", "Pothwari", "Pulaar", "Punjabi", "Putian", "Quichua", "Romanian", "Russian", "Samoan", "Serbian", "Shanghainese", "Shona", "Sichuan", "Sicilian", "Sinhalese", "Slovak", "Somali", "Sorani", "Spanish", "Sudanese Arabic", "Sundanese", "Susu", "Swahili", "Swedish", "Sylhetti", "Tagalog", "Taiwanese", "Tajik", "Tamil", "Telugu", "Thai", "Tibetan", "Tigre", "Tigrinya", "Toishanese", "Tongan", "Toucouleur", "Trique", "Tshiluba", "Turkish", "Twi", "Ukrainian", "Urdu", "Uyghur", "Uzbek", "Vietnamese", "Visayan", "Welsh", "Wolof", "Yiddish", "Yoruba", "Yupik"
    };

    // maps the countries to their codes in order to make it able to locate their flag files
    public static final Map<String, String> COUNTRY_CODES = new HashMap<String, String>() {{
        put("Canada", "ca");
        put("Saint Martin", "mf");
        put("Sao Tome and Principe", "st");
        put("Guinea-Bissau", "gw");
        put("Iran, Islamic Republic of", "ir");
        put("Lithuania", "lt");
        put("Saint Pierre and Miquelon", "pm");
        put("Saint Helena, Ascension and Tristan da Cunha", "sh");
        put("Switzerland", "ch");
        put("Ethiopia", "et");
        put("Aruba", "aw");
        put("Sri Lanka", "lk");
        put("Swaziland", "sz");
        put("Svalbard and Jan Mayen Islands", "sj");
        put("Palestine", "ps");
        put("Argentina", "ar");
        put("Cameroon", "cm");
        put("Burkina Faso", "bf");
        put("Turkmenistan", "tm");
        put("Bahrain", "bh");
        put("Saudi Arabia", "sa");
        put("Togo", "tg");
        put("Japan", "jp");
        put("Cape Verde", "cv");
        put("Cocos (Keeling) Islands", "cc");
        put("Faroe Islands", "fo");
        put("Guatemala", "gt");
        put("Bosnia and Herzegovina", "ba");
        put("Kuwait", "kw");
        put("Russian Federation", "ru");
        put("Germany", "de");
        put("Saint Barthelemy", "bl");
        put("Virgin Islands, British", "vg");
        put("Spain", "es");
        put("Liberia", "lr");
        put("Maldives", "mv");
        put("Armenia", "am");
        put("Jamaica", "jm");
        put("Oman", "om");
        put("Isle of Man", "im");
        put("Gabon", "ga");
        put("Niue", "nu");
        put("Monaco", "mc");
        put("England", "gb_eng");
        put("New Zealand", "nz");
        put("Yemen", "ye");
        put("Jersey", "je");
        put("Pakistan", "pk");
        put("Greenland", "gl");
        put("Samoa", "ws");
        put("Norfolk Island", "nf");
        put("Liechtenstein", "li");
        put("Guam", "gu");
        put("Uruguay", "uy");
        put("Viet Nam", "vn");
        put("Azerbaijan", "az");
        put("Lesotho", "ls");
        put("Saint Vincent and the Grenadines", "vc");
        put("United Arab Emirates", "ae");
        put("Cyprus", "cy");
        put("Tajikistan", "tj");
        put("Turkey", "tr");
        put("Afghanistan", "af");
        put("Bangladesh", "bd");
        put("Mauritania", "mr");
        put("Solomon Islands", "sb");
        put("Turks and Caicos Islands", "tc");
        put("Saint Lucia", "lc");
        put("San Marino", "sm");
        put("French Polynesia", "pf");
        put("Wallis and Futuna Islands", "wf");
        put("Macedonia, the former Yugoslav Republic of", "mk");
        put("Syrian Arab Republic", "sy");
        put("Bermuda", "bm");
        put("Slovakia", "sk");
        put("Somalia", "so");
        put("Peru", "pe");
        put("Wales", "gb_wls");
        put("Nauru", "nr");
        put("Seychelles", "sc");
        put("Norway", "no");
        put("Malawi", "mw");
        put("Cook Islands", "ck");
        put("Benin", "bj");
        put("Congo, the Democratic Republic of the", "cd");
        put("Libya", "ly");
        put("Mexico", "mx");
        put("Montenegro", "me");
        put("Saint Kitts and Nevis", "kn");
        put("Mayotte", "yt");
        put("Holy See (Vatican City State)", "va");
        put("China", "cn");
        put("Micronesia, Federated States of", "fm");
        put("Antigua and Barbuda", "ag");
        put("Dominican Republic", "domr");
        put("Ukraine", "ua");
        put("Ghana", "gh");
        put("Tonga", "to");
        put("Cayman Islands", "ky");
        put("Qatar", "qa");
        put("Western Sahara", "eh");
        put("Finland", "fi");
        put("Central African Republic", "cf");
        put("Mauritius", "mu");
        put("Sweden", "se");
        put("Australia", "au");
        put("Mali", "ml");
        put("Cambodia", "kh");
        put("American Samoa", "as");
        put("Bulgaria", "bg");
        put("United States", "us");
        put("Romania", "ro");
        put("Angola", "ao");
        put("French Southern Territories", "tf");
        put("Portugal", "pt");
        put("South Africa", "za");
        put("Tokelau", "tk");
        put("Macao", "mo");
        put("Christmas Island", "cx");
        put("South Georgia and the South Sandwich Islands", "gs");
        put("Caribbean Netherlands", "bq");
        put("Brunei Darussalam", "bn");
        put("Venezuela, Bolivarian Republic of", "ve");
        put("Malaysia", "my");
        put("Senegal", "sn");
        put("Mozambique", "mz");
        put("Uganda", "ug");
        put("Hungary", "hu");
        put("Niger", "ne");
        put("Brazil", "br");
        put("Pitcairn", "pn");
        put("Guinea", "gn");
        put("Panama", "pa");
        put("Korea, Republic of", "kr");
        put("Scotland", "gb_sct");
        put("Costa Rica", "cr");
        put("Luxembourg", "lu");
        put("Virgin Islands, U.S.", "vi");
        put("Bahamas", "bs");
        put("Gibraltar", "gi");
        put("Ireland", "ie");
        put("Italy", "it");
        put("Nigeria", "ng");
        put("Ecuador", "ec");
        put("Northern Mariana Islands", "mp");
        put("Europe", "eu");
        put("Belarus", "by");
        put("Korea, Democratic People's Republic of", "kp");
        put("Algeria", "dz");
        put("Slovenia", "si");
        put("El Salvador", "sv");
        put("Tuvalu", "tv");
        put("Czech Republic", "cz");
        put("Rwanda", "rw");
        put("Chile", "cl");
        put("Puerto Rico", "pr");
        put("Belgium", "be");
        put("Marshall Islands", "mh");
        put("Haiti", "ht");
        put("Iraq", "iq");
        put("Hong Kong", "hk");
        put("Sierra Leone", "sl");
        put("Georgia", "ge");
        put("Lao People's Democratic Republic", "la");
        put("Gambia", "gm");
        put("Philippines", "ph");
        put("Morocco", "ma");
        put("Albania", "al");
        put("Croatia", "hr");
        put("Mongolia", "mn");
        put("Guernsey", "gg");
        put("Kiribati", "ki");
        put("Namibia", "na");
        put("Grenada", "gd");
        put("France", "fr");
        put("Belize", "bz");
        put("Tanzania, United Republic of", "tz");
        put("Chad", "td");
        put("Estonia", "ee");
        put("Kosovo", "xk");
        put("Bouvet Island", "bv");
        put("Lebanon", "lb");
        put("India", "in");
        put("Uzbekistan", "uz");
        put("Tunisia", "tn");
        put("Falkland Islands (Malvinas)", "fk");
        put("Heard Island and McDonald Islands", "hm");
        put("Timor-Leste", "tl");
        put("Dominica", "dm");
        put("Colombia", "co");
        put("Reunion", "re");
        put("Burundi", "bi");
        put("Taiwan", "tw");
        put("Fiji", "fj");
        put("Barbados", "bb");
        put("Madagascar", "mg");
        put("Palau", "pw");
        put("Curacao", "cw");
        put("Bhutan", "bt");
        put("Kenya", "ke");
        put("Sudan", "sd");
        put("Bolivia, Plurinational State of", "bo");
        put("Vanuatu", "vu");
        put("Singapore", "sg");
        put("Malta", "mt");
        put("Netherlands", "nl");
        put("Suriname", "sr");
        put("Anguilla", "ai");
        put("Thailand", "th");
        put("Netherlands Antilles", "an");
        put("Aland Islands", "ax");
        put("Israel", "il");
        put("Indonesia", "id");
        put("Iceland", "is");
        put("Zambia", "zm");
        put("Austria", "at");
        put("Papua New Guinea", "pg");
        put("Cote d'Ivoire", "ci");
        put("Zimbabwe", "zw");
        put("Jordan", "jo");
        put("Martinique", "mq");
        put("Kazakhstan", "kz");
        put("Poland", "pl");
        put("Moldova, Republic of", "md");
        put("Djibouti", "dj");
        put("Eritrea", "er");
        put("Kyrgyzstan", "kg");
        put("Denmark", "dk");
        put("Northern Ireland", "gb_nir");
        put("British Indian Ocean Territory", "io");
        put("Montserrat", "ms");
        put("New Caledonia", "nc");
        put("Andorra", "ad");
        put("Trinidad and Tobago", "tt");
        put("Latvia", "lv");
        put("South Sudan", "ss");
        put("Guyana", "gy");
        put("Guadeloupe", "gp");
        put("Nepal", "np");
        put("Honduras", "hn");
        put("Myanmar", "mm");
        put("Equatorial Guinea", "gq");
        put("Egypt", "eg");
        put("Nicaragua", "ni");
        put("Cuba", "cu");
        put("Serbia", "rs");
        put("Comoros", "km");
        put("United Kingdom", "gb");
        put("Antarctica", "aq");
        put("Congo", "cg");
        put("Sint Maarten (Dutch part)", "sx");
        put("Greece", "gr");
        put("Paraguay", "py");
        put("French Guiana", "gf");
        put("Botswana", "bw");
        put("US Minor Outlying Islands", "um");
    }};
}
