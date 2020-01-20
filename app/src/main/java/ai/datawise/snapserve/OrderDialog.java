package ai.datawise.snapserve;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;


public class OrderDialog extends Dialog
{

    String tableName="";
    LinearLayout mainLayout=null;
    Context context=null;
    Spinner spinner=null;
    DialPad dialPad=null;
    Button continueButton=null;

    public OrderDialog(String name,Context a)
    {
        super(a);
        tableName=name;
        context=a;
    }


    int getText()
    {
        if(dialPad.getInputValue().length()==0) return 0;
        return (int)(Double.parseDouble(dialPad.getInputValue()));
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainLayout=new LinearLayout(context);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        final int dialogWidth=85 * Global.width/100;
        final int dialogHeight=80 * Global.height/100;
        mainLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        this.getWindow().setLayout(dialogWidth,dialogHeight);

        TextView t=new TextView(context);
        t.setText(context.getString(R.string.neworderText));
        t.setTextSize(Global.fontSize+12);
        mainLayout.addView(t);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
        tparams.leftMargin=Global.width/5;
        t.setLayoutParams(tparams);

        LinearLayout row1=new LinearLayout(context);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(row1);
        LinearLayout.LayoutParams rparams=(LinearLayout.LayoutParams)row1.getLayoutParams();
        rparams.leftMargin=tparams.leftMargin+Global.width/10;
        row1.setLayoutParams(rparams);


        ImageView tableIcon=new ImageView(context);
        tableIcon.setImageBitmap(Global.grayTable);
        row1.addView(tableIcon);

        TextView tableText=new TextView(context);
        tableText.setText(tableName);
        row1.addView(tableText);
        LinearLayout.LayoutParams tableTextParams=(LinearLayout.LayoutParams)tableText.getLayoutParams();
        tableTextParams.leftMargin=Global.width/40;
        tableText.setLayoutParams(tableTextParams);

        ArrayList<String> langArray=new ArrayList<String>();
        langArray.add("English");
        langArray.add("Greek");
        PosSpinnerAdapter adapter=new PosSpinnerAdapter(context,langArray);
        spinner=new Spinner(context);
        spinner.setBackgroundColor(Color.rgb(247,247,248));

        spinner.setBackgroundResource(R.drawable.spinnerbg);
        spinner.setPrompt("Language:");
        spinner.setAdapter(adapter);
        mainLayout.addView(spinner);

        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)spinner.getLayoutParams();
        sparams.topMargin=dialogHeight/40;
        sparams.leftMargin=Global.width/20;
        sparams.width=65 * Global.width/100;
        sparams.height=Global.height/12;
        spinner.setLayoutParams(sparams);

        dialPad=new DialPad(context,"","","",dialogWidth,50 * dialogHeight/100);
        //dialPad.enableDot();
        mainLayout.addView(dialPad);

        LinearLayout buttonLayout=new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(buttonLayout);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)buttonLayout.getLayoutParams();
        t1params.topMargin=dialogHeight/40;
        t1params.leftMargin=sparams.leftMargin/2;
        t1params.width=70 * Global.width/100;

        buttonLayout.setLayoutParams(t1params);

        Button cancelButton=new Button(context);
        cancelButton.setText(context.getString(R.string.cancel_text));
        cancelButton.setBackgroundResource(R.drawable.transparentbutton);
        buttonLayout.addView(cancelButton);
        LinearLayout.LayoutParams c1params=(LinearLayout.LayoutParams)cancelButton.getLayoutParams();
        c1params.leftMargin=3 * dialogWidth/100;
        c1params.width=37 * dialogWidth/100;
        c1params.height=dialogHeight/10;
        cancelButton.setLayoutParams(c1params);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPad.setValue("0");
                continueButton.setEnabled(false);
                dismiss();
            }
        });

        continueButton=new Button(context);
        continueButton.setText(context.getString(R.string.continuteToOrderText));
        continueButton.setBackgroundResource(R.drawable.roundyellowbutton);
        buttonLayout.addView(continueButton);
        continueButton.setLayoutParams(c1params);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueButton.setEnabled(false);
                dismiss();
            }
        });
    }


    public String getLanguage()
    {
        String s=spinner.getSelectedItem().toString();
        return Global.getLanguageInitials(s);
    }
}
