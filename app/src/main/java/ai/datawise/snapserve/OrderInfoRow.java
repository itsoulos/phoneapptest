package ai.datawise.snapserve;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderInfoRow
{
    Context context=null;
    LinearLayout secondRow=null;
    TextView numberOfItems=null;
    TextView totalAmount=null;
    LinearLayout.LayoutParams hparams;
    OrderStore order=null;
    LinearLayout mainLayout=null;

    void makeInfoRow()
    {
        secondRow=new LinearLayout(context);
        secondRow.setOrientation(LinearLayout.HORIZONTAL);
        secondRow.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        mainLayout.addView(secondRow);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)secondRow.getLayoutParams();
        sparams.width=hparams.width;
        sparams.height=75 * hparams.height/100;
        secondRow.setLayoutParams(sparams);
        ImageView im1=new ImageView(context);
        im1.setImageBitmap(Global.downArrow);
        secondRow.addView(im1);
        im1.setClickable(true);
        im1.setOnTouchListener(Global.touchListener);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im1.getLayoutParams();
        iparams.leftMargin=5 * hparams.width/100;
        iparams.topMargin=15 * hparams.height/100;
        iparams.width=Global.downArrow.getWidth();
        iparams.height=Global.downArrow.getHeight();
        im1.setLayoutParams(iparams);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        numberOfItems=new TextView(context);
        numberOfItems.setTextColor(Color.WHITE);
        numberOfItems.setBackgroundResource(R.drawable.roundtextview);
        numberOfItems.setTextSize(Global.fontSize);
        numberOfItems.setText("0");
        secondRow.addView(numberOfItems);
        LinearLayout.LayoutParams nparams=(LinearLayout.LayoutParams)numberOfItems.getLayoutParams();
        //nparams.topMargin=iparams.topMargin/2;
        nparams.leftMargin=2*iparams.leftMargin;
        nparams.height=55*hparams.height/100;
        nparams.width=65*hparams.height/100;
        nparams.gravity= Gravity.CENTER_VERTICAL;
        numberOfItems.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        numberOfItems.setLayoutParams(nparams);

        totalAmount=new TextView(context);
        totalAmount.setTextSize(Global.bigFontSize);
        totalAmount.setTypeface(Typeface.DEFAULT_BOLD);
        totalAmount.setTextColor(Color.parseColor(Global.textColor));
        totalAmount.setText(Global.currency+"0.00");
        secondRow.addView(totalAmount);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)totalAmount.getLayoutParams();
        tparams.topMargin=nparams.topMargin/4;
        tparams.rightMargin=iparams.leftMargin;
        tparams.width=20 * hparams.width/100;
        tparams.gravity=Gravity.CENTER;
        tparams.leftMargin=hparams.width-iparams.leftMargin-iparams.width-nparams.leftMargin-nparams.width-tparams.width-tparams.rightMargin;
        totalAmount.setLayoutParams(tparams);
        totalAmount.setGravity(Gravity.RIGHT);

    }

    void update()
    {
        secondRow.setBackgroundColor(Color.parseColor("#ffea9c"));
        numberOfItems.setTypeface(Typeface.DEFAULT_BOLD);
        numberOfItems.setBackgroundResource(R.drawable.yellowbutton);
        numberOfItems.setTextColor(Color.parseColor(Global.textColor));
        numberOfItems.setText(""+order.orderCount());
        double sum=order.allActualCost();
        totalAmount.setText(Global.currency+Global.displayDecimal(sum));
    }

    void clear()
    {
        secondRow.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        numberOfItems.setText(""+order.orderCount());
        totalAmount.setText(Global.currency+Global.displayDecimal(order.allOrdersCost()));

        if(order.orderCount()==0) {
            numberOfItems.setTypeface(Typeface.DEFAULT);
            numberOfItems.setTextColor(Color.WHITE);
            numberOfItems.setBackgroundResource(R.drawable.roundtextview);
        }
    }

    public OrderInfoRow(Context ctx,LinearLayout l,LinearLayout.LayoutParams params,OrderStore o)
    {
        context=ctx;
        mainLayout=l;
        hparams=params;
        order=o;
        makeInfoRow();
    }
}
