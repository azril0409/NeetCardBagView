package sample.neetoffice.com.neetcardbagview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
        Toast.makeText(this,"textView onClick",Toast.LENGTH_SHORT).show();
    }
}
