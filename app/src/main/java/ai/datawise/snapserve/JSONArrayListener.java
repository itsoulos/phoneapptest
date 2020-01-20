package ai.datawise.snapserve;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface JSONArrayListener {
    void onResponse(JSONArray response);
    void onErrorResponse(VolleyError error);
}
