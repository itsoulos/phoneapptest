package ai.datawise.snapserve;

import com.android.volley.VolleyError;

public interface StringListener {
    public void onResponse(String response);
    public void onErrorResponse(VolleyError error);
}
