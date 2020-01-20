package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DialPad extends  LinearLayout
{
    private String title,cancelTitle,acceptTile;
    Context context=null;
    TextView inputText=null;
    Button cancelButton,acceptButton;
    DialPadListener listener=null;
    int dialWidth=0,dialHeight=0;
    private boolean displayDot=false;
    private boolean displayPassword=false;
    private String realValueInput="";
    public void enablePassword()
    {
        displayPassword=true;
    }

    public void enableDot()
    {
        displayDot=true;
        removeAllViews();
        drawPad();
    }

    void setListener(DialPadListener d)
    {
        listener=d;
    }

    String getInputValue()
    {
        return realValueInput;
    }

    void drawPad()
    {
        FrameLayout.LayoutParams fparams=new FrameLayout.LayoutParams(dialWidth,dialHeight);
        setLayoutParams(fparams);
        if(title.length()!=0) {
            TextView t1 = new TextView(context);
            t1.setText(title);
            t1.setTextSize(Global.bigFontSize+2);
            t1.setTextColor(Color.parseColor(Global.textColor));
            addView(t1);
            LinearLayout.LayoutParams t1params = (LinearLayout.LayoutParams) t1.getLayoutParams();
            t1params.width = 70 * fparams.width / 100;
            t1params.height = 10 * fparams.height / 100;
            t1params.leftMargin = 30 * fparams.width / 100;
            t1params.topMargin = 2 * fparams.height / 100;
            t1.setLayoutParams(t1params);
        }
        inputText=new TextView(context);
        inputText.setTextSize(Global.bigFontSize);
        inputText.setTextColor(Color.parseColor(Global.textColor));
        inputText.setBackgroundResource(R.drawable.roundedittext);
        addView(inputText);

        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)inputText.getLayoutParams();
        iparams.leftMargin=10 * fparams.width/100;
        iparams.rightMargin=10 * fparams.width/100;
        iparams.topMargin=2 * fparams.height/100;
        iparams.height=18 * dialHeight/100;
        if(iparams.height>Global.buttonHeight)
            iparams.height=Global.buttonHeight;
        iparams.width=70 * fparams.width/100;
        inputText.setLayoutParams(iparams);
        inputText.setPadding(iparams.width/10,iparams.height/8,iparams.width/40,iparams.height/20);
        TableLayout tableLayout=new TableLayout(context);
        addView(tableLayout);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)tableLayout.getLayoutParams();
        tparams.leftMargin=iparams.leftMargin;
        tparams.topMargin=iparams.topMargin/2;
        tparams.width=iparams.width;
        tableLayout.setLayoutParams(tparams);

        TableRow row[]=new TableRow[4];
        int buttonCount=1;
        TableRow.LayoutParams bparams=null;
        for(int i=0;i<row.length;i++)
        {
            row[i]=new TableRow(context);
            tableLayout.addView(row[i]);
            TableLayout.LayoutParams rparams=(TableLayout.LayoutParams)row[i].getLayoutParams();
            rparams.width=tparams.width;
            rparams.height=95*iparams.height/100;
            row[i].setLayoutParams(rparams);

            if(i<3)
            {
                for(int j=0;j<3;j++) {
                    Button bt = new Button(context);
                    bt.setText("" + (buttonCount));
                    bt.setTag(new Integer(buttonCount));
                    bt.setBackgroundResource(R.drawable.transparentbutton);
                    row[i].addView(bt);
                    if(i==0 && j==0) {
                        bparams = (TableRow.LayoutParams) bt.getLayoutParams();
                        bparams.leftMargin = tparams.width / 80;
                        bparams.width = 32 * tparams.width / 100;
                        bparams.height = 90* rparams.height/100;
                        bparams.bottomMargin = fparams.height / 80;
                    }
                    bt.setLayoutParams(bparams);
                    bt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String itext=realValueInput;
                            itext=itext+(Integer)v.getTag();
                            setValue(itext);
                            if(listener!=null)
                                listener.onChange(itext);
                        }
                    });
                    buttonCount++;
                }
            }
            else
            {
                Button dot=null;
                if(displayDot)
                {

                    final char cc='.';

                    dot=new Button(context);
                    dot.setText(""+cc);
                    dot.setBackgroundResource(R.drawable.transparentbutton);
                    row[i].addView(dot);
                    dot.setLayoutParams(bparams);
                    dot.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String itext=realValueInput;
                            if(itext.length()!=0 && !itext.contains(""+cc))
                                setValue(itext+cc);
                        }
                    });
                }
                Button bt1=new Button(context);
                bt1.setText("0");
                bt1.setTag(new Integer(0));
                bt1.setBackgroundResource(R.drawable.transparentbutton);
                row[i].addView(bt1);
                bt1.setLayoutParams(bparams);
                bt1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String itext=realValueInput;
                        //if(itext.length()>0)
                        itext=itext+(Integer)v.getTag();
                        setValue(itext);
                        if(listener!=null)
                            listener.onChange(itext);
                    }
                });
                ImageButton bspace=new ImageButton(context);
                //   ImageView bspace=new ImageView(context);
                Bitmap bm3= BitmapFactory.decodeResource(context.getResources(),R.drawable.backspace);

                int newHeight=40 * bparams.height/100;
                int newWidth=50 * bparams.width/100;
                bm3=bm3.createScaledBitmap(bm3,newWidth,newHeight,true);

                bspace.setImageBitmap(bm3);
                bspace.setBackgroundResource(R.drawable.transparentbutton);
                row[i].addView(bspace);
                TableRow.LayoutParams backparams=(TableRow.LayoutParams)bspace.getLayoutParams();
                backparams.leftMargin=bparams.leftMargin;
                backparams.height=bparams.height;
                backparams.span=displayDot?1:2;
                bspace.setLayoutParams(backparams);
                bspace.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String itext=realValueInput;
                        if(itext.length()>0)
                            itext=itext.substring(0,itext.length()-1);
                        setValue(itext);
                        if(listener!=null)
                            listener.onChange(itext);
                    }
                });
            }
        }

        if(title.length()!=0 && cancelTitle.length()!=0) {
            LinearLayout buttonLayout = new LinearLayout(context);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            addView(buttonLayout);
            LinearLayout.LayoutParams buttonParams = (LinearLayout.LayoutParams) buttonLayout.getLayoutParams();
            buttonParams.leftMargin = tparams.leftMargin;
            buttonParams.width = tparams.width;
            buttonParams.topMargin = 2 * tparams.topMargin;
            buttonParams.height = iparams.height;
            buttonLayout.setLayoutParams(buttonParams);

            cancelButton = new Button(context);
            cancelButton.setText(cancelTitle);
            cancelButton.setBackgroundResource(R.drawable.transparentbutton);
            buttonLayout.addView(cancelButton);
            LinearLayout.LayoutParams c1params = (LinearLayout.LayoutParams) cancelButton.getLayoutParams();
            c1params.leftMargin = 3 * tparams.width / 100;
            c1params.width = 46 * tparams.width / 100;
            c1params.height = 2 * iparams.height / 3;
            cancelButton.setLayoutParams(c1params);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.cancelClicked();
                }
            });

            Button continueButton = new Button(context);
            continueButton.setText(acceptTile);
            continueButton.setBackgroundResource(R.drawable.roundyellowbutton);
            buttonLayout.addView(continueButton);
            continueButton.setLayoutParams(c1params);
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.acceptClicked();
                }
            });
        }
    }


    public DialPad(Context ctx,String t,String c,String a)
    {
        super(ctx);
        dialWidth=85*Global.width/100;
        dialHeight=80 * Global.height/100;
        context=ctx;
        title=t;
        cancelTitle=c;
        acceptTile=a;

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.parseColor(Global.backgroundColor));
        drawPad();
    }

    public DialPad(Context ctx,String t,String c,String a,int dw,int dh)
    {
        super(ctx);
        dialWidth=dw;
        dialHeight=dh;
        context=ctx;
        title=t;
        cancelTitle=c;
        acceptTile=a;

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.parseColor(Global.backgroundColor));
        drawPad();
    }

    void setValue(String value)
    {
        realValueInput=value;
        if(displayPassword)
        {
            String passValue="";
            for(int i=0;i<value.length()-1;i++)
                passValue+="*";
            if(value.length()!=0)            passValue+=value.charAt(value.length()-1);
            inputText.setText(passValue);
        }
        else
            inputText.setText(value);
    }
}
