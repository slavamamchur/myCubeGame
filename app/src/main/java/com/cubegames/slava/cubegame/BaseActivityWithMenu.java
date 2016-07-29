package com.cubegames.slava.cubegame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import static com.cubegames.slava.cubegame.SettingsManager.PARAM_AUTH_TOKEN;

public class BaseActivityWithMenu extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.menu_exit:
                finish();
                return true;
            case R.id.menu_logout:
                doLogout();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLogout(){
        SettingsManager.getInstance(getApplicationContext()).setAuthToken("");
    }

}
