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

public class UserslistAdapter extends ArrayAdapter {
    JSONArray users=new JSONArray();
    Context context=null;
    private ArrayList<Integer> isSelected=new ArrayList<Integer>();

    public UserslistAdapter(Context ctx, JSONArray d) {
        super(ctx,0);
        context=ctx;
        users=d;
        for(int i=0;i<d.length();i++)
            isSelected.add(new Integer(0));
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
        return users.length();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        ImageView im=new ImageView(context);
        im.setImageBitmap(Global.userIcon);
        l.addView(im);
        TextView t=new TextView(context);
        t.setTextSize(Global.fontSize);
        t.setTextColor(Color.parseColor(Global.textColor));
        try {
            t.setText(users.getString(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        l.addView(t);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        iparams.leftMargin=3 * Global.width/100;
        iparams.width=Global.userIcon.getWidth();
        iparams.gravity= Gravity.CENTER;
        iparams.height= 2 * Global.userIcon.getHeight();
        im.setLayoutParams(iparams);

        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
        tparams.gravity=Gravity.CENTER;
        tparams.width=50 * Global.width/100;
        tparams.height=iparams.height;
        tparams.leftMargin=6 * Global.width/100;
        t.setLayoutParams(tparams);
        t.setGravity(Gravity.CENTER_VERTICAL);
        if(isSelected.get(position)==1)
            l.setBackgroundColor(Color.parseColor(Global.listSelectColor));
        else
            l.setBackgroundColor(Color.WHITE);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<isSelected.size();i++)
                {
                    isSelected.set(i,0);
                }
                isSelected.set(position,1);

                notifyDataSetChanged();

            }
        });

        return l;
    }
}
