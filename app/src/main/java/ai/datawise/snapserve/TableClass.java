package ai.datawise.snapserve;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableClass
{
    private LinearLayout mylayout=null;
    private int pageWidth=0,pageHeight=0,tableCount=0,openTables=0;
    private Context context=null;
    private int currentLevel=0;
    private JSONArray mtables=null;
    private TextView openTablesText;
    private ScrollView tableView;
    private TableLayout tableLayout;

    int getCurrentLevel()
    {
        return  currentLevel;
    }

    static LongOperation operation=null;


    void openOrderTable(int tableid)
    {
        try {
            int waiterId=0;
            if(mtables.getJSONObject(tableid).has("order"))
            {
                waiterId=mtables.getJSONObject(tableid).getJSONObject("order").getInt("waiterId");
            }

            if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.others.view")==false
                    && waiterId!=
                    Global.loginCredentials.getJSONObject("user").getInt("id")
                    && waiterId!=0)
            {



                Global.showAlert(context.getString(R.string.operationNotPermittedText),context);
            }
            else {
                final JSONObject x;
                try {

                    x = mtables.getJSONObject(tableid);
                    Intent I = new Intent(context, OpenOrderActivity.class);
                    I.putExtra("isTakeOut",false);
                    I.putExtra("tableCode", x.getString("tableCode"));
                    I.putExtra("tableJson", x.toString());
                    ((Activity) context).startActivityForResult(I, Global.exitCode);
                } catch (JSONException e) {
                    System.out.println("GIANNIS E"+e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            System.out.println("GIANNSI E"+e.getMessage());
            e.printStackTrace();
        }

    }

    void updateTableView(final int levelid, final int tableid)
    {

        InternetClass.ObjectGETCall(context, Global.server + "/tables/status?levelId=" + levelid,
                Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray levels= null;
                        mtables=new JSONArray();
                        try {
                            levels = response.getJSONArray("levels");
                            for(int i=0;i<levels.length();i++)
                            {
                                JSONObject xx=levels.getJSONObject(i);
                                if(xx.getInt("id")==levelid)
                                {
                                    mtables=xx.getJSONArray("tables");
                                    break;
                                }

                            }
                            updateTableViewSecond(levelid,tableid);
                        } catch (JSONException e) {
                            System.out.println("GIANNIS JSON EXCEPTION "+e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert(context.getString(R.string.executionProblemText)+" Fetch Tables",context);

                    }
                });
    }



    void makeActualOrder(final int tableid, String tablecoode, int persons, String language, String workDate, final View view)
    {
        JSONObject jsonBody=new JSONObject();
        try {
            jsonBody.put("tableCode",tablecoode);
            jsonBody.put("workdate",workDate);
            jsonBody.put("persons",persons);
            jsonBody.put("language",language);
            jsonBody.put("customerId",1);
            jsonBody.put("customerCard",0);
            int levelId=Global.levelIndex>=0?
                    Global.levels.getJSONObject(Global.levelIndex).getInt("id"):0;
            jsonBody.put("levelId",levelId);
            jsonBody.put("posTerminalId",Global.loginCredentials.getInt("terminalid"));
        } catch (JSONException e) {
            view.setEnabled(true);
            e.printStackTrace();
        }


        InternetClass.ObjectPOSTCall(context,
                Global.server + "/orders/tables/", jsonBody,
                Global.getAuthorizationString(Global.loginCredentials),
                new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        view.setEnabled(true);
                        updateTableView(currentLevel, tableid);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert(context.getString(R.string.executionProblemText)+" Create Order ",context);
                        view.setEnabled(true);
                    }
                }
        );
    }


    public static boolean firsttime=true;


    void actualTableAction(final int tableid, final View view)
    {
        try {
            final JSONObject x;
            x = mtables.getJSONObject(tableid);
            if (x.getBoolean("isOpen") == false) {


                //check if can open new table
                if(Global.loginCredentials.getJSONObject("user").getJSONObject("rolePermissions").getBoolean("orders.tables.open")==false)
                {
                    Global.showAlert(context.getString(R.string.operationNotPermittedText),context);
                    view.setEnabled(true);
                }
                else {
                    //show keypad
                    OrderDialog dd = new OrderDialog(x.getString("tableCode"), context);
                    dd.show();
                    dd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            OrderDialog mydialog = (OrderDialog) dialog;
                            try {

                                String tablecode = x.getString("tableCode");
                                String language = mydialog.getLanguage();
                                int npersons = mydialog.getText();
                                if (npersons != 0)
                                    makeActualOrder( tableid,tablecode,npersons,language,Global.workdate,view);
                                else
                                    view.setEnabled(true);
                            } catch (JSONException e) {
                                view.setEnabled(true);
                                e.printStackTrace();
                            }

                        }
                    });
                }
            } else {
                openOrderTable(tableid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void tableAction(final int tableid, final View view)
    {

        try {
            if(
                    Global.loginCredentials.has("inBreak") &&
                            Global.loginCredentials.getBoolean("inBreak")==true)
            {
                view.setEnabled(true);
                OrderCloseDialog dialog=new OrderCloseDialog(context,
                        context.getString(R.string.inBreakMessage),
                        context.getString(R.string.okGotItText),"");
                dialog.show();
            }
            else {
                try {
                    if (Global.loginCredentials.has("checkedIn")==false ||
                            Global.loginCredentials.getBoolean("checkedIn") == false) {
                        InternetClass.checkIn(context, Global.server + "/checkin", Global.getAuthorizationString(Global.loginCredentials),
                                new JSONObjectListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        actualTableAction(tableid,view);
                                        try {
                                            Global.loginCredentials.put("checkedIn", true);
                                            Global.saveData();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        view.setEnabled(true);
                                        Global.showAlert(context.getString(R.string.executionProblemText)+" Check In",context);

                                    }
                                });
                    } else actualTableAction(tableid,view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            view.setEnabled(true);
            e.printStackTrace();
        }
    }

    void updateTableViewSecond(int levelid,int tableid)
    {

        currentLevel=levelid;
        if(mtables==null)
        {
            Global.showAlert(context.getString(R.string.tableProblemText),context);
        }
        else {

            int totalTables=mtables.length();
            openTables=0;  tableCount = 0;
            tableLayout.removeAllViews();


            int tablesPerLine=3;
            int numberOfLines = mtables.length() / tablesPerLine;
            if(numberOfLines * tablesPerLine < totalTables) numberOfLines++;

            int tableWidth = 30 * pageWidth/100;
            int tableHeight = 20 * pageHeight/100;

            View.OnClickListener tableActionListener=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    int tableid = (Integer) v.getTag();
                    tableAction(tableid,v);
                }
            };

            for (int j = 0; j < numberOfLines; j++) {
                TableRow row = new TableRow(context);
                tableLayout.addView(row);
                for (int k = 0; k < tablesPerLine; k++) {

                    if (tableCount >= mtables.length()) break;
                    JSONObject currentTable=null;
                    try {
                        currentTable=mtables.getJSONObject(tableCount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int colorId=-1;

                    Button tableButton = new Button(context);
                    try {
                        tableButton.setTransformationMethod(null);
                        tableButton.setText(currentTable.getString("tableCode"));
                        row.addView(tableButton);

                        if (currentTable.getBoolean("isOpen") == true) {

                            tableButton.setBackgroundResource(R.drawable.blueroundbutton);
                            tableButton.setTextColor(Color.rgb(255, 255, 255));
                            openTables++;
                            String openedAt=currentTable.getString("openedAt");
                            String diffTime=Global.diffDateFrom(openedAt);
                            tableButton.setLines(2);
                            tableButton.setText(tableButton.getText()+"\n"+diffTime);
                        }
                        else
                        {
                            tableButton.setBackgroundResource(R.drawable.transparentbutton);
                            tableButton.setTextColor(Color.parseColor("#272f3d"));
                            colorId=-1;
                            if(currentTable.get("colorId")!=JSONObject.NULL)
                            {
                                colorId=currentTable.getInt("colorId");
                            }

                            if(colorId!=-1)
                            {
                                try {
                                    StateListDrawable gradientDrawable = (StateListDrawable) tableButton.getBackground();
                                    DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) gradientDrawable.getConstantState();
                                    Drawable[] children = drawableContainerState.getChildren();

                                    GradientDrawable selectedDrawable = (GradientDrawable) children[0];
                                    GradientDrawable unselectedDrawable = (GradientDrawable) children[1];
                                    unselectedDrawable.setColor(Color.parseColor(Global.getHexColor(colorId)));

                                    tableButton.setBackground(gradientDrawable);
                                }catch (Exception e)
                                {
                                    System.out.println("GIANNIS E "+e.getMessage());
                                }
                            }
                        }
                        tableButton.setTag(new Integer(tableCount));
                        tableButton.setOnClickListener(tableActionListener);

                        TableRow.LayoutParams bparams = (TableRow.LayoutParams) tableButton.getLayoutParams();
                        bparams.width = tableWidth;
                        bparams.height = tableHeight;
                        bparams.leftMargin = pageWidth / 40;
                        bparams.bottomMargin = pageHeight / 160;
                        bparams.topMargin = pageHeight / 160;
                        bparams.gravity=Gravity.CENTER_VERTICAL;
                        tableButton.setLayoutParams(bparams);

                        tableCount++;
                    } catch (JSONException e) {
                        System.out.println("GIANNIS JSON E2 "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            openTablesText.setText(openTables+" "+context.getString(R.string.oftext)+" "+tableCount);
            openTablesText.invalidate();
            mylayout.invalidate();

        }
        if(firsttime)
        {
            firsttime=false;
            operation= new LongOperation(this);
            operation.execute("");
        }
        if(tableid!=-1)
            openOrderTable(tableid);
    }

    private void drawTable()
    {
        LinearLayout h=new LinearLayout(context);
        h.setOrientation(LinearLayout.HORIZONTAL);
        h.setBackgroundColor(Color.parseColor("#e5e6e8"));
        mylayout.addView(h);
        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)h.getLayoutParams();
        hparams.width=pageWidth;
        hparams.height=10 * pageHeight/100;
        h.setLayoutParams(hparams);
        TextView t1=new TextView(context);
        t1.setTextSize(Global.fontSize);
        t1.setText(context.getString(R.string.openalltablestext));
        t1.setTypeface(Typeface.DEFAULT_BOLD);
        h.addView(t1);
        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=5 * hparams.width/100;
        t1params.gravity=Gravity.CENTER;
        t1params.width=hparams.width/4;
        t1.setLayoutParams(t1params);

        openTablesText=new TextView(context);
        openTablesText.setTextSize(Global.fontSize);
        openTablesText.setText(0+" "+context.getString(R.string.oftext)+" "+0);
        h.addView(openTablesText);
        LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)openTablesText.getLayoutParams();
        oparams.leftMargin=t1params.leftMargin;
        oparams.width=hparams.width/6;
        oparams.gravity=Gravity.CENTER;
        openTablesText.setLayoutParams(oparams);



        ImageView im=new ImageView(context);
        im.setImageBitmap(Global.rightArrow);
        im.setClickable(true);
        im.setOnTouchListener(Global.touchListener);
        h.addView(im);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        iparams.rightMargin=oparams.leftMargin;
        iparams.width=Global.rightArrow.getWidth();
        iparams.height=Global.rightArrow.getHeight();
        iparams.leftMargin=hparams.width-t1params.leftMargin-t1params.width-oparams.leftMargin-oparams.width-iparams.width-iparams.rightMargin;
        iparams.gravity=Gravity.CENTER;
        im.setLayoutParams(iparams);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mtables!=null && mtables.length()!=0)
                {
                    Intent I=new Intent(context,OpenTablesActivity.class);
                    I.putExtra("mtables",mtables.toString());
                    ((Activity)context).startActivityForResult(I,Global.exitCode);
                }
            }
        });

        h.setClickable(true);

        h.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setBackgroundColor(Color.parseColor("#f4d63a"));
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_CANCEL: {
                        v.setBackgroundColor(Color.parseColor("#e5e6e8"));
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mtables!=null && mtables.length()!=0)
                {
                    Intent I=new Intent(context,OpenTablesActivity.class);
                    I.putExtra("mtables",mtables.toString());
                    ((Activity)context).startActivityForResult(I,Global.exitCode);
                }
            }
        });

        LinearLayout secondRow=new LinearLayout(context);
        secondRow.setOrientation(LinearLayout.HORIZONTAL);
        secondRow.setBackgroundColor(Color.TRANSPARENT);
        mylayout.addView(secondRow);
        secondRow.setLayoutParams(hparams);
        secondRow.setBackgroundResource(R.drawable.backwithborder);

        Bitmap bm1=BitmapFactory.decodeResource(context.getResources(),R.drawable.graystack);
        double ratio;
        int neww;
        int newh;
        ratio = bm1.getWidth()*1.0/bm1.getHeight();
        neww=8 * hparams.width/100;
        newh=(int)(neww/ratio);
        bm1=bm1.createScaledBitmap(bm1,neww,newh,true);
        ImageView im1=new ImageView(context);
        im1.setImageBitmap(bm1);
        secondRow.addView(im1);
        LinearLayout.LayoutParams im1params=(LinearLayout.LayoutParams)im1.getLayoutParams();
        im1params.leftMargin=t1params.leftMargin;
        im1params.width=neww;
        im1params.gravity=Gravity.CENTER;
        im1.setLayoutParams(im1params);

        HorizontalScrollView scrollView=new HorizontalScrollView(context);
        secondRow.addView(scrollView);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)scrollView.getLayoutParams();
        sparams.leftMargin=im1params.leftMargin;
        sparams.gravity=Gravity.CENTER;
        //sparams.topMargin=im1params.topMargin;
        sparams.width=hparams.width-im1params.leftMargin-im1params.width-sparams.leftMargin;
        scrollView.setLayoutParams(sparams);
        LinearLayout scrollLayout=new LinearLayout(context);
        scrollView.addView(scrollLayout);
        scrollView.setHorizontalScrollBarEnabled(false);
        final ArrayList<Button> levelButton=new ArrayList<Button>();

        for(int i=0;i<Global.levels.length();i++)
        {

            Button bt1=new Button(context);
            try {
                bt1.setText(Global.levels.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            scrollLayout.addView(bt1);
            bt1.setBackgroundResource(R.drawable.tablewhite);
            bt1.setTextSize(Global.smallFontSize);
            bt1.setGravity(Gravity.CENTER);
            bt1.setTextColor(Color.parseColor("#272f3d"));
            LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)bt1.getLayoutParams();
            bparams.leftMargin=2 * hparams.width/100;
            bt1.setLayoutParams(bparams);
            bt1.setTag(new Integer(i));
            levelButton.add(bt1);
            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mlevel=(Integer)v.getTag();
                    Global.levelIndex=mlevel;
                    for(int j=0;j<levelButton.size();j++)
                    {
                        int id=(Integer)levelButton.get(j).getTag();
                        if(id!=mlevel)
                            levelButton.get(j).setBackgroundResource(R.drawable.tablewhitetop);
                    }
                    v.setBackgroundResource(R.drawable.yellowbutton);

                    try {
                        updateTableView(Global.levels.getJSONObject(mlevel).getInt("id"),-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        tableView=new ScrollView(context);
        mylayout.addView(tableView);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)tableView.getLayoutParams();
        tparams.width=hparams.width;
        tparams.height=pageHeight-2*hparams.height;
        tableView.setLayoutParams(tparams);
        tableLayout=new TableLayout(context);
        tableView.addView(tableLayout);
        if(Global.levelIndex>0)
        {
            try {
                updateTableView(Global.levels.getJSONObject(Global.levelIndex).getInt("id"), -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                updateTableView(Global.levels.getJSONObject(0).getInt("id"), -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    TableClass(Context ctx,LinearLayout l,int pw,int ph)
    {
        context=ctx;
        mylayout=l;
        pageWidth=pw;
        pageHeight=ph;
        drawTable();
    }
}
