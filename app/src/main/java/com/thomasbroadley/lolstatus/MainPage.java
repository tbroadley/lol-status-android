package com.thomasbroadley.lolstatus;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainPage extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        MainPageFragment mainPage = new MainPageFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainPage).commit();
    }

    @Override
    public void onPause() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof MainPageFragment) {
            ((MainPageFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).updateExpanded();
        }
        super.onPause();
    }
}
