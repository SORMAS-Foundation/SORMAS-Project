package org.technologybord.tryoutbutterknife;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindArray;
import butterknife.BindBool;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindFloat;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView1) TextView textView1;
    @BindView(R.id.textView2) TextView textView2;
    @BindView(R.id.imageView1) ImageView imageView1;
    @BindView(R.id.button1) Button button1;

    @BindString(R.string.message1) String message1;
    @BindString(R.string.message2) String message2;

    @BindDimen(R.dimen.dpvalue1) float dpValue1;
    @BindDimen(R.dimen.dpvalue2) int dpValue2;

    @BindFloat(R.dimen.float_value) float floatValue1;
    @BindFloat(R.dimen.float_15_32) float floatValue_15_32;
    @BindFloat(R.dimen.float_value_in_floats) float float_value_in_floats;
    //@BindFloat(R.dimen.percent_15) float floatValue_15_percent; //IMPORTANT: Not working

    @BindBool(R.bool.boolValue) boolean boolValue;
    @BindInt(R.integer.integerValue) int integerValue;

    @BindArray(R.array.items) String[] arrayItems;

    @BindDrawable(R.drawable.ic_done_black_24dp) Drawable doneDrawable;
    @BindColor(R.color.pink) int pink; // int or ColorStateList field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        textView1.setText("Technology Board");

        textView2.setText("Creative Approach. Unique Result.");
        textView2.setTextColor(pink);

        imageView1.setImageDrawable(doneDrawable);
    }


    @OnClick(R.id.button1)
    public void onClick(View v) {
        Toast.makeText(this, "Hello from Technology Board!", Toast.LENGTH_LONG).show();
        Toast.makeText(this, message1, Toast.LENGTH_LONG).show();
        Toast.makeText(this, String.format("Exact: %1$10.2f, Pixel: %2$d", dpValue1, dpValue2), Toast.LENGTH_LONG).show();

        //%2$4d - For integers it introduces left padding
        //%1$.2f - For float; it displays in 2 decimal place
        //%1$.1f - For float; it displays in 1 decimal place and rounds up also
        //%1$1f - For float; it displays the full float value without round or reducing the decimal places
        //%1$10.2f - For float; ensures the value is displayed 10 spaces wide


        // The '(' numeric flag may be used to format negative numbers with
        // parentheses rather than a minus sign.  Group separators are
        // automatically inserted.
        /*formatter.format("Amount gained or lost since last statement: $ %(,.2f",
                balanceDelta);*/
        // -> "Amount gained or lost since last statement: $ (6,217.58)"

        // Writes a formatted string to System.out.
        //System.out.format("Local time: %tT", Calendar.getInstance());
        // -> "Local time: 13:34:18"
    }
}
