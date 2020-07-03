package pk.com.USC.IT_Department;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Reports extends AppCompatActivity {

    EditText fromDate, toDate;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        fromDate = findViewById(R.id.editText3);
        toDate = findViewById(R.id.editText);

        fromDate.setInputType(InputType.TYPE_NULL);
        toDate.setInputType(InputType.TYPE_NULL);

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
                new DatePickerDialog(Reports.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Reports.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        getUserStatistics();
    }

    public void initTable(JSONArray rows) {
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        stk.removeAllViews();
        TableRow tbrow0 = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText("                                                                                   Date ");
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(8,8,8,8);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" IN 1 ");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(8,8,8,8);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" OUT 1 ");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(8,8,8,8);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" IN 2 ");
        tv3.setTextColor(Color.WHITE);
        tv3.setPadding(8,8,8,8);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" OUT 2 ");
        tv4.setTextColor(Color.WHITE);
        tv4.setPadding(8,8,8,8);
        tbrow0.addView(tv4);

        TextView tv5 = new TextView(this);
        tv5.setText(" IN 3 ");
        tv5.setTextColor(Color.WHITE);
        tv5.setPadding(8,8,8,8);
        tbrow0.addView(tv5);
        TextView tv6 = new TextView(this);
        tv6.setText(" OUT 3 ");
        tv6.setTextColor(Color.WHITE);
        tv6.setPadding(8,8,8,8);
        tbrow0.addView(tv6);

        TextView tv7 = new TextView(this);
        tv7.setText(" IN 4 ");
        tv7.setTextColor(Color.WHITE);
        tv7.setPadding(8,8,8,8);
        tbrow0.addView(tv7);
        TextView tv8 = new TextView(this);
        tv8.setText(" OUT 4 ");
        tv8.setTextColor(Color.WHITE);
        tv8.setPadding(8,8,8,8);
        tbrow0.addView(tv8);

        TextView tv9 = new TextView(this);
        tv9.setText(" IN 5 ");
        tv9.setTextColor(Color.WHITE);
        tv9.setPadding(8,8,8,8);
        tbrow0.addView(tv9);
        TextView tv10 = new TextView(this);
        tv10.setText(" OUT 5 ");
        tv10.setTextColor(Color.WHITE);
        tv10.setPadding(8,8,8,8);
        tbrow0.addView(tv10);

        TextView tv11 = new TextView(this);
        tv11.setText(" IN 6 ");
        tv11.setTextColor(Color.WHITE);
        tv11.setPadding(8,8,8,8);
        tbrow0.addView(tv11);
        TextView tv12 = new TextView(this);
        tv12.setText(" OUT 6 ");
        tv12.setPadding(8,8,8,8);
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
                t1v.setText("                                                                                  " + result);
                t1v.setTextColor(Color.WHITE);
                t1v.setGravity(Gravity.CENTER);
                t1v.setPadding(8,8,8,8);
                tbrow.addView(t1v);
                TextView t2v = new TextView(this);
                t2v.setText(IN1);
                t2v.setTextColor(Color.WHITE);
                t2v.setGravity(Gravity.CENTER);
                t2v.setPadding(8,8,8,8);
                tbrow.addView(t2v);
                TextView t3v = new TextView(this);
                t3v.setText(OUT1);
                t3v.setTextColor(Color.WHITE);
                t3v.setGravity(Gravity.CENTER);
                t3v.setPadding(8,8,8,8);
                tbrow.addView(t3v);
                TextView t4v = new TextView(this);
                t4v.setText(IN2);
                t4v.setTextColor(Color.WHITE);
                t4v.setGravity(Gravity.CENTER);
                t4v.setPadding(8,8,8,8);
                tbrow.addView(t4v);

                TextView t5v = new TextView(this);
                t5v.setText(OUT2);
                t5v.setTextColor(Color.WHITE);
                t5v.setGravity(Gravity.CENTER);
                t5v.setPadding(8,8,8,8);
                tbrow.addView(t5v);

                TextView t6 = new TextView(this);
                t6.setText(IN3);
                t6.setTextColor(Color.WHITE);
                t6.setGravity(Gravity.CENTER);
                t6.setPadding(8,8,8,8);
                tbrow.addView(t6);

                TextView t7 = new TextView(this);
                t7.setText(OUT3);
                t7.setTextColor(Color.WHITE);
                t7.setGravity(Gravity.CENTER);
                t7.setPadding(8,8,8,8);
                tbrow.addView(t7);

                TextView t8 = new TextView(this);
                t8.setText(IN4);
                t8.setTextColor(Color.WHITE);
                t8.setGravity(Gravity.CENTER);
                t8.setPadding(8,8,8,8);
                tbrow.addView(t8);

                TextView t9 = new TextView(this);
                t9.setText(OUT4);
                t9.setTextColor(Color.WHITE);
                t9.setGravity(Gravity.CENTER);
                t9.setPadding(8,8,8,8);
                tbrow.addView(t9);

                TextView t10 = new TextView(this);
                t10.setText(IN5);
                t10.setTextColor(Color.WHITE);
                t10.setGravity(Gravity.CENTER);
                t10.setPadding(8,8,8,8);
                tbrow.addView(t10);

                TextView t11 = new TextView(this);
                t11.setText(OUT5);
                t11.setTextColor(Color.WHITE);
                t11.setGravity(Gravity.CENTER);
                t11.setPadding(8,8,8,8);
                tbrow.addView(t11);

                TextView t12 = new TextView(this);
                t12.setText(IN6);
                t12.setTextColor(Color.WHITE);
                t12.setGravity(Gravity.CENTER);
                t12.setPadding(8,8,8,8);
                tbrow.addView(t12);

                TextView t13 = new TextView(this);
                t13.setText(OUT6);
                t13.setTextColor(Color.WHITE);
                t13.setGravity(Gravity.CENTER);
                t13.setPadding(8,8,8,8);
                tbrow.addView(t13);

                stk.addView(tbrow);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void getUserStatistics() {
        Date d = Calendar.getInstance().getTime();
        final String currentDate = Calendar.getInstance().get(Calendar.YEAR) + "-" + String.valueOf(d.getMonth()+1) + "-" + d.getDate();
        RequestQueue queue = Volley.newRequestQueue(Reports.this);
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
//                                    checkInBtn.setEnabled(true);
//                                    checkOutBtn.setEnabled(false);
                                }
                                if (disable.equals("checkOut")) {
//                                    checkInBtn.setEnabled(false);
//                                    checkOutBtn.setEnabled(true);
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

//                                logs.setText(result);
                            } else {
                                Toast.makeText(getApplicationContext(), "No User Data Found", Toast.LENGTH_SHORT).show();
//                                checkInBtn.setEnabled(true);
//                                checkOutBtn.setEnabled(false);
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
                params.put("currentDate", currentDate);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void searchDateHandler(View view) {
        final String fDate = fromDate.getText().toString();
        final String tDate = toDate.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(Reports.this);
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

//                                logs.setText(result);
                            } else {
                                Toast.makeText(getApplicationContext(), "No User Data Found", Toast.LENGTH_SHORT).show();
//                                checkInBtn.setEnabled(true);
//                                checkOutBtn.setEnabled(false);
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
                params.put("fDate", fDate);
                params.put("tDate", tDate);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
