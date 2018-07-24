package com.three_eung.saemoi;

import android.content.Context;
import android.util.AttributeSet;

public class CustomLoginButton extends com.kakao.usermgmt.LoginButton {
    public CustomLoginButton(Context context) {
        super(context);
    }

    public CustomLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 로그인 버튼 클릭시 세션을 오픈하도록 설정한다.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        inflate(getContext(), R.layout.kakao_login_layout, this);
    }
}
