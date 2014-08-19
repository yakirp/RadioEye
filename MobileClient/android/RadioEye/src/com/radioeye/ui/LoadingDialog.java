package com.radioeye.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InputMismatchException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.BadTokenException;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidviewhover.BlurLayout;
import com.daimajia.androidviewhover.tools.Blur;
import com.radioeye.R;
import com.radioeye.utils.AppPreferences;

public class LoadingDialog extends Dialog {


    private   BlurLayout.AppearListener callback;
    private Activity activity;

    private BlurLayout hoverLayout;





    public LoadingDialog(Activity context) {
        super(context);
        this.activity = context;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(false);
        setContentView(R.layout.progressdialog);



        //  View hover2 = this.activity.getLayoutInflater().inflate(R.layout.hover_progress, null);
        // View hover2 =   LayoutInflater.from( this.activity).inflate(R.layout.hover_progress, null);
        View hover2 = getLayoutInflater().inflate(R.layout.hover_progress, null);
        hoverLayout = (BlurLayout) findViewById(R.id.blur_layout_progress);
        hoverLayout.setHoverView(hover2,false);




        hoverLayout.addChildAppearAnimator(hover2, R.id.profileImage, Techniques.FadeInDown, 1200);
        hoverLayout.addChildDisappearAnimator(hover2, R.id.profileImage, Techniques.FadeOutUp);

        hoverLayout.addChildAppearAnimator(hover2, R.id.progressBar1, Techniques.FadeInUp, 1200);
        hoverLayout.addChildDisappearAnimator(hover2, R.id.progressBar1, Techniques.FadeOutDown);

        hoverLayout.showHover(callback);
    }


    public void start(BlurLayout.AppearListener callback) {
        this.callback = callback;

      show();
    }

    @Override
    public void show() {
        super.show();


    }

    @Override
    public void hide() {

        close();

        super.hide();
    }

    public void close() {


        if (hoverLayout.getHoverStatus()== BlurLayout.HOVER_STATUS.APPEARED || hoverLayout.getHoverStatus()== BlurLayout.HOVER_STATUS.APPEARING) {

            System.out.println("close close close ");


            new Thread(new Runnable() {
                public void run() {
                    while (hoverLayout.getHoverStatus() != BlurLayout.HOVER_STATUS.APPEARED) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }

                    }

                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            hoverLayout.dismissHover(new BlurLayout.DisappearListener() {
                                @Override
                                public void onDisappearStart() {

                                }

                                @Override
                                public void onDisappearEnd() {
                                        dismiss();
                                }
                            });
                        }

                    });

                }
            }).start();


        }



        }




}
