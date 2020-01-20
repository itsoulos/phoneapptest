package ai.datawise.snapserve;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OrderCloseDialog extends Dialog {
    int dialogWidth=0,dialogHeight=0;
    LinearLayout mainLayout=null;
    private int closeFlag=0;
    private  Context context=null;
    public static final int CLOSE_OK=1;
    public static final int CLOSE_CANCEL=2;
    private String textMessage="";
    private String button1Message="";
    private String button2Message="";

    int getCloseFlag()
    {
        return closeFlag;
    }

    public OrderCloseDialog(Context ctx) {
        super(ctx);
        context=ctx;
        textMessage=context.getString(R.string.closeWindowMessage);
        button1Message=context.getString(R.string.yesText);
        button2Message=context.getString(R.string.noText);
    }

    public OrderCloseDialog(Context ctx,String message,String button1,String button2)
    {
        super(ctx);
        context=ctx;
        textMessage=message;
        button1Message=button1;
        button2Message=button2;
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
        icon.setImageBitmap(Global.dialogIcon);
        h.addView(icon);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)icon.getLayoutParams();
        iparams.leftMargin=5 * dialogWidth/100;
        iparams.width=Global.datawiseIcon.getWidth();
        iparams.height=70 * hparams.height/100;
        iparams.gravity=Gravity.CENTER;
        icon.setLayoutParams(iparams);

        TextView t1=new TextView(context);
        t1.setTextSize(Global.bigFontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setTypeface(Typeface.DEFAULT_BOLD);
        t1.setText(context.getString(R.string.informationText));
        h.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.width=60 * dialogWidth/100;
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
        info.setText(textMessage);
        info.setBackgroundResource(R.drawable.roundedittext);
        mainLayout.addView(info);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)info.getLayoutParams();
        iparams.leftMargin=5 * dialogWidth/100;
        iparams.topMargin=5 * dialogHeight/100;
        iparams.width=80 * dialogWidth/100;
        iparams.height=40 * dialogHeight/100;
        info.setLayoutParams(iparams);
        info.setPadding(2*dialogWidth/100,5 * dialogHeight/100,2*dialogWidth/100,5*dialogHeight/100);
    }

    void makeButtons()
    {
        LinearLayout buttonLayout=new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(buttonLayout);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)buttonLayout.getLayoutParams();
        bparams.leftMargin=5*dialogWidth/100;
        bparams.topMargin=5 * dialogHeight/100;
        bparams.width=80 * dialogWidth/100;
        bparams.height=25 * dialogHeight/100;
        buttonLayout.setLayoutParams(bparams);

        Button yesButton=new Button(context);
        yesButton.setTextColor(Color.parseColor(Global.textColor));
        yesButton.setTextSize(Global.fontSize);
        yesButton.setText(button1Message);
        yesButton.setBackgroundResource(R.drawable.transparentbutton);
        buttonLayout.addView(yesButton);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)yesButton.getLayoutParams();
        lparams.leftMargin=2 * dialogWidth/100;
        lparams.topMargin=20*bparams.height/100;
        lparams.height=65 *bparams.height/100;
        if(button2Message.length()==0)
            lparams.width=90 * bparams.width/100;
        else
            lparams.width=45 * bparams.width/100;
        yesButton.setLayoutParams(lparams);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFlag=CLOSE_OK;
                dismiss();
            }
        });

        if(button2Message.length()!=0) {
            Button noButton = new Button(context);
            noButton.setTextColor(yesButton.getCurrentTextColor());
            noButton.setTextSize(Global.fontSize);
            noButton.setText(button2Message);
            noButton.setBackgroundResource(R.drawable.roundyellowbutton);
            buttonLayout.addView(noButton);
            noButton.setLayoutParams(lparams);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeFlag = CLOSE_CANCEL;
                    dismiss();
                }
            });
        }
        TextView t=new TextView(context);
        t.setText("     ");
        mainLayout.addView(t);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainLayout = new LinearLayout(context);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        dialogWidth = 85 * Global.width / 100;
        dialogHeight = 40 * Global.height / 100;
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        this.getWindow().setLayout(dialogWidth, dialogHeight);
        makeHeader();
        makeText();
        makeButtons();
    }
}
