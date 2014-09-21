package com.thomasbroadley.lolstatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class Update implements Serializable {
    private HashMap<String, String> messages;
    private Date updated;

    public Update(JSONObject json) throws Exception{
        messages = new HashMap<String, String>();
        messages.put("default", json.getString("content"));

        JSONArray jsonTranslations = json.getJSONArray("translations");
        for (int i = 0; i < jsonTranslations.length(); i++) {
            JSONObject thisTranslation = jsonTranslations.getJSONObject(i);

            String locale = thisTranslation.getString("locale");
            String language = locale.split("_")[0];

            String message = thisTranslation.getString("content");

            messages.put(language, message);
        }

        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        updated = sdf.parse(json.getString("created_at"));
    }

    public String getMessage() {
        return messages.get("default");
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

    public Date getUpdated() {
        return updated;
    }
}
