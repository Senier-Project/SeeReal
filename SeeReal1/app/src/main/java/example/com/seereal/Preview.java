package example.com.seereal;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class Preview extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder mHolder;

    Camera mCamera;
    int mw;
    int mh;

    Preview(Context context,int w,int h) {

        super(context);
        mw=w;
        mh=h;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.

        mHolder = getHolder();

        mHolder.addCallback(this);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }


    public void surfaceCreated(SurfaceHolder holder) {

        // The Surface has been created, acquire the camera and tell it where
        // to draw.

        mCamera = Camera.open();

        try{    //수정부분

            mCamera.setPreviewDisplay(holder);

            if (false) throw new IOException();

        }catch (IOException e){

            //do something

        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {

        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.

        mCamera.stopPreview();
        mCamera = null;

    }



    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        // Now that the size is known, set up the camera parameters and begin
        // the preview.

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mw, mh);
        mCamera.setParameters(parameters);
        mCamera.startPreview();

    }

}
