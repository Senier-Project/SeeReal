package com.three_eung.saemoi;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private InitApp initApp;
    private MainActivity mainActivity;

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Before
    public void setUp() {
        initApp = new InitApp();
        mainActivity = new MainActivity();
        initApp.onCreate();
        initApp.initDatabase();
        mainActivity.onCreate(null);
    }

    @Test
    public void test() {
        Log.e("Log", "test: "+initApp.getInCate());
    }

    @After
    public void finish() {
        mainActivity.onDestroy();
        initApp.terminate();
        initApp.onTerminate();
    }
}
