package example.com.seereal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class DrawOnTop extends View implements View.OnTouchListener{

    private Path    mPath;
    private Paint   mPaint;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private Path re_mPath;
    public String coordX="",coordY="";
    public boolean drawing=true;

    private int color=Color.RED;

    public DrawOnTop(Context context,int color)
    {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        mPath = new Path();
        paths.add(mPath);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        for (Path p : paths){
            canvas.drawPath(p, mPaint);
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {


        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        Utils.mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath = new Path();
        paths.add(mPath);
    }

    public void onClickUndo () {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }
        else
        {

        }
        //toast the user
    }

    public void onClickRedo (){
        if (undonePaths.size()>0)
        {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }
        else
        {

        }
        //toast the user
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                coordX="";
                coordY="";

                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                drawing=false;
                touch_up();
                invalidate();
                break;
        }
        coordX+=x+" ";
        coordY+=y+" ";
        return true;
    }

    public String getCoordX() {
        return coordX;
    }
    public String getCoordY() {
        return coordY;
    }
    public void draw2(Vector<Float> x_v ,Vector<Float> y_v) {


        for(int i=0;i<x_v.size();i++) {
            if(i==0) {
                touch_start(x_v.get(i), y_v.get(i));
                invalidate();
            } else if (i==x_v.size()-1) {
                touch_up();
                invalidate();
            } else {
                touch_move(x_v.get(i), y_v.get(i));
                invalidate();
            }
        }

    }


    public void setColor(int color){
        this.color=color;
    }

    public int getColor(){
        return this.color;
    }


}
