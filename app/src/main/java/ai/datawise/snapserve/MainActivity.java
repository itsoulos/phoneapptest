package ai.datawise.snapserve;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;

public class MainActivity extends AppCompatActivity {

    LinearLayout mainLayout=null;
    TextView lab1=null;
    EditText loginText=null;
    EditText passwordText=null;
    Button loginButton=null;
    Button cancelButton=null;
    ProgressDialog progressBar=null;
    LinearLayout header=null;
    LinearLayout.LayoutParams headerParams;

    private final int REQUEST_WRITE_EXTERNAL_STORAGE=1000;
    private final int REQUEST_ALERT=2000;

    void goNextActivity()
    {
        progressBar.hide();
        progressBar=null;
        startService(new Intent(MainActivity.this, AdminService.class));
        Global.makeTakeoutTable();
        Intent I=new Intent(MainActivity.this,DashboardActivity.class);
        MainActivity.this.startActivityForResult(I,Global.exitCode);
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {

        if (requestCode == Global.exitCode) {

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();

        }
        else
        if(requestCode==Global.settingsCode)
        {

            InternetClass.ObjectGETCall(this, Global.server + "/auth/info",
                    new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("authMethod").equals("pin"))
                                    displayPinLogin(response.getJSONArray("users"));
                                else
                                    displayPasswordLogin();
                            } catch (JSONException e) {
                                Global.showAlert("ERROR ON SETTINFS",MainActivity.this);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Global.showAlert("ERROR "+error,MainActivity.this);

                        }
                    });
        }
    }

    void getColors()
    {
        InternetClass.ArrayGETCall(this, Global.server +
                "/colors", Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                Global.colors=response;
                goNextActivity();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.hide();
                Global.showAlert(getString(R.string.executionProblemText)+" Get Setup",MainActivity.this);

            }
        });
    }

    void getWorkDate()
    {
        InternetClass.ArrayGETCall(this, Global.server +
                "/setup/options?k=store.workdate", Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    Global.workdate=response.getJSONObject(0).getString("optionValue");
                } catch (JSONException e) {
                    progressBar.hide();
                    Global.showAlert(getString(R.string.executionProblemText)+" Get Setup",MainActivity.this);
                    e.printStackTrace();
                }
                getColors();
            }

            @Override
            public void onErrorResponse(VolleyError error) {


                progressBar.hide();
                Global.showAlert(getString(R.string.executionProblemText)+" Get Setup",MainActivity.this);

            }
        });
    }

    void getGrossOrNet()
    {
        InternetClass.ArrayGETCall(this, Global.server +
                "/setup/options?k=store.order.items.price.view", Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {

                System.out.println("GIANNIS NET IS "+response);
                try {
                    Global.grossornetoption=response.getJSONObject(0).getString("optionValue");
                } catch (JSONException e) {
                    progressBar.hide();
                    Global.showAlert(getString(R.string.executionProblemText)+" Get Setup",MainActivity.this);
                    e.printStackTrace();
                }
                getWorkDate();
            }

            @Override
            public void onErrorResponse(VolleyError error) {


                progressBar.hide();
                Global.showAlert(getString(R.string.executionProblemText)+" Get Setup",MainActivity.this);

            }
        });
    }

    void getUsers()
    {

        InternetClass.ArrayGETCall(this,
                Global.server + "/users",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.users=response;
                        getGrossOrNet();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Setup",MainActivity.this);

                    }
                });

    }

    void getPaymentMethods()
    {
        InternetClass.ArrayGETCall(this,
                Global.server + "/paymentMethods",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.paymentMethods=response;
                        getUsers();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Payment",MainActivity.this);

                    }
                });
    }


    void getPagers()
    {
        InternetClass.ArrayGETCall(this,
                Global.server + "/pagers",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.pagers=response;
                        getPaymentMethods();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Pagers",MainActivity.this);

                    }
                });
    }

    void getTypes()
    {
        InternetClass.ArrayGETCall(this,
                Global.server + "/inventory/unitTypes",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.unitTypes=response;
                        getPagers();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Unit Types",MainActivity.this);

                    }
                });
    }

    void getCurrency() throws JSONException
    {
        InternetClass.ArrayGETCall(this, Global.server +
                "/setup/options?k=store.mode.currency", Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    Global.currency=response.getJSONObject(0).getString("optionValue");
                    Currency currency =Currency.getInstance(Global.currency);// Currency.getInstance(Locale.getDefault());
                    Global.currency = currency.getSymbol();
                } catch (JSONException e) {
                    Global.showAlert(getString(R.string.executionProblemText)+" Get Currency",MainActivity.this);

                    e.printStackTrace();
                }
                getTypes();
            }

            @Override
            public void onErrorResponse(VolleyError error) {


                progressBar.hide();
                Global.showAlert(getString(R.string.executionProblemText)+" Get Currency",MainActivity.this);

            }
        });
    }

    void getExtras()
    {
        InternetClass.ArrayGETCall(this, Global.server + "/extras/options",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.productExtras=response;
                        for (int i = 0; i < Global.productCategories.length(); i++) {
                            JSONObject x = null;
                            try {
                                x = Global.productCategories.getJSONObject(i);
                                if (x.getBoolean("isExtra") == true)
                                    Global.extraItems.put(x);
                            } catch (JSONException e) {
                                System.out.println("GIANNIS ERROR IN JSON "+e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        try {
                            getCurrency();
                        } catch (JSONException e) {
                            progressBar.hide();
                            Global.showAlert(getString(R.string.executionProblemText)+" Get Extras",MainActivity.this);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Extras",MainActivity.this);
                    }
                });
    }

    void getProductsInPriceLists(int id)
    {
        Global.productsInPriceLists=new JSONArray();
        InternetClass.ArrayGETCall(this, Global.server + "/priceLists/active/items",
                Global.getAuthorizationString(Global.loginCredentials),
                new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.productsInPriceLists=response;
                        getExtras();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get active products ",MainActivity.this);


                    }
                });
    }

    void getPriceLists()
    {
        InternetClass.ArrayGETCall(this, Global.server + "/priceLists",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.priceList=response;
                        Global.activeCatalogueId=0;
                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject xx=response.getJSONObject(i);
                                if(xx.getBoolean("active")==true)
                                {
                                    Global.activeCatalogueId=xx.getInt("id");
                                    break;
                                }
                            } catch (JSONException e) {
                                progressBar.hide();
                                Global.showAlert(getString(R.string.executionProblemText)+" Get Pricelists",MainActivity.this);
                                e.printStackTrace();
                            }

                        }
                        getProductsInPriceLists(Global.activeCatalogueId);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Pricelists",MainActivity.this);


                    }
                });
    }

    void getProductItems()
    {
        InternetClass.ArrayGETCall(this, Global.server + "/items", Global.getAuthorizationString(Global.loginCredentials),
                new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.productItems=response;
                        getPriceLists();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Items",MainActivity.this);

                    }
                });
    }

    void getCategories()
    {
        InternetClass.ArrayGETCall(this, Global.server + "/categories", Global.getAuthorizationString(Global.loginCredentials),
                new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.productCategories=new JSONArray();
                        Global.innerProductCategories=new JSONArray();
                        Global.extraItems=new JSONArray();
                        Global.tableProductCategories=new JSONArray();
                        Global.takeoutProductCategories=new JSONArray();
                        for(int i=0;i<response.length();i++)
                        {


                            try {
                                JSONObject xx=response.getJSONObject(i);

                                if(xx.getBoolean("active")==false) continue;
                                boolean v=xx.getBoolean("isGroupCategory");
                                if(xx.getInt("parentCategoryId")==0 && xx.getBoolean("isExtra")==false
                                ) {
                                    Global.productCategories.put(xx);
                                    if(xx.has("inTables") && xx.getBoolean("inTables")==true)
                                        Global.tableProductCategories.put(xx);
                                    if(xx.has("inTakeaway") && xx.getBoolean("inTakeaway")==true)
                                        Global.takeoutProductCategories.put(xx);
                                }
                                else
                                if(!xx.getBoolean("isExtra"))
                                    Global.innerProductCategories.put(xx);
                                if(xx.getBoolean("isExtra"))
                                    Global.extraItems.put(xx);
                            } catch (JSONException e) {
                                progressBar.hide();
                                Global.showAlert(getString(R.string.executionProblemText)+" Get Categories",MainActivity.this);
                                e.printStackTrace();
                            }
                        }

                        getProductItems();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.hide();
                        Global.showAlert(getString(R.string.executionProblemText)+" Get Categories",MainActivity.this);

                    }
                });
    }


    void getLevels2()
    {
        InternetClass.ArrayGETCall(this, Global.server + "/levels", Global.getAuthorizationString(Global.loginCredentials),
                new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Global.levels=response;
                        getCategories();

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.hide();

                        Global.showAlert(getString(R.string.executionProblemText)+" Get Levels",MainActivity.this);

                    }
                });
    }

    void getLevels()
    {
        if(Global.loginCredentials.has("terminalid"))
        {

            getLevels2();
        }
        else {
            if(progressBar!=null) progressBar.hide();
            InternetClass.ArrayGETCall(this, Global.server + "/terminals", Global.getAuthorizationString(Global.loginCredentials),
                    new JSONArrayListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            SelectDeviceDialog dialog = null;
                            try {
                                dialog = new SelectDeviceDialog(MainActivity.this, response);
                                dialog.show();
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        try {
                                            if (((SelectDeviceDialog) dialog).getSelectedName().length() != 0) {

                                                Global.loginCredentials.put("terminalid", ((SelectDeviceDialog) dialog).getSelectedId());
                                                Global.loginCredentials.put("terminalname", ((SelectDeviceDialog) dialog).getSelectedName());
                                                Global.saveData();
                                                displayProgressBar();
                                                getLevels2();
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
    }

    void makeHeaderView()
    {
        header=new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(Color.parseColor(Global.headerColor));
        mainLayout.addView(header);
        headerParams=(LinearLayout.LayoutParams)header.getLayoutParams();
        headerParams.height=Global.headerHeight;
        headerParams.width=Global.width;
        header.setLayoutParams(headerParams);

        ImageView snap=new ImageView(this);
        snap.setImageBitmap(Global.dialogIcon);
        header.addView(snap);
        LinearLayout.LayoutParams s1params=(LinearLayout.LayoutParams)snap.getLayoutParams();
        s1params.width=Global.dialogIcon.getWidth();
        s1params.height=80 * headerParams.height/100;
        s1params.gravity=Gravity.CENTER_VERTICAL;
        s1params.leftMargin=1 * Global.width/100;
        snap.setLayoutParams(s1params);

        TextView versionNumber=new TextView(this);
        versionNumber.setTextSize(Global.bigFontSize);
        versionNumber.setTextColor(Color.WHITE);
        versionNumber.setText("v."+Global.version);
        header.addView(versionNumber);
        LinearLayout.LayoutParams s2params=(LinearLayout.LayoutParams)versionNumber.getLayoutParams();
        s2params.height=s1params.height;
        s2params.gravity=Gravity.CENTER_VERTICAL;
        s2params.width=14 * Global.getMeasureWidth(this,versionNumber.getText().toString(),Global.bigFontSize)/7;
        versionNumber.setLayoutParams(s2params);
        versionNumber.setGravity(Gravity.CENTER);

        lab1=new TextView(this);
        lab1.setText(getString(R.string.login_label));
        lab1.setTextColor(Color.WHITE);
        lab1.setTextSize(Global.bigFontSize);
        header.addView(lab1);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)lab1.getLayoutParams();
        lparams.leftMargin=7 *headerParams.width/100;
        lparams.topMargin=2*headerParams.height/10;
        lparams.width=headerParams.width/4;
        int d=8 * Global.getMeasureWidth(this,lab1.getText().toString(),Global.bigFontSize)/7;
        if(d>lparams.width) lparams.width=d;
        lparams.height=headerParams.height-2*lparams.topMargin;
        lab1.setLayoutParams(lparams);
        lab1.setGravity(Gravity.CENTER);


        ImageView im=new ImageView(this);
        im.setImageBitmap(Global.settingsIcon);
        im.setClickable(true);
        im.setOnTouchListener(Global.touchListener);
        header.addView(im);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        iparams.width=Global.settingsIcon.getWidth();
        iparams.height=Global.settingsIcon.getHeight();
        iparams.gravity=Gravity.CENTER_VERTICAL;
        iparams.rightMargin=4*lparams.leftMargin/6;
        iparams.leftMargin=headerParams.width-lparams.leftMargin-lparams.width-iparams.width-iparams.rightMargin
                -s1params.width-s1params.leftMargin-s2params.width;
        im.setLayoutParams(iparams);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I=new Intent(MainActivity.this,SettingsActivity.class);
                startActivityForResult(I,Global.settingsCode);
            }
        });
    }

    void makeInputs()
    {
        TextInputLayout tl1=new TextInputLayout(this);
        mainLayout.addView(tl1);
        LinearLayout.LayoutParams tl1params=(LinearLayout.LayoutParams)tl1.getLayoutParams();
        tl1params.leftMargin=(Global.width-Global.buttonWidth)/2;
        tl1params.width=Global.buttonWidth;
        tl1params.topMargin=Global.height/8;
        tl1.setLayoutParams(tl1params);

        loginText=new EditText(this);
        loginText.setInputType(InputType.TYPE_CLASS_TEXT);
        loginText.setHintTextColor(Color.parseColor(Global.hintColor));
        loginText.setHint(getString(R.string.login_placeholder));
        loginText.setBackgroundResource(R.drawable.roundedittext);
        loginText.setTextColor(Color.parseColor(Global.textColor));
        tl1.addView(loginText);
        loginText.setPadding(Global.width/30,Global.buttonHeight/4,Global.width/30,Global.buttonHeight/4);

        TextInputLayout tl2=new TextInputLayout(this);
        mainLayout.addView(tl2);
        LinearLayout.LayoutParams tl2params=(LinearLayout.LayoutParams)tl2.getLayoutParams();
        tl2params.leftMargin=tl1params.leftMargin;
        tl2params.width=tl1params.width;
        tl2params.topMargin=tl1params.topMargin/5;
        tl2.setLayoutParams(tl2params);

        passwordText=new EditText(this);
        passwordText.setHint(getString(R.string.password_placeholder));
        passwordText.setHintTextColor(Color.parseColor(Global.hintColor));
        passwordText.setTextColor(Color.parseColor(Global.textColor));
        passwordText.setBackgroundResource(R.drawable.roundedittext);
        tl2.addView(passwordText);
        passwordText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        passwordText.setPadding(Global.width/30,Global.buttonHeight/4,Global.width/30,Global.buttonHeight/4);
    }

    void displayProgressBar()
    {
        progressBar=new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage(MainActivity.this.getString(R.string.applicationStartingText));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
    }

    void makeButton()
    {
        loginButton=new Button(this);
        loginButton.setText(getString(R.string.loginbutton_text));
        loginButton.setBackgroundResource(R.drawable.roundyellowbutton);
        loginButton.setTextColor(Color.parseColor(Global.textColor));
        mainLayout.addView(loginButton);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)loginButton.getLayoutParams();
        lparams.height=Global.buttonHeight;
        lparams.leftMargin=(Global.width-Global.buttonWidth)/2;
        lparams.width=Global.buttonWidth;
        lparams.topMargin=Global.height/8;
        loginButton.setLayoutParams(lparams);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Global.checkConnection(MainActivity.this)==false)
                {
                    Global.showAlert(getString(R.string.noNetwork),MainActivity.this);
                }
                else
                if(loginText.getText().length()==0 || passwordText.getText().length()==0)
                {
                    Global.showAlert(getString(R.string.emptyFields),MainActivity.this);
                }
                else
                {
                    final JSONObject xx = new JSONObject();
                    try {
                        xx.put("username", loginText.getText().toString());
                        xx.put("password", passwordText.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    InternetClass.ObjectPOSTCall(MainActivity.this, Global.server + "/login",
                            Global.getAuthorizationString(xx), new JSONObjectListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Global.loginCredentials=response;
                                        Global.loginCredentials.put("username",loginText.getText().toString());
                                        Global.loginCredentials.put("password",passwordText.getText().toString());
                                        Global.loginCredentials.put("hasLogout",false);
                                        Global.loginCredentials.put("token",response.getString("token"));
                                        Global.loginCredentials.put("server",Global.server);
                                        //Global.loginCredentials.put("checkedIn",false);
                                        //Global.loginCredentials.put("inBreak",false);

                                        if(response.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("modules.manager")==true)
                                        {
                                            Global.loginCredentials.put("isManager",true);
                                        }
                                        else
                                        {
                                            Global.loginCredentials.put("isManager",false);
                                        }
                                        Global.saveData();
                                        getLevels();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(progressBar!=null)
                                        progressBar.hide();
                                    Global.showAlert(getString(R.string.wrongCredentials),MainActivity.this);
                                }
                            });

                }
            }
        });
    }


    TextView selectText;
    ListView usersList=null;
    DialPad dialPad;

    void displayPinLogin(final JSONArray users)
    {
        Global.showProgess(this);
        if(usersList!=null)
        {
            final UserslistAdapter adapter=new UserslistAdapter(this,users);
            usersList.setAdapter(adapter);
            Global.hideProgress();
            return;
        }
        if(loginText!=null)
        {
            mainLayout.removeAllViews();
            makeHeaderView();
        }
        Global.hideProgress();
        selectText=new TextView(this);
        selectText.setTextSize(Global.bigFontSize);
        selectText.setTextColor(Color.parseColor(Global.textColor));
        selectText.setGravity(Gravity.CENTER);
        selectText.setText(getString(R.string.selectUserText));
        selectText.setTypeface(Typeface.DEFAULT_BOLD);
        mainLayout.addView(selectText);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)selectText.getLayoutParams();
        sparams.topMargin=2 * Global.height/100;
        sparams.height=150 * Global.getMeasuredHeight(this,selectText.getText().toString(),Global.bigFontSize)/100;
        selectText.setLayoutParams(sparams);


        usersList=new ListView(this);
        mainLayout.addView(usersList);
        final UserslistAdapter adapter=new UserslistAdapter(this,users);
        usersList.setAdapter(adapter);
        LinearLayout.LayoutParams uparams=(LinearLayout.LayoutParams)usersList.getLayoutParams();
        uparams.topMargin=sparams.topMargin;
        uparams.width=Global.width;
        uparams.height=25 * Global.height/100;
        usersList.setLayoutParams(uparams);
        usersList.setScrollbarFadingEnabled(false);

        dialPad=new DialPad(this,getString(R.string.password_placeholder),"","",98*Global.width/100,40 * Global.height/100);
        dialPad.enablePassword();
        mainLayout.addView(dialPad);

        LinearLayout.LayoutParams dparams=(LinearLayout.LayoutParams)dialPad.getLayoutParams();
        dparams.topMargin=uparams.topMargin;
        dparams.leftMargin=1*Global.width/100;
        dparams.height=40 * Global.height/100;
        dparams.gravity=Gravity.CENTER;
        dialPad.setLayoutParams(dparams);

        Button loginButton=new Button(this);
        loginButton.setBackgroundResource(R.drawable.roundyellowbutton);
        loginButton.setTextSize(Global.fontSize);
        loginButton.setText(getString(R.string.loginbutton_text));
        mainLayout.addView(loginButton);

        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)loginButton.getLayoutParams();
        lparams.bottomMargin=5 * Global.height/100;
        lparams.width=2*Global.buttonWidth/4;
        lparams.topMargin=5 * Global.height/100;
        lparams.leftMargin=40*(Global.width-lparams.width)/100;
        lparams.height=Global.height-sparams.topMargin-sparams.height-uparams.topMargin-uparams.height-dparams.topMargin-dparams.height-
                lparams.bottomMargin-lparams.topMargin;
        if(lparams.height>2 * Global.buttonHeight/3)
            lparams.height=2 * Global.buttonHeight/3;
        loginButton.setLayoutParams(lparams);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialPad.getInputValue().length()==0)
                {
                    Global.showAlert(getString(R.string.emptyFields), MainActivity.this);
                }
                else
                {
                    final JSONObject xx = new JSONObject();
                    try {
                        xx.put("username", users.getString( adapter.getSelected()));
                        xx.put("password", dialPad.getInputValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    InternetClass.ObjectPOSTCall(MainActivity.this, Global.server + "/login",
                            Global.getAuthorizationString(xx), new JSONObjectListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Global.loginCredentials=response;
                                        Global.loginCredentials.put("username",users.getString( adapter.getSelected()));
                                        Global.loginCredentials.put("password",dialPad.getInputValue());
                                        Global.loginCredentials.put("hasLogout",false);
                                        Global.loginCredentials.put("token",response.getString("token"));
                                        Global.loginCredentials.put("server",Global.server);

                                        //Global.loginCredentials.put("checkedIn",false);
                                        //Global.loginCredentials.put("inBreak",false);

                                        if(response.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("modules.manager")==true)
                                        {
                                            Global.loginCredentials.put("isManager",true);
                                        }
                                        else
                                        {
                                            Global.loginCredentials.put("isManager",false);
                                        }
                                        Global.saveData();
                                        getLevels();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    if(progressBar!=null)
                                        progressBar.hide();
                                    Global.showAlert(getString(R.string.wrongCredentials),MainActivity.this);
                                }
                            });
                }
            }
        });
        tryLogin();
    }

    void displayPasswordLogin()
    {
        Global.showProgess(this);
        if(usersList!=null)
        {
            mainLayout.removeAllViews();
            makeHeaderView();
            makeInputs();
            makeButton();
        }
        if(loginText==null) {
            makeInputs();
            makeButton();
        }
        Global.hideProgress();
        tryLogin();
    }

    void tryLogin()
    {

        try {
            System.out.println("GIANNIS ROLES "+Global.loginCredentials.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);


        }
        if (!Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1999);
        }



        if(Global.hasOfflineData() && Global.loginCredentials.has("username"))
        {
            Global.loadData();
            try {
                Global.server=Global.loginCredentials.getString("server");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            displayProgressBar();
            InternetClass.ObjectPOSTCall(MainActivity.this, Global.server + "/auth/status",
                    Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getBoolean("valid")==true) {

                                    getLevels();
                                }
                                else
                                {
                                    progressBar.hide();
                                    Global.showAlert(getString(R.string.renterData),MainActivity.this);
                                }
                            } catch (JSONException e) {
                                System.out.println("GIANNIS ERROR IN JSON "+e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressBar.hide();
                            Global.showAlert(getString(R.string.wrongCredentials),MainActivity.this);
                        }
                    });
        }
    }
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        setContentView(mainLayout);

        InternetClass.makeQueue(this);

        makeHeaderView();

        if(Global.hasOfflineData())
        {
            Global.loadData();
            try {
                Global.server=Global.loginCredentials.getString("server");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(Global.server.length()==0)
        {
            //go settings
            Intent I=new Intent(MainActivity.this,SettingsActivity.class);
            startActivityForResult(I,Global.settingsCode);

        }
        else
            InternetClass.ObjectGETCall(this, Global.server + "/auth/info",
                    new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("authMethod").equals("pin"))
                                    displayPinLogin(response.getJSONArray("users"));
                                else
                                    displayPasswordLogin();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Global.showAlert("ERROR "+error,MainActivity.this);
                        }
                    });
    }

    protected void onResume() {
        super.onResume();
    }
}