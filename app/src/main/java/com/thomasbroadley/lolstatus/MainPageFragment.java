package com.thomasbroadley.lolstatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainPageFragment extends Fragment {
    final String APIKEY = "?api_key=" + new APIKey().getKey();
    final String URL = "http://status.leagueoflegends.com/shards/";

    final String[] SERVER = new String[] {"br", "eune", "euw", "lan", "las", "na", "oce", "ru", "tr"};
    final int[] PREFERRED_SERVER = new int[] {2, 5};

    ArrayList<ServerStatus> server;
    ArrayList<Boolean> useServer;
    ArrayList<String> displayedServer;

    ExpandableListView elv;

    public MainPageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

    @Override
    public void onStart() {
        try {
            readStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onStart();
    }

    public void readStatus() throws Exception {
        server = new ArrayList<ServerStatus>();

        String filename = "servers_to_display";

        ObjectInputStream ois;

        if (getActivity().getFileStreamPath(filename).exists() && getActivity().getFileStreamPath(filename).length() != 0) {
            ois = new ObjectInputStream(getActivity().openFileInput(filename));
            useServer = (ArrayList<Boolean>) ois.readObject();
        } else {
            useServer = new ArrayList<Boolean>();

            for (int i = 0; i < SERVER.length; i++) {
                useServer.add(false);
            }

            for (int i = 0; i < PREFERRED_SERVER.length; i++) {
                useServer.set(PREFERRED_SERVER[i], true);
            }

            ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(filename, Context.MODE_PRIVATE));
            oos.writeObject(useServer);

        }

        updateDisplayedServers();
        updateServerStatus();

        elv = (ExpandableListView)getView().findViewById(R.id.expandableListView);
        elv.setAdapter(new ServerStatusAdapter(getActivity(), server, elv));

        SharedPreferences prefs = getActivity().getSharedPreferences("LOL_STATUS_PREFS", Context.MODE_PRIVATE);
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

    public void updateDisplayedServers() {
        displayedServer = new ArrayList<String>();

        for (int i = 0; i < SERVER.length; i++) {
            if (useServer.get(i)) {
                displayedServer.add(SERVER[i]);
            }
        }
    }

    public void updateServerStatus() throws Exception {
        server = new ArrayList<ServerStatus>();

        String filename = "status_file";

        ConnectivityManager manager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            for (int i = 0; i < displayedServer.size(); i++) {
                String url = URL + displayedServer.get(i) + APIKEY;

                JSONReader json = new JSONReader();
                JSONObject jsonobj = json.read(url);
                server.add(new ServerStatus(jsonobj));

            }

            TextView empty = (TextView) getView().findViewById(R.id.empty);
            if (server.isEmpty()) {
                empty.setVisibility(View.VISIBLE);
                empty.setText(R.string.no_servers_selected);
            } else {
                empty.setVisibility(View.INVISIBLE);
            }

            Toast t = Toast.makeText(getActivity(), "Server status updated", Toast.LENGTH_SHORT);
            t.show();
        } else {
            TextView empty = (TextView) getView().findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
            empty.setText(R.string.connected_query);

            Toast t = Toast.makeText(getActivity(), "Could not update server status", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            updateExpanded();

            SettingsFragment settings = new SettingsFragment();

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, settings);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.action_refresh) {
            try {
                updateServerStatus();
                ((BaseExpandableListAdapter)elv.getExpandableListAdapter()).notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.action_disclaimer) {
            updateExpanded();

            DisclaimerFragment disclaimer = new DisclaimerFragment();

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, disclaimer);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateExpanded() {
        SharedPreferences prefs = getActivity().getSharedPreferences("LOL_STATUS_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int expandedIndex = ((ServerStatusAdapter) elv.getExpandableListAdapter()).getLastExpandedGroupPosition();

        if (expandedIndex != -1) {
            String expanded = displayedServer.get(expandedIndex);
            editor.putString("expanded", expanded);
        } else {
            editor.putString("expanded", "");
        }

        editor.apply();
    }
}
