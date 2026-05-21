package com.example.localisation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private double posLat;
    private double posLong;
    private double posAlt;
    private float posPrecision;
    private RequestQueue volleyNetQueue;
    private TextView displayInfo;

    private String serverEndpoint = "http://10.0.2.2/localisation/createPosition.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayInfo = findViewById(R.id.tvInfo);
        volleyNetQueue = Volley.newRequestQueue(getApplicationContext());

        LocationManager gpsSystemSvc =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 1);
            return;
        }

        gpsSystemSvc.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        posLat = location.getLatitude();
                        posLong = location.getLongitude();
                        posAlt = location.getAltitude();
                        posPrecision = location.getAccuracy();

                        String infoMsg = "Latitude : " + posLat
                                + "\nLongitude : " + posLong
                                + "\nAltitude : " + posAlt
                                + "\nPrécision : " + posPrecision + " m";

                        displayInfo.setText(infoMsg);
                        Toast.makeText(getApplicationContext(), infoMsg, Toast.LENGTH_LONG).show();

                        uploadLocationData(posLat, posLong);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                "Provider activé : " + provider,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                "Provider désactivé : " + provider,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void uploadLocationData(final double latitudeVal, final double longitudeVal) {
        StringRequest networkRequest = new StringRequest(
                Request.Method.POST,
                serverEndpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),
                                response,
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Erreur : " + error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> dataMap = new HashMap<>();
                SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                dataMap.put("latitude", String.valueOf(latitudeVal));
                dataMap.put("longitude", String.valueOf(longitudeVal));
                dataMap.put("date_position", timeFormatter.format(new Date()));

                String uniqueId = android.provider.Settings.Secure.getString(
                        getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID
                );
                dataMap.put("imei", uniqueId);

                return dataMap;
            }
        };

        volleyNetQueue.add(networkRequest);
    }
}