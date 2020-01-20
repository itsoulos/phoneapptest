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

public class ShowOrdersAdapter extends ArrayAdapter {
    Context context=null;
    JSONArray info=new JSONArray();

    public ShowOrdersAdapter(Context ctx, JSONArray d)
    {
        super(ctx, 0);
        info=d;
        context=ctx;
    }

    @Override
    public int getCount()
    {
        return info.length()+1;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);

        TextView orderIdText=new TextView(context);

        l.addView(orderIdText);

        TextView openedAtText=new TextView(context);
        l.addView(openedAtText);

        TextView amountText=new TextView(context);
        l.addView(amountText);

        orderIdText.setTextSize(Global.fontSize);
        openedAtText.setTextSize(Global.fontSize);
        amountText.setTextSize(Global.fontSize);

        try {
            if(position==0)
            {
                orderIdText.setText(context.getString(R.string.orderIdText));
                openedAtText.setText(context.getString(R.string.openedAtText));
                amountText.setText(context.getString(R.string.amountText));
                orderIdText.setTextColor(Color.LTGRAY);
                openedAtText.setTextColor(Color.LTGRAY);
                amountText.setTextColor(Color.LTGRAY);

            }
            else {
                orderIdText.setText("" + info.getJSONObject(position-1).getInt("id"));
                openedAtText.setText(Global.getHourAndMinute(info.getJSONObject(position-1).getString("openedAt")));
                amountText.setText(Global.currency +Global.displayDecimal(info.getJSONObject(position-1).getDouble("amountTotal")));
                orderIdText.setTextColor(Color.parseColor(Global.textColor));
                openedAtText.setTextColor(orderIdText.getCurrentTextColor());
                amountText.setTextColor(orderIdText.getCurrentTextColor());
            }
        } catch (JSONException e) {
            try {
                System.out.println("GIANNIS THIS JSON E "+e.getMessage()+"JSON WAS "+info.getJSONObject(position-1));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)orderIdText.getLayoutParams();
        t1params.leftMargin=5 * Global.width/100;
        t1params.topMargin=1 * Global.height/100;
        t1params.width=20 * Global.width/100;
        t1params.height=2 * Global.height/100 + 3*Global.getMeasuredHeight(context,orderIdText.getText().toString(),Global.fontSize)/2;
        t1params.gravity= Gravity.CENTER_VERTICAL;
        orderIdText.setLayoutParams(t1params);

        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)openedAtText.getLayoutParams();
        t2params.leftMargin=2*t1params.leftMargin;
        t2params.topMargin=t1params.topMargin;
        t2params.width=t1params.width;
        t2params.height=t1params.height;
        t2params.gravity=Gravity.CENTER_VERTICAL;
        openedAtText.setLayoutParams(t2params);
        openedAtText.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams t3params=(LinearLayout.LayoutParams)amountText.getLayoutParams();
        t3params.rightMargin=t1params.leftMargin;
        t3params.topMargin=t2params.topMargin;
        t3params.width=2*t1params.width;
        t3params.height=t1params.height;
        t3params.leftMargin=Global.width-t1params.leftMargin-t1params.width-t2params.leftMargin-t2params.width-t3params.rightMargin-t3params.width;
        t3params.gravity=Gravity.CENTER_VERTICAL;
        amountText.setLayoutParams(t3params);
        amountText.setGravity(Gravity.RIGHT);
        return l;
    }
}
