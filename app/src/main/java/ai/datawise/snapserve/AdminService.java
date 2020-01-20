package ai.datawise.snapserve;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.AbstractThreadedSyncAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.textclassifier.TextLinks;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.GenericArrayType;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class AdminService extends IntentService {

    private static String TAG = "MyService";
    private Handler handler;
    private Runnable runnable;
    private final int runTime = 5000;
    private Context context=null;
    AlertDialog alertDialog=null;
    private int jsonid=0;
    RequestQueue queue=null;


    public  AdminService()
    {
        super("AdminService");
        context=this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    void sendResponse(int which)
    {
        final String murl=Global.server+"/auth_requests/"+jsonid;

        final JSONObject params=new JSONObject();

        try {
            params.put("approvalStatus",which);
            params.put("approvedBy",Global.loginCredentials.getJSONObject("user").getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // put your json here
        RequestBody body = RequestBody.create(JSON, params.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(murl)
                .put(body)
                .addHeader("Authorization", Global.getAuthorizationString(Global.loginCredentials))
                .build();

        Response response = null;
        try {
            com.squareup.okhttp.Response response1 = client.newCall(request).execute();
            String resStr = response1.body().string();
            System.out.println("GIANNIS RESP IS "+resStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(runnable, runTime);
                try {
                    if(Global.loginCredentials.getBoolean("isManager"))
                    {
                        InternetClass.ArrayGETCall(context, Global.server + "/auth_requests/oldest",
                                Global.getAuthorizationString(Global.loginCredentials),
                                new JSONArrayListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {

                                        if(alertDialog!=null)
                                            if(alertDialog.isShowing()) return;;
                                        if(response.length()==0) return;

                                        String username="";
                                        for(int i=0;i<Global.users.length();i++)
                                        {
                                            try {
                                                if(Global.users.getJSONObject(i).getInt("id")==response.getJSONObject(0).getInt("createdBy"))
                                                {
                                                    username=Global.users.getJSONObject(i).getString("username");
                                                    break;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        System.out.println("GIANNIS RESPONSE "+response);
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                        } else {
                                            v.vibrate(500);
                                        }
                                        String message="";
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        try {
                                            jsonid=response.getJSONObject(0).getInt("id");
                                            message=response.getJSONObject(0).getString("message");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        alertDialog = builder.create();
                                        final int dialogWidth=80 * Global.width/100;
                                        final int dialogHeight=35 * Global.height/100;

                                        LinearLayout l=new LinearLayout(context);
                                        l.setOrientation(LinearLayout.VERTICAL);
                                        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
                                        LinearLayout h=new LinearLayout(context);
                                        h.setOrientation(LinearLayout.HORIZONTAL);
                                        h.setBackgroundColor(Color.BLUE);
                                        l.addView(h);
                                        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)h.getLayoutParams();
                                        hparams.width=LinearLayout.LayoutParams.MATCH_PARENT;
                                        hparams.height=125 * Global.dialogIcon.getHeight()/100;
                                        h.setLayoutParams(hparams);
                                        ImageView icon=new ImageView(context);
                                        icon.setImageBitmap(Global.dialogIcon);
                                        h.addView(icon);

                                        TextView t=new TextView(context);
                                        t.setTextSize(Global.fontSize);
                                        t.setTextColor(Color.YELLOW);
                                        t.setText(context.getString(R.string.app_name));
                                        h.addView(t);

                                        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)icon.getLayoutParams();
                                        iparams.leftMargin=2 *dialogWidth/100;
                                        iparams.gravity=Gravity.CENTER_HORIZONTAL;
                                        iparams.width=Global.dialogIcon.getWidth();
                                        iparams.height=95 * hparams.height/100;

                                        icon.setLayoutParams(iparams);

                                        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
                                        tparams.gravity=Gravity.CENTER_VERTICAL;
                                        tparams.height=iparams.height;
                                        tparams.leftMargin=5 * iparams.leftMargin;
                                        t.setLayoutParams(tparams);
                                        t.setGravity(Gravity.CENTER);

                                        LinearLayout l1=new LinearLayout(context);
                                        l1.setOrientation(LinearLayout.VERTICAL);
                                        l1.setBackgroundColor(Color.parseColor(Global.backgroundColor));
                                        l.addView(l1);
                                        LinearLayout.LayoutParams l1params=(LinearLayout.LayoutParams)l1.getLayoutParams();
                                        l1params.width=LinearLayout.LayoutParams.MATCH_PARENT;
                                        l1params.height=dialogHeight-tparams.height;
                                        l1.setLayoutParams(l1params);


                                        LinearLayout t1layout=new LinearLayout(context);
                                        t1layout.setOrientation(LinearLayout.HORIZONTAL);
                                        l1.addView(t1layout);

                                        ImageView userIcon=new ImageView(context);
                                        userIcon.setImageBitmap(Global.userIcon);
                                        t1layout.addView(userIcon);
                                        LinearLayout.LayoutParams uparams=(LinearLayout.LayoutParams)userIcon.getLayoutParams();
                                        uparams.height=20 *l1params.height/100;
                                        uparams.width=Global.userIcon.getWidth();
                                        uparams.gravity=Gravity.CENTER;
                                        uparams.leftMargin=5 * dialogWidth/100;
                                        userIcon.setLayoutParams(uparams);

                                        TextView userText=new TextView(context);
                                        userText.setTextSize(Global.fontSize);
                                        userText.setTextColor(Color.parseColor(Global.textColor));
                                        userText.setText(username);
                                        userText.setGravity(Gravity.CENTER);
                                        t1layout.addView(userText);
                                        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)userText.getLayoutParams();
                                        t1params.gravity=Gravity.CENTER_VERTICAL;
                                        t1params.leftMargin=uparams.leftMargin/2;
                                        t1params.width=LinearLayout.LayoutParams.WRAP_CONTENT;
                                        t1params.height=20 * l1params.height/100;
                                        userText.setLayoutParams(t1params);


                                        LinearLayout t2layout=new LinearLayout(context);
                                        t2layout.setOrientation(LinearLayout.HORIZONTAL);
                                        l1.addView(t2layout);
                                        ImageView lockerIcon=new ImageView(context);
                                        lockerIcon.setImageBitmap(Global.lockerIcon);
                                        t2layout.addView(lockerIcon);
                                        lockerIcon.setLayoutParams(uparams);

                                        TextView typeText=new TextView(context);
                                        typeText.setTextColor(Color.parseColor(Global.textColor));
                                        typeText.setTextSize(Global.fontSize);
                                        typeText.setGravity(Gravity.CENTER);
                                        try {
                                            if(response.getJSONObject(0).getInt("type")<Global.messageType.length)
                                                typeText.setText(
                                                        context.getString(Global.messageType[response.getJSONObject(0).getInt("type")]));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        t2layout.addView(typeText);
                                        typeText.setLayoutParams(t1params);

                                        WebView info=new WebView(context);
                                        l1.addView(info);
                                        info.loadDataWithBaseURL("",message,"text/html","utf-8","");
                                        LinearLayout.LayoutParams infoParams=(LinearLayout.LayoutParams)info.getLayoutParams();
                                        infoParams.width=90 * dialogWidth/100;
                                        infoParams.height=50 * l1params.height/100;
                                        infoParams.topMargin=2 * l1params.height/100;
                                        infoParams.gravity=Gravity.CENTER;
                                        info.setLayoutParams(infoParams);

                                        LinearLayout buttonLayout=new LinearLayout(context);
                                        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                                        l.addView(buttonLayout);
                                        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)buttonLayout.getLayoutParams();
                                        bparams.width=LinearLayout.LayoutParams.MATCH_PARENT;
                                        bparams.height=dialogHeight-hparams.height-l1params.height;
                                        buttonLayout.setLayoutParams(bparams);

                                        Button rejectButton=new Button(context);
                                        rejectButton.setText(context.getString(R.string.rejectText));
                                        rejectButton.setTextSize(Global.fontSize);
                                        rejectButton.setBackgroundResource(R.drawable.transparentbutton);
                                        rejectButton.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(Global.redxicon),null,null,null);
                                        rejectButton.setPadding(2*dialogWidth/100,0,0,0);
                                        buttonLayout.addView(rejectButton);
                                        rejectButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                sendResponse(2);
                                                alertDialog.dismiss();
                                            }
                                        });

                                        Button acceptButton=new Button(context);
                                        acceptButton.setText(context.getString(R.string.acceptText));
                                        acceptButton.setTextSize(Global.fontSize);
                                        acceptButton.setBackgroundResource(R.drawable.roundyellowbutton);
                                        acceptButton.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(Global.tickIcon),null,null,null);
                                        acceptButton.setPadding(2*dialogWidth/100,0,0,0);
                                        buttonLayout.addView(acceptButton);

                                        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)rejectButton.getLayoutParams();
                                        cparams.leftMargin=3 * dialogWidth/100;
                                        cparams.width=43 * dialogWidth/100;
                                        cparams.height=80 * bparams.height/100;
                                        cparams.gravity=Gravity.CENTER;
                                        rejectButton.setLayoutParams(cparams);
                                        acceptButton.setLayoutParams(cparams);
                                        acceptButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                sendResponse(1);
                                                alertDialog.dismiss();
                                            }
                                        });

                                        TextView bottom=new TextView(context);
                                        l.addView(bottom);

                                        alertDialog.setCancelable(false);
                                        alertDialog.setView(l);

                                        // Set to TYPE_SYSTEM_ALERT so that the Service can display it
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                        }
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                        {
                                            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                        }
                                        alertDialog.getWindow().setLayout(80*Global.width/100,50*Global.height/100);
                                        alertDialog.show();

                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("GIANNIS ERROR IN SERVICE "+error);

                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

}
