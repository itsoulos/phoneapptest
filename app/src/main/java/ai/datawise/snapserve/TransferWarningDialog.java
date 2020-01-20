package ai.datawise.snapserve;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class TransferWarningDialog extends Dialog {
    int dialogWidth=0,dialogHeight=0;
    LinearLayout mainLayout=null;
    private Context context=null;

    String transferMessage="";
    public TransferWarningDialog(@NonNull Context ctx) {
        super(ctx);
        context=ctx;
        transferMessage=context.getString(R.string.transferWarningMessage);
    }

    public TransferWarningDialog(@NonNull Context ctx,String t) {
        super(ctx);
        context=ctx;
        transferMessage=t;
    }

    void makeHeader()
    {
        LinearLayout h=new LinearLayout(context);
        h.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(h);
        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)h.getLayoutParams();
        hparams.width=dialogWidth;
        hparams.height=3 * Global.datawiseIcon.getHeight()/2;
        h.setLayoutParams(hparams);

        ImageView icon=new ImageView(context);
        icon.setImageBitmap(Global.datawiseIcon);
        h.addView(icon);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)icon.getLayoutParams();
        iparams.leftMargin=5 * dialogWidth/100;
        iparams.width=Global.datawiseIcon.getWidth();
        iparams.height=70 * hparams.height/100;
        iparams.gravity= Gravity.CENTER;
        icon.setLayoutParams(iparams);

        TextView t1=new TextView(context);
        t1.setTextSize(Global.fontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setTypeface(Typeface.DEFAULT_BOLD);
        t1.setText(context.getString(R.string.transferWarningTitle));
        h.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.width=80 * dialogWidth/100;
        t1params.height=70 * hparams.height/100;
        t1params.rightMargin=iparams.leftMargin;
        t1params.leftMargin=5 * dialogWidth/100;
        t1params.gravity= Gravity.CENTER;
        t1.setLayoutParams(t1params);

    }
    void makeText()
    {
        TextView info=new TextView(context);
        info.setTextSize(Global.fontSize);
        info.setTextColor(Color.BLACK);
        info.setText(transferMessage);
        info.setBackgroundResource(R.drawable.roundedittext);
        mainLayout.addView(info);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)info.getLayoutParams();
        iparams.leftMargin=5 * dialogWidth/100;
        iparams.topMargin=10 * dialogHeight/100;
        iparams.width=80 * dialogWidth/100;
        iparams.height=35 * dialogHeight/100;
        info.setLayoutParams(iparams);
        info.setPadding(2*dialogWidth/100,5 * dialogHeight/100,2*dialogWidth/100,5*dialogHeight/100);
    }

    void makeButtons() {
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(buttonLayout);
        LinearLayout.LayoutParams bparams = (LinearLayout.LayoutParams) buttonLayout.getLayoutParams();
        bparams.leftMargin = 5 * dialogWidth / 100;
        bparams.topMargin = 10 * dialogHeight / 100;
        bparams.width = 80 * dialogWidth / 100;
        bparams.height = 25 * dialogHeight / 100;
        buttonLayout.setLayoutParams(bparams);

        Button yesButton = new Button(context);
        yesButton.setTextColor(Color.parseColor(Global.textColor));
        yesButton.setTextSize(Global.fontSize);
        yesButton.setText(context.getString(R.string.okGotItText));
        yesButton.setBackgroundResource(R.drawable.transparentbutton);
        buttonLayout.addView(yesButton);
        LinearLayout.LayoutParams lparams = (LinearLayout.LayoutParams) yesButton.getLayoutParams();
        lparams.leftMargin = 2 * dialogWidth / 100;
        lparams.topMargin = 20 * bparams.height / 100;
        lparams.height = 65 * bparams.height / 100;
        lparams.width = 95 * bparams.width / 100;
        yesButton.setLayoutParams(lparams);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainLayout = new LinearLayout(context);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        dialogWidth = 85 * Global.width / 100;
        dialogHeight = 50 * Global.height / 100;
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        this.getWindow().setLayout(dialogWidth, dialogHeight);
        makeHeader();
        makeText();
        makeButtons();
    }
}
