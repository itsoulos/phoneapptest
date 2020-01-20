package ai.datawise.snapserve;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

public class ClockedInDialog extends Dialog implements  DialPadListener{
    Context context=null;
    int dialogWidth=0,dialogHeight=0;
    DialPad dialPad=null;
    LinearLayout mainLayout=null;
    String value="";
    String getValue()
    {
        return value;
    }

    public ClockedInDialog(Context ctx) {
        super(ctx);
        context=ctx;

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainLayout = new LinearLayout(context);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        dialogWidth = 80 * Global.width / 100;
        dialogHeight = 50 * Global.height / 100;
        dialPad=new DialPad(context,context.getString(R.string.clockin),
                context.getString(R.string.cancel_text),context.getString(R.string.checkedInText));
        dialPad.setListener(this);
        dialPad.enableDot();
        mainLayout.addView(dialPad);
    }

    @Override
    public void cancelClicked() {
        value="cancel";
        dismiss();
    }

    @Override
    public void acceptClicked() {
        value=dialPad.getInputValue();
        dismiss();
    }

    @Override
    public void onChange(String text) {

    }
}
