package com.three_eung.saemoi;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.three_eung.saemoi.tabs.HomeTab;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public static MainActivity mainActivity;

    @ClassRule
    public static ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void setUp() throws Exception {
        mainActivity = mActivityRule.getActivity();
        mainActivity.setFirebase();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch(InterruptedException e) {}
            }
        };
        thread.start();
        thread.join();
    }

    @Test
    public void testValue1() throws Exception {
        int testValue = 0;
        for(Fragment fragment : mainActivity.getSupportFragmentManager().getFragments()) {
            if(fragment.getArguments() != null) {
                if(fragment.getArguments().get("TAG").equals("HomeTab")) {
                    testValue = ((HomeTab)fragment).getTestSaving();
                }
            }
        }

        assertEquals(25000, testValue);
    }

    @Test
    public void testValue2() throws Exception {
        Map<String, Integer> mList = ((InitApp)mainActivity.getApplication()).getBudget();
        for(String key : mList.keySet()) {
            if(key.equals("total")) {
                assertEquals(300000, (int)mList.get(key));
            }
        }
    }

    @AfterClass
    public static void finish() {
        mActivityRule.finishActivity();
    }
}
