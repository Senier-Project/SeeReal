package example.com.seereal;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public static int width;
    public static int height;
    //  TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        BottomNavigationView bottomNavigationItemView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(this);
        bottomNavigationItemView.setSelectedItemId(R.id.navi_freind);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

       /* mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();
                switch(id){
                    case R.id.nav_notify :
                        Toast.makeText(MainActivity.this, "공지사항", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_myPage :
                        Toast.makeText(MainActivity.this, "마이페이지", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_setting :
                        Toast.makeText(MainActivity.this, "설정", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_sign_out :
                        Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                        break;

                }
                return true;
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.navi_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
       if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
           mDrawerLayout.closeDrawer(GravityCompat.START);
       }
       else{
           super.onBackPressed();
       }
    }
}


