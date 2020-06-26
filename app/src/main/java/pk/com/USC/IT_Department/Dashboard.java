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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import pk.com.USC.IT_Department.Adapter.ImageAdapter;

public class Dashboard extends AppCompatActivity {

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
        setContentView(R.layout.activity_dashboard);

        Intent i = getIntent();
        cm = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        activeNetwork = cm.getActiveNetworkInfo();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        GridView gridview = (GridView) findViewById(R.id.dashboard_grid);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
//                Toast.makeText(getApplicationContext(), position+ "", Toast.LENGTH_SHORT).show();

                if(position == 0){
                    Intent attend = new Intent(getApplicationContext(), MainActivity.class);
                    attend.putExtra("user_id", i.getStringExtra("user_id"));
                    attend.putExtra("store_name", i.getStringExtra("store_name"));
                    startActivity(attend);
                }else if(position == 1){
                    Intent report = new Intent(getApplicationContext(), Reports.class);
                    report.putExtra("user_id", i.getStringExtra("user_id"));
                    report.putExtra("store_name", i.getStringExtra("store_name"));
                    startActivity(report);
                }else if(position == 2){
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    // Add data to the intent, the receiving app will decide
                    // what to do with it.
                    share.putExtra(Intent.EXTRA_SUBJECT, "Share Current Location");

                    share.putExtra(Intent.EXTRA_TEXT, "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);

                    startActivity(Intent.createChooser(share, "Share location!"));
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(null, null, "hello world", null, null);
//                    Toast.makeText(getApplicationContext(), "Share Location\n" + lat + "\n" + lng , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Never happen", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }
}
