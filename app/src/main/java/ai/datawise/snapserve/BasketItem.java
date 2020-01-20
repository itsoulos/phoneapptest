package ai.datawise.snapserve;

public class BasketItem
{
    private int id;
    private String name;
    private int qnt;
    private double price;
    boolean isExtra;
    int     extraId;
    int     currentItemPos;
    private String notes;
    private boolean hasChanged=false;
    private int split;
    BasketItem(int i,int c,String n,int q,double p,int msplit)
    {
        id=i;
        currentItemPos=c;
        name=n;
        qnt=q;
        price=p;
        isExtra=false;
        extraId=id;
        notes="";
        split=msplit;
    }

    public void setHasChanged(boolean b)
    {
        hasChanged=b;
    }

    public boolean getHasChaged()
    {
        return hasChanged;
    }

    BasketItem(int i,int c,String n,int q,double p,int eid,int msplit)
    {
        id=i;
        currentItemPos=c;
        name=n;
        qnt=q;
        price=p;
        isExtra=true;
        extraId=eid;
        notes="";
        split=msplit;
    }

    public  int getSplit()
    {
        return  split;
    }

    public void setNotes(String n)
    {
        notes=n;
    }

    public String getNotes()
    {
        return notes;
    }

    int getExtraId()
    {
        return  extraId;
    }

    boolean getIsExtra()
    {
        return isExtra;
    }

    int getCurrentItemPos()
    {
        return currentItemPos;
    }

    int getId()
    {
        return id;
    }

    String getName()
    {
        return name;
    }

    int getQnt()
    {
        return qnt;
    }
    int incQnt() { qnt++; return qnt;}
    int decQnt() {qnt--;if(qnt==0) qnt=1; return qnt;}
    void setQnt(int v) {qnt =v ;}
    void setPrice(double v)
    {
        price=v;
    }
    double getPrice()
    {
        return price;
    }

    public String toString()
    {
        return id+","+currentItemPos+","+name+","+qnt+","+price+","+isExtra+","+extraId;
    }
}