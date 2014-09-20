package com.thomasbroadley.lolstatus;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    String[] serverAbbrev;
    String[] serverName;

    ArrayList<CheckBox> box;

    ArrayList<Boolean> useServer;
    ArrayList<Boolean> oldUseServer;
    String filename = "servers_to_display";

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serverAbbrev = getActivity().getResources().getStringArray(R.array.server_abbrev);
        serverName = getActivity().getResources().getStringArray(R.array.server_full);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        Button select = (Button)v.findViewById(R.id.select_all);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox cb : box) {
                    cb.setChecked(true);
                    useServer.set((Integer)cb.getTag(), true);
                }

                try {
                    ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(filename, Context.MODE_PRIVATE));
                    oos.writeObject(useServer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button clear = (Button)v.findViewById(R.id.clear_all);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox cb : box) {
                    cb.setChecked(false);
                    useServer.set((Integer)cb.getTag(), false);
                }

                try {
                    ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(filename, Context.MODE_PRIVATE));
                    oos.writeObject(useServer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            ObjectInputStream ois = new ObjectInputStream(getActivity().openFileInput(filename));
            useServer = (ArrayList<Boolean>) ois.readObject();
            oldUseServer = new ArrayList<Boolean>(useServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        box = new ArrayList<CheckBox>();

        LinearLayout ll = (LinearLayout)getView().findViewById(R.id.settings_layout);

        for (int i = 0; i < serverAbbrev.length; i++) {
            CheckBox cb = new CheckBox(getActivity());
            cb.setChecked(useServer.get(i));
            cb.setTag(i);
            cb.setText(serverName[i]);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    useServer.set((Integer)compoundButton.getTag(), b);

                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(filename, Context.MODE_PRIVATE));
                        oos.writeObject(useServer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            box.add(cb);

            ll.addView(cb);
        }

        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            MainPageFragment mainPage = new MainPageFragment();

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mainPage);
            transaction.addToBackStack(null);
            transaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
