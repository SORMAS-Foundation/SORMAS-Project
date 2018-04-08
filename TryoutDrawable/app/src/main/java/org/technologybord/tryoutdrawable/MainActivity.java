package org.technologybord.tryoutdrawable;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView imageView1 = null;
    Button button1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = (ImageView)findViewById(R.id.imageView1);
        button1 = (Button)findViewById(R.id.button1);

        final Drawable d1 = getResources().getDrawable(R.drawable.level_01).mutate();
        imageView1.setImageDrawable(d1);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxLevel = d1.getLevel();

                if (maxLevel == 0) {
                    d1.setLevel(1);
                } else {
                    d1.setLevel(0);
                }
            }
        });
    }
}
