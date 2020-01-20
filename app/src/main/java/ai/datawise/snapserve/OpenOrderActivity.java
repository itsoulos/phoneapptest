package ai.datawise.snapserve;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OpenOrderActivity extends AppCompatActivity
{
    public static LinearLayout mainLayout=null;
    LinearLayout header=null;
    LinearLayout.LayoutParams hparams;
    JSONObject tableJson=null;
    int orderId=0;
    ArrayList<Button> catButton=new ArrayList<Button>();
    int selectedCategoryPos;
    TextView selectedCategoryText=null;
    ListView productList=null;
    ArrayList<BasketItem> basketItems=new ArrayList<BasketItem>();
    ImageView threeDotsButton=null;
    private boolean havePartialPaid=false;

    FullOrderWindow fullOrderWindow=null;
    Button viewButton=null,splitButton=null,extrasButton=null;
    private JSONArray idItems=new JSONArray();//the items displayed in the list view
    private JSONObject currentItem=new JSONObject();//current item highlighted in the list
    private int        currentItemPos=-1;

    private OrderStore orderStore=null;//the temporary array with the order
    private ItemAdapter productAdapter=null;
    private ExtrasWindow extWindow=null;
    private int selectedInnerCategoryId=-1;
    private boolean isTakeOutFlag=false;

    void clearProductList()
    {
        ArrayList<String> dummy=new ArrayList<String>();
        for(int i=0;i<idItems.length();i++) {
            dummy.add(new String(""+i));
        }
        productAdapter=new ItemAdapter(OpenOrderActivity.this,idItems,dummy,hparams,true,isTakeOutFlag);
        productList.setAdapter(productAdapter);
        currentItemPos=-1;
    }
    void showExtras()
    {
        int extraCategoryId= 0;
        try {
            extraCategoryId = currentItem.getInt("extraCategoryId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(extraCategoryId==0) return;
        if(extWindow==null)  extWindow=new ExtrasWindow(this,mainLayout,Global.width,Global.height-Global.headerHeight,orderStore);
        extWindow.setInformation(orderStore,currentItemPos,currentItem);
        extWindow.showAtLocation(mainLayout, Gravity.CENTER,0,Global.headerHeight);
        extWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                clearProductList();
            }
        });
        threeDotsButton.setImageBitmap(Global.xicon);
    }


    //display the basket list
    void updateBasket()
    {
        Intent I=new Intent(OpenOrderActivity.this,BasketActivity.class);
        String object2string=orderStore.toString();
        I.putExtra("order",object2string);
        I.putExtra("tableJson",tableJson.toString());
        I.putExtra("isTakeout",isTakeOutFlag);
        I.putExtra("partialPaid",havePartialPaid);
        startActivityForResult(I,Global.basketCode);

    }



    void makeItemWindow()
    {

        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        mainLayout.addView(l);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)l.getLayoutParams();
        lparams.width=hparams.width;
        lparams.height=97 * hparams.height/100;
        l.setLayoutParams(lparams);

        TextView v=new TextView(this);
        v.setText("      ");
        v.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        mainLayout.addView(v);
        LinearLayout.LayoutParams vvparams=(LinearLayout.LayoutParams)v.getLayoutParams();
        vvparams.width=hparams.width;
        vvparams.height=3 * hparams.height/100;
        v.setLayoutParams(vvparams);

        viewButton=new Button(this);
        viewButton.setText(getString(R.string.viewText));
        viewButton.setTextSize(Global.fontSize);
        viewButton.setTextColor(Color.WHITE);
        viewButton.setBackgroundResource(R.drawable.blueroundbutton);
        viewButton.setTransformationMethod(null);
        l.addView(viewButton);
        LinearLayout.LayoutParams vparams=(LinearLayout.LayoutParams)viewButton.getLayoutParams();
        vparams.leftMargin=5 * lparams.width/100;
        //vparams.topMargin=5 * lparams.height/100;
        vparams.width=43 * lparams.width/100;
        vparams.height=75 * lparams.height/100;
        vparams.gravity=Gravity.CENTER;
        viewButton.setLayoutParams(vparams);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBasket();

            }
        });

        splitButton=new Button(this);
        splitButton.setTextSize(Global.fontSize);
        splitButton.setTextColor(Color.parseColor(Global.textColor));
        splitButton.setBackgroundResource(R.drawable.transparentbutton);
        splitButton.setText(getString(R.string.splitButtonText));
        splitButton.setTransformationMethod(null);
        l.addView(splitButton);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)splitButton.getLayoutParams();
        sparams.leftMargin=vparams.leftMargin/2;
        sparams.width=vparams.width/2;
        sparams.height=vparams.height;
        sparams.gravity=Gravity.CENTER;
        splitButton.setLayoutParams(sparams);
        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderStore.hasTakeout())
                {
                    Global.showAlert(getString(R.string.impossibleSplit),OpenOrderActivity.this);
                }
                else
                    makeSplit();
            }
        });

        extrasButton=new Button(this);
        extrasButton.setTextSize(Global.fontSize);
        extrasButton.setTextColor(Color.parseColor(Global.textColor));
        extrasButton.setBackgroundResource(R.drawable.transparentbutton);
        extrasButton.setText(getString(R.string.extrasButtonText));
        extrasButton.setTransformationMethod(null);
        l.addView(extrasButton);
        extrasButton.setLayoutParams(sparams);
        extrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentItemPos==-1) return ;
                try {
                    int extraCategoryId=currentItem.getInt("extraCategoryId");
                    if(extraCategoryId!=0) showExtras();

                } catch (JSONException e) {
                    System.out.println("GIANNIS JSON EXCEPTION "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    void makeSplit()
    {
        final ArrayList<BasketItem> items=orderStore.getBasketItemsNoExtras();
        final SplitDialog dialog=new SplitDialog(this,items, orderStore);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(dialog.getSplit()) {
                    if (orderStore.getOrderSplit() == 0) {
                        //reset split
                        final String murl = Global.server + "/orders/" + orderId + "/split/reset";
                        final JSONObject params = new JSONObject();
                        InternetClass.putRequest(murl, params, Global.getAuthorizationString(Global.loginCredentials));
                    } else {
                        //if order has change split from 0 to zero then put with ids
                        //else put without ids
                        final JSONArray splitDetails = new JSONArray();
                        for (int i = 1; i <= orderStore.getOrderSplit(); i++) {
                            JSONObject x = new JSONObject();
                            try {
                                x.put("split", i);
                                JSONArray orderDetails = new JSONArray();
                                //if (orderStore.getHasPrinted())
                                {
                                    //send ids
                                    ArrayList<BasketItem> tt=orderStore.getBasketItemsOfSplit(i);
                                    for(int j=0;j<tt.size();j++)
                                    {
                                        JSONObject xx=orderStore.itemAtPos(tt.get(j).getCurrentItemPos());
                                        if(xx.has("id"))
                                        orderDetails.put(xx.getInt("id"));
                                    }
                                }
                                x.put("orderDetailIds", orderDetails);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            splitDetails.put(x);
                        }

                        final String murl = Global.server + "/orders/" + orderId + "/split/";
                        final JSONObject params = new JSONObject();
                        try {
                            params.put("splitType", orderStore.getSplitType());
                            params.put("numberOfSplits", orderStore.getOrderSplit());
                            params.put("splitDetails", splitDetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        InternetClass.putRequest(murl, params, Global.getAuthorizationString(Global.loginCredentials));

                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==Global.basketCode)
        {

            if (resultCode == Activity.RESULT_CANCELED) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            else {
                String ss = data.getExtras().getString("order");
                JSONObject json = null;
                try {
                    json = new JSONObject(ss);
                    orderStore = new OrderStore();
                    orderStore.setOrderId(orderId);
                    orderStore.setWaiterId(json.getInt("openedBy"));
                    if(havePartialPaid)
                        orderStore.enablePartialPaid();
                    if(isTakeOutFlag) {
                        orderStore.enableTakeOut();
                    }
                    orderStore.setOrderInfo(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                boolean payFlag=data.getExtras().getBoolean("payFlag");

                if(payFlag)
                {
                    try {
                        int waiterId=orderStore.getWaiterId();
                        if (
                                waiterId!=Global.loginCredentials.getJSONObject("user").getInt("id")
                                        &&  Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.others.pay") == false
                        )
                        {
                            Global.showAlert("Can not pay this order", OpenOrderActivity.this);
                        }
                        else
                            InternetClass.ObjectGETCall(OpenOrderActivity.this, Global.server + "/orders/" + orderId + "/payments",
                                    Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Intent I = new Intent(OpenOrderActivity.this, PayActivity.class);
                                            String object2string = orderStore.toString();
                                            I.putExtra("order", object2string);
                                            I.putExtra("orderId",orderId);
                                            String object3string = response.toString();
                                            I.putExtra("pay", object3string);
                                            OpenOrderActivity.this.startActivityForResult(I, Global.payCode);
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Global.showAlert(getString(R.string.executionProblemText)+" Get Pay Flag",OpenOrderActivity.this);
                                        }
                                    });
                    } catch (JSONException e) {
                        try {
                            Global.showAlert("Can not pay this order", OpenOrderActivity.this);

                            System.out.println("GIANNIS ERROR JSON IN PAY  "+e.getMessage()+" "+Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions"));
                        } catch (JSONException ex) {
                            Global.showAlert("Can not pay this order", OpenOrderActivity.this);

                            ex.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        if(requestCode==Global.payCode) {
            String ss = data.getExtras().getString("json");
            try {
                final JSONObject xx = new JSONObject(ss);
                xx.put("loyaltyPoints", null);
                xx.put("tip", null);
                if (xx.getInt("paymentMethod") == -1) return;
                if (xx.getJSONArray("orderDetailsIds").length() == 0
                        && xx.getDouble("amount")==0.0)
                    return;
                InternetClass.ObjectPOSTCall(this, Global.server + "/orders/" + orderId + "/pay", xx,
                        Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("GIANNIS RETURNED VALUE FROM PAY IS "+response);
                                orderStore.enablePartialPaid();
                                havePartialPaid=true;
                                if(fullOrderWindow!=null)
                                    fullOrderWindow.setOrder(orderStore);
                                try {
                                    if (response.getBoolean("totalAmountPaid") == true) {
                                        if(response.getDouble("change")>0.02)
                                        {
                                            OrderCloseDialog d=new OrderCloseDialog(OpenOrderActivity.this,getString(R.string.changeText)+
                                                    Global.currency+Global.displayDecimal(response.getDouble("change")),getString(R.string.okText),
                                                    "");
                                            d.show();
                                            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialogInterface) {
                                                    Intent returnIntent = new Intent();
                                                    setResult(Activity.RESULT_OK, returnIntent);
                                                    finish();
                                                }
                                            });
                                        }
                                        else {
                                            Intent returnIntent = new Intent();
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            finish();
                                        }
                                    }
                                    else
                                    {

                                        updateBasket();
                                    }
                                } catch (JSONException e) {
                                    System.out.println("GIANNIS JSON E "+e.getMessage());
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Global.showAlert(getString(R.string.executionProblemText)+" PAY "+error,OpenOrderActivity.this);

                            }
                        });
            } catch (JSONException e) {
                System.out.println("GIANNIS CATCH PAY EXCEPTION "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void makeHeaderView()
    {
        header=new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setBackgroundColor(Color.WHITE);
        mainLayout.addView(header);

        hparams=(LinearLayout.LayoutParams)header.getLayoutParams();
        hparams.width=Global.width;
        hparams.height=Global.headerHeight;
        header.setLayoutParams(hparams);
        header.setBackgroundResource(R.drawable.shadowline);
        ImageView im1=new ImageView(this);
        im1.setImageBitmap(Global.grayBack);
        header.addView(im1);
        im1.setClickable(true);
        im1.setOnTouchListener(Global.touchListener);
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderStore.getHasPrinted()==false)
                {
                    OrderCloseDialog d=new OrderCloseDialog(OpenOrderActivity.this);
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            int flag=((OrderCloseDialog )dialog).getCloseFlag();
                            if(flag==OrderCloseDialog.CLOSE_OK)
                            {
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        }
                    });

                }
                else {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im1.getLayoutParams();
        iparams.leftMargin=5 *hparams.width/100;
        iparams.width=Global.grayBack.getWidth();
        iparams.height=Global.grayBack.getHeight();
        iparams.topMargin=20 * hparams.height/100;
        im1.setLayoutParams(iparams);

        TextView t1=new TextView(this);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setTextSize(Global.bigFontSize);
        t1.setText(getString(R.string.orderText)+" #"+orderId);

        if(isTakeOutFlag) {
            String name="";
            for(int i=0;i<Global.users.length();i++)
            {
                try {
                    if(Global.users.getJSONObject(i).getInt("id")==orderStore.getWaiterId())
                    {
                        name=Global.users.getJSONObject(i).getString("name");
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            t1.setTextSize(Global.smallFontSize);
            t1.setText(t1.getText().toString() + " -- " +name);

        }
        else {
            try {
                t1.setText(t1.getText().toString()+" -- "+(orderStore.getTableCode().length()==0?
                        tableJson.getString("tableCode"):orderStore.getTableCode()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        header.addView(t1);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t1.getLayoutParams();
        tparams.topMargin=iparams.topMargin;
        tparams.leftMargin=2 * iparams.leftMargin;
        tparams.width=hparams.width/2;
        t1.setLayoutParams(tparams);

        threeDotsButton=new ImageView(this);
        threeDotsButton.setImageBitmap(Global.threeDots);
        header.addView(threeDotsButton);
        threeDotsButton.setClickable(true);
        threeDotsButton.setOnTouchListener(Global.touchListener);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)threeDotsButton.getLayoutParams();
        bparams.topMargin=iparams.topMargin;
        bparams.rightMargin=iparams.leftMargin;
        bparams.height=Global.threeDots.getHeight();
        bparams.width=Global.threeDots.getWidth();

        bparams.leftMargin=hparams.width-iparams.leftMargin-iparams.width-tparams.leftMargin-tparams.width-bparams.rightMargin-bparams.width;
        threeDotsButton.setLayoutParams(bparams);
        threeDotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extWindow!=null && extWindow.isShowing())
                {
                    extWindow.dismiss();
                    threeDotsButton.setImageBitmap(Global.threeDots);

                }
                else
                {
                    if(fullOrderWindow==null)
                    {
                        fullOrderWindow=new FullOrderWindow(OpenOrderActivity.this,mainLayout,Global.width,Global.height,orderStore);
                        fullOrderWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                if(fullOrderWindow.getFinish())
                                {
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                }
                            }
                        });
                    }
                    fullOrderWindow.setOrder(orderStore);
                    fullOrderWindow.showAtLocation(mainLayout,Gravity.CENTER,0,0);
                }
            }
        });
    }

    View.OnClickListener categoryListener=null;
    void makeCategories()
    {
        ScrollView catScroll=new ScrollView(this);
        mainLayout.addView(catScroll);
        catScroll.setScrollbarFadingEnabled(false);
        catScroll.setBackgroundResource(R.drawable.shadowline);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)catScroll.getLayoutParams();
        sparams.topMargin=hparams.height/80;
        sparams.width=hparams.width;
        sparams.height=2 * hparams.height;
        sparams.bottomMargin=sparams.topMargin;
        catScroll.setLayoutParams(sparams);
        catScroll.setPadding(0,2*sparams.height/100,0,2*sparams.height/100);
        TableLayout table=new TableLayout(this);
        catScroll.addView(table);
        ScrollView.LayoutParams tableParams=(ScrollView.LayoutParams)table.getLayoutParams();
        tableParams.width=sparams.width;
        tableParams.height=sparams.height-catScroll.getPaddingBottom()-catScroll.getPaddingTop();
        table.setLayoutParams(tableParams);
        final int itemsPerRow=3;

        int nrows= (isTakeOutFlag?Global.takeoutProductCategories.length():Global.tableProductCategories.length())/itemsPerRow;
        if(nrows * itemsPerRow<Global.productCategories.length()) nrows++;
        final ArrayList<Integer> catnumber=new ArrayList<Integer>();
        int idcount=0;
        selectedInnerCategoryId=-1;

        if(categoryListener==null)
        {
            categoryListener=new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int selectedPos=(Integer)v.getTag();

                    for(int k=0;k<catButton.size();k++)
                    {
                        if(k==selectedPos)
                            catButton.get(k).setBackgroundResource(R.drawable.yellowbutton);
                        else
                        {
                            JSONObject p= null;
                            try {
                                p = (isTakeOutFlag==false?Global.tableProductCategories.getJSONObject(k):Global.takeoutProductCategories.getJSONObject(k));
                                int colorId=p.getInt("btnColorId");
                                Drawable drawable = getDrawable(R.drawable.tablewhite);
                                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                                gradientDrawable.setColor(Color.parseColor(Global.getHexColor(colorId)));
                                catButton.get(k).setBackground(gradientDrawable);
                            } catch (JSONException e) {
                                System.out.println("GIANNIS E "+e.getMessage());
                                e.printStackTrace();
                            }

                        }
                    }
                    selectedCategoryPos=catnumber.get(selectedPos);
                    //check if it has inner product categories
                    int parentCategoryId= 0;
                    try {
                        boolean isGroupCategory=(isTakeOutFlag==false?Global.tableProductCategories.getJSONObject(selectedCategoryPos).getBoolean("isGroupCategory"):
                                Global.takeoutProductCategories.getJSONObject(selectedCategoryPos).getBoolean("isGroupCategory"));
                        if(isGroupCategory)
                        {
                            int currentId=(isTakeOutFlag==false?Global.tableProductCategories.getJSONObject(selectedCategoryPos).getInt("id")
                                    :Global.takeoutProductCategories.getJSONObject(selectedCategoryPos).getInt("id"));
                            final JSONArray selectedInnnerCategories=new JSONArray();
                            for(int j=0;j<Global.innerProductCategories.length();j++)
                            {
                                JSONObject xx=Global.innerProductCategories.getJSONObject(j);
                                if(xx.getInt("parentCategoryId")==currentId && !xx.getBoolean("isExtra"))
                                {
                                    selectedInnnerCategories.put(xx);
                                }

                            }
                            //find the inner Categories
                            InnerCategoriesDialog dialog=new InnerCategoriesDialog(OpenOrderActivity.this,selectedInnnerCategories);
                            dialog.show();
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    selectedInnerCategoryId=((InnerCategoriesDialog)dialog).getSelectedId();
                                    updateList();
                                }
                            });

                        }
                        else
                        {
                            selectedInnerCategoryId=-1;
                            updateList();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        for(int i=0;i<nrows;i++)
        {
            if(idcount>=(isTakeOutFlag==false?Global.tableProductCategories.length():Global.takeoutProductCategories.length())) break;
            TableRow row=new TableRow(this);
            table.addView(row);
            LinearLayout.LayoutParams rparams=(LinearLayout.LayoutParams)row.getLayoutParams();
            rparams.height=90 * hparams.height/100;
            row.setLayoutParams(rparams);

            HorizontalScrollView hsview=new HorizontalScrollView(this);
            row.addView(hsview);
            LinearLayout hslayout=new LinearLayout(this);
            hslayout.setOrientation(LinearLayout.HORIZONTAL);
            hsview.addView(hslayout);

            for(int j=0;j<itemsPerRow;j++)
            {
                try {
                    JSONObject p=(isTakeOutFlag==false?Global.tableProductCategories.getJSONObject(idcount):Global.takeoutProductCategories.getJSONObject(idcount));
                    if(p.has("isExtra")) {
                        if (p.getBoolean("isExtra") == true) {
                            continue;
                        }
                    }

                    int colorId=p.getInt("btnColorId");

                    Button bt=new Button(this);
                    bt.setTextSize(Global.smallFontSize);
                    bt.setTextColor(Color.parseColor(Global.textColor));
                    bt.setBackgroundResource(R.drawable.tablewhite);

                    Drawable drawable = getDrawable(R.drawable.tablewhite);
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    gradientDrawable.setColor(Color.parseColor(Global.getHexColor(colorId)));

                    bt.setBackground(drawable);

                    bt.setLines(2);
                    bt.setText(p.getString("name"));
                    catnumber.add(idcount);
                    hslayout.addView(bt);

                    LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)bt.getLayoutParams();
                    bparams.leftMargin=2*hparams.width/100;
                    bparams.topMargin=2 * hparams.height/100;
                    bparams.height=85 * hparams.height/100;
                    bparams.width=30*hparams.width/100;
                    bparams.gravity=Gravity.CENTER;
                    bparams.bottomMargin=bparams.topMargin;
                    bt.setLayoutParams(bparams);

                    catButton.add(bt);
                    bt.setTag(new Integer(idcount));
                    idcount++;
                    bt.setOnClickListener(categoryListener);
                    if(idcount>=(isTakeOutFlag==false?Global.tableProductCategories.length():Global.takeoutProductCategories.length()))
                        break;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    void makeItems()
    {
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(l);
        LinearLayout.LayoutParams llparams=(LinearLayout.LayoutParams)l.getLayoutParams();
        llparams.height= 85* hparams.height/100;
        l.setLayoutParams(llparams);

        selectedCategoryText=new TextView(this);
        selectedCategoryText.setTextSize(Global.bigFontSize);
        selectedCategoryText.setTextColor(Color.parseColor(Global.textColor));
        try {
            selectedCategoryText.setText((isTakeOutFlag==false?Global.tableProductCategories.getJSONObject(selectedCategoryPos).getString("name")
                    :Global.takeoutProductCategories.getJSONObject(selectedCategoryPos).getString("name")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        l.addView(selectedCategoryText);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)selectedCategoryText.getLayoutParams();
        sparams.topMargin=10 * hparams.height/100;
        sparams.leftMargin=5 * Global.width/100;
        sparams.gravity=Gravity.CENTER_VERTICAL;
        sparams.width=90 * Global.width/100;
        sparams.height=50*hparams.height/100;
        selectedCategoryText.setLayoutParams(sparams);

        TextView lineView=new TextView(this);
        lineView.setBackgroundResource(R.drawable.backwithborderline);
        l.addView(lineView);

        productList=new ListView(this);
        mainLayout.addView(productList);
        productList.setSelection(ListView.CHOICE_MODE_SINGLE);
        productList.setSelector(new ColorDrawable(0xffffea9c));
        productList.setScrollbarFadingEnabled(false);

        LinearLayout.LayoutParams pparams=(LinearLayout.LayoutParams)productList.getLayoutParams();
        pparams.width=hparams.width;
        pparams.height=Global.height-hparams.height-2*hparams.height-hparams.height-hparams.height;
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    int waiterId=orderStore.getWaiterId();
                    if
                    (Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions")
                            .getBoolean("orders.others.edit")==false

                            && waiterId!=Global.loginCredentials.getJSONObject("user").getInt("id")

                    )
                    {
                        Global.showAlert(getString(R.string.operationNotPermittedText),OpenOrderActivity.this);
                    }
                    else {
                        if (idItems.getJSONObject(position).getBoolean("isGroupItem")) {

                            try {
                                selectedCategoryText.setText(idItems.getJSONObject(position).getString("itemName"));
                                int catid = idItems.getJSONObject(position).getInt("categoryId");
                                int parentid=idItems.getJSONObject(position).getInt("id");
                                idItems = Global.getSubProductItemsOfCategory(catid,parentid);
                                ArrayList<String> dummy = new ArrayList<String>();
                                for (int i = 0; i < idItems.length(); i++) {
                                    dummy.add(new String("" + i));
                                }
                                productAdapter = new ItemAdapter(OpenOrderActivity.this, idItems, dummy, hparams,false,isTakeOutFlag);
                                productList.setAdapter(productAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Snackbar bar = Snackbar.make(productList, getString(R.string.itemUndoText), Snackbar.LENGTH_LONG);
                            bar.setActionTextColor(Color.parseColor("#fed65e"));

                            bar.show();
                            View vv = bar.getView();

                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) vv.getLayoutParams();
                            params.gravity = Gravity.CENTER_VERTICAL;
                            params.height = 75 * Global.headerHeight / 100;
                            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
                            params.setMargins(0, Global.height / 4, 0, 0);
                            vv.setLayoutParams(params);

                            bar.setAction(getString(R.string.undoText), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    orderStore.removeLastItem();
                                    productList.clearChoices();
                                    currentItemPos = -1;
                                    if (extWindow != null)
                                        if (extWindow.isShowing()) extWindow.dismiss();
                                }
                            });
                            try {
                                currentItem = idItems.getJSONObject(position);

                                orderStore.addItemToOrder(idItems.getJSONObject(position));
                                currentItemPos = orderStore.getSize() - 1;
                                boolean selfOpenExtras = currentItem.getBoolean("selfOpenExtras");
                                if (selfOpenExtras) {
                                    showExtras();
                                }
                            } catch (JSONException e) {
                                System.out.println("GIANNIS E"+e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("GIANNIS E "+e.getMessage());
                    e.printStackTrace();
                }

            }
        });
    }


    void updateList()
    {
        if(selectedInnerCategoryId!=-1)
        {
            try {
                int innerPos=-1;
                for(int k=0;k<Global.innerProductCategories.length();k++)
                {
                    if(Global.innerProductCategories.getJSONObject(k).getInt("id")==selectedInnerCategoryId)
                    {
                        innerPos=k;
                        break;
                    }
                }
                selectedCategoryText.setText(Global.innerProductCategories.getJSONObject(innerPos).getString("name"));
                int catid = Global.innerProductCategories.getJSONObject(innerPos).getInt("id");
                idItems = Global.getProductItemsOfCategory(catid);
                ArrayList<String> dummy = new ArrayList<String>();
                for (int i = 0; i < idItems.length(); i++) {
                    dummy.add(new String("" + i));
                }
                productAdapter = new ItemAdapter(this, idItems, dummy, hparams,true,isTakeOutFlag);
                productList.setAdapter(productAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                selectedCategoryText.setText((isTakeOutFlag==false?Global.tableProductCategories.getJSONObject(selectedCategoryPos).getString("name"):
                        Global.takeoutProductCategories.getJSONObject(selectedCategoryPos).getString("name")));
                int catid = (isTakeOutFlag==false?Global.tableProductCategories.getJSONObject(selectedCategoryPos).getInt("id"):
                        Global.takeoutProductCategories.getJSONObject(selectedCategoryPos).getInt("id"));
                idItems = Global.getProductItemsOfCategory(catid);
                ArrayList<String> dummy = new ArrayList<String>();
                for (int i = 0; i < idItems.length(); i++) {
                    dummy.add(new String("" + i));
                }
                productAdapter = new ItemAdapter(this, idItems, dummy, hparams,true,isTakeOutFlag);
                productList.setAdapter(productAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void fillOrderTable(JSONObject x)
    {
        if(x.length()!=0)
        {

            try {
                int levelId=Global.levelIndex>=0?
                        Global.levels.getJSONObject(Global.levelIndex).getInt("id"):0;
                x.put("levelId",levelId);
            } catch (JSONException e) {

                e.printStackTrace();
            };
            orderStore.setOrderInfo(x);

            try {
                if(x.has("details") && x.getJSONArray("details").length()!=0) {
                    JSONArray details=x.getJSONArray("details");
                    for(int i=0;i<details.length();i++)
                    {
                        JSONObject xx=details.getJSONObject(i);
                        xx.put("hasChanged",false);
                        details.put(i,xx);
                    }
                    x.put("details",details);
                    orderStore.setOrderInfo(x);
                    mainLayout.post(new Runnable() {
                        public void run() {

                            updateBasket();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            InternetClass.ObjectGETCall(OpenOrderActivity.this, Global.server + "/orders/" + orderId + "/payments",
                    Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getJSONArray("orderPayments").length()!=0) {
                                    havePartialPaid=true;
                                    orderStore.enablePartialPaid();
                                }

                            } catch (JSONException e) {
                                System.out.println("GIANNIS E "+e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

        }
    }

    void createActivity()
    {
        makeHeaderView();
        makeCategories();
        makeItems();
        makeItemWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        setContentView(mainLayout);
        categoryListener=null;
        havePartialPaid=false;
        Bundle extras = getIntent().getExtras();
        try {
            tableJson=new JSONObject(extras.getString("tableJson"));
            orderId=tableJson.getJSONObject("order").getInt("orderId");
            isTakeOutFlag=extras.getBoolean("isTakeOut");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderStore=new OrderStore();
        orderStore.setOrderId(orderId);
        if(isTakeOutFlag) {

            try {
                orderStore.setWaiterId(Global.loginCredentials.getJSONObject("user").getInt("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            orderStore.enableTakeOut();

        }
        else {
            try {
                orderStore.setWaiterId(tableJson.getJSONObject("order").getInt("waiterId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        createActivity();
        if(isTakeOutFlag)
        {
            try {
                JSONObject response=new JSONObject(extras.getString("details"));
                orderStore.setWaiterId(response.getInt("openedBy"));
                fillOrderTable(response);
            } catch (JSONException e) {
                System.out.println("GIANNIS ERROR E "+e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            InternetClass.ObjectGETCall(this, Global.server + "/orders/" + orderId, Global.getAuthorizationString(Global.loginCredentials),
                    new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            fillOrderTable(response);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Global.showAlert(getString(R.string.executionProblemText) + " Fetch ORDER", OpenOrderActivity.this);

                        }
                    });
        }
    }

    public  void onBackPressed()
    {
        //nothing here
    }
}