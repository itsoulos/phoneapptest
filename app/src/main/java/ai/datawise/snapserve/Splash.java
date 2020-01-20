package ai.datawise.snapserve;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;


public class Splash extends AppCompatActivity {

    LinearLayout mainLayout=null;
    ImageView image=null;
    private final int SPLASH_DISPLAY_DURATION = 3000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Global.exitCode) {
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Global.getDimensions(this);
        mainLayout=new LinearLayout(this);
        setContentView(mainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundResource(R.drawable.food);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                Splash.this.startActivityForResult(mainIntent,Global.exitCode);
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}
