package apex.mams.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import com.google.android.gms.auth.api.Auth;
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

public class AuthenticateActivity extends AppCompatActivity {

    Getip getip;

    EditText edt_otp;
    Button btn_verify;

    EditText edt_pass, edt_cpass;
    Button btn_chngpass;

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;

    boolean mVerificationInProgress = false;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendOTP;

    String mobno;
    String ReceivedOTP;
    TextInputLayout passinput, passinput2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        Intent i = getIntent();
        mobno = i.getStringExtra("mobno");
        edt_otp = findViewById(R.id.edt_otp);
        btn_verify = findViewById(R.id.btn_verify);

        edt_pass = findViewById(R.id.edt_pass);
        edt_cpass = findViewById(R.id.edt_cpass);
        btn_chngpass = findViewById(R.id.btn_chngpass);
        passinput = findViewById(R.id.passinput);
        passinput2 = findViewById(R.id.passinput2);

        getip = new Getip(this);

        firebaseAuth = FirebaseAuth.getInstance();

        mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mVerificationInProgress = false;
                Toast.makeText(AuthenticateActivity.this, "Verification Complete!", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(AuthenticateActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(AuthenticateActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(AuthenticateActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                mResendOTP = forceResendingToken;

                mVerificationId = s;
                ReceivedOTP = s;
            }
        };

        SendSMS();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyOTP();
            }
        });

        btn_chngpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword();
            }
        });
    }

    public void SendSMS() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobno, 60, TimeUnit.SECONDS, this, mcallback
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            edt_otp.setVisibility(View.GONE);
                            btn_verify.setVisibility(View.GONE);
                            edt_pass.setVisibility(View.VISIBLE);
                            edt_cpass.setVisibility(View.VISIBLE);
                            btn_chngpass.setVisibility(View.VISIBLE);
                            passinput.setVisibility(View.VISIBLE);
                            passinput2.setVisibility(View.VISIBLE);
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
        final ProgressDialog progressDialog = new ProgressDialog(AuthenticateActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Login in progress..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/change_password.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("1")) {
                        Toast.makeText(AuthenticateActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        IsAuthenticated();
                    } else {
                        Toast.makeText(AuthenticateActivity.this, "Password Can't Changed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AuthenticateActivity.this, "Error Occured" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("mobno", mobno);
                map.put("pass", edt_cpass.getText().toString());
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }

    public void IsAuthenticated() {
        final ProgressDialog progressDialog = new ProgressDialog(AuthenticateActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Login in progress..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getip.getUrl() + "student_master/change_authenticate.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("1")) {
                        Intent intent3 = new Intent(AuthenticateActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent3);
                    } else {
                        Toast.makeText(AuthenticateActivity.this, "Can't Authenticate", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(AuthenticateActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent3);
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
                Toast.makeText(AuthenticateActivity.this, "Error Occured" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("mobno", mobno);
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyApp.getVolleyApp().getRequestQueue().add(stringRequest);
    }
}
