package ai.datawise.snapserve;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;

public class InnerCategoriesDialog extends Dialog {
    private JSONArray categories=new JSONArray();
    private Context context=null;
    private int dialogWidth=0,dialogHeight=0;
    private LinearLayout mainLayout=null;
    private int selectedCategoryId=-1;

    public InnerCategoriesDialog(Context ctx,JSONArray cat) {
        super(ctx);
        context=ctx;
        dialogWidth=Global.width;
        dialogHeight=Global.height/3;
        categories=cat;
    }

    public int getSelectedId()
    {
        return selectedCategoryId;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setLayout(dialogWidth,dialogHeight);
        setCancelable(true);
        mainLayout=new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundResource(R.drawable.roundcorner);
        setContentView(mainLayout);
        ScrollView scrollView=new ScrollView(context);
        mainLayout.addView(scrollView);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)scrollView.getLayoutParams();
        sparams.width=dialogWidth;
        sparams.height=dialogHeight;
        TableLayout slayout=new TableLayout(context);
        scrollView.addView(slayout);
        int nrows=categories.length()/2;
        if(nrows*2<categories.length()) nrows++;
        int currentItem=0;
        for(int i=0;i<nrows;i++)
        {
            TableRow r= new TableRow(context);
            slayout.addView(r);
            for(int k=0;k<2;k++) {
                Button bt = new Button(context);
                try {
                    bt.setText(categories.getJSONObject(currentItem).getString("name"));
                    bt.setBackgroundResource(R.drawable.transparentbutton);
                    bt.setTextColor(Color.parseColor(Global.textColor));
                    bt.setTextSize(Global.fontSize);
                    r.addView(bt);
                    TableRow.LayoutParams bparams=(TableRow.LayoutParams)bt.getLayoutParams();
                    bparams.leftMargin=4 * dialogWidth/100;
                    bparams.topMargin=8 * dialogHeight/100;
                    bparams.width=40 * dialogWidth/100;
                    bparams.height=25 * dialogHeight/100;
                    bt.setLayoutParams(bparams);
                    bt.setTag(new Integer(categories.getJSONObject(currentItem).getInt("id")));
                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedCategoryId=(Integer)v.getTag();
                            dismiss();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ++currentItem;
                if(currentItem>=categories.length()) break;
            }
            if(currentItem>=categories.length()) break;
        }
    }
}
