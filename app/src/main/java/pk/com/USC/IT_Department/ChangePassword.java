package pk.com.USC.IT_Department;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {

    EditText prevPassword, newPassword, confirrmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        prevPassword = findViewById(R.id.editText2);
        newPassword = findViewById(R.id.editText4);
        confirrmPassword = findViewById(R.id.editText5);
    }


    public void changePasswordHandler(View view) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if (!newPassword.getText().toString().equals(confirrmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "New Password Field Mistach", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest loginRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/changePassword",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                Log.d("Response", json.toString());
                                Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                                if (json.getBoolean("isSuccess")) {
                                    confirrmPassword.setText("");
                                    newPassword.setText("");
                                    Toast.makeText(getApplicationContext(), "Password change successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    Intent i = getIntent();
                    params.put("user_id", i.getStringExtra("user_id"));
                    params.put("newPassword", newPassword.getText().toString());

                    return params;
                }
            };
            queue.add(loginRequest);
        }
    }
}
