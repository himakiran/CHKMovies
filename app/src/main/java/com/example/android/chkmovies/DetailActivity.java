package com.example.android.chkmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*
    Commenting the below code has succesfully solved the issue of udacity review : "You have inflated detail_activity layout twice.
    Here and in onCreate method. " Instead of the below code which was duplicating the inflation of the layout file activity_detail
    we have added the line <include layout="@layout/content_detail" /> in activty_detail and added the line
    android:name="com.example.android.chkmovies.DetailActivityFragment" in content_detail.
 */
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new DetailActivityFragment())
//                    .commit();
//        }
        /*
                    We now implement a shared preference file to store all favorite movies

             */


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//
//        return super.onOptionsItemSelected(item);
//
//
//
//
//    }
}


