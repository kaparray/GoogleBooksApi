package com.kaparray.googlebooks;


import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


import com.kaparray.googlebooks.fragments.GooglingFragments;
import com.kaparray.googlebooks.fragments.HistoryFragment;
import com.kaparray.googlebooks.fragments.MagazineFragment;


public class MainActivity extends AppCompatActivity{


    private GooglingFragments googlingFragments = new GooglingFragments();
    private HistoryFragment historyFragment = new HistoryFragment();
    private MagazineFragment magazineFragment = new MagazineFragment();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_main:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, googlingFragments)
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.action_magazine:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, magazineFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.action_history:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.container, historyFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);





        getSupportFragmentManager()
                .beginTransaction()
                .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, googlingFragments)
                .addToBackStack(null)
                .commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


   // @Override
//    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (v instanceof EditText) {
//                v.clearFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//
//            }
//        }
//
//        return super.dispatchTouchEvent(event);
//    }



}