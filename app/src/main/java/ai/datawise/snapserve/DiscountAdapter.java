package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class DiscountAdapter extends PagerAdapter implements  DialPadListener
{
    Context context=null;
    private int pageWidth=0,pageHeight=0;
    private ArrayList<BasketItem> basketItems=new ArrayList<BasketItem>();
    OrderStore order=null;
    //selectedPercent discount with %discount tab
    //selectedDiscount discount with money discount tab
    double selectedPercent=-1.0;
    double selectedDiscount=-1.0;

    DialPad dialPad=null;
    boolean selectedWithDialPad=false;

    public DiscountAdapter(Context ctx,ArrayList<BasketItem> b,OrderStore o,int pw,int ph)
    {
        context=ctx;
        basketItems=b;
        order=o;
        pageWidth=pw;
        pageHeight=ph;
    }
    @Override
    public int getCount() {
        return 2;
    }


    public double getSelectedPercent()
    {
        return selectedPercent;
    }

    public double getSelectedDiscount()
    {
        if(selectedWithDialPad==true)
            return
                    selectedDiscount;
        else
            return -1.0;
    }

    LinearLayout makePercenLayout()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        ListView listView=new ListView(context);
        l.addView(listView);
        final ArrayList<String> options=new ArrayList<String>();
        options.add(context.getString(R.string.noDiscountText));
        double partialCost=order.allOrdersCost(basketItems);

        for(int i=10;i<=90;i+=10)
        {
            double percentValue=i/100.0;
            options.add(i+"%");
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_checked,options);
        listView.setAdapter(adapter);
        LinearLayout.LayoutParams llparams=(LinearLayout.LayoutParams)listView.getLayoutParams();
        llparams.leftMargin=2 * pageWidth/100;
        llparams.width=95 * pageWidth/100;
        listView.setLayoutParams(llparams);
        listView.setSelector(new ColorDrawable(0xffffea9c));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String percentString=(position==0)?"0":options.get(position).substring(0,options.get(position).indexOf("%"));
                System.out.println("GIANNIS P "+percentString);
                double percentValue=Double.parseDouble(percentString)/100.0;
                System.out.println("GIANNIS PERCENTVALUE IS "+percentValue);
                double partialCost=order.allOrdersCost(basketItems);
                dialPad.setValue(Global.displayDecimal(partialCost-percentValue*partialCost));

                selectedDiscount=percentValue;
                selectedPercent=percentValue;
                if(position==0)
                    selectedWithDialPad=true;
                else
                    selectedWithDialPad=false;
            }
        });
        return l;
    }


    LinearLayout makeDialpadLayout()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        dialPad=new DialPad(context,"",context.getString(R.string.cancel_text),
                context.getString(R.string.acceptText),105*pageWidth/100,95 * pageHeight/100);
        dialPad.enableDot();
        dialPad.setListener(this);
        l.addView(dialPad);
        return l;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {

        LinearLayout l = null;
        switch (position) {
            case 0:
                l=makePercenLayout();
                break;
            case 1:
                l=makeDialpadLayout();
                break;
            default:
                break;
        }
        collection.addView(l);
        return l;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void cancelClicked() {

    }

    @Override
    public void acceptClicked() {

    }

    @Override
    public void onChange(String text) {
        selectedWithDialPad=true;
        if(text.length()!=0)
            selectedDiscount=Double.parseDouble(text);
        else selectedDiscount=0.0;
        double partialCost=order.allOrdersCost(basketItems);
        if(selectedDiscount>partialCost)
        {
         //   selectedDiscount=partialCost;
         //   dialPad.setValue(Global.displayDecimal(partialCost));
        }
    }
}
