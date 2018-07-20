package example.com.seereal;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class InitApp extends Application{

    private static InitApp self;
    public volatile static FirebaseAuth sAuth;
    public volatile static FirebaseUser sUser;
    public static FirebaseDatabase sDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;
        sAuth = FirebaseAuth.getInstance();
        sUser = sAuth.getCurrentUser();
    }

}
