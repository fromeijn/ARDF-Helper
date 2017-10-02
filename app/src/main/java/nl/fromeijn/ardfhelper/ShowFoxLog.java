package nl.fromeijn.ardfhelper;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.widget.Toast;

public class ShowFoxLog extends ListActivity  {

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_fox_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.clear_log:{
                // TODO: 2-10-17 ask to confirmation 
                FoxLog.deleteAll(FoxLog.class);
                Toast.makeText(ShowFoxLog.this, "Log Cleared", Toast.LENGTH_LONG).show();
                finish(); // go back to EnterActivity
                break;
            }
            case R.id.FoxLog_to_file:{
                
                String state = Environment.getExternalStorageState();
                if (!Environment.MEDIA_MOUNTED.equals(state)) {
                    // TODO: 2-10-17 ask for permission 
                    Toast.makeText(ShowFoxLog.this, "ERROR: Can't write file, no permission!", Toast.LENGTH_LONG).show();
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
                    for (FoxLog foxlog : foxlogs ){
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

        return super.onOptionsItemSelected(item);
    }
}
