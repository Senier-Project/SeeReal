package com.three_eung.saemoi.tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.three_eung.saemoi.Events;
import com.three_eung.saemoi.InitApp;
import com.three_eung.saemoi.R;
import com.three_eung.saemoi.Utils;
import com.three_eung.saemoi.bind.SavingBind;
import com.three_eung.saemoi.databinding.FragmentSavingBinding;
import com.three_eung.saemoi.databinding.ItemSavingBinding;
import com.three_eung.saemoi.dialogs.SavingInputDialog;
import com.three_eung.saemoi.dialogs.SavingListDialog;
import com.three_eung.saemoi.infos.SavingInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;

public class SavingTab extends Fragment implements View.OnClickListener {
    private static final String TAG = SavingTab.class.getSimpleName();

    private FragmentSavingBinding mBinding;
    private SavingAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private DatabaseReference mRef;
    private ArrayList<SavingInfo> mSavingList;

    private int totalSaving;

    public static Fragment newInstance() {
        SavingTab savingTab = new SavingTab();

        return savingTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_saving, container, false);

        mSavingList = ((InitApp)(getActivity().getApplication())).getSavingList();

        mBinding.saveUid.setText(InitApp.sUser.getDisplayName());

        mAdapter = new SavingAdapter(this.getContext(), mSavingList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mBinding.saveList.getChildAdapterPosition(v);

                SavingInfo savingInfo = mSavingList.get(position);

                showList(savingInfo);
            }
        }, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = mBinding.saveList.getChildAdapterPosition(v);

                SavingInfo savingInfo = mSavingList.get(position);

                modifyItem(savingInfo);

                return true;
            }
        });

        layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mBinding.saveList.setHasFixedSize(true);
        mBinding.saveList.setLayoutManager(layoutManager);
        mBinding.saveList.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.getContext(), layoutManager.getOrientation());
        mBinding.saveList.addItemDecoration(dividerItemDecoration);

        mRef = InitApp.sDatabase.getReference("users").child(InitApp.sUser.getUid()).child("saving");

        mBinding.saveFab.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_fab:
                FragmentManager fm = getChildFragmentManager();
                SavingInputDialog inputDialog = SavingInputDialog.newInstance(new SavingInputDialog.InfoListener() {
                    @Override
                    public void onDataInputComplete(SavingInfo savingInfo) {
                        if (savingInfo != null) {
                            mRef.push().setValue(savingInfo);
                        }
                    }
                });
                inputDialog.show(fm, "InputDialogSaving");
                break;
        }
    }

    private void showList(SavingInfo savingInfo) {
        FragmentManager fm = getChildFragmentManager();
        SavingListDialog.newInstance(savingInfo).show(fm, "SavingList");
    }

    private void modifyItem(final SavingInfo savingInfo) {
        AlertDialog.Builder choiceDialogBuilder = new AlertDialog.Builder(this.getContext());
        choiceDialogBuilder.setTitle("삭제")
                .setMessage("메시지를 삭제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mRef.child(savingInfo.getId()).removeValue();
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        final AlertDialog choiceDialog = choiceDialogBuilder.create();

        final CharSequence[] listItem = {"삭제"};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
        alertDialogBuilder.setTitle("메뉴");
        alertDialogBuilder.setItems(listItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                switch (id) {
                    case 0:
                        choiceDialog.show();
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void setTotal() {
        totalSaving = 0;

        for (SavingInfo savingInfo : mSavingList) {
            totalSaving += savingInfo.getValue() * savingInfo.getCount();
        }

        mBinding.saveTotal.setText(Utils.toCurrencyFormat(totalSaving));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.mSavingList = events.getSvList();
        setTotal();
        if (mAdapter != null) {
            mAdapter.setItems(mSavingList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e(TAG, "onStart: ");

        mSavingList = ((InitApp)(getActivity().getApplication())).getSavingList();
        setTotal();
        if (mAdapter != null) {
            mAdapter.setItems(mSavingList);
            mAdapter.notifyDataSetChanged();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.ViewHolder> {
        private Context mContext;
        private ArrayList<SavingInfo> items;
        private View.OnClickListener mListener;
        private View.OnLongClickListener mLongListener;

        public SavingAdapter(Context context, ArrayList<SavingInfo> items, View.OnClickListener mListener, View.OnLongClickListener mLongListener) {
            this.mContext = context;
            this.items = items;
            this.mListener = mListener;
            this.mLongListener = mLongListener;
        }

        public void setItems(ArrayList<SavingInfo> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemSavingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_saving, parent, false);

            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final int itempos = position;

            int unit = items.get(itempos).getValue();

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SavingInfo savingInfo = items.get(itempos);
                    String key = savingInfo.getId();
                    int count = savingInfo.getCount() + 1;
                    ArrayList<String> savedDate = savingInfo.getSavedDate();

                    if (savedDate == null)
                        savedDate = new ArrayList<>();

                    Calendar calendar = Calendar.getInstance();
                    savedDate.add(Utils.dateToString(calendar.getTime()));

                    SavingInfo temp = new SavingInfo(savingInfo.getTitle(), savingInfo.getValue(), count, savingInfo.getStartDate(), savedDate);

                    mRef.child(key).setValue(temp);
                }
            };

            viewHolder.itemBinding.setSaving(new SavingBind(items.get(itempos).getTitle(), Utils.toCurrencyString(unit), Utils.toCurrencyString(items.get(itempos).getCount()*unit), listener));
        }

        @Override
        public int getItemCount() {
            if (items != null)
                return items.size();
            else
                return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSavingBinding itemBinding;

            public ViewHolder(ItemSavingBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;

                if(mListener != null) {
                    itemBinding.getRoot().setOnClickListener(mListener);
                }

                if (mLongListener != null) {
                    itemBinding.getRoot().setOnLongClickListener(mLongListener);
                }
            }
        }
    }
}
