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

public class ShiftDisplayAdapter extends ArrayAdapter {
    Context context=null;
    JSONArray info=new JSONArray();

    public ShiftDisplayAdapter(Context ctx, JSONArray data, ArrayList<String> dummy)
    {
        super(ctx,0,dummy);
        context=ctx;
        info=data;
    }
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);

        TextView workDateText=new TextView(context);
        l.addView(workDateText);

        TextView checkedInText=new TextView(context);
        l.addView(checkedInText);


        TextView checkedOutText=new TextView(context);
        l.addView(checkedOutText);

        workDateText.setTextSize(Global.fontSize);
        checkedInText.setTextSize(Global.fontSize);
        checkedOutText.setTextSize(Global.fontSize);

        if(position==0)
        {
            workDateText.setText(context.getString(R.string.workDateText));
            checkedInText.setText(context.getString(R.string.checkedInText));
            checkedOutText.setText(context.getString(R.string.checkedOutText));
            workDateText.setTextColor(Color.LTGRAY);
            checkedInText.setTextColor(workDateText.getCurrentTextColor());
            checkedOutText.setTextColor(workDateText.getCurrentTextColor());
        }
        else
        {
            try {
                workDateText.setText(info.getJSONObject(position-1).getString("workDate"));
                checkedInText.setText(Global.getHourAndMinute(info.getJSONObject(position-1).getString("checkInAt")));
                String t="";
                if(info.getJSONObject(position-1).getString("checkOutAt").isEmpty())
                    t=context.getString(R.string.NonAvailableText);
                else
                    t=info.getJSONObject(position-1).getString("checkOutAt");
                checkedOutText.setText(Global.getHourAndMinute(t));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ImageView im=null;
        if(position>0) {
            im = new ImageView(context);
            l.addView(im);
            im.setImageBitmap(Global.rightArrow);
        }

        int mheight= Global.rightArrow.getHeight();
        if(Global.getMeasuredHeight(context,workDateText.getText().toString(),Global.fontSize)>mheight)
            mheight=Global.getMeasuredHeight(context,workDateText.getText().toString(),Global.fontSize);

        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)workDateText.getLayoutParams();
        t1params.leftMargin=5 * Global.width/100;
        t1params.topMargin=1 * Global.height/100;
        t1params.width=25 * Global.width/100;
        if(Global.getMeasureWidth(context,workDateText.getText().toString(),Global.fontSize)>t1params.width)
            t1params.width=Global.getMeasureWidth(context,workDateText.getText().toString(),Global.fontSize);
        t1params.height= 2* t1params.topMargin+mheight;
        t1params.gravity= Gravity.CENTER_VERTICAL;
        workDateText.setLayoutParams(t1params);

        checkedInText.setLayoutParams(t1params);
        checkedOutText.setLayoutParams(t1params);

        if(position>0) {
            LinearLayout.LayoutParams iparams = (LinearLayout.LayoutParams) im.getLayoutParams();
            iparams.height = t1params.height;
            iparams.topMargin = t1params.topMargin;
            iparams.width = Global.rightArrow.getWidth();
            iparams.rightMargin = t1params.leftMargin;
            iparams.leftMargin = Global.width - 3 * t1params.width - 3 * t1params.leftMargin - iparams.width - iparams.rightMargin+5;
            im.setLayoutParams(iparams);
        }
        return l;
    }
}
