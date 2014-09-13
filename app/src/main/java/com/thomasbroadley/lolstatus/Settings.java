package com.thomasbroadley.lolstatus;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class Settings extends ActionBarActivity {

    final String APIKEY = "?api_key=6aa5a261-184f-42b9-8a43-92ef62505597";
    final String URL = "http://status.leagueoflegends.com/shards/";

    final String[] SERVER = new String[] {"br", "eune", "euw", "lan", "las", "na", "oce", "ru", "tr"};

    ArrayList<String> serverName;
    ArrayList<Boolean> useServer;
    ArrayList<Boolean> oldUseServer;
    String filename = "servers_to_display";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serverName = new ArrayList<String>();

        for (String s : SERVER) {
            String url = URL + s + APIKEY;

            try {
                JSONReader json = new JSONReader();
                JSONObject jsonobj = json.read(url);
                serverName.add(jsonobj.getString("name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(openFileInput(filename));
            useServer = (ArrayList<Boolean>) ois.readObject();
            oldUseServer = new ArrayList<Boolean>(useServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout ll = (LinearLayout)findViewById(R.id.settings_layout);

        for (int i = 0; i < SERVER.length; i++) {
            CheckBox cb = new CheckBox(this);
            cb.setChecked(useServer.get(i));
            cb.setTag(i);
            cb.setText(serverName.get(i));
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    useServer.set((Integer)compoundButton.getTag(), b);

                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(filename, MODE_PRIVATE));
                        oos.writeObject(useServer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ll.addView(cb);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(filename, MODE_PRIVATE));
            oos.writeObject(oldUseServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onBackPressed();
    }
}
