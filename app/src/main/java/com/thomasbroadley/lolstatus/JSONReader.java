package com.thomasbroadley.lolstatus;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONReader {
    public JSONReader() {}

    public JSONObject read(String url) throws Exception {
        String json = new JSONTask().execute(url).get();

        return new JSONObject(json);
    }

    private class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String json = "";

            try {
                URL site = new URL(url[0]);
                HttpURLConnection connection = (HttpURLConnection)site.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                json = IOUtils.toString(in, "UTF-8");
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return json;
        }
    }
}


