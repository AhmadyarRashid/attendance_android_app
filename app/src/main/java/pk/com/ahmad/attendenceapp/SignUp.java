package pk.com.ahmad.attendenceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText name, phoneNo, email, pass, pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name_signup_et);
        phoneNo = findViewById(R.id.phone_signup_et);
        email = findViewById(R.id.email_signup_et);
        pass = findViewById(R.id.pass_signup_et);
        pass1 = findViewById(R.id.pass2_signup_et);
    }

    public void loginHandler(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    public void signupHandler(View view) {
        if(name.getText().toString().length() == 0 ||
                phoneNo.getText().toString().length() == 0 ||
                email.getText().toString().length() == 0 ||
                pass.getText().toString().length() == 0 ||
                pass1.getText().toString().length() == 0
                ){
            Toast.makeText(getApplicationContext(), "Some field are empty", Toast.LENGTH_SHORT).show();
        } else if(phoneNo.getText().toString().length() < 9){
            Toast.makeText(getApplicationContext(), "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
        }else if(pass.getText().toString().length() < 5){
            Toast.makeText(getApplicationContext(), "Your enter password is too weak", Toast.LENGTH_SHORT).show();
        } else if(pass.getText().toString().equals(pass1.getText().toString())){
            RequestQueue queue = Volley.newRequestQueue(SignUp.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address)+"/api/users/register",
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                JSONObject json = new JSONObject(response);
                                Log.d("Response", json.toString());
                                Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                                if(json.getBoolean("isSuccess")){
                                    Toast.makeText(getApplicationContext(), "Account Created successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                }else {
                                    Toast.makeText(getApplicationContext(), "Mobile No already exists" , Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("name", name.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("mobile_no", phoneNo.getText().toString());
                    params.put("password", pass.getText().toString());

                    return params;
                }
            };
            queue.add(postRequest);
        }else {
            Toast.makeText(getApplicationContext(), "Both Password Field Mismatch", Toast.LENGTH_SHORT).show();
        }

    }
}
