package ai.datawise.snapserve;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSettingsActivity extends AppCompatActivity {

    LinearLayout mainLayout=null;
    LinearLayout header=null;
    LinearLayout.LayoutParams hparams;
    ListView options;

    class ListAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> data = new ArrayList<String>();
        Switch managerSwitch=null;

        public ListAdapter(Context ctx, ArrayList<String> tt) {
            super(ctx, 0,tt);
            context=ctx;
            data = tt;
        }

        void changeItem(int pos, String x)
        {
            data.set(pos,x);
            notifyDataSetChanged();
        }
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LinearLayout bigLayout=new LinearLayout(context);
            bigLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout l=new LinearLayout(context);
            bigLayout.addView(l);
            l.setOrientation(LinearLayout.HORIZONTAL);
            ImageView im=new ImageView(context);
            l.addView(im);


            if(position==0) im.setImageBitmap(Global.userIcon);
            if(position==1) im.setImageBitmap(Global.terminalIcon);
            if(position==2) im.setImageBitmap(Global.xiconForList);
            if(position==3) im.setImageBitmap(Global.cofferForList);
            if(position==4) im.setImageBitmap(Global.clockForList);
            LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
            iparams.leftMargin=5 * Global.height/100;
            iparams.topMargin=2 * Global.height/100;
            iparams.height=250 * Global.getMeasuredHeight(context,data.get(position),Global.fontSize)/100;
            iparams.width=Global.editProfile.getWidth();
            iparams.bottomMargin=2 * Global.height/100;
            iparams.gravity= Gravity.CENTER_VERTICAL;
            im.setLayoutParams(iparams);

            TextView t=new TextView(context);
            t.setText(data.get(position));
            t.setTextSize(Global.fontSize);
            t.setTextColor(Color.parseColor("#de000000"));
            l.addView(t);
            LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
            tparams.leftMargin=iparams.leftMargin;
            tparams.topMargin=iparams.topMargin;
            tparams.bottomMargin=iparams.bottomMargin;
            tparams.height=iparams.height;
            tparams.width=50 * Global.width/100;
            tparams.gravity=Gravity.CENTER;
            t.setLayoutParams(tparams);
            t.setGravity(Gravity.CENTER_VERTICAL);
            if(position<=1)
            {
                TextView divider=new TextView(context);
                divider.setBackgroundColor(Color.parseColor("#1e000000"));
                bigLayout.addView(divider);
                LinearLayout.LayoutParams dparams=(LinearLayout.LayoutParams)divider.getLayoutParams();
                dparams.width=Global.width;
                dparams.height=2;
                divider.setLayoutParams(dparams);
            }
            if(position==0)
            {
                try {
                    if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("modules.manager")==true) {
                        managerSwitch = new Switch(context);
                        l.addView(managerSwitch);
                        LinearLayout.LayoutParams mparams = (LinearLayout.LayoutParams) managerSwitch.getLayoutParams();
                        mparams.rightMargin = iparams.rightMargin;
                        mparams.width = 15 * Global.width / 100;
                        mparams.height = tparams.height;
                        mparams.gravity = Gravity.CENTER;
                        mparams.leftMargin = Global.width - iparams.leftMargin - iparams.width - tparams.leftMargin - tparams.width - mparams.width - mparams.rightMargin;
                        managerSwitch.setLayoutParams(mparams);
                        if(Global.loginCredentials.getBoolean("isManager")==true)
                        {
                            managerSwitch.setChecked(true);
                        }
                        else
                            managerSwitch.setChecked(false);
                        managerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                try {
                                    Global.loginCredentials.put("isManager",isChecked);
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    System.out.println("GIANNIS BUG "+e.getMessage());
                    e.printStackTrace();
                }


            }
            return bigLayout;
        }
    }

    void makeHeader()
    {
        header=new LinearLayout(this);
        header.setBackgroundColor(Color.parseColor("#f7f7f8"));
        header.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(header);
        hparams=(LinearLayout.LayoutParams)header.getLayoutParams();
        hparams.width=Global.width;
        hparams.height=Global.height/4;
        header.setLayoutParams(hparams);

        ImageView im1=new ImageView(this);
        im1.setImageBitmap(Global.grayBack);
        im1.setClickable(true);
        im1.setOnTouchListener(Global.touchListener);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        header.addView(im1);
        LinearLayout.LayoutParams im1params=(LinearLayout.LayoutParams)im1.getLayoutParams();
        im1params.leftMargin=5 * hparams.width/100;
        im1params.width=Global.grayBack.getWidth();
        im1params.height=Global.grayBack.getHeight();
        im1params.topMargin=10*hparams.height/100;
        im1.setLayoutParams(im1params);

        LinearLayout middle=new LinearLayout(this);
        middle.setOrientation(LinearLayout.VERTICAL);
        header.addView(middle);
        LinearLayout.LayoutParams mparams=(LinearLayout.LayoutParams)middle.getLayoutParams();
        mparams.leftMargin=15*hparams.width/100;
        mparams.topMargin=im1params.topMargin;
        mparams.height=hparams.height-2*mparams.topMargin;
        mparams.width=40 * hparams.width/100;
        middle.setLayoutParams(mparams);
        Bitmap bigBerson= BitmapFactory.decodeResource(getResources(),R.drawable.person);
        double ratio=bigBerson.getWidth()*1.0/bigBerson.getHeight();
        int newh=mparams.height/2;
        int neww=(int)(newh * ratio);
        bigBerson=bigBerson.createScaledBitmap(bigBerson,neww,newh,true);
        ImageView im2=new ImageView(this);
        im2.setImageBitmap(bigBerson);
        middle.addView(im2);

        TextView t1=new TextView(this);
        t1.setTextSize(Global.fontSize);
        t1.setTextColor(Color.BLACK);
        middle.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.topMargin=mparams.height/40;
        t1params.width=mparams.width;
        t1.setLayoutParams(t1params);



        TextView t2=new TextView(this);
        t2.setTextSize(Global.fontSize);
        t2.setTextColor(Color.BLACK);
        middle.addView(t2);
        t2.setLayoutParams(t1params);

        try {
            t1.setText("    "+Global.loginCredentials.getJSONObject("user").getString("name"));
            t2.setText(Global.loginCredentials.getJSONObject("user").getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        t1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        t2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        LinearLayout right=new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        header.addView(right);
        LinearLayout.LayoutParams rparams=(LinearLayout.LayoutParams)right.getLayoutParams();
        rparams.topMargin=mparams.topMargin;
        rparams.rightMargin=im1params.leftMargin;
        rparams.width=15*hparams.width/100;
        rparams.leftMargin=hparams.width-im1params.leftMargin-im1params.width-mparams.leftMargin-mparams.width-rparams.rightMargin-rparams.width;
        right.setLayoutParams(rparams);

        ImageView im3=new ImageView(this);
        im3.setImageBitmap(Global.grayLogout);
        im3.setClickable(true);
        im3.setOnTouchListener(Global.touchListener);
        right.addView(im3);
        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableClass.operation.terminate();
                TableClass.operation.cancel(true);
                stopService(new Intent(UserSettingsActivity.this,AdminService.class));
                Global.clearData();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        TextView t3=new TextView(this);
        t3.setTextSize(Global.fontSize);
        t3.setTextColor(Color.parseColor("#272f3d"));
        t3.setText(getString(R.string.logoutText));
        right.addView(t3);

    }

    void updateUser(int userid, final JSONObject object)
    {

        JSONObject body=new JSONObject();
        try {
            body.put("username",object.getString("username"));
            body.put("password",Global.md5(object.getString("password")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.PUT,
                Global.server +"/users/"+userid+"/credentials", body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                JSONObject user= null;
                try {
                    user = Global.loginCredentials.getJSONObject("user");
                    user.put("username",object.get("username"));
                    user.put("password",object.get("password"));
                    Global.loginCredentials.put("user",user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Global.showAlert(getString(R.string.executionProblemText)+" Update User ",UserSettingsActivity.this);


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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObejct);

    }
    ArrayList<String> t=new ArrayList<String>();
    void makeOptions()
    {
        options=new ListView(this);

        t.add(getString(R.string.switchToManagerText));
        try {
            t.add(getString(R.string.currentTerminalText)+Global.loginCredentials.getString("terminalname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        t.add(getString(R.string.createNewOrderText));
        try {
            if(Global.loginCredentials.getBoolean("inBreak")==false) {
                t.add(getString(R.string.takeAbreakText));
            }
            else
            {
                t.add(getString(R.string.endABreakText));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if(Global.loginCredentials.getBoolean("checkedIn")==false)
            {
                t.add(getString(R.string.clockin));
            }
            else
            {
                t.add(getString(R.string.clockoutText));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapter adapter=new ListAdapter(this,t);
        options.setAdapter(adapter);
        mainLayout.addView(options);
        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)options.getLayoutParams();
        params.width=Global.width;
        params.height=Global.height-hparams.height;
        options.setLayoutParams(params);
        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    /*
                    EditProfileDialog d = new EditProfileDialog(UserSettingsActivity.this);
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            EditProfileDialog dd=(EditProfileDialog)dialog;
                            if(dd.getname().length()!=0 && dd.getpassword().length()!=0)
                            {
                                System.out.println("GIANNIS Login is "+Global.loginCredentials);
                                try {
                                    JSONObject user=Global.loginCredentials.getJSONObject("user");
                                    user.put("username",dd.getname());
                                    user.put("password",dd.getpassword());

                                    JSONObject object=new JSONObject();
                                    object.put("username",dd.getname());
                                    object.put("password",dd.getpassword());
                                    updateUser(user.getInt("id"),object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });*/
                }
                else
                if(position==1)
                {
                    InternetClass.ArrayGETCall(UserSettingsActivity.this, Global.server + "/terminals", Global.getAuthorizationString(Global.loginCredentials),
                            new JSONArrayListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    SelectDeviceDialog dialog = null;
                                    try {
                                        dialog = new SelectDeviceDialog(UserSettingsActivity.this, response);
                                        dialog.show();
                                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                try {
                                                    if (((SelectDeviceDialog) dialog).getSelectedName().length() != 0) {

                                                        Global.loginCredentials.put("terminalid", ((SelectDeviceDialog) dialog).getSelectedId());
                                                        Global.loginCredentials.put("terminalname", ((SelectDeviceDialog) dialog).getSelectedName());
                                                        Global.saveData();
                                                        adapter.changeItem(1,getString(R.string.currentTerminalText)+Global.loginCredentials.getString("terminalname"));
                                                    } else System.out.println("GIANNIS NO DEVICE SELECTED");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                }
                else
                if(position==2)
                {
                    try {
                        if(Global.loginCredentials.getBoolean("inBreak")==true)
                        {
                            Global.showAlert(UserSettingsActivity.this.getString(R.string.inBreakMessage),UserSettingsActivity.this);
                        }
                        else
                        if(Global.loginCredentials.getBoolean("checkedIn")==false)
                        {
                            InternetClass.checkIn(UserSettingsActivity.this, Global.server + "/checkin", Global.getAuthorizationString(Global.loginCredentials),
                                    new JSONObjectListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                Global.loginCredentials.put("checkedIn",true);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            t.set(3,UserSettingsActivity.this.getString(R.string.clockoutText));
                                            ListAdapter adapter=new ListAdapter(UserSettingsActivity.this,t);
                                            options.setAdapter(adapter);
                                            Intent I = new Intent(UserSettingsActivity.this, NewOrderActivity.class);
                                            UserSettingsActivity.this.startActivity(I);
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Global.showAlert(getString(R.string.executionProblemText)+" Check In ",UserSettingsActivity.this);

                                        }
                                    });
                        }
                        else
                        {
                            Intent I = new Intent(UserSettingsActivity.this, NewOrderActivity.class);
                            UserSettingsActivity.this.startActivity(I);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else
                if(position==3)
                {
                    try {
                        if(Global.loginCredentials.getBoolean("checkedIn")==false)
                        {
                            //show alert
                            Global.showAlert(UserSettingsActivity.this.getString(R.string.ImpossibleBreak), UserSettingsActivity.this);
                        }
                        else
                        {
                            InternetClass.takeABreak(UserSettingsActivity.this, Global.server + "/break",
                                    Global.getAuthorizationString(Global.loginCredentials), new StringListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            String service=InternetClass.lasturl;
                                            if(service.endsWith("start")) {
                                                try {
                                                    Global.loginCredentials.put("inBreak",true);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                t.set(3,UserSettingsActivity.this.getString(R.string.endABreakText));
                                                ListAdapter adapter=new ListAdapter(UserSettingsActivity.this,t);
                                                options.setAdapter(adapter);
                                                Toast.makeText(UserSettingsActivity.this, UserSettingsActivity.this.getString(R.string.BreakStartedMessage), Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                try {
                                                    Global.loginCredentials.put("inBreak",false);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                t.set(3,UserSettingsActivity.this.getString(R.string.takeAbreakText));
                                                ListAdapter adapter=new ListAdapter(UserSettingsActivity.this,t);
                                                options.setAdapter(adapter);
                                                Toast.makeText(UserSettingsActivity.this, UserSettingsActivity.this.getString(R.string.BreakEndedMessage), Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Global.showAlert(getString(R.string.executionProblemText)+" Break",UserSettingsActivity.this);

                                        }
                                    });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                if(position==4)
                {
                    try {
                        if(Global.loginCredentials.getBoolean("checkedIn")==false)
                        {
                            InternetClass.checkIn(UserSettingsActivity.this, Global.server + "/checkin", Global.getAuthorizationString(Global.loginCredentials),
                                    new JSONObjectListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                Global.loginCredentials.put("checkedIn",true);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            t.set(4,UserSettingsActivity.this.getString(R.string.clockoutText));
                                            ListAdapter adapter=new ListAdapter(UserSettingsActivity.this,t);
                                            options.setAdapter(adapter);
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Global.showAlert(getString(R.string.executionProblemText)+" Check In",UserSettingsActivity.this);

                                        }
                                    });
                        }
                        else
                        {
                            InternetClass.ObjectPOSTCall(UserSettingsActivity.this, Global.server + "/checkout", Global.getAuthorizationString(Global.loginCredentials),
                                    new JSONObjectListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                Global.loginCredentials.put("checkedIn",false);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            t.set(4,UserSettingsActivity.this.getString(R.string.clockin));
                                            ListAdapter adapter=new ListAdapter(UserSettingsActivity.this,t);
                                            options.setAdapter(adapter);
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            OrderCloseDialog dialog=new OrderCloseDialog(UserSettingsActivity.this,getString(R.string.ImpossibleClockOut),
                                                    getString(R.string.okText),"");
                                            dialog.show();
                                        }
                                    });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        options.setDivider(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        setContentView(mainLayout);
        makeHeader();
        makeOptions();
    }

    public void onBackPressed()
    {
        //
    }
}
