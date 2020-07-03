package pk.com.USC.IT_Department;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText mobile_no, password;
    TextView latlngtv;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double lat = 0.0;
    private double lng = 0.0;
    private Boolean isSuccess = false;

    // network checks
    ConnectivityManager cm;
    NetworkInfo wifiNetwork;
    NetworkInfo mobileNetwork;
    NetworkInfo activeNetwork;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobile_no = findViewById(R.id.email_login_et);
        password = findViewById(R.id.pass_login_et);
        latlngtv = findViewById(R.id.coordinates);

        cm = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        activeNetwork = cm.getActiveNetworkInfo();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if ((wifiNetwork != null && wifiNetwork.isConnected()) || (mobileNetwork != null && mobileNetwork.isConnected()) || (activeNetwork != null && activeNetwork.isConnected())) {
            Toast.makeText(getApplicationContext(), "Internet Connected Successfully", Toast.LENGTH_SHORT).show();
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                startLocationUpdates();
            } else {
                showGPSDisabledAlertToUser();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Check Internet Connection.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        //get current location
        // check if GPS enabled

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
            startLocationUpdates();
        } else {
            Toast.makeText(getApplicationContext(), "Please turn on GPS ", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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

        latlngtv.setText("Latitude : " + lat + "\nLongitutde : " + lng);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void loginHandler(View view) {

//        float[] results = new float[1];
//        Location.distanceBetween(lat, lng, 33.6518, 73.1566, results);
//        float distanceInMeters = results[0];
//        boolean isWithin10km = distanceInMeters < 2000;

        RequestQueue queue = Volley.newRequestQueue(Login.this);
        if (mobile_no.getText().toString().length() == 0 || password.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Some field are empty", Toast.LENGTH_SHORT).show();
        } else if (mobile_no.getText().toString().length() < 9) {
            Toast.makeText(getApplicationContext(), "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
        } else {

            // now going to login
            StringRequest loginRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/login",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                JSONObject json = new JSONObject(response);
                                Log.d("Response", json.toString());
                                Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                                if (json.getBoolean("isSuccess")) {
//                                    JSONObject rows = json.getJSONObject("pay")
                                    JSONObject payload = json.getJSONObject("payload");
                                    JSONArray rows = payload.getJSONArray("rows");
                                    JSONArray doc = payload.getJSONArray("doc");


                                    Log.d("Response", rows.toString());

                                    JSONObject data = rows.getJSONObject(0);
                                    JSONObject store = doc.getJSONObject(0);

                                    Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                    i.putExtra("user_id", data.get("id").toString());
                                    i.putExtra("store_name", store.get("store_name").toString());
                                    startActivity(i);
                                } else {
//                                    Toast.makeText(getApplicationContext(), "Your cell# or password is incorrect", Toast.LENGTH_SHORT).show();
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
                    params.put("mobile_no", mobile_no.getText().toString());
                    params.put("password", password.getText().toString());
                    params.put("lat", String.valueOf(lat));
                    params.put("lng", String.valueOf(lng));

                    return params;
                }
            };
            queue.add(loginRequest);


//            // first check in radius or not
//            StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/getAllLocations",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            // response
//                            try {
//                                Boolean isFound = false;
//                                JSONObject json = new JSONObject(response);
//                                Log.d("Response", json.toString());
//                                Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
//                                if (json.getBoolean("isSuccess")) {
//                                    JSONArray storeList = json.getJSONArray("payload");
//                                    for (int i = 0; i < storeList.length(); i++) {
//                                        JSONObject store = (JSONObject) storeList.get(i);
//
//                                        float[] results = new float[1];
//                                        Location.distanceBetween(
//                                                lat,
//                                                lng,
//                                                Double.parseDouble(store.getString("leti")),
//                                                Double.parseDouble(store.getString("longi")),
//                                                results);
//                                        float distanceInMeters = results[0];
//                                        boolean isWithinRadius = distanceInMeters < Float.parseFloat(store.getString("radius"));
//                                        Log.d("Response", "hello check store wise----" + store.getString("store_name") + "---" + isWithinRadius);
//                                        if (isWithinRadius) {
//                                            Log.d("Response", "Now you can login");
//                                            isFound = true;
//
//                                            // login request here
//                                        }
//                                    }
//
//                                    if(!isFound){
//                                        Toast.makeText(getApplicationContext(), "You are away from your designated location.", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//
//                                    Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//
//                                e.printStackTrace();
//                            }
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // error
//                            isSuccess = false;
//                            Log.d("Error.Response", error.toString());
//                        }
//                    }
//            ) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    return params;
//                }
//            };
//            queue.add(postRequest);
        }

    }

    public void createNewHandler(View view) {
        startActivity(new Intent(getApplicationContext(), SignUp.class));
    }
}
