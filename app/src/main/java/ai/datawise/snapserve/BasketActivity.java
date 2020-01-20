package ai.datawise.snapserve;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BasketActivity extends AppCompatActivity implements BasketInterface {

    LinearLayout mainLayout=null;
    OrderStore order=null;
    OrderInfoRow infoRow=null;
    LinearLayout header=null;
    LinearLayout.LayoutParams hparams;
    ImageView threeDotsButton=null;
    JSONObject tableJson=null;
    int orderId=0;
    private boolean payFlag=false;
    ArrayList<BasketItem> basketItems=new ArrayList<BasketItem>();
    BasketAdapter basketAdapter=null;
    ProgressDialog progressBar=null;
    ListView basketList=null;
    LinearLayout.LayoutParams basketParams;

    PopupWindow viewWindow=null;
    Button extrasButton=null;
    ExtrasWindow wextras=null;
    int lastSelectedId=-1;
    Button printButton=null;
    FullOrderWindow fullOrderWindow=null;
    ImageButton notesButton=null;
    LinearLayout buttonLayout=null;
    LinearLayout.LayoutParams blparams;

    void closeActivity()
    {
        if(viewWindow.isShowing())
        {
            basketParams.height=Global.height-3*Global.headerHeight;
            basketList.setLayoutParams(basketParams);
            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                    blparams.bottomMargin-blparams.height-hparams.height;
            buttonLayout.setLayoutParams(blparams);
            viewWindow.dismiss();
        }
        if(wextras!=null && wextras.isShowing()) wextras.dismiss();
        Intent returnIntent = new Intent();
        String orderstring=order.toString();
        returnIntent.putExtra("order",orderstring);
        returnIntent.putExtra("payFlag",payFlag);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    void finishActivity()
    {
        if(viewWindow.isShowing())
        {
            basketParams.height=Global.height-3*Global.headerHeight-2;
            basketList.setLayoutParams(basketParams);

            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                    blparams.bottomMargin-blparams.height-hparams.height;
            buttonLayout.setLayoutParams(blparams);
            viewWindow.dismiss();
        }
        if(wextras!=null && wextras.isShowing()) wextras.dismiss();
        Intent returnIntent = new Intent();

        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    void makeViewWindow()
    {
        viewWindow=new PopupWindow(mainLayout,
                Global.width, Global.headerHeight);
        HorizontalScrollView scrollView=new HorizontalScrollView(this);
        viewWindow.setContentView(scrollView);

        final LinearLayout.LayoutParams hparams=new LinearLayout.LayoutParams(Global.width,Global.headerHeight);

        LinearLayout row1=new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        scrollView.addView(row1);
        ScrollView.LayoutParams rparams=(ScrollView.LayoutParams)row1.getLayoutParams();
        rparams.topMargin=5 * hparams.height/100;
        rparams.width=hparams.width;
        rparams.height=90*hparams.height/100;
        row1.setLayoutParams(rparams);


        extrasButton=new Button(this);
        extrasButton.setTextSize(Global.smallFontSize);
        extrasButton.setTextColor(Color.parseColor(Global.textColor));
        extrasButton.setText(getString(R.string.extrasButtonText));
        extrasButton.setBackgroundResource(R.drawable.disabledbutton);
        extrasButton.setEnabled(false);
        row1.addView(extrasButton);
        final LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)extrasButton.getLayoutParams();
        tparams.leftMargin=1*hparams.width/100;
        tparams.width=20 * hparams.width/100;
        tparams.height=75 * rparams.height/100;
        tparams.gravity= Gravity.CENTER_VERTICAL;
        tparams.topMargin=8 * rparams.height/100;
        tparams.bottomMargin=tparams.topMargin;
        extrasButton.setLayoutParams(tparams);

        extrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastSelectedId==-1) return;
                if(wextras==null)
                    wextras=new ExtrasWindow(BasketActivity.this,mainLayout,Global.width,Global.height-Global.headerHeight,order);
                wextras.showAtLocation(mainLayout,Gravity.CENTER,0,Global.headerHeight);
                wextras.setInformation(order, lastSelectedId,order.itemAtPos(lastSelectedId));
                threeDotsButton.setImageBitmap(Global.xicon);
                wextras.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        threeDotsButton.setImageBitmap(Global.threeDots);
                        lastSelectedId=-1;
                        extrasButton.setEnabled(false);
                        extrasButton.setBackgroundResource(R.drawable.disabledbutton);
                        if(viewWindow.isShowing())
                        {
                            basketParams.height=Global.height-3*Global.headerHeight-2;
                            basketList.setLayoutParams(basketParams);
                            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                                    blparams.bottomMargin-blparams.height-hparams.height;
                            buttonLayout.setLayoutParams(blparams);

                            viewWindow.dismiss();
                        }
                        updateBasket();
                    }
                });
            }
        });

        Button minusButton=new Button(this);
        minusButton.setTextSize(Global.bigFontSize);
        minusButton.setTextColor(extrasButton.getCurrentTextColor());
        minusButton.setBackgroundResource(R.drawable.transparentbutton);
        minusButton.setText("-");
        row1.addView(minusButton);

        LinearLayout.LayoutParams mparams=(LinearLayout.LayoutParams)minusButton.getLayoutParams();
        mparams.leftMargin=tparams.leftMargin;
        mparams.height=tparams.height;
        mparams.width=65*tparams.width/100;
        mparams.gravity=tparams.gravity;
        mparams.topMargin=tparams.topMargin;
        mparams.bottomMargin=mparams.topMargin;
        minusButton.setLayoutParams(mparams);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseItems();
            }
        });

        Button plusButton=new Button(this);
        plusButton.setTextSize(Global.bigFontSize);
        plusButton.setTextColor(extrasButton.getCurrentTextColor());
        plusButton.setBackgroundResource(R.drawable.transparentbutton);
        plusButton.setText("+");
        row1.addView(plusButton);
        plusButton.setLayoutParams(mparams);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseItems();
            }
        });

        Button deleteButton=new Button(this);
        deleteButton.setTextSize(Global.smallFontSize);
        deleteButton.setTextColor(extrasButton.getCurrentTextColor());
        deleteButton.setBackgroundResource(R.drawable.transparentbutton);
        deleteButton.setText(getString(R.string.delButtonText));
        row1.addView(deleteButton);
        LinearLayout.LayoutParams delparams=(LinearLayout.LayoutParams)deleteButton.getLayoutParams();
        delparams.leftMargin=tparams.leftMargin;
        delparams.width=65*tparams.width/100;
        delparams.height=tparams.height;
        delparams.gravity=mparams.gravity;
        delparams.topMargin=mparams.topMargin;
        delparams.bottomMargin=delparams.topMargin;
        deleteButton.setLayoutParams(delparams);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItems(true);
            }
        });

        final Button transferButton=new Button(this);
        transferButton.setTextSize(Global.smallFontSize);
        transferButton.setTextColor(Color.parseColor(Global.textColor));
        transferButton.setBackgroundResource(R.drawable.transparentbutton);
        transferButton.setText(getString(R.string.transferButtonText));
        row1.addView(transferButton);
        transferButton.setLayoutParams(tparams);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.transfer") == false) {
                        final OrderCloseDialog dialog=new OrderCloseDialog(BasketActivity.this,getString(R.string.requestAccess),
                                "CANCEL","OK");
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                if(dialog.getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL)
                                {
                                    AuthRequest.makeRequest(BasketActivity.this, order.getOrderId(),  Global.TYPE_TRANSFER_ORDER, new AuthInterface() {
                                        @Override
                                        public void onApproval() {
                                            makeTransfer();
                                        }

                                        @Override
                                        public void onRejected() {
                                            Global.showAlert("Rejected request",BasketActivity.this);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        makeTransfer();
                    }
                } catch (JSONException e) {
                    Global.showAlert(getString(R.string.executionProblemText) + "Transfer", BasketActivity.this);
                    e.printStackTrace();
                }
            }
        });
        notesButton=new ImageButton(this);

        if(Global.rectnotesIcon==null)
        {
            Global.rectnotesIcon= BitmapFactory.decodeResource(getResources(),R.drawable.rectnotes);
            Global.rectnotesIcon=Global.rectnotesIcon.createScaledBitmap(Global.rectnotesIcon,90*delparams.width/100,90*delparams.height/100,true);
        }

        notesButton.setImageBitmap(Global.rectnotesIcon);
        notesButton.setBackgroundResource(R.drawable.transparentbutton);
        row1.addView(notesButton);
        notesButton.setLayoutParams(delparams);
        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Integer> items = basketAdapter.getIsSelected();
                int selectedItem=-1;
                int selectedCount=0;

                for(int i=0;i<items.size();i++)
                {
                    if(items.get(i)==1)
                    {
                        selectedCount++;
                        selectedItem=i;
                    }
                }
                if (selectedCount>=1) {
                    String currentNotes="";
                    if(selectedCount==1)
                        currentNotes=order.getNotes(basketItems.get(selectedItem).currentItemPos);
                    final NotesDialog d = new NotesDialog(BasketActivity.this, currentNotes);
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(d.getNotes().length()!=0) {
                                for(int i=0;i<items.size();i++) {
                                    if(items.get(i)==1) {
                                        final int pos = basketItems.get(i).getCurrentItemPos();
                                        order.setNotes(pos, d.getNotes());
                                    }

                                }updateBasket();
                            }
                        }
                    });

                }
            }
        });
    }

    void makeTransfer()
    {
        if (order.getHasPrinted() == false) {
            TransferWarningDialog d = new TransferWarningDialog(BasketActivity.this);
            d.show();
        } else {
            if (order.getPartialPaid()) {
                TransferWarningDialog d = new TransferWarningDialog(this, getString(R.string.partialPaidText));
                d.show();
            } else {
                TransferClass transferClass = new TransferClass(BasketActivity.this);
                transferClass.show();
                transferClass.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        TransferClass d = (TransferClass) dialog;
                        int levelId = d.getSelectedLevelId();
                        String tableCode = d.getSelectedTableCode();
                        if (levelId != -1) {
                            final JSONObject transfer = new JSONObject();
                            try {
                                transfer.put("tableCode", tableCode);
                                transfer.put("persons", -1);
                                JSONArray ids = new JSONArray();
                                final ArrayList<Integer> items = basketAdapter.getIsSelected();
                                sumSelected = 0;
                                for (int k = 0; k < items.size(); k++) {
                                    if (items.get(k) == 1) {
                                        int pos = basketItems.get(k).getCurrentItemPos();
                                        JSONObject object = order.itemAtPos(pos);
                                        ids.put(object.getInt("id"));
                                    }
                                    sumSelected += items.get(k);
                                }
                                //todo edo na doume oti ontos ta dialegei ola
                                if (sumSelected == order.orderCount()) ids = new JSONArray();

                                transfer.put("orderDetailIds", ids);
                                InternetClass.ObjectPOSTCall(BasketActivity.this, Global.server + "/orders/" + order.getOrderId() + "/transfer",
                                        transfer, Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                if (sumSelected == 0 || sumSelected == order.orderCount()) {
                                                    //delete all items
                                                    //todo edo na doume oti ontos kanei close Activity
                                                    if (order.orderCount() != 0)
                                                        order.removeAll();
                                                    finishActivity();
                                                } else {
                                                    removeItems(false);
                                                }
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Global.showAlert(getString(R.string.executionProblemText) + "Transfer", BasketActivity.this);
                                            }
                                        });

                            } catch (JSONException e) {
                                Global.showAlert(getString(R.string.executionProblemText) + "Transfer", BasketActivity.this);
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        }
    }

    boolean getPayFlag()
    {
        return  payFlag;
    }
    void displayProgressBar()
    {
        progressBar=new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage(getString(R.string.loadPayment));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
    }

    void increaseItems()
    {
        ArrayList<Integer> items=basketAdapter.getIsSelected();
        for(int i=1;i<items.size();i++)
            if(items.get(i).intValue()==1) {
                int pos=basketItems.get(i).getCurrentItemPos();

                if(order.itemCanChange(pos)==false) continue;


                if(basketItems.get(i).getIsExtra()==true) {
                    order.increaseExtraItem(pos, basketItems.get(i).getId());
                }
                else
                    order.increaseItem(pos);
            }
        updateBasket();
        infoRow.update();
        basketAdapter.setIsSelected(items);
       /* basketAdapter.deselectAll();
        if(viewWindow.isShowing())
        {
            basketParams.height=Global.height-3*Global.headerHeight-2;
            basketList.setLayoutParams(basketParams);
            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                    blparams.bottomMargin-blparams.height-hparams.height;
            buttonLayout.setLayoutParams(blparams);

            viewWindow.dismiss();
        }*/
    }

    void decreaseItems()
    {
        ArrayList<Integer> items=basketAdapter.getIsSelected();
        for(int i=1;i<items.size();i++)
            if(items.get(i).intValue()==1) {
                int pos=basketItems.get(i).getCurrentItemPos();
                if(order.itemCanChange(pos)==false) continue;

                if(basketItems.get(i).getIsExtra()==true)
                    order.decreaseExtraItem(pos,basketItems.get(i).getId());
                else
                    order.decreaseItem(pos);
            }
        updateBasket();
        infoRow.update();
        basketAdapter.setIsSelected(items);
        /*basketAdapter.deselectAll();
        if(viewWindow.isShowing())
        {
            basketParams.height=Global.height-3*Global.headerHeight-2;
            basketList.setLayoutParams(basketParams);
            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                    blparams.bottomMargin-blparams.height-hparams.height;
            buttonLayout.setLayoutParams(blparams);

            viewWindow.dismiss();
        }*/
    }

    //removeItems
    void removeItems(boolean hasChangedCheck)
    {
        ArrayList<Integer> items=basketAdapter.getIsSelected();
        ArrayList<Integer> toDel=new ArrayList<Integer>();
        for(int i=1;i<items.size();i++)
            if(items.get(i).intValue()==1)
            {

                int pos=basketItems.get(i).getCurrentItemPos();
                if(order.itemCanChange(pos)==false && hasChangedCheck) continue;
                //delete from order
                //an einai item feygei olo to item. An einai extra feygei aplos to extra
                if(basketItems.get(i).getIsExtra()==false)
                    toDel.add(basketItems.get(i).getCurrentItemPos());
                else
                    order.removeExtrasItem(pos,basketItems.get(i).getId());

            }
        for(int i=0;i<toDel.size();i++)
        {
            for(int j=0;j<toDel.size()-1;j++)
            {
                if(toDel.get(j+1)>toDel.get(j))
                {
                    Integer x=toDel.get(j);
                    toDel.set(j,toDel.get(j+1));
                    toDel.set(j+1,x);
                }
            }
        }
        for(int i=0;i<toDel.size();i++)
            order.removeItem(toDel.get(i));

        updateBasket();
        infoRow.update();
        basketAdapter.deselectAll();
        if(viewWindow.isShowing())
        {
            basketParams.height=Global.height-3*Global.headerHeight-2;
            basketList.setLayoutParams(basketParams);
            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                    blparams.bottomMargin-blparams.height-hparams.height;
            buttonLayout.setLayoutParams(blparams);

            viewWindow.dismiss();
        }
    }
    int sumSelected=0;

    public void updateBasket()
    {
        basketItems=order.getBasketItems();

        basketAdapter=null;
        basketAdapter=new BasketAdapter(this,basketItems,this);
        basketList.setAdapter(basketAdapter);
        if( basketItems.size()!=0 && order.allItemsHaveCashier()) {
            printButton.setText(getString(R.string.paymentText));
        }
        else
        if( basketItems.size()!=0 && order.getHasPrinted())
            printButton.setText(getString(R.string.billButtonText));
        else
            printButton.setText(getString(R.string.printButtonText));
        double totalValue=order.allOrdersCost();

        infoRow.update();

    }

    void makeHeader()
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
                if(order.getHasPrinted()==false)
                {
                    OrderCloseDialog d=new OrderCloseDialog(BasketActivity.this);
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            int flag=((OrderCloseDialog )dialog).getCloseFlag();
                            if(flag==OrderCloseDialog.CLOSE_OK)
                            {
                                finishActivity();
                            }
                        }
                    });

                }
                else {
                    finishActivity();
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
        if(order.hasTakeout())
        {
            String name="";
            int wid=order.getOpenedBy();
            for(int i=0;i<Global.users.length();i++)
            {
                try {
                    if(Global.users.getJSONObject(i).getInt("id")==wid)
                    {
                        name=Global.users.getJSONObject(i).getString("name");
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            t1.setTextSize(Global.smallFontSize);
            t1.setText(t1.getText()+" -- "+name);
        }
        else
            t1.setText(t1.getText()+" -- "+order.getTableCode());
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
                if(wextras!=null && wextras.isShowing())
                {
                    wextras.dismiss();
                    threeDotsButton.setImageBitmap(Global.threeDots);

                }
                else
                {
                    if(fullOrderWindow==null)
                    {
                        fullOrderWindow=new FullOrderWindow(BasketActivity.this,mainLayout,Global.width,Global.height,order);
                        fullOrderWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                if(fullOrderWindow.getFinish())
                                    finishActivity();
                                else updateBasket();
                            }
                        });
                    }
                    fullOrderWindow.showAtLocation(mainLayout, Gravity.CENTER,0,0);
                }
            }
        });
    }

    void makeList()
    {
        basketList=new ListView(this);
        mainLayout.addView(basketList);

        basketParams=(LinearLayout.LayoutParams)basketList.getLayoutParams();
        basketParams.width=Global.width;
        basketParams.height=Global.height-3*Global.headerHeight-2;
        basketParams.gravity= Gravity.TOP;
        basketList.setLayoutParams(basketParams);
        basketList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        basketList.setDividerHeight(3);
    }

    void makeButtons()
    {
        buttonLayout=new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        mainLayout.addView(buttonLayout);
        blparams=(LinearLayout.LayoutParams)buttonLayout.getLayoutParams();
        blparams.width=Global.width;
        blparams.height=90*Global.headerHeight/100;
        blparams.bottomMargin=5 * Global.headerHeight/100;
        blparams.topMargin=Global.height-hparams.height-basketParams.height-
                blparams.bottomMargin-blparams.height-hparams.height;
        buttonLayout.setLayoutParams(blparams);

        final Button editButton=new Button(this);
        editButton.setTextSize(Global.smallFontSize);
        editButton.setTextColor(Color.WHITE);
        editButton.setBackgroundResource(R.drawable.blueroundbutton);
        editButton.setText(getString(R.string.editText));
        buttonLayout.addView(editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });

        LinearLayout.LayoutParams pparams=(LinearLayout.LayoutParams)editButton.getLayoutParams();
        pparams.leftMargin=2*Global.width/100;
        pparams.gravity=Gravity.CENTER;
        pparams.topMargin=5 *blparams.height/100;
        pparams.height=85*blparams.height/100;
        pparams.width=18*Global.width/100;
        editButton.setLayoutParams(pparams);

        printButton=new Button(this);
        printButton.setTextSize(Global.smallFontSize);
        printButton.setTextColor(Color.parseColor(Global.textColor));
        printButton.setBackgroundResource(R.drawable.roundyellowbutton);
        printButton.setText(getString(R.string.printButtonText));
        buttonLayout.addView(printButton);
        printButton.setLayoutParams(pparams);

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printButton.setEnabled(false);
                final int orderId = order.getOrderId();
                if( basketItems.size()!=0 && printButton.getText().toString().equals(getString(R.string.paymentText)))
                {
                    payFlag=true;
                    printButton.setEnabled(true);
                    closeActivity();

                }
                else
                if ( basketItems.size()!=0 &&  printButton.getText().toString().equals(BasketActivity.this.getString(R.string.billButtonText))) {

                    String url = Global.server+"/orders/"+orderId+"/receipt";
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    //GET ORDER AGAIN TO UPDATE CASHIER STATUS
                                    printButton.setEnabled(true);
                                    order.billOrder();
                                    printButton.setText(getString(R.string.paymentText));
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    printButton.setEnabled(true);
                                    progressBar.hide();
                                    Global.showAlert(getString(R.string.executionProblemText)+" Bill",BasketActivity.this);
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", Global.getAuthorizationString(Global.loginCredentials));
                            params.put("Content-Type", "application/json");
                            return params;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(BasketActivity.this);
                    queue.add(postRequest);

                } else {
                    JSONObject orderInfo = order.getOrderInfoChanged();
                    try {
                        final JSONArray details = orderInfo.getJSONArray("details");
                        displayProgressBar();
                        if (details.length() == 0) {
                            printButton.setEnabled(true);
                            progressBar.hide();

                            order.print(details,null);
                            return;
                        }
                        InternetClass.ArrayPostCall(BasketActivity.this, Global.server + "/orders/" + orderId + "/add_and_print",
                                details, Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        InternetClass.ObjectGETCall(BasketActivity.this, Global.server + "/orders/" + orderId,
                                                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {

                                                        order.setOrderInfo(response);
                                                        order.print(details,new JSONArray());
                                                        printButton.setText(getString(R.string.billButtonText));
                                                        progressBar.hide();
                                                        printButton.setEnabled(true);
                                                        updateBasket();
                                                    }

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        printButton.setEnabled(true);
                                                        progressBar.hide();
                                                    }
                                                });

                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Global.showAlert(getString(R.string.executionProblemText)+" PRINT "+error,BasketActivity.this);
                                        System.out.println("GIANNIS FAILED DETAILS ARE "+details);
                                        progressBar.hide();
                                        printButton.setEnabled(true);

                                    }
                                });
                    } catch (JSONException e) {
                        printButton.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }
        });



        Button discountButton=new Button(this);
        discountButton.setTextSize(Global.smallFontSize);
        discountButton.setTextColor(printButton.getCurrentTextColor());
        discountButton.setBackgroundResource(R.drawable.transparentbutton);
        discountButton.setText(getString(R.string.discountText));
        buttonLayout.addView(discountButton);
        discountButton.setLayoutParams(pparams);
        discountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").has("orders.discount")
                            ||
                            Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.discount") == false) {

                        final OrderCloseDialog dialog=new OrderCloseDialog(BasketActivity.this,getString(R.string.requestAccess),
                                "CANCEL","OK");
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                if(dialog.getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL)
                                {
                                    AuthRequest.makeRequest(BasketActivity.this, order.getOrderId(),  Global.TYPE_DISCOUNT_ORDER, new AuthInterface() {
                                        @Override
                                        public void onApproval() {
                                            makeDiscount();
                                        }

                                        @Override
                                        public void onRejected() {
                                            Global.showAlert("Rejected request",BasketActivity.this);
                                        }
                                    });
                                }
                            }
                        });

                    } else {
                        makeDiscount();
                    }
                } catch (JSONException e) {
                    System.out.println("GIANNIS E "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        Button treatButton=new Button(this);
        treatButton.setTextSize(Global.smallFontSize);
        treatButton.setTextColor(printButton.getCurrentTextColor());
        treatButton.setBackgroundResource(R.drawable.transparentbutton);
        treatButton.setText(getString(R.string.treatButtonText));
        buttonLayout.addView(treatButton);
        treatButton.setLayoutParams(pparams);
        treatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.treat")==false)
                    {
                        final OrderCloseDialog dialog=new OrderCloseDialog(BasketActivity.this,BasketActivity.this.getString(R.string.requestAccess),
                                "CANCEL","OK");
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                if(dialog.getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL) {
                                    AuthRequest.makeRequest(BasketActivity.this, order.getOrderId(),  Global.TYPE_TREAT_ORDER, new AuthInterface() {
                                        @Override
                                        public void onApproval() {
                                            makeTreat();
                                        }

                                        @Override
                                        public void onRejected() {
                                            Global.showAlert("Rejected request",BasketActivity.this);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else
                    {
                        makeTreat();
                    }
                } catch (JSONException e) {
                    System.out.println("GIANNIS TREAT "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });


        Button splitButton=new Button(this);
        splitButton.setTextSize(Global.smallFontSize);
        splitButton.setTextColor(printButton.getCurrentTextColor());
        splitButton.setBackgroundResource(R.drawable.transparentbutton);
        splitButton.setText(getString(R.string.splitButtonText));
        buttonLayout.addView(splitButton);
        splitButton.setLayoutParams(pparams);
        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(order.hasTakeout())
                {
                    Global.showAlert(getString(R.string.impossibleSplit),BasketActivity.this);
                }
                else {
                    //if some items are printed the first
                    //we should print the non printed items and then we should make split
                    makeSplit();
                }
            }
        });
    }


    void makeAddAndPrint()
    {
        JSONObject orderInfo = order.getOrderInfo();
        try {
            InternetClass.putRequest(Global.server+"/orders/"+orderId+"/details",orderInfo.getJSONArray("details"),
                    Global.getAuthorizationString(Global.loginCredentials)
                    );
            updateBasket();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeSplit()
    {

        ArrayList<Integer> vv=basketAdapter.getIsSelected();
        final ArrayList<BasketItem> items=new ArrayList<BasketItem>();

            for(int i=1;i<vv.size();i++)
            {
                if(!basketItems.get(i).getIsExtra())
                {
                    items.add(basketItems.get(i));
                }
            }
        final SplitDialog dialog=new SplitDialog(this,items, order);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(dialog.getSplit()) {

                    if (order.getOrderSplit() == 0) {
                        //reset split
                        final String murl = Global.server + "/orders/" + orderId + "/split/reset";
                        final JSONObject params = new JSONObject();
                        InternetClass.putRequest(murl,params,Global.getAuthorizationString(Global.loginCredentials));
                    } else {

                            //if order has change split from 0 to zero then put with ids
                            //else put without ids
                            final JSONArray splitDetails = new JSONArray();

                            for (int i = 1; i <= order.getOrderSplit(); i++) {
                                JSONObject x = new JSONObject();
                                try {
                                    x.put("split", i);
                                    JSONArray orderDetails = new JSONArray();
                                    //if (order.getHasPrinted())
                                    {
                                        //send ids
                                        ArrayList<BasketItem> tt = order.getBasketItemsOfSplit(i);
                                        for (int j = 0; j < tt.size(); j++) {
                                            JSONObject xx = order.itemAtPos(tt.get(j).getCurrentItemPos());
                                            if(xx.has("id"))
                                            orderDetails.put(xx.getInt("id"));
                                        }
                                    }
                                    x.put("orderDetailIds", orderDetails);
                                } catch (JSONException e) {
                                    System.out.println("GIANNIS FAILED JSON "+e);
                                    e.printStackTrace();
                                }
                                splitDetails.put(x);
                            }

                            final String murl = Global.server + "/orders/" + orderId + "/split/";

                            final JSONObject params = new JSONObject();
                            try {
                                params.put("splitType", order.getSplitType());
                                params.put("numberOfSplits", order.getOrderSplit());
                                params.put("splitDetails", splitDetails);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            InternetClass.putRequest(murl, params, Global.getAuthorizationString(Global.loginCredentials));
                        }
                    }
                    updateBasket();
                }

        });
    }


    void makeTreat()
    {
        final boolean isPrintedFlag=order.getHasPrinted();

        ArrayList<Integer> vv = basketAdapter.getIsSelected();
        final ArrayList<BasketItem> items = new ArrayList<BasketItem>();

        for (int i = 0; i < vv.size(); i++) {
            if (vv.get(i) == 1) {
                if (!basketItems.get(i).getIsExtra()) {
                    items.add(basketItems.get(i));
                }
            }
        }
        if (items.size() == 0) {
            for (int i = 1; i < vv.size(); i++) {
                if (!basketItems.get(i).getIsExtra()) {
                    items.add(basketItems.get(i));
                }
            }
        }
        for (int i = 0; i < items.size(); i++) {
            int pos = items.get(i).getCurrentItemPos();
            order.treat(pos);
        }
        if(isPrintedFlag)
            makeAddAndPrint();
        else
        updateBasket();
    }

    void makeDiscount()
    {
        final boolean isPrintedFlag=order.getHasPrinted();
        ArrayList<Integer> vv = basketAdapter.getIsSelected();
        final ArrayList<BasketItem> items = new ArrayList<BasketItem>();

        for (int i = 0; i < vv.size(); i++) {
            if (vv.get(i) == 1) {
                if (!basketItems.get(i).getIsExtra()) {
                    items.add(basketItems.get(i));
                }
            }
        }
        if (items.size() == 0) {
            for (int i = 1; i < vv.size(); i++) {
                if (!basketItems.get(i).getIsExtra()) {
                    items.add(basketItems.get(i));
                }
            }
        }
        DiscountDialog d = new DiscountDialog(BasketActivity.this, items, order);
        d.show();
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DiscountDialog mydialog = (DiscountDialog) dialog;
                if (mydialog.getDiscountPercent() >= 0) {

                    for (int i = 0; i < items.size(); i++) {
                        int pos = items.get(i).getCurrentItemPos();
                        order.putPercentDiscount(pos, mydialog.getDiscountPercent());
                    }

                } else if (mydialog.getDiscountValue() >= 0) {
                    double partialCost =0;//
                    for(int i=0;i<items.size();i++)
                        partialCost+=order.costfOfItemForDiscount(items.get(i).getCurrentItemPos());
                    // order.allOrderCostForDiscount();// order.allOrdersCost(items);
                    double sumDiscount = 0.0;
                    double totalDiscount =
                            Global.DoubleNumber(mydialog.getDiscountValue());
                    if (totalDiscount > partialCost) {
                        Global.showAlert(getString(R.string.impossibleDiscount),BasketActivity.this);
                    } else {
                        for (int i = 0; i < items.size() - 1; i++) {
                            int pos = items.get(i).getCurrentItemPos();
                    //        if (order.isPrinted(pos)) continue;
                            double cost = order.costfOfItemForDiscount(pos);//  order.costOfItem(pos);
                            double value = totalDiscount * cost / partialCost;
                            value = Double.parseDouble(Global.displayDecimal(value));
                            sumDiscount += value;
                            order.putValueDiscount(pos, value);
                        }


                        double remainDiscount = totalDiscount - sumDiscount;
                        order.putValueDiscount(items.get(items.size() - 1).getCurrentItemPos(),
                                remainDiscount);
                    }
                }
                if(isPrintedFlag)
                    makeAddAndPrint();
                else
                    updateBasket();


            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(mainLayout);
        Bundle b=getIntent().getExtras();
        String info=b.getString("order");
        String tt=b.getString("tableJson");
        boolean isTakeout=b.getBoolean("isTakeout");
        try {
            tableJson=new JSONObject(tt);
            orderId=tableJson.getJSONObject("order").getInt("orderId");
            JSONObject json = new JSONObject(info);
            order = new OrderStore();
            if(isTakeout)
                order.enableTakeOut();
            order.setOrderInfo(json);
            order.setOrderId(orderId);
            if(b.getBoolean("partialPaid"))
                order.enablePartialPaid();
            try {
                order.setWaiterId(tableJson.getJSONObject("order").getInt("waiterId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (Exception e)
        {

        }

        InternetClass.ObjectGETCall(this, Global.server + "/orders/" + orderId + "/payments",
                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getJSONArray("orderPayments").length()!=0) {
                                order.enablePartialPaid();
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


        makeViewWindow();
        makeHeader();
        infoRow=new OrderInfoRow(this,mainLayout,hparams,order);
        makeList();
        makeButtons();
        updateBasket();
        infoRow.update();
    }

    @Override
    public void onClickFunction() {
        if (viewWindow == null)
            makeViewWindow();
        ArrayList<Integer> count = basketAdapter.getIsSelected();
        int icount = 0;
        lastSelectedId=-1;
        for (int i = 0; i < count.size(); i++) {
            if (count.get(i) == 1)
            {
                lastSelectedId=basketItems.get(i).getCurrentItemPos();
                icount++;
            }
        }


        if(icount==0 && viewWindow.isShowing())
        {
            viewWindow.dismiss();
            basketParams.height=Global.height-3*Global.headerHeight-2;
            basketList.setLayoutParams(basketParams);
            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                    blparams.bottomMargin-blparams.height-hparams.height;
            buttonLayout.setLayoutParams(blparams);

            return;
        }
        if(icount>0  && !viewWindow.isShowing()) {
            basketParams.height=Global.height-4*Global.headerHeight;
            basketList.setLayoutParams(basketParams);
            blparams.topMargin=Global.height-hparams.height-basketParams.height-
                    blparams.bottomMargin-blparams.height-hparams.height;
            buttonLayout.setLayoutParams(blparams);

            viewWindow.showAtLocation(mainLayout, Gravity.BOTTOM, 0, Global.height - 850 * Global.headerHeight / 100);
        }

        if(icount>=1)
        {
            notesButton.setEnabled(true);
            notesButton.setBackgroundResource(R.drawable.transparentbutton);
        }
        else
        {
            notesButton.setEnabled(false);
            notesButton.setBackgroundResource(R.drawable.disabledbutton);
        }

        System.out.println("GIANNIS TEST "+icount+"  "+lastSelectedId+"  "+order.itemHasExtras(lastSelectedId)+" "+order.itemCanChange(lastSelectedId));

        if(icount==1 && order.itemHasExtras(lastSelectedId)==true  && order.itemCanChange(lastSelectedId))
        {
            extrasButton.setEnabled(true);
            extrasButton.setBackgroundResource(R.drawable.transparentbutton);
        }
        else
        {
            extrasButton.setEnabled(false);
            extrasButton.setBackgroundResource(R.drawable.disabledbutton);
        }
    }

    public void onBackPressed()
    {
        //do nothing
    }
}
