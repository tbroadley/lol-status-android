package com.thomasbroadley.lolstatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Incident implements Serializable {
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
