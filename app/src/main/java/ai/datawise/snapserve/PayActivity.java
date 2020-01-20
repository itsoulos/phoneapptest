package ai.datawise.snapserve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PayActivity extends AppCompatActivity{
    LinearLayout mainLayout=null;
    ListView payList=null;
    ArrayList<BasketItem> items=new ArrayList<BasketItem>();
    BasketAdapter adapter=null;
    OrderStore order=null;
    TextView amountText=null;
    JSONArray selectedItems=new JSONArray();
    PopupWindow dialWindow=null;
    DialPad dialPad=null;
    ArrayList<Button> payButton=new ArrayList<Button>();
    int payMethod=-1;
    LinearLayout listLayout;
    LinearLayout.LayoutParams hparams,lparams;
    ViewPager pager=null;
    ArrayList<Integer> havePay=new ArrayList<Integer>();
    TextView t2;
    JSONObject payJson;
    TextView amountNetText=null;
    TextView amountTaxText=null;
    TextView amountActualText=null;
    double totalPaidAmount=0.0;
    boolean selectAllflag=false;
    /** Ta xrimata pou exoun plirothei ana item **/
    private ArrayList < ArrayList<BasketItem> > itemPerSplit= new ArrayList< ArrayList<BasketItem> >();
    private ArrayList<Double> payPerSplit=new ArrayList<Double>();
    private int currentSplit=0;//to split gia to opoio plironoume


    class PaySpinnerAdapter extends ArrayAdapter<String>
    {
        private Context context=null;
        private ArrayList<String> data=new ArrayList<String>();

        public PaySpinnerAdapter(@NonNull Context ctx, ArrayList<String> d) {
            super(ctx,0);
            context=ctx;
            data=d;
        }

        public int getCount()
        {
            return data.size();
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            LinearLayout l=new LinearLayout(context);
            l.setOrientation(LinearLayout.HORIZONTAL);
            TextView t=new TextView(context);
            t.setText(data.get(position));
            t.setTextColor(Color.parseColor(Global.textColor));
            t.setTextSize(Global.fontSize);
            l.addView(t);
            LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
            tparams.leftMargin=2 * Global.width/100;
            tparams.width=30 * Global.width/100;
            tparams.height=8 * Global.height/100;
            tparams.gravity= Gravity.CENTER;
            t.setLayoutParams(tparams);
            t.setGravity(Gravity.CENTER);


            ImageView im=new ImageView(context);
            im.setImageBitmap(Global.rightArrow);
            l.addView(im);
            LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
            iparams.rightMargin=tparams.leftMargin;
            iparams.width=Global.rightArrow.getWidth();
            iparams.height=tparams.height;
            iparams.gravity=Gravity.CENTER;
            iparams.leftMargin=Global.width-tparams.leftMargin-tparams.width-iparams.rightMargin-iparams.width;
            im.setLayoutParams(iparams);
            return l;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

    void updateItemsAdapter()
    {
        items=new ArrayList<BasketItem>();
        ArrayList<BasketItem> copyItems=new ArrayList<BasketItem>();
        copyItems=order.getBasketItemsNoExtras();
        for(int i=0;i<copyItems.size();i++)
        {
            if(order.getOrderSplit()>0 && currentSplit!=0 &&
                    currentSplit!=order.getItemSplit(copyItems.get(i).getCurrentItemPos())) continue;
            items.add(copyItems.get(i));
        }

    }

    void updateHavePayed()
    {
        for(int i=0;i<items.size();i++) {
            int itemPos=items.get(i).currentItemPos;
            JSONObject json=order.itemAtPos(itemPos);
            try {
                int id=json.getInt("id");
                boolean found=false;
                for(int j=0;j<havePay.size();j++)
                {
                    if(havePay.get(j)==id)
                    {
                        found=true;
                        break;
                    }
                }
                if(found)
                    adapter.disableItem(i);
                if(order.costOfItem(itemPos)<1e-3)
                    adapter.disableItem(i);

            } catch (JSONException e) {
                System.out.println("GIANNIS ERROR IN ADAPTER "+e.getMessage());
                e.printStackTrace();
            }
        }


    }



    class PayAdapter extends PagerAdapter implements BasketInterface {

        private Context context=null;
        PayAdapter(Context ctx)
        {
            context=ctx;
        }

        LinearLayout makePayLayout(int item)
        {
            LinearLayout l=new LinearLayout(context);
            l.setOrientation(LinearLayout.VERTICAL);
            if(item==0)
            {
                LinearLayout l2=new LinearLayout(context);
                l2.setOrientation(LinearLayout.VERTICAL);
                l2.setBackgroundColor(Color.WHITE);
                l.addView(l2);
                LinearLayout.LayoutParams l2params=(LinearLayout.LayoutParams)l2.getLayoutParams();
                l2params.width=lparams.width;
                l2params.height=20*lparams.height/100;
                l2.setLayoutParams(l2params);

                if(order.getOrderSplit()>0)
                {
                    Spinner spinner=new Spinner(context);
                    l2.addView(spinner);
                    final ArrayList<String> tt =new ArrayList<String>();
                    for(int i=1;i<=order.getOrderSplit();i++)
                    {
                        if(itemPerSplit.get(i-1).size()>0)  tt.add(context.getString(R.string.billButtonText)+":"+i);
                    }
                    spinner.setAdapter(new PaySpinnerAdapter(context,tt));
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            if(i<=order.getOrderSplit()-1)
                            {
                                currentSplit=Integer.parseInt(tt.get(i).substring(tt.get(i).indexOf(":")+1));//   i+1;

                                totalPaidAmount=payPerSplit.get(currentSplit-1);
                                if(Math.abs(totalPaidAmount)<1e-5)
                                    selectAllflag = true;
                                else
                                    selectAllflag=false;
                            }
                            else currentSplit=0;

                            updateItemsAdapter();
                            adapter=new BasketAdapter(PayActivity.this,items,PayAdapter.this);
                            adapter.disableHeader();
                            payList.setAdapter(adapter);

                            if(selectAllflag)
                                adapter.selectAll();
                            onClickFunction();
                            updateHavePayed();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                }
                amountNetText=new TextView(context);
                amountNetText.setTextColor(Color.parseColor(Global.textColor));
                amountNetText.setTextSize(Global.bigFontSize);
                amountNetText.setText(
                        Html.fromHtml("<b>"+getString(R.string.subTotalText)+"</b>"+
                                Global.currency+Global.displayDecimal(0)));
                amountNetText.setGravity(Gravity.RIGHT);
                l2.addView(amountNetText);
                LinearLayout.LayoutParams ttparams=(LinearLayout.LayoutParams)amountNetText.getLayoutParams();
                ttparams.leftMargin=1* l2params.width/100;
                ttparams.width=90 * l2params.width/100;
                ttparams.height=l2params.height/2;
                ttparams.gravity=Gravity.CENTER;
                ttparams.bottomMargin=2 * l2params.height/100;
                amountNetText.setLayoutParams(ttparams);

                amountTaxText=new TextView(context);
                amountTaxText.setTextColor(Color.parseColor(Global.textColor));
                amountTaxText.setTextSize(Global.fontSize);
                amountTaxText.setText(
                        Html.fromHtml("<b>"+
                                getString(R.string.taxText)+"</b>"+
                                Global.currency+Global.displayDecimal(0)));
                amountTaxText.setGravity(Gravity.RIGHT);
                l2.addView(amountTaxText);
                amountTaxText.setLayoutParams(ttparams);

                amountActualText=new TextView(context);
                amountActualText.setTextColor(Color.parseColor(Global.textColor));
                amountActualText.setTextSize(Global.smallFontSize);
                amountActualText.setText(
                        Html.fromHtml("<b>"+
                                getString(R.string.totalText)+"</b>"+
                                Global.currency+Global.displayDecimal(0)));
                amountActualText.setGravity(Gravity.RIGHT);
                l2.addView(amountActualText);
                amountActualText.setLayoutParams(ttparams);


                payList=new ListView(context);
                l.addView(payList);
                LinearLayout.LayoutParams llparams=(LinearLayout.LayoutParams)payList.getLayoutParams();
                llparams.width=Global.width;
                llparams.height=68 * lparams.height/100;
                payList.setLayoutParams(llparams);

                updateItemsAdapter();
                adapter=new BasketAdapter(context,items,this);
                adapter.disableHeader();
                payList.setAdapter(adapter);

                updateHavePayed();

                LinearLayout l1=new LinearLayout(context);
                l1.setOrientation(LinearLayout.HORIZONTAL);
                l1.setBackgroundColor(Color.parseColor(Global.backgroundColor));
                l.addView(l1);
                LinearLayout.LayoutParams l1params=(LinearLayout.LayoutParams)l1.getLayoutParams();
                l1params.width=lparams.width;
                l1params.height=10 * lparams.height/100;
                l1.setLayoutParams(l1params);
                ImageView im1=new ImageView(context);
                im1.setImageBitmap(Global.upArrow);
                im1.setClickable(true);
                im1.setOnTouchListener(Global.touchListener);
                l1.addView(im1);
                im1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pager.setCurrentItem(1);

                    }
                });
                LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im1.getLayoutParams();
                iparams.leftMargin=5 * l1params.width/100;
                iparams.topMargin= 5 * l1params.height/100;
                iparams.width=Global.upArrow.getWidth();
                iparams.height=l1params.height-2*iparams.topMargin;
                im1.setLayoutParams(iparams);

                /* */

                TextView t1=new TextView(context);
                t1.setTextSize(Global.bigFontSize+4);
                t1.setTextColor(Color.parseColor(Global.textColor));
                t1.setTypeface(Typeface.DEFAULT_BOLD);
                t1.setText(getString(R.string.totalText));
                l1.addView(t1);
                LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
                t1params.topMargin=3*iparams.topMargin;
                t1params.width=2 * Global.getMeasureWidth(context,t1.getText().toString(),Global.bigFontSize+4);

                t1params.leftMargin=3 * iparams.leftMargin;
                t1.setLayoutParams(t1params);

                t2=new TextView(context);
                t2.setTextSize(Global.bigFontSize+4);
                t2.setTextColor(t1.getCurrentTextColor());
                t2.setTypeface(Typeface.DEFAULT_BOLD);
                l1.addView(t2);

                try {
                    t2.setText(Global.currency+
                            Global.displayDecimal(order.allActualCost()-
                                    payJson.getDouble("totalPaidAmount")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)t2.getLayoutParams();
                t2params.topMargin=t1params.topMargin;
                t2params.rightMargin=iparams.leftMargin;
                t2params.width=2*Global.getMeasureWidth(context,t2.getText().toString(),Global.bigFontSize+4);
                t2params.leftMargin=l1params.width-iparams.leftMargin-iparams.width-t1params.leftMargin-t1params.width-t2params.width;
                t2.setLayoutParams(t2params);
                if(selectAllflag) {
                    adapter.selectAll();
                    onClickFunction();
                }
            }
            else
            {
                LinearLayout l1=new LinearLayout(context);
                l1.setOrientation(LinearLayout.HORIZONTAL);
                l1.setBackgroundColor(Color.parseColor(Global.backgroundColor));
                l.addView(l1);
                LinearLayout.LayoutParams l1params=(LinearLayout.LayoutParams)l1.getLayoutParams();
                l1params.width=lparams.width;
                l1params.height=10 * lparams.height/100;
                l1.setLayoutParams(l1params);

                ImageView im1=new ImageView(context);
                im1.setImageBitmap(Global.downArrow);
                im1.setClickable(true);
                im1.setOnTouchListener(Global.touchListener);
                l1.addView(im1);
                im1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //     amountText.setText(dialPad.getInputValue());
                        pager.setCurrentItem(0);
                    }
                });
                LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im1.getLayoutParams();
                iparams.leftMargin=5 * l1params.width/100;
                iparams.topMargin= 5 * l1params.height/100;
                iparams.width=Global.downArrow.getWidth();
                iparams.height=l1params.height-2*iparams.topMargin;
                im1.setLayoutParams(iparams);

                TextView t1=new TextView(context);
                t1.setTextSize(Global.bigFontSize+4);
                t1.setTextColor(Color.parseColor(Global.textColor));
                t1.setTypeface(Typeface.DEFAULT_BOLD);
                t1.setText(getString(R.string.totalText));
                l1.addView(t1);
                LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
                t1params.topMargin=3*iparams.topMargin;
                t1params.width=2 * Global.getMeasureWidth(context,t1.getText().toString(),Global.bigFontSize+4);

                t1params.leftMargin=3 * iparams.leftMargin;
                t1.setLayoutParams(t1params);

                TextView t2=new TextView(context);
                t2.setTextSize(Global.bigFontSize+4);
                t2.setTextColor(t1.getCurrentTextColor());
                t2.setTypeface(Typeface.DEFAULT_BOLD);
                t2.setText(Global.currency+Global.displayDecimal(order.allActualCost()));
                l1.addView(t2);

                LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)t2.getLayoutParams();
                t2params.topMargin=t1params.topMargin;
                t2params.rightMargin=iparams.leftMargin;
                t2params.width=2*Global.getMeasureWidth(context,t2.getText().toString(),Global.bigFontSize+4);
                t2params.leftMargin=l1params.width-iparams.leftMargin-iparams.width-t1params.leftMargin-t1params.width-t2params.width;
                t2.setLayoutParams(t2params);


                LinearLayout l2=new LinearLayout(context);
                l2.setOrientation(LinearLayout.VERTICAL);
                l2.setBackgroundColor(Color.WHITE);
                l.addView(l2);
                LinearLayout.LayoutParams l2params=(LinearLayout.LayoutParams)l2.getLayoutParams();

                l2params.width=lparams.width;
                l2params.height=lparams.height-l1params.height;
                l2.setLayoutParams(l2params);

                dialPad=new DialPad(context,"","","",110*l2params.width/100,90*l2params.height/100);
                dialPad.setBackgroundColor(Color.WHITE);
                dialPad.enableDot();
                l2.addView(dialPad);

                LinearLayout.LayoutParams dparams=(LinearLayout.LayoutParams)dialPad.getLayoutParams();
                dparams.topMargin=10 * l2params.height/100;
                dialPad.setLayoutParams(dparams);

            }
            return l;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {

            LinearLayout l = makePayLayout(position);
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
        public void onClickFunction() {

            ArrayList<Integer> selected=adapter.getIsSelected();
            selectedItems=new JSONArray();
            double total=0.0;
            double totalTax=0.0;
            double totalActualAmount=0.0;
            double totalOrderCost=0.0;
            for(int i=0;i<selected.size();i++)
            {
                if(selected.get(i)==1)
                {
                    int pos=items.get(i).getCurrentItemPos();

                    JSONObject x=order.itemAtPos(pos);
                    if(currentSplit!=0 && order.costOfItem(pos)<1e-3) continue;
                    try {
                        selectedItems.put(x.getInt("id"));
                        ArrayList<Integer> extras=order.getExtraIds(pos);
                        for(int j=0;j<extras.size();j++)
                            selectedItems.put(extras.get(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    total+=order.costOfItem(pos);
                    totalTax+=order.taxOfItem(pos);
                    totalActualAmount+=order.actualCost(pos);
                }
                totalOrderCost+=order.actualCost(items.get(i).currentItemPos);
            }
            if(dialPad!=null) {
                dialPad.setValue(Global.displayDecimal(totalOrderCost - totalPaidAmount));
                selectAllflag = false;
            }
            amountTaxText.setText(
                    Html.fromHtml("<b>"+
                            getString(R.string.taxText)+"</b>&nbsp;"+Global.currency+Global.displayDecimal(totalTax)));
            amountNetText.setText(
                    Html.fromHtml("<b>"+getString(R.string.subTotalText)+"</b>&nbsp;"+
                            Global.currency+Global.displayDecimal(total)));
            amountActualText.setText(
                    Html.fromHtml("<b>"+
                            getString(R.string.totalText)+"</b>&nbsp;"+
                            Global.currency+Global.displayDecimal(totalActualAmount)));

            t2.setText(Global.displayDecimal(totalActualAmount));
        }
    }

    JSONObject getPayObject()
    {
        JSONObject x=new JSONObject();
        try {
            x.put("paymentMethod",payMethod);
            boolean changeFlag=false;
            double df=Double.parseDouble(dialPad.getInputValue().isEmpty()?"0.0":dialPad.getInputValue());
            if(df>(order.allActualCost()-totalPaidAmount))
            {
                changeFlag=true;
            }
            if(selectAllflag) {
                if(order.allActualCost()==0 && totalPaidAmount==0)
                    ;
                else
                    dialPad.setValue(Global.displayDecimal(order.allActualCost() - totalPaidAmount));
            }
            if(dialPad.getInputValue().isEmpty())
                x.put("amount",0.0);
            else
                x.put("amount",Double.parseDouble(dialPad.getInputValue()));
            if(changeFlag)
                x.put("amount",df);
            x.put("split",currentSplit);
            x.put("orderDetailsIds",selectedItems);
            x.put("loyaltyPoints",null);
            x.put("tip",null);
            System.out.println("GIANNIS JSON FOR PAY "+x);
        } catch (JSONException e) {
            System.out.println("GIANNIS ERROR SPLIT "+e.getMessage());
            e.printStackTrace();
        }

        return x;
    }

    void makeHeader()
    {
        LinearLayout header=new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(header);
        hparams=(LinearLayout.LayoutParams)header.getLayoutParams();
        hparams.width=Global.width;
        hparams.height=Global.headerHeight;
        header.setLayoutParams(hparams);
        ImageView im=new ImageView(this);
        im.setImageBitmap(Global.grayBack);
        im.setClickable(true);
        im.setOnTouchListener(Global.touchListener);
        header.addView(im);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        iparams.leftMargin= 5 * Global.width/100;
        iparams.topMargin= 20 * hparams.height/100;
        iparams.width=Global.grayBack.getWidth();
        iparams.height=Global.grayBack.getHeight();
        im.setLayoutParams(iparams);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItems=new JSONArray();
                payMethod=-1;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("json",getPayObject().toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        TextView t1=new TextView(this);
        t1.setTextSize(Global.bigFontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setText(getString(R.string.paymentText));
        header.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.topMargin=iparams.topMargin;
        t1params.rightMargin=iparams.leftMargin;
        t1params.width=hparams.width-iparams.leftMargin-iparams.width-t1params.rightMargin;
        t1params.height=iparams.height;
        t1.setLayoutParams(t1params);
        t1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
    }

    void makeList()
    {
        listLayout=new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(listLayout);
        lparams=(LinearLayout.LayoutParams)listLayout.getLayoutParams();
        lparams.width=Global.width;
        lparams.height=Global.height-2*Global.headerHeight;
        listLayout.setLayoutParams(lparams);

        View line=new View(this);
        line.setBackgroundResource(R.drawable.backwithborderline);
        listLayout.addView(line);
        final LinearLayout.LayoutParams lineparams=(LinearLayout.LayoutParams)line.getLayoutParams();
        lineparams.width=lparams.width;
        lineparams.height=2*lparams.height/100;
        line.setLayoutParams(lineparams);

        pager=new ViewPager(this);
        listLayout.addView(pager);
        LinearLayout.LayoutParams pparams=(LinearLayout.LayoutParams)pager.getLayoutParams();
        pparams.width=lparams.width;
        pparams.height=98 * lparams.height;
        pager.setLayoutParams(pparams);
        pager.setAdapter(new PayAdapter(this));
        pager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

    }

    Button makeButton(String title)
    {
        Button bt=new Button(this);
        bt.setTextSize(Global.smallFontSize-4);
        bt.setTextColor(Color.parseColor(Global.textColor));
        bt.setText(title);
        bt.setBackgroundResource(R.drawable.roundyellowbutton);
        return bt;
    }
    void makeButtons()
    {
        HorizontalScrollView scrollView=new HorizontalScrollView(this);
        mainLayout.addView(scrollView);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)scrollView.getLayoutParams();
        sparams.width=Global.width;
        sparams.height=Global.height-hparams.height-lparams.height-3 * Global.height/100;
        scrollView.setLayoutParams(sparams);

        LinearLayout buttonLayout=new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        scrollView.addView(buttonLayout);
        HorizontalScrollView.LayoutParams bparams=(HorizontalScrollView.LayoutParams)buttonLayout.getLayoutParams();
        bparams.width=Global.width;
        bparams.height=sparams.height;
        buttonLayout.setLayoutParams(bparams);

        TextView v=new TextView(this);
        v.setText("  ");
        v.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        mainLayout.addView(v);
        LinearLayout.LayoutParams vparams=(LinearLayout.LayoutParams)v.getLayoutParams();
        vparams.width=sparams.width;
        vparams.height=3*Global.height/100;
        v.setLayoutParams(vparams);



        for(int i=0;i<Global.paymentMethods.length();i++)
        {
            try {
                JSONObject x=Global.paymentMethods.getJSONObject(i);
                Button bt=makeButton(x.getString("name"));
                buttonLayout.addView(bt);


                LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)bt.getLayoutParams();
                cparams.leftMargin=2 * bparams.width/100;
                //cparams.topMargin= 5 * bparams.height/100;
                cparams.gravity=Gravity.CENTER;
                cparams.height=90 * bparams.height/100;
                cparams.width= 18 * bparams.width/100;

                bt.setLayoutParams(cparams);
                bt.setTag(new Integer(x.getInt("id")));
                payButton.add(bt);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payMethod=(Integer)v.getTag();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("json",getPayObject().toString());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    int orderId=0;

    void getCurrentPayment()
    {

        InternetClass.ObjectGETCall(this, Global.server + "/orders/" + orderId + "/payments",
                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            totalPaidAmount=response.getDouble("totalPaidAmount");
                            JSONArray orderPayements=response.getJSONArray("orderPayments");

                            for(int i=1;i<=order.getOrderSplit();i++) {
                                itemPerSplit.add(order.getBasketItemsOfSplit(i));
                                payPerSplit.add(0.0);
                            }
                            for(int i=0;i<orderPayements.length();i++)
                            {
                                JSONObject xx=orderPayements.getJSONObject(i);
                                JSONArray paymentDetails=xx.getJSONArray("paymentDetails");
                                for(int k=0;k<paymentDetails.length();k++)
                                {
                                    int pid=paymentDetails.getInt(k);
                                    for(int j=0;j<payPerSplit.size();j++)
                                    {
                                        ArrayList<BasketItem> bt=itemPerSplit.get(j);
                                        for(int m=0;m<bt.size();m++)
                                        {
                                            int mid=order.itemAtPos( bt.get(m).getCurrentItemPos()).getInt("id");
                                            if(mid==pid)
                                            {
                                                double df=order.actualCost(bt.get(m).getCurrentItemPos());
                                                double currentf=payPerSplit.get(j);
                                                payPerSplit.set(j,currentf+df);
                                            }
                                        }
                                    }
                                }
                            }
                            System.out.println("GIANNIS PAY PER SPLIT IS "+payPerSplit);
                            if(Math.abs(totalPaidAmount)<1e-5)
                                selectAllflag = true;
                            else
                                selectAllflag=false;
                            makeHeader();
                            makeList();
                            makeButtons();

                        } catch (JSONException e) {
                            System.out.println("GIANNIS JSON "+e.getMessage());
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert("Error in get payment ",PayActivity.this);
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        setContentView(mainLayout);
        Bundle extras = getIntent().getExtras();
        String x=extras.getString("order");
        String xx=extras.getString("pay");
        orderId=extras.getInt("orderId");
        try {
            JSONObject json=new JSONObject(x);
            order=new OrderStore();
            order.setOrderInfo(json);
            payJson=new JSONObject(xx);
            JSONArray orderPayments=payJson.getJSONArray("orderPayments");
            for(int i=0;i<orderPayments.length();i++)
            {
                JSONObject obj=orderPayments.getJSONObject(i);
                JSONArray details=obj.getJSONArray("paymentDetails");
                for(int j=0;j<details.length();j++)
                {
                    int value=details.getInt(j);
                    havePay.add(value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getCurrentPayment();
    }
    public void onBackPressed()
    {
        //do nothing
    }

}