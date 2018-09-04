package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {

    private Bitmap arkaplan, gemi, dusman_gemi,
            gemi_ates, dusman_gemi_ates;
    //Geminin koordinatlarının ve hızının bulunduğu değişken
    private int gemix, gemiy, gemi_hedefx, gemi_hedefy, gemi_hizy;
    //Düşmanın koordinatlarının ve hızının bulunduğu değişken
    private int dusman_gemix, dusman_gemiy, dusman_gemi_hedefx,
            dusman_gemi_hedefy, dusman_gemi_hizy;
    //Ateş efektinin x ve y koordinatları
    private int gemi_atesx, gemi_atesy,
            dusman_gemi_atesx, dusman_gemi_atesy;
    //Ateş efektinin hedeflerinin x ve y koordinatları
    private int gemi_ates_hedefx, gemi_ates_hedefy,
            dusman_gemi_ates_hedefx, dusman_gemi_ates_hedefy;
    private int gemi_ates_hizx, gemi_ates_hizy,
            dusman_gemi_ates_hizx, dusman_gemi_ates_hizy;
    //Gemilerin birbirlerinin menziline girip girmediklerini ölçen değişken
    private int tetikleyici, dusman_tetikleyici;

    private boolean tetiklendimi, dusman_tetiklendimi, yoket;


    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {
        Log.i(TAG, "setup");
        //Oyuncu gemi parametreleri
        gemix = 160;
        gemiy = 180;
        gemi_hedefx = 160;
        gemi_hedefy = 1230;
        gemi_hizy = 10;
        //Düşman gemi parametreleri
        dusman_gemix = 710;
        dusman_gemiy = 1270;
        dusman_gemi_hedefx = 710;
        dusman_gemi_hedefy = 200;
        dusman_gemi_hizy = 10;
        //
        tetikleyici = 0;
        dusman_tetikleyici = 0;

        tetiklendimi = false;
        dusman_tetiklendimi = false;
        yoket = false;

        gemi_ates_hedefx = 0;
        gemi_ates_hedefy = 0;
        dusman_gemi_ates_hedefx = 0;
        dusman_gemi_ates_hedefy = 0;

        gemi_ates_hizx = 30;
        gemi_ates_hizy = 30;
        dusman_gemi_ates_hizx = 30;
        dusman_gemi_ates_hizy = 30;



        arkaplan = Utils.loadImage(root, "map.png");
        gemi = Utils.loadImage(root, "ship_1_360_01.png");
        dusman_gemi = Utils.loadImage(root, "ship_7_rot_01.png");
        gemi_ates = Utils.loadImage(root, "muzzle_top_blue_01.png");
        dusman_gemi_ates = Utils.loadImage(root, "muzzle_top_red_01.png");
    }

    public void update() {
        Log.i(TAG, "update");

        if (gemiy >= gemi_hedefy) {
            gemiy = gemiy + 0;
        } else {
            gemiy = gemiy + gemi_hizy;
        }

        if (dusman_gemiy <= dusman_gemi_hedefy) {
            dusman_gemiy = dusman_gemiy - 0;
        } else {
            dusman_gemiy = dusman_gemiy - dusman_gemi_hizy;
        }

        tetikleyici = Math.abs(gemiy - dusman_gemiy);
        dusman_tetikleyici = Math.abs(dusman_gemiy - gemiy);

        if (tetikleyici < 400) {
            tetiklendimi = true;
        }

        if (dusman_tetikleyici < 400) {
            dusman_tetiklendimi = true;
        }

        //Ateş etme mekanizmasının başlangıcı
        if (tetiklendimi == false) {
            gemi_atesx = gemix + 0;
            gemi_atesy = gemiy + 54;
        }

        if (tetiklendimi == true) {
            gemi_atesx = gemi_atesx + gemi_ates_hizx + 18;
            gemi_atesy = gemi_atesy + gemi_ates_hizy - 16;
            if ((Math.abs(gemi_atesy - dusman_gemiy)) < 40) {
                tetiklendimi = false;
                yoket = true;
            }
        }

        gemi_ates_hedefx = dusman_gemix;
        gemi_ates_hedefy = dusman_gemiy;



    }

    public void draw(Canvas canvas) {
        Log.i(TAG, "draw");

        canvas.drawBitmap(arkaplan, 0, 0, null);
        canvas.drawBitmap(gemi, gemix, gemiy, null);
        if (yoket == false) {
            canvas.drawBitmap(dusman_gemi, dusman_gemix, dusman_gemiy, null);
        }

        if (tetiklendimi && yoket == false) {
            canvas.drawBitmap(gemi_ates, gemi_atesx, gemi_atesy, null);
        }


    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public boolean backPressed() {
        return true;
    }

    public void surfaceChanged(int width, int height) {

    }

    public void surfaceCreated() {

    }

    public void surfaceDestroyed() {

    }

    public void touchDown(int x, int y, int id) {

    }

    public void touchMove(int x, int y, int id) {

    }

    public void touchUp(int x, int y, int id) {

    }


    public void pause() {

    }


    public void resume() {

    }


    public void reloadTextures() {

    }


    public void showNotify() {

    }

    public void hideNotify() {

    }

}
