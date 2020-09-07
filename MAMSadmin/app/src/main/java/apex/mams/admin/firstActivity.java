package apex.mams.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class firstActivity extends AppCompatActivity {
Button btn;
MyPref  myPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPref = new MyPref(firstActivity.this);


        try {

            if (myPref.getKEY_ID().equals("admin")) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, AdminDashboardActivity.class));
                return;
            }

        } catch (NullPointerException e) {

            setContentView(R.layout.activity_first);

        }


        btn= (Button) findViewById(R.id.btn);
    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(firstActivity.this,Loginactivity.class);
            startActivity(i);
        }
    });
    }
}
