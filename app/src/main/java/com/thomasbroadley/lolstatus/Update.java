package com.thomasbroadley.lolstatus;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
* Created by Thomas on 2014-09-13.
*/
public class Update {
    private String message;
    private Date updated;

    public Update(JSONObject json) throws Exception{
        message = json.getString("content");
        updated = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'").parse(json.getString("updated_at"));
    }

    public String getMessage() {
        return message;
    }

    public Date getUpdated() {
        return updated;
    }
}