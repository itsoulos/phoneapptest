package ai.datawise.snapserve;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NewOrderActivity extends AppCompatActivity {

    LinearLayout mainLayout=null;
    LinearLayout header=null;
    LinearLayout.LayoutParams hparams;
    Spinner spinner=null;
    EditText tableCodeText=null;
    EditText numberOfPersonsText=null;
    Button cancelButton,continueButton;
    LinearLayout buttonLayout=null;
    LinearLayout.LayoutParams sparams;
    LinearLayout.LayoutParams tlparams;
    LinearLayout.LayoutParams eparams;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Global.exitCode) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    void makeHeaderView()
    {
        header=new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(header);
        hparams=(LinearLayout.LayoutParams)header.getLayoutParams();
        hparams.width=Global.width;
        hparams.height=Global.headerHeight;
        header.setLayoutParams(hparams);
        header.setBackgroundResource(R.drawable.backwithborder);


        ImageView im=new ImageView(this);
        im.setImageBitmap(Global.grayBack);
        im.setClickable(true);
        im.setOnTouchListener(Global.touchListener);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        header.addView(im);
        LinearLayout.LayoutParams iparams=(LinearLayout.LayoutParams)im.getLayoutParams();
        iparams.leftMargin=5 * hparams.width/100;
        iparams.width=Global.grayBack.getWidth();
        iparams.height=hparams.height;
        iparams.gravity= Gravity.CENTER;
        im.setLayoutParams(iparams);

        TextView t1=new TextView(this);
        t1.setTextSize(Global.bigFontSize);
        t1.setTextColor(Color.parseColor(Global.textColor));
        t1.setText(getString(R.string.neworderText));
        header.addView(t1);
        LinearLayout.LayoutParams tparams=(LinearLayout.LayoutParams)t1.getLayoutParams();
        tparams.leftMargin=2* iparams.leftMargin;
        tparams.width=LinearLayout.LayoutParams.WRAP_CONTENT;
        tparams.height=hparams.height;
        tparams.gravity=Gravity.CENTER;
        t1.setLayoutParams(tparams);
        t1.setGravity(Gravity.CENTER);
    }


    void makeEdits()
    {
        ArrayList<String> langArray=new ArrayList<String>();
        langArray.add("English");
        langArray.add("Greek");
        PosSpinnerAdapter adapter=new PosSpinnerAdapter(this,langArray);
        spinner=new Spinner(this);
        spinner.setBackgroundResource(R.drawable.spinnerbg);
        spinner.setPrompt("Language:");
        spinner.setAdapter(adapter);
        mainLayout.addView(spinner);
        sparams=(LinearLayout.LayoutParams)spinner.getLayoutParams();
        sparams.leftMargin=10 * Global.width/100;
        sparams.width=80 * Global.width/100;
        sparams.topMargin=10 * Global.height/100;
        sparams.height=2*Global.buttonHeight/3;
        spinner.setLayoutParams(sparams);

        LinearLayout editLayout=new LinearLayout(this);
        editLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(editLayout);
        eparams=(LinearLayout.LayoutParams)editLayout.getLayoutParams();
        eparams.width=Global.width;
        eparams.topMargin=Global.height/100;
        eparams.height=3 * Global.buttonHeight;
        editLayout.setLayoutParams(eparams);

        TextInputLayout tl1=new TextInputLayout(this);
        tl1.setOrientation(TextInputLayout.VERTICAL);
        editLayout.addView(tl1);

        tlparams=(LinearLayout.LayoutParams)tl1.getLayoutParams();
        tlparams.leftMargin=sparams.leftMargin;
        tlparams.width=sparams.width;
        tlparams.topMargin=sparams.topMargin/4;
        tlparams.height=Global.buttonHeight;
        tl1.setLayoutParams(tlparams);

        tableCodeText=new EditText(this);
        tableCodeText.setHint(getString(R.string.tableCodeText));
        tableCodeText.setBackgroundResource(R.drawable.roundedittext);
        tableCodeText.setInputType(InputType.TYPE_CLASS_TEXT);
        tableCodeText.setHintTextColor(Color.parseColor(Global.hintColor));
        tableCodeText.setTextColor(Color.parseColor(Global.textColor));
        tableCodeText.setPadding(Global.width/30,Global.buttonHeight/4,Global.width/30,Global.buttonHeight/4);

        tl1.addView(tableCodeText);

        TextInputLayout tl2=new TextInputLayout(this);
        tl2.setOrientation(TextInputLayout.VERTICAL);
        editLayout.addView(tl2);
        tl2.setLayoutParams(tlparams);

        numberOfPersonsText=new EditText(this);
        numberOfPersonsText.setHint(getString(R.string.numberOfPersonsText));
        numberOfPersonsText.setBackgroundResource(R.drawable.roundedittext);
        numberOfPersonsText.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberOfPersonsText.setHintTextColor(Color.parseColor(Global.hintColor));
        numberOfPersonsText.setTextColor(Color.parseColor(Global.textColor));
        numberOfPersonsText.setPadding(Global.width/30,Global.buttonHeight/4,Global.width/30,Global.buttonHeight/4);
        tl2.addView(numberOfPersonsText);

        numberOfPersonsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(count!=0 && tableCodeText.getText().length()!=0)
                {
                    buttonLayout.setVisibility(View.VISIBLE);
                }
                else
                    buttonLayout.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });

        tableCodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(count!=0 && numberOfPersonsText.getText().length()!=0)
                {
                    buttonLayout.setVisibility(View.VISIBLE);
                }
                else
                    buttonLayout.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });
    }

    void makeActualOrder(final String tablecoode, int persons, String language, String workDate)
    {
        JSONObject jsonBody=new JSONObject();
        try {
            jsonBody.put("tableCode",tablecoode);
            jsonBody.put("workdate",workDate);
            jsonBody.put("persons",persons);
            jsonBody.put("levelId",Global.levels.getJSONObject(Global.levelIndex).getInt("id"));
            jsonBody.put("language",Global.getLanguageInitials(language));
            jsonBody.put("customerId",1);
            jsonBody.put("customerCard",0);
            jsonBody.put("posTerminalId",Global.loginCredentials.getInt("terminalid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        InternetClass.ObjectPOSTCall(this, Global.server + "/orders/tables", jsonBody, Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
            @Override
            public void onResponse(JSONObject response) {

                Intent I=new Intent(NewOrderActivity.this, OpenOrderActivity.class);
                I.putExtra("tableCode",tablecoode);
                I.putExtra("isTakeOut",false);
                JSONObject table=new JSONObject();
                try {
                    table.put("tableCode",tablecoode);
                    table.put("isOpen",true);
                    table.put("openedAt","");
                    table.put("order",new JSONObject());
                    table.getJSONObject("order").put("orderId",response.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                I.putExtra("tableJson",table.toString());

                NewOrderActivity.this.startActivityForResult(I,Global.exitCode);
                /*
                InternetClass.ObjectGETCall(NewOrderActivity.this, Global.server + "/orders/tables/" + tablecoode,
                        Global.getAuthorizationString(Global.loginCredentials), new JSONObjectListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent I=new Intent(NewOrderActivity.this, OpenOrderActivity.class);
                        I.putExtra("tableCode",tablecoode);
                        I.putExtra("isTakeOut",false);
                        I.putExtra("tableJson",response.toString());
                        NewOrderActivity.this.startActivityForResult(I,Global.exitCode);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                       OrderCloseDialog dialog=new OrderCloseDialog(NewOrderActivity.this,error+ "  "+NewOrderActivity.this.getString(R.string.tableExistsMessage),
                               NewOrderActivity.this.getString(R.string.okGotItText),"");
                       dialog.show();
                    }
                });
                */

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                OrderCloseDialog dialog=new OrderCloseDialog(NewOrderActivity.this,NewOrderActivity.this.getString(R.string.tableExistsMessage),
                        NewOrderActivity.this.getString(R.string.okGotItText),"");
                dialog.show();
            }
        });
    }

    void makeButtons()
    {
        buttonLayout=new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(buttonLayout);
        LinearLayout.LayoutParams bparams=(LinearLayout.LayoutParams)buttonLayout.getLayoutParams();
        bparams.width=Global.width;
        bparams.bottomMargin=Global.height/40;
        bparams.height=Global.buttonHeight+4 * bparams.bottomMargin;
        bparams.topMargin=Global.height-Global.headerHeight-sparams.topMargin-sparams.height-eparams.topMargin-eparams.height-
                bparams.height-2*bparams.bottomMargin;
        buttonLayout.setLayoutParams(bparams);

        buttonLayout.setVisibility(View.GONE);

        cancelButton=new Button(this);
        cancelButton.setTextSize(Global.fontSize);
        cancelButton.setText(getString(R.string.cancel_text));
        cancelButton.setTextColor(Color.parseColor(Global.textColor));
        cancelButton.setBackgroundResource(R.drawable.transparentbutton);
        buttonLayout.addView(cancelButton);
        LinearLayout.LayoutParams cparams=(LinearLayout.LayoutParams)cancelButton.getLayoutParams();
        cparams.leftMargin=5 *bparams.width/100;
        cparams.height=Global.buttonHeight;
        cparams.width=42 *bparams.width/100;
        cancelButton.setLayoutParams(cparams);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        continueButton=new Button(this);
        continueButton.setTextSize(Global.fontSize);
        continueButton.setText(getString(R.string.continuteToOrderText));
        continueButton.setTextColor(Color.parseColor(Global.textColor));
        continueButton.setBackgroundResource(R.drawable.roundyellowbutton);
        buttonLayout.addView(continueButton);
        continueButton.setLayoutParams(cparams);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tableCodeText.getText().toString().length()==0 ||
                        numberOfPersonsText.getText().toString().length()==0)
                {
                    return;
                }

                makeActualOrder(tableCodeText.getText().toString(),Integer.parseInt(numberOfPersonsText.getText().toString()),
                        spinner.getSelectedItem().toString(),Global.workdate);
                    /*
                InternetClass.ArrayGETCall(NewOrderActivity.this, Global.server + "/setup/options?k=store.workDate",
                        Global.getAuthorizationString(Global.loginCredentials),
                        new JSONArrayListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    JSONObject x0=response.getJSONObject(0);
                                    String workDate=x0.getString("optionValue");
                                    makeActualOrder(tableCodeText.getText().toString(),Integer.parseInt(numberOfPersonsText.getText().toString()),
                                            spinner.getSelectedItem().toString(),workDate);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Global.showAlert(getString(R.string.executionProblemText)+" Get WorkDate",NewOrderActivity.this);
                            }
                        });*/

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainLayout=new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        setContentView(mainLayout);

        makeHeaderView();

        makeEdits();
        makeButtons();
    }

    public void onBackPressed()
    {
        //nothing
    }
}
