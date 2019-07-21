package com.lingua.lingua.models;

import java.util.ArrayList;

public class Language {
    private String languageID;
    private String languageName;
    private ArrayList<String> usersWithKnownSelected;
    private ArrayList<String> usersWithExploreSelected;

    public Language() {
        languageID = generateRandomID();
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
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

    public static final String[] LANGUAGES = new String[] {
            "Acholi",
            "Afrikaans",
            "Akan",
            "Albanian",
            "Amharic",
            "Arabic",
            "Ashante",
            "Asl",
            "Assyrian",
            "Azerbaijani",
            "Azeri",
            "Bajuni",
            "Basque",
            "Behdini",
            "Belorussian",
            "Bengali",
            "Berber",
            "Bosnian",
            "Bravanese",
            "Bulgarian",
            "Burmese",
            "Cakchiquel",
            "Cambodian",
            "Cantonese",
            "Catalan",
            "Chaldean",
            "Chamorro",
            "Chao-chow",
            "Chavacano",
            "Chin",
            "Chuukese",
            "Cree",
            "Croatian",
            "Czech",
            "Dakota",
            "Danish",
            "Dari",
            "Dinka",
            "Diula",
            "Dutch",
            "Edo",
            "English",
            "Estonian",
            "Ewe",
            "Fante",
            "Farsi",
            "Fijian Hindi",
            "Finnish",
            "Flemish",
            "French Canadian",
            "French",
            "Fukienese",
            "Fula",
            "Fulani",
            "Fuzhou",
            "Ga",
            "Gaddang",
            "Gaelic",
            "Gaelic-irish",
            "Gaelic-scottish",
            "Georgian",
            "German",
            "Gorani",
            "Greek",
            "Gujarati",
            "Haitian Creole",
            "Hakka",
            "Hakka-chinese",
            "Hausa",
            "Hebrew",
            "Hindi",
            "Hmong",
            "Hungarian",
            "Ibanag",
            "Ibo",
            "Icelandic",
            "Igbo",
            "Ilocano",
            "Indonesian",
            "Inuktitut",
            "Italian",
            "Jakartanese",
            "Japanese",
            "Javanese",
            "Kanjobal",
            "Karen",
            "Karenni",
            "Kashmiri",
            "Kazakh",
            "Kikuyu",
            "Kinyarwanda",
            "Kirundi",
            "Korean",
            "Kosovan",
            "Kotokoli",
            "Krio",
            "Kurdish",
            "Kurmanji",
            "Kyrgyz",
            "Lakota",
            "Laotian",
            "Latvian",
            "Lingala",
            "Lithuanian",
            "Luganda",
            "Luo",
            "Maay",
            "Macedonian",
            "Malay",
            "Malayalam",
            "Maltese",
            "Mandarin",
            "Mandingo",
            "Mandinka",
            "Marathi",
            "Marshallese",
            "Mien",
            "Mina",
            "Mirpuri",
            "Mixteco",
            "Moldavan",
            "Mongolian",
            "Montenegrin",
            "Navajo",
            "Neapolitan",
            "Nepali",
            "Nigerian Pidgin",
            "Norwegian",
            "Oromo",
            "Pahari",
            "Papago",
            "Papiamento",
            "Pashto",
            "Patois",
            "Pidgin English",
            "Polish",
            "Portug.creole",
            "Portuguese",
            "Pothwari",
            "Pulaar",
            "Punjabi",
            "Putian",
            "Quichua",
            "Romanian",
            "Russian",
            "Samoan",
            "Serbian",
            "Shanghainese",
            "Shona",
            "Sichuan",
            "Sicilian",
            "Sinhalese",
            "Slovak",
            "Somali",
            "Sorani",
            "Spanish",
            "Sudanese Arabic",
            "Sundanese",
            "Susu",
            "Swahili",
            "Swedish",
            "Sylhetti",
            "Tagalog",
            "Taiwanese",
            "Tajik",
            "Tamil",
            "Telugu",
            "Thai",
            "Tibetan",
            "Tigre",
            "Tigrinya",
            "Toishanese",
            "Tongan",
            "Toucouleur",
            "Trique",
            "Tshiluba",
            "Turkish",
            "Twi",
            "Ukrainian",
            "Urdu",
            "Uyghur",
            "Uzbek",
            "Vietnamese",
            "Visayan",
            "Welsh",
            "Wolof",
            "Yiddish",
            "Yoruba",
            "Yupik"
    };
}