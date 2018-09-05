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

    private boolean tetiklendimi, dusman_tetiklendimi,
            dusman_gemi_yoket, gemi_yoket;


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
        dusman_gemix = 190;
        dusman_gemiy = 1270;
        dusman_gemi_hedefx = 710;
        dusman_gemi_hedefy = 200;
        dusman_gemi_hizy = 10;
        //
        tetikleyici = 0;
        dusman_tetikleyici = 0;

        tetiklendimi = false;
        dusman_tetiklendimi = false;
        dusman_gemi_yoket = false;
        gemi_yoket = false;

        gemi_ates_hedefx = 0;
        gemi_ates_hedefy = 0;
        dusman_gemi_ates_hedefx = 0;
        dusman_gemi_ates_hedefy = 0;

        gemi_ates_hizx = 30;
        gemi_ates_hizy = 15;
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

        gemiIlerletme();
        dusmanGemiIlerletme();
        menzilTesti();
        atesUretme();
    }

    public void draw(Canvas canvas) {
        Log.i(TAG, "draw");

        arkaplanCizici(canvas);
        gemiOlustur(canvas);
        dusmanGemiOlustur(canvas);
        gemiAtesEtme(canvas);
        dusmanGemiAtesEtme(canvas);


    }

    //Arkaplanı çizen method
    private void arkaplanCizici(Canvas canvas) {
        canvas.drawBitmap(arkaplan, 0, 0, null);
    }

    //Yeni gemi üreten method
    private void gemiUret() {
        gemi_yoket = false;
        dusman_gemi_yoket = false;
        gemix = 160;
        gemiy = 180;
        dusman_gemix = 190;
        dusman_gemiy = 1270;
    }

    //Gemiyi çizen method
    private void gemiOlustur(Canvas canvas) {
        if (gemi_yoket == false) {
            canvas.drawBitmap(gemi, gemix, gemiy, null);
        } else {
            dusman_tetiklendimi = false;
        }
    }

    //Düşman gemisini çizen method
    private void dusmanGemiOlustur(Canvas canvas) {
        if (dusman_gemi_yoket == false) {
            canvas.drawBitmap(dusman_gemi, dusman_gemix, dusman_gemiy, null);
        } else {
            tetiklendimi = false;
        }
    }

    //Oyuncu gemisinin ateşini çizen method
    private void gemiAtesEtme(Canvas canvas) {
        if (tetiklendimi && dusman_gemi_yoket == false) {
            canvas.drawBitmap(gemi_ates, gemi_atesx, gemi_atesy, null);
        }
    }

    //Düşman gemisinin ateşini çizen method
    private void dusmanGemiAtesEtme(Canvas canvas) {
        if (dusman_tetiklendimi && gemi_yoket == false) {
            canvas.drawBitmap(dusman_gemi_ates, dusman_gemi_atesx, dusman_gemi_atesy, null);
        }
    }

    //Oyuncu gemisinin ilerleyişi
    private void gemiIlerletme() {
        if ((gemiy >= gemi_hedefy) || (tetiklendimi == true)) {
            gemiy = gemiy + 0;
        } else {
            gemiy = gemiy + gemi_hizy;
        }
    }

    //Düşman gemisinin ilerleyişi
    private void dusmanGemiIlerletme() {
        if ((dusman_gemiy <= dusman_gemi_hedefy) || (dusman_tetiklendimi == true)) {
            //dusman_gemiy = dusman_gemiy - 0;
        } else {
            dusman_gemiy = dusman_gemiy - dusman_gemi_hizy;
        }
    }

    //İki gemi arasındaki mesafeyi ölçen method
    private void menzilTesti() {
        tetikleyici = Math.abs(gemiy - dusman_gemiy);
        dusman_tetikleyici = Math.abs(dusman_gemiy - gemiy);

        if (tetikleyici < 400) {
            tetiklendimi = true;
        }

        if (dusman_tetikleyici < 400) {
            dusman_tetiklendimi = true;
        }
    }

    //Ateş efektini üreten method
    private void atesUretme() {
        //Ateş etme mekanizmasının başlangıcı
        if (tetiklendimi == false) {
            gemi_atesx = gemix + 35;
            gemi_atesy = gemiy + 54;
        }

        if (tetiklendimi == true) {
            //gemi_atesx = gemi_atesx;
            gemi_atesy = gemi_atesy + gemi_ates_hizy;

            if ((Math.abs(gemi_atesy - dusman_gemiy)) < 40) {
                tetiklendimi = false;
                dusman_gemi_yoket = true;
            }
        }

        if (dusman_tetiklendimi == false) {
            dusman_gemi_atesx = dusman_gemix;
            dusman_gemi_atesy = dusman_gemiy - 100;
        }

        if (dusman_tetiklendimi == true) {
            dusman_gemi_atesy = dusman_gemi_atesy - dusman_gemi_ates_hizy;

            if ((Math.abs(dusman_gemi_atesy - gemiy)) < 40) {
                dusman_tetiklendimi = false;
                gemi_yoket = true;
            }
        }

        gemi_ates_hedefx = dusman_gemix;
        gemi_ates_hedefy = dusman_gemiy;
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
        gemiUret();
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
