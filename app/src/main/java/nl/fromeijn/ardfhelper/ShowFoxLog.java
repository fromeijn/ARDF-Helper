package nl.fromeijn.ardfhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.widget.Toast;

import static nl.fromeijn.ardfhelper.EnterActivity.MY_PERMISSIONS_REQUEST_STORAGE;

public class ShowFoxLog extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_fox_log);

        List<FoxLog> foxlogs = FoxLog.listAll(FoxLog.class);

        ArrayAdapter<FoxLog> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, foxlogs);
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_fox_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.clear_log: {
                // TODO: 2-10-17 ask to confirmation 
                FoxLog.deleteAll(FoxLog.class);
                Toast.makeText(ShowFoxLog.this, "Log Cleared", Toast.LENGTH_LONG).show();
                finish(); // go back to EnterActivity
                break;
            }
            case R.id.FoxLog_to_file: {

                if (ContextCompat.checkSelfPermission(ShowFoxLog.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShowFoxLog.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);
                } else {

                    String state = Environment.getExternalStorageState();
                    if (!Environment.MEDIA_MOUNTED.equals(state)) {
                        Toast.makeText(ShowFoxLog.this, "ERROR: Can't write file, not mounted?!?", Toast.LENGTH_LONG).show();
                    }

                    String filename = "ARDF-Helper.log";

                    try {
                        File path = new File(Environment.getExternalStorageDirectory(), "ARDF-Helper");
                        if (!path.exists()) {
                            path.mkdirs();
                        }
                        File file = new File(path, filename);
                        FileWriter writer = new FileWriter(file);//, true);
                        writer.append("fox,lat,lon,angle\n");
                        List<FoxLog> foxlogs = FoxLog.listAll(FoxLog.class);
                        for (FoxLog foxlog : foxlogs) {
                            writer.append(foxlog.toString());
                            writer.append("\n");
                        }
                        writer.flush();
                        writer.close();
                        Toast.makeText(ShowFoxLog.this, "Log successful written to file!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(ShowFoxLog.this, "ERROR: Can't write file!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
