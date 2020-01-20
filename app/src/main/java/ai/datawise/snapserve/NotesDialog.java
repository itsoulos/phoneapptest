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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class NotesDialog extends Dialog {
    Context context = null;
    int dialogWidth = 0;
    int dialogHeight = 0;
    LinearLayout mainLayout = null;
    Button cancelButton, confirmButton;
    EditText textArea = null;
    String notes = "";

    public String getNotes() {
        return notes;
    }


    public NotesDialog(Context ctx,String n)
    {
        super(ctx);
        if(n==null) notes="";
        else
            notes=n;
        context=ctx;
    }

    void makeHeader()
    {
        TextView t1=new TextView(context);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setTextSize(Global.bigFontSize);
        t1.setText(context.getString(R.string.notesText));
        mainLayout.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=dialogWidth/6;
        t1params.width=dialogWidth/2;
        t1params.height=dialogHeight/10;
        t1.setLayoutParams(t1params);
        t1.setGravity(Gravity.CENTER);
    }

    void makeNotes()
    {
        TextInputLayout tl=new TextInputLayout(context);
        mainLayout.addView(tl);
        LinearLayout.LayoutParams tlparams=(LinearLayout.LayoutParams)tl.getLayoutParams();
        tlparams.leftMargin=5 * dialogWidth/100;
        tlparams.topMargin=5 * dialogHeight/100;
        tlparams.width=80 * dialogWidth/100;
        tlparams.height=50 * dialogHeight/100;
        tl.setLayoutParams(tlparams);
        textArea=new EditText(context);
        textArea.setTextColor(Color.parseColor(Global.textColor));
        textArea.setTextSize(Global.fontSize);
        textArea.setLines(8);
        textArea.setHint(context.getString(R.string.writeNotesHint));
        textArea.setBackgroundResource(R.drawable.roundedittext);
        System.out.println("GIANNIS PARAM NOTES ARE "+notes);
        if(notes.length()!=0 && !notes.equals("null"))
            textArea.setText(notes);
        tl.addView(textArea);
        textArea.setPadding(5*dialogWidth/100,Global.buttonHeight/6,5*dialogWidth/100,Global.buttonHeight/6);
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
                notes="";
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
                notes=textArea.getText().toString();
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
        makeNotes();
        makeButtons();
    }
}
