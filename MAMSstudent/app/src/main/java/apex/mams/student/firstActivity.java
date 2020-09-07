package apex.mams.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class firstActivity extends AppCompatActivity {

    Button btn;
    MyPref myPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        myPref=new MyPref(getApplicationContext());
        try {

            if (myPref.getKEY_ID().equals("Student")) {

                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, StudentDashboardActivity.class));
                return;
            }
        } catch (NullPointerException e) {
            setContentView(R.layout.activity_first);
        }


        btn= (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(firstActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}

