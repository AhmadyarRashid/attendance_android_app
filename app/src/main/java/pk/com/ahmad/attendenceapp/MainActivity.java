package pk.com.ahmad.attendenceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView currentTime, logs, latLngtv, storeNametv;
    EditText fromDate, toDate;
    Button checkInBtn, checkOutBtn;
    TableLayout table;
    final Handler h = new Handler();
    String user_id = "";
    String store_name = "";

    private FusedLocationProviderClient mFusedLocationClient;
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double lat = 0.0;
    private double lng = 0.0;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentTime = findViewById(R.id.current_time_main);
        logs = findViewById(R.id.time_logs);
        checkInBtn = findViewById(R.id.button2);
        checkOutBtn = findViewById(R.id.button3);
        latLngtv = findViewById(R.id.latlngtxt);
        fromDate = findViewById(R.id.editText3);
        toDate = findViewById(R.id.editText);
        storeNametv = findViewById(R.id.textView5);

        Intent i = getIntent();
        user_id = i.getStringExtra("user_id");
        store_name = i.getStringExtra("store_name");

        storeNametv.setText(store_name);

        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String selectedDate = year + "-" + String.valueOf(monthOfYear+1) + "-" + dayOfMonth;
                fromDate.setText(selectedDate);
                Toast.makeText(getApplicationContext(), year + "-" + String.valueOf(monthOfYear+1) + "-" + dayOfMonth , Toast.LENGTH_SHORT).show();
            }
        };

        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String selectedDate = year + "-" + String.valueOf(monthOfYear+1) + "-" + dayOfMonth;
                toDate.setText(selectedDate);
                Toast.makeText(getApplicationContext(), year + "-" + String.valueOf(monthOfYear+1) + "-" + dayOfMonth , Toast.LENGTH_SHORT).show();
            }
        };

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
//        user_id = "1";

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
//
//
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(20 * 1000);
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        wayLatitude = location.getLatitude();
//                        wayLongitude = location.getLongitude();
////                        latLngtv.setText("Latitude : "+String.valueOf(wayLatitude) + " \nLongitude : " + String.valueOf(wayLongitude));
//                    }
//                }
//            }
//        };


//        checkLocationPermission();
        startLocationUpdates();
        CurrentTimeHandler();
        getUserStatistics();

    }

    public void initTable(JSONArray rows) {
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        stk.removeAllViews();
        TableRow tbrow0 = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText("                                                        Date ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" IN 1 ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" OUT 1 ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" IN 2 ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" OUT 2 ");
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);

        TextView tv5 = new TextView(this);
        tv5.setText(" IN 3 ");
        tv5.setTextColor(Color.WHITE);
        tbrow0.addView(tv5);
        TextView tv6 = new TextView(this);
        tv6.setText(" OUT 3 ");
        tv6.setTextColor(Color.WHITE);
        tbrow0.addView(tv6);

        TextView tv7 = new TextView(this);
        tv7.setText(" IN 4 ");
        tv7.setTextColor(Color.WHITE);
        tbrow0.addView(tv7);
        TextView tv8 = new TextView(this);
        tv8.setText(" OUT 4 ");
        tv8.setTextColor(Color.WHITE);
        tbrow0.addView(tv8);

        TextView tv9 = new TextView(this);
        tv9.setText(" IN 5 ");
        tv9.setTextColor(Color.WHITE);
        tbrow0.addView(tv9);
        TextView tv10 = new TextView(this);
        tv10.setText(" OUT 5 ");
        tv10.setTextColor(Color.WHITE);
        tbrow0.addView(tv10);

        TextView tv11 = new TextView(this);
        tv11.setText(" IN 6 ");
        tv11.setTextColor(Color.WHITE);
        tbrow0.addView(tv11);
        TextView tv12 = new TextView(this);
        tv12.setText(" OUT 6 ");
        tv12.setTextColor(Color.WHITE);
        tbrow0.addView(tv12);

        stk.addView(tbrow0);


        for (int i = 0; i < rows.length(); i++) {
            TableRow tbrow = new TableRow(this);
            try {
                JSONObject data = rows.getJSONObject(i);
                String result = data.getString("date");
                String IN1 = data.getString("IN1");
                String IN2 = data.getString("IN2");
                String IN3 = data.getString("IN3");
                String IN4 = data.getString("IN4");
                String IN5 = data.getString("IN5");
                String IN6 = data.getString("IN6");

                String OUT1 = data.getString("OUT1");
                String OUT2 = data.getString("OUT2");
                String OUT3 = data.getString("OUT3");
                String OUT4 = data.getString("OUT4");
                String OUT5 = data.getString("OUT5");
                String OUT6 = data.getString("OUT6");


                TextView t1v = new TextView(this);
                t1v.setText("                                                        " + result);
                t1v.setTextColor(Color.WHITE);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);
                TextView t2v = new TextView(this);
                t2v.setText(IN1);
                t2v.setTextColor(Color.WHITE);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);
                TextView t3v = new TextView(this);
                t3v.setText(OUT1);
                t3v.setTextColor(Color.WHITE);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v);
                TextView t4v = new TextView(this);
                t4v.setText(IN2);
                t4v.setTextColor(Color.WHITE);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v);

                TextView t5v = new TextView(this);
                t5v.setText(OUT2);
                t5v.setTextColor(Color.WHITE);
                t5v.setGravity(Gravity.CENTER);
                tbrow.addView(t5v);

                TextView t6 = new TextView(this);
                t6.setText(IN3);
                t6.setTextColor(Color.WHITE);
                t6.setGravity(Gravity.CENTER);
                tbrow.addView(t6);

                TextView t7 = new TextView(this);
                t7.setText(OUT3);
                t7.setTextColor(Color.WHITE);
                t7.setGravity(Gravity.CENTER);
                tbrow.addView(t7);

                TextView t8 = new TextView(this);
                t8.setText(IN4);
                t8.setTextColor(Color.WHITE);
                t8.setGravity(Gravity.CENTER);
                tbrow.addView(t8);

                TextView t9 = new TextView(this);
                t9.setText(OUT4);
                t9.setTextColor(Color.WHITE);
                t9.setGravity(Gravity.CENTER);
                tbrow.addView(t9);

                TextView t10 = new TextView(this);
                t10.setText(IN5);
                t10.setTextColor(Color.WHITE);
                t10.setGravity(Gravity.CENTER);
                tbrow.addView(t10);

                TextView t11 = new TextView(this);
                t11.setText(OUT5);
                t11.setTextColor(Color.WHITE);
                t11.setGravity(Gravity.CENTER);
                tbrow.addView(t11);

                TextView t12 = new TextView(this);
                t12.setText(IN6);
                t12.setTextColor(Color.WHITE);
                t12.setGravity(Gravity.CENTER);
                tbrow.addView(t12);

                TextView t13 = new TextView(this);
                t13.setText(OUT6);
                t13.setTextColor(Color.WHITE);
                t13.setGravity(Gravity.CENTER);
                tbrow.addView(t13);

                stk.addView(tbrow);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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

        latLngtv.setText("Latitude : " + lat + "\nLongitutde : " + lng);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                    latLngtv.setText("Latitude : "+String.valueOf(wayLatitude) + " \nLongitude : " + String.valueOf(wayLongitude));
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            latLngtv.setText("Latitude : "+String.valueOf(wayLatitude) + " \nLongitude : " + String.valueOf(wayLongitude));
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void CurrentTimeHandler() {
        h.postDelayed(new Runnable() {
            private long time = 0;

            @Override
            public void run() {
                Date d = Calendar.getInstance().getTime();
                currentTime.setText(String.valueOf("Time : " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds()));
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void logoutHandler(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    public void checkInHandler(View view) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        Date d = Calendar.getInstance().getTime();
        final String time = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
        final String currentDate = Calendar.getInstance().get(Calendar.YEAR) + "-" + String.valueOf(d.getMonth()+1) + "-" + d.getDate();

        StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/checkIn",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.d("Response", json.toString());
                            Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                            if (json.getBoolean("isSuccess")) {
                                getUserStatistics();
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
                params.put("user_id", user_id);
                params.put("time", time);
                params.put("currentDate", currentDate);
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));

                return params;
            }
        };
        queue.add(postRequest);

//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        // first check in radius or not
//        StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/getAllLocations",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        try {
//                            Boolean isFound = false;
//                            JSONObject json = new JSONObject(response);
//                            Log.d("Response", json.toString());
//                            Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
//                            if (json.getBoolean("isSuccess")) {
//                                JSONArray storeList = json.getJSONArray("payload");
//                                for (int i = 0; i < storeList.length(); i++) {
//                                    JSONObject store = (JSONObject) storeList.get(i);
//
//                                    float[] results = new float[1];
//                                    Location.distanceBetween(
//                                            lat,
//                                            lng,
//                                            Double.parseDouble(store.getString("leti")),
//                                            Double.parseDouble(store.getString("longi")),
//                                            results);
//                                    float distanceInMeters = results[0];
//                                    boolean isWithinRadius = distanceInMeters < Float.parseFloat(store.getString("radius"));
//                                    Log.d("Response", "hello check store wise----" + store.getString("store_name") + "---" + isWithinRadius);
//                                    if (isWithinRadius) {
//                                        Log.d("Response", "Now you can login");
//                                        isFound = true;
//
//
//                                        // now send request to it
//
//
//
//                                    }
//                                }
//
//                                if(!isFound){
//                                    Toast.makeText(getApplicationContext(), "You are away from your designated location.", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//
//                                Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("Error.Response", error.toString());
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//        };
//        queue.add(postRequest);
    }

    public void checkOutHandler(View view) {

        // now send request to it
        // ===============================================

        Date d = Calendar.getInstance().getTime();
        final String time = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
        final String currentDate = Calendar.getInstance().get(Calendar.YEAR) + "-" + String.valueOf(d.getMonth()+1) + "-" + d.getDate();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/checkOut",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.d("Response", json.toString());
                            Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                            if (json.getBoolean("isSuccess")) {
                                getUserStatistics();
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
                params.put("user_id", user_id);
                params.put("time", time);
                params.put("currentDate", currentDate);
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));

                return params;
            }
        };
        queue.add(postRequest);




//
//
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        // first check in radius or not
//        StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/getAllLocations",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        try {
//                            Boolean isFound = false;
//                            JSONObject json = new JSONObject(response);
//                            Log.d("Response", json.toString());
//                            Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
//                            if (json.getBoolean("isSuccess")) {
//                                JSONArray storeList = json.getJSONArray("payload");
//                                for (int i = 0; i < storeList.length(); i++) {
//                                    JSONObject store = (JSONObject) storeList.get(i);
//
//                                    float[] results = new float[1];
//                                    Location.distanceBetween(
//                                            lat,
//                                            lng,
//                                            Double.parseDouble(store.getString("leti")),
//                                            Double.parseDouble(store.getString("longi")),
//                                            results);
//                                    float distanceInMeters = results[0];
//                                    boolean isWithinRadius = distanceInMeters < Float.parseFloat(store.getString("radius"));
//                                    Log.d("Response", "hello check store wise----" + store.getString("store_name") + "---" + isWithinRadius);
//                                    if (isWithinRadius) {
//                                        Log.d("Response", "Now you can login");
//                                        isFound = true;
//
//
//
//
//                                    }
//                                }
//
//                                if(!isFound){
//                                    Toast.makeText(getApplicationContext(), "You are away from your designated location.", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//
//                                Toast.makeText(getApplicationContext(), "Network Issue", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // error
//                        Log.d("Error.Response", error.toString());
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//        };
//        queue.add(postRequest);
    }

    private void getUserStatistics() {
        Date d = Calendar.getInstance().getTime();
        final String currentDate = Calendar.getInstance().get(Calendar.YEAR) + "-" + String.valueOf(d.getMonth()+1) + "-" + d.getDate();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/getAllDataById",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.d("Response", json.toString());
                            Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                            if (json.getBoolean("isSuccess")) {
                                JSONObject payload = json.getJSONObject("payload");
                                String disable = payload.getString("disable");

                                if (disable.equals("checkIn")) {
                                    checkInBtn.setEnabled(true);
                                    checkOutBtn.setEnabled(false);
                                }
                                if (disable.equals("checkOut")) {
                                    checkInBtn.setEnabled(false);
                                    checkOutBtn.setEnabled(true);
                                }

                                JSONArray rows = payload.getJSONArray("rows");

                                initTable(rows);
                                JSONObject data = rows.getJSONObject(0);
                                String result = data.getString("date") + "\n";
                                String IN1 = data.getString("IN1");
                                String IN2 = data.getString("IN2");
                                String IN3 = data.getString("IN3");
                                String IN4 = data.getString("IN4");
                                String IN5 = data.getString("IN5");
                                String IN6 = data.getString("IN6");

                                String OUT1 = data.getString("OUT1");
                                String OUT2 = data.getString("OUT2");
                                String OUT3 = data.getString("OUT3");
                                String OUT4 = data.getString("OUT4");
                                String OUT5 = data.getString("OUT5");
                                String OUT6 = data.getString("OUT6");


                                result += "IN1 = " + IN1 + ", OUT1 = " + OUT1 + "\n" +
                                        "IN2 = " + IN2 + ", OUT2 = " + OUT2 + "\n" +
                                        "IN3 = " + IN3 + ", OUT3 = " + OUT3 + "\n" +
                                        "IN4 = " + IN4 + ", OUT4 = " + OUT4 + "\n" +
                                        "IN5 = " + IN5 + ", OUT5 = " + OUT5 + "\n" +
                                        "IN6 = " + IN6 + ", OUT6 = " + OUT6;

                                logs.setText(result);
                            } else {
                                Toast.makeText(getApplicationContext(), "No User Data Found", Toast.LENGTH_SHORT).show();
                                checkInBtn.setEnabled(true);
                                checkOutBtn.setEnabled(false);
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
                params.put("user_id", user_id);
                params.put("currentDate", currentDate);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void searchDateHandler(View view) {
        final String fDate = fromDate.getText().toString();
        final String tDate = toDate.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, getString(R.string.ip_address) + "/api/users/getDateFilter",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.d("Response", json.toString());
                            Log.d("Response", String.valueOf(json.getBoolean("isSuccess")));
                            if (json.getBoolean("isSuccess")) {
                                JSONObject payload = json.getJSONObject("payload");

                                JSONArray rows = payload.getJSONArray("rows");
                                initTable(rows);
                                String result = "";
                                for(int i = 0 ; i < rows.length(); i ++){
                                    JSONObject data = rows.getJSONObject(i);

                                    result += data.getString("date") + "\n";

                                    String IN1 = data.getString("IN1");
                                    String IN2 = data.getString("IN2");
                                    String IN3 = data.getString("IN3");
                                    String IN4 = data.getString("IN4");
                                    String IN5 = data.getString("IN5");
                                    String IN6 = data.getString("IN6");

                                    String OUT1 = data.getString("OUT1");
                                    String OUT2 = data.getString("OUT2");
                                    String OUT3 = data.getString("OUT3");
                                    String OUT4 = data.getString("OUT4");
                                    String OUT5 = data.getString("OUT5");
                                    String OUT6 = data.getString("OUT6");


                                    result += "IN1 = " + IN1 + ", OUT1 = " + OUT1 + "\n" +
                                            "IN2 = " + IN2 + ", OUT2 = " + OUT2 + "\n" +
                                            "IN3 = " + IN3 + ", OUT3 = " + OUT3 + "\n" +
                                            "IN4 = " + IN4 + ", OUT4 = " + OUT4 + "\n" +
                                            "IN5 = " + IN5 + ", OUT5 = " + OUT5 + "\n" +
                                            "IN6 = " + IN6 + ", OUT6 = " + OUT6 + "\n\n";
                                }

                                logs.setText(result);
                            } else {
                                Toast.makeText(getApplicationContext(), "No User Data Found", Toast.LENGTH_SHORT).show();
                                checkInBtn.setEnabled(true);
                                checkOutBtn.setEnabled(false);
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
                params.put("user_id", user_id);
                params.put("fDate", fDate);
                params.put("tDate", tDate);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
