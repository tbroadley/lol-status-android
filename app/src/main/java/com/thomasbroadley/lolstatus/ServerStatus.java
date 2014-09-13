package com.thomasbroadley.lolstatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
* Created by Thomas on 2014-09-13.
*/
public class ServerStatus {
    private String name;
    private String shortname;
    private ArrayList<Service> service;

    public ServerStatus() {}

    public ServerStatus(JSONObject json) throws Exception {
        name = json.getString("name");
        shortname = json.getString("slug");
        service = new ArrayList<Service>();

        JSONArray jsonService = json.getJSONArray("services");

        for (int i = 0; i < jsonService.length(); i++) {
            service.add(new Service(jsonService.getJSONObject(i)));
        }
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortname;
    }

    public Service getService(int i) {
        return service.get(i);
    }

    public ArrayList<Service> getServices() {
        return service;
    }
}
