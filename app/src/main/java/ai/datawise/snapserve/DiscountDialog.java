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
import android.widget.ListView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class DiscountDialog extends Dialog {
    Context context=null;
    int dialogWidth=0;
    int dialogHeight=0;
    LinearLayout mainLayout=null;
    Button cancelButton,confirmButton;
    ListView discountList=null;
    DiscountAdapter adapter=null;
    ArrayList<BasketItem> basketItems=new ArrayList<BasketItem>();
    OrderStore order=null;
    boolean cancelPressed=false;

    double getDiscountPercent()
    {
        if(cancelPressed) return -1.0;
        return adapter.getSelectedPercent();
    }

    double getDiscountValue()
    {
        if(cancelPressed) return -1.0;
        return adapter.getSelectedDiscount();
    }

    public DiscountDialog( Context ctx, ArrayList<BasketItem> b,OrderStore o)
    {
        super(ctx);
        context=ctx;
        basketItems=b;
        order=o;
    }

    void makeHeader()
    {
        TextView t1=new TextView(context);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setTextSize(Global.bigFontSize);
        t1.setText(context.getString(R.string.addDiscountText));
        mainLayout.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=dialogWidth/6;
        t1params.width=dialogWidth/2;
        t1params.height=dialogHeight/10;
        t1.setLayoutParams(t1params);
        t1.setGravity(Gravity.CENTER);
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
        bparams.height=110 * Global.buttonHeight/100;
        buttonLayout.setLayoutParams(bparams);

        cancelButton=new Button(context);
        cancelButton.setTextSize(Global.fontSize);
        cancelButton.setTextColor(Color.parseColor(Global.textColor));
        cancelButton.setText(context.getString(R.string.cancel_text));
        cancelButton.setBackgroundResource(R.drawable.transparentbutton);
        buttonLayout.addView(cancelButton);
        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)cancelButton.getLayoutParams();
        cparams.width=42*bparams.width/100;
        cparams.height=80*Global.buttonHeight/100;
        cparams.gravity=Gravity.CENTER;
        cancelButton.setLayoutParams(cparams);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPressed=true;
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

    void makeDiscountTab()
    {
        TabLayout tab=new TabLayout(context);
        tab.addTab(tab.newTab().setText("%"+context.getString(R.string.percentDiscountText)));
        tab.addTab(tab.newTab().setText(Global.currency+context.getString(R.string.valueDiscountText)));
        mainLayout.addView(tab);
        final ViewPager p=new ViewPager(context);
        adapter=new  DiscountAdapter(context,basketItems,order,95 *dialogWidth/100,50 *dialogHeight/100);
        p.setAdapter(adapter);
        mainLayout.addView(p);
        LinearLayout.LayoutParams pparams=(LinearLayout.LayoutParams)p.getLayoutParams();
        pparams.leftMargin=2 * dialogWidth/100;
        pparams.width=95 *dialogWidth/100;
        pparams.height=50 * dialogHeight/100;
        p.setLayoutParams(pparams);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getText().equals("%"+context.getString(R.string.percentDiscountText)))
                    p.setCurrentItem(0);
                else
                    p.setCurrentItem(1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
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
        dialogWidth = 90 * Global.width / 100;
        dialogHeight = 80 * Global.height / 100;
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        this.getWindow().setLayout(dialogWidth, dialogHeight);
        makeHeader();
        makeDiscountTab();
        makeButtons();
    }
}
