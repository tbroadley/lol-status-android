package com.thomasbroadley.lolstatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;


public class MainPage extends ActionBarActivity {

    final String APIKEY = new APIKey().getKey();
    final String URL = "http://status.leagueoflegends.com/shards/";

    final String[] SERVER = new String[] {"br", "eune", "euw", "lan", "las", "na", "oce", "ru", "tr"};
    ArrayList<String> serverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        serverName = new ArrayList<String>();

        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        MainPageFragment mainPage = new MainPageFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainPage).commit();
    }

    @Override
    public void onPause() {
        ((MainPageFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).updateExpanded();
        super.onPause();
    }
}
