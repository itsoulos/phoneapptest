package ai.datawise.snapserve;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FullOrderWindow extends PopupWindow
{
    Context context=null;
    View view=null;
    int width=0;
    int height=0;
    ImageView closeView,notesView,pagerView,discountView,treatView,cancelOrderView,transferView;
    LinearLayout mainLayout=null;
    OrderStore order=null;

    Runnable runnable=null;
    int requestTries=0;

    boolean shouldFinish=false;

    boolean getFinish()
    {
        return shouldFinish;
    }

    void makeHeader()
    {
        LinearLayout h=new LinearLayout(context);
        h.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(h);
        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)h.getLayoutParams();
        hparams.width=width;
        hparams.height=Global.headerHeight;
        h.setLayoutParams(hparams);
        closeView=new ImageView(context);
        closeView.setImageBitmap(Global.xicon);
        closeView.setClickable(true);
        closeView.setOnTouchListener(Global.touchListener);
        h.addView(closeView);
        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)closeView.getLayoutParams();
        cparams.topMargin=20 * hparams.height/100;
        cparams.width=Global.xicon.getWidth();
        cparams.height=Global.xicon.getHeight();
        cparams.rightMargin=5 * width/100;
        cparams.leftMargin=width-cparams.width-cparams.rightMargin;
        closeView.setLayoutParams(cparams);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    void makeList()
    {
        LinearLayout l1=new LinearLayout(context);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        TextView t1=new TextView(context);
        t1.setTextSize(Global.fontSize);
        t1.setTextColor(Color.parseColor("#de000000"));
        t1.setText(context.getString(R.string.notesText));
        l1.addView(t1);
        int d=8 * Global.getMeasureWidth(context,context.getString(R.string.cancelOrderText),Global.fontSize)/7;

        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=58*width/100;
        t1params.width=20 * width/100;
        if(d>t1params.width)
        {
            int diff=d-t1params.width;
            t1params.leftMargin-=diff;
            t1params.width=d;
        }
        t1params.height=70 * Global.headerHeight/100;
        t1params.topMargin=18 * Global.headerHeight/100;
        t1.setLayoutParams(t1params);
        t1.setGravity(Gravity.RIGHT);

        notesView=new ImageView(context);
        notesView.setImageBitmap(Global.notesIcon);
        l1.addView(notesView);
        LinearLayout.LayoutParams nparams=(LinearLayout.LayoutParams)notesView.getLayoutParams();
        nparams.rightMargin=5 * width/100;
        nparams.width=Global.notesIcon.getWidth();
        nparams.height=Global.notesIcon.getHeight();
        nparams.topMargin=5*Global.headerHeight/100;
        nparams.leftMargin=width-t1params.leftMargin-t1params.width-nparams.width-nparams.rightMargin;
        notesView.setLayoutParams(nparams);
        notesView.setClickable(true);
        notesView.setOnTouchListener(Global.touchListener);
        notesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesDialog d=new NotesDialog(context,order.getNotes());
                d.show();
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String notes = ((NotesDialog) dialog).getNotes();
                        if (notes.length() != 0) {
                            order.setNotes(((NotesDialog) dialog).getNotes());
                            //execute put request
                            final String murl = Global.server + "/orders/" + order.getOrderId();

                            final JSONObject params = new JSONObject();

                            try {
                                params.put("notes", ((NotesDialog) dialog).getNotes());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println("GIANNIS BODY IS " + params);
                            OkHttpClient client = new OkHttpClient();
                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody body = RequestBody.create(JSON, params.toString());
                            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                                    .url(murl)
                                    .put(body)
                                    .addHeader("Authorization", Global.getAuthorizationString(Global.loginCredentials))
                                    .build();

                            Response response = null;
                            try {
                                com.squareup.okhttp.Response response1 = client.newCall(request).execute();
                                String resStr = response1.body().string();
                                System.out.println("GIANNIS RESP IS " + resStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        mainLayout.addView(l1);
        LinearLayout.LayoutParams l1params=(LinearLayout.LayoutParams)l1.getLayoutParams();
        l1params.width=width;
        l1params.height=3*Global.pagerIcon.getHeight()/2;
        l1.setLayoutParams(l1params);

        LinearLayout l2=new LinearLayout(context);
        l2.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(l2);
        l2.setLayoutParams(l1params);

        TextView t2=new TextView(context);
        t2.setTextColor(t1.getCurrentTextColor());
        t2.setTextSize(Global.fontSize);
        t2.setText(context.getString(R.string.addPagerText));
        l2.addView(t2);
        t2.setLayoutParams(t1params);
        t2.setGravity(Gravity.RIGHT);

        pagerView=new ImageView(context);
        pagerView.setImageBitmap(Global.pagerIcon);
        pagerView.setClickable(true);
        pagerView.setOnTouchListener(Global.touchListener);
        l2.addView(pagerView);
        pagerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Global.pagers.length()==0)
                {
                    Global.showAlert(context.getString(R.string.noPagersFoundText),context);
                }
                else {
                    PagerDialog d = new PagerDialog(context);
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            int k = ((PagerDialog) dialog).getPagerId();
                            if (k != -1)
                                order.setPager(k);
                        }
                    });
                }
            }
        });
        pagerView.setLayoutParams(nparams);

        LinearLayout l3=new LinearLayout(context);
        l3.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(l3);
        l3.setLayoutParams(l1params);

        TextView t3=new TextView(context);
        t3.setTextColor(t1.getCurrentTextColor());
        t3.setTextSize(Global.fontSize);
        t3.setText(context.getString(R.string.discountText));
        l3.addView(t3);
        t3.setLayoutParams(t1params);
        t3.setGravity(Gravity.RIGHT);

        discountView=new ImageView(context);
        discountView.setImageBitmap(Global.discountIcon);
        discountView.setClickable(true);
        discountView.setOnTouchListener(Global.touchListener);
        l3.addView(discountView);
        discountView.setLayoutParams(nparams);
        discountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").has("orders.discount")
                            ||
                            Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.discount") == false) {
                        final OrderCloseDialog dialog=new OrderCloseDialog(context,context.getString(R.string.requestAccess),
                                "CANCEL","OK");
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                if(dialog.getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL)
                                {
                                    AuthRequest.makeRequest(context, order.getOrderId(),  Global.TYPE_DISCOUNT_ORDER, new AuthInterface() {
                                        @Override
                                        public void onApproval() {
                                            makeDiscount();
                                        }

                                        @Override
                                        public void onRejected() {
                                            Global.showAlert("Rejected request",context);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        makeDiscount();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        LinearLayout l4=new LinearLayout(context);
        l4.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(l4);
        l4.setLayoutParams(l1params);

        TextView t4=new TextView(context);
        t4.setTextColor(t1.getCurrentTextColor());
        t4.setTextSize(Global.fontSize);
        t4.setText(context.getString(R.string.treatText));
        l4.addView(t4);
        t4.setLayoutParams(t1params);
        t4.setGravity(Gravity.RIGHT);

        treatView=new ImageView(context);
        treatView.setImageBitmap(Global.treatIcon);
        treatView.setClickable(true);
        treatView.setOnTouchListener(Global.touchListener);
        l4.addView(treatView);
        treatView.setLayoutParams(nparams);
        treatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.treat")==false)
                    {
                        final OrderCloseDialog dialog=new OrderCloseDialog(context,context.getString(R.string.requestAccess),
                                "CANCEL","OK");
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                if(dialog.getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL) {
                                    AuthRequest.makeRequest(context, order.getOrderId(),  Global.TYPE_TREAT_ORDER, new AuthInterface() {
                                        @Override
                                        public void onApproval() {
                                            makeTreat();
                                        }

                                        @Override
                                        public void onRejected() {
                                            Global.showAlert("Rejected request",context);
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

        LinearLayout l6=new LinearLayout(context);
        l6.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(l6);
        l6.setLayoutParams(l1params);
        TextView t6=new TextView(context);
        t6.setTextColor(t1.getCurrentTextColor());
        t6.setTextSize(Global.fontSize);
        t6.setText(context.getString(R.string.transferOrderText));
        l6.addView(t6);
        t6.setLayoutParams(t1params);
        t6.setGravity(Gravity.RIGHT);
        transferView=new ImageView(context);
        transferView.setImageBitmap(Global.transfericon);
        transferView.setClickable(true);
        transferView.setOnTouchListener(Global.touchListener);
        l6.addView(transferView);
        transferView.setLayoutParams(nparams);
        transferView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.transfer") == false) {
                        final OrderCloseDialog dialog=new OrderCloseDialog(context,context.getString(R.string.requestAccess),
                                "CANCEL","OK");
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                if(dialog.getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL)
                                {
                                    AuthRequest.makeRequest(context, order.getOrderId(),  Global.TYPE_TRANSFER_ORDER, new AuthInterface() {
                                        @Override
                                        public void onApproval() {
                                            makeTransfer();
                                        }

                                        @Override
                                        public void onRejected() {
                                            Global.showAlert("Rejected request",context);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        makeTransfer();
                    }
                } catch (JSONException e) {
                    Global.showAlert(context.getString(R.string.executionProblemText) + "Transfer", context);
                    e.printStackTrace();
                }

            }
        });

        LinearLayout l5=new LinearLayout(context);
        l5.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(l5);
        l5.setLayoutParams(l1params);

        TextView t5=new TextView(context);
        t5.setTextColor(t1.getCurrentTextColor());
        t5.setTextSize(Global.fontSize);
        t5.setText(context.getString(R.string.cancelOrderText));
        l5.addView(t5);
        t5.setLayoutParams(t1params);
        t5.setGravity(Gravity.RIGHT);

        cancelOrderView=new ImageView(context);
        cancelOrderView.setImageBitmap(Global.cancelOrderIcon);
        cancelOrderView.setClickable(true);
        cancelOrderView.setOnTouchListener(Global.touchListener);
        l5.addView(cancelOrderView);
        cancelOrderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.cancel")==false)
                    {
                        final OrderCloseDialog dialog=new OrderCloseDialog(context,context.getString(R.string.requestAccess),
                                "CANCEL","OK");
                        dialog.show();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface d) {
                                if(dialog.getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL) {
                                    AuthRequest.makeRequest(context, order.getOrderId(),  Global.TYPE_CANCEL_ORDER, new AuthInterface() {
                                        @Override
                                        public void onApproval() {
                                            makeCancelOrder();
                                        }

                                        @Override
                                        public void onRejected() {
                                            Global.showAlert("Rejected request",context);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else
                    {
                        makeCancelOrder();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        cancelOrderView.setLayoutParams(nparams);

    }

    public void setOrder(OrderStore o)
    {
        order=o;
    }


    void makeAddAndPrint()
    {
        JSONObject orderInfo = order.getOrderInfo();
        try {
            InternetClass.putRequest(Global.server+"/orders/"+order.getOrderId()+"/details",orderInfo.getJSONArray("details"),
                    Global.getAuthorizationString(Global.loginCredentials)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void makeTransfer()
    {

        if (order.getHasPrinted() == false) {
            TransferWarningDialog d = new TransferWarningDialog(context);
            d.show();
        } else {

            if(order.getPartialPaid())
            {
                TransferWarningDialog d = new TransferWarningDialog(context,context.getString(R.string.partialPaidText));
                d.show();
            }
            else {
                TransferClass transferClass = new TransferClass(context);
                transferClass.show();
                transferClass.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface dialog) {
                        TransferClass d = (TransferClass) dialog;
                        int levelId = d.getSelectedLevelId();
                        String tableCode = d.getSelectedTableCode();
                        if (levelId != -1) {
                            final JSONObject transfer = new JSONObject();
                            try {
                                transfer.put("tableCode", tableCode);
                                transfer.put("persons", -1);
                                JSONArray ids = new JSONArray();


                                transfer.put("orderDetailIds", ids);
                                InternetClass.ObjectPOSTCall(context, Global.server + "/orders/" + order.getOrderId() + "/transfer",
                                        transfer, Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                if (order.orderCount() != 0)
                                                    order.removeAll();

                                                shouldFinish = true;
                                                dismiss();
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Global.showAlert(context.getString(R.string.executionProblemText) + "Transfer", context);
                                            }
                                        });

                            } catch (JSONException e) {
                                Global.showAlert(context.getString(R.string.executionProblemText) + "Transfer", context);
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        }
    }
    void makeCancelOrder()
    {
        /* removed for now 15/1/2020**/
        /*if(order.orderCount()!=0)
        {
            Global.showAlert("Order is not empty",context);
        }
        else*/
            {
            InternetClass.StringPostCall(context, Global.server + "/orders/" + order.getOrderId() + "/cancel",
                    Global.getAuthorizationString(Global.loginCredentials), new StringListener() {
                        @Override
                        public void onResponse(String response) {
                            shouldFinish=true;
                            dismiss();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Global.showAlert("Failed to cancel order " + error, context);
                        }
                    });

        }
    }
    void makeTreat()
    {
        final boolean isPrintedFlag=order.getHasPrinted();

        ArrayList<BasketItem> copyitems = order.getBasketItems();
        copyitems.remove(0);
        final ArrayList<BasketItem> items = new ArrayList<BasketItem>();

        for (int i = 0; i < copyitems.size(); i++) {
            if (copyitems.get(i).getIsExtra()) continue;
            items.add(copyitems.get(i));
        }

        for (int i = 0; i < items.size(); i++) {
            int pos = items.get(i).getCurrentItemPos();
            order.treat(pos);
        }
        Global.showAlert(context.getString(R.string.treatMessage),context);
       if(isPrintedFlag) makeAddAndPrint();
    }

    void makeDiscount()
    {
        final boolean isPrintedFlag=order.getHasPrinted();

        ArrayList<BasketItem> copyitems = order.getBasketItems();
        copyitems.remove(0);
        final ArrayList<BasketItem> items = new ArrayList<BasketItem>();
        for (int i = 0; i < copyitems.size(); i++) {
            if (copyitems.get(i).getIsExtra()) continue;
            items.add(copyitems.get(i));
        }
        if (items.size() == 0) return;
        DiscountDialog d = new DiscountDialog(context, items, order);
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


                    double partialCost =0; // order.allOrdersCost(items);
                    for(int i=0;i<items.size();i++)
                        partialCost+=order.costfOfItemForDiscount(items.get(i).getCurrentItemPos());
                    double sumDiscount = 0.0;
                    double totalDiscount = Global.DoubleNumber(mydialog.getDiscountValue());
                    if (totalDiscount > partialCost) {
                        Global.showAlert(context.getString(R.string.impossibleDiscount),context);
                    } else {
                        for (int i = 0; i < items.size() - 1; i++) {
                            int pos = items.get(i).getCurrentItemPos();
                            //if (order.isPrinted(pos)) continue;
                            double cost = order.costOfItem(pos);
                            double value = totalDiscount * cost / partialCost;
                            sumDiscount += value;
                            order.putValueDiscount(pos, value);
                        }


                        double remainDiscount = totalDiscount - sumDiscount;
                        order.putValueDiscount(items.get(items.size() - 1).getCurrentItemPos(),
                                remainDiscount);
                    }
                }
                if(isPrintedFlag) makeAddAndPrint();
            }
        });
    }

    public FullOrderWindow(Context ctx,View v, int w, int h,OrderStore o)
    {
        super(v,w,h);
        context=ctx;
        view=v;
        width=w;
        height=h;
        order=o;
        mainLayout=new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(mainLayout);
        mainLayout.setBackgroundColor(Color.parseColor("#E8ffffff"));
        makeHeader();
        makeList();
    }
}
