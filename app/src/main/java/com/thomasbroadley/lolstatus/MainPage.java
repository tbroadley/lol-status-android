package com.thomasbroadley.lolstatus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class MainPage extends ActionBarActivity {

    final String APIKEY = "?api_key=6aa5a261-184f-42b9-8a43-92ef62505597";
    final String URL = "http://status.leagueoflegends.com/shards/";

    final String[] SERVER = new String[] {"br", "eune", "euw", "lan", "las", "na", "oce", "ru", "tr"};
    ArrayList<String> serverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        serverName = new ArrayList<String>();

        try {
            for (int i = 0; i < SERVER.length; i++) {
                String url = URL + SERVER[i] + APIKEY;
                JSONReader json = new JSONReader();
                JSONObject jsonobj = json.read(url);

                serverName.add(jsonobj.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainPageFragment mainPage = new MainPageFragment();

        Bundle b = new Bundle();
        b.putStringArrayList("serverName", serverName);
        mainPage.setArguments(b);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainPage).commit();
    }

    @Override
    public void onPause() {
        ((MainPageFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).updateExpanded();
        super.onPause();
    }
}
