package example.com.seereal;

import android.graphics.Canvas;
import android.view.View;
import android.graphics.Point;


class CanvasShadow extends View.DragShadowBuilder {


    int mWidth, mHeight;


    int mX, mY;


    public CanvasShadow(View v, int x, int y) {


        super(v);


        //좌표를 저장해둠


        mWidth = v.getWidth();


        mHeight = v.getHeight();


        mX = x;


        mY = y;


    }
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint){


        shadowSize.set(mWidth, mHeight);//섀도우 이미지 크기 지정함


        shadowTouchPoint.set(mX, mY);//섀도우 이미지 중심점을 지정함.


    }





    public void onDrawShadow(Canvas canvas){


        super.onDrawShadow(canvas);//이미지 복사


    }


}

