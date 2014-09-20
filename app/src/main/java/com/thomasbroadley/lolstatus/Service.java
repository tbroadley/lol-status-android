package com.thomasbroadley.lolstatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Service implements Serializable {
    private String name;
    private String status;
    private ArrayList<Incident> incident;

    public Service(JSONObject json) throws Exception {
        name = json.getString("name");
        status = json.getString("status");
        incident = new ArrayList<Incident>();

        JSONArray jsonIncident = json.getJSONArray("incidents");

        for (int i = 0; i < jsonIncident.length(); i++) {
            incident.add(new Incident(jsonIncident.getJSONObject(i)));
        }
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Incident getIncident(int i) {
        return incident.get(i);
    }

    public ArrayList<Incident> getIncidents() {
        return incident;
    }

}
