package ai.datawise.snapserve;

public interface DialPadListener
{
    void cancelClicked();
    void acceptClicked();
    void onChange(String text);
}
