package apex.mams.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edt_oldpassword, edt_password, edt_cpassword;
    Button btn_changePassword;
    TextView forgetpassword;
    MyPref myPref;
    Getip getip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        myPref = new MyPref(getApplicationContext());
        getip = new Getip(getApplicationContext());
        edt_oldpassword = (EditText) findViewById(R.id.edt_oldpassword);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_cpassword = (EditText) findViewById(R.id.edt_cpassword);
        forgetpassword = (TextView) findViewById(R.id.forgetpassword);
        btn_changePassword = (Button) findViewById(R.id.btn_changePassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                i.putExtra("Sel", "2");
                startActivity(i);
            }
        });
        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int flag = 0;
                if (edt_oldpassword.getText().toString().equals("")) {
                    edt_oldpassword.setError("Fill The Field");
                    flag = 1;
                }
                if (edt_password.getText().toString().equals("")) {
                    edt_password.setError("Fill The Field");
                    flag = 1;
                }
                if (edt_cpassword.getText().toString().equals("")) {
                    edt_cpassword.setError("Fill The Field");
                    flag = 1;
                }


                if (flag == 0) {

                    int flag2 = 0;
                    if (!edt_oldpassword.getText().toString().equals(myPref.getPASS_ID())) {
                        edt_oldpassword.setError("Wrong Old Password");
                        flag2 = 1;
                    }

                    if (!edt_password.getText().toString().equals(edt_cpassword.getText().toString())) {
                        edt_cpassword.setError("Confirm Password Mismatch");
                        flag2 = 1;
                    }

                    if (edt_password.getText().toString().equals(edt_oldpassword.getText().toString())) {
                        edt_password.setError("Old Password is same as New Password");
                        flag2 = 1;
                    }

                    if (flag2 == 0) {
                        change_Password();
                    }
                }
            }
        });


    }


    public void change_Password() {

        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Changing Password");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/change_password.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int i = jsonObject.getInt("status");

                    if (i == 0) {
                        Toast.makeText(getApplicationContext(), "Not Changed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (i == 1) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Successfully Password Changed", Toast.LENGTH_SHORT).show();


                        myPref.ClearAll();
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("mobno", myPref.getMobile());
                map.put("pass", edt_password.getText().toString());
                return map;
            }
        };

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
