package com.thomasbroadley.lolstatus;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JSONReader {
    String json;

    public JSONReader() {}

    public JSONObject read(String url) throws Exception {
        String json = new JSONTask().execute(url).get();

        return new JSONObject(json);
    }

    private class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            StringBuilder json = new StringBuilder();
            InputStream is;

            try {
                is = new URL(url[0]).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                json = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    json.append(line);
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return json.toString();
        }
    }
}


