package com.lingua.lingua;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Async task to handle querying the database for the users' languages. Implemented to ensure that all the languages are present before the object is passed to the intent
 */

public class LanguageQuery extends AsyncTask<String, Void, ArrayList<String>> {
    private Context context;

    public LanguageQuery(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        ArrayList<String> languages = new ArrayList<>();
        int count = strings.length;
        for (int index = 0; index < count; index++) {

        }
        return languages;
    }
}
