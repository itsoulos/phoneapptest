package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WaitersAdapter extends ArrayAdapter {
    JSONArray info=new JSONArray();
    Context context=null;

    public WaitersAdapter(Context ctx,JSONArray d)
    {
        super(ctx, 0);
        context=ctx;
        info=d;
    }

    @Override
    public int getCount()
    {
        return info.length()+1;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams tparams=null;

        TextView t[]=new TextView[5];
        for(int i=0;i<t.length;i++)
        {
            t[i]=new TextView(context);
            t[i].setTextSize(Global.smallFontSize);
            t[i].setTextColor(Color.parseColor(Global.textColor));
            l.addView(t[i]);
            t[i].setGravity(Gravity.CENTER);
            if(i==0)
            {
                tparams=(LinearLayout.LayoutParams)t[i].getLayoutParams();
                tparams.gravity= Gravity.CENTER;
                tparams.width=19*Global.width/100;
                tparams.height=8*Global.height/100;
                if(tparams.height<150 * Global.getMeasuredHeight(context,"SIMPLE TEXT",Global.fontSize)/100)
                    tparams.height=150 * Global.getMeasuredHeight(context,"SIMPLE TEXT",Global.fontSize)/100;
            }
            t[i].setLayoutParams(tparams);
        }
        if(position==0)
        {
            t[0].setText(context.getString(R.string.nameText));
            t[1].setText(context.getString(R.string.ordersText));
            t[2].setText(context.getString(R.string.amountText));
            t[3].setText(context.getString(R.string.treatsText));
            t[4].setText(context.getString(R.string.checkedInText));
            for(int i=0;i<t.length;i++)
                t[i].setTextColor(Color.LTGRAY);
        }
        else
        {
            try {
                t[0].setText(info.getJSONObject(position-1).getString("userDisplayName"));
                JSONObject metrics=null;
                if(info.getJSONObject(position-1).getJSONObject("shiftMetrics")!=JSONObject.NULL)
                    metrics=info.getJSONObject(position-1).getJSONObject("shiftMetrics");
                if(metrics!=null) {
                    t[1].setText("" + metrics.getInt("orderCount"));
                    t[2].setText(Global.currency + Global.displayDecimal(metrics.getDouble("totalAmount")));
                    t[3].setText(Global.currency + Global.displayDecimal(metrics.getDouble("offerAmount")));
                    if(metrics.getString("checkInAt").equals("null"))
                        t[4].setText("N/A");
                    else
                        t[4].setText(Global.getHourAndMinute(metrics.getString("checkInAt")));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return l;
    }
}
