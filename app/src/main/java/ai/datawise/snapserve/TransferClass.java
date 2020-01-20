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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

public class TransferClass extends Dialog {
    Context context=null;
    int dialogWidth=0;
    int dialogHeight=0;
    LinearLayout mainLayout=null;
    Button cancelButton,confirmButton;
    private int selectedLevelId=-1;
    private String selectedTableCode="";
    TablePageAdapter tablePageAdapter;

    public int getSelectedLevelId() {
        return selectedLevelId;
    }
    public String getSelectedTableCode()
    {
        return selectedTableCode;
    }

    public TransferClass(@NonNull Context ctx) {
        super(ctx);
        context=ctx;
    }

    void makeHeader()
    {
        LinearLayout header=new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(header);
        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)header.getLayoutParams();
        hparams.width=dialogWidth;
        hparams.height=dialogHeight/10;
        header.setLayoutParams(hparams);
        TextView t1=new TextView(context);
        header.addView(t1);
        t1.setTextSize(Global.bigFontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setText(context.getString(R.string.transderOrderTitle));
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.topMargin=2 * dialogHeight/100;
        t1params.leftMargin=20 * hparams.width/100;
        t1params.width=80 * hparams.width/100;
        t1params.height=80 * hparams.height/100;
        t1.setLayoutParams(t1params);
    }

    void makeTabs() {
        TabLayout tab = new TabLayout(context);
        mainLayout.addView(tab);
        tab.setSmoothScrollingEnabled(true);
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < Global.levels.length(); i++) {
            try {

                tab.addTab(tab.newTab().setText(Global.levels.getJSONObject(i).getString("name")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final ViewPager p = new ViewPager(context);
        mainLayout.addView(p);
        LinearLayout.LayoutParams pparams = (LinearLayout.LayoutParams) p.getLayoutParams();
        pparams.leftMargin = 2 * dialogWidth / 100;
        pparams.width = 95 * dialogWidth / 100;
        pparams.height = 60 * dialogHeight / 100;
        p.setLayoutParams(pparams);
        tablePageAdapter=new TablePageAdapter(context,pparams.width,pparams.height);
        p.setAdapter(tablePageAdapter);
        try {
            selectedLevelId=Global.levels.getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int posistion=tab.getPosition();
                p.setCurrentItem(posistion);
                try {
                    selectedLevelId=Global.levels.getJSONObject(posistion).getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        bparams.height=12 * dialogHeight/100;
        buttonLayout.setLayoutParams(bparams);

        cancelButton=new Button(context);
        cancelButton.setTextSize(Global.fontSize);
        cancelButton.setTextColor(Color.parseColor(Global.textColor));
        cancelButton.setText(context.getString(R.string.cancel_text));
        cancelButton.setBackgroundResource(R.drawable.transparentbutton);
        buttonLayout.addView(cancelButton);
        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)cancelButton.getLayoutParams();
        cparams.width=42*bparams.width/100;
        cparams.height=10 * dialogHeight/100;
        cparams.gravity= Gravity.CENTER;
        cancelButton.setLayoutParams(cparams);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLevelId=-1;
                selectedTableCode="";
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
                selectedTableCode=tablePageAdapter.getSelectedTable();
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
        dialogWidth = 95 * Global.width / 100;
        dialogHeight = 95 * Global.height / 100;
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        this.getWindow().setLayout(dialogWidth, dialogHeight);
        makeHeader();
        makeTabs();
        makeButtons();
    }
}
