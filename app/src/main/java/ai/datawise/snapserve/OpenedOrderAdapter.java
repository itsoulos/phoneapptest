package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OpenedOrderAdapter extends ArrayAdapter { Context context=null;
    JSONArray info=new JSONArray();
    private ArrayList<Integer> isSelected=new ArrayList<Integer>();
    boolean showWaiter=false;
    private  AdapterView.OnItemClickListener listener=null;


    public OpenedOrderAdapter(Context ctx, JSONArray d, boolean waiterFlag, AdapterView.OnItemClickListener l)
    {
        super(ctx, 0);
        info=d;
        showWaiter=waiterFlag;
        context=ctx;
        for(int i=0;i<d.length();i++)
            isSelected.add(new Integer(0));
        listener=l;
    }


    public JSONObject getInfo(int position)
    {
        try {
            return  info.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    int getSelected()
    {
        for(int i=0;i<isSelected.size();i++)
            if(isSelected.get(i)==1)
                return i;
        return 0;
    }


    @Override
    public int getCount()
    {
        return info.length()+1;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);

        TextView orderIdText=new TextView(context);

        l.addView(orderIdText);

        TextView amountText=new TextView(context);
        l.addView(amountText);

        TextView openedAtText=new TextView(context);
        l.addView(openedAtText);

        orderIdText.setTextSize(Global.fontSize);
        openedAtText.setTextSize(Global.fontSize);
        amountText.setTextSize(Global.fontSize);
        ImageView rightImageView=null;
        try {
            if(position==0)
            {
                orderIdText.setText(context.getString(R.string.orderIdText));
                if(showWaiter)
                    openedAtText.setText(context.getString(R.string.waiterText));
                else
                    openedAtText.setText(context.getString(R.string.timeText));
                amountText.setText(context.getString(R.string.amountText));
                orderIdText.setTextColor(Color.LTGRAY);
                openedAtText.setTextColor(Color.LTGRAY);
                amountText.setTextColor(Color.LTGRAY);

            }
            else {
                orderIdText.setText("" + info.getJSONObject(position-1).getInt("id"));
                if(showWaiter)
                    openedAtText.setText(info.getJSONObject(position-1).getString("waiterName"));
                else
                    openedAtText.setText(Global.getHourAndMinute(info.getJSONObject(position-1).getString("openedAt")));
                amountText.setText(Global.currency +Global.displayDecimal(info.getJSONObject(position-1).getDouble("amountTotal")));
                orderIdText.setTextColor(Color.parseColor(Global.textColor));
                openedAtText.setTextColor(orderIdText.getCurrentTextColor());
                amountText.setTextColor(orderIdText.getCurrentTextColor());
                rightImageView=new ImageView(context);
                rightImageView.setImageBitmap(Global.rightArrow);
                l.addView(rightImageView);
            }
        } catch (JSONException e) {
            try {
                System.out.println("GIANNIS JSON E "+e.getMessage()+" json is "+info.getJSONObject(position));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)orderIdText.getLayoutParams();
        t1params.leftMargin=5 * Global.width/100;
        t1params.topMargin=1 * Global.height/100;
        t1params.width=15 * Global.width/100;
        t1params.height=2 * Global.height/100 + 3*Global.getMeasuredHeight(context,orderIdText.getText().toString(),Global.fontSize)/2;
        t1params.gravity= Gravity.CENTER_VERTICAL;
        orderIdText.setLayoutParams(t1params);
        orderIdText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)amountText.getLayoutParams();
        t2params.leftMargin=t1params.leftMargin;
        t2params.topMargin=t1params.topMargin;
        t2params.width=t1params.width;
        t2params.height=t1params.height;
        t2params.gravity=Gravity.CENTER_VERTICAL;
        amountText.setLayoutParams(t2params);
        amountText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams t3params=(LinearLayout.LayoutParams)openedAtText.getLayoutParams();

        t3params.topMargin=t2params.topMargin;
        if(showWaiter)
            t3params.width=2 * t2params.width;
        else
            t3params.width=t2params.width;
        t3params.height=t2params.height;
        if(showWaiter && position>0)
            t3params.height=2 * t2params.height;
        t3params.leftMargin= t2params.leftMargin;
        t3params.gravity=Gravity.CENTER_VERTICAL;
        openedAtText.setLayoutParams(t3params);
        openedAtText.setGravity(Gravity.CENTER);

        if(position!=0)
        {
            LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)rightImageView.getLayoutParams();
            iparams.rightMargin=t1params.leftMargin;
            iparams.width=Global.rightArrow.getWidth();
            iparams.height=t1params.height;
            if(iparams.height<Global.rightArrow.getHeight()) iparams.height=Global.rightArrow.getHeight();
            iparams.gravity=Gravity.CENTER;
            iparams.leftMargin=Global.width-t1params.width-t1params.leftMargin-t2params.width-t2params.leftMargin-t3params.leftMargin-t3params.width-
                    iparams.rightMargin-iparams.width;
            rightImageView.setLayoutParams(iparams);
        }
        if(position>0) {
            if (isSelected.get(position - 1) == 1)
                l.setBackgroundColor(Color.parseColor(Global.listSelectColor));
            else
                l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        }
        else
        {
            l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        }
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0) return;
                for(int i=0;i<isSelected.size();i++)
                {
                    isSelected.set(i,0);
                }
                isSelected.set(position-1,1);
                if(listener!=null) {
                    listener.onItemClick(null, l, position - 1, 0);

                }
                notifyDataSetChanged();

            }
        });
        return l;
    }
}
