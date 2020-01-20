package ai.datawise.snapserve;

import android.os.AsyncTask;

public class LongOperation extends AsyncTask<String, Void, String> {
    boolean ok=true;
    final static int timeout=15000;
    private TableClass mclass;
    public LongOperation(TableClass mx)
    {
        mclass=mx;
    }
    @Override
    protected String doInBackground(String... params) {

        while(ok) {
            try {
                if(Global.loginCredentials==null)
                {
                    ok=false;
                    break;
                }
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        mclass.updateTableView(mclass.getCurrentLevel(),-1);

    }

    void terminate()
    {
        ok=false;
    }
}

