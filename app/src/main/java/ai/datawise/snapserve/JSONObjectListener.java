package ai.datawise.snapserve;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface JSONObjectListener {
    void onResponse(JSONObject response);
    void onErrorResponse(VolleyError error);
}
