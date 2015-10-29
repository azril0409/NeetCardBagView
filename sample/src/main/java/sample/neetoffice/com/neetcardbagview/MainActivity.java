package sample.neetoffice.com.neetcardbagview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import library.neetoffice.com.neetcardbagview.CardBagView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardBagView cardBagView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardBagView = (CardBagView) findViewById(R.id.cardBagView);
        cardBagView.setAdapter(new Adapter(this));
        findViewById(R.id.textView).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "textView onClick", Toast.LENGTH_SHORT).show();
    }
}
