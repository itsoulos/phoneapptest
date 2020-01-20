package ai.datawise.snapserve;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplitDialog extends Dialog {
    ArrayList<BasketItem> items=new ArrayList<BasketItem>();
    Context context=null;
    int dialogWidth=0,dialogHeight=0;
    LinearLayout mainLayout=null;
    private ListView topView=null;
    private TabLayout tab=null;
    private ListView tabListView=null;
    private OrderStore order=null;
    private int selectedSplit=-1;
    private CheckedAdapter checkedAdapter=null;
    private CheckedAdapter tabAdapter=null;
    private JSONObject copyOrderInfo=new JSONObject();
    private boolean isSplit=false;

    public SplitDialog(Context ctx, ArrayList<BasketItem> bitems,OrderStore o) {
        super(ctx);
        context=ctx;
        items=bitems;
        order=o;
    }

    public boolean getSplit()
    {
        return isSplit;
    }

    void makeHeader()
    {
        LinearLayout header=new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(Color.WHITE);
        mainLayout.addView(header);
        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)header.getLayoutParams();
        hparams.width=dialogWidth;
        hparams.height=8 * dialogHeight/100;
        header.setLayoutParams(hparams);


        TextView t=new TextView(context);
        t.setTextColor(Color.parseColor(Global.textColor));
        t.setTextSize(Global.bigFontSize);
        t.setText(context.getString(R.string.splitOrderText));
        header.addView(t);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
        tparams.leftMargin=5*hparams.width/100;
        tparams.topMargin=2*hparams.height/100;
        tparams.width=40 * hparams.width/100;
        tparams.height=90 * hparams.height/100;
        tparams.gravity= Gravity.CENTER;
        t.setLayoutParams(tparams);
        t.setGravity(Gravity.CENTER_VERTICAL);

        ImageView rightImage=new ImageView(context);
        rightImage.setImageBitmap(Global.blackxIcon);
        header.addView(rightImage);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)rightImage.getLayoutParams();
        iparams.rightMargin=tparams.leftMargin;
        iparams.topMargin=tparams.topMargin/2;
        iparams.gravity=Gravity.CENTER;
        iparams.width=Global.blackxIcon.getWidth();
        iparams.height=tparams.height;
        iparams.leftMargin=hparams.width-tparams.leftMargin-tparams.width-3*iparams.rightMargin-iparams.width;
        rightImage.setLayoutParams(iparams);
        rightImage.setClickable(true);
        rightImage.setOnTouchListener(Global.touchListener);
        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSplit();
            }
        });

        View line=new View(context);
        line.setBackgroundResource(R.drawable.shadowline);
        mainLayout.addView(line);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)line.getLayoutParams();
        lparams.width=dialogWidth;
        lparams.height=2;
        line.setLayoutParams(lparams);
    }

    void makeTopView()
    {
        topView=new ListView(context);
        mainLayout.addView(topView);
        ArrayList<String> tt=new ArrayList<String>();
        for(int i=0;i<items.size();i++)
            tt.add(items.get(i).getName());
        checkedAdapter=new CheckedAdapter(context,tt,80*dialogWidth/100,null);
        topView.setAdapter(checkedAdapter);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)topView.getLayoutParams();
        tparams.topMargin=2 * dialogHeight/100;
        tparams.leftMargin=5 * dialogWidth/100;
        tparams.width= 80 * dialogWidth/100;
        tparams.height=25 * dialogHeight/100;
        topView.setLayoutParams(tparams);
    }

    void makeButtonArea()
    {
        LinearLayout blayout=new LinearLayout(context);
        blayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(blayout);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)blayout.getLayoutParams();
        bparams.width=dialogWidth;
        bparams.height=8 * dialogHeight/100;
        bparams.topMargin=2 * dialogHeight/100;
        blayout.setLayoutParams(bparams);
        ImageButton b1,b2,b3,b4;

        b2=new ImageButton(context);
        b2.setBackgroundResource(R.drawable.transparentbutton);
        blayout.addView(b2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedSplit!=-1)
                {
                    ArrayList<Integer> updateList=new ArrayList<Integer>();
                    ArrayList<Integer> X=checkedAdapter.getAllSelected();
                    for(int ik=0;ik<X.size();ik++) {
                        int k =X.get(ik);// checkedAdapter.getSelected();
                        int count = 0;
                        int bpos = 0;
                        for (int i = 0; i < items.size(); i++) {
                            int msplit = order.getItemSplit(items.get(i).getCurrentItemPos());
                            if (msplit == 0) {
                                count++;
                                if (count == k + 1) {
                                    updateList.add(i);
                                }
                            }
                        }

                    }
                    for(int i=0;i<updateList.size();i++)
                    {
                        BasketItem bitem = items.get(updateList.get(i));
                        int bitempos = bitem.getCurrentItemPos();
                        order.setSplit(selectedSplit, bitempos);
                    }

                    updateAdapters();

                }
            }
        });

        b1=new ImageButton(context);
        b1.setBackgroundResource(R.drawable.transparentbutton);
        blayout.addView(b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedSplit!=-1)
                {
                    //move all the items to the currentSplit
                    for(int i=0;i<items.size();i++)
                    {
                        int bitempos=items.get(i).getCurrentItemPos();
                        int msplit=order.getItemSplit(items.get(i).getCurrentItemPos());
                        if(msplit==0)
                        {
                            order.setSplit(selectedSplit,bitempos);
                        }
                    }
                    updateAdapters();
                }
            }
        });



        b3=new ImageButton(context);
        b3.setBackgroundResource(R.drawable.transparentbutton);
        blayout.addView(b3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedSplit!=-1)
                {
                    ArrayList<Integer> updateList=new ArrayList<Integer>();

                    ArrayList<Integer> X=tabAdapter.getAllSelected();
                    for(int ik=0;ik<X.size();ik++) {
                        int k = X.get(ik);
                        int count = 0;

                        for (int i = 0; i < items.size(); i++) {
                            int msplit = order.getItemSplit(items.get(i).getCurrentItemPos());
                            if (msplit == selectedSplit) {
                                count++;
                                if (count == k + 1) {
                                    updateList.add(i);
                                    // order.setSplit(0, items.get(i).getCurrentItemPos());
                                }
                            }

                        }
                    }
                    for(int i=0;i<updateList.size();i++)
                    {
                        BasketItem bitem = items.get(updateList.get(i));
                        int bitempos = bitem.getCurrentItemPos();
                        order.setSplit(0, bitempos);
                    }
                    updateAdapters();

                }
                //move the selected item from the tablist to the current list
            }
        });

        b4=new ImageButton(context);
        b4.setBackgroundResource(R.drawable.transparentbutton);
        blayout.addView(b4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move all the items from the tablist to the current list
                if(selectedSplit!=-1)
                {

                    for(int i=0;i<items.size();i++)
                    {
                        int msplit=order.getItemSplit(items.get(i).getCurrentItemPos());
                        if(msplit==selectedSplit) {
                            order.setSplit(0,items.get(i).getCurrentItemPos());
                        }
                    }
                    updateAdapters();
                }
            }
        });

        LinearLayout.LayoutParams b1params=(LinearLayout.LayoutParams)b1.getLayoutParams();
        b1params.leftMargin=2 * bparams.width/100;
        b1params.width=20 * bparams.width/100;
        b1params.gravity=Gravity.CENTER;
        b1params.height=80 * bparams.height/100;
        b1.setLayoutParams(b1params);
        b2.setLayoutParams(b1params);
        b3.setLayoutParams(b1params);
        b4.setLayoutParams(b1params);


        int neww=35 * b1params.width/100;
        int newh=35 * b1params.height/100;
        if(Global.bluedownIcon==null) {
            Global.bluedownIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluedown);
            Global.bluedownIcon = Global.bluedownIcon.createScaledBitmap(Global.bluedownIcon, neww, newh, true);
            Global.doublebluedownIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.doublebluedown);
            Global.doublebluedownIcon = Global.doublebluedownIcon.createScaledBitmap(Global.doublebluedownIcon, neww, newh, true);
            Global.blueupIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.blueup);
            Global.blueupIcon = Global.blueupIcon.createScaledBitmap(Global.blueupIcon, neww, newh, true);
            Global.doubleupIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.doubleblueup);
            Global.doubleupIcon = Global.doubleupIcon.createScaledBitmap(Global.doubleupIcon, neww, newh, true);
        }

        b2.setImageBitmap(Global.bluedownIcon);
        b1.setImageBitmap(Global.doublebluedownIcon);
        b3.setImageBitmap(Global.blueupIcon);
        b4.setImageBitmap(Global.doubleupIcon);
    }

    void makeSplitArea()
    {
        LinearLayout split=new LinearLayout(context);
        split.setOrientation(LinearLayout.VERTICAL);
        split.setBackgroundResource(R.drawable.backwithborder);
        mainLayout.addView(split);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)split.getLayoutParams();
        sparams.topMargin=2 * dialogHeight/100;
        sparams.leftMargin= 3 * dialogWidth/100;
        sparams.width=85 * dialogWidth/100;
        sparams.height=40 * dialogHeight/100;
        split.setLayoutParams(sparams);
        tab=new TabLayout(context);
        tab.addTab(tab.newTab().setText("+"));
        split.addView(tab);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)tab.getLayoutParams();
        tparams.width=sparams.width-16;
        tparams.topMargin=4;
        tparams.leftMargin=4;//for the backwithborder xml
        tparams.height=15 * sparams.height/100;
        tparams.gravity=Gravity.CENTER;
        tab.setLayoutParams(tparams);

        tabListView=new ListView(context);
        split.addView(tabListView);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)tabListView.getLayoutParams();
        lparams.width=tparams.width;
        lparams.leftMargin=tparams.leftMargin;
        lparams.height=sparams.height-tparams.height;
        tabListView.setLayoutParams(lparams);

        tab.setSelectedTabIndicatorColor(Color.parseColor(Global.tintColor));


        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab t) {
                if(!t.getText().toString().equals("+")) {
                    int pos=Integer.parseInt(t.getText().toString());
                    selectedSplit=pos;
                    updateAdapters();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab t) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab t) {
                if(t.getText().toString().equals("+")) {
                    tab.addTab(tab.newTab().setText(""+tab.getTabCount()), tab.getTabCount()-1);
                    order.setSplit(tab.getTabCount()-1);
                }
                if(!t.getText().toString().equals("+")) {
                    int pos=Integer.parseInt(t.getText().toString());
                    selectedSplit=pos;
                    updateAdapters();
                }

            }
        });
    }

    void initSplits()
    {
        tab.addTab(tab.newTab().setText(""+tab.getTabCount()), tab.getTabCount()-1);
    }

    void cancelSplit()
    {
        order.setOrderInfo(copyOrderInfo);

        isSplit=false;
        dismiss();
        //reset the order
    }

    void makeButtons()
    {
        LinearLayout blayout=new LinearLayout(context);
        blayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(blayout);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)blayout.getLayoutParams();
        bparams.width=dialogWidth;
        bparams.height=8 * dialogHeight/100;
        blayout.setLayoutParams(bparams);

        Button cancelButton=new Button(context);
        cancelButton.setText(context.getString(R.string.cancel_text));
        cancelButton.setTextColor(Color.parseColor(Global.textColor));
        cancelButton.setTextSize(Global.fontSize);
        cancelButton.setBackgroundResource(R.drawable.transparentbutton);
        blayout.addView(cancelButton);

        final Button confirmButton=new Button(context);
        confirmButton.setText(context.getString(R.string.confirmText));
        confirmButton.setTextColor(Color.parseColor(Global.textColor));
        confirmButton.setBackgroundResource(R.drawable.roundyellowbutton);
        confirmButton.setTextSize(Global.fontSize);
        blayout.addView(confirmButton);

        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)cancelButton.getLayoutParams();
        cparams.leftMargin=7 * bparams.width/100;
        cparams.width=35 * bparams.width/100;
        cparams.height=80 * bparams.height/100;
        cparams.gravity=Gravity.CENTER;
        cancelButton.setLayoutParams(cparams);
        confirmButton.setLayoutParams(cparams);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSplit();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //just accept the order
                isSplit=true;
                boolean okflag=true;
                int zeroCount=0;
                //check for zero split
                for(int i=0;i<items.size();i++) {
                    int msplit=order.getItemSplit(items.get(i).currentItemPos);
                    if(msplit==0) zeroCount++;
                }

                if(order.getOrderSplit()<=1 ||  zeroCount==items.size() || items.size()==0)
                {
                    order.setSplit(0)   ;
                    for(int i=0;i<items.size();i++)
                        order.setSplit(0,items.get(i).getCurrentItemPos());
                }
                else
                if(zeroCount>0 && order.getOrderSplit()>1)
                {
                    //orphan items
                    okflag=false;
                }

                int sameSplit=0;
                int osplit=(items.size()!=0)?order.getItemSplit(items.get(0).getCurrentItemPos()):0;
                for(int i=0;i<items.size();i++)
                {
                    int msplit=order.getItemSplit(items.get(i).currentItemPos);
                    if(msplit==osplit) sameSplit++;
                }
                if(sameSplit==items.size())
                {
                    order.setSplit(0)   ;
                    for(int i=0;i<items.size();i++)
                        order.setSplit(0,items.get(i).getCurrentItemPos());
                }

                if(okflag)  dismiss();
                else
                {
                    OrderCloseDialog dialog=new OrderCloseDialog(context,context.getString(R.string.splitWarning),context.getString(R.string.okGotItText),"");
                    dialog.show();
                }
            }
        });
    }

    void updateAdapters()
    {
        ArrayList<String> tt=new ArrayList<String>();
        ArrayList<String> tt2=new ArrayList<String>();

        for(int i=0;i<items.size();i++)
        {
            int msplit=order.getItemSplit(items.get(i).getCurrentItemPos());
            if(msplit==selectedSplit) tt2.add(items.get(i).getName());
            if(msplit==0) tt.add(items.get(i).getName());
        }
        checkedAdapter=new CheckedAdapter(context,tt,80*dialogWidth/100,null,true);
        topView.setAdapter(checkedAdapter);
        tabAdapter=new CheckedAdapter(context,tt2,80*dialogWidth/100,null,true);
        tabListView.setAdapter(tabAdapter);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainLayout = new LinearLayout(context);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        dialogWidth = 98 * Global.width / 100;
        dialogHeight = 98 * Global.height / 100;
        mainLayout.setBackgroundColor(Color.WHITE);
        this.getWindow().setLayout(dialogWidth, dialogHeight);
        try {
            copyOrderInfo=new JSONObject(order.getOrderInfo().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        makeHeader();
        makeTopView();
        makeButtonArea();
        makeSplitArea();
        makeButtons();
        if(order.getOrderSplit()>0) {
            mainLayout.post(new Runnable() {
                public void run() {
                    //create splits
                    for(int i=1;i<=order.getOrderSplit();i++)
                        tab.addTab(tab.newTab().setText(""+tab.getTabCount()), tab.getTabCount()-1);
                    selectedSplit=1;
                    updateAdapters();

                }
            });
        }
        else
        {
            mainLayout.post(new Runnable() {
                public void run() {
                    if(items.size()!=0) {
                        order.setSplit(1);
                        initSplits();
                        selectedSplit = 1;
                        updateAdapters();
                    }
                }});

        }
    }
}
