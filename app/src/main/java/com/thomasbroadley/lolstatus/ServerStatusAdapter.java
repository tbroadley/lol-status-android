package com.thomasbroadley.lolstatus;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ServerStatusAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ExpandableListView elv;
    private ArrayList<ServerStatus> group;
    private int lastExpandedGroupPosition;

    public ServerStatusAdapter(Context context, ArrayList<ServerStatus> group, ExpandableListView elv) {
        this.context = context;
        this.group = group;
        this.elv = elv;
        lastExpandedGroupPosition = -1;
    }

    public int getLastExpandedGroupPosition() {
        return lastExpandedGroupPosition;
    }

    public void setLastExpandedGroupPosition(int i) {
        lastExpandedGroupPosition = i;
    }

    public ArrayList<ServerStatus> getGroups() {
        return group;
    }

    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return group.get(i).getServices().size();
    }

    @Override
    public Object getGroup(int i) {
        return group.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return group.get(i).getService(i2);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
       return i2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ServerStatus status = (ServerStatus)getGroup(i);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.server_status, null);

        Resources res = context.getResources();
        int paddingInDP = 5;
        int paddingInPX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingInDP, res.getDisplayMetrics());

        view.setPadding(0, paddingInPX, 0, paddingInPX);

        TextView tv = (TextView)view.findViewById(R.id.server_name);
        tv.setText(status.getName());

        ImageView iv = (ImageView)view.findViewById(R.id.server_icon);

        boolean serverProblem = false;
        for (int j = 0; j < status.getServices().size(); j++) {
            if (!status.getService(j).getStatus().equals("online")) {
                serverProblem = true;
            }
        }

        if (serverProblem) {
            iv.setImageResource(R.drawable.server_offline);
            view.setBackgroundColor(context.getResources().getColor(R.color.server_bg_offline_color));
        } else {
            iv.setImageResource(R.drawable.server_online);
            view.setBackgroundColor(context.getResources().getColor(R.color.server_bg_online_color));
        }

        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        Service service = (Service)getChild(i, i2);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.service, null);

        LinearLayout incidents = (LinearLayout) view.findViewById(R.id.incidents);

        ArrayList<Incident> incidentsArrayList = service.getIncidents();

        for (int j = incidentsArrayList.size() - 1; j >= 0; j--) {
            LinearLayout updates = new TableLayout(context);

            ArrayList<Update> updatesArrayList = incidentsArrayList.get(j).getUpdates();

            for (int k = updatesArrayList.size() - 1; k >= 0; k--) {
                Update thisUpdate = updatesArrayList.get(k);

                Resources res = context.getResources();
                int sidePaddingInDP = 15;
                int sidePaddingInPX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sidePaddingInDP, res.getDisplayMetrics());
                int topPaddingInDP = 2;
                int topPaddingInPX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topPaddingInDP, res.getDisplayMetrics());

                TextView message = new TextView(context);
                message.setText(thisUpdate.getMessage());
                message.setTextSize(17);
                message.setPadding(sidePaddingInPX, topPaddingInPX, sidePaddingInPX, 0);

                TextView time = new TextView(context);
                time.setText("Posted " + DateUtils.getRelativeDateTimeString(context, thisUpdate.getUpdated().getTime(), 0, DateUtils.WEEK_IN_MILLIS, 0));
                time.setTextSize(12);
                time.setPadding(sidePaddingInPX, 0, sidePaddingInPX, 0);

                updates.addView(message);
                updates.addView(time);
            }

            incidents.addView(updates);
        }

        TextView name = (TextView)view.findViewById(R.id.service_name);
        name.setTextSize(22);
        name.setText(service.getName());

        TextView status = (TextView)view.findViewById(R.id.service_status);
        status.setTextSize(22);
        String statusString = service.getStatus();

        if (statusString.equals("online")) {
            status.setTextColor(context.getResources().getColor(R.color.online_color));
        } else {
            status.setTextColor(context.getResources().getColor(R.color.offline_color));
        }

        String output = statusString.substring(0, 1).toUpperCase() + statusString.substring(1);
        status.setText(output);

        Resources res = context.getResources();
        int topPaddingInDP = 3;
        int topPaddingInPX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topPaddingInDP, res.getDisplayMetrics());

        int bottomPaddingInDP = 5;
        int bottomPaddingInPX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomPaddingInDP, res.getDisplayMetrics());

        view.setPadding(0, topPaddingInPX, 0, bottomPaddingInPX);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {
        if (i != lastExpandedGroupPosition && lastExpandedGroupPosition != -1) {
            elv.collapseGroup(lastExpandedGroupPosition);
        }

        super.onGroupExpanded(i);
        lastExpandedGroupPosition = i;
    }

    @Override
    public void onGroupCollapsed(int i) {
        boolean allCollapsed = true;

        for (int j = 0; j < getGroupCount(); j++) {
            if (elv.isGroupExpanded(j)) {
                allCollapsed = false;
            }
        }

        if (allCollapsed) {
            lastExpandedGroupPosition = -1;
        }

        super.onGroupCollapsed(i);
    }
}
