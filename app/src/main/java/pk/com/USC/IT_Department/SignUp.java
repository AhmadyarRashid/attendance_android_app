package pk.com.USC.IT_Department;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText name, phoneNo, email, pass, pass1;
    TextView cordinates;
    Button signupbtn;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double lat = 0.0;
    private double lng = 0.0;
    private Boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name_signup_et);
        phoneNo = findViewById(R.id.phone_signup_et);
        email = findViewById(R.id.email_signup_et);
        pass = findViewById(R.id.pass_signup_et);
        pass1 = findViewById(R.id.pass2_signup_et);
        cordinates = findViewById(R.id.textView7);
        signupbtn = findViewById(R.id.signup_button);

        if (lat == 0.0 && lng == 0.0) {
            signupbtn.setEnabled(false);
        }

        startLocationUpdates();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        lat = location.getLatitude();
        lng = location.getLongitude();

        if(lat > 0 && lng > 0){
            signupbtn.setEnabled(true);
        }

//        Toast.makeText(getApplicationContext(), "Latitude : " + lat + "\nLongitutde : " + lng, Toast.LENGTH_SHORT).show();

        cordinates.setText("Latitude : " + lat + "\nLongitutde : " + lng);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void loginHandler(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    public void signupHandler(View view) {
        if (name.getText().toString().length() == 0 ||
                phoneNo.getText().toString().length() == 0 ||
                email.getText().toString().length() == 0 ||
                pass.getText().toString().length() == 0 ||
                pass1.getText().toString().length() == 0
                ) {
            Toast.makeText(getApplicationContext(), "Some field are empty", Toast.LENGTH_SHORT).show();
        } else if (phoneNo.getText().toString().length() < 9) {
            Toast.makeText(getApplicationContext(), "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
        } else if (pass.getText().toString().length() < 5) {
            Toast.makeText(getApplicationContext(), "Your enter password is too weak", Toast.LENGTH_SHORT).show();
        } else if (pass.getText().toString().equals(pass1.getText().toString())) {
            RequestQueue queue = Volley.newRequestQueue(SignUp.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/register",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                JSONObject json = new JSONObject(response);
                                Log.d("Response", json.toString());
                                Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                                if (json.getBoolean("isSuccess")) {
                                    Toast.makeText(getApplicationContext(), "Account Created successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Mobile No already exists", Toast.LENGTH_SHORT).show();
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
                    params.put("name", name.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("mobile_no", phoneNo.getText().toString());
                    params.put("password", pass.getText().toString());
                    params.put("lati", String.valueOf(lat));
                    params.put("longi", String.valueOf(lng));

                    return params;
                }
            };
            queue.add(postRequest);
        } else {
            Toast.makeText(getApplicationContext(), "Both Password Field Mismatch", Toast.LENGTH_SHORT).show();
        }

    }
}
