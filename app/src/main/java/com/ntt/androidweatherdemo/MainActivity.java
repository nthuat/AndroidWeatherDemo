package com.ntt.androidweatherdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity implements
        ActionBar.OnNavigationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        if(Global.START_APP){
            runMainFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if(Global.fragmentDetail != null)
            runNavigationOnBack();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        Global.refreshMenuItem = menu.getItem(0);
        if(!Global.START_APP){
            runMainFragment();
            Global.START_APP = true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                runRefresh(item);
                return true;
            case android.R.id.home:
                if(Global.fragmentDetail != null)
                    runNavigationOnBack();
                else
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void runRefresh(MenuItem item){
        Global.refreshMenuItem = item;
        Global.refreshMenuItem.setActionView(R.layout.action_progressbar);
        Global.REFRESH = true;
        Global.fragmentMain = new MainFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, Global.fragmentMain).commit();
    }

    private void runNavigationOnBack(){
        Global.refreshMenuItem.setVisible(true);
        Global.fragmentDetail = null;
        Global.REFRESH = false;
        //Global.fragmentMain = new MainFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, Global.fragmentMain).commit();
    }

    private void runMainFragment(){
        Global.REFRESH = false;
        Global.fragmentMain = new MainFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, Global.fragmentMain).commit();
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return false;
    }
}
