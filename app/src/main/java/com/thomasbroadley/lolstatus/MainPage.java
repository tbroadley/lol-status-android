package com.thomasbroadley.lolstatus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class MainPage extends ActionBarActivity {

    final String APIKEY = "?api_key=6aa5a261-184f-42b9-8a43-92ef62505597";
    final String URL = "http://status.leagueoflegends.com/shards/";

    final String[] SERVER = new String[] {"br", "eune", "euw", "lan", "las", "na", "oce", "ru", "tr"};
    final int[] PREFERRED_SERVER = new int[] {2, 5};

    ArrayList<ServerStatus> server;
    ArrayList<Boolean> useServer;

    ArrayList<String> displayedServer;

    ExpandableListView elv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        try {
            readStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readStatus() throws Exception {
        server = new ArrayList<ServerStatus>();

        String filename = "servers_to_display";

        ObjectInputStream ois;

        if (getFileStreamPath(filename).exists()) {
            ois = new ObjectInputStream(openFileInput(filename));
            useServer = (ArrayList<Boolean>) ois.readObject();
        } else {
            useServer = new ArrayList<Boolean>();

            for (int i = 0; i < SERVER.length; i++) {
                useServer.add(false);
            }

            for (int i = 0; i < PREFERRED_SERVER.length; i++) {
                useServer.set(PREFERRED_SERVER[i], true);
            }

            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(filename, MODE_PRIVATE));
            oos.writeObject(useServer);

        }

        updateDisplayedServers();
        updateServerStatus();

        elv = (ExpandableListView)findViewById(R.id.expandableListView);
        elv.setAdapter(new ServerStatusAdapter(this, server, elv));

        SharedPreferences prefs = getSharedPreferences("LOL_STATUS_PREFS", MODE_PRIVATE);
        String expanded = prefs.getString("expanded", "");

        if (displayedServer.contains(expanded)) {
            ServerStatusAdapter ssa = ((ServerStatusAdapter)elv.getExpandableListAdapter());
            ArrayList<ServerStatus> statuses = ssa.getGroups();

            ServerStatus expandedServerStatus = new ServerStatus();

            for (ServerStatus ss : statuses) {
                if (ss.getShortName().equals(expanded)) {
                    expandedServerStatus = ss;
                }
            }

            int expandedIndex = statuses.indexOf(expandedServerStatus);

            elv.expandGroup(expandedIndex);
            ((ServerStatusAdapter)elv.getExpandableListAdapter()).setLastExpandedGroupPosition(expandedIndex);
        } else {
            ((ServerStatusAdapter)elv.getExpandableListAdapter()).setLastExpandedGroupPosition(-1);
        }
    }

    private void updateDisplayedServers() {
        displayedServer = new ArrayList<String>();

        for (int i = 0; i < SERVER.length; i++) {
            if (useServer.get(i)) {
                displayedServer.add(SERVER[i]);
            }
        }
    }

    private void updateServerStatus() throws Exception {
        server = new ArrayList<ServerStatus>();

        for (int i = 0; i < displayedServer.size(); i++) {
            String url = URL + displayedServer.get(i) + APIKEY;

            JSONReader json = new JSONReader();
            JSONObject jsonobj = json.read(url);
            server.add(new ServerStatus(jsonobj));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SharedPreferences prefs = getSharedPreferences("LOL_STATUS_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            int expandedIndex = ((ServerStatusAdapter) elv.getExpandableListAdapter()).getLastExpandedGroupPosition();

            if (expandedIndex != -1) {
                String expanded = displayedServer.get(expandedIndex);
                editor.putString("expanded", expanded);
            } else {
                editor.putString("expanded", "");
            }

            editor.apply();

            Intent i = new Intent(this, Settings.class);
            startActivity(i);
        } else if (id == R.id.action_refresh) {
            try {
                updateServerStatus();
                ((BaseExpandableListAdapter)elv.getExpandableListAdapter()).notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
