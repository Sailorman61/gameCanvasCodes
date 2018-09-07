package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Vector;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {

    private static final int GEMIOZELLIGI_TUR = 0, GEMIOZELLIGI_POSX = 1, GEMIOZELLIGI_POSY = 2,
            GEMIOZELLIGI_CAN = 3, GEMIOZELLIGI_GUC = 4;

    private final int gemi_turu_adedi = 4;
    private Bitmap arkaplan, gemi_resmi[],
            gemi_ates, dusman_gemi_ates;
    //Geminin koordinatlarının ve hızının bulunduğu değişken
    private int gemix, gemiy,
            gemi_hedefx, gemi_hedefy,
            gemi_hizy, can, guc;
    //Düşmanın koordinatlarının ve hızının bulunduğu değişken
    private int dusman_gemix, dusman_gemiy, dusman_gemi_hedefx,
            dusman_gemi_hedefy, dusman_gemi_hizy;
    //Ateş efektinin x ve y koordinatları
    private int[] gemi_atesx, gemi_atesy,
            dusman_gemi_atesx, dusman_gemi_atesy;
    //Ateş efektinin hedeflerinin x ve y koordinatları
    private int gemi_ates_hedefx, gemi_ates_hedefy,
            dusman_gemi_ates_hedefx, dusman_gemi_ates_hedefy;
    private int gemi_ates_hizx, gemi_ates_hizy,
            dusman_gemi_ates_hizx, dusman_gemi_ates_hizy;
    //Gemilerin birbirlerinin menziline girip girmediklerini ölçen değişken
    private int[] tetikleyici, dusman_tetikleyici;

    private int[] baslangicx, baslangicy, sinirx, siniry;
    private int gemi_sec1;
    private int gecikme;

    private boolean[] ates_edildimi, dusman_ates_edildimi,
            dusman_gemi_yoket, gemi_yoket;

    private Vector<Vector<Integer>> oyuncu_gemileri;
    private Vector<Vector<Integer>> dusman_gemileri;
    private Vector<Vector<Integer>> mermiler;
    private Vector<Vector<Integer>> dusman_mermileri;

    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {
        Log.i(TAG, "setup");
        //Oyuncu gemi resmi parametreleri
        gemix = 160;
        gemiy = 180;
        gemi_hedefx = 160;
        gemi_hedefy = 1230;
        gemi_hizy = 10;
        //Düşman gemi resmi parametreleri
        dusman_gemix = 190;
        dusman_gemiy = 1270;
        dusman_gemi_hedefx = 710;
        dusman_gemi_hedefy = 200;
        dusman_gemi_hizy = 10;
        gemi_ates_hedefx = 0;
        gemi_ates_hedefy = 0;
        dusman_gemi_ates_hedefx = 0;
        dusman_gemi_ates_hedefy = 0;
        gemi_ates_hizx = 30;
        gemi_ates_hizy = 15;
        dusman_gemi_ates_hizx = 30;
        dusman_gemi_ates_hizy = 30;
        gemi_sec1 = 0;
        can = 10;
        guc = 2;
        gecikme = 0;
        tetikleyici = new int[10];
        dusman_tetikleyici = new int[10];
        gemi_atesx = new int[10];
        gemi_atesy = new int[10];
        dusman_gemi_atesx = new int[10];
        dusman_gemi_atesy = new int[10];

        //Üretileceği gemiyi seçen koordinatlar
        baslangicx = new int[]{70, 325, 580, 835};
        baslangicy = new int[]{1680, 1680, 1680, 1680};
        sinirx = new int[]{300, 555, 810, 1065};
        siniry = new int[]{1900, 1900, 1900, 1900};

        ates_edildimi = new boolean[10];
        dusman_ates_edildimi = new boolean[10];
        dusman_gemi_yoket = new boolean[10];
        gemi_yoket = new boolean[10];

        arkaplan = Utils.loadImage(root, "map.png");
        gemi_resmi = new Bitmap[gemi_turu_adedi];
        gemi_resmi[0] = Utils.loadImage(root, "ship_1_360_01.png");
        gemi_resmi[1] = Utils.loadImage(root, "ship_7_rot_01.png");
        gemi_resmi[2] = Utils.loadImage(root, "ship_6_ver1_02.png");
        gemi_resmi[3] = Utils.loadImage(root, "ship_3_01.png");
        gemi_ates = Utils.loadImage(root, "muzzle_top_blue_01.png");
        dusman_gemi_ates = Utils.loadImage(root, "muzzle_top_red_01.png");

        oyuncu_gemileri = new Vector<Vector<Integer>>();
        dusman_gemileri = new Vector<Vector<Integer>>();
        mermiler = new Vector<Vector<Integer>>();
        dusman_mermileri = new Vector<Vector<Integer>>();
    }

    public void update() {
        gemileriIlerlet();
        dusmanGemileriniIlerlet();
        dusmanGemiUret();
        menzilTestleriniYap();
        atesEt();
    }

    public void draw(Canvas canvas) {
        arkaplaniCiz(canvas);
        slotlariCiz(canvas);
        gemileriCiz(canvas);
        dusmanGemileriniCiz(canvas);
        mermileriCiz(canvas);
        dusmanMermileriniCiz(canvas);
    }

    //Arkaplanı çizen method
    private void arkaplaniCiz(Canvas canvas) {
        canvas.drawBitmap(arkaplan, 0, 0, null);
    }

    /**
     * Bu metot yeni bir gemi üreterek çizime hazır hale getirmektedir.
     * Üretilecek geminin türü gemi_türü parametresiyle gönderilir.
     * Gemi üretildikten sonra gemiler vektörüne eklenerek hazır hale getirilir.
     *
     * @param gemi_turu Üretilecek geminin türü
     */
    private void gemiUret(int gemi_turu) {
        //gemi_yoket = false;
        gemix = 160;
        gemiy = 180;
        Vector<Integer> yeni_gemi = new Vector<>();
        yeni_gemi.add(gemi_turu);
        yeni_gemi.add(gemix);
        yeni_gemi.add(gemiy);
        yeni_gemi.add(can);
        yeni_gemi.add(guc);
        oyuncu_gemileri.add(yeni_gemi);
    }

    //Yeni düşman gemisi üreten method
    private void dusmanGemiUret() {
        if (gecikme < 50) {
            gecikme++;
            return;
        }
        gecikme = 0;
        int gemi_turu = (int)(Math.random() * 4);
        if (dusman_gemileri.size() < 10) {
            //dusman_gemi_yoket = false;
            dusman_gemix = 190;
            dusman_gemiy = 1270;
            Vector<Integer> dusman_yeni_gemi = new Vector<>();
            dusman_yeni_gemi.add(gemi_turu);
            dusman_yeni_gemi.add(dusman_gemix);
            dusman_yeni_gemi.add(dusman_gemiy);
            dusman_yeni_gemi.add(can);
            dusman_yeni_gemi.add(guc);
            dusman_gemileri.add(dusman_yeni_gemi);
        }
    }

    //Gemiyi çizen method
    private void gemileriCiz(Canvas canvas) {
        for (int i = 0; i < oyuncu_gemileri.size(); i++) {
            canvas.drawBitmap(
                    gemi_resmi[oyuncu_gemileri.get(i).get(GEMIOZELLIGI_TUR)],
                    oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSX) + (((400 - oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSX))
                            - gemi_resmi[oyuncu_gemileri.get(i).get(GEMIOZELLIGI_TUR)].getWidth()) / 2),
                    oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSY),
                    null);
            //Yukarıdaki 400 yazan yer geminin oluştuğu yerin x ekseninin sınırı.
            //Yukarıdaki 430 yazan yer geminin oluştuğu yerin y ekseninin sınırı.
        }
    }

    private void slotlariCiz(Canvas canvas) {
        for (int i = 0; i < gemi_turu_adedi; i++) {
            canvas.drawBitmap(gemi_resmi[i],
                    baslangicx[i] + (((sinirx[i] - baslangicx[i]) - gemi_resmi[i].getWidth()) / 2),
                    baslangicy[i] + (((siniry[i] - baslangicy[i]) - gemi_resmi[i].getHeight()) / 2),
                    null);
        }
    }

    //Düşman gemisini çizen method
    private void dusmanGemileriniCiz(Canvas canvas) {
        for (int i = 0; i < dusman_gemileri.size(); i++) {
            canvas.drawBitmap(gemi_resmi[dusman_gemileri.get(i).get(GEMIOZELLIGI_TUR)],
                    dusman_gemileri.get(i).get(GEMIOZELLIGI_POSX)
                            + (((370 - dusman_gemileri.get(i).get(GEMIOZELLIGI_POSX))
                            - gemi_resmi[dusman_gemileri.get(i).get(GEMIOZELLIGI_TUR)].getWidth()) / 2),
                    dusman_gemileri.get(i).get(GEMIOZELLIGI_POSY),
                    null);

        }
    }

    //Oyuncu gemisinin ateşini çizen method
    private void mermileriCiz(Canvas canvas) {
        for (int i = 0; i < oyuncu_gemileri.size(); i++) {
            for (int j = 0; j < dusman_gemileri.size(); j++) {
                if (ates_edildimi[i] && !dusman_gemi_yoket[j]) {
                    canvas.drawBitmap(gemi_ates, gemi_atesx[i], gemi_atesy[i], null);
                }
            }
        }
    }

    //Düşman gemisinin ateşini çizen method
    private void dusmanMermileriniCiz(Canvas canvas) {
        /*
        if (dusman_ates_edildimi && gemi_yoket == false) {
            canvas.drawBitmap(dusman_gemi_ates, dusman_gemi_atesx, dusman_gemi_atesy, null);
        }
        */
    }

    /**
     * Gemiler vektörüne kayıtlı oyuncu gemileri geminin hızı kadar ilerletilir.
     * Gemi hızları piksel cinsinden verilmelidir.
     * Eğer geminin y değeri hedef y'den büyükse veya gemi ateş ediyorsa ilerletme işlemi yapılmaz.
     */
    private void gemileriIlerlet() {
        for (int i = 0; i < oyuncu_gemileri.size(); i++) {
            if ((oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSY) < gemi_hedefy) && (!ates_edildimi[i]))
                oyuncu_gemileri.get(i).set(GEMIOZELLIGI_POSY, oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSY) + gemi_hizy);
        }
    }

    //Düşman gemisinin ilerleyişi
    private void dusmanGemileriniIlerlet() {
        for (int i = 0; i < dusman_gemileri.size(); i++) {
            if ((dusman_gemileri.get(i).get(GEMIOZELLIGI_POSY) > dusman_gemi_hedefy)
                    && (!dusman_ates_edildimi[i]))
                dusman_gemileri.get(i).set(GEMIOZELLIGI_POSY, dusman_gemileri.get(i).get(GEMIOZELLIGI_POSY)
                        - dusman_gemi_hizy);
        }
    }

    //İki gemi_resmi arasındaki mesafeyi ölçen method
    private void menzilTestleriniYap() {
        if (oyuncu_gemileri.isEmpty() || dusman_gemileri.isEmpty())
            return;

        if ((!oyuncu_gemileri.isEmpty()) && (!dusman_gemileri.isEmpty())) {
        for (int i = 0; i < oyuncu_gemileri.size(); i++) {
            for (int j = 0; j < dusman_gemileri.size(); j++) {
                tetikleyici[i] = Math.abs(oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSY)
                        - dusman_gemileri.get(j).get(GEMIOZELLIGI_POSY));
                dusman_tetikleyici[j] = Math.abs(dusman_gemileri.get(j).get(GEMIOZELLIGI_POSY)
                        - oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSY));

                if (tetikleyici[i] < 400) {
                    ates_edildimi[i] = true;
                }

                if (dusman_tetikleyici[j] < 400) {
                    dusman_ates_edildimi[j] = true;
                }
            }
        }
        }
    }

    //Ateş efektini üreten method
    private void atesEt() {
        //Ateş etme mekanizmasının başlangıcı
        for (int i = 0; i < oyuncu_gemileri.size(); i++) {
            for (int j = 0; j < dusman_gemileri.size(); j++) {
                if (!ates_edildimi[i]) {
                    gemi_atesx[i] = oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSX) + 35;
                    gemi_atesy[i] = oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSY) + 54;
                }

                if (ates_edildimi[i]) {
                    //gemi_atesx = gemi_atesx;
                    gemi_atesy[i] = gemi_atesy[i] + gemi_ates_hizy;

                    if ((Math.abs(gemi_atesy[i] - dusman_gemiy)) < 40) {
                        ates_edildimi[i] = false;
                        dusman_gemi_yoket[j] = true;
                        dusman_gemileri.remove(j);
                    }
                }

                if (!dusman_ates_edildimi[j]) {
                    dusman_gemi_atesx[j] = dusman_gemileri.get(j).get(GEMIOZELLIGI_POSX);
                    dusman_gemi_atesy[j] = dusman_gemileri.get(j).get(GEMIOZELLIGI_POSY) - 100;
                }

                if (dusman_ates_edildimi[j]) {
                    dusman_gemi_atesy[j] = dusman_gemi_atesy[j] - dusman_gemi_ates_hizy;

                    if ((Math.abs(dusman_gemi_atesy[j] - oyuncu_gemileri.get(i).get(GEMIOZELLIGI_POSY))) < 40) {
                        dusman_ates_edildimi[j] = false;
                        gemi_yoket[i] = true;
                    }
                }
                gemi_ates_hedefx = dusman_gemileri.get(j).get(GEMIOZELLIGI_POSX);
                gemi_ates_hedefy = dusman_gemileri.get(j).get(GEMIOZELLIGI_POSY);
            }
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
        if (y > 1679 && y < 1900) {
            gemi_sec1 = (x - 70) / 255;
            if (oyuncu_gemileri.size() < 10) {
                gemiUret(gemi_sec1);
            }
        }
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
