package ai.datawise.snapserve;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SymbolTable;
import android.view.textclassifier.TextClassification;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InternetClass
{
    public static String lasturl="";
    public static RequestQueue queue=null;
    public static int timeout=10000;
    public static void makeQueue(Context ctx)
    {
        queue = Volley.newRequestQueue(ctx);
    }
    public static void ObjectGETCall(Context ctx,String url,final String authorization,final JSONObjectListener listener)
    {
        lasturl=url;
        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", authorization);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObejct.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObejct);
    }


    public static void ObjectGETCall(Context ctx,String url,final JSONObjectListener listener)
    {
        lasturl=url;
        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObejct.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObejct);
    }


    public static void ObjectPOSTCall(Context ctx, String url, final String authorization, final JSONObjectListener listener)
    {
        lasturl=url;
        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.POST, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", authorization);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObejct.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObejct);
    }

    public static void ObjectPOSTCall(Context ctx, String url, JSONObject params, final String authorization, final JSONObjectListener listener)
    {
        lasturl=url;
        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.POST, url,
                params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
                System.out.println("GIANNIS ERROR IS "+error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", authorization);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObejct.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObejct);
    }


    public static void ObjectPutCall(Context ctx, String url, JSONObject params, final String authorization, final JSONObjectListener listener)
    {
        lasturl=url;
        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.PUT, url,
                params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", authorization);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObejct.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObejct);
    }


    public  static void ArrayGETCall(Context ctx, String url, final String authorization, final JSONArrayListener listener)
    {
        lasturl=url;
        JsonArrayRequest jsonObejct = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                listener.onResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("GIANNIS VOLLEY ERROR: "+error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", authorization);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObejct.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObejct);
    }

    public  static void ArrayPostCall(Context ctx, String url, JSONArray params,
                                      final String authorization, final JSONArrayListener listener)
    {
        lasturl=url;
        JsonArrayRequest jsonObejct = new JsonArrayRequest(Request.Method.POST, url,
                params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                listener.onResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", authorization);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObejct.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObejct);
    }

    public static void checkIn(final Context context, final String url, final String authorization, final JSONObjectListener listener)
    {
        lasturl=url;

        OrderCloseDialog closeDialog=new OrderCloseDialog(context,
                context.getString(R.string.clockedInMessage),
                context.getString(R.string.yesClockInText),
                context.getString(R.string.closeButtonText));
        closeDialog.show();
        closeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                OrderCloseDialog d=(OrderCloseDialog)dialog;
                if(d.getCloseFlag()==OrderCloseDialog.CLOSE_OK)
                {
                    final ClockedInDialog clockedInDialog=new ClockedInDialog(context);
                    clockedInDialog.show();
                    clockedInDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(!clockedInDialog.getValue().equals("cancel"))
                            {
                                //call checkin
                                JSONObject params = new JSONObject();
                                try {
                                    double f=0.0;
                                    if(clockedInDialog.getValue().length()!=0)
                                        f=Double.parseDouble(clockedInDialog.getValue());
                                    params.put("checkInCash", f);
                                } catch (JSONException e) {
                                    Global.showAlert(context.getString(R.string.executionProblemText)+" Check In ",context);

                                    e.printStackTrace();
                                }
                                InternetClass.ObjectPOSTCall(context, url,
                                        params,authorization,listener);
                            }
                        }
                    });
                }
            }
        });
    }

    public static String takeABreak(final Context context, final String url, final String authorization, final StringListener listener)
    {
        lasturl=url;
        final String[] service = {"start"};
        InternetClass.ObjectGETCall(context, url, authorization, new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean isBreak=response.getBoolean("isInBreak");
                    if(isBreak) service[0] ="end";

                    InternetClass.StringPostCall(context,url+"/"+ service[0],authorization,listener);

                } catch (JSONException e) {
                    service[0]="error";
                    e.printStackTrace();
                }


            }

            @Override
            public void onErrorResponse(VolleyError error) {
                service[0]="error";
            }
        });
        return service[0];
    }

    public static void StringPostCall(Context context, String url, final String authorization, final StringListener listener)
    {
        lasturl=url;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if(listener!=null)
                            listener.onResponse(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(listener!=null)
                            listener.onErrorResponse(error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", authorization);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    public static void putRequest(String murl,JSONObject params,String authorization)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // put your json here
        RequestBody body = RequestBody.create(JSON, params.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(murl)
                .put(body)
                .addHeader("Authorization",authorization)
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

    public static void putRequest(String murl,JSONArray params,String authorization)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // put your json here
        RequestBody body = RequestBody.create(JSON, params.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(murl)
                .put(body)
                .addHeader("Authorization",authorization)
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
}
