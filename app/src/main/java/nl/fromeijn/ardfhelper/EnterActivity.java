package nl.fromeijn.ardfhelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.TimedText;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Handler;

public class EnterActivity extends Activity implements LocationListener{
    final static int INTERVAL = 500; // 1/2s
    final static int MAXtimeLastGpsUpdate = 20; // 1/2s

    private TextView latField;
    private TextView lonField;
    private TextView autoSyncInfo;
    private LocationManager locationManager;
    private String provider;
    private TextView secondsLeft;
    private TextView foxNow;
    private ProgressBar foxProgressbar;
    private EditText degInput;
    private RadioGroup foxradiogroup;
    private RadioButton fox1, fox2, fox3, fox4, fox5;
    private Button storeButton;
    private boolean enabledAutoSync;
    private double LastLat, LastLon;
    private int foxOffsetMin=0, foxOffsetSec=0;
    private int timeLastGpsUpdate = MAXtimeLastGpsUpdate*2;
    private boolean uglyGpsUpdateFix = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        latField = (TextView) findViewById(R.id.textLat);
        lonField = (TextView) findViewById(R.id.textLon);
        autoSyncInfo = (TextView) findViewById(R.id.autoSyncInfo);
        secondsLeft = (TextView) findViewById(R.id.secondsLeft);
        foxNow = (TextView) findViewById(R.id.foxNow);
        foxProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        degInput = (EditText) findViewById(R.id.degInput);
        foxradiogroup = (RadioGroup) findViewById(R.id.radioGroup);
        fox1 = (RadioButton) findViewById(R.id.fox1);
        fox2 = (RadioButton) findViewById(R.id.fox2);
        fox3 = (RadioButton) findViewById(R.id.fox3);
        fox4 = (RadioButton) findViewById(R.id.fox4);
        fox5 = (RadioButton) findViewById(R.id.fox5);

        fox1.setId(1);
        fox2.setId(2);
        fox3.setId(3);
        fox4.setId(4);
        fox5.setId(5);

        fox1.setTextColor(Color.RED);
        fox2.setTextColor(Color.GREEN);
        fox3.setTextColor(Color.MAGENTA);
        fox4.setTextColor(Color.BLUE);
        fox5.setTextColor(Color.BLACK);

//        test
//        FoxLog foxlog1 = new FoxLog(1, 52.335340, 4.924072, 300);
//        foxlog1.save();
//        FoxLog foxlog2 = new FoxLog(1, 52.360546, 4.908906, 229);
//        foxlog2.save();

        setupStoreButton();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latField.setText("Location not available");
            lonField.setText("Location not available");
        }

        foxradiogroup.check(1);

        new Thread(new Runnable() {
            public void run() {
                while (true){
                    try{
                        Thread.sleep(INTERVAL);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    updateTimeAndFox();
                }
            }
        }).start();
    }

    private void setupStoreButton(){
        storeButton = (Button) findViewById(R.id.store_button);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strDegInput = degInput.getText().toString();
                if (!strDegInput.isEmpty()) {
                    int angle = Integer.parseInt(strDegInput);
                    int fox = foxradiogroup.getCheckedRadioButtonId();
                    if (angle >= 0 && angle < 360) {
                        Toast.makeText(EnterActivity.this, "Location & Angle(" + String.valueOf(angle) + ") for FOX " + String.valueOf(fox) + " Stored", Toast.LENGTH_LONG).show();
                        FoxLog foxlog = new FoxLog(fox, LastLat, LastLon, angle);
                        foxlog.save();
//                        FoxLog foxlog = new FoxLog(1,52.091866,5.363554,75);
//                        foxlog.save();
//                        foxlog = new FoxLog(1,52.097408,5.363628,102);
//                        foxlog.save();
//                        foxlog = new FoxLog(1,52.092880,5.363268,78);
//                        foxlog.save();
//
//                        foxlog = new FoxLog(2,52.092876,5.363270,12);
//                        foxlog.save();
//                        foxlog = new FoxLog(2,52.092896,5.370762,348);
//                        foxlog.save();
//                        foxlog = new FoxLog(2,52.092395,5.377097,326);
//                        foxlog.save();
//                        foxlog = new FoxLog(2,52.095774,5.379566,300);
//                        foxlog.save();

                    } else {
                        Toast.makeText(EnterActivity.this, "Check Angle", Toast.LENGTH_LONG).show();
                    }
                    degInput.setText("");
                }
            }
        });
    }

    public void updateTimeAndFox() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Calendar rightNow = Calendar.getInstance();
                int seconds = rightNow.get(Calendar.SECOND);
                int minutes = rightNow.get(Calendar.MINUTE);
                if(seconds>=foxOffsetSec || foxOffsetSec==0)
                    seconds = seconds-foxOffsetSec;
                else {
                    minutes += 4; // minus 1;
                    seconds += (60-foxOffsetSec);
                }
                seconds = 59-seconds;
                minutes += (5-foxOffsetMin);

                int curFox = (minutes % 5) + 1;
                switch (curFox){
                    case 1: foxProgressbar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        break;
                    case 2: foxProgressbar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        break;
                    case 3: foxProgressbar.getProgressDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);
                        break;
                    case 4: foxProgressbar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                        break;
                    case 5: foxProgressbar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                        break;
                }
                secondsLeft.setText(String.valueOf(seconds));
                foxProgressbar.setProgress(seconds);
                foxNow.setText(String.valueOf(curFox));
                if (enabledAutoSync){
                    autoSyncInfo.setText("Auto Sync Enabled");
                    foxradiogroup.check(curFox);
                }else{
                    autoSyncInfo.setText("Auto Sync Disabled");
                }

                if(timeLastGpsUpdate<=MAXtimeLastGpsUpdate)
                    storeButton.setBackgroundColor(0X5500a400);
                else
                    storeButton.setBackgroundColor(0X55000100);

                if(timeLastGpsUpdate < 5000)
                    timeLastGpsUpdate++;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        latField.setText(String.valueOf(lat));
        lonField.setText(String.valueOf(lon));
        LastLat = lat;
        LastLon = lon;
        if(uglyGpsUpdateFix)
            uglyGpsUpdateFix = false;
        else
            timeLastGpsUpdate = 0;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // does nothing

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.open_maps) {
            startActivity(new Intent(EnterActivity.this, MapsActivity.class));
            return true;
        }

        if (id == R.id.show_log) {
            startActivity(new Intent(EnterActivity.this, ShowFoxLog.class));
            return true;
        }

        if (id == R.id.sync) {
            Calendar rightNow = Calendar.getInstance();
            foxOffsetSec =  rightNow.get(Calendar.SECOND);
            foxOffsetMin =  rightNow.get(Calendar.MINUTE);
            return true;
        }

        if (id == R.id.auto_sync) {
            if(enabledAutoSync){
                Toast.makeText(EnterActivity.this, "AutoSync Disabled", Toast.LENGTH_LONG).show();
                enabledAutoSync = !enabledAutoSync;
            }else {
                Toast.makeText(EnterActivity.this, "AutoSync Enabled", Toast.LENGTH_LONG).show();
                enabledAutoSync = !enabledAutoSync;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
