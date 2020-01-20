package ai.datawise.snapserve;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckedAdapter extends ArrayAdapter<String> {
    Context context=null;
    int dialogWidth=0;
    ArrayList<String> data=new ArrayList<String>();
    private ArrayList<Integer> isSelected=new ArrayList<Integer>();
    private  AdapterView.OnItemClickListener listener=null;
    boolean enableMultiple=false;
    public CheckedAdapter(Context ctx, ArrayList<String> d,int dw, AdapterView.OnItemClickListener  l) {
        super(ctx, 0,d);
        context=ctx;
        data=d;
        dialogWidth=dw;
        for(int i=0;i<d.size();i++)
            isSelected.add(new Integer(0));
        listener=l;
    }

    public CheckedAdapter(Context ctx, ArrayList<String> d,int dw, AdapterView.OnItemClickListener  l,boolean m) {
        this(ctx,d,dw,l);
        enableMultiple=m;
    }

    int getSelected()
    {
        for(int i=0;i<isSelected.size();i++)
            if(isSelected.get(i)==1)
                return i;
        return 0;
    }

    ArrayList<Integer> getAllSelected()
    {
        ArrayList<Integer> x=new ArrayList<Integer>();
        for(int i=0;i<isSelected.size();i++)
        {
            if(isSelected.get(i)==1)
                x.add(i);
        }
        return x;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setClickable(true);
        TextView info=new TextView(context);
        info.setTextColor(Color.parseColor("#80000000"));
        info.setTextSize(Global.fontSize);
        info.setText(data.get(position));
        l.addView(info);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)info.getLayoutParams();

        iparams.width=80 * dialogWidth/100;
        iparams.height=8 * Global.height/100;
        iparams.gravity= Gravity.CENTER;
        info.setLayoutParams(iparams);
        info.setGravity(Gravity.CENTER);
        if(isSelected.get(position)==1)
            l.setBackgroundColor(Color.parseColor(Global.listSelectColor));
        else
            l.setBackgroundColor(Color.WHITE);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!enableMultiple) {
                    for (int i = 0; i < isSelected.size(); i++) {
                        isSelected.set(i, 0);
                    }
                }
                if(enableMultiple)
                {
                    if(isSelected.get(position)==1)
                        isSelected.set(position,0);
                    else
                        isSelected.set(position,1);
                }
                else
                isSelected.set(position,1);

                notifyDataSetChanged();
                if(listener!=null)
                {

                    listener.onItemClick(null, l, position, 0);

                }
            }
        });
        return l;
    }
}
