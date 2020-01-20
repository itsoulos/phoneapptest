package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PosSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    Context context;
    ArrayList<String> items = new ArrayList<String>();
    View.OnTouchListener textListener = null;

    public PosSpinnerAdapter(Context ctx, ArrayList<String> t) {

        context = ctx;
        items = t;
        textListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        LinearLayout l = (LinearLayout) v;
                        v.setBackgroundColor(Color.parseColor(Global.backgroundColor));

                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.WHITE);
                        break;
                }
                return false;
            }
        };
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        TextView t = new TextView(context);
        t.setTextColor(Color.parseColor(Global.textColor));
        t.setTextSize(Global.fontSize);
        t.setText(items.get(position));
        l.addView(t);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
        tparams.leftMargin=5 * Global.width/100;
        tparams.topMargin=Global.height/40;
        tparams.height=8 * Global.height/100;
        tparams.width=LinearLayout.LayoutParams.WRAP_CONTENT;
        t.setLayoutParams(tparams);
        l.setOnTouchListener(textListener);
        return l;
    }
}
