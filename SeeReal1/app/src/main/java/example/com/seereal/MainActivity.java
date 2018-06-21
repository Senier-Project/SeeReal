package example.com.seereal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.testText);

        BottomNavigationView bottomNavigationItemView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               Fragment fragment  = null;
                switch (item.getItemId()) {
                    case R.id.navi_server:
                       // tv.setText("SERVER");
                         fragment = new ServerFragment();
                        break;
                    case R.id.navi_more:
                        fragment = new FriendFragment();
                          //tv.setText("더보기이ㅣ");
                        break;
                    case R.id.navi_freind:
                        fragment = new FriendFragment();

                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
    /*private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }*/


    private boolean loadFrament (android.support.v4.app.Fragment fragment){
        if(fragment != null){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
}

}
