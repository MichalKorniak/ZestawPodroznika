package com.example.admin.zestawpodroznika;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements SensorEventListener,
        LocationListener {


    private LocDbDirector db;
    private File locFilePath;

    private Toolbar toolbar;

    private ImageView imgCompass;
    private TextView txtCourse;
    private SensorManager sensorManager;
    private float currentRotation = 0;

    private LocationManager locationManager;
    private TextView txtGpsIsDisabled;
    private LinearLayout layGpsIsEnabled;
    private TextView txtLati;
    private TextView txtLongi;
    private TextView txtLocation;
    private boolean isLocalisationDetermied;

    private double longi;
    private double lati;

    private LinearLayout layLightSensor;
    private TextView txtLight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //dzieki tej instrukcji tytul bedzie wyswietlany w toolbarze

        imgCompass = (ImageView) findViewById(R.id.img_compass);
        txtCourse = (TextView) findViewById(R.id.txt_course);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        txtGpsIsDisabled = (TextView) findViewById(R.id.txt_gps_is_disabled);
        layGpsIsEnabled = (LinearLayout) findViewById(R.id.lay_gps_is_enabled);
        txtLati = (TextView) findViewById(R.id.txt_Lati);
        txtLongi = (TextView) findViewById(R.id.txt_Longi);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        isLocalisationDetermied = false;

        layLightSensor = (LinearLayout) findViewById(R.id.lay_light_sensor);
        txtLight = (TextView) findViewById(R.id.txt_light);


        locFilePath = new File(getFilesDir(), "saved_loc.txt");
        db = new LocDbDirector(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.itm_close: {
                finish();
                return true;
            }
            case R.id.itm_save_loc: {
                try {
                    saveLoc();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case R.id.itm_show_loc: {
                Intent intent = new Intent(getApplicationContext(), LocalisationsListActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.itm_open_in_map: {
                if (!isLocalisationDetermied) {
                    Toast.makeText(this, "Lokalizacja nie została jeszcze ustalona", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lati, longi);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveLoc() throws IOException
    {
        if (!isLocalisationDetermied) {
            Toast.makeText(this, "Lokalizacja nie została jeszcze ustalona", Toast.LENGTH_SHORT).show();
            return false;
        }
        Localisation loc = new Localisation(txtLocation.getText().toString(), longi, lati);
        db.Add(loc);


        return true;


    }

    // Powiazanie toolbara z main_menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Sprawdz uprawnienia GPS", Toast.LENGTH_SHORT).show();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    this
            );
        }
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null)
            layLightSensor.setVisibility(View.INVISIBLE);
        else {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_GAME);
        }


    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
        //TODO unregister locationManager
    }

    //Compass
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION: {
                int degree = Math.round(event.values[0]);
                String course = "";


                if (degree < 22.5 || degree > 337.5) course += "N";
                else if (degree < 67.5) course += "NE";
                else if (degree < 112.5) course += "E";
                else if (degree < 157.5) course += "SE";
                else if (degree < 202.5) course += "S";
                else if (degree < 247.5) course += "SW";
                else if (degree < 292.5) course += "W";
                else course += "NW";


                txtCourse.setText(Integer.toString(degree) + course);
                float rotation = -degree;

                // create a rotation animation (reverse turn degree degrees)
                RotateAnimation ra = new RotateAnimation(
                        currentRotation,
                        rotation,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);

                // how long the animation will take place
                ra.setDuration(150);

                // set the animation after the end of the reservation status
                ra.setFillAfter(true);

                // Start the animation
                imgCompass.startAnimation(ra);
                currentRotation = rotation;
                break;
            }
            case Sensor.TYPE_LIGHT: {
                txtLight.setText(String.valueOf((int)event.values[0]));
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    //GPS
    @Override
    public void onLocationChanged(Location loc)
    {
        isLocalisationDetermied = true;
        longi = loc.getLongitude();
        lati = loc.getLatitude();
        txtLongi.setText(String.format("%.4f", longi));
        txtLati.setText(String.format("%.4f", lati));


        List<Address> adressess;
        try {
            Geocoder gcd = new Geocoder(getBaseContext());
            adressess = gcd.getFromLocation(lati, longi, 1);
            if (adressess.size() > 0) {
                txtLocation.setText(adressess.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        layGpsIsEnabled.setVisibility(View.VISIBLE);
        txtGpsIsDisabled.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        layGpsIsEnabled.setVisibility(View.INVISIBLE);
        txtGpsIsDisabled.setVisibility(View.VISIBLE);
    }
}
