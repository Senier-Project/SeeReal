package example.com.seereal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {


    //  TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationItemView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(this);
        bottomNavigationItemView.setSelectedItemId(R.id.navi_freind);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navi_server:
                // tv.setText("SERVER");
                fragment = new ServerFragment();
                break;
            case R.id.navi_more:
                fragment = new MoreFragment();
                //tv.setText("더보기이ㅣ");
                break;
            case R.id.navi_freind:
                fragment = FriendFragment.newInstance();
                break;

            default:
                return false;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }
}


