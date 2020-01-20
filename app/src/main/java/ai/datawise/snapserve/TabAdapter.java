package ai.datawise.snapserve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TabAdapter extends PagerAdapter {

    private Context context;
    private int pageWidth=0,pageHeight=0;
    ViewPager p;

    ArrayList<Integer> pos=new ArrayList<Integer>();

    public TabAdapter(Context ctx,int pw,int ph)
    {
        context=ctx;
        pageWidth=pw;
        pageHeight=ph;
    }


    LinearLayout makeDashBoard()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        TabLayout tab=new TabLayout(context);
        try {
            if(Global.loginCredentials.getBoolean("isManager"))
            {
                tab.addTab(tab.newTab().setText(context.getString(R.string.overviewText)));
                tab.addTab(tab.newTab().setText(context.getString(R.string.waitersText)));

            }
            else {
                tab.addTab(tab.newTab().setText(context.getString(R.string.overviewText)));
                tab.addTab(tab.newTab().setText(context.getString(R.string.ordersText)));
                tab.addTab(tab.newTab().setText(context.getString(R.string.workText)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        l.addView(tab);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)tab.getLayoutParams();
        tparams.width=pageWidth;
        tparams.height=10 * pageHeight/100;
        tab.setLayoutParams(tparams);
        tab.setSelectedTabIndicatorColor(Color.parseColor(Global.tintColor));

        p=new ViewPager(context);
        p.setBackgroundColor(Color.RED);
        l.addView(p);

        LinearLayout.LayoutParams pparams=(LinearLayout.LayoutParams)p.getLayoutParams();
        pparams.width=pageWidth;
        pparams.height=pageHeight-tparams.height;
        p.setLayoutParams(pparams);


        p.setAdapter(new DashboardPager(context,pparams.width,pparams.height));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals(context.getString(R.string.overviewText)))
                    p.setCurrentItem(0);
                else
                if(tab.getText().equals(context.getString(R.string.ordersText)))
                    p.setCurrentItem(1);
                else
                    p.setCurrentItem(2);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
        return l;
    }

    AdapterView.OnItemClickListener listener=null;
    JSONArray takeoutarray=new JSONArray();

    void openTakeOut(int position)

    {if(position<takeoutarray.length()) {
        Global.makeTakeoutTable();
        try {
            Global.takeoutTable.put("order", takeoutarray.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Global.takeoutTable.getJSONObject("order").put("orderId", takeoutarray.getJSONObject(position).getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent I = new Intent(((Activity) context), OpenOrderActivity.class);
        I.putExtra("isTakeOut", true);
        try {
            I.putExtra("tableCode", Global.takeoutTable.getString("tableCode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        I.putExtra("tableJson", Global.takeoutTable.toString());
        try {
            I.putExtra("details", takeoutarray.getJSONObject(position).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((Activity) context).startActivityForResult(I, Global.takeoutExitCode);
    }
    else {
        JSONObject params = new JSONObject();
        try {
            params.put("workdate", Global.workdate);
            params.put("customerName", "");
            params.put("customerId", 1);
            params.put("loyaltyCard", "");
            params.put("posTerminalId", Global.loginCredentials.getInt("terminalid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        InternetClass.ObjectPOSTCall(context, Global.server + "/orders/takeaway/", params,
                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Global.makeTakeoutTable();
                        try {

                            Global.takeoutTable.put("order", response);
                            Global.takeoutTable.getJSONObject("order").put("details", new JSONArray());
                            Global.takeoutTable.getJSONObject("order").put("orderId", response.getInt("id"));
                            Intent I = new Intent(((Activity) context), OpenOrderActivity.class);
                            I.putExtra("isTakeOut", true);
                            I.putExtra("tableCode", Global.takeoutTable.getString("tableCode"));
                            I.putExtra("tableJson", Global.takeoutTable.toString());
                            I.putExtra("details", response.toString());
                            ((Activity) context).startActivityForResult(I, Global.takeoutExitCode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert("Problem in takeway", context);
                    }
                });
    }
    }


    LinearLayout makeTakeout()
    {
        if(listener==null)
        {
            listener=new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    try {
                        if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.tables.open")==false)
                        {
                            Global.showAlert(context.getString(R.string.operationNotPermittedText),context);
                        }
                        else
                        {
                            if(
                                    Global.loginCredentials.has("inBreak") &&
                                            Global.loginCredentials.getBoolean("inBreak")==true)
                            {
                                OrderCloseDialog dialog=new OrderCloseDialog(context,
                                        context.getString(R.string.inBreakMessage),
                                        context.getString(R.string.okGotItText),"");
                                dialog.show();
                            }
                            else {
                                if (Global.loginCredentials.has("checkedIn")==false ||
                                        Global.loginCredentials.getBoolean("checkedIn") == false) {
                                    InternetClass.checkIn(context, Global.server + "/checkin", Global.getAuthorizationString(Global.loginCredentials),
                                            new JSONObjectListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    openTakeOut(position);
                                                    try {
                                                        Global.loginCredentials.put("checkedIn", true);
                                                        Global.saveData();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Global.showAlert(context.getString(R.string.executionProblemText)+" Check In",context);

                                                }
                                            });
                                }
                                else
                                    openTakeOut(position);
                            }
                        }
                    } catch (JSONException e) {
                        System.out.println("GIANNIS JSON ERROR "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            };
        }
        LinearLayout l=new LinearLayout(context);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        l.setOrientation(LinearLayout.VERTICAL);
        final ListView list=new ListView(context);
        l.addView(list);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)list.getLayoutParams();
        lparams.topMargin=2 * Global.height/100;
        lparams.leftMargin=1 * Global.width/100;
        lparams.width=98 * Global.width/100;
        list.setLayoutParams(lparams);

        InternetClass.ArrayGETCall(context, Global.server + "/orders/takeaway/open", Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> choice=new ArrayList<String>();
                takeoutarray=response;
                for(int i=0;i<response.length();i++)
                {
                    try {
                        choice.add(context.getString(R.string.takeOutText)+"#"+response.getJSONObject(i).getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                choice.add(context.getString(R.string.newTakeOutText));
                CheckedAdapter adapter=new CheckedAdapter(context,choice,98*Global.width/100,listener);
                list.setAdapter(adapter);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Global.showAlert("ERROR IN TAKEOUT "+error,context);

            }
        });
        return l;
    }

    LinearLayout makeTables()
    {
        LinearLayout l=new LinearLayout(context);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        l.setOrientation(LinearLayout.VERTICAL);
        TableClass t=new TableClass(context,l,pageWidth,pageHeight);
        return l;
    }

    LinearLayout makeTablesWithLevel(int level)
    {
        return makeTables();
    }

    LinearLayout makeDeliver()
    {
        LinearLayout l=new LinearLayout(context);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    LinearLayout makeInventory()
    {
        LinearLayout l=new LinearLayout(context);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    @Override
    public int getCount()
    {
        int k=5;
        JSONObject roles= null;
        try {
            roles = Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions");
            if(roles.getBoolean("modules.mobile.dashboard")==false &&
                    roles.getBoolean("modules.mobile.inventory")==false &&
                    roles.getBoolean("orders.delivery.dispatch")==false)
                k=2;
            else
            if(roles.getBoolean("modules.mobile.dashboard")==false
                    && roles.getBoolean("modules.mobile.inventory")==false
                    && roles.getBoolean("orders.delivery.dispatch")==true)
                k=4;
            else
            if(roles.getBoolean("modules.mobile.dashboard")==false
                    && roles.getBoolean("modules.mobile.inventory")==true
                    &&  roles.getBoolean("orders.delivery.dispatch")==false)
                k=4;
            else
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==false
                    &&  roles.getBoolean("orders.delivery.dispatch")==false)
                k=3;
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==false
                    &&  roles.getBoolean("orders.delivery.dispatch")==true)
                k=4;
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==true
                    &&  roles.getBoolean("orders.delivery.dispatch")==false)
                k=4;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return k;
    }

    int setItem(int id)
    {
        int position=-1;
        for(int i=0;i<pos.size();i++)
        {
            if(pos.get(i)==id)
            {
                position=i;
                break;
            }
        }
        return position;
    }
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position)
    {

        int k=0;
        pos.clear();
        JSONObject roles= null;
        try {
            roles = Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions");
            if(roles.getBoolean("modules.mobile.dashboard")==false &&
                    roles.getBoolean("modules.mobile.inventory")==false &&
                    roles.getBoolean("orders.delivery.dispatch")==false) {
                pos.add(R.id.tabtables);
                pos.add(R.id.tabtakeout);
                k = 1;
            }
            else
            if(roles.getBoolean("modules.mobile.dashboard")==false
                    && roles.getBoolean("modules.mobile.inventory")==false
                    && roles.getBoolean("orders.delivery.dispatch")==true) {
                k = 2;
                pos.add(R.id.tabtables);
                pos.add(R.id.tabtakeout);
                pos.add(R.id.tabdelivery);
            }
            else
            if(roles.getBoolean("modules.mobile.dashboard")==false
                    && roles.getBoolean("modules.mobile.inventory")==true
                    &&  roles.getBoolean("orders.delivery.dispatch")==false) {
                pos.add(R.id.tabtables);
                pos.add(R.id.tabtakeout);
                pos.add(R.id.tabinventory);
                k = 3;
            }
            else
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==false
                    &&  roles.getBoolean("orders.delivery.dispatch")==false) {
                pos.add(R.id.tabhome);
                pos.add(R.id.tabtables);
                pos.add(R.id.tabtakeout);
                k = 4;
            }
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==false
                    &&  roles.getBoolean("orders.delivery.dispatch")==true) {
                pos.add(R.id.tabhome);
                pos.add(R.id.tabtables);
                pos.add(R.id.tabtakeout);
                pos.add(R.id.tabdelivery);
                k = 5;
            }
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==true
                    &&  roles.getBoolean("orders.delivery.dispatch")==false) {
                pos.add(R.id.tabhome);
                pos.add(R.id.tabtables);
                pos.add(R.id.tabtakeout);
                pos.add(R.id.tabinventory);
                k = 6;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(k==0)
        {
            pos.add(R.id.tabhome);
            pos.add(R.id.tabtables);
            pos.add(R.id.tabtakeout);
            pos.add(R.id.tabinventory);
            pos.add(R.id.tabdelivery);
        }
        LinearLayout l=null;

        if(k==1) {
            switch (position) {
                case 0:
                    l = makeTablesWithLevel(Global.levelIndex);
                    break;
                case 1:
                    l = makeTakeout();
                    break;
                default:
                    return null;
            }
        }
        else
        if(k==2)
        {
            switch (position) {
                case 0:
                    l = makeTablesWithLevel(Global.levelIndex);
                    break;
                case 1:
                    l = makeTakeout();
                    break;
                case 2:
                    l = makeDeliver();
                    break;

                default:
                    return null;
            }
        }
        else
        if(k==3)
        {
            switch (position) {
                case 0:
                    l = makeTablesWithLevel(Global.levelIndex);
                    break;
                case 1:
                    l = makeTakeout();
                    break;
                case 2:
                    l = makeInventory();
                    break;
                default:
                    return null;
            }
        }
        else
        if(k==4)
        {
            switch (position) {
                case 0:
                    l = makeDashBoard();
                    break;
                case 1:
                    l = makeTablesWithLevel(Global.levelIndex);
                    break;
                case 2:
                    l = makeTakeout();
                    break;
                default:
                    return null;
            }
        }
        else
        if(k==5)
        {
            switch (position) {
                case 0:
                    l = makeDashBoard();
                    break;
                case 1:
                    l = makeTablesWithLevel(Global.levelIndex);
                    break;
                case 2:
                    l = makeTakeout();
                    break;
                case 3:
                    l = makeDeliver();
                    break;

                default:
                    return null;
            }
        }
        else
        if(k==6)
        {
            switch (position) {
                case 0:
                    l = makeDashBoard();
                    break;
                case 1:
                    l = makeTablesWithLevel(Global.levelIndex);
                    break;
                case 2:
                    l = makeTakeout();
                    break;
                case 3:
                    l = makeInventory();
                    break;
                default:
                    return null;
            }
        }
        else
        if(k==0)
        {
            switch (position) {
                case 0:
                    l = makeDashBoard();
                    break;
                case 1:
                    l =makeTablesWithLevel(Global.levelIndex);
                    break;
                case 2:
                    l = makeTakeout();
                    break;
                case 3:
                    l = makeDeliver();
                    break;
                case 4:
                    l = makeInventory();
                    break;
                default:
                    return null;
            }
        }
        collection.addView(l);
        return l;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
