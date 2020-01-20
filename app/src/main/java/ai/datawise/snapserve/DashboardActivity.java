package ai.datawise.snapserve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {

    LinearLayout mainLayout=null;
    LinearLayout header=null;
    LinearLayout.LayoutParams headerParams;

    ImageView rightHeaderButton;
    boolean rightHeaderState=true;
    BottomNavigationView bottom;
    LinearLayout overlay=null;
    PopupWindow overlayWindow=null;
    TabAdapter adapter=null;

    TextView t2=null;
    TextView t3=null;
    ImageView IconBreak=null;
    ImageView IconClock=null;

     class DeactivatedViewPager extends ViewPager {

        public DeactivatedViewPager (Context context) {
            super(context);
        }

        public DeactivatedViewPager (Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean canScrollHorizontally(int direction) {
            return false;
        }
    }


    ViewPager pager=null;

    void clockin()
    {
        t3.setText(getString(R.string.clockoutText));
        IconBreak.setClickable(true);
        IconBreak.setImageBitmap(Global.breakIcon);
        IconBreak.setOnTouchListener(Global.touchListener);
        try {
            Global.loginCredentials.put("checkedIn",true);
            Global.saveData();
        } catch (JSONException e) {
            Global.showAlert(getString(R.string.executionProblemText)+"Checked In",this);
            e.printStackTrace();
        }
    }

    void clockout()
    {
        t3.setText(getString(R.string.clockin));
        try {
            Global.loginCredentials.put("checkedIn",false);
            Global.saveData();
        } catch (JSONException e) {
            Global.showAlert(getString(R.string.executionProblemText)+"Checked In",this);

            e.printStackTrace();
        }
        IconBreak.setClickable(false);
        IconBreak.setImageBitmap(Global.coffeGray);
        IconBreak.setOnTouchListener(null);
        IconBreak.setOnTouchListener(null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Global.takeoutExitCode) {
            pager.setAdapter(adapter);
            bottom.setSelectedItemId(R.id.tabtakeout);
            pager.setCurrentItem(adapter.setItem(R.id.tabtakeout));

        } else if (requestCode == Global.exitCode) {

            if (resultCode == Activity.RESULT_OK) {
                //fetch again tables
                pager.setAdapter(adapter);

                bottom.setSelectedItemId(R.id.tabtables);
                pager.setCurrentItem(adapter.setItem(R.id.tabtables));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        }
    }

    void makeHeaderView()
    {
        header=new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(Color.parseColor(Global.headerColor));
        mainLayout.addView(header);
        headerParams=(LinearLayout.LayoutParams)header.getLayoutParams();
        headerParams.width=Global.width;
        headerParams.height=80*Global.headerHeight/100;
        header.setLayoutParams(headerParams);

        ImageView im1=new ImageView(this);
        im1.setImageBitmap(Global.bellIcon);
        header.addView(im1);
        LinearLayout.LayoutParams imparams=(LinearLayout.LayoutParams)im1.getLayoutParams();
        imparams.leftMargin=5 * headerParams.width/100;
        imparams.topMargin=20 * headerParams.height/100;
        imparams.width=Global.bellIcon.getWidth();
        imparams.height=Global.bellIcon.getHeight();
        im1.setLayoutParams(imparams);

        ImageView im2=new ImageView(this);
        im2.setImageBitmap(Global.personIcon);
        header.addView(im2);
        LinearLayout.LayoutParams im2params=(LinearLayout.LayoutParams)im2.getLayoutParams();
        im2params.topMargin=imparams.topMargin;
        im2params.width=Global.personIcon.getWidth();
        im2params.height=Global.personIcon.getHeight();
        im2params.leftMargin=3 * imparams.leftMargin;
        im2.setLayoutParams(im2params);
        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I=new Intent(DashboardActivity.this,UserSettingsActivity.class);
                DashboardActivity.this.startActivityForResult(I,Global.exitCode);
            }
        });

        TextView t1=new TextView(this);
        t1.setTextColor(Color.WHITE);
        t1.setTextSize(Global.fontSize);
        try {
            t1.setText(Global.loginCredentials.getJSONObject("user").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        header.addView(t1);
        t1.setClickable(true);
        t1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView)v).setTextColor(Color.BLACK);
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView)v).setTextColor(Color.WHITE);
                        break;
                }
                return false;
            }
        });
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I=new Intent(DashboardActivity.this,UserSettingsActivity.class);
                DashboardActivity.this.startActivityForResult(I,Global.exitCode);
            }
        });
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t1.getLayoutParams();
        tparams.leftMargin=imparams.leftMargin;
        tparams.topMargin=imparams.topMargin;
        int d=8*Global.getMeasureWidth(this,t1.getText().toString(),Global.fontSize)/7;
        tparams.height=headerParams.height-2*tparams.topMargin;
        tparams.width=headerParams.width/3;
        if(d>tparams.width) tparams.width=d;
        t1.setLayoutParams(tparams);


        rightHeaderButton=new ImageView(this);
        rightHeaderButton.setImageBitmap(Global.yellowIcon);
        rightHeaderButton.setClickable(true);
        rightHeaderButton.setOnTouchListener(Global.touchListener);
        header.addView(rightHeaderButton);
        LinearLayout.LayoutParams yparams=(LinearLayout.LayoutParams)rightHeaderButton.getLayoutParams();
        yparams.topMargin=imparams.topMargin;
        yparams.rightMargin=imparams.leftMargin;
        yparams.height=Global.yellowIcon.getHeight();
        yparams.width=Global.yellowIcon.getWidth();
        yparams.leftMargin=headerParams.width-imparams.leftMargin-imparams.width-im2params.leftMargin-im2params.width-tparams.leftMargin-
                tparams.width-yparams.rightMargin-yparams.width;
        rightHeaderButton.setLayoutParams(yparams);
        rightHeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(overlayWindow.isShowing())
                {
                    overlayWindow.dismiss();
                }
                else
                {

                    try {
                        if (!Global.loginCredentials.has("checkedIn")
                                ||Global.loginCredentials.getBoolean("checkedIn") == false) {
                            t3.setText(getString(R.string.clockin));

                            IconBreak.setClickable(false);
                            IconBreak.setImageBitmap(Global.coffeGray);
                            IconBreak.setOnTouchListener(null);
                            IconBreak.setOnTouchListener(null);

                        }
                        else
                        {
                            t3.setText(getString(R.string.clockoutText));
                            IconBreak.setClickable(true);
                            IconBreak.setImageBitmap(Global.breakIcon);
                            IconBreak.setOnTouchListener(Global.touchListener);

                        }
                    } catch (JSONException e) {
                        Global.showAlert(getString(R.string.executionProblemText)+"Checked In",DashboardActivity.this);

                        e.printStackTrace();
                    }
                    try {
                        if((Global.loginCredentials.has("inBreak") &&
                                Global.loginCredentials.getBoolean("inBreak")==true))
                        {
                            t2.setText(DashboardActivity.this.getString(R.string.endABreakText));
                        }
                        else
                            t2.setText(DashboardActivity.this.getString(R.string.takeAbreakText));
                    } catch (JSONException e) {
                        Global.showAlert(getString(R.string.executionProblemText)+" Break",DashboardActivity.this);

                        e.printStackTrace();
                    }

                    overlayWindow.showAtLocation(mainLayout, Gravity.CENTER,0,0);
                }
            }
        });
    }

    void createOverlay()
    {
        overlayWindow=new PopupWindow(mainLayout,Global.width,Global.height);

        overlay=new LinearLayout(this);
        overlayWindow.setContentView(overlay);

        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        overlay.addView(l);
        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)l.getLayoutParams();
        params.width=Global.width;
        params.height=Global.height;
        l.setLayoutParams(params);
        l.setBackgroundColor(Color.parseColor("#E8ffffff"));

        LinearLayout.LayoutParams fparams=new LinearLayout.LayoutParams(params.width,params.height);
        fparams.topMargin=Global.headerHeight;

        LinearLayout line1=new LinearLayout(this);
        line1.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(line1);
        ImageView im=new ImageView(this);
        line1.addView(im);
        im.setImageBitmap(Global.xicon);
        im.setClickable(true);
        im.setOnTouchListener(Global.touchListener);
        LinearLayout.LayoutParams imparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        imparams.width=Global.xicon.getWidth();
        imparams.height=Global.xicon.getHeight();
        imparams.topMargin=3*(20 * headerParams.height/100)/4;
        imparams.rightMargin=5 * Global.width/100;
        imparams.leftMargin=Global.width-imparams.width-imparams.rightMargin;
        im.setLayoutParams(imparams);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayWindow.dismiss();
            }
        });

        LinearLayout l1=new LinearLayout(this);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(l1);
        LinearLayout.LayoutParams l1params=(LinearLayout.LayoutParams)l1.getLayoutParams();
        l1params.width=params.width;
        l1params.height=  headerParams.height;
        l1params.topMargin=30 * Global.headerHeight/100;
        l1.setLayoutParams(l1params);

        TextView t1=new TextView(this);
        t1.setTextSize(Global.fontSize);
        t1.setTextColor(Color.parseColor("#de000000"));
        t1.setText(getString(R.string.createNewOrderText));
        l1.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=l1params.width/2;
        t1params.width=30 * l1params.width/100;
        t1params.topMargin=2*l1params.height/10;
        t1.setLayoutParams(t1params);
        t1.setGravity(Gravity.RIGHT);


        ImageView im1=new ImageView(this);
        im1.setImageBitmap(Global.yellowIcon);
        l1.addView(im1);
        im1.setClickable(true);
        im1.setOnTouchListener(Global.touchListener);
        LinearLayout.LayoutParams im1params=(LinearLayout.LayoutParams)im1.getLayoutParams();
        im1params.topMargin=t1params.topMargin/4;
        im1params.width=Global.yellowIcon.getWidth();
        im1params.height=Global.yellowIcon.getHeight();
        im1params.rightMargin=5 * l1params.width/100;
        im1params.leftMargin=l1params.width-t1params.leftMargin-t1params.width-im1params.rightMargin-im1params.width;
        im1.setLayoutParams(im1params);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(Global.loginCredentials.getBoolean("inBreak")==true)
                    {
                        OrderCloseDialog dialog=new OrderCloseDialog(DashboardActivity.this,
                                DashboardActivity.this.getString(R.string.inBreakMessage),
                                DashboardActivity.this.getString(R.string.okGotItText),"");
                        dialog.show();
                    }
                    else
                    if (Global.loginCredentials.getBoolean("checkedIn") == false) {
                        InternetClass.checkIn(DashboardActivity.this, Global.server + "/checkin", Global.getAuthorizationString(Global.loginCredentials),
                                new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        clockin();
                                        Intent I = new Intent(DashboardActivity.this, NewOrderActivity.class);
                                        DashboardActivity.this.startActivityForResult(I,Global.exitCode);
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Global.showAlert(getString(R.string.executionProblemText)+"Checked In",DashboardActivity.this);

                                    }
                                });
                    }
                    else
                    {
                        Intent I = new Intent(DashboardActivity.this, NewOrderActivity.class);
                        DashboardActivity.this.startActivityForResult(I,Global.exitCode);
                    }
                } catch(JSONException e){
                    Global.showAlert(getString(R.string.executionProblemText)+"Checked In",DashboardActivity.this);

                    e.printStackTrace();
                }

            }
        });

        LinearLayout l2=new LinearLayout(this);
        l2.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(l2);
        LinearLayout.LayoutParams l2params=(LinearLayout.LayoutParams)l2.getLayoutParams();
        l2params.width=params.width;
        l2params.height=  headerParams.height;
        l2params.topMargin=2 * Global.headerHeight/100;
        l2.setLayoutParams(l2params);

        t2=new TextView(this);
        t2.setTextSize(Global.fontSize);
        t2.setTextColor(t1.getCurrentTextColor());
        t2.setText(getString(R.string.takeAbreakText));
        l2.addView(t2);
        t2.setLayoutParams(t1params);
        t2.setGravity(Gravity.RIGHT);


        IconBreak=new ImageView(this);
        IconBreak.setImageBitmap(Global.breakIcon);
        l2.addView(IconBreak);
        IconBreak.setLayoutParams(im1params);
        IconBreak.setClickable(true);
        IconBreak.setOnTouchListener(Global.touchListener);
        IconBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!Global.loginCredentials.has("checkedIn")
                            ||Global.loginCredentials.getBoolean("checkedIn") == false) {
                        OrderCloseDialog dialog=new OrderCloseDialog(DashboardActivity.this,
                                DashboardActivity.this.getString(R.string.ImpossibleClockOut),
                                DashboardActivity.this.getString(R.string.okGotItText),"");
                        dialog.show();
                    }
                    else
                    {
                        InternetClass.takeABreak(DashboardActivity.this, Global.server + "/break",
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
                                            t2.setText(DashboardActivity.this.getString(R.string.endABreakText));

                                            Toast.makeText(DashboardActivity.this, DashboardActivity.this.getString(R.string.BreakStartedMessage), Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            try {
                                                Global.loginCredentials.put("inBreak",false);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            t2.setText(DashboardActivity.this.getString(R.string.takeAbreakText));
                                            Toast.makeText(DashboardActivity.this, DashboardActivity.this.getString(R.string.BreakEndedMessage), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Global.showAlert(getString(R.string.executionProblemText)+" Break",DashboardActivity.this);
                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }});

        LinearLayout l3=new LinearLayout(this);
        l3.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(l3);
        l3.setLayoutParams(l2params);

        t3=new TextView(this);
        t3.setTextSize(Global.fontSize);
        t3.setTextColor(t1.getCurrentTextColor());
        t3.setText(getString(R.string.clockoutText));
        try {
            if (!Global.loginCredentials.has("checkedIn")
                    ||Global.loginCredentials.getBoolean("checkedIn") == false) {
                t3.setText(getString(R.string.clockin));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        l3.addView(t3);
        t3.setLayoutParams(t1params);
        t3.setGravity(Gravity.RIGHT);


        IconClock=new ImageView(this);
        IconClock.setImageBitmap(Global.breakIcon);
        l3.addView(IconClock);
        IconClock.setLayoutParams(im1params);
        IconClock.setClickable(true);
        IconClock.setOnTouchListener(Global.touchListener);
        IconClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Global.loginCredentials.getBoolean("checkedIn") == false) {
                        InternetClass.checkIn(DashboardActivity.this, Global.server + "/checkin",
                                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        clockin();
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Global.showAlert(getString(R.string.executionProblemText)+"Checked In",DashboardActivity.this);

                                    }
                                });
                    }
                    else
                    {
                        InternetClass.ObjectPOSTCall(DashboardActivity.this, Global.server + "/checkout",
                                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        clockout();
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //show alert
                                        OrderCloseDialog dialog=new OrderCloseDialog(DashboardActivity.this,
                                                DashboardActivity.this.getString(R.string.ImpossibleClockOut),
                                                DashboardActivity.this.getString(R.string.okGotItText),"");
                                        dialog.show();
                                    }
                                });
                    }
                } catch (JSONException e) {
                    Global.showAlert(getString(R.string.executionProblemText)+"Checked In",DashboardActivity.this);

                    e.printStackTrace();
                }
            }
        });
    }

    LinearLayout.LayoutParams pparams;

    @SuppressLint("WrongConstant")
    void makeBottomView()
    {
        final LinearLayout l1=new LinearLayout(this);
        l1.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(l1);
        l1.setBackgroundResource(R.drawable.backwithborder);
        final LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)l1.getLayoutParams();
        bparams.width=Global.width;
        bparams.height=Global.height-headerParams.height-pparams.topMargin-pparams.height;

        bottom=new BottomNavigationView(this);
        int usedResource=R.menu.menutab;
        try {
            JSONObject roles=Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions");
            if(roles.getBoolean("modules.mobile.dashboard")==false &&
                    roles.getBoolean("modules.mobile.inventory")==false &&
                    roles.getBoolean("orders.delivery.dispatch")==false)
                usedResource=R.menu.menutabnodashboardnoinventorynodelivery;
            else
            if(roles.getBoolean("modules.mobile.dashboard")==false
                    && roles.getBoolean("modules.mobile.inventory")==false
                    && roles.getBoolean("orders.delivery.dispatch")==true)
                usedResource=R.menu.menutabnodashboardnoinventory;
            else
            if(roles.getBoolean("modules.mobile.dashboard")==false
                    && roles.getBoolean("modules.mobile.inventory")==true
                    &&  roles.getBoolean("orders.delivery.dispatch")==false)
                usedResource=R.menu.menutabnodashboardnodelivery;
            else
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==false
                    &&  roles.getBoolean("orders.delivery.dispatch")==false)
                usedResource=R.menu.menutabnoinventorynodelivery;
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==false
                    &&  roles.getBoolean("orders.delivery.dispatch")==true)
                usedResource=R.menu.menutabnoinventory;
            if(roles.getBoolean("modules.mobile.dashboard")==true
                    && roles.getBoolean("modules.mobile.inventory")==true
                    &&  roles.getBoolean("orders.delivery.dispatch")==false)
                usedResource=R.menu.menutabnodelivery;
        } catch (JSONException ex) {
            System.out.println("GIANNIS E "+ ex.getMessage());
            ex.printStackTrace();
        }


        bottom.inflateMenu(usedResource);
        l1.addView(bottom);
        final LinearLayout.LayoutParams blparams=(LinearLayout.LayoutParams)bottom.getLayoutParams();
        blparams.topMargin=1 * Global.height/100;
        blparams.width=98 * Global.width/100;
        blparams.leftMargin=1 * Global.width/100;
        blparams.bottomMargin=1 * Global.height/100;
        blparams.gravity=Gravity.TOP;


        bottom.post(new Runnable() {
            @Override
            public void run() {
                int height = (int) bottom.getMeasuredHeight();
                bparams.height = 125 * height / 100;
                l1.setLayoutParams(bparams);
                blparams.height = height;
                bottom.setLayoutParams(blparams);

                pparams.height = Global.height - bparams.height - headerParams.height - pparams.topMargin - 20;
                pager.setLayoutParams(pparams);

                adapter = new TabAdapter(DashboardActivity.this, Global.width, 98 * pparams.height / 100);
                pager.setAdapter(adapter);
                pager.setCurrentItem(0, false);

                pager.setOnTouchListener(new View.OnTouchListener() {
                                             @Override
                                             public boolean onTouch(View v, MotionEvent event) {
                                                 int d=pager.getCurrentItem();
                                                 if (d>=0)// == PAGE || d==PAGE+1)
                                                 {
                                                 //    if(d>0)
                                                     pager.setCurrentItem(d - 1, false);
                                                     pager.setCurrentItem(d, false);
                                                     return true;
                                                 }
                                                 return false;
                                             }

                                         }
                );
            }
        });


        ColorStateList csl = ColorStateList.valueOf(getResources().getColor(R.color.tabTextColor));
        bottom.setLabelVisibilityMode(1);
        bottom.setItemTextColor(csl);

        bottom.setItemBackgroundResource(R.drawable.tabselector);
        bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.tabhome:
                        pager.setCurrentItem(adapter.setItem(R.id.tabhome),false);
                        break;
                    case R.id.tabtables:
                        pager.setCurrentItem(adapter.setItem(R.id.tabtables),false);
                        break;
                    case R.id.tabtakeout:
                        pager.setCurrentItem(adapter.setItem(R.id.tabtakeout));
                        break;
                    case R.id.tabdelivery:
                        pager.setCurrentItem(adapter.setItem(R.id.tabdelivery));
                        break;
                    case R.id.tabinventory:
                        pager.setCurrentItem(adapter.setItem(R.id.tabinventory));
                        break;
                }
                return true;
            }
        });
    }
    void getBreakAndChecked()
    {
        int userid= 0;
        try {
            userid = Global.loginCredentials.getJSONObject("user").getInt("id");
        } catch (JSONException e) {
            System.out.println("GIANNIS E "+e.getMessage());
            e.printStackTrace();
        }
        InternetClass.ObjectGETCall(DashboardActivity.this, Global.server +
                        "/users/" + userid + "/overview",
                Global.getAuthorizationString(Global.loginCredentials),
                new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("GIANNIS PUT RESPONSE IS "+response);
                            Global.loginCredentials.put("checkedIn",response.getBoolean("isCheckedIn"));
                        } catch (JSONException e) {
                            System.out.println("GIANNIS ERROR "+e.getMessage());

                            e.printStackTrace();
                        }
                        try {
                            Global.loginCredentials.put("inBreak",response.getBoolean("isInBreak"));
                        } catch (JSONException e) {
                            System.out.println("GIANNIS ERROR "+e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("GIANNIS ERROR "+error);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        setContentView(mainLayout);
        makeHeaderView();
        pager=new ViewPager(this);
        mainLayout.addView(pager);
        pparams=(LinearLayout.LayoutParams)pager.getLayoutParams();
        pparams.width=Global.width;
        pparams.topMargin=1 * Global.height/100;
        pparams.height=75 * Global.height/100;
        pager.setLayoutParams(pparams);

        //  adapter=new TabAdapter(this,Global.width,pparams.height);
        //  pager.setAdapter(adapter);
        //  pager.setCurrentItem(0);
        pager.setBackgroundResource(R.drawable.backwithborder);
        makeBottomView();
        createOverlay();
        getBreakAndChecked();
    }
}
