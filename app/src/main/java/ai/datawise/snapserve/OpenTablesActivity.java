package ai.datawise.snapserve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.HardwarePropertiesManager;
import android.provider.ContactsContract;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenTablesActivity extends AppCompatActivity {

    LinearLayout mainLayout=null;
    LinearLayout header=null;
    JSONArray mtables=null;
    int totalTables=0;
    int openTables=0;
    LinearLayout.LayoutParams hparams;
    ListView listview=null;
    ArrayList<String> openTablesList=new ArrayList<String>();

    class TableItem
    {
        private String id;
        private String time;
        private double gain;
        TableItem(String i,String t,double g)
        {
            id=i;
            time=t;
            gain=g;
        }
        String getid()
        {
            return id;
        }

        String gettime()
        {
            return time;
        }

        double getgain()
        {
            return gain;
        }
        void setGain(double v)
        {
            gain=v;
        }
    }


    class ItemAdapter extends ArrayAdapter<TableItem>
    {
        private Context mContext;
        private ArrayList<TableItem> itemList = new ArrayList<TableItem>();

        public ItemAdapter(Context ctx, ArrayList<TableItem> t)
        {
            super(ctx, 0,t);
            itemList=t;
            mContext=ctx;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView t1=new TextView(mContext);
            t1.setTextColor(Color.parseColor("#272f3d"));
            t1.setTextSize(Global.fontSize);
            t1.setText(itemList.get(position).getid()+"");
            layout.addView(t1);
            LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
            t1params.leftMargin=5*Global.width/100;
            t1params.height=6 * Global.height/100;
            t1params.width=hparams.width/8;
            t1params.gravity= Gravity.CENTER;
            t1.setLayoutParams(t1params);
            t1.setGravity(Gravity.CENTER);

            TextView t2=new TextView(mContext);
            t2.setTextColor(Color.parseColor("#163582"));
            t2.setTextSize(Global.fontSize);
            String tt=itemList.get(position).gettime();
            if(tt.equals("1"))
                t2.setText("1 "+mContext.getString(R.string.hourText));
            else
                t2.setText(tt+" "+mContext.getString(R.string.hoursText));
            layout.addView(t2);
            LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)t2.getLayoutParams();
            t2params.gravity=Gravity.CENTER;
            t2params.height=t1params.height;
            t2params.width=hparams.width/4;
            t2params.leftMargin=2*t1params.leftMargin;
            t2.setLayoutParams(t2params);
            t2.setGravity(Gravity.CENTER);


            TextView t3=new TextView(mContext);
            t3.setTextSize(Global.fontSize);
            t3.setTextColor(t1.getCurrentTextColor());
            t3.setText(Global.currency+Global.displayDecimal(itemList.get(position).getgain()));
            layout.addView(t3);
            LinearLayout.LayoutParams t3params=(LinearLayout.LayoutParams)t3.getLayoutParams();
            t3params.gravity=Gravity.CENTER;
            t3params.height=t2params.height;
            t3params.width=t2params.width/2;
            t3params.leftMargin=3*t1params.leftMargin;
            t3.setLayoutParams(t3params);
            t3.setGravity(Gravity.CENTER);


            ImageView im=new ImageView(mContext);
            im.setImageBitmap(Global.rightArrow);
            im.setClickable(true);
            im.setOnTouchListener(Global.touchListener);
            layout.addView(im);
            LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
            iparams.gravity=Gravity.CENTER;
            iparams.rightMargin=t1params.leftMargin;
            iparams.width=8*Global.rightArrow.getWidth()/7;
            iparams.height=Global.rightArrow.getHeight();
            iparams.leftMargin=hparams.width-t1params.leftMargin-t1params.width-t2params.leftMargin-t2params.width-t3params.leftMargin-t3params.width
                    -iparams.rightMargin-iparams.width;
            im.setLayoutParams(iparams);

            return layout;
        }

        void updateGain(int pos,double value)
        {
            TableItem t=itemList.get(pos);
            t.setGain(value);
            itemList.set(pos,t);
            this.notifyDataSetChanged();
        }
    }

    ItemAdapter adapter;
    void getOrder(final int tablepos, int orderid)
    {
        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.GET, Global.server + "/orders/"+orderid,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Flags final orders is "+response);
                try {
                    double value=response.getDouble("amountTotal");

                    adapter.updateGain(tablepos,value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Global.showAlert(getString(R.string.executionProblemText)+" GET Orders",OpenTablesActivity.this);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", Global.getAuthorizationString(Global.loginCredentials));
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObejct);

    }

    void makeHeaderView()
    {
        header=new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundResource(R.drawable.backwithborder);

        header.setBackgroundColor(Color.WHITE);
        mainLayout.addView(header);
        hparams=(LinearLayout.LayoutParams)header.getLayoutParams();
        hparams.width=Global.width;
        hparams.height=Global.headerHeight;
        header.setLayoutParams(hparams);
        Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.grayback);
        double ratio = bm.getWidth()*1.0/bm.getHeight();
        int neww=8 * hparams.width/100;
        int newh=(int)(neww/ratio);
        bm=bm.createScaledBitmap(bm,neww,newh,true);
        ImageView im=new ImageView(this);
        im.setImageBitmap(bm);
        im.setClickable(true);
        im.setOnTouchListener(Global.touchListener);
        header.addView(im);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        iparams.leftMargin=5 * hparams.width/100;
        iparams.topMargin=20 * hparams.height/100;
        iparams.width=neww;
        im.setLayoutParams(iparams);
        TextView t1=new TextView(this);
        t1.setText(getString(R.string.openalltablestext));
        t1.setTextSize(Global.fontSize+4);
        t1.setTextColor(Color.parseColor("#272f3d"));
        header.addView(t1);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t1.getLayoutParams();
        tparams.topMargin=iparams.topMargin;
        tparams.leftMargin=2 * iparams.leftMargin;
        tparams.width=hparams.width/2;
        t1.setLayoutParams(tparams);
        TextView t2=new TextView(this);
        t2.setTextSize(Global.fontSize);
        t2.setTextColor(t1.getCurrentTextColor());
        t2.setText(openTables+" "+getString(R.string.oftext)+" "+totalTables);
        header.addView(t2);
        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)t2.getLayoutParams();
        t2params.topMargin=tparams.topMargin;
        t2params.width=hparams.width/5;
        t2params.rightMargin=iparams.leftMargin;
        t2params.leftMargin=hparams.width-iparams.leftMargin-iparams.width-tparams.leftMargin-tparams.width-t2params.rightMargin-t2params.width;
        t2.setLayoutParams(t2params);
    }

    void openOrder(String tableCode)
    {
        try {
            if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.others.view")==false
            )
            {
                Global.showAlert(getString(R.string.operationNotPermittedText),this);
            }
            else {
                for (int i = 0; i < mtables.length(); i++) {
                    try {
                        JSONObject x = mtables.getJSONObject(i);
                        if (x.getString("tableCode").equals(tableCode)) {

                            Intent I = new Intent(OpenTablesActivity.this, OpenOrderActivity.class);
                            I.putExtra("tableCode", x.getString("tableCode"));
                            I.putExtra("isTakeOut", false);
                            I.putExtra("tableJson", x.toString());
                            OpenTablesActivity.this.startActivityForResult(I, Global.exitCode);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    ArrayList<TableItem> tt=new ArrayList<TableItem>();

    void makeAdapter()
    {

        tt.clear();
        for(int i=0;i<mtables.length();i++)
        {
            try {
                JSONObject x=mtables.getJSONObject(i);
                double value=0.0;

                if(x.has("isOpen") && x.getBoolean("isOpen")==false)  continue;

                TableItem t=new TableItem(x.getString("tableCode"),
                        Global.hoursDiffFromDate(x.getString("openedAt")),value);
                tt.add(t);

            } catch (JSONException e) {
                System.out.println("GIANNIS JSON ERROR "+e.getMessage());
                e.printStackTrace();
            }

        }
        adapter=new ItemAdapter(this,tt);
    }

    void makeListView()
    {
        listview=new ListView(this);
        mainLayout.addView(listview);
        final LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)listview.getLayoutParams();
        lparams.topMargin=2;
        lparams.width=hparams.width;
        lparams.height=Global.height-2*hparams.height-5;
        listview.setLayoutParams(lparams);
        listview.setBackgroundColor(Color.WHITE);

        makeAdapter();

        listview.setAdapter(adapter);
        int icount=0;
        for(int i=0;i<mtables.length();i++)
        {
            try {
                JSONObject x=mtables.getJSONObject(i);
                if(x.has("isOpen") && x.getBoolean("isOpen")==false)
                    continue;

                JSONObject orders=x.getJSONObject("order");
                if(x!=null)
                {
                    int ic=icount;
                    getOrder(ic,orders.getInt("orderId"));
                    icount++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String tableCode = tt.get(position).getid();

                try {
                    if (Global.loginCredentials.getBoolean("inBreak") == true)
                    {
                        OrderCloseDialog dialog=new OrderCloseDialog(OpenTablesActivity.this,
                                OpenTablesActivity.this.getString(R.string.inBreakMessage),
                                OpenTablesActivity.this.getString(R.string.okGotItText),"");
                        dialog.show();
                    }
                    else
                    if(Global.loginCredentials.getBoolean("checkedIn")==false)
                    {
                        InternetClass.checkIn(OpenTablesActivity.this, Global.server + "/checkin", Global.getAuthorizationString(Global.loginCredentials),
                                new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            Global.loginCredentials.put("checkedIn",true);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        openOrder(tableCode);
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Global.showAlert(getString(R.string.executionProblemText)+" Checked In",OpenTablesActivity.this);
                                    }
                                });

                    }
                    else {
                        openOrder(tableCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //refresh current level with tables
        //if there are not tables onbackpressed
        int levelid= 0;
        try {
            levelid = Global.levels.getJSONObject(Global.levelIndex).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int finalLevelid = levelid;
        InternetClass.ObjectGETCall(this, Global.server + "/tables/status?levelId=" + levelid,
                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray levels= null;
                        mtables=new JSONArray();
                        try {
                            levels = response.getJSONArray("levels");
                            for(int i=0;i<levels.length();i++)
                            {
                                JSONObject xx=levels.getJSONObject(i);
                                if(xx.getInt("id")== finalLevelid)
                                {
                                    mtables=xx.getJSONArray("tables");
                                    break;
                                }

                            }
                            totalTables=mtables.length();
                            openTables=0;
                            for(int i=0;i<mtables.length();i++)
                            {
                                if(mtables.getJSONObject(i).getBoolean("isOpen")==true)
                                    openTables++;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        fetchAdditionalTables();

                        if(openTables==0)
                        {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert(getString(R.string.executionProblemText)+" GET Tables "+error,OpenTablesActivity.this);

                    }
                });
    }

    void makeBottomView()
    {
        LinearLayout bottomLayout=new LinearLayout(this);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(bottomLayout);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)bottomLayout.getLayoutParams();
        bparams.width=Global.width;
        bparams.height=hparams.height+5;
        bparams.height/=2;
        bottomLayout.setLayoutParams(bparams);
        bottomLayout.setBackgroundColor(Color.WHITE);
        /*
        ImageButton settingsButton=new ImageButton(this);
        Bitmap bm1=BitmapFactory.decodeResource(getResources(),R.drawable.linesettings);
        Bitmap bm2=BitmapFactory.decodeResource(getResources(),R.drawable.printicon);
        double ratio =bm1.getWidth()*1.0/bm1.getHeight();
        int newh=25 *bparams.height/100;
        int neww=(int)(ratio*newh);

        bm1=bm1.createScaledBitmap(bm1,neww,newh,true);
        bm2=bm2.createScaledBitmap(bm2,neww,newh,true);
        settingsButton.setBackgroundResource(R.drawable.transparentbutton);
        settingsButton.setImageBitmap(bm1);

        bottomLayout.addView(settingsButton);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)settingsButton.getLayoutParams();
        sparams.leftMargin=5 * bparams.width/100;
        sparams.topMargin=10 * bparams.height/100;
        sparams.width=40 * bparams.width/100;
        sparams.height=60*bparams.height/100;
        sparams.bottomMargin=sparams.topMargin;
        settingsButton.setLayoutParams(sparams);
        settingsButton.setPadding(5*sparams.width/100,5*sparams.height/100,5*sparams.width/100,5*sparams.height/100);

        ImageButton printButton=new ImageButton(this);
        printButton.setImageBitmap(bm2);
        printButton.setBackgroundResource(R.drawable.transparentbutton);
        bottomLayout.addView(printButton);
        printButton.setLayoutParams(sparams);
        printButton.setPadding(5*sparams.width/100,5*sparams.height/100,5*sparams.width/100,5*sparams.height/100);
*/
    }

    void addItemToTable(final int startIndex)
    {
        if(startIndex>=openTablesList.size())
        {
            makeAdapter();
            listview.setAdapter(adapter);
            openTables+=openTablesList.size();
            return;
        }
        InternetClass.ObjectGETCall(this, Global.server + "/orders/tables/" + openTablesList.get(startIndex),
                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mtables.put(response);
                        addItemToTable(startIndex+1);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert(getString(R.string.executionProblemText)+" GET Tables "+error,OpenTablesActivity.this);

                    }
                });
    }

    void fetchAdditionalTables()
    {


        InternetClass.ObjectGETCall(this, Global.server + "/orders/tables/open", Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                openTablesList=new ArrayList<String>();

                try {
                    JSONArray orders=response.getJSONArray("orders");
                    for(int i=0;i<orders.length();i++)
                    {
                        JSONObject xx=orders.getJSONObject(i);
                        if(xx.has("levelId") && xx.get("levelId")!=JSONObject.NULL && xx.getInt("levelId")==
                                Global.levels.getJSONObject(Global.levelIndex).getInt("id"))
                        {
                            String tcode=xx.getString("tableCode");
                            boolean found=false;
                            for(int j=0;j<mtables.length();j++)
                            {
                                JSONObject mx=mtables.getJSONObject(j);
                                if(mx.getString("tableCode").equals(tcode)){
                                    found=true;
                                    break;
                                }
                            }
                            if(!found) {
                                openTablesList.add(tcode);

                                JSONObject table=new JSONObject();
                                try {
                                    table.put("tableCode",tcode);
                                    table.put("isOpen",true);
                                    table.put("openedAt","");
                                    table.put("order",new JSONObject());
                                    table.getJSONObject("order").put("orderId",xx.getInt("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mtables.put(table);
                                System.out.println("GIANNIS ADD " + xx);
                            }
                        }
                    }
                    openTables+=openTablesList.size();
                    makeAdapter();
                    listview.setAdapter(adapter);

                } catch (JSONException e) {

                    e.printStackTrace();
                }


                // addItemToTable(0);

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Global.showAlert(getString(R.string.executionProblemText)+" GET Tables "+error.networkResponse,OpenTablesActivity.this);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        setContentView(mainLayout);

        Bundle b = getIntent().getExtras();
        String Array=b.getString("mtables");
        try {
            mtables=new JSONArray(Array);
            totalTables=mtables.length();
            for(int i=0;i<mtables.length();i++)
            {
                if(mtables.getJSONObject(i).getBoolean("isOpen")==true)
                    openTables++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeHeaderView();
        makeListView();
        makeBottomView();
        fetchAdditionalTables();
    }

    public void onBackPressed()
    {
        //nothing here
    }
}
