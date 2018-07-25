package com.three_eung.saemoi;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.three_eung.saemoi.infos.CategoryInfo;
import com.three_eung.saemoi.infos.HousekeepInfo;
import com.three_eung.saemoi.infos.SavingInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by CH on 2018-02-18.
 */

public class InitApp extends Application {
    private static InitApp self;
    public volatile static FirebaseAuth sAuth;
    public volatile static FirebaseUser sUser;
    public static FirebaseDatabase sDatabase;
    private ArrayList<HousekeepInfo> mHousekeepList;
    private ArrayList<SavingInfo> mSavingList;
    private HashMap<String, Integer> mInCate, mExCate;
    private DatabaseReference mHousekeepRef, mSavingRef, mInfoRef, mCateRef;
    private ChildEventListener mHousekeepListener, mSavingListener, mBudgetListener;
    private ValueEventListener mCateListener;
    private CategoryInfo defaultCate = null, customCate = null;
    private HashMap<String, Integer> mBudget, mCategory;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return self;
                    }
                };
            }
        });

        sAuth = FirebaseAuth.getInstance();
        sUser = sAuth.getCurrentUser();
    }

    public void initDatabase() {
        sDatabase = FirebaseDatabase.getInstance();

        mHousekeepList = new ArrayList<>();
        mSavingList = new ArrayList<>();
        mInCate = new HashMap<>();
        mExCate = new HashMap<>();
        mBudget = new HashMap<>();

        mHousekeepRef = this.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("housekeeping");
        mSavingRef = this.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("saving");
        mInfoRef = this.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("info").child("budget");
        mCateRef = this.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("info").child("category");
        this.sDatabase.getReference().child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                defaultCate = dataSnapshot.getValue(CategoryInfo.class);
                updateCate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mHousekeepListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HousekeepInfo housekeepInfo = dataSnapshot.getValue(HousekeepInfo.class);
                housekeepInfo.setId(dataSnapshot.getKey());
                mHousekeepList.add(housekeepInfo);

                updateData();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (int i = 0; i < mHousekeepList.size(); i++) {
                    if (mHousekeepList.get(i).getId().equals(dataSnapshot.getKey())) {
                        HousekeepInfo housekeepInfo = dataSnapshot.getValue(HousekeepInfo.class);
                        housekeepInfo.setId(dataSnapshot.getKey());

                        mHousekeepList.set(i, housekeepInfo);
                    }
                }
                updateData();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HousekeepInfo toRemove = new HousekeepInfo();

                for (Iterator<HousekeepInfo> iterator = mHousekeepList.iterator(); iterator.hasNext(); ) {
                    HousekeepInfo housekeepInfo = iterator.next();

                    if (housekeepInfo.getId().equals(dataSnapshot.getKey())) {
                        toRemove = housekeepInfo;
                    }
                }

                mHousekeepList.remove(toRemove);

                updateData();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };

        mSavingListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SavingInfo savingInfo = dataSnapshot.getValue(SavingInfo.class);
                savingInfo.setId(dataSnapshot.getKey());
                mSavingList.add(savingInfo);

                updateData();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (int i = 0; i < mSavingList.size(); i++) {
                    if (mSavingList.get(i).getId().equals(dataSnapshot.getKey())) {
                        SavingInfo savingInfo = dataSnapshot.getValue(SavingInfo.class);
                        savingInfo.setId(dataSnapshot.getKey());

                        mSavingList.set(i, savingInfo);
                    }
                }

                updateData();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                SavingInfo toRemove = new SavingInfo();

                for (Iterator<SavingInfo> iterator = mSavingList.iterator(); iterator.hasNext(); ) {
                    SavingInfo savingInfo = iterator.next();

                    if (savingInfo.getId().equals(dataSnapshot.getKey())) {
                        toRemove = savingInfo;
                    }
                }

                mSavingList.remove(toRemove);

                updateData();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mBudgetListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mBudget.put(dataSnapshot.getKey(), (int) (long) dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mBudget.put(dataSnapshot.getKey(), (int) (long) dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mBudget.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mCateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customCate = dataSnapshot.getValue(CategoryInfo.class);
                updateCate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mHousekeepRef.addChildEventListener(mHousekeepListener);
        mSavingRef.addChildEventListener(mSavingListener);
        mInfoRef.addChildEventListener(mBudgetListener);
        mCateRef.addValueEventListener(mCateListener);
    }

    private void updateData() {
        EventBus.getDefault().post(new Events((ArrayList<HousekeepInfo>) mHousekeepList.clone(), (ArrayList<SavingInfo>) mSavingList.clone()));
    }

    public ArrayList<HousekeepInfo> getHousekeepList() {
        return (ArrayList<HousekeepInfo>) mHousekeepList.clone();
    }

    public ArrayList<SavingInfo> getSavingList() {
        return (ArrayList<SavingInfo>) mSavingList.clone();
    }

    public Map<String, Integer> getBudget() {
        return (HashMap<String, Integer>) mBudget.clone();
    }

    public void terminate() {
        mHousekeepRef.removeEventListener(mHousekeepListener);
        mSavingRef.removeEventListener(mSavingListener);
        mInfoRef.removeEventListener(mBudgetListener);
        mCateRef.removeEventListener(mCateListener);
        mHousekeepList = null;
        mSavingList = null;
    }

    private void updateCate() {
        mInCate.clear();
        mExCate.clear();

        if (defaultCate != null) {
            if (defaultCate.getIn() != null) {
                HashMap<String, Integer> tmpList = defaultCate.getIn();
                for (String tmp : tmpList.keySet()) {
                    mInCate.put(tmp.toString(), tmpList.get(tmp));
                }
            }

            if (defaultCate.getEx() != null) {
                HashMap<String, Integer> tmpList = defaultCate.getEx();
                for (String tmp : tmpList.keySet()) {
                    mExCate.put(tmp.toString(), tmpList.get(tmp));
                }
            }
        }

        if (customCate != null) {
            if (customCate.getIn() != null) {
                HashMap<String, Integer> tmpList = customCate.getIn();
                for (String tmp : tmpList.keySet()) {
                    mInCate.put(tmp.toString(), tmpList.get(tmp));
                }
            }

            if (customCate.getEx() != null) {
                HashMap<String, Integer> tmpList = customCate.getEx();
                for (String tmp : tmpList.keySet()) {
                    mExCate.put(tmp.toString(), tmpList.get(tmp));
                }
            }
        }
    }

    public HashMap<String, Integer> getInCate() {
        return (HashMap<String, Integer>) mInCate.clone();
    }

    public HashMap<String, Integer> getExCate() {
        return (HashMap<String, Integer>) mExCate.clone();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
