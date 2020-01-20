package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ExtrasAdapter extends ArrayAdapter<String>
{
    private Context context=null;
    LinearLayout.LayoutParams hparams;
    JSONArray extras=new JSONArray();
    OrderStore orderStore=null;
    public ExtrasAdapter(Context ctx, ArrayList<String> dummy, JSONArray a, LinearLayout.LayoutParams params, OrderStore o)
    {
        super(ctx,0,dummy);
        context=ctx;
        extras=a;
        hparams=params;
        orderStore=o;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams fparams=new FrameLayout.LayoutParams(hparams.width,70*hparams.height/100);
        linearLayout.setLayoutParams(fparams);

        TextView t1=new TextView(context);
        t1.setTextSize(Global.fontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        linearLayout.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=5 * hparams.width/100;
        t1params.topMargin= 10 * hparams.height/100;
        t1params.width=40 * hparams.width/100;
        t1params.height = 50 * hparams.height/100;
        t1params.gravity= Gravity.CENTER_VERTICAL;
        t1.setLayoutParams(t1params);

        TextView t2=new TextView(context);
        t2.setTextSize(Global.fontSize);
        t2.setTextColor(t1.getCurrentTextColor());
        linearLayout.addView(t2);
        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)t2.getLayoutParams();
        t2params.topMargin=t1params.topMargin;
        t2params.width=30 * hparams.width/100;
        t2params.height=t1params.height;
        t2params.rightMargin=t1params.leftMargin;
        t2params.gravity=Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        t2params.leftMargin=20 * hparams.width/100;
        t2.setLayoutParams(t2params);
        t2.setGravity(Gravity.RIGHT);
        try {
            t1.setText(extras.getJSONObject(position).getString("itemName"));
            double costInTable=0.0;
            if(orderStore.hasTakeout()==false)
                //edo na allaxei
                costInTable = Global.getProductTablePrice(extras.getJSONObject(position).getInt("id"));
            else
                costInTable=Global.getTakeAwayPrice(extras.getJSONObject(position).getInt("id"));
            t2.setText(Global.currency+Global.displayDecimal(costInTable));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return linearLayout;
    }
}
