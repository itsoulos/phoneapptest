package ai.datawise.snapserve;

import android.content.Context;
import android.os.Handler;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthRequest {
    private static Runnable runnable=null;
    private static final int maxTries=15;
    private static final int timeout=2000;
    private static  int requestTries=0;
    private static int requestid=0;
    public static void makeRequest(final Context context, int orderid, int type, final AuthInterface listener)
    {
        requestTries=0;

        final JSONObject params=new JSONObject();
        try {
            params.put("type",type);
            params.put("details",new JSONArray());
            params.put("orderId",orderid);
            InternetClass.ObjectPOSTCall(context,
                    Global.server + "/auth_requests/", params, Global.getAuthorizationString(Global.loginCredentials),
                    new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            requestTries=0;
                            try {
                                requestid=response.getInt("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final Handler handler = new Handler();
                            runnable = new Runnable() {
                                @Override
                                public void run()
                                {
                                    handler.postDelayed(runnable, timeout);

                                    InternetClass.ObjectGETCall(context, Global.server + "/auth_requests/" + requestid,
                                            Global.getAuthorizationString(Global.loginCredentials),
                                            new JSONObjectListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    requestTries++;
                                                    try {
                                                        if(response.getInt("approvalStatus")!=0 ||  requestTries>=15)
                                                        {
                                                            Global.hideProgress();
                                                            handler.removeCallbacks(runnable);
                                                            if(response.getInt("approvalStatus")==2)
                                                            {
                                                                if(listener!=null)
                                                                    listener.onRejected();
                                                            }
                                                            else
                                                            if(response.getInt("approvalStatus")==1)
                                                            {
                                                                if(listener!=null)
                                                                    listener.onApproval();
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    System.out.println("GIANNIS AUTH ERROR IS "+error);
                                                }
                                            });
                                }};

                            Global.showProgess(context);
                            handler.post(runnable);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Global.showAlert("ERROR IN AUTHORIZATION",context);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
