package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExtrasWindow extends PopupWindow
{
    Context context=null;
    View view=null;
    int width=0;
    int height=0;
    OrderStore order=null;
    private LinearLayout extrasLayout=null;
    private TextView extrasItemText=null;
    private TextView     extrasCostText=null;
    private TextView     extrasCategoryText=null;
    private ListView extrasList=null;
    private JSONArray localExtra=new JSONArray();
    private JSONObject currentItem=new JSONObject();
    private int          currentItemPos=-1;
    private JSONObject   currentExtraItem=new JSONObject();
    private ArrayList<Button> extButton=new ArrayList<Button>();//the buttons for the extra options
    private int currentExtButtonPressed=-1;
    private int extrasPositionSelected=-1;

    void makeHeader()
    {
        LinearLayout l1=new LinearLayout(context);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        extrasLayout.addView(l1);
        l1.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        extrasItemText=new TextView(context);
        extrasItemText.setTextSize(Global.fontSize);
        extrasItemText.setTextColor(Color.parseColor(Global.textColor));
        extrasItemText.setTypeface(Typeface.DEFAULT_BOLD);
        extrasItemText.setText(" ");
        l1.addView(extrasItemText);
        LinearLayout.LayoutParams eparams=(LinearLayout.LayoutParams)l1.getLayoutParams();
        eparams.width=Global.width;
        eparams.height= 75*Global.headerHeight/100;
        l1.setLayoutParams(eparams);

        LinearLayout.LayoutParams e1params=(LinearLayout.LayoutParams)extrasItemText.getLayoutParams();
        e1params.leftMargin=5 * eparams.width/100;
        e1params.topMargin= 20 * eparams.height/100;
        e1params.width=40 * eparams.width/100;
        e1params.height=eparams.height-e1params.topMargin;
        e1params.gravity= Gravity.CENTER;
        extrasItemText.setLayoutParams(e1params);

        extrasCostText=new TextView(context);
        extrasCostText.setTextSize(Global.bigFontSize);
        extrasCostText.setTypeface(Typeface.DEFAULT_BOLD);
        extrasCostText.setText(Global.currency+"0.00");
        extrasCostText.setTextColor(extrasItemText.getCurrentTextColor());
        l1.addView(extrasCostText);
        LinearLayout.LayoutParams e2params=(LinearLayout.LayoutParams)extrasCostText.getLayoutParams();
        e2params.topMargin=e1params.topMargin;
        e2params.width=25 * eparams.width/100;
        e2params.rightMargin=e1params.leftMargin;
        e2params.leftMargin=40*eparams.width/100;
        e2params.height=e1params.height;
        e2params.gravity= Gravity.CENTER;
        extrasCostText.setLayoutParams(e2params);

        LinearLayout l2=new LinearLayout(context);
        l2.setOrientation(LinearLayout.HORIZONTAL);
        extrasLayout.addView(l2);
        l2.setLayoutParams(eparams);

        extrasCategoryText=new TextView(context);
        extrasCategoryText.setTextSize(Global.bigFontSize);
        extrasCategoryText.setText("  ");
        extrasCategoryText.setTextColor(extrasItemText.getCurrentTextColor());
        l2.addView(extrasCategoryText);

        LinearLayout.LayoutParams etparams=(LinearLayout.LayoutParams)extrasCategoryText.getLayoutParams();
        etparams.leftMargin=e1params.leftMargin;
        etparams.height=e1params.height;
        etparams.width=80 * Global.width/100;
        extrasCategoryText.setLayoutParams(etparams);
    }

    void extrasPressed(int position)
    {
        try {
            JSONObject selectedExtra=localExtra.getJSONObject(position);

            //create extras json
            JSONObject extrasJson=new JSONObject();
            //extrasJson.put("id",selectedExtra.getInt("id"));
            extrasJson.put("orderId",order.getOrderId());
            String idname="id";
            if(currentItem.has("itemId")) idname="itemId";
            extrasJson.put("itemId",selectedExtra.getInt("id"));
            extrasJson.put("itemName",selectedExtra.getString("itemName"));
            extrasJson.put("openedBy",order.getWaiterId());

            extrasJson.put("discountType",0);
            extrasJson.put("quantity",1);
            extrasJson.put("extraOptionsId",
                    currentExtButtonPressed!=-1?
                            Global.productExtras.getJSONObject(currentExtButtonPressed).getInt("id"):0);
            extrasJson.put("priceListId",Global.activeCatalogueId);
            extrasJson.put("vatPercent",order.getVatPercentOfItem(selectedExtra.getInt("id")));
            extrasJson.put("amountNet", order.getPriceOfItem(selectedExtra.getInt("id")));
            extrasJson.put("amountDiscount",0.00);
            extrasJson.put("notes","");
            extrasJson.put("servePriorityId",selectedExtra.getInt("servePriorityId"));
            extrasJson.put("servePriorityName","");//ask team
            extrasJson.put("unitTypeId",(int)1);

            if(order.hasTakeout())
                extrasJson.put("amountVat",
                        Global.getAmountVatTakeAway(extrasJson.getInt("itemId")));
            else
                extrasJson.put("amountVat",
                        Global.getAmountVatTable(extrasJson.getInt("itemId")));

            if(order.hasTakeout())
                extrasJson.put("amountTotal",Global.getProductTakeawayGross(extrasJson.getInt("itemId")));
            else
                extrasJson.put("amountTotal",Global.getProductTableGross(extrasJson.getInt("itemId")));

            System.out.println("GIANNIS EXTRA AMOUNT TOTAL IS  "+extrasJson.getDouble("amountTotal")+" TAKEOUT "+
                    order.hasTakeout());

            Date currentTime = Calendar.getInstance().getTime();
            extrasJson.put("createdAt", new SimpleDateFormat(Global.dateFormat).format(currentTime));
            order.addExtrasToOrder(currentItemPos,extrasJson);
            extrasCostText.setText(Global.currency+""+
                    Global.displayDecimal(order.actualCost(currentItemPos)));

            Snackbar bar=Snackbar.make(extrasList,context.getString(R.string.itemUndoText),Snackbar.LENGTH_LONG);
            bar.setActionTextColor(Color.parseColor("#fed65e"));

            bar.show();
            View vv = bar.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)vv.getLayoutParams();
            params.gravity = Gravity.CENTER_VERTICAL;
            params.height=75 * Global.headerHeight/100;
            params.width=FrameLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(0,height/4,0,0);
            vv.setLayoutParams(params);

            bar.setAction(context.getString(R.string.undoText), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentItemPos!=-1)
                        order.removeLastExtras(currentItemPos);
                }
            });
            extrasPositionSelected=-1;
            updateAdapter();

        } catch (JSONException e) {
            System.out.println("GIANNIS JSON ERROR "+e.getMessage());
        }
    }

    void makeList()
    {
        extrasList=new ListView(context);
        extrasLayout.addView(extrasList);
        LinearLayout.LayoutParams eparams=(LinearLayout.LayoutParams)extrasList.getLayoutParams();
        eparams.width=width;
        eparams.height=height-3*Global.headerHeight/2-Global.headerHeight;
        extrasList.setLayoutParams(eparams);
        extrasList.setSelector(new ColorDrawable(0xffffea9c));
        extrasList.setSelection(ListView.CHOICE_MODE_SINGLE);
        extrasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                extrasPositionSelected=position;
                extrasPressed(position);

            }
        });
    }

    void updateAdapter()
    {
        //display local extra
        ArrayList<String> dummy=new ArrayList<String>();
        for(int i=0;i<localExtra.length();i++)
        {
            dummy.add(" "+i);
            try {
                JSONObject x=localExtra.getJSONObject(i);
                x.put("itemPrice",Global.getProductTablePrice(x.getInt("id")));
                localExtra.put(i,x);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LinearLayout.LayoutParams hparams=new LinearLayout.LayoutParams(Global.width,Global.headerHeight);
        ExtrasAdapter extrasAdapter=new ExtrasAdapter(context,dummy,localExtra,hparams,order);
        extrasList.setAdapter(extrasAdapter);

    }
    void  setInformation(OrderStore o, int pos,JSONObject item)
    {
        order=o;
        currentItemPos=pos;
        currentItem=item;
        //compute local extra and update list
        int extraCategoryId= 0;
        try {

            extraCategoryId = currentItem.getInt("extraCategoryId");
            System.out.println("GIANNIS EXTRA CATEGORY IS "+extraCategoryId);
            for(int i=0;i<Global.extraItems.length();i++)
            {
                System.out.println("GIANNIS EXTRA ITEM IS "+Global.extraItems.getJSONObject(i));
                if(Global.extraItems.getJSONObject(i).getInt("id")==extraCategoryId)
                {
                    localExtra=Global.getProductItemsOfCategory(extraCategoryId);
                    currentExtraItem=Global.extraItems.getJSONObject(i);
                    break;
                }
            }
        } catch (JSONException e) {
            System.out.println("GIANNIS JSON E"+e.getMessage());
            System.out.println("GIANNIS ITEM IS "+currentItem);
            e.printStackTrace();
        }
        currentExtButtonPressed=-1;
        for(int i=0;i<extButton.size();i++)
        {
            extButton.get(i).setTextColor(Color.parseColor(Global.textColor));
            extButton.get(i).setBackgroundColor(Color.parseColor("#e0e0e0"));
        }
        updateAdapter();
        try {
            String idname="id";
            extrasItemText.setText(currentItem.getString("itemName"));
            if(currentItem.has("itemId")) idname="itemId";
            if(order.hasTakeout())
                extrasCostText.setText(Global.currency+Global.displayDecimal(Global.getTakeAwayPrice(currentItem.getInt(idname))));
            else
                extrasCostText.setText(Global.currency+Global.displayDecimal(Global.getProductTablePrice(currentItem.getInt(idname))));
            extrasCategoryText.setText(currentExtraItem.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void makeButtons()
    {
        final HorizontalScrollView extrasScroll=new HorizontalScrollView(context);
        extrasLayout.addView(extrasScroll);

        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        extrasScroll.addView(l);

        HorizontalScrollView.LayoutParams lparams=(HorizontalScrollView.LayoutParams)l.getLayoutParams();
        lparams.width=width;
        lparams.height=Global.headerHeight;
        l.setLayoutParams(lparams);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        for(int i=0;i<Global.productExtras.length();i++)
        {
            try {
                JSONObject x=Global.productExtras.getJSONObject(i);
                Button bt=new Button(context);
                l.addView(bt);
                bt.setTextSize(Global.smallFontSize);
                bt.setTextColor(Color.parseColor(Global.textColor));
                bt.setBackgroundColor(Color.parseColor("#e0e0e0"));
                bt.setText(x.getString("name"));
                extButton.add(bt);
                LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)bt.getLayoutParams();
                bparams.leftMargin=2 * width/100;
                bparams.topMargin=5 * Global.headerHeight/100;
                bparams.width= 15 * width/100;
                int d=8 *Global.getMeasureWidth(context,bt.getText().toString(),Global.smallFontSize)/7;
                if(bparams.width<d) bparams.width=d;
                bt.setLayoutParams(bparams);

                bt.setTag(new Integer(i));
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tag=(Integer)v.getTag();
                        boolean sameTag=false;
                        if(tag==currentExtButtonPressed) sameTag=true;
                        if(sameTag) currentExtButtonPressed=-1;
                        for(int j=0;j<extButton.size();j++)
                        {
                            if(j==tag &&!sameTag)
                            {

                                currentExtButtonPressed=j;
                                extButton.get(j).setTextColor(Color.WHITE);
                                extButton.get(j).setBackgroundColor(Color.parseColor(Global.textColor));
                            }
                            else
                            {
                                extButton.get(j).setTextColor(Color.parseColor(Global.textColor));
                                extButton.get(j).setBackgroundColor(Color.parseColor("#e0e0e0"));
                            }
                        }
                        if(extrasPositionSelected!=-1)
                            extrasPressed(extrasPositionSelected);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ExtrasWindow(Context ctx,View v, int w, int h,OrderStore o) {
        super(v, w, h);
        context = ctx;
        view = v;
        width = w;
        height = h;
        order = o;
        extrasLayout = new LinearLayout(context);
        extrasLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(extrasLayout);
        extrasLayout.setBackgroundColor(Color.WHITE);
        makeHeader();
        makeList();
        makeButtons();
    }
}
