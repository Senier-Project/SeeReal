package com.three_eung.saemoi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.three_eung.saemoi.R;

public class LoadingDialog extends Dialog {
    private Context mContext;
    private ImageView imgLoad;

    public LoadingDialog (Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        imgLoad = (ImageView) findViewById(R.id.load_img);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_load);
        imgLoad.setAnimation(anim);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
