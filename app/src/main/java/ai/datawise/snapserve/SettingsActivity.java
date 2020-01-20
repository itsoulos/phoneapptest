package ai.datawise.snapserve;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.stealthcopter.networktools.PortScan;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout mainLayout=null;
    LinearLayout header=null;
    LinearLayout.LayoutParams headerParams;

    void makeHeaderView()
    {
        header=new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(Color.parseColor(Global.headerColor));
        mainLayout.addView(header);
        headerParams=(LinearLayout.LayoutParams)header.getLayoutParams();
        headerParams.width=Global.width;
        headerParams.height=Global.headerHeight;
        header.setLayoutParams(headerParams);

        ImageView im1=new ImageView(this);
        im1.setImageBitmap(Global.whiteBack);
        im1.setClickable(true);
        im1.setOnTouchListener(Global.touchListener);
        header.addView(im1);

        LinearLayout.LayoutParams im1params=(LinearLayout.LayoutParams)im1.getLayoutParams();
        im1params.leftMargin=5 * headerParams.width/100;
        im1params.width=Global.whiteBack.getWidth();
        im1params.height=Global.whiteBack.getHeight();
        im1params.topMargin=20*headerParams.height/100;
        im1.setLayoutParams(im1params);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView t1=new TextView(this);
        t1.setText(getString(R.string.settingsText));
        t1.setTextSize(Global.bigFontSize);
        t1.setTextColor(Color.WHITE);
        header.addView(t1);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t1.getLayoutParams();
        tparams.topMargin=im1params.topMargin;
        tparams.width=headerParams.width/5;
        tparams.leftMargin=2*im1params.leftMargin;
        int d=8 * Global.getMeasureWidth(this,t1.getText().toString(),Global.bigFontSize)/7;
        if(d>tparams.width) tparams.width=d;
        t1.setLayoutParams(tparams);
        t1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


        ImageView im2=new ImageView(this);
        im2.setImageBitmap(Global.xshape);
        im2.setClickable(true);
        im2.setOnTouchListener(Global.touchListener);
        header.addView(im2);
        LinearLayout.LayoutParams im2params=(LinearLayout.LayoutParams)im2.getLayoutParams();
        im2params.topMargin=im1params.topMargin;
        im2params.width=Global.xshape.getWidth();
        im2params.height=Global.xshape.getHeight();
        im2params.rightMargin=im1params.leftMargin;
        im2params.leftMargin=headerParams.width-im1params.leftMargin-im1params.width-tparams.leftMargin-tparams.width-im2params.rightMargin-im2params.width;
        im2.setLayoutParams(im2params);
        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    EditText hostText=null;
    Button testButton=null;
    Button saveButton=null;
    Button scanButton=null;

    void testMethod()
    {

        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.GET, hostText.getText().toString() + "/cp/health",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Global.showAlert("OK",SettingsActivity.this);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("GIANNIS ERROR "+error);
                Global.showAlert(getString(R.string.executionProblemText)+" Get Health",SettingsActivity.this);
            }
        }) ;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObejct);

    }

    void saveMethod()
    {
        if(hostText.getText().toString().length()==0)
            Global.showAlert(getString(R.string.emptyFields),this);
        else
        {

            Global.server=hostText.getText().toString();
            try {
                Global.loginCredentials.put("server",Global.server);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Global.saveData();
            Global.showAlert(getString(R.string.saveOKText),this);
        }
    }

    public String intToIp(int i) {

        return Formatter.formatIpAddress(i);
    }

    ArrayList<String> scannedIp=new ArrayList<String>();
    boolean terminateScan=false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        setContentView(mainLayout);
        makeHeaderView();

        TextInputLayout tl=new TextInputLayout(this);
        mainLayout.addView(tl);
        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)tl.getLayoutParams();
        hparams.width=Global.buttonWidth;
        hparams.height=Global.buttonHeight;
        hparams.topMargin=Global.height/8;
        hparams.leftMargin=(Global.width-hparams.width)/2;
        tl.setLayoutParams(hparams);

        hostText=new EditText(this);
        hostText.setInputType(InputType.TYPE_CLASS_TEXT);
        hostText.setHintTextColor(Color.parseColor(Global.hintColor));
        hostText.setHint(getString(R.string.hostText));
        hostText.setBackgroundResource(R.drawable.roundedittext);
        hostText.setTextColor(Color.parseColor(Global.textColor));
        tl.addView(hostText);
        hostText.setPadding(Global.width/30,Global.buttonHeight/4,Global.width/30,Global.buttonHeight/4);
        hostText.setText(Global.server);

        LinearLayout l1=new LinearLayout(this);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(l1);

        testButton=new Button(this);
        testButton.setBackgroundResource(R.drawable.transparentbutton);
        testButton.setText(getString(R.string.testText));
        testButton.setTextColor(Color.parseColor(Global.textColor));
        testButton.setTextSize(Global.fontSize);
        l1.addView(testButton);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)testButton.getLayoutParams();
        tparams.width=55*hparams.width/100;
        tparams.height=hparams.height;
        tparams.leftMargin=hparams.leftMargin/2;
        tparams.topMargin=hparams.topMargin/4;
        testButton.setLayoutParams(tparams);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMethod();
            }
        });

        scanButton=new Button(this);
        scanButton.setBackgroundResource(R.drawable.roundyellowbutton);
        scanButton.setTextColor(Color.parseColor(Global.textColor));
        scanButton.setTextSize(Global.fontSize);
        scanButton.setText(getString(R.string.scanText));
        l1.addView(scanButton);
        scanButton.setLayoutParams(tparams);

        final ArrayList<Integer> ports=new ArrayList<Integer>();
        ports.add(80);
        ports.add(8080);
        ports.add(8081);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanButton.setEnabled(false);
                scannedIp.clear();
                terminateScan=false;
                Global.showProgess(SettingsActivity.this);
                SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                    @Override
                    public void onDeviceFound(Device device) {
                        scannedIp.add(device.ip);
                    }


                    @Override
                    public void onFinished(ArrayList<Device> devicesFound) {

                        // scannedIp.add("162.213.250.174");
                        for(int i=0;i<scannedIp.size();i++)
                        {
                            if(terminateScan) break;
                            try {
                                final String myip=scannedIp.get(i);

                                PortScan.onAddress(myip).setTimeOutMillis(1000).setPorts(ports).setMethodTCP().doScan(new PortScan.PortListener() {
                                    @Override
                                    public void onResult(final int portNo, boolean open) {
                                        if (open)
                                        {
                                            InternetClass.ObjectGETCall(SettingsActivity.this, "http://"+myip + ":"+portNo+"/cp/info",
                                                    "", new JSONObjectListener() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {

                                                            hostText.setText("http://"+myip+":"+portNo);
                                                            saveMethod();
                                                            terminateScan=true;
                                                        }

                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            System.out.println("GIANNIS VOLLEY "+error);
                                                        }
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onFinished(ArrayList<Integer> openPorts) {
                                        // Stub: Finished scanning
                                    }
                                });
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                        }

                        scanButton.setEnabled(true);
                        Global.hideProgress();
                    }
                });
            }
        });

        saveButton=new Button(this);
        saveButton.setBackgroundResource(R.drawable.roundyellowbutton);
        saveButton.setTextColor(Color.parseColor(Global.textColor));
        saveButton.setTextSize(Global.fontSize);
        saveButton.setText(getString(R.string.saveText));
        mainLayout.addView(saveButton);
        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)saveButton.getLayoutParams();
        t2params.width=125*hparams.width/100;
        t2params.height=hparams.height;
        t2params.leftMargin=hparams.leftMargin/2;
        t2params.topMargin=hparams.topMargin/4;
        saveButton.setLayoutParams(t2params);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMethod();
            }
        });
    }
}
