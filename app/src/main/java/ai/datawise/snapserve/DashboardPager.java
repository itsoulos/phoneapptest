package ai.datawise.snapserve;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SymbolTable;
import android.provider.ContactsContract;
import android.renderscript.ScriptIntrinsicYuvToRGB;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class DashboardPager extends PagerAdapter {
    private Context context;
    private int pageWidth = 0, pageHeight = 0;
    private JSONArray shifts = null;

    TextView totalOrders, totalValue, totalTreats, openOrders, cachIn, cashOut, checkIn, checkOut;
    TextView displayNameText = null;
    TextView displayWordkTimeText = null;
    TextView shiftPayText = null;
    TextView totalBreakText = null;
    ListView openOrdersList = null;
    TextView canceledOrders = null;
    TextView cashAmount = null;
    TextView creditAmount = null;
    TextView discountAmount = null;
    TextView vatAmount = null;
    ListView waitersList=null;

    boolean upState = false;

    public DashboardPager(Context ctx, int pw, int ph) {
        context = ctx;
        pageWidth = pw;
        pageHeight = ph;
    }

    void getWorks(int userid) {
        InternetClass.ArrayGETCall(context, Global.server + "/users/" + userid + "/shifts/all",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        shifts = response;
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert("Problem in Shifts",context);
                    }
                });
    }

    String mdate;
    OpenedOrderAdapter adapter=null;

    void getPendingOrders()
    {

        InternetClass.ArrayGETCall(context, Global.server + "/orders/pending",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        adapter=new OpenedOrderAdapter(context,response,true,listener);
                        openOrdersList.setAdapter(adapter);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("GIANNIS ERROR IN PENDING "+error);
                    }
                });
    }



    void getOverviewManager(final int userid) throws JSONException {
        InternetClass.ObjectGETCall(context, Global.server +
                        "/users/" + userid + "/overview",
                Global.getAuthorizationString(Global.loginCredentials),
                new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Global.loginCredentials.put("checkedIn",response.getBoolean("isCheckedIn"));
                            Global.loginCredentials.put("inBreak",response.getBoolean("isInBreak"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mdate=Global.workdate;

                        InternetClass.ObjectPOSTCall(context, Global.server + "/reports/metrics?workdate=" + mdate,
                                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            totalOrders.setText("" + response.getInt("ordersTotalCount"));
                                            canceledOrders.setText("" + response.getInt("ordersCancelCount"));
                                            totalValue.setText(Global.currency + Global.displayDecimal(response.getDouble("incomeTotalAmount")));
                                            vatAmount.setText(Global.currency + Global.displayDecimal(response.getDouble("vatAmount")));
                                            discountAmount.setText(Global.currency + Global.displayDecimal(response.getDouble("discountAmount")));
                                            totalTreats.setText("" + response.getInt("treatsAmount"));
                                            cashAmount.setText(Global.currency + Global.displayDecimal(response.getDouble("cashAmount")));
                                            creditAmount.setText(Global.currency + Global.displayDecimal(response.getDouble("creditAmount")));
                                            getPendingOrders();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Global.showAlert("Execution problem on metrics", context);
                                    }
                                });
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert("Execution problem on metrics", context);

                    }
                });

    }

    AdapterView.OnItemClickListener listener=null;

    void getOverview(final int userid) throws JSONException
    {
        final NumberFormat formatter = new DecimalFormat("#0.00");
        JsonObjectRequest jsonObejct = new JsonObjectRequest(Request.Method.GET, Global.server +
                "/users/"+userid+"/overview",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("GIANNIS BEFORE works");
                getWorks(userid);
                try {
                    System.out.println("GIANNIS AFTER WORKS");
                    System.out.println("GIANNIS RESPONSE Chec "+response);
                    Global.loginCredentials.put("checkedIn",response.getBoolean("isCheckedIn"));
                    System.out.println("GIANNIS PASS 0");
                    Global.loginCredentials.put("inBreak",response.getBoolean("isInBreak"));
                    JSONObject shift=null;
                    System.out.println("GIANNIS PASS 1");
                    if(response.get("shiftMetrics")!=JSONObject.NULL)
                        shift=response.getJSONObject("shiftMetrics");
                    int shiftDuration=(shift!=null)?shift.getInt("shiftDuration"):0;
                    int hours=shiftDuration/60;
                    int minutes=shiftDuration==0?0:shiftDuration%60;
                    displayWordkTimeText.setText(hours+"h and "+minutes+"min.");
                    JSONArray  orders=response.getJSONArray("openOrders");
                    displayNameText.setText(response.getString("userDisplayName"));
                    totalTreats.setText(Global.currency+((shift!=null)?
                            Global.displayDecimal(shift.getDouble("offerAmount")):0));
                    totalOrders.setText(""+((shift!=null)?shift.getInt("orderCount"):0));
                    totalValue.setText(Global.currency+Global.displayDecimal((shift!=null?shift.getDouble("totalAmount"):0)));
                    openOrders.setText(""+orders.length());
                    cachIn.setText(Global.currency+Global.displayDecimal((shift!=null?shift.getDouble("checkInCash"):0)));
                    cashOut.setText(Global.currency+Global.displayDecimal((shift!=null?shift.getDouble("checkOutCash"):0)));
                    String checkInTime=(shift!=null)?shift.getString("checkInAt"):null;
                    checkIn.setText(checkInTime==null?"":Global.getHourAndMinute(checkInTime));
                    totalBreakText.setText(""+(shift!=null?shift.getInt("totalBreaksDuration"):0));


                    adapter=new OpenedOrderAdapter(context,orders,false,listener);
                    openOrdersList.setAdapter(adapter);


                } catch (JSONException e) {
                    Global.showAlert(context.getString(R.string.executionProblemText)+" 5)Shifts",context);
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Global.showAlert(context.getString(R.string.executionProblemText)+" 1)Shifts",context);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", Global.getAuthorizationString(Global.loginCredentials));
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObejct);

    }

    LinearLayout makeOverViewManager()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        final TableLayout table=new TableLayout(context);
        l.addView(table);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)table.getLayoutParams();
        tparams.width=pageWidth;
        tparams.height=55*pageHeight/100;
        table.setLayoutParams(tparams);

        TableRow r1=new TableRow(context);
        table.addView(r1);

        TableLayout.LayoutParams r1params=(TableLayout.LayoutParams)r1.getLayoutParams();
        r1params.leftMargin=3 * tparams.width/100;
        r1params.topMargin=tparams.height/100;
        r1params.width=96 * tparams.width/100;
        r1params.height=20 * tparams.height/100;
        r1.setLayoutParams(r1params);


        LinearLayout r11=new LinearLayout(context);
        r11.setOrientation(LinearLayout.VERTICAL);
        r11.setBackgroundResource(R.drawable.transparentbutton);

        r1.addView(r11);
        TableRow.LayoutParams r11params=(TableRow.LayoutParams)r11.getLayoutParams();
        r11params.topMargin=2 * r1params.height/100;
        r11params.leftMargin=3 * r1params.width/100;
        r11params.width=44 * r1params.width/100;
        r11params.height=98 * r1params.height/100;
        r11.setLayoutParams(r11params);
        LinearLayout r11_header=new LinearLayout(context);
        r11_header.setOrientation(LinearLayout.HORIZONTAL);
        r11.addView(r11_header);
        ImageView im1=new ImageView(context);
        im1.setImageBitmap(Global.totalorderscircularicon);
        r11_header.addView(im1);
        TextView t1=new TextView(context);
        t1.setTextSize(Global.fontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setText(context.getString(R.string.totalOrdersText));
        t1.setGravity(Gravity.CENTER);
        r11_header.addView(t1);
        LinearLayout.LayoutParams i1params=(LinearLayout.LayoutParams)im1.getLayoutParams();
        i1params.leftMargin=5 * r11params.width/100;
        i1params.width=Global.userIcon.getWidth();
        i1params.topMargin=3 * r11params.height/100;
        i1params.height=150 * Global.getMeasuredHeight(context,t1.getText().toString(),Global.fontSize)/100;
        if(i1params.height<120*Global.userIcon.getHeight()/100) i1params.height=120*Global.userIcon.getHeight()/100;
        i1params.topMargin=5 * r11params.height/100;
        i1params.gravity=Gravity.CENTER;
        im1.setLayoutParams(i1params);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=i1params.leftMargin/2;
        t1params.topMargin=i1params.topMargin;
        t1params.rightMargin=i1params.leftMargin;
        t1params.height=i1params.height;
        t1params.gravity=Gravity.CENTER;
        t1.setLayoutParams(t1params);

        totalOrders=new TextView(context);
        totalOrders.setText("Orders");
        totalOrders.setTextColor(Color.parseColor(Global.textColor));
        totalOrders.setTextSize(Global.fontSize);
        totalOrders.setTypeface(Typeface.DEFAULT_BOLD);
        r11.addView(totalOrders);
        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)totalOrders.getLayoutParams();
        t2params.width=90 *r11params.width/100;
        t2params.topMargin=t1params.topMargin;
        t2params.height=r11params.height-t1params.height-t2params.topMargin;
        t2params.leftMargin=5 * r11params.width/100;
        t2params.gravity=Gravity.CENTER;
        totalOrders.setLayoutParams(t2params);

        LinearLayout r12=new LinearLayout(context);
        r12.setOrientation(LinearLayout.VERTICAL);
        r1.addView(r12);
        r12.setBackgroundResource(R.drawable.transparentbutton);
        r12.setLayoutParams(r11params);
        LinearLayout r12_header=new LinearLayout(context);
        r12_header.setOrientation(LinearLayout.HORIZONTAL);
        r12.addView(r12_header);
        ImageView im2=new ImageView(context);
        im2.setImageBitmap(Global.cancelcircularicon);
        r12_header.addView(im2);
        im2.setLayoutParams(i1params);
        TextView t2=new TextView(context);
        t2.setTextSize(Global.fontSize);
        t2.setTextColor(Color.parseColor(Global.textColor));
        t2.setText(context.getString(R.string.canceledOrdersText));
        t2.setGravity(Gravity.CENTER);
        r12_header.addView(t2);
        t2.setLayoutParams(t1params);
        canceledOrders=new TextView(context);
        canceledOrders.setText(context.getString(R.string.canceledOrdersText));
        canceledOrders.setTextColor(Color.parseColor(Global.textColor));
        canceledOrders.setTextSize(Global.fontSize);
        canceledOrders.setTypeface(Typeface.DEFAULT_BOLD);
        r12.addView(canceledOrders);
        canceledOrders.setLayoutParams(t2params);


        TableRow r2=new TableRow(context);
        table.addView(r2);
        r2.setLayoutParams(r1params);
        LinearLayout r21,r22;

        r21=new LinearLayout(context);
        r21.setOrientation(LinearLayout.VERTICAL);
        r2.addView(r21);
        LinearLayout r21_header=new LinearLayout(context);
        r21_header.setOrientation(LinearLayout.HORIZONTAL);
        r21.addView(r21_header);
        ImageView im3=new ImageView(context);
        im3.setImageBitmap(Global.moneycircularicon);
        r21_header.addView(im3);
        im3.setLayoutParams(i1params);
        TextView t3=new TextView(context);
        t3.setTextSize(Global.fontSize);
        t3.setTextColor(Color.parseColor(Global.textColor));
        t3.setText(context.getString(R.string.totalIncomeText));
        t3.setGravity(Gravity.CENTER);
        r21_header.addView(t3);
        t3.setLayoutParams(t1params);
        totalValue=new TextView(context);
        totalValue.setText(context.getString(R.string.totalIncomeText));
        totalValue.setTextColor(Color.parseColor(Global.textColor));
        totalValue.setTextSize(Global.fontSize);
        totalValue.setTypeface(Typeface.DEFAULT_BOLD);
        r21.addView(totalValue);
        totalValue.setLayoutParams(t2params);
        r21.setBackgroundResource(R.drawable.transparentbutton);
        r21.setLayoutParams(r11params);



        r22=new LinearLayout(context);
        r22.setOrientation(LinearLayout.VERTICAL);
        r2.addView(r22);
        LinearLayout r22_header=new LinearLayout(context);
        r22_header.setOrientation(LinearLayout.HORIZONTAL);
        r22.addView(r22_header);
        ImageView im4=new ImageView(context);
        im4.setImageBitmap(Global.treatscircularicon);
        r22_header.addView(im4);
        im4.setLayoutParams(i1params);
        TextView t4=new TextView(context);
        t4.setTextSize(Global.fontSize);
        t4.setTextColor(Color.parseColor(Global.textColor));
        t4.setText(context.getString(R.string.totalTreatsText));
        t4.setGravity(Gravity.CENTER);
        r22_header.addView(t4);
        t4.setLayoutParams(t1params);
        totalTreats=new TextView(context);
        totalTreats.setText(context.getString(R.string.totalTreatsText));
        totalTreats.setTextColor(Color.parseColor(Global.textColor));
        totalTreats.setTextSize(Global.fontSize);
        totalTreats.setTypeface(Typeface.DEFAULT_BOLD);
        r22.addView(totalTreats);
        totalTreats.setLayoutParams(t2params);
        r22.setBackgroundResource(R.drawable.transparentbutton);
        r22.setLayoutParams(r11params);

        final TableRow r3=new TableRow(context);
        table.addView(r3);
        r3.setLayoutParams(r1params);
        LinearLayout r31,r32;

        r31=new LinearLayout(context);
        r31.setOrientation(LinearLayout.VERTICAL);
        r3.addView(r31);
        LinearLayout r31_header=new LinearLayout(context);
        r31_header.setOrientation(LinearLayout.HORIZONTAL);
        r31.addView(r31_header);
        ImageView im5=new ImageView(context);
        im5.setImageBitmap(Global.moneycircularicon);
        r31_header.addView(im5);
        im5.setLayoutParams(i1params);
        TextView t5=new TextView(context);
        t5.setTextSize(Global.fontSize);
        t5.setTextColor(Color.parseColor(Global.textColor));
        t5.setText(context.getString(R.string.cashAmountText));
        t5.setGravity(Gravity.CENTER);
        r31_header.addView(t5);
        t5.setLayoutParams(t1params);
        cashAmount=new TextView(context);
        cashAmount.setText(context.getString(R.string.cashAmountText));
        cashAmount.setTextColor(Color.parseColor(Global.textColor));
        cashAmount.setTextSize(Global.fontSize);
        cashAmount.setTypeface(Typeface.DEFAULT_BOLD);
        r31.addView(cashAmount);
        cashAmount.setLayoutParams(t2params);
        r31.setBackgroundResource(R.drawable.transparentbutton);
        r31.setLayoutParams(r11params);
        r32=new LinearLayout(context);
        r32.setOrientation(LinearLayout.VERTICAL);
        r3.addView(r32);
        LinearLayout r32_header=new LinearLayout(context);
        r32_header.setOrientation(LinearLayout.HORIZONTAL);
        r32.addView(r32_header);
        ImageView im6=new ImageView(context);
        im6.setImageBitmap(Global.moneycircularicon);
        r32_header.addView(im6);
        im6.setLayoutParams(i1params);
        TextView t6=new TextView(context);
        t6.setTextSize(Global.fontSize);
        t6.setTextColor(Color.parseColor(Global.textColor));
        t6.setText(context.getString(R.string.creditAmountText));
        t6.setGravity(Gravity.CENTER);
        r32_header.addView(t6);
        t6.setLayoutParams(t1params);
        creditAmount=new TextView(context);
        creditAmount.setText(context.getString(R.string.creditAmountText));
        creditAmount.setTextColor(Color.parseColor(Global.textColor));
        creditAmount.setTextSize(Global.fontSize);
        creditAmount.setTypeface(Typeface.DEFAULT_BOLD);
        r32.addView(creditAmount);
        creditAmount.setLayoutParams(t2params);
        r32.setBackgroundResource(R.drawable.transparentbutton);
        r32.setLayoutParams(r11params);


        final TableRow r4=new TableRow(context);
        table.addView(r4);
        r4.setLayoutParams(r1params);
        LinearLayout r41,r42;

        r41=new LinearLayout(context);
        r41.setOrientation(LinearLayout.VERTICAL);
        r4.addView(r41);
        LinearLayout r41_header=new LinearLayout(context);
        r41_header.setOrientation(LinearLayout.HORIZONTAL);
        r41.addView(r41_header);
        ImageView im7=new ImageView(context);
        im7.setImageBitmap(Global.moneycircularicon);
        r41_header.addView(im7);
        im7.setLayoutParams(i1params);
        TextView t7=new TextView(context);
        t7.setTextSize(Global.fontSize);
        t7.setTextColor(Color.parseColor(Global.textColor));
        t7.setText(context.getString(R.string.discountAmountText));
        t7.setGravity(Gravity.CENTER);
        r41_header.addView(t7);
        t7.setLayoutParams(t1params);
        discountAmount=new TextView(context);
        discountAmount.setText(context.getString(R.string.discountAmountText));
        discountAmount.setTextColor(Color.parseColor(Global.textColor));
        discountAmount.setTextSize(Global.fontSize);
        discountAmount.setTypeface(Typeface.DEFAULT_BOLD);
        r41.addView(discountAmount);
        discountAmount.setLayoutParams(t2params);
        r41.setBackgroundResource(R.drawable.transparentbutton);
        r41.setLayoutParams(r11params);

        r42=new LinearLayout(context);
        r42.setOrientation(LinearLayout.VERTICAL);
        r4.addView(r42);
        LinearLayout r42_header=new LinearLayout(context);
        r42_header.setOrientation(LinearLayout.HORIZONTAL);
        r42.addView(r42_header);
        ImageView im8=new ImageView(context);
        im8.setImageBitmap(Global.moneycircularicon);
        r42_header.addView(im8);
        im8.setLayoutParams(i1params);
        TextView t8=new TextView(context);
        t8.setTextSize(Global.fontSize);
        t8.setTextColor(Color.parseColor(Global.textColor));
        t8.setText(context.getString(R.string.vatAmountText));
        t8.setGravity(Gravity.CENTER);
        r42_header.addView(t8);
        t8.setLayoutParams(t1params);
        vatAmount=new TextView(context);
        vatAmount.setText(context.getString(R.string.vatAmountText));
        vatAmount.setTextColor(Color.parseColor(Global.textColor));
        vatAmount.setTextSize(Global.fontSize);
        vatAmount.setTypeface(Typeface.DEFAULT_BOLD);
        r42.addView(vatAmount);
        vatAmount.setLayoutParams(t2params);
        r42.setBackgroundResource(R.drawable.transparentbutton);
        r42.setLayoutParams(r11params);


        LinearLayout ll1=new LinearLayout(context);
        ll1.setOrientation(LinearLayout.HORIZONTAL);
        l.addView(ll1);
        TextView cancelText=new TextView(context);
        cancelText.setTextColor(Color.parseColor(Global.textColor));
        cancelText.setTextSize(Global.fontSize);
        cancelText.setText(context.getString(R.string.cancelAllOrdersText));
        ll1.addView(cancelText);
        LinearLayout.LayoutParams ccparams=(LinearLayout.LayoutParams)cancelText.getLayoutParams();
        ccparams.leftMargin=35 * Global.width/100;
        ccparams.gravity=Gravity.CENTER_VERTICAL;
        ccparams.width=8 * Global.getMeasureWidth(context,cancelText.getText().toString(),Global.fontSize)/7;
        ccparams.height= 10 * Global.getMeasuredHeight(context,cancelText.getText().toString(),Global.fontSize)/7;
        cancelText.setLayoutParams(ccparams);

        ImageButton cancelButton=new ImageButton(context);
        cancelButton.setBackgroundResource(R.drawable.transparentbutton);
        cancelButton.setImageBitmap(Global.cancelcircularicon);
        ll1.addView(cancelButton);
        LinearLayout.LayoutParams ciparams=(LinearLayout.LayoutParams)cancelButton.getLayoutParams();
        ciparams.height=200 * Global.cancelcircularicon.getHeight()/100;
        ciparams.width=200 * Global.cancelcircularicon.getWidth()/100;
        ciparams.gravity=Gravity.CENTER_VERTICAL;
        cancelButton.setLayoutParams(ciparams);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OrderCloseDialog dialog=new OrderCloseDialog(context,context.getString(R.string.cancelAllOrdersText)+"?",
                        "CANCEL","OK");
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(((OrderCloseDialog)dialog).getCloseFlag()==OrderCloseDialog.CLOSE_CANCEL)
                        {
                            cancelAllOrders();
                        }
                    }
                });
            }
        });

        final ImageButton roundButton=new ImageButton(context);
        roundButton.setBackgroundResource(R.drawable.whiteroundbutton);
        l.addView(roundButton);
        roundButton.setImageBitmap(Global.upArrow);
        LinearLayout.LayoutParams rparams=(LinearLayout.LayoutParams)roundButton.getLayoutParams();
        rparams.width=2 * Global.downArrow.getWidth();
        rparams.height=2 * Global.downArrow.getHeight();
        rparams.topMargin=2 * pageHeight/100;
        rparams.leftMargin=(pageWidth-rparams.width)/2;
        roundButton.setLayoutParams(rparams);
        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upState==false)
                {
                    r3.setVisibility(View.GONE);
                    r4.setVisibility(View.GONE);
                    LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)table.getLayoutParams();
                    tparams.height=27 *pageHeight/100;
                    LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)openOrdersList.getLayoutParams();
                    oparams.height=50 * pageHeight/100;
                    roundButton.setImageBitmap(Global.downArrow);
                }
                else
                {
                    r3.setVisibility(View.VISIBLE);
                    r4.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)table.getLayoutParams();
                    LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)openOrdersList.getLayoutParams();
                    oparams.height=25 * pageHeight/100;
                    tparams.height=55 * pageHeight/100;
                    roundButton.setImageBitmap(Global.upArrow);
                }
                upState=!upState;
            }
        });
        TextView openOrdersLabel=new TextView(context);
        openOrdersLabel.setTextSize(Global.fontSize);
        openOrdersLabel.setTextColor(Color.parseColor("#80000000"));
        openOrdersLabel.setText(context.getString(R.string.openOrdersText));
        openOrdersLabel.setGravity(Gravity.CENTER);
        l.addView(openOrdersLabel);
        LinearLayout.LayoutParams ooparams=(LinearLayout.LayoutParams)openOrdersLabel.getLayoutParams();
        ooparams.topMargin=2 * pageHeight/100;
        openOrdersLabel.setLayoutParams(ooparams);

        openOrdersList=new ListView(context);
        l.addView(openOrdersList);
        LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)openOrdersList.getLayoutParams();
        oparams.width=pageWidth;
        oparams.height=25 * pageHeight/100;
        openOrdersList.setLayoutParams(oparams);

        try {
            getOverviewManager(Global.loginCredentials.getJSONObject("user").getInt("id"));
        } catch (JSONException e) {
            Global.showAlert(context.getString(R.string.executionProblemText)+" 6)Shifts",context);

            e.printStackTrace();
        }

        return l;
    }

    int remainOrders=0;

    void cancelOrder(int id)
    {
        InternetClass.StringPostCall(context, Global.server + "/orders/" + id + "/cancel",
                Global.getAuthorizationString(Global.loginCredentials), new StringListener() {
                    @Override
                    public void onResponse(String response) {
                        remainOrders--;
                        if(remainOrders<=0)
                            getPendingOrders();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert("Failed to cancel order " + error, context);
                    }
                });
    }
    void cancelAllOrders()
    {
        InternetClass.ArrayGETCall(context, Global.server + "/orders/pending",
                Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        remainOrders=response.length();
                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                cancelOrder(response.getJSONObject(i).getInt("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert("ERROR IN GET ORDERS ",context);
                    }
                });
    }
    LinearLayout makeWaiters()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        waitersList=new ListView(context);
        waitersList.setSelector(new ColorDrawable(0));
        l.addView(waitersList);
        LinearLayout.LayoutParams wparams=(LinearLayout.LayoutParams)waitersList.getLayoutParams();
        wparams.width=pageWidth;
        wparams.height=pageHeight;
        waitersList.setLayoutParams(wparams);

        InternetClass.ArrayGETCall(context, Global.server + "/users/activity", Global.getAuthorizationString(Global.loginCredentials),
                new JSONArrayListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        WaitersAdapter adapter=new WaitersAdapter(context,response);
                        waitersList.setAdapter(adapter);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert("ERROR IN ACTIVITY",context);
                    }
                });
        return l;
    }

    LinearLayout makeOverview()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        final TableLayout table=new TableLayout(context);
        l.addView(table);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)table.getLayoutParams();
        tparams.width=pageWidth;
        tparams.height=75*pageHeight/100;
        table.setLayoutParams(tparams);

        TableRow r1=new TableRow(context);
        table.addView(r1);

        TableLayout.LayoutParams r1params=(TableLayout.LayoutParams)r1.getLayoutParams();
        r1params.leftMargin=3 * tparams.width/100;
        r1params.topMargin=tparams.height/100;
        r1params.width=96 * tparams.width/100;
        r1params.height=15 * tparams.height/100;
        r1.setLayoutParams(r1params);


        LinearLayout r11=new LinearLayout(context);
        r11.setOrientation(LinearLayout.VERTICAL);
        r11.setBackgroundResource(R.drawable.transparentbutton);

        r1.addView(r11);
        TableRow.LayoutParams r11params=(TableRow.LayoutParams)r11.getLayoutParams();
        r11params.topMargin=2 * r1params.height/100;
        r11params.leftMargin=3 * r1params.width/100;
        r11params.width=44 * r1params.width/100;
        r11params.height=98 * r1params.height/100;
        r11.setLayoutParams(r11params);

        LinearLayout r11_header=new LinearLayout(context);
        r11_header.setOrientation(LinearLayout.HORIZONTAL);
        r11.addView(r11_header);
        ImageView im1=new ImageView(context);
        im1.setImageBitmap(Global.usercircularicon);
        r11_header.addView(im1);
        TextView t1=new TextView(context);
        t1.setTextSize(Global.fontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setGravity(Gravity.CENTER);
        t1.setText(context.getString(R.string.displayNameText));
        r11_header.addView(t1);
        LinearLayout.LayoutParams i1params=(LinearLayout.LayoutParams)im1.getLayoutParams();
        i1params.leftMargin=5 * r11params.width/100;
        i1params.width=Global.userIcon.getWidth();
        i1params.topMargin=3 * r11params.height/100;
        i1params.height=150 * Global.getMeasuredHeight(context,t1.getText().toString(),Global.fontSize)/100;
        if(i1params.height<120*Global.userIcon.getHeight()/100) i1params.height=120*Global.userIcon.getHeight()/100;
        i1params.topMargin=5 * r11params.height/100;
        i1params.gravity=Gravity.CENTER;
        im1.setLayoutParams(i1params);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=i1params.leftMargin/2;
        t1params.topMargin=i1params.topMargin;
        t1params.rightMargin=i1params.leftMargin;
        t1params.height=i1params.height;
        t1params.gravity=Gravity.CENTER;
        t1.setLayoutParams(t1params);

        displayNameText=new TextView(context);
        displayNameText.setText("      ");
        displayNameText.setTextColor(Color.parseColor(Global.textColor));
        displayNameText.setTextSize(Global.fontSize);
        displayNameText.setTypeface(Typeface.DEFAULT_BOLD);
        r11.addView(displayNameText);
        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)displayNameText.getLayoutParams();
        t2params.width=90 *r11params.width/100;
        t2params.topMargin=t1params.topMargin/2;
        t2params.height=r11params.height-t1params.height-t2params.topMargin;
        t2params.leftMargin=5 * r11params.width/100;
        t2params.gravity=Gravity.CENTER;
        displayNameText.setLayoutParams(t2params);


        LinearLayout r12=new LinearLayout(context);
        r12.setOrientation(LinearLayout.VERTICAL);
        r1.addView(r12);
        r12.setBackgroundResource(R.drawable.transparentbutton);
        r12.setLayoutParams(r11params);

        LinearLayout r12_header=new LinearLayout(context);
        r12_header.setOrientation(LinearLayout.HORIZONTAL);
        r12.addView(r12_header);
        ImageView im2=new ImageView(context);
        im2.setImageBitmap(Global.clockcircularicon);
        r12_header.addView(im2);
        im2.setLayoutParams(i1params);
        TextView t2=new TextView(context);
        t2.setTextSize(Global.fontSize);
        t2.setTextColor(Color.parseColor(Global.textColor));
        t2.setText(context.getString(R.string.totalWorkTimeText));
        t2.setGravity(Gravity.CENTER);
        r12_header.addView(t2);
        t2.setLayoutParams(t1params);
        displayWordkTimeText=new TextView(context);
        displayWordkTimeText.setText("Work time in hrs");
        displayWordkTimeText.setTextColor(Color.parseColor(Global.textColor));
        displayWordkTimeText.setTextSize(Global.fontSize);
        displayWordkTimeText.setTypeface(Typeface.DEFAULT_BOLD);
        r12.addView(displayWordkTimeText);
        displayWordkTimeText.setLayoutParams(t2params);



        TableRow r2=new TableRow(context);
        table.addView(r2);
        r2.setLayoutParams(r1params);
        LinearLayout r21,r22;

        r21=new LinearLayout(context);
        r21.setOrientation(LinearLayout.VERTICAL);
        r2.addView(r21);
        LinearLayout r21_header=new LinearLayout(context);
        r21_header.setOrientation(LinearLayout.HORIZONTAL);
        r21.addView(r21_header);
        ImageView im3=new ImageView(context);
        im3.setImageBitmap(Global.moneycircularicon);
        r21_header.addView(im3);
        im3.setLayoutParams(i1params);
        TextView t3=new TextView(context);
        t3.setTextSize(Global.fontSize);
        t3.setTextColor(Color.parseColor(Global.textColor));
        t3.setText(context.getString(R.string.shiftPayText));
        t3.setGravity(Gravity.CENTER);
        r21_header.addView(t3);
        t3.setLayoutParams(t1params);
        shiftPayText=new TextView(context);
        shiftPayText.setText("Shift Pay");
        shiftPayText.setTextColor(Color.parseColor(Global.textColor));
        shiftPayText.setTextSize(Global.fontSize);
        shiftPayText.setTypeface(Typeface.DEFAULT_BOLD);
        r21.addView(shiftPayText);
        shiftPayText.setLayoutParams(t2params);
        r21.setBackgroundResource(R.drawable.transparentbutton);
        r21.setLayoutParams(r11params);



        r22=new LinearLayout(context);
        r22.setOrientation(LinearLayout.VERTICAL);
        r2.addView(r22);
        LinearLayout r22_header=new LinearLayout(context);
        r22_header.setOrientation(LinearLayout.HORIZONTAL);
        r22.addView(r22_header);
        ImageView im4=new ImageView(context);
        im4.setImageBitmap(Global.coffercirclaricon);
        r22_header.addView(im4);
        im4.setLayoutParams(i1params);
        TextView t4=new TextView(context);
        t4.setTextSize(Global.fontSize);
        t4.setGravity(Gravity.CENTER);
        t4.setTextColor(Color.parseColor(Global.textColor));
        t4.setText(context.getString(R.string.totalBreakDurText));
        r22_header.addView(t4);
        t4.setLayoutParams(t1params);
        totalBreakText=new TextView(context);
        totalBreakText.setText("Total Break time");
        totalBreakText.setTextColor(Color.parseColor(Global.textColor));
        totalBreakText.setTextSize(Global.fontSize);
        totalBreakText.setTypeface(Typeface.DEFAULT_BOLD);
        r22.addView(totalBreakText);
        totalBreakText.setLayoutParams(t2params);
        r22.setBackgroundResource(R.drawable.transparentbutton);
        r22.setLayoutParams(r11params);





        TableRow r3=new TableRow(context);
        table.addView(r3);
        r3.setLayoutParams(r1params);
        LinearLayout r31,r32;

        r31=new LinearLayout(context);
        r31.setOrientation(LinearLayout.VERTICAL);
        r3.addView(r31);
        LinearLayout r31_header=new LinearLayout(context);
        r31_header.setOrientation(LinearLayout.HORIZONTAL);
        r31.addView(r31_header);
        ImageView im5=new ImageView(context);
        im5.setImageBitmap(Global.totalorderscircularicon);
        r31_header.addView(im5);
        im5.setLayoutParams(i1params);
        TextView t5=new TextView(context);
        t5.setTextSize(Global.fontSize);
        t5.setTextColor(Color.parseColor(Global.textColor));
        t5.setText(context.getString(R.string.totalOrdersText));
        t5.setGravity(Gravity.CENTER);
        r31_header.addView(t5);
        t5.setLayoutParams(t1params);
        totalOrders=new TextView(context);
        totalOrders.setText(context.getString(R.string.totalOrdersText));
        totalOrders.setTextColor(Color.parseColor(Global.textColor));
        totalOrders.setTextSize(Global.fontSize);
        totalOrders.setTypeface(Typeface.DEFAULT_BOLD);
        r31.addView(totalOrders);
        totalOrders.setLayoutParams(t2params);
        r31.setBackgroundResource(R.drawable.transparentbutton);
        r31.setLayoutParams(r11params);
        r32=new LinearLayout(context);
        r32.setOrientation(LinearLayout.VERTICAL);
        r3.addView(r32);
        LinearLayout r32_header=new LinearLayout(context);
        r32_header.setOrientation(LinearLayout.HORIZONTAL);
        r32.addView(r32_header);
        ImageView im6=new ImageView(context);
        im6.setImageBitmap(Global.openorderscircularicon);
        r32_header.addView(im6);
        im6.setLayoutParams(i1params);
        TextView t6=new TextView(context);
        t6.setTextSize(Global.fontSize);
        t6.setTextColor(Color.parseColor(Global.textColor));
        t6.setText(context.getString(R.string.openOrdersText));
        t6.setGravity(Gravity.CENTER);
        r32_header.addView(t6);
        t6.setLayoutParams(t1params);
        openOrders=new TextView(context);
        openOrders.setText(context.getString(R.string.openOrdersText));
        openOrders.setTextColor(Color.parseColor(Global.textColor));
        openOrders.setTextSize(Global.fontSize);
        openOrders.setTypeface(Typeface.DEFAULT_BOLD);
        r32.addView(openOrders);
        openOrders.setLayoutParams(t2params);
        r32.setBackgroundResource(R.drawable.transparentbutton);
        r32.setLayoutParams(r11params);



        final TableRow r4=new TableRow(context);
        table.addView(r4);
        r4.setLayoutParams(r1params);
        LinearLayout r41,r42;

        r41=new LinearLayout(context);
        r41.setOrientation(LinearLayout.VERTICAL);
        r4.addView(r41);
        LinearLayout r41_header=new LinearLayout(context);
        r41_header.setOrientation(LinearLayout.HORIZONTAL);
        r41.addView(r41_header);
        ImageView im7=new ImageView(context);
        im7.setImageBitmap(Global.moneycircularicon);
        r41_header.addView(im7);
        im7.setLayoutParams(i1params);
        TextView t7=new TextView(context);
        t7.setTextSize(Global.fontSize);
        t7.setTextColor(Color.parseColor(Global.textColor));
        t7.setText(context.getString(R.string.totalIncomeText));
        t7.setGravity(Gravity.CENTER);
        r41_header.addView(t7);
        t7.setLayoutParams(t1params);
        totalValue=new TextView(context);
        totalValue.setText(context.getString(R.string.totalIncomeText));
        totalValue.setTextColor(Color.parseColor(Global.textColor));
        totalValue.setTextSize(Global.fontSize);
        totalValue.setTypeface(Typeface.DEFAULT_BOLD);
        r41.addView(totalValue);
        totalValue.setLayoutParams(t2params);
        r41.setBackgroundResource(R.drawable.transparentbutton);
        r41.setLayoutParams(r11params);

        r42=new LinearLayout(context);
        r42.setOrientation(LinearLayout.VERTICAL);
        r4.addView(r42);
        LinearLayout r42_header=new LinearLayout(context);
        r42_header.setOrientation(LinearLayout.HORIZONTAL);
        r42.addView(r42_header);
        ImageView im8=new ImageView(context);
        im8.setImageBitmap(Global.treatscircularicon);
        r42_header.addView(im8);
        im8.setLayoutParams(i1params);
        TextView t8=new TextView(context);
        t8.setTextSize(Global.fontSize);
        t8.setTextColor(Color.parseColor(Global.textColor));
        t8.setText(context.getString(R.string.totalTreatsText));
        t8.setGravity(Gravity.CENTER);
        r42_header.addView(t8);
        t8.setLayoutParams(t1params);
        totalTreats=new TextView(context);
        totalTreats.setText(context.getString(R.string.totalTreatsText));
        totalTreats.setTextColor(Color.parseColor(Global.textColor));
        totalTreats.setTextSize(Global.fontSize);
        totalTreats.setTypeface(Typeface.DEFAULT_BOLD);
        r42.addView(totalTreats);
        totalTreats.setLayoutParams(t2params);
        r42.setBackgroundResource(R.drawable.transparentbutton);
        r42.setLayoutParams(r11params);


        final TableRow r5=new TableRow(context);
        table.addView(r5);
        r5.setLayoutParams(r1params);
        LinearLayout r51,r52;
        r51=new LinearLayout(context);
        r51.setOrientation(LinearLayout.VERTICAL);
        r5.addView(r51);
        LinearLayout r51_header=new LinearLayout(context);
        r51_header.setOrientation(LinearLayout.HORIZONTAL);
        r51.addView(r51_header);
        ImageView im9=new ImageView(context);
        im9.setImageBitmap(Global.cashincircularicon);
        r51_header.addView(im9);
        im9.setLayoutParams(i1params);
        TextView t9=new TextView(context);
        t9.setTextSize(Global.fontSize);
        t9.setTextColor(Color.parseColor(Global.textColor));
        t9.setText(context.getString(R.string.cashOnText));
        r51_header.addView(t9);
        t9.setLayoutParams(t1params);
        cachIn=new TextView(context);
        cachIn.setText(context.getString(R.string.cashOnText));
        cachIn.setTextColor(Color.parseColor(Global.textColor));
        cachIn.setTextSize(Global.fontSize);
        cachIn.setTypeface(Typeface.DEFAULT_BOLD);
        r51.addView(cachIn);
        cachIn.setLayoutParams(t2params);
        r51.setBackgroundResource(R.drawable.transparentbutton);
        r51.setLayoutParams(r11params);

        r52=new LinearLayout(context);
        r52.setOrientation(LinearLayout.VERTICAL);
        r5.addView(r52);
        LinearLayout r52_header=new LinearLayout(context);
        r52_header.setOrientation(LinearLayout.HORIZONTAL);
        r52.addView(r52_header);
        ImageView im10=new ImageView(context);
        im10.setImageBitmap(Global.cashoutcircularicon);
        r52_header.addView(im10);
        im10.setLayoutParams(i1params);
        TextView t10=new TextView(context);
        t10.setTextSize(Global.smallFontSize);
        t10.setGravity(Gravity.CENTER);
        t10.setTextColor(Color.parseColor(Global.textColor));
        t10.setText(context.getString(R.string.cashOutText));
        r52_header.addView(t10);
        t10.setLayoutParams(t1params);
        cashOut=new TextView(context);
        cashOut.setText(context.getString(R.string.cashOutText));
        cashOut.setTextColor(Color.parseColor(Global.textColor));
        cashOut.setTextSize(Global.fontSize);
        cashOut.setTypeface(Typeface.DEFAULT_BOLD);
        r52.addView(cashOut);
        cashOut.setLayoutParams(t2params);
        r52.setBackgroundResource(R.drawable.transparentbutton);
        r52.setLayoutParams(r11params);


        final TableRow r6=new TableRow(context);
        table.addView(r6);
        r6.setLayoutParams(r1params);
        LinearLayout r61;
        r61=new LinearLayout(context);
        r61.setOrientation(LinearLayout.VERTICAL);
        r6.addView(r61);
        LinearLayout r61_header=new LinearLayout(context);
        r61_header.setOrientation(LinearLayout.HORIZONTAL);
        r61.addView(r61_header);
        ImageView im11=new ImageView(context);
        im11.setImageBitmap(Global.clockcircularicon);
        r61_header.addView(im11);
        im11.setLayoutParams(i1params);
        TextView t11=new TextView(context);
        t11.setTextSize(Global.fontSize);
        t11.setTextColor(Color.parseColor(Global.textColor));
        t11.setText(context.getString(R.string.checkInText));
        t11.setGravity(Gravity.CENTER);
        r61_header.addView(t11);
        t11.setLayoutParams(t1params);
        checkIn=new TextView(context);
        checkIn.setText(context.getString(R.string.checkInText));
        checkIn.setTextColor(Color.parseColor(Global.textColor));
        checkIn.setTextSize(Global.fontSize);
        checkIn.setTypeface(Typeface.DEFAULT_BOLD);
        r61.addView(checkIn);
        checkIn.setLayoutParams(t2params);
        r61.setBackgroundResource(R.drawable.transparentbutton);
        r61.setLayoutParams(r11params);

        final ImageButton roundButton=new ImageButton(context);
        roundButton.setBackgroundResource(R.drawable.whiteroundbutton);
        l.addView(roundButton);
        roundButton.setImageBitmap(Global.upArrow);
        LinearLayout.LayoutParams rparams=(LinearLayout.LayoutParams)roundButton.getLayoutParams();
        rparams.width=2 * Global.downArrow.getWidth();
        rparams.height=2 * Global.downArrow.getHeight();
        rparams.topMargin=2 * pageHeight/100;
        rparams.leftMargin=(pageWidth-rparams.width)/2;
        roundButton.setLayoutParams(rparams);
        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upState==false)
                {
                    r4.setVisibility(View.GONE);
                    r5.setVisibility(View.GONE);
                    r6.setVisibility(View.GONE);
                    LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)table.getLayoutParams();
                    tparams.height=37 *pageHeight/100;
                    LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)openOrdersList.getLayoutParams();
                    oparams.height=24 * pageHeight/100;
                    roundButton.setImageBitmap(Global.downArrow);
                }
                else
                {
                    r4.setVisibility(View.VISIBLE);
                    r5.setVisibility(View.VISIBLE);
                    r6.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)table.getLayoutParams();
                    LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)openOrdersList.getLayoutParams();
                    oparams.height=12 * pageHeight/100;
                    tparams.height=75 * pageHeight/100;
                    roundButton.setImageBitmap(Global.upArrow);
                }
                upState=!upState;
            }
        });

        TextView openOrdersLabel=new TextView(context);
        openOrdersLabel.setTextSize(Global.fontSize);
        openOrdersLabel.setTextColor(Color.parseColor("#80000000"));
        openOrdersLabel.setText(context.getString(R.string.openOrdersText));
        openOrdersLabel.setGravity(Gravity.CENTER);
        l.addView(openOrdersLabel);
        LinearLayout.LayoutParams ooparams=(LinearLayout.LayoutParams)openOrdersLabel.getLayoutParams();
        ooparams.topMargin=2 * pageHeight/100;
        openOrdersLabel.setLayoutParams(ooparams);

        openOrdersList=new ListView(context);
        l.addView(openOrdersList);
        LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)openOrdersList.getLayoutParams();
        oparams.width=pageWidth;
        oparams.height=10 * pageHeight/100;
        openOrdersList.setLayoutParams(oparams);
        try {
            getOverview(Global.loginCredentials.getJSONObject("user").getInt("id"));
        } catch (JSONException e) {
            Global.showAlert(context.getString(R.string.executionProblemText)+" 2)Shifts",context);

            e.printStackTrace();
        }
        return l;
    }

    LinearLayout makeOrders()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        final ListView infoList=new ListView(context);
        l.addView(infoList);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)infoList.getLayoutParams();
        lparams.width=pageWidth;
        lparams.height=pageHeight;
        infoList.setLayoutParams(lparams);
        try {
            InternetClass.ObjectGETCall(context, Global.server + "/users/" +
                            Global.loginCredentials.getJSONObject("user").getInt("id") + "/orders",
                    Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray info=new JSONArray();
                            JSONArray content=new JSONArray();
                            try {
                                 content=response.getJSONArray("content");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for(int i=0;i<response.length();i++)
                            {

                                try {
                                    JSONObject x=content.getJSONObject(i);
                                    JSONObject fullOrder=x.getJSONObject("fullOrder");
                                    info.put(fullOrder);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            ShowOrdersAdapter adapter=new ShowOrdersAdapter(context,info);
                            infoList.setSelector(new ColorDrawable(0));
                            infoList.setAdapter(adapter);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("GIANNIS VOLEY ERROR IS "+error);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*try {

            InternetClass.ArrayGETCall(context, Global.server + "/users/" + Global.loginCredentials.getJSONObject("user").getInt("id")+"/orders",
                    Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONArray info=new JSONArray();

                            for(int i=0;i<response.length();i++)
                            {

                                try {
                                    JSONObject x=response.getJSONObject(i);
                                    JSONObject fullOrder=x.getJSONObject("fullOrder");
                                    info.put(fullOrder);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            ShowOrdersAdapter adapter=new ShowOrdersAdapter(context,info);
                            infoList.setSelector(new ColorDrawable(0));
                            infoList.setAdapter(adapter);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Global.showAlert(context.getString(R.string.executionProblemText)+" Orders",context);

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
         */
        return l;
    }

    class ListAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> data = new ArrayList<String>();

        public ListAdapter(Context ctx, ArrayList<String> tt) {
            super(ctx, 0, tt);
            context = ctx;
            data = tt;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout l = new LinearLayout(context);
            TextView t=new TextView(context);
            t.setTextSize(Global.fontSize);
            t.setTextColor(Color.parseColor(Global.textColor));
            t.setText(data.get(2*position));
            l.addView(t);

            TextView t1=new TextView(context);
            t1.setTextSize(Global.fontSize);
            t1.setTextColor(t.getCurrentTextColor());
            t1.setText(data.get(2*position+1));
            l.addView(t1);

            return l;
        }
    }


    JSONArray copyResponse=new JSONArray();

    LinearLayout makeWork()
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        final ListView listView=new ListView(context);
        l.addView(listView);
        LinearLayout.LayoutParams lparams=(LinearLayout.LayoutParams)listView.getLayoutParams();
        lparams.width=pageWidth;
        lparams.height=pageHeight;
        listView.setLayoutParams(lparams);
        listView.setBackgroundColor(Color.WHITE);

        try {
            InternetClass.ArrayGETCall(context, Global.server + "/users/" + Global.loginCredentials.getJSONObject("user").getInt("id") + "/shifts/all",
                    Global.getAuthorizationString(Global.loginCredentials), new JSONArrayListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            copyResponse=response;
                            ArrayList<String> dummy=new ArrayList<String>();
                            for(int i=0;i<response.length()+1;i++)
                                dummy.add(" ");
                            listView.setAdapter(new ShiftDisplayAdapter(context,response,dummy));
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Global.showAlert(context.getString(R.string.executionProblemText)+" 3)Shifts",context);

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    try {
                        JSONObject xx=copyResponse.getJSONObject(position-1);
                        String message=context.getString(R.string.workDateText)+":\t"+xx.getString("workDate")+"<br>"+
                                context.getString(R.string.checkedInText)+":\t"+Global.getHourAndMinute(xx.getString("checkInAt"))+"<br>"+
                                context.getString(R.string.checkedOutText)+":\t"+Global.getHourAndMinute(xx.getString("checkOutAt"))+"<br>"+
                                context.getString(R.string.cashInText)+":\t"+Global.currency+Global.displayDecimal(xx.getDouble("checkInCash"))+"<br>"+
                                context.getString(R.string.cashOutTextForList)+":\t"+Global.currency+Global.displayDecimal(
                                xx.get("checkOutCash")==JSONObject.NULL?0:
                                        xx.getDouble("checkOutCash"))+"<br>"+
                                context.getString(R.string.shiftDurationText)+":\t"+xx.getInt("shiftDuration")+"<br>"+//lepta
                                context.getString(R.string.shiftPayText)+":\t"+Global.currency+Global.displayDecimal(xx.getDouble("dailyPay"))+"<br>"+
                                context.getString(R.string.totalOrdersText)+":\t"+xx.getInt("orderCount")+"\n"+
                                context.getString(R.string.totalIncomeText)+":\t"+Global.currency+Global.displayDecimal(xx.getDouble("dailyPay"));//

                        Global.showAlert(message,context);
                    } catch (JSONException e) {
                        Global.showAlert(context.getString(R.string.executionProblemText)+" 4)Shifts",context);

                        e.printStackTrace();
                    }
                }
            }
        });
        return l;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position)
    {
        LinearLayout l=null;
        if(listener==null)
        {
            listener=new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.tables.open")==false)
                        {
                            Global.showAlert(context.getString(R.string.operationNotPermittedText),context);
                        }
                        else
                        {
                            //open order now
                            InternetClass.ObjectGETCall(context,Global.server+"/orders/"+adapter.getInfo(position).getInt("id"),
                                    Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response.getInt("orderType")==2)
                                                {
                                                    //einai takeway
                                                    Global.makeTakeoutTable();
                                                    try {
                                                        Global.takeoutTable.put("order", new JSONObject());
                                                        Global.takeoutTable.getJSONObject("order").put("details", response);
                                                        Global.takeoutTable.getJSONObject("order").put("orderId", response.getInt("id"));

                                                        Intent I = new Intent(((Activity) context), OpenOrderActivity.class);
                                                        I.putExtra("isTakeOut", true);
                                                        I.putExtra("tableCode", Global.takeoutTable.getString("tableCode"));
                                                        I.putExtra("tableJson", Global.takeoutTable.toString());
                                                        I.putExtra("details", response.toString());
                                                        ((Activity) context).startActivityForResult(I, Global.takeoutExitCode);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                else
                                                {
                                                    //anoigei apla opos kai stin open tables
                                                    JSONObject table=new JSONObject();
                                                    try {
                                                        table.put("tableCode",response.getString("tableCode"));
                                                        table.put("isOpen",true);
                                                        table.put("openedAt","");
                                                        table.put("order",response);
                                                        table.getJSONObject("order").put("orderId",response.getInt("id"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Intent I = new Intent(context, OpenOrderActivity.class);
                                                    try {
                                                        I.putExtra("tableCode", response.getString("tableCode"));
                                                        I.putExtra("tableJson", table.toString());
                                                        I.putExtra("isTakeOut",false);
                                                        ((Activity) context).startActivityForResult(I, Global.exitCode);
                                                    } catch (JSONException e) {
                                                        Global.showAlert("Failed to open table "+response,context);
                                                        e.printStackTrace();
                                                    }

                                                }

                                            /*    InternetClass.ObjectGETCall(context, Global.server + "/orders/tables/" + response.getString("tableCode"),
                                                        Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                Intent I = new Intent(context, OpenOrderActivity.class);
                                                                try {
                                                                    I.putExtra("tableCode", response.getString("tableCode"));
                                                                    I.putExtra("tableJson", response.toString());
                                                                    I.putExtra("isTakeOut",false);
                                                                    ((Activity) context).startActivityForResult(I, Global.exitCode);
                                                                } catch (JSONException e) {
                                                                    Global.showAlert("Failed to open table "+response,context);
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Global.showAlert("Failed to open table "+error,context);
                                                            }
                                                        });*/
                                            } catch (JSONException e) {
                                                Global.showAlert("Failed to open table ",context);
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Global.showAlert("Failed to open table ",context);
                                        }
                                    });
                        }
                    } catch (JSONException e) {
                        Global.showAlert("Failed to open table ",context);

                        e.printStackTrace();
                    }
                }
            };
        }

        try {
            if(Global.loginCredentials.getBoolean("isManager"))
            {
                switch (position) {
                    case 0:
                        l = makeOverViewManager();
                        break;
                    case 1:
                        l = makeWaiters();
                        break;
                    default:
                        return null;
                }
            }
            else {
                switch (position) {
                    case 0:
                        l = makeOverview();
                        break;
                    case 1:
                        l = makeOrders();
                        break;
                    case 2:
                        l = makeWork();
                        break;
                    default:
                        return null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        collection.addView(l);
        return l;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }
    @Override
    public int getCount() {
        try {
            if(Global.loginCredentials.getBoolean("isManager"))
                return 2;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
