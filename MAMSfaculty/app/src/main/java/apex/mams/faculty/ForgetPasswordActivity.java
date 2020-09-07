package apex.mams.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ForgetPasswordActivity extends AppCompatActivity {

    Getip getip;
    MyPref myPref;

    String Sel;

    EditText edt_mobno, edt_otp;
    Button btn_verify, btn_sendotp;

    EditText edt_pass, edt_cpass;
    Button btn_chngpass;

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;

    boolean mVerificationInProgress = false;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendOTP;

    String mobno;
    String ReceivedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        edt_mobno = findViewById(R.id.edt_mobno);
        edt_otp = findViewById(R.id.edt_otp);
        btn_sendotp = findViewById(R.id.btn_sendotp);
        btn_verify = findViewById(R.id.btn_verify);

        edt_pass = findViewById(R.id.edt_pass);
        edt_cpass = findViewById(R.id.edt_cpass);
        btn_chngpass = findViewById(R.id.btn_chngpass);

        final Intent i = getIntent();
        Sel = i.getStringExtra("Sel");

        getip = new Getip(this);
        myPref = new MyPref(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();



        mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mVerificationInProgress = false;
                Toast.makeText(ForgetPasswordActivity.this, "Verification Complete!", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(ForgetPasswordActivity.this, "Verification Failed" + e, Toast.LENGTH_LONG).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(ForgetPasswordActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(ForgetPasswordActivity.this, "Too many Requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(ForgetPasswordActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                mResendOTP = forceResendingToken;

                mVerificationId = s;
                ReceivedOTP = s;
            }
        };

        btn_sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_mobno.getText().toString().equals("")) {
                    edt_mobno.setError("Empty Field !");
                } else if (Sel.equals("2")) {
                    if (edt_mobno.getText().toString().equals(myPref.getMobile())) {
                        SendSMS();
                    } else {
                        edt_mobno.setError("Invalid Number !");
                    }
                } else if (Sel.equals("1")) {
                    SendSMS();
                }

            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                VerifyOTP();
            }
        });

        btn_chngpass.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (edt_pass.getText().toString().equals("")) {
                    edt_pass.setError("Empty Field !");
                } else if (!edt_cpass.getText().toString().equals(edt_pass.getText().toString())) {
                    edt_cpass.setError("Password Mismatch !");
                } else {
                    ChangePassword();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void SendSMS() {
        mobno = edt_mobno.getText().toString();
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91" + mobno, 60, TimeUnit.SECONDS, this, mcallback
            );
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                btn_sendotp.setEnabled(false);
                btn_sendotp.setText("Resend (" + millisUntilFinished / 1000 + ")");
            }

            public void onFinish() {
                btn_sendotp.setEnabled(true);
                btn_sendotp.setText("Resend");
            }
        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPasswordActivity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
                            edt_mobno.setEnabled(false);
                            edt_otp.setVisibility(View.GONE);
                            btn_sendotp.setVisibility(View.GONE);
                            btn_verify.setVisibility(View.GONE);
                            edt_pass.setVisibility(View.VISIBLE);
                            edt_cpass.setVisibility(View.VISIBLE);
                            btn_chngpass.setVisibility(View.VISIBLE);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                edt_otp.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    public void VerifyOTP() {

        String input_otp = edt_otp.getText().toString();

        if (TextUtils.isEmpty(input_otp)) {
            edt_otp.setError("Enter Code !");

        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, input_otp);
        signInWithPhoneAuthCredential(credential);
    }

    public void ChangePassword() {
        final ProgressDialog progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Password Is Changing ... ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "faculty_master/change_password.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("1")) {

                        Toast.makeText(ForgetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ForgetPasswordActivity.this, myPref.getMobile(), Toast.LENGTH_LONG).show();
                        myPref.ClearAll();
                        Intent i = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "Password Can't Changed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForgetPasswordActivity.this, "Error Occured" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("mobno", edt_mobno.getText().toString());
                map.put("pass", edt_cpass.getText().toString());

                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
}
