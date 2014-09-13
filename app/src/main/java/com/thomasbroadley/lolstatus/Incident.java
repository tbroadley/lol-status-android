package com.thomasbroadley.lolstatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* Created by Thomas on 2014-09-13.
*/
public class Incident {
    private ArrayList<Update> update;

    public Incident(JSONObject json) throws Exception {
        update = new ArrayList<Update>();
        JSONArray jsonUpdate = json.getJSONArray("updates");

        for (int i = 0; i < jsonUpdate.length(); i++) {
            update.add(new Update(jsonUpdate.getJSONObject(i)));
        }
    }

    public Update getUpdate(int i) {
        return update.get(i);
    }

    public ArrayList<Update> getUpdates() {
        return update;
    }
}
