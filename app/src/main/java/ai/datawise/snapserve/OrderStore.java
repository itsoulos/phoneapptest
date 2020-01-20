package ai.datawise.snapserve;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderStore
{
    private String notes="";
    private int pagerId=0;
    private int orderId=0;
    private int waiterId=0;
    private JSONObject orderInfo=new JSONObject();
    boolean hasPrinted=true;
    private final String amountNetString="amountNet";
    private  final String amountDiscountString="amountDiscount";
    private final String amountTotalString="amountTotal";
    private boolean isTakeOutFlag;
    private boolean partialPaid;


    public OrderStore()
    {
        JSONArray details=new JSONArray();
        try {
            orderInfo.put("details",details);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hasPrinted=true;
        isTakeOutFlag=false;
        partialPaid=false;
    }

    public int getSplitType()
    {
        try {
            return orderInfo.has("splitType")?orderInfo.getInt("splitType"):0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public JSONObject getOrderInfo() {
        return orderInfo;
    }

    void enablePartialPaid()
    {
        partialPaid=true;
    }

    public boolean getPartialPaid()
    {
        return partialPaid;
    }

    void enableTakeOut()
    {
        isTakeOutFlag=true;
    }

    void setOrderId(int x)
    {
        orderId=x;
    }

    int getOrderId()
    {
        return orderId;
    }

    void setWaiterId(int x)
    {
        waiterId=x;
    }

    int  getWaiterId()
    {
        return waiterId;
    }

    boolean itemHasExtras(int pos)
    {
        try {
            JSONObject x=orderInfo.getJSONArray("details").getJSONObject(pos);
            int extraCategoryId=x.getInt("extraCategoryId");
            if(extraCategoryId!=0) return true;
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    int getSize()
    {
        int k=0;
        try {
            k=orderInfo.getJSONArray("details").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return k;
    }
    int orderCount()
    {
        int k=0;
        try
        {
            for(int i=0;i<orderInfo.getJSONArray("details").length();i++)
            {
                k+=orderInfo.getJSONArray("details").getJSONObject(i).getInt("quantity");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return k;
    }

    JSONObject itemAtPos(int pos)
    {
        JSONObject x=null;
        try {
            x=orderInfo.getJSONArray("details").getJSONObject(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }
    void setOrderInfo(JSONObject item)
    {
        orderInfo=item;

    }

    JSONObject getOrderInfoChanged()
    {
        JSONObject copyOrderInfo= new JSONObject();
        try {
            copyOrderInfo=new JSONObject(orderInfo.toString());
            JSONArray details=copyOrderInfo.getJSONArray("details");
            JSONArray copyDetails=new JSONArray();
            for(int i=0;i<details.length();i++)
            {
                JSONObject x=details.getJSONObject(i);

                boolean flag=x.getBoolean("hasChanged");

                if(flag)
                {
                    x.remove("hasChanged");
                    x.put("currentItemPos",i);
                    copyDetails.put(x);
                }
            }
            copyOrderInfo.put("details",copyDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return copyOrderInfo;
    }

    double getPriceOfItem(int id)
    {
        double v=0.0;
        if(isTakeOutFlag) {
            v = Global.getNetTakeAwayPrice(id);

        }
        else
            v=Global.getNetTablePrice(id);

        return v;
    }


    double getVatPercentOfItem(int id)
    {
        JSONObject price=Global.getPriceDetails(id);
        try {
            if(isTakeOutFlag==false)
                return Global.DoubleNumber(price.isNull("vatTables")?0.0:price.getDouble("vatTables"));
            else
                return Global.DoubleNumber(price.isNull("vatTakeaway")?0.0:price.getDouble("vatTakeaway"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    int getPricelistId(int id)
    {
        JSONObject price=Global.getPriceDetails(id);
        try {
            return price.getInt("priceListId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    void addItemToOrder(JSONObject item)
    {
        hasPrinted=false;
        JSONObject tempItem=new JSONObject();
        try {

            tempItem.put("itemName",item.getString("itemName"));
            tempItem.put("itemId",item.getInt("id"));
            tempItem.put("quantity",1);
            tempItem.put(amountNetString,getPriceOfItem(item.getInt("id")));
            tempItem.put("vatPercent",getVatPercentOfItem(item.getInt("id")));
            tempItem.put("unitTypeId",(int)1);
            tempItem.put("split",(int)0);
            tempItem.put("notes","");
            tempItem.put(amountDiscountString,0.00);
            tempItem.put("priceListId",getPricelistId(item.getInt("id")));
            tempItem.put("discountType",0);
            tempItem.put("hasChanged",true);
            if(isTakeOutFlag)
                tempItem.put("amountVat",
                        Global.getAmountVatTakeAway(item.getInt("id")));
            else
                tempItem.put("amountVat",
                        Global.getAmountVatTable(item.getInt("id")));


            if(isTakeOutFlag)
                tempItem.put(amountTotalString,Global.getProductTakeawayGross(tempItem.getInt("itemId")));
            else
                tempItem.put(amountTotalString,Global.getProductTableGross(tempItem.getInt("itemId")));

            if(!item.has("extraCategoryId"))
                tempItem.put("extraCategoryId",0);
            else
                tempItem.put("extraCategoryId",item.isNull("extraCategoryId")?0:item.getInt("extraCategoryId"));


            if(!item.has("extraOptionsId"))
                tempItem.put("extraOptionsId",0);
            else
                tempItem.put("extraOptionsId",item.isNull("extraOptionsId")?0:item.getInt("extraOptionsId"));

            tempItem.put("split",getOrderSplit());
            /*
            String ename="extraCategoryId";
            if(!item.has(ename))
                ename="extraOptionsId";
            if(item.isNull(ename))
                tempItem.put("extraOptionsId",0);
            else
            tempItem.put("extraOptionsId",item.getInt(ename));
            */
            JSONArray extras=new JSONArray();
            tempItem.put("extras",extras);
        } catch (JSONException e) {
        }

        try {

            orderInfo.getJSONArray("details").put(tempItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean hasTakeout()
    {
        return isTakeOutFlag;
    }

    void putValueDiscount(int pos,double value)
    {
       // if(isPrinted(pos)) return;

        try {
            JSONArray details=orderInfo.getJSONArray("details");
            JSONObject item=details.getJSONObject(pos);
            double d1=costfOfItemForDiscount(pos);
            double d2=costOfExtrasForDiscount(pos);
            System.out.println("GIANNIS COST FOR VALUE DISCOUNT IS "+value+" ...  "+d1+" ... "+d2);

            d1=d1-d2;
            double discount1=Global.DoubleNumber(value * d1 / (d1+d2));
            double discount2=value-discount1;
            JSONArray extras=item.getJSONArray("extras");

            if(Global.grossornetoption.equals("Gross"))
            {
                item.put(amountTotalString,item.getInt("quantity")*Global.getProductTablePrice(item.getInt("itemId"))-discount1);
                if(isTakeOutFlag)
                    item.put(amountTotalString,item.getInt("quantity")*Global.getTakeAwayPrice(item.getInt("itemId"))-discount1);
                item.put(amountNetString,item.getDouble(amountTotalString)/(1.0+item.getDouble("vatPercent")));
                item.put("amountVat",item.getDouble(amountTotalString)-item.getDouble(amountNetString));
                item.put(amountDiscountString,discount1);
                if (discount1 == 0)
                    item.put("discountType", 0);
                else
                    item.put("discountType", 1);
                double sumd2 = 0.0;
                for (int i = 0; i < extras.length()-1; i++) {
                    JSONObject e = extras.getJSONObject(i);
                    double dvalue=0.0;
                    if(isTakeOutFlag)
                        dvalue=Global.getTakeAwayPrice(extras.getJSONObject(i).getInt("itemId"))*extras.getJSONObject(i).getInt("quantity");
                    else
                        dvalue=Global.getProductTablePrice(extras.getJSONObject(i).getInt("itemId"))*extras.getJSONObject(i).getInt("quantity");
                    double amountDiscount=discount2 * dvalue/d2;
                    sumd2+=amountDiscount;
                    e.put(amountTotalString,e.getInt("quantity")*Global.getProductTablePrice(e.getInt("itemId"))-amountDiscount);
                    if(isTakeOutFlag)
                        e.put(amountTotalString,e.getInt("quantity")*Global.getTakeAwayPrice(e.getInt("itemId"))-amountDiscount);
                    e.put(amountNetString,e.getDouble(amountTotalString)/(1.0+e.getDouble("vatPercent")));
                    e.put("amountVat",e.getDouble(amountTotalString)-e.getDouble(amountNetString));
                    if (amountDiscount == 0)
                        e.put("discountType", 0);
                    else
                        e.put("discountType", 1);
                    e.put(amountDiscountString,amountDiscount);
                    extras.put(i,e);
                }
                double remaind2 = discount2 - sumd2;
                if(extras.length()!=0) {
                    JSONObject e = extras.getJSONObject(extras.length() - 1);
                    double dvalue=0.0;
                    if(isTakeOutFlag)
                        dvalue=Global.getTakeAwayPrice(e.getInt("itemId"))*e.getInt("quantity");
                    else
                        dvalue=Global.getProductTablePrice(e.getInt("itemId"))*e.getInt("quantity");

                    e.put(amountTotalString,e.getInt("quantity")*Global.getProductTablePrice(e.getInt("itemId"))-remaind2);
                    if(isTakeOutFlag)
                        e.put(amountTotalString,e.getInt("quantity")*Global.getTakeAwayPrice(e.getInt("itemId"))-remaind2);
                    e.put(amountNetString,e.getDouble(amountTotalString)/(1.0+e.getDouble("vatPercent")));
                    e.put("amountVat",e.getDouble(amountTotalString)-e.getDouble(amountNetString));
                    if (remaind2 == 0)
                        e.put("discountType", 0);
                    else
                        e.put("discountType", 1);
                    e.put(amountDiscountString,remaind2);

                    extras.put(extras.length() - 1, e);
                }
            }
            else {
                item.put(amountNetString, item.getInt("quantity") * Global.getProductTablePrice(item.getInt("itemId")));
                if (isTakeOutFlag)
                    item.put(amountNetString, item.getInt("quantity") * Global.getTakeAwayPrice(item.getInt("itemId")));
                item.put("amountVat", (item.getDouble(amountNetString) - discount1) * item.getDouble("vatPercent"));
                item.put(amountTotalString, item.getDouble(amountNetString) + item.getDouble("amountVat") - discount1);
                item.put(amountDiscountString, discount1);
                double sumd2 = 0.0;
                for (int i = 0; i < extras.length()-1; i++)
                {
                    JSONObject e = extras.getJSONObject(i);
                    e.put(amountNetString,e.getInt("quantity")*Global.getProductTablePrice(e.getInt("itemId")));
                    if(isTakeOutFlag)
                        e.put(amountNetString,e.getInt("quantity")*Global.getTakeAwayPrice(e.getInt("itemId")));
                    double amountDiscount=discount2 * e.getDouble(amountNetString)/d2;
                    sumd2+=amountDiscount;
                    e.put(amountDiscountString,amountDiscount);
                    e.put("amountVat",(e.getDouble(amountNetString) - amountDiscount) * e.getDouble("vatPercent"));
                    e.put(amountTotalString, e.getDouble(amountNetString) + e.getDouble("amountVat") - amountDiscount);
                    if (amountDiscount == 0)
                        e.put("discountType", 0);
                    else
                        e.put("discountType", 1);
                    extras.put(i,e);
                }
                double remaind2 = discount2 - sumd2;
                if(extras.length()!=0)
                {
                    JSONObject e = extras.getJSONObject(extras.length() - 1);
                    e.put(amountNetString,e.getInt("quantity")*Global.getProductTablePrice(e.getInt("itemId")));
                    if(isTakeOutFlag)
                        e.put(amountNetString,e.getInt("quantity")*Global.getTakeAwayPrice(e.getInt("itemId")));

                    e.put(amountDiscountString,remaind2);
                    e.put("amountVat",(e.getDouble(amountNetString) - remaind2) * e.getDouble("vatPercent"));
                    e.put(amountTotalString, e.getDouble(amountNetString) + e.getDouble("amountVat") - remaind2);
                    if (remaind2 == 0)
                        e.put("discountType", 0);
                    else
                        e.put("discountType", 1);
                    extras.put(extras.length() - 1, e);

                }

            }
            item.put("extras",extras);
            details.put(pos,item);
            System.out.println("GIANNIS EXTRAS  IS AFTER DISCOUNT "+item);
            orderInfo.put("details",details);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void putPercentDiscount(int pos,double percent)
    {
     //   if(isPrinted(pos)) return;
        try {

            JSONArray details=orderInfo.getJSONArray("details");
            JSONObject item=details.getJSONObject(pos);

            //se groos pairno to grossPriceTables
            double grossValue=Global.getProductTableGross(item.getInt("itemId"));
            if(isTakeOutFlag)
                grossValue=Global.getProductTakeawayGross(item.getInt("itemId"));

            double df1= item.getInt("quantity")*percent*grossValue;
            item.put("amountDiscount",df1);
            item.put("amountTotal",item.getInt("quantity")*grossValue-item.getDouble(amountDiscountString));
            item.put(amountNetString,item.getDouble(amountTotalString)/(1+item.getDouble("vatPercent")));
            item.put("amountVat",item.getDouble(amountTotalString)-item.getDouble(amountNetString));

            /*double df=Global.DoubleNumber(percent * item.getDouble("amountNet"));
            double dtax =Global.DoubleNumber( item.getDouble("vatPercent")*(item.getDouble(amountNetString)-df));
            item.put("amountDiscount", df);
            item.put("amountVat",dtax);
            item.put("amountTotal",item.getDouble("amountNet")-item.getDouble("amountDiscount")+dtax);
            */
            JSONArray extras=item.getJSONArray("extras");
            for(int i=0;i<extras.length();i++)
            {
                JSONObject e=extras.getJSONObject(i);

              /*  df1=Global.DoubleNumber(percent * e.getDouble("amountNet"));
                e.put("amountDiscount", df1);

                double dtax1 = Global.DoubleNumber( e.getDouble("vatPercent")*(e.getDouble("amountNet")-e.getDouble("amountDiscount")));
                e.put("amountVat",dtax1);
                e.put("amountTotal",e.getDouble("amountNet")-e.getDouble("amountDiscount")+dtax1);*/

                grossValue=Global.getProductTableGross(e.getInt("itemId"));
                if(isTakeOutFlag)
                    grossValue=Global.getProductTakeawayGross(e.getInt("itemId"));
                df1= e.getInt("quantity")*percent*grossValue;
                e.put("amountDiscount",df1);
                e.put("amountTotal",e.getInt("quantity")*grossValue-e.getDouble(amountDiscountString));
                e.put(amountNetString,e.getDouble(amountTotalString)/(1+e.getDouble("vatPercent")));
                e.put("amountVat",e.getDouble(amountTotalString)-e.getDouble(amountNetString));
                extras.put(i,e);

                if(percent==0.0)
                    e.put("discountType",0);
                else
                    e.put("discountType",1);

            }
            item.put("extras",extras);
            if(percent==0)
                item.put("discountType",0);
            else
                item.put("discountType",1);
            details.put(pos,item);
            System.out.println("GIANNIS PUT ITEM IS "+item);
            orderInfo.put("details",details);
        } catch (JSONException e) {
            System.out.println("GIANNIS FAIL IS "+e);
            e.printStackTrace();
        }
    }

    public boolean isPrinted(int pos)
    {
        JSONArray details= null;
        try {
            details = orderInfo.getJSONArray("details");
            JSONObject item=details.getJSONObject(pos);
            if(item.has("printStatus") && item.getInt("printStatus")==1) return true;
        } catch (JSONException e) {
            System.out.println("GIANNIS ERROR E "+e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


    public void treat(int pos)
    {
       // if(isPrinted(pos)) return;
        double percent=1.0;
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            JSONObject item=details.getJSONObject(pos);

            //se groos pairno to grossPriceTables

            double grossValue=Global.getProductTableGross(item.getInt("itemId"));
            if(isTakeOutFlag)
                grossValue=Global.getProductTakeawayGross(item.getInt("itemId"));
            double df1= item.getInt("quantity")*percent*grossValue;
            item.put("amountDiscount",df1);
            item.put("amountTotal",item.getInt("quantity")*grossValue-item.getDouble(amountDiscountString));
            item.put(amountNetString,item.getDouble(amountTotalString)/(1+item.getDouble("vatPercent")));
            item.put("amountVat",item.getDouble(amountTotalString)-item.getDouble(amountNetString));

            /*double df=Global.DoubleNumber(percent * item.getDouble("amountNet"));
            double dtax =Global.DoubleNumber( item.getDouble("vatPercent")*(item.getDouble(amountNetString)-df));
            item.put("amountDiscount", df);
            item.put("amountVat",dtax);
            item.put("amountTotal",item.getDouble("amountNet")-item.getDouble("amountDiscount")+dtax);
            */
            JSONArray extras=item.getJSONArray("extras");

            for(int i=0;i<extras.length();i++)
            {
                JSONObject e=extras.getJSONObject(i);
              /*  df1=Global.DoubleNumber(percent * e.getDouble("amountNet"));
                e.put("amountDiscount", df1);
                double dtax1 = Global.DoubleNumber( e.getDouble("vatPercent")*(e.getDouble("amountNet")-e.getDouble("amountDiscount")));
                e.put("amountVat",dtax1);
                e.put("amountTotal",e.getDouble("amountNet")-e.getDouble("amountDiscount")+dtax1);
                extras.put(i,e);
                e.put("discountType",2);

        */

                grossValue=Global.getProductTableGross(e.getInt("itemId"));
                if(isTakeOutFlag)
                    grossValue=Global.getProductTakeawayGross(e.getInt("itemId"));
                df1= e.getInt("quantity")*percent*grossValue;
                e.put("amountDiscount",df1);
                e.put("amountTotal",e.getInt("quantity")*grossValue-e.getDouble(amountDiscountString));
                e.put(amountNetString,e.getDouble(amountTotalString)/(1+e.getDouble("vatPercent")));
                e.put("amountVat",e.getDouble(amountTotalString)-e.getDouble(amountNetString));
                extras.put(i,e);
                e.put("discountType",2);
            }
            item.put("extras",extras);

            item.put("discountType",2);
            details.put(pos,item);
            orderInfo.put("details",details);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    double allOrdersCost()
    {
        double value=0.0;

        try {
            for(int i=0;i<orderInfo.getJSONArray("details").length();i++) {
                value += costOfItem(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    double allOrdersCost(ArrayList<BasketItem> items)
    {
        double value=0.0;
        for(int i=0;i<items.size();i++)
        {
            if(items.get(i).getIsExtra()) continue;
            int pos=items.get(i).currentItemPos;
            value += costOfItem(pos);
        }
        return value;
    }


    double costOfExtrasForDiscount(int item)
    {
        double sum=0.0;
        try {
            JSONArray extras=orderInfo.getJSONArray("details").getJSONObject(item).getJSONArray("extras");
          //  if(isPrinted(item)) return 0.0;
            for(int i=0;i<extras.length();i++)
            {
                if(isTakeOutFlag)
                    sum+=Global.getTakeAwayPrice(extras.getJSONObject(i).getInt("itemId"))*extras.getJSONObject(i).getInt("quantity");
                else
                    sum+=Global.getProductTablePrice(extras.getJSONObject(i).getInt("itemId"))*extras.getJSONObject(i).getInt("quantity");;
            }
        } catch (JSONException e) {
            System.out.println("GIANNIS E "+e.getMessage());
            e.printStackTrace();
        }
        return sum;
    }

    double costfOfItemForDiscount(int item)
    {
        try {
    //        if(isPrinted(item)) return 0.0;
            JSONObject x=orderInfo.getJSONArray("details").getJSONObject(item);
            double d1=Global.getProductTablePrice(x.getInt("itemId"));
            if(isTakeOutFlag)
                d1=Global.getTakeAwayPrice(x.getInt("itemId"))*x.getInt("quantity");
            double d2=costOfExtrasForDiscount(item);
            return d1+d2;
        } catch (JSONException e) {
            System.out.println("GIANNIS E "+e.getMessage());

        }
        return 0.0;
    }


    public String getTableCode()
    {
        try {
            return orderInfo.getString("tableCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public int getOpenedBy()
    {
        try {
            return orderInfo.getInt("openedBy");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
    double allOrderCostForDiscount()
    {
        double value=0.0;

        try {
            for(int i=0;i<orderInfo.getJSONArray("details").length();i++) {
             //   if(isPrinted(i)) continue;
                value += costfOfItemForDiscount(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    double costOfItem(int item)
    {
        try {
            JSONObject x=orderInfo.getJSONArray("details").getJSONObject(item);
            double d1=x.getDouble(amountTotalString)-x.getDouble("amountVat");
            double d2=costOfExtras(item);
            return d1+d2;
        } catch (JSONException e) {
        }
        return 0.0;

    }

    void removeItem(int pos)
    {
        hasPrinted=false;
        try {
            orderInfo.getJSONArray("details").remove(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void removeAll()
    {
        try {
            orderInfo.put("details",new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void removeExtrasItem(int pos,int extraid)
    {
        hasPrinted=false;
        try {
            JSONObject x=orderInfo.getJSONArray("details").getJSONObject(pos);
            JSONArray extras=x.getJSONArray("extras");
            for(int i=0;i<extras.length();i++)
            {
                if(extras.getJSONObject(i).getInt("id")==extraid)
                {
                    extras.remove(i);
                }
            }
            x.put("extras",extras);
            orderInfo.getJSONArray("details").put(pos,x);
            updateCostOfItem(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setNotes(int pos,String n)
    {
        hasPrinted=false;
        try {
            orderInfo.getJSONArray("details").getJSONObject(pos).put("notes",n);
        } catch (JSONException e) {
            System.out.println("GIANNIS ERROR IN NOTES "+e.getMessage());
            e.printStackTrace();
        }
    }

    public String getNotes(int pos)
    {
        try {

            String n=orderInfo.getJSONArray("details").getJSONObject(pos).getString("notes");
            return  n;
        } catch (JSONException e) {
            System.out.println("GIANNIS ERROR IN NOTES "+e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public String getNotes()
    {
        try {
            if(orderInfo.getString("notes")==null)
                return "";
            return orderInfo.getString("notes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setNotes(String n)
    {

        hasPrinted=false;
        notes=n;
        try {
            orderInfo.put("notes",notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPager(int id)
    {
        hasPrinted=false;
        pagerId=id;
        try {
            orderInfo.put("pagerId",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSplit(int k)
    {
        try {
            orderInfo.put("split",k);
            orderInfo.put("splitType",k>0?1:0);
        } catch (JSONException e) {
            System.out.println("GIANNIS E "+e.getMessage());

            e.printStackTrace();
        }
    }

    public void setSplit(int k,int pos)
    {
        try {
            orderInfo.getJSONArray("details").getJSONObject(pos).put("split",k);
        } catch (JSONException e) {
            System.out.println("GIANNIS E "+e.getMessage());
            e.printStackTrace();
        }
    }

    public int getOrderSplit()
    {
        if(!orderInfo.has("split")) return 0;
        try {
            return orderInfo.getInt("split");
        } catch (JSONException e) {
            System.out.println("GIANNIS E "+e.getMessage());

            e.printStackTrace();
        }
        return 0;
    }

    public int getItemSplit(int pos)
    {
        try {
            JSONObject xx=orderInfo.getJSONArray("details").getJSONObject(pos);
            if(!xx.has("split")) return 0;
            return xx.getInt("split");
        } catch (JSONException e) {
            System.out.println("GIANNIS E "+e.getMessage());

            e.printStackTrace();
        }
        return 0;
    }

    boolean itemCanChange(int pos)
    {
        try {
            if(orderInfo.getJSONArray("details").getJSONObject(pos).getBoolean("hasChanged")==true)
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    double costOfExtras(int item)
    {
        double sum=0.0;
        try {
            JSONArray extras=orderInfo.getJSONArray("details").getJSONObject(item).getJSONArray("extras");
            for(int i=0;i<extras.length();i++)
            {
                sum+=extras.getJSONObject(i).getDouble(amountTotalString)-extras.getJSONObject(i).getDouble("amountVat");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sum;
    }

    void removeLastItem() {
        hasPrinted=false;
        int s = 0;
        try {
            s = orderInfo.getJSONArray("details").length();
            orderInfo.getJSONArray("details").remove(s - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void updateCostOfItem(int pos)
    {
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            JSONObject x=details.getJSONObject(pos);
            int id=x.getInt("itemId");
            int q=x.getInt("quantity");

            if(isTakeOutFlag)
                x.put(amountTotalString,Global.DoubleNumber(Global.getProductTakeawayGross(id)*q));
            else
                x.put(amountTotalString,Global.DoubleNumber(Global.getProductTableGross(id)*q));

            x.put(amountNetString,(x.getDouble(amountTotalString)-x.getDouble(amountDiscountString))/(1.0+x.getDouble("vatPercent")));
            x.put("amountVat", x.getDouble(amountTotalString)-x.getDouble(amountNetString));



            JSONArray extras=orderInfo.getJSONArray("details").getJSONObject(pos).getJSONArray("extras");
            for(int i=0;i<extras.length();i++)
            {
                JSONObject xx=extras.getJSONObject(i);
                int id2=xx.getInt("itemId");
                int q2=q;//xx.getInt("quantity");



                if(isTakeOutFlag)
                    xx.put(amountTotalString,Global.DoubleNumber(Global.getProductTakeawayGross(id2)*q2));
                else
                    xx.put(amountTotalString,Global.DoubleNumber(Global.getProductTableGross(id2)*q2));
                xx.put("quantity",q2);
                xx.put(amountNetString,(xx.getDouble(amountTotalString)-xx.getDouble(amountDiscountString))/(1.0+xx.getDouble("vatPercent")));
                double v22=xx.getDouble(amountDiscountString);
                xx.put("amountVat", xx.getDouble(amountTotalString)-xx.getDouble(amountNetString));

                extras.put(i,xx);
            }
            x.put("extras",extras);

            details.put(pos,x);
            orderInfo.put("details",details);



        } catch (JSONException e) {
            System.out.println("GIANNIS EXCEPTION  WAS "+e.getMessage());
            e.printStackTrace();
        }
    }

    double taxOfItem(int pos)
    {
        double sumTax=0.0;
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            JSONObject x=details.getJSONObject(pos);
            sumTax+=x.getDouble("amountVat");
            JSONArray extras=orderInfo.getJSONArray("details").getJSONObject(pos).getJSONArray("extras");
            for(int i=0;i<extras.length();i++)
            {
                sumTax+=extras.getJSONObject(i).getDouble("amountVat");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sumTax;
    }

    double actualCost(int pos)
    {
        double sumTax=0.0;
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            JSONObject x=details.getJSONObject(pos);
            sumTax+=x.getDouble(amountTotalString);
            JSONArray extras=orderInfo.getJSONArray("details").getJSONObject(pos).getJSONArray("extras");
            for(int i=0;i<extras.length();i++)
            {
                sumTax+=extras.getJSONObject(i).getDouble(amountTotalString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sumTax;
    }

    void increaseExtraItem(int pos,int extraid)
    {
        hasPrinted=false;
        try {
            JSONObject x=orderInfo.getJSONArray("details").getJSONObject(pos);
            JSONArray extras=x.getJSONArray("extras");
            boolean passed=false;
            for(int j=0;j<extras.length();j++)
            {
                if(extras.getJSONObject(j).getInt("id")==extraid)
                {
                    int q=extras.getJSONObject(j).getInt("quantity");
                    q=q+1;
                    extras.getJSONObject(j).put("quantity",q);
                    passed=true;
                    extras.getJSONObject(j).put("amountTotal",
                            extras.getJSONObject(j).getDouble("amountNet")*extras.getJSONObject(j).getInt("quantity")-
                                    extras.getJSONObject(j).getDouble("amountDiscount"));
                }
            }
            if(passed) {
                x.put("extras", extras);
                orderInfo.getJSONArray("details").put(pos, x);
                orderInfo.getJSONArray("details").getJSONObject(pos).put("hasChanged",true);
                updateCostOfItem(pos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void decreaseExtraItem(int pos,int extraid)
    {
        hasPrinted=false;
        try {
            JSONObject x=orderInfo.getJSONArray("details").getJSONObject(pos);
            JSONArray extras=x.getJSONArray("extras");
            boolean passed=false;
            for(int j=0;j<extras.length();j++)
            {
                if(extras.getJSONObject(j).getInt("id")==extraid)
                {
                    int q=extras.getJSONObject(j).getInt("quantity");
                    q=q-1;
                    if(q<=0) q=1;
                    extras.getJSONObject(j).put("quantity",q);
                    passed=true;
                    extras.getJSONObject(j).put("amountTotal",
                            extras.getJSONObject(j).getDouble("amountNet")*extras.getJSONObject(j).getInt("quantity")-
                                    extras.getJSONObject(j).getDouble("amountDiscount"));
                }
            }
            if(passed) {
                x.put("extras", extras);
                orderInfo.getJSONArray("details").put(pos, x);
                orderInfo.getJSONArray("details").getJSONObject(pos).put("hasChanged",true);
                updateCostOfItem(pos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void increaseItem(int pos)
    {
        hasPrinted=false;
        try {
            if(orderInfo.getJSONArray("details").getJSONObject(pos).has("discountType")
                    && orderInfo.getJSONArray("details").getJSONObject(pos).getInt("discountType")!=0) return;
            int q=orderInfo.getJSONArray("details").getJSONObject(pos).getInt("quantity");
            q=q+1;
            orderInfo.getJSONArray("details").getJSONObject(pos).put("quantity",q);
            orderInfo.getJSONArray("details").getJSONObject(pos).put("hasChanged",true);
            updateCostOfItem(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void decreaseItem(int pos)
    {
        hasPrinted=false;
        try {
            if(orderInfo.getJSONArray("details").getJSONObject(pos).has("discountType")
                    && orderInfo.getJSONArray("details").getJSONObject(pos).getInt("discountType")!=0) return;
            int q=orderInfo.getJSONArray("details").getJSONObject(pos).getInt("quantity");
            q=q-1;
            if(q<=0) q=1;
            orderInfo.getJSONArray("details").getJSONObject(pos).put("quantity",q);
            orderInfo.getJSONArray("details").getJSONObject(pos).put("hasChanged",true);
            updateCostOfItem(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void changeItemQuantity(int pos,int value)
    {
        hasPrinted=false;
        try {
            int q=orderInfo.getJSONArray("details").getJSONObject(pos).getInt("quantity");
            q=value;
            if(q<=0) q=1;
            orderInfo.getJSONArray("details").getJSONObject(pos).put("quantity",q);
            orderInfo.getJSONArray("details").getJSONObject(pos).put("hasChanged",true);
            updateCostOfItem(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void addExtrasToOrder(int item,JSONObject extras)
    {
        hasPrinted=false;
        try {
            orderInfo.getJSONArray("details").getJSONObject(item).getJSONArray("extras").put(extras);
            orderInfo.getJSONArray("details").getJSONObject(item).put("hasChanged",true);
            updateCostOfItem(item);
        } catch (JSONException e) {
            System.out.println("GIANNIS EXTRAS ERROR "+e.getMessage());
            e.printStackTrace();
        }
    }

    void removeLastExtras(int item)
    {
        hasPrinted=false;
        int size= 0;
        try {
            size = orderInfo.getJSONArray("details").getJSONObject(item).getJSONArray("extras").length();
            orderInfo.getJSONArray("details").getJSONObject(item).put("hasChanged",true);
            orderInfo.getJSONArray("details").getJSONObject(item).getJSONArray("extras").remove(size-1);
            updateCostOfItem(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean getHasPrinted()
    {

        try {
            for(int i=0;i<orderInfo.getJSONArray("details").length();i++) {
                JSONObject x = orderInfo.getJSONArray("details").getJSONObject(i);
                if (x.getBoolean("hasChanged")) return false;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    ArrayList<Integer> getExtraIds(int item)
    {
        ArrayList<Integer> x=new ArrayList<Integer>();
        JSONObject xx = null;
        try {
            xx = orderInfo.getJSONArray("details").getJSONObject(item);
            JSONArray extras=xx.getJSONArray("extras");
            for(int i=0;i<extras.length();i++)
                x.add(extras.getJSONObject(i).getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }
    ArrayList<BasketItem> getBasketItemsNoExtras()
    {
        ArrayList<BasketItem> bt=new ArrayList<BasketItem>();
        JSONArray details=null;
        try {
            details=orderInfo.getJSONArray("details");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for(int i=0;i<details.length();i++) {
                JSONObject x= details.getJSONObject(i);
                int id = x.getInt("itemId");
                String name = x.getString("itemName");
                int qnt = x.getInt("quantity");
                double price = x.getDouble("amountTotal");

                BasketItem b = new BasketItem(id, i, name, qnt, price,x.has("split")?x.getInt("split"):0);

                String ss=x.getString("notes");
                b.setNotes(ss);
                b.setHasChanged(x.has("hasChanged")?x.getBoolean("hasChanged"):false);
                bt.add(b);
            }
        } catch (JSONException e) {
            System.out.println("GIANNIS DSTORE ERROR "+e.getMessage());
            e.printStackTrace();
        }
        return bt;
    }

    ArrayList<BasketItem> getBasketItemsOfSplit(int k)
    {
        ArrayList<BasketItem> bt=new ArrayList<BasketItem>();
        JSONArray details=null;
        try {
            details=orderInfo.getJSONArray("details");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            for(int i=0;i<details.length();i++) {
                JSONObject x= details.getJSONObject(i);
                int id = x.getInt("itemId");
                String name = x.getString("itemName");
                int qnt = x.getInt("quantity");
                double price = x.getDouble("amountTotal");
                BasketItem b = new BasketItem(id, i, name, qnt, price,x.has("split")?x.getInt("split"):0);

                String ss=x.getString("notes");
                b.setNotes(ss);
                b.setHasChanged(x.has("hasChanged")?x.getBoolean("hasChanged"):false);
                if(x.has("split") && x.getInt("split")==k)  bt.add(b);
            }
        } catch (JSONException e) {
            System.out.println("GIANNIS DSTORE ERROR "+e.getMessage());
            e.printStackTrace();
        }
        return bt;
    }

    ArrayList<BasketItem> getBasketItems()
    {
        ArrayList<BasketItem> bt=new ArrayList<BasketItem>();
        JSONArray details=null;
        try {
            details=orderInfo.getJSONArray("details");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bt.add(new BasketItem(0,0,"",0,0,0));
        try {
            for(int i=0;i<details.length();i++) {
                JSONObject x= details.getJSONObject(i);
                int id = x.getInt("itemId");
                String name = x.getString("itemName");
                int qnt = x.getInt("quantity");
                double price = x.getDouble("amountTotal");
                BasketItem b = new BasketItem(id, i, name, qnt, price,x.has("split")?x.getInt("split"):0);

                String ss=x.getString("notes");
                b.setNotes(ss);
                b.setHasChanged(x.has("hasChanged")?x.getBoolean("hasChanged"):false);
                bt.add(b);

                JSONArray extras = x.getJSONArray("extras");
                for (int j = 0; j < extras.length(); j++) {
                    String extraOptionsName = "";
                    for (int k = 0; k < Global.productExtras.length(); k++) {
                        JSONObject xx = Global.productExtras.getJSONObject(k);

                        String ename="extraOptionsId";
                        if (extras.getJSONObject(j).has(ename) &&
                                extras.getJSONObject(j).get(ename)!=JSONObject.NULL
                                && xx.getInt("id") == extras.getJSONObject(j).getInt(ename)) {
                            extraOptionsName = xx.getString("name");
                            break;
                        }
                    }
                    String name2 = extraOptionsName + " " + extras.getJSONObject(j).getString("itemName");
                    int qnt2 = extras.getJSONObject(j).getInt("quantity");
                    double price2 = extras.getJSONObject(j).getDouble("amountTotal");
                    BasketItem b2 = new BasketItem(extras.getJSONObject(j).getInt("itemId"), i,
                            name2, qnt2, price2, id,0);
                    String ss2=extras.getJSONObject(j).getString("notes");
                    b2.setNotes(ss2);
                    b2.setHasChanged(x.has("hasChanged")?x.getBoolean("hasChanged"):false);
                    bt.add(b2);
                }
            }
        } catch (JSONException e) {
            System.out.println("GIANNIS DSTORE ERROR "+e.getMessage());
            e.printStackTrace();
        }
        return bt;
    }

    void print(JSONArray copyDetails,JSONArray response)
    {
        hasPrinted=true;
        if(response==null) return;
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            for(int i=0;i<details.length();i++)
            {
                details.getJSONObject(i).put("hasChanged",false);
            }
            orderInfo.put("details",details);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    int getSplit()
    {

        try {
            if(orderInfo.has("split"))
                return orderInfo.getInt("split");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
    boolean allItemsHaveCashier()
    {
        int icount=0;
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            for(int i=0;i<details.length();i++)
            {
                JSONObject x=details.getJSONObject(i);
                if(x.has("cashierStatus") && x.getInt("cashierStatus")==1) icount++;
            }
            if(icount==details.length()) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String toString()
    {
        return orderInfo.toString();
    }

    void billOrder()
    {
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            for(int i=0;i<details.length();i++)
            {
                JSONObject x=details.getJSONObject(i);
                if(x.has("cashierStatus") && x.getInt("cashierStatus")==0)
                {
                    x.put("cashierStatus",1);
                    details.put(i,x);
                }
            }
            orderInfo.put("details",details);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    double allActualCost()
    {
        double sum=0.0;
        try {
            JSONArray details=orderInfo.getJSONArray("details");
            for(int i=0;i<details.length();i++)
                sum+=actualCost(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  sum;
    }
}
