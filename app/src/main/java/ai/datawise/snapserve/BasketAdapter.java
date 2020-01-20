package ai.datawise.snapserve;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public  class BasketAdapter extends ArrayAdapter<BasketItem>
{

    private Context context=null;
    private ArrayList<BasketItem> items=new ArrayList<BasketItem>();
    private ArrayList<Integer> isSelected=new ArrayList<Integer>();
    BasketInterface basketInterface=null;
    private boolean headerFlag=true;

    void disableHeader()
    {
        headerFlag=false;
    }

    public BasketAdapter(Context ctx, ArrayList<BasketItem> d,BasketInterface iface)
    {
        super(ctx, 0,d);
        context=ctx;
        basketInterface=iface;
        items=d;
        for(int i=0;i<d.size();i++)
            isSelected.add(new Integer(0));
    }

    public ArrayList<Integer> getIsSelected()
    {
        return isSelected;
    }

    void setQuantity(int pos,int value)
    {
        items.get(pos).setQnt(value);
        notifyDataSetChanged();
    }

    void increaseQuantity(int pos)
    {
        items.get(pos).incQnt();
        notifyDataSetChanged();
    }

    void decreaseQuantity(int pos)
    {
        items.get(pos).decQnt();
        notifyDataSetChanged();
    }

    void updatePrice(int pos,double v)
    {
        items.get(pos).setPrice(v);
        notifyDataSetChanged();
    }
    int selectedItems()
    {
        int k=0;
        for(int i=0;i<isSelected.size();i++)
            k+=isSelected.get(i);
        return k;
    }
    void deselectAll()
    {
        for(int i=0;i<isSelected.size();i++)
            isSelected.set(i,new Integer(0));
        notifyDataSetChanged();
    }

    void setIsSelected(ArrayList<Integer> v)
    {
        for(int i=0;i<isSelected.size();i++)
            isSelected.set(i,new Integer(v.get(i)));
        notifyDataSetChanged();
    }

    void selectAll()
    {
        for(int i=0;i<isSelected.size();i++)
            isSelected.set(i,new Integer(1));
        notifyDataSetChanged();
    }

    public void putNotes(int pos,String n)
    {
        items.get(pos).setNotes(n);
        notifyDataSetChanged();
    }


    public String getNotes(int pos)
    {
        return items.get(pos).getNotes();
    }

    boolean isExtraItem(int position)
    {
        return items.get(position).getIsExtra();
    }

    int   getExtraId(int position)
    {
        return items.get(position).getExtraId();
    }

    void disableItem(int pos)
    {
        isSelected.set(pos,2);
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        int totalWidth=Global.width;
        int totalHeight=2 * Global.headerHeight/3;
        int textColor= Color.parseColor(Global.textColor);
        if(isSelected.get(position)==2)
            textColor=Color.parseColor("#babcc4");
        LinearLayout l=new LinearLayout(context);

        LinearLayout.LayoutParams bbparams=new LinearLayout.LayoutParams(0,0);
        bbparams.leftMargin=0;

        if(items.get(position).getIsExtra()==true)
        {
            ImageView bb=new ImageView(context);
            bb.setImageBitmap(Global.rightEnter);
            l.addView(bb);
            bbparams.leftMargin=2*Global.width/100;
            bbparams.width=Global.rightEnter.getWidth();
            bbparams.height=Global.rightEnter.getHeight();
            bbparams.topMargin=10 * totalHeight/100;
            bb.setLayoutParams(bbparams);
        }

        TextView t1=new TextView(context);
        t1.setText((position==0 && headerFlag)?context.getString(R.string.itemsText):items.get(position).getName());
        t1.setTextSize(items.get(position).getIsExtra()?Global.smallFontSize:Global.fontSize);
        t1.setTextColor((position==0 && headerFlag)?Color.parseColor("#babcc4"):textColor);
        l.addView(t1);

        if(items.get(position).getSplit()!=0)
        {
            TextView splitText=new TextView(context);
            l.addView(splitText);
            splitText.setTypeface(Typeface.DEFAULT_BOLD);
            splitText.setBackgroundResource(R.drawable.yellowbutton);
            splitText.setTextColor(Color.parseColor(Global.textColor));
            splitText.setText("("+items.get(position).getSplit()+")");
            LinearLayout.LayoutParams s1params=(LinearLayout.LayoutParams)splitText.getLayoutParams();
            s1params.width=5 * totalWidth/100;
            s1params.gravity=Gravity.CENTER;
            splitText.setLayoutParams(s1params);
        }


        LinearLayout.LayoutParams t1params=(LinearLayout.LayoutParams)t1.getLayoutParams();
        t1params.leftMargin=5 * totalWidth/100;
        t1params.topMargin=10 * totalHeight/100;
        t1params.height=70 * totalHeight/100;
        int actualHeight= 8 * Global.getMeasuredHeight(context,t1.getText().toString(),Global.fontSize)/7;
        if(actualHeight>t1params.height)
            t1params.height=actualHeight;
        t1params.width=40 * totalWidth/100;
        t1params.bottomMargin=10 *totalHeight/100;
        t1params.gravity= Gravity.CENTER;
        t1.setLayoutParams(t1params);

        TextView t2=new TextView(context);
        t2.setText((position==0 && headerFlag)?context.getString(R.string.quantityText):items.get(position).getQnt()+"");
        t2.setTextSize(Global.fontSize);
        t2.setTextColor(t1.getCurrentTextColor());
        l.addView(t2);
        LinearLayout.LayoutParams t2params=(LinearLayout.LayoutParams)t2.getLayoutParams();
        t2params.topMargin=t1params.topMargin;
        t2params.leftMargin=2*t1params.leftMargin-bbparams.leftMargin-bbparams.width;
        t2params.height=t1params.height;
        t2params.width=15 * totalWidth/100;
        t2params.bottomMargin=10 *totalHeight/100;
        t2.setLayoutParams(t2params);

        ImageView notesView=new ImageView(context);
        notesView.setImageBitmap(Global.noimageforlisticon);
        if(items.get(position).getNotes().length()!=0)
            notesView.setImageBitmap(Global.notesforlisticon);
        l.addView(notesView);
        LinearLayout.LayoutParams nparams=(LinearLayout.LayoutParams)notesView.getLayoutParams();
        nparams.width=Global.noimageforlisticon.getWidth();
        nparams.height=t2params.height;
        nparams.topMargin=t2params.topMargin/2;
        nparams.leftMargin=t1params.leftMargin/2;


        TextView t3=new TextView(context);
        t3.setText((position==0 && headerFlag)?context.getString(R.string.priceText):Global.currency+Global.displayDecimal(items.get(position).getPrice()));
        t3.setTextSize(Global.fontSize);
        t3.setTextColor(t1.getCurrentTextColor());
        l.addView(t3);
        LinearLayout.LayoutParams t3params=(LinearLayout.LayoutParams)t3.getLayoutParams();
        t3params.topMargin=t2params.topMargin;
        t3params.height=t2params.height;
        t3params.rightMargin=t1params.leftMargin;
        t3params.width=20 * totalWidth/100;
        t3params.bottomMargin=10 *totalHeight/100;
        t3params.gravity=Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        t3params.leftMargin=Global.width-nparams.width-nparams.leftMargin-bbparams.width-bbparams.leftMargin-t1params.leftMargin-t1params.width-t2params.leftMargin-t2params.width-t3params.width-t3params.rightMargin;
        if(items.get(position).getSplit()!=0)
            t3params.leftMargin-=5*totalWidth/100;
        t3.setLayoutParams(t3params);
        t3.setGravity(Gravity.RIGHT);
        l.setClickable(true);
        if (isSelected.get(position) == 1)
            l.setBackgroundColor(Color.parseColor(Global.listSelectColor));
        else
            l.setBackgroundColor(Color.WHITE);
        if(items.get(position).getHasChaged() && isSelected.get(position)==0)
            l.setBackgroundColor(Color.parseColor("#ec407a"));
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0 && headerFlag) return;
                if (items.get(position).getIsExtra() == true)
                {
                    int extraId=items.get(position).getExtraId();
                    for(int i=0;i<items.size();i++)
                    {
                        if(items.get(i).getId()==extraId && isSelected.get(i)==1)
                        {
                            if(isSelected.get(position)==1) isSelected.set(position,0);
                            else isSelected.set(position,1);
                            if(isSelected.get(position)==1)
                                v.setBackgroundColor(Color.parseColor(Global.listSelectColor));
                            else {
                                if(items.get(position).getHasChaged())
                                    v.setBackgroundColor(Color.parseColor("#ec407a"));
                                v.setBackgroundColor(Color.WHITE);
                            }
                        }
                    }
                } else {
                    if (isSelected.get(position) == 0) {
                        isSelected.set(position, 1);
                        v.setBackgroundColor(Color.parseColor(Global.listSelectColor));
                    }
                    else
                    if(isSelected.get(position)==1){
                        isSelected.set(position, 0);
                        //deselect extras
                        if(items.get(position).getHasChaged())
                            v.setBackgroundColor(Color.parseColor("#ec407a"));
                        else
                            v.setBackgroundColor(Color.WHITE);
                        boolean passed=false;
                        for(int i=0;i<items.size();i++)
                        {
                            if(i==position) continue;

                            if(items.get(i).getIsExtra()==true && isSelected.get(i)==1 && items.get(i).getExtraId()==items.get(position).getId())
                            {
                                isSelected.set(i,0);
                                passed=true;
                            }
                        }
                        if(passed) notifyDataSetChanged();
                    }
                    basketInterface.onClickFunction();
                }
            }
        });
        return l;
    }


}