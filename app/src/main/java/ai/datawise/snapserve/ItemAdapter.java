package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

class ItemAdapter extends ArrayAdapter<String>
{
    private Context context;
    JSONArray items=new JSONArray();
    LinearLayout.LayoutParams hparams;
    boolean enableSubCategory=false;
    boolean takeAwayFlag=false;
    public ItemAdapter(Context ctx, JSONArray i, ArrayList<String> dummy, LinearLayout.LayoutParams params, boolean subflag,
                       boolean tflag)
    {
        super(ctx,0,dummy);
        context=ctx;
        items=i;
        hparams=params;
        enableSubCategory=subflag;
        takeAwayFlag=tflag;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        try {
            if (items.getJSONObject(position).getBoolean("isGroupItem"))
            {
                LinearLayout bigLayout=new LinearLayout(context);
                bigLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView t1 = new TextView(context);
                t1.setTextSize(Global.fontSize);
                t1.setTextColor(Color.parseColor(Global.textColor));
                try {
                    t1.setText(items.getJSONObject(position).getString("itemName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bigLayout.addView(t1);
                LinearLayout.LayoutParams t1params = (LinearLayout.LayoutParams) t1.getLayoutParams();
                t1params.topMargin = 10 * hparams.height / 100;
                t1params.leftMargin = 5 * hparams.width / 100;
                t1params.height = 50 * hparams.height / 100;
                t1params.gravity= Gravity.CENTER;
                t1params.width = 8 * Global.getMeasureWidth(context, t1.getText().toString(), Global.fontSize) / 7;
                if (t1params.width < 40 * hparams.width / 100) t1params.width = 40 * hparams.width / 100;
                t1.setLayoutParams(t1params);


                ImageView bb=new ImageView(context);
                bb.setImageBitmap(Global.rightArrow);
                bigLayout.addView(bb);
                LinearLayout.LayoutParams bbparams=(LinearLayout.LayoutParams)bb.getLayoutParams();
                bbparams.rightMargin=t1params.leftMargin;
                bbparams.width=Global.rightArrow.getWidth();
                bbparams.height=t1params.height;
                bbparams.leftMargin=hparams.width-t1params.leftMargin-t1params.width-bbparams.rightMargin-bbparams.width;
                bbparams.gravity= Gravity.CENTER;
                bb.setLayoutParams(bbparams);

                return bigLayout;
            }
            else {
                LinearLayout l = new LinearLayout(context);
                l.setOrientation(LinearLayout.HORIZONTAL);
                TextView t1 = new TextView(context);
                t1.setTextSize(Global.fontSize);
                t1.setTextColor(Color.parseColor(Global.textColor));
                try {
                    t1.setText(items.getJSONObject(position).getString("itemName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                l.addView(t1);
                LinearLayout.LayoutParams t1params = (LinearLayout.LayoutParams) t1.getLayoutParams();
                t1params.topMargin = 10 * hparams.height / 100;
                t1params.leftMargin = 5 * hparams.width / 100;
                t1params.bottomMargin = t1params.topMargin;
                t1params.height = 50 * hparams.height / 100;
                t1params.width = 8 * Global.getMeasureWidth(context, t1.getText().toString(), Global.fontSize) / 7;
                if(t1params.width>=60 * hparams.width/100)
                {
                    t1params.width=60 *hparams.width/100;
                    t1.setMaxLines(10);
                    t1.setSingleLine(false);
                    t1.setHorizontallyScrolling(false);
                    t1params.height=3*  t1params.height/2;

                }
                if (t1params.width < 40 * hparams.width / 100)
                    t1params.width = 40 * hparams.width / 100;
                t1.setLayoutParams(t1params);

                TextView t2 = new TextView(context);
                t2.setTextSize(Global.fontSize);
                t2.setTextColor(t1.getCurrentTextColor());
                try {
                    double costInTable = 0.0;

                    if(takeAwayFlag==false)
                        //edo na allaxei
                        costInTable = Global.getProductTablePrice(items.getJSONObject(position).getInt("id"));
                    else
                        costInTable=Global.getTakeAwayPrice(items.getJSONObject(position).getInt("id"));

                    t2.setText(Global.currency + "" + Global.displayDecimal(costInTable));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                l.addView(t2);
                LinearLayout.LayoutParams t2params = (LinearLayout.LayoutParams) t2.getLayoutParams();
                t2params.topMargin = t1params.topMargin;
                t2params.bottomMargin = t1params.bottomMargin;
                t2params.rightMargin = t1params.leftMargin;
                int d = Global.getMeasureWidth(context, t2.getText().toString(), Global.fontSize);
                t2params.width = 10 * hparams.width / 100;
                if (8 * d / 7 > t2params.width) t2params.width = 8 * d / 7;
                t2params.height = 50 * hparams.height / 100;
                t2params.leftMargin = hparams.width - t1params.leftMargin - t1params.width - t2params.rightMargin - t2params.width;
                t2.setLayoutParams(t2params);

                return l;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

}