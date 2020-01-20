package ai.datawise.snapserve;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

public class PagerDialog extends Dialog {
    Context context=null;
    int dialogWidth=0;
    int dialogHeight=0;
    LinearLayout mainLayout=null;
    Button cancelButton,confirmButton;
    ArrayList<Button> pagerButton=new ArrayList<Button>();
    int selectedPager=-1;

    public PagerDialog( Context ctx) {
        super(ctx);
        context=ctx;
    }

    int getPagerId()
    {
        if(selectedPager==-1) return -1;
        try {
            return Global.pagers.getJSONObject(selectedPager).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    void makeHeader()
    {

        TextView t1=new TextView(context);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setTextSize(Global.bigFontSize);
        t1.setText(context.getString(R.string.addPagerToOrderText));
        mainLayout.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=dialogWidth/6;
        t1params.width=dialogWidth/2;
        t1params.height=dialogHeight/10;
        t1.setLayoutParams(t1params);
        t1.setGravity(Gravity.CENTER);
    }

    void makePagers()
    {
        //four pagers per line
        ScrollView scroll=new ScrollView(context);
        mainLayout.addView(scroll);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)scroll.getLayoutParams();
        sparams.width=dialogWidth;
        sparams.height=dialogHeight-25*dialogHeight/100-3*Global.buttonHeight/2;
        scroll.setLayoutParams(sparams);

        final int nitems=3;
        int nrows=Global.pagers.length()/nitems;
        if(nrows * nitems<Global.pagers.length()) nrows++;

        TableLayout playout=new TableLayout(context);
        scroll.addView(playout);

        int itemcount=0;
        for(int i=0;i<nrows;i++)
        {
            TableRow r=new TableRow(context);
            playout.addView(r);
            for(int j=0;j<nitems;j++)
            {
                if(itemcount>=Global.pagers.length()) break;
                Button bt=new Button(context);
                bt.setTextSize(Global.fontSize);
                bt.setTextColor(Color.parseColor(Global.textColor));
                try {
                    bt.setText(""+Global.pagers.getJSONObject(itemcount).getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                r.addView(bt);
                LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)bt.getLayoutParams();
                bparams.leftMargin=2*dialogWidth/100;
                bparams.rightMargin=2*dialogWidth/100;
                bparams.bottomMargin=2*dialogHeight/100;
                bparams.width=(int)(sparams.width/(nitems+0.8));
                bparams.height=10*dialogHeight/100;
                bt.setLayoutParams(bparams);
                bt.setTag(new Integer(itemcount));
                bt.setBackgroundResource(R.drawable.transparentbutton);
                pagerButton.add(bt);
                itemcount++;
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPager=(Integer)v.getTag();
                        for(int k=0;k<pagerButton.size();k++)
                        {
                            if(k==selectedPager)
                            {
                                pagerButton.get(k).setTextColor(Color.WHITE);
                                pagerButton.get(k).setBackgroundResource(R.drawable.blueroundbutton);
                            }
                            else
                            {
                                pagerButton.get(k).setTextColor(Color.parseColor(Global.textColor));
                                pagerButton.get(k).setBackgroundResource(R.drawable.transparentbutton);
                            }
                        }
                    }
                });
            }
        }
    }


    void makeButtons()
    {
        LinearLayout buttonLayout=new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(buttonLayout);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)buttonLayout.getLayoutParams();
        bparams.leftMargin=5 * dialogWidth/100;
        bparams.topMargin=5 * dialogHeight/100;
        bparams.width=90 * dialogWidth/100;
        bparams.height=3 * Global.buttonHeight/2;
        buttonLayout.setLayoutParams(bparams);

        cancelButton=new Button(context);
        cancelButton.setTextSize(Global.fontSize);
        cancelButton.setTextColor(Color.parseColor(Global.textColor));
        cancelButton.setText(context.getString(R.string.cancel_text));
        cancelButton.setBackgroundResource(R.drawable.transparentbutton);
        buttonLayout.addView(cancelButton);
        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)cancelButton.getLayoutParams();
        cparams.width=42*bparams.width/100;
        cparams.height=Global.buttonHeight;
        cparams.gravity=Gravity.CENTER;
        cancelButton.setLayoutParams(cparams);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPager=-1;
                dismiss();
            }
        });

        confirmButton=new Button(context);
        confirmButton.setTextSize(Global.fontSize);
        confirmButton.setTextColor(Color.parseColor(Global.textColor));
        confirmButton.setText(context.getString(R.string.confirmText));
        confirmButton.setBackgroundResource(R.drawable.roundyellowbutton);
        buttonLayout.addView(confirmButton);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)confirmButton.getLayoutParams();
        iparams.leftMargin=5 * bparams.width/100;
        iparams.width=cparams.width;
        iparams.height=cparams.height;
        iparams.gravity=Gravity.CENTER;
        confirmButton.setLayoutParams(iparams);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout = new LinearLayout(context);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        dialogWidth = 85 * Global.width / 100;
        dialogHeight = 65 * Global.height / 100;
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        this.getWindow().setLayout(dialogWidth, dialogHeight);
        makeHeader();
        makePagers();
        makeButtons();
    }
}
