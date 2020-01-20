package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TablePageAdapter extends PagerAdapter {
    Context context=null;
    private int pageWidth=0,pageHeight=0;
    private JSONArray mtables=new JSONArray();
    private  String selectedTable="";
    private ArrayList<Button> blist=new ArrayList<Button>();

    public String getSelectedTable()
    {
        return selectedTable;
    }


    TablePageAdapter(Context ctx,int pw,int ph)
    {
        context=ctx;
        pageWidth=pw;
        pageHeight=ph;
    }

    @Override
    public int getCount() {
        return Global.levels.length();
    }

    void updateTableView(final int levelid, final TableLayout table)
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
                            table.removeAllViews();
                            final int itemsPerRow=3;
                            int nrows=mtables.length()/itemsPerRow;
                            if(nrows * itemsPerRow<mtables.length()) nrows++;
                            int idcount=0;
                            for(int j=0;j<nrows;j++)
                            {
                                if(idcount>=mtables.length()) break;
                                TableRow r=new TableRow(context);
                                table.addView(r);
                                TableLayout.LayoutParams rparams=(TableLayout.LayoutParams)r.getLayoutParams();
                                rparams.width=pageWidth;
                                rparams.height=14 *pageHeight/100;
                                r.setLayoutParams(rparams);
                                blist.clear();
                                for(int k=0;k<itemsPerRow;k++)
                                {
                                    final Button bt=new Button(context);
                                    blist.add(bt);
                                    bt.setTextSize(Global.fontSize);
                                    bt.setTextColor(Color.parseColor(Global.textColor));
                                    bt.setText(mtables.getJSONObject(idcount).getString("tableCode"));
                                    bt.setTag(bt.getText());
                                    bt.setId(1000+idcount);
                                    if(mtables.getJSONObject(idcount).getBoolean("isOpen")==true)
                                    {
                                        bt.setBackgroundResource(R.drawable.blueroundbutton);
                                        bt.setTextColor(Color.WHITE);
                                    }
                                    else
                                    {
                                        bt.setBackgroundResource(R.drawable.transparentbutton);
                                        bt.setTextColor(Color.parseColor(Global.textColor));
                                    }
                                    r.addView(bt);
                                    TableRow.LayoutParams btparams=(TableRow.LayoutParams)bt.getLayoutParams();
                                    btparams.leftMargin=2 *pageWidth/100;
                                    btparams.width=28 * pageWidth/100;
                                    btparams.height=12 *pageHeight/100;
                                    btparams.topMargin=2 *pageHeight/100;
                                    bt.setLayoutParams(btparams);
                                    bt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                //if(mtables.getJSONObject(bt.getId()-1000).getBoolean("isOpen")==false)
                                                {
                                                    selectedTable = (String) bt.getTag();
                                                    System.out.println("GIANNIS SELECTED TABLE IS "+selectedTable);
                                                    Button b = (Button) v;
                                                    b.setBackgroundResource(R.drawable.blueroundbutton);
                                                    b.setTextColor(Color.WHITE);
                                                    for (int m = 0; m < blist.size(); m++) {
                                                        if (!blist.get(m).getTag().equals(selectedTable)) {
                                                            int mid=blist.get(m).getId()-1000;
                                                            if(mtables.getJSONObject(mid).getBoolean("isOpen")==true)
                                                            {
                                                                blist.get(m).setBackgroundResource(R.drawable.blueroundbutton);
                                                                blist.get(m).setTextColor(Color.WHITE);
                                                            }
                                                            else
                                                            {
                                                                blist.get(m).setBackgroundResource(R.drawable.transparentbutton);
                                                                blist.get(m).setTextColor(Color.parseColor(Global.textColor));
                                                            }

                                                        }
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    idcount++;
                                    if(idcount>=mtables.length()) break;
                                }
                            }
                            // updateTableViewSecond(levelid);
                        } catch (JSONException e) {
                            System.out.println("GIANNIS JSON ERROR "+e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Global.showAlert(context.getString(R.string.executionProblemText)+" Fetch Tables",context);

                    }
                });
    }

    LinearLayout makeLevelLayout(int level)
    {
        LinearLayout l=new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        ScrollView scroll=new ScrollView(context);
        l.addView(scroll);
        LinearLayout.LayoutParams sparams=(LinearLayout.LayoutParams)scroll.getLayoutParams();
        sparams.width=pageWidth;
        sparams.height=pageHeight;
        scroll.setLayoutParams(sparams);

        TableLayout table=new TableLayout(context);
        scroll.addView(table);
        ScrollView.LayoutParams tparams=(ScrollView.LayoutParams)table.getLayoutParams();
        tparams.width=sparams.width;
        tparams.height=sparams.height;
        table.setLayoutParams(tparams);

        try {
            updateTableView(Global.levels.getJSONObject(level).getInt("id"),table);
        } catch (JSONException e) {
            System.out.println("GIANNIS LEVEL ERROR "+e.getMessage());
            e.printStackTrace();
        }
        return l;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {

        LinearLayout l = makeLevelLayout(position);
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
}
