package com.thomasbroadley.lolstatus;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Update implements Serializable {
    private String message;
    private Date updated;

    public Update(JSONObject json) throws Exception{
        message = json.getString("content");

        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        updated = sdf.parse(json.getString("created_at"));
    }

    public String getMessage() {
        return message;
    }

    public Date getUpdated() {
        return updated;
    }
}
