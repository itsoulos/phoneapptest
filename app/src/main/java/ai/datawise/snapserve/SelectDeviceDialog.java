package ai.datawise.snapserve;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class SelectDeviceDialog extends Dialog
{
    LinearLayout mainLayout=null;
    ListView mylist=null;
    CheckedAdapter adapter=null;
    TextView title=null;
    Button acceptButton=null,cancelButton=null;

    JSONArray deviceList=null;
    int selectedId=0;
    String selectedName="";
    int selectedListPos=-1;

    public int getSelectedId()
    {
        return selectedId;
    }
    public String getSelectedName() {return selectedName;}

    public SelectDeviceDialog(Context context, final JSONArray list) throws JSONException {
        super(context);
        deviceList=list;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainLayout=new LinearLayout(context);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        final int dialogWidth=80 * Global.width/100;
        final int dialogHeight=80 * Global.height/100;
        mainLayout.setBackgroundColor(Color.rgb(245,245,245));
        this.getWindow().setLayout(dialogWidth,dialogHeight);


        title=new TextView(context);
        title.setText(context.getString(R.string.selectDeviceText));
        title.setGravity(Gravity.CENTER);
        title.setTextSize(Global.bigFontSize);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mainLayout.addView(title);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)title.getLayoutParams();
        tparams.topMargin=dialogHeight/24;
        tparams.height=8*Global.height/100;
        tparams.width=80*(16 * dialogWidth/20)/100;
        tparams.gravity=Gravity.CENTER;

        ArrayList<String> deviceNames=new ArrayList<String>();
        for(int i=0;i<list.length();i++)
        {
            if(i==0) {
                selectedId = list.getJSONObject(i).getInt("id");
                selectedName=list.getJSONObject(i).getString("name");
            }
            deviceNames.add(list.getJSONObject(i).getString("name"));

        }

        adapter=new CheckedAdapter(context,deviceNames,16*dialogWidth/20,null);
        mylist=new ListView(context);
        mylist.setAdapter(adapter);
        //mylist.setSelector(R.drawable.listbg);
        mylist.setBackgroundResource(R.drawable.transparentbutton);
        mylist.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        mainLayout.addView(mylist);
        LinearLayout.LayoutParams mparams=(LinearLayout.LayoutParams)mylist.getLayoutParams();
        mparams.leftMargin=dialogWidth/20;
        mparams.width=16 * dialogWidth/20;
        mparams.topMargin=dialogHeight/40;
        mparams.height=50*dialogHeight/100;
        mylist.setLayoutParams(mparams);


        LinearLayout bottomLayout=new LinearLayout(context);
        bottomLayout.setOrientation(LinearLayout.VERTICAL);
        bottomLayout.setBackgroundColor(Color.parseColor("#1A000000"));
        mainLayout.addView(bottomLayout);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)bottomLayout.getLayoutParams();
        bparams.height=dialogHeight/4;
        bparams.bottomMargin=5*dialogHeight/100;
        bparams.topMargin=dialogHeight-bparams.height-tparams.height-tparams.topMargin-mparams.height-mparams.topMargin-bparams.bottomMargin;
        bparams.width=dialogWidth;
        bottomLayout.setLayoutParams(bparams);




        Button change=new Button(context);
        change.setText(context.getString(R.string.cancel_text));
        change.setTextSize(Global.fontSize);
        change.setBackgroundResource(R.drawable.grayroundbutton);
        bottomLayout.addView(change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedName="";
                dismiss();
            }
        });
        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)change.getLayoutParams();

        cparams.width=80 *dialogWidth/100;
        cparams.leftMargin=5*dialogWidth/100;
        cparams.height=35 * bparams.height/100;
        cparams.topMargin=1 * dialogHeight/100;
        change.setLayoutParams(cparams);

        Button login=new Button(context);
        login.setText(context.getString(R.string.okText));
        login.setTextSize(Global.fontSize);
        login.setBackgroundResource(R.drawable.roundyellowbutton);
        bottomLayout.addView(login);
        login.setLayoutParams(cparams);

        TextView t=new TextView(context);
        t.setText("   ");
        bottomLayout.addView(t);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t.getLayoutParams();
        t1params.height=2 * bparams.height/100;
        t.setLayoutParams(t1params);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedListPos=adapter.getSelected();
                try {
                    if(selectedListPos<0) {
                        selectedId = list.getJSONObject(0).getInt("id");
                        selectedName=list.getJSONObject(0).getString("name");
                    }
                    else {
                        selectedId = list.getJSONObject(selectedListPos).getInt("id");
                        selectedName=list.getJSONObject(selectedListPos).getString("name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });

    }
}
