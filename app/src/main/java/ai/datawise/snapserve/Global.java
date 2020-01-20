package ai.datawise.snapserve;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Global {
    public static int width = 0;
    public static int height = 0;

    //codes for startActivityForResult
    public static final int exitCode = 200;
    public static final int payCode = 300;
    public static final int basketCode = 400;
    public static final int settingsCode = 500;
    public static final int takeoutExitCode=600;
    public static int levelIndex = 0;

    public static String backgroundColor = "#f7f7f8";
    public static String hintColor = "#8097b0";
    public static String headerColor = "#3a415b";
    public static String listSelectColor = "#ffea9c";
    public static String textColor = "#d9000000";
    public static String tintColor = "#ffd63a";
    public static String dateFormat = "yyyy-M-dd'T'hh:mm:ss";
    public static String currency = "";
    public static int fontSize = 14;
    public static int bigFontSize = 0;
    public static int smallFontSize = 0;
    public static int buttonWidth = 0;
    public static int buttonHeight = 0;
    public static int headerHeight = 0;
    public static int activeCatalogueId = 0;
    public static String workdate = "";
    public static NumberFormat formatter = new DecimalFormat("#0.00");


    public static Bitmap grayBack = null;
    public static Bitmap grayLogout = null;
    public static Bitmap editProfile = null;
    public static Bitmap xiconForList = null;
    public static Bitmap cofferForList = null;
    public static Bitmap clockForList = null;
    public static Bitmap rightArrow = null;
    public static Bitmap grayTable = null;
    public static Bitmap threeDots = null;
    public static Bitmap downArrow = null;
    public static Bitmap xicon = null;
    public static Bitmap settingsIcon = null;
    public static Bitmap bellIcon = null;
    public static Bitmap personIcon = null;
    public static Bitmap yellowIcon = null;
    public static Bitmap whiteBack = null;
    public static Bitmap xshape = null;
    public static Bitmap coffeIcon = null;
    public static Bitmap breakIcon = null;
    public static Bitmap rightEnter = null;
    public static Bitmap notesIcon = null;
    public static Bitmap pagerIcon = null;
    public static Bitmap discountIcon = null;
    public static Bitmap treatIcon = null;
    public static Bitmap datawiseIcon = null;
    public static Bitmap upArrow = null;
    public static Bitmap coffeGray = null;
    public static Bitmap breakGray = null;
    public static Bitmap dialogIcon = null;
    public static Bitmap userIcon = null;
    public static Bitmap lockerIcon = null;
    public static Bitmap tickIcon = null;
    public static Bitmap redxicon = null;
    public static Bitmap clockcircularicon = null;
    public static Bitmap coffercirclaricon = null;
    public static Bitmap usercircularicon = null;
    public static Bitmap moneycircularicon = null;
    public static Bitmap openorderscircularicon = null;
    public static Bitmap treatscircularicon = null;
    public static Bitmap cancelcircularicon = null;
    public static Bitmap totalorderscircularicon = null;
    public static Bitmap cashincircularicon = null;
    public static Bitmap cashoutcircularicon = null;
    public static Bitmap terminalIcon=null;
    public static Bitmap rectnotesIcon=null;
    public static Bitmap cancelOrderIcon=null;
    public  static Bitmap notesforlisticon=null;
    public static Bitmap noimageforlisticon=null;
    public static Bitmap transfericon=null;
    public static Bitmap blackxIcon=null;
    public static Bitmap bluedownIcon=null;
    public static Bitmap doublebluedownIcon=null;
    public static Bitmap blueupIcon=null;
    public static Bitmap doubleupIcon=null;

    public static String server = "";//http://n2.datawise.ai:8081/";
    public static JSONObject loginCredentials = new JSONObject();
    public static JSONArray productItems = new JSONArray();
    public static JSONArray productCategories = new JSONArray();

    public static JSONArray tableProductCategories=new JSONArray();
    public static JSONArray takeoutProductCategories=new JSONArray();

    public static JSONArray priceList = new JSONArray();
    public static JSONArray productsInPriceLists = new JSONArray();
    public static JSONArray levels = new JSONArray();
    public static JSONArray productExtras = new JSONArray();
    public static JSONArray unitTypes = new JSONArray();
    public static JSONArray extraItems = new JSONArray();
    public static JSONArray pagers = new JSONArray();
    public static JSONArray paymentMethods = new JSONArray();
    public static JSONArray innerProductCategories = new JSONArray();
    public static JSONArray colors=new JSONArray();
    public static AdminService adminService = null;
    public static View.OnTouchListener touchListener = null;

    public static String savePath = "xposwaiter.txt";
    public static String grossornetoption = "net";
    public static ProgressDialog progressBar = null;
    public static JSONArray users = new JSONArray();
    public static JSONObject takeoutTable=new JSONObject();
    public static final Integer TYPE_CLOCK_IN       = 0;
    public static final Integer TYPE_CANCEL_ORDER   = 1;
    public static final Integer TYPE_REFUND_ORDER   = 2;
    public static final Integer TYPE_DISCOUNT_ORDER = 3;
    public static final Integer TYPE_TREAT_ORDER    = 4;
    public static final Integer TYPE_TRANSFER_ORDER = 5;

    public static String version="1.18";

    public static  int[] messageType=
            {
                    R.string.userClockInText,
                    R.string.userCancelOrderText,
                    R.string.userRefundOrderText,
                    R.string.userDiscountOrderText,
                    R.string.userTreatOrderText,
                    R.string.userTransderText
            };

    public static void makeTakeoutTable()
    {
        try {
            takeoutTable.put("tableCode","-1");
            takeoutTable.put("isOpen",true);
            takeoutTable.put("openedAt","");
            takeoutTable.put("order",new JSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void showProgess(Context context)
    {
        if(progressBar==null) {
            progressBar = new ProgressDialog(context);
            progressBar.setCancelable(false);
            progressBar.setMessage(context.getString(R.string.pleaseWaitText));
            progressBar.show();
        }
    }

    public static void hideProgress()
    {
        if(progressBar!=null) {
            progressBar.hide();
            progressBar = null;
        }
    }

    public static void makeDecimalFormat()
    {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        Global.formatter=new DecimalFormat("#0.00",otherSymbols);
    }

    public static String getFilepath(Context context)
    {
        File path = context.getFilesDir();
        File file = new File(path, "xposwaiter.txt");
        System.out.println("GIANNIS FILEPATH IS "+file.toString());
        return file.toString();
    }

    public static boolean hasOfflineData()
    {
        File f2 = new File(Global.savePath);
        return f2.exists();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void loadData()
    {
        String path = Global.savePath;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            Global.loginCredentials=new JSONObject(everything);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData()
    {
        String path =  Global.savePath;
        File f2 = new File(path);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f2));
            writer.write (Global.loginCredentials.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("GIANNIS Save data failed "+e.getMessage());
            e.printStackTrace();
        }
    }


    public static void clearData()
    {
        try {
            String server=Global.loginCredentials.getString("server");
            Global.loginCredentials=new JSONObject();
            Global.loginCredentials.put("server",server);
            saveData();
        } catch (JSONException e) {
            System.out.println("GIANNIS EE "+e.getMessage());
            e.printStackTrace();
        }

    }
    public static void removeData()
    {
        String path =  Global.savePath;
        File f2 = new File(path);
        f2.delete();
    }


    private static String[] getScreenDimension(Context ctx){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity )ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double)width / (double)dens;
        double hi = (double)height / (double)dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x+y);

        String[] screenInformation = new String[3];
        screenInformation[0] = String.valueOf(width) + " px";
        screenInformation[1] = String.valueOf(height) + " px" ;
        screenInformation[2] = screenInches+"";

        return screenInformation;
    }

    public static void loadIcons(Context context)
    {

        //big width icons
        Global.grayBack= BitmapFactory.decodeResource(context.getResources(),R.drawable.grayback);
        double ratio=Global.grayBack.getWidth()*1.0/Global.grayBack.getHeight();
        int neww=6 * Global.width/100;
        int newh=(int)(neww/ratio);
        newh=(Global.headerHeight/3);
        neww=(int)(ratio*newh);
        Global.grayBack=Global.grayBack.createScaledBitmap(Global.grayBack,neww,newh,true);
        Global.grayLogout=BitmapFactory.decodeResource(context.getResources(),R.drawable.logout);
        Global.grayLogout=Global.grayLogout.createScaledBitmap(Global.grayLogout,neww,newh,true);
        Global.whiteBack=BitmapFactory.decodeResource(context.getResources(),R.drawable.back);
        Global.whiteBack=Global.whiteBack.createScaledBitmap(Global.whiteBack,neww,newh,true);


        newh=(Global.headerHeight/3);
        neww=(int)(ratio*newh);
        Global.personIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.person);
        Global.personIcon=Global.personIcon.createScaledBitmap(Global.personIcon,neww,newh,true);
        Global.personIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.person);
        Global.personIcon=Global.personIcon.createScaledBitmap(Global.personIcon,neww,newh,true);
        Global.bellIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.bell);
        Global.bellIcon=Global.bellIcon.createScaledBitmap(Global.bellIcon,neww,newh,true);
        Global.settingsIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.settings);
        Global.settingsIcon=Global.settingsIcon.createScaledBitmap(Global.settingsIcon,neww,newh,true);
        Global.xshape=BitmapFactory.decodeResource(context.getResources(),R.drawable.xshape);
        Global.xshape=Global.xshape.createScaledBitmap(Global.xshape,neww,newh,true);
        Global.blackxIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.blackx);
        Global.blackxIcon=Global.blackxIcon.createScaledBitmap(Global.blackxIcon,neww,newh,true);
        Global.datawiseIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.datawise);
        Global.datawiseIcon=Global.datawiseIcon.createScaledBitmap(Global.datawiseIcon,neww,newh,true);


        //small height icons
        Global.editProfile=BitmapFactory.decodeResource(context.getResources(),R.drawable.editprofile);
        ratio=Global.editProfile.getWidth()*1.0/Global.editProfile.getHeight();
        neww=6 * Global.width/100;
        newh=(int)(neww/ratio);
        Global.editProfile=Global.editProfile.createScaledBitmap(Global.editProfile,neww,newh,true);
        Global.xiconForList=BitmapFactory.decodeResource(context.getResources(),R.drawable.plusiconforlist);
        Global.xiconForList=Global.xiconForList.createScaledBitmap(Global.xiconForList,neww,newh,true);
        Global.cofferForList=BitmapFactory.decodeResource(context.getResources(),R.drawable.coffeforlist);
        Global.cofferForList=Global.cofferForList.createScaledBitmap(Global.cofferForList,neww,newh,true);
        Global.clockForList=BitmapFactory.decodeResource(context.getResources(),R.drawable.clockforlist);
        Global.clockForList=Global.clockForList.createScaledBitmap(Global.clockForList,neww,newh,true);
        Global.userIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.person);
        Global.userIcon=Global.userIcon.createScaledBitmap(Global.userIcon,neww,newh,true);
        Global.terminalIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.terminal);
        Global.terminalIcon=Global.terminalIcon.createScaledBitmap(Global.terminalIcon,neww,newh,true);
        Global.lockerIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.locker);
        Global.lockerIcon=Global.lockerIcon.createScaledBitmap(Global.lockerIcon,neww,newh,true);
        Global.tickIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.tickicon);
        Global.tickIcon=Global.lockerIcon.createScaledBitmap(Global.tickIcon,neww,newh,true);
        Global.redxicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.redxicon);
        Global.redxicon=Global.lockerIcon.createScaledBitmap(Global.redxicon,neww,newh,true);
        Global.clockcircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.clockcircular);
        Global.clockcircularicon=Global.clockcircularicon.createScaledBitmap(Global.clockcircularicon,neww,newh,true);
        Global.coffercirclaricon=BitmapFactory.decodeResource(context.getResources(),R.drawable.coffecircular);
        Global.coffercirclaricon=Global.coffercirclaricon.createScaledBitmap(Global.coffercirclaricon,neww,newh,true);
        Global.usercircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.circularuser);
        Global.usercircularicon=Global.usercircularicon.createScaledBitmap(Global.usercircularicon,neww,newh,true);
        Global.moneycircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.circularmoney);
        Global.moneycircularicon=Global.moneycircularicon.createScaledBitmap(Global.moneycircularicon,neww,newh,true);
        Global.openorderscircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.openorderscircular);
        Global.openorderscircularicon=Global.openorderscircularicon.createScaledBitmap(Global.openorderscircularicon,neww,newh,true);
        Global.treatscircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.treatscircular);
        Global.treatscircularicon=Global.treatscircularicon.createScaledBitmap(Global.treatscircularicon,neww,newh,true);
        Global.cancelcircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.cancelcircular);
        Global.cancelcircularicon=Global.cancelcircularicon.createScaledBitmap(Global.cancelcircularicon,neww,newh,true);
        Global.totalorderscircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.totalorderscircular);
        Global.totalorderscircularicon=Global.totalorderscircularicon.createScaledBitmap(Global.totalorderscircularicon,neww,newh,true);
        Global.cashincircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.cashincircular);
        Global.cashincircularicon=Global.cashincircularicon.createScaledBitmap(Global.cashincircularicon,neww,newh,true);
        Global.cashoutcircularicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.cashoutcircular);
        Global.cashoutcircularicon=Global.cashoutcircularicon.createScaledBitmap(Global.cashoutcircularicon,neww,newh,true);
        Global.notesforlisticon=BitmapFactory.decodeResource(context.getResources(),R.drawable.notesforlist);
        Global.notesforlisticon=Global.notesforlisticon.createScaledBitmap(Global.notesforlisticon,neww,newh,true);
        Global.noimageforlisticon=BitmapFactory.decodeResource(context.getResources(),R.drawable.emptyimage);
        Global.noimageforlisticon=Global.noimageforlisticon.createScaledBitmap(noimageforlisticon,neww,newh,true);

        Global.grayTable=BitmapFactory.decodeResource(context.getResources(),R.drawable.graytable);
        Global.grayTable=Global.grayTable.createScaledBitmap(Global.grayTable,neww,newh,true);
        Global.downArrow=BitmapFactory.decodeResource(context.getResources(),R.drawable.downarrow);
        Global.downArrow=Global.downArrow.createScaledBitmap(Global.downArrow,neww,newh,true);
        Global.upArrow=BitmapFactory.decodeResource(context.getResources(),R.drawable.uparrow);
        Global.upArrow=Global.upArrow.createScaledBitmap(Global.upArrow,neww,newh,true);
        Global.rightEnter=BitmapFactory.decodeResource(context.getResources(),R.drawable.rightenter);
        Global.rightEnter=Global.rightEnter.createScaledBitmap(Global.rightEnter,neww,newh,true);


        //special cases
        newh=(Global.headerHeight/2);
        neww=(int)(ratio*newh);

        Global.xicon=BitmapFactory.decodeResource(context.getResources(),R.drawable.xicon);
        Global.xicon=Global.xicon.createScaledBitmap(Global.xicon,neww,newh,true);
        Global.threeDots=BitmapFactory.decodeResource(context.getResources(),R.drawable.threedots);
        Global.threeDots=Global.threeDots.createScaledBitmap(Global.threeDots,neww,newh,true);
        Global.rightArrow=BitmapFactory.decodeResource(context.getResources(),R.drawable.rightarrow);
        Global.rightArrow=Global.rightArrow.createScaledBitmap(Global.rightArrow,neww,newh,true);


        Global.yellowIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.yellowplus);
        Global.yellowIcon=Global.yellowIcon.createScaledBitmap(Global.yellowIcon,neww,newh,true);
        Global.coffeIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.coffe);
        Global.coffeIcon=Global.coffeIcon.createScaledBitmap(Global.coffeIcon,neww,newh,true);
        Global.breakIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.clock);
        Global.breakIcon=Global.breakIcon.createScaledBitmap(Global.breakIcon,neww,newh,true);
        Global.coffeGray=BitmapFactory.decodeResource(context.getResources(),R.drawable.coffegray);
        Global.coffeGray=Global.coffeGray.createScaledBitmap(Global.coffeGray,neww,newh,true);
        Global.breakGray=BitmapFactory.decodeResource(context.getResources(),R.drawable.clockgray);
        Global.breakGray=Global.breakGray.createScaledBitmap(Global.breakGray,neww,newh,true);
        //special cases
        newh=65 * Global.headerHeight/100;
        neww=(int)(ratio*newh);
        Global.notesIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.notesicon);
        Global.notesIcon=Global.notesIcon.createScaledBitmap(Global.notesIcon,neww,newh,true);

        Global.pagerIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.pagericon);
        Global.pagerIcon=Global.pagerIcon.createScaledBitmap(Global.pagerIcon,neww,newh,true);
        Global.discountIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.discounticon);
        Global.discountIcon=Global.discountIcon.createScaledBitmap(Global.discountIcon,neww,newh,true);
        Global.transfericon=BitmapFactory.decodeResource(context.getResources(),R.drawable.transfer);
        Global.transfericon=Global.transfericon.createScaledBitmap(Global.transfericon,neww,newh,true);
        Global.treatIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.treaticon);
        Global.treatIcon=Global.treatIcon.createScaledBitmap(Global.treatIcon,neww,newh,true);
        Global.cancelOrderIcon=BitmapFactory.decodeResource(context.getResources(),R.drawable.cancelorder);
        Global.cancelOrderIcon=Global.cancelOrderIcon.createScaledBitmap(Global.cancelOrderIcon,neww,newh,true);
        if(Global.dialogIcon==null) {
            int h1=Global.height/3;
            int h2=20 * h1/100;
            Global.dialogIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.snapserve);
            ratio = Global.dialogIcon.getWidth() * 1.0 / Global.dialogIcon.getHeight();
            int newheight = 80 * h2 / 100;
            int newwidth = (int) (ratio * newheight);
            Global.dialogIcon = Global.dialogIcon.createScaledBitmap(Global.dialogIcon, newwidth, newheight, true);
        }

    }

    public static void getDimensions(Context context)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Global.height = metrics.heightPixels;
        Global.width = metrics.widthPixels;
        String[] screenData = getScreenDimension(context);
        double inches=Double.parseDouble(screenData[2]);
        Global.buttonWidth=70 * Global.width/100;
        Global.buttonHeight=10 * Global.height/100;
        Global.headerHeight=10 * Global.height/100;
        Global.fontSize=14;
        if(inches>=6.0)  Global.fontSize+=4;
        Global.bigFontSize=Global.fontSize+4;
        Global.smallFontSize=Global.fontSize-2;
        if(Global.grayBack==null) Global.loadIcons(context);
        makeDecimalFormat();
        if(Global.touchListener==null)
            Global.touchListener=new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView view = (ImageView) v;
                            //overlay is black with transparency of 0x77 (119)
                            view.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                            view.invalidate();
                            // menulistener.onClick(v);

                            break;
                        }
                        case MotionEvent.ACTION_UP:

                        case MotionEvent.ACTION_CANCEL: {
                            ImageView view = (ImageView) v;
                            //clear the overlay
                            view.clearColorFilter();
                            view.invalidate();
                            break;
                        }
                    }

                    return false;
                }};
        Global.savePath=Global.getFilepath(context);
    }


    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public static void showAlert(String message,Context context)
    {
        final int dialogWidth=Global.width;
        final int dialogHeight=Global.height/3;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        final AlertDialog dialog=alertDialogBuilder.create();
        dialog.getWindow().setLayout(dialogWidth,dialogHeight);

        LinearLayout l=new LinearLayout(context);
        dialog.setView(l);
        FrameLayout.LayoutParams fparams=new FrameLayout.LayoutParams(dialogWidth,dialogHeight);
        l.setLayoutParams(fparams);

        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.parseColor(Global.backgroundColor));
        alertDialogBuilder.setCancelable(false);
        LinearLayout h=new LinearLayout(context);
        h.setOrientation(LinearLayout.HORIZONTAL);
        h.setBackgroundColor(Color.BLUE);
        l.addView(h);
        LinearLayout.LayoutParams hparams=(LinearLayout.LayoutParams)h.getLayoutParams();
        hparams.width=dialogWidth;
        hparams.height=20 * dialogHeight/100;
        h.setLayoutParams(hparams);
        if(Global.dialogIcon==null) {
            Global.dialogIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.snapserve);
            double ratio = Global.dialogIcon.getWidth() * 1.0 / Global.dialogIcon.getHeight();
            int newheight = 80 * hparams.height / 100;
            int newwidth = (int) (ratio * newheight);
            Global.dialogIcon = Global.dialogIcon.createScaledBitmap(Global.dialogIcon, newwidth, newheight, true);
        }
        ImageView im=new ImageView(context);
        im.setImageBitmap(Global.dialogIcon);
        h.addView(im);

        TextView t=new TextView(context);
        t.setTextColor(Color.WHITE);
        t.setTextSize(Global.bigFontSize);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setText(context.getString(R.string.informationText));
        h.addView(t);

        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        iparams.leftMargin=5 * hparams.width/100;
        iparams.width=Global.dialogIcon.getWidth();
        iparams.height=Global.dialogIcon.getHeight();
        iparams.gravity= Gravity.CENTER;
        im.setLayoutParams(iparams);



        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t.getLayoutParams();
        tparams.height=iparams.height;
        tparams.width=hparams.width/2;
        tparams.gravity=Gravity.CENTER;
        tparams.leftMargin=15 * dialogWidth/100;
        t.setLayoutParams(tparams);

        LinearLayout l1=new LinearLayout(context);
        l.addView(l1);
        LinearLayout.LayoutParams l1params=(LinearLayout.LayoutParams)l1.getLayoutParams();
        l1params.width=dialogWidth;
        l1params.height=40 * dialogHeight/100;
        l1.setLayoutParams(l1params);


        WebView infoText=new WebView(context);
        infoText.setBackgroundResource(R.drawable.roundedittext);
        infoText.getSettings().setJavaScriptEnabled(true);
        infoText.loadDataWithBaseURL(null, message, "text/html", "utf-8", null);
        l1.addView(infoText);

        LinearLayout.LayoutParams infoparams=(LinearLayout.LayoutParams)infoText.getLayoutParams();
        infoparams.topMargin=2 * dialogHeight/100;
        infoparams.leftMargin= 2 * dialogWidth/100;
        infoparams.rightMargin=5 * dialogWidth/100;
        infoparams.width = 80 * dialogWidth/100;
        infoparams.height= 35 * dialogHeight/100;
        infoparams.gravity=Gravity.CENTER;
        infoText.setLayoutParams(infoparams);
        infoText.setPadding(infoparams.width/10,infoparams.height/10,infoparams.width/10,infoparams.height/10);

        Button okButton=new Button(context);
        okButton.setText("OK");
        okButton.setBackgroundResource(R.drawable.roundyellowbutton);
        l.addView(okButton);
        LinearLayout.LayoutParams oparams=(LinearLayout.LayoutParams)okButton.getLayoutParams();
        oparams.topMargin=2 * dialogHeight/100;
        oparams.leftMargin=15 * dialogWidth/100;
        oparams.height=20 * dialogHeight/100;
        oparams.width=50 * dialogWidth/100;
        oparams.bottomMargin= 2 * dialogHeight/100;
        okButton.setLayoutParams(oparams);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++) {
                String ss=Integer.toHexString(0xFF & messageDigest[i]);
                if(ss.length()==1) ss="0"+ss;
                hexString.append(ss);
            }
            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAuthorizationString(JSONObject json)
    {
        if(json.has("token")) {
            try {
                return json.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            String MD5_Hash_String = null;
            try {
                MD5_Hash_String = md5(json.getString("password"));
                return  json.getString("username") + ":" +
                        MD5_Hash_String;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    static JSONObject getPriceDetails(int id)
    {
        JSONObject xx=null;
        for(int i=0;i<Global.productsInPriceLists.length();i++) {

            try {
                xx = Global.productsInPriceLists.getJSONObject(i);
                if (xx.getInt("id") == id)
                    return xx.getJSONObject("pricingDetails");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static double getNetTablePrice(int id)
    {
        JSONObject xx=null;
        for(int i=0;i<Global.productsInPriceLists.length();i++)
        {
            try {
                xx=Global.productsInPriceLists.getJSONObject(i);
                if(xx.getInt("id")==id)
                {
                    JSONObject priceDetails=xx.getJSONObject("pricingDetails");

                    return priceDetails.getDouble("priceTables");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS"," Object was  "+xx+" Exception in price "+e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    static double getAmountVatTable(int id)
    {

        JSONObject xx=null;
        for(int i=0;i<Global.productsInPriceLists.length();i++)
        {
            try {
                xx=Global.productsInPriceLists.getJSONObject(i);
                if(xx.getInt("id")==id)
                {
                    JSONObject priceDetails=xx.getJSONObject("pricingDetails");

                    return priceDetails.getDouble("vatAmountTables");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS"," Object was  "+xx+" Exception in price "+e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    static double getAmountVatTakeAway(int id)
    {

        JSONObject xx=null;
        for(int i=0;i<Global.productsInPriceLists.length();i++)
        {
            try {
                xx=Global.productsInPriceLists.getJSONObject(i);
                if(xx.getInt("id")==id)
                {
                    JSONObject priceDetails=xx.getJSONObject("pricingDetails");
                    System.out.println("GIANNIS VAT IS "+priceDetails.getDouble("vatAmountTakeaway"));
                    return priceDetails.getDouble("vatAmountTakeaway");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS"," Object was  "+xx+" Exception in price "+e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }


    static double getNetTakeAwayPrice(int id)
    {
        JSONObject xx=null;
        for(int i=0;i<Global.productsInPriceLists.length();i++)
        {
            try {
                xx=Global.productsInPriceLists.getJSONObject(i);
                if(xx.getInt("id")==id)
                {
                    JSONObject priceDetails=xx.getJSONObject("pricingDetails");

                    return priceDetails.getDouble("priceTakeaway");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS"," Object was  "+xx+" Exception in price "+e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    static double getProductTablePrice(int id)
    {
        JSONObject xx=null;
        for(int i=0;i<Global.productsInPriceLists.length();i++)
        {
            try {
                xx=Global.productsInPriceLists.getJSONObject(i);
                if(xx.getInt("id")==id)
                {
                    JSONObject priceDetails=xx.getJSONObject("pricingDetails");
                    if(Global.grossornetoption.equals("Net"))
                        return priceDetails.getDouble("priceTables");
                    else
                        return priceDetails.getDouble("grossPriceTables");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS"," Object was  "+xx+" Exception in price "+e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }
    static double getTakeAwayPrice(int id) {
        JSONObject xx = null;
        for (int i = 0; i < Global.productsInPriceLists.length(); i++) {
            try {
                xx = Global.productsInPriceLists.getJSONObject(i);
                if (xx.getInt("id") == id) {
                    JSONObject priceDetails = xx.getJSONObject("pricingDetails");

                    if (Global.grossornetoption.equals("Net"))
                        return priceDetails.getDouble("priceTakeaway");
                    else
                        return priceDetails.getDouble("grossPriceTakeaway");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS", " Object was  " + xx + " Exception in price " + e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }



    static double getProductTakeawayGross(int id )
    {
        JSONObject xx = null;
        for (int i = 0; i < Global.productsInPriceLists.length(); i++) {
            try {
                xx = Global.productsInPriceLists.getJSONObject(i);
                if (xx.getInt("id") == id) {
                    JSONObject priceDetails = xx.getJSONObject("pricingDetails");
                    return priceDetails.getDouble("grossPriceTakeaway");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS", " Object was  " + xx + " Exception in price " + e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    static double getProductTableGross(int id )
    {
        JSONObject xx = null;
        for (int i = 0; i < Global.productsInPriceLists.length(); i++) {
            try {
                xx = Global.productsInPriceLists.getJSONObject(i);
                if (xx.getInt("id") == id) {
                    JSONObject priceDetails = xx.getJSONObject("pricingDetails");

                    return priceDetails.getDouble("grossPriceTables");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS", " Object was  " + xx + " Exception in price " + e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }
    static double getProductTableVat(int id)
    {
        JSONObject xx=null;
        for(int i=0;i<Global.productsInPriceLists.length();i++)
        {
            try {
                xx=Global.productsInPriceLists.getJSONObject(i);
                if(xx.getInt("id")==id)
                {
                    JSONObject priceDetails=xx.getJSONObject("pricingDetails");
                    return priceDetails.getDouble("vatAmountTables");
                }
            } catch (JSONException e) {
                Log.d("GIANNIS"," Object was  "+xx+" Exception in price "+e.getMessage());
                e.printStackTrace();
            }
        }
        return 0.0;
    }




    static JSONArray getProductItemsOfCategory(int catid)
    {
        JSONArray idItems=new JSONArray();
        for(int k=0;k<Global.productItems.length();k++)
        {
            JSONObject item= null;
            try {
                item = Global.productItems.getJSONObject(k);
                if(item.has("active") && item.getBoolean("active")==false) continue;
                if(item.getInt("categoryId")==catid)
                {
                    if(item.getInt("parentItemId")==0)
                        idItems.put(item);
                }
            } catch (JSONException e) {
                Log.d("GIANNIS","EXCEPTION IS "+e.getMessage());
                e.printStackTrace();
            }
        }
        return idItems;
    }

    static JSONArray getSubProductItemsOfCategory(int catid,int parentid)
    {
        JSONArray idItems=new JSONArray();
        for(int k=0;k<Global.productItems.length();k++)
        {
            JSONObject item= null;
            try {
                item = Global.productItems.getJSONObject(k);

                if(item.getInt("categoryId")==catid)
                {
                    if(item.getInt("parentItemId")==parentid)
                        idItems.put(item);
                }
            } catch (JSONException e) {
                Log.d("GIANNIS","EXCEPTION IS "+e.getMessage());
                e.printStackTrace();
            }
        }
        return idItems;
    }


    public static void setHtmlText(TextView t,String text)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            t.setText(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        }

        else
        {
            t.setText(Html.fromHtml(text));
        }
    }

    public static String intToRoman(int num) {
        StringBuilder sb = new StringBuilder();
        int times = 0;
        String[] romans = new String[] { "I", "IV", "V", "IX", "X", "XL", "L",
                "XC", "C", "CD", "D", "CM", "M" };
        int[] ints = new int[] { 1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500,
                900, 1000 };
        for (int i = ints.length - 1; i >= 0; i--) {
            times = num / ints[i];
            num %= ints[i];
            while (times > 0) {
                sb.append(romans[i]);
                times--;
            }
        }
        return sb.toString();
    }

    public static String getCurrentDate()
    {
        String ss="";
        Date d2 = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d2);
    }
    public static String diffDateFrom(String openedAt)
    {
        String ss="";
        Date d1 = null;
        try {
            d1 = new SimpleDateFormat(Global.dateFormat).parse(openedAt);
            Date d2 = new Date(System.currentTimeMillis());
            long diff = d2.getTime() - d1.getTime();
            long days=diff/(1000 * 24 * 60 * 60);
            diff=diff - days * (1000 * 24 * 60 * 60);
            long hours=diff/(1000 * 60 * 60);
            diff=diff-hours * (1000 * 60 * 60);
            long minutes=diff/(1000 * 60);
            diff=diff-minutes * (1000 * 60);
            long seconds=diff/1000;
            if(days==1) ss=days+" day,";
            if(days>1) ss=days+" days,";
            ss=ss+ String.format("%d:%02d:%02d",hours,minutes,seconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ss;
    }


    public static String getHourAndMinute(String openedAt)
    {
        String ss="";
        Date d1 = null;
        try {
            d1 = new SimpleDateFormat(Global.dateFormat).parse(openedAt);
            int hours=d1.getHours();
            int minutes=d1.getMinutes();
            ss=ss+ String.format("%d:%02d",hours,minutes);
        } catch (ParseException e) {

            e.printStackTrace();
            return "N/A";
        }
        return ss;
    }

    public static String hoursDiffFromDate(String openedAt)
    {
        String ss="";
        Date d1 = null;
        try {
            d1 = new SimpleDateFormat(Global.dateFormat).parse(openedAt);
            Date d2 = new Date(System.currentTimeMillis());

            long diff = d2.getTime() - d1.getTime();
            long days=diff/(1000 * 24 * 60 * 60);
            diff=diff - days * (1000 * 24 * 60 * 60);
            long hours=diff/(1000 * 60 * 60);
            return (days*24+hours)+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ss;
    }

    public static int getMeasureWidth(Context ctx,String str,int fontSize)
    {
        Paint paint = new Paint();
        Typeface typeface = ResourcesCompat.getFont(ctx, R.font.roboto);
        paint.setTypeface(typeface);//Typeface.create("sans",Typeface.NORMAL));
        int spSize = fontSize;
        float scaledSizeInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                spSize,
                ctx.getResources().getDisplayMetrics());
        paint.setTextSize(scaledSizeInPixels);

        Rect bounds = new Rect();
        String myString = str;
        paint.getTextBounds(myString, 0, myString.length(), bounds);
        return bounds.width();
    }

    public static int getMeasuredHeight(Context ctx,String str,int fontSize)
    {
        Paint paint = new Paint();
        Typeface typeface = ResourcesCompat.getFont(ctx, R.font.roboto);
        paint.setTypeface(typeface);//Typeface.create("sans",Typeface.NORMAL));
        int spSize = fontSize;
        float scaledSizeInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                spSize,
                ctx.getResources().getDisplayMetrics());
        paint.setTextSize(scaledSizeInPixels);

        Rect bounds = new Rect();
        String myString = str;
        paint.getTextBounds(myString, 0, myString.length(), bounds);
        return bounds.height();
    }

    public static String displayDecimal(double value)
    {
        return ((DecimalFormat) Global.formatter).format(value);
    }


    public static String getLanguageInitials(String s)
    {
        return s.toString().substring(0,2).toLowerCase();
    }


    public static double DoubleNumber(double x)
    {
        return x;
        //return Double.parseDouble(displayDecimal(x));
    }

    public static String getHexColor(int id)
    {
        for(int i=0;i<colors.length();i++)
        {
            try {
                if(colors.getJSONObject(i).getInt("id")==id)
                    return colors.getJSONObject(i).getString("hexCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "#ffffff";
    }
}
