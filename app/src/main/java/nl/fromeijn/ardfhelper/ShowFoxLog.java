package nl.fromeijn.ardfhelper;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;
import android.widget.ArrayAdapter;
import android.app.ListActivity;

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
        if (id == R.id.clear_log) {
            FoxLog.deleteAll(FoxLog.class);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
