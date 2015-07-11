package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.ImageView;


/**
 * Created by Ree on 2015/7/9.
 */
public class ImageOperation extends Activity {
    private ImageView[] imageView;

    private Bitmap photoBitmap, stickerBitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView[0]=(ImageView)findViewById(R.id.imageView);//origin image
        imageView[2]=(ImageView)findViewById(R.id.imageView2);//enhanced image 1
        imageView[3]=(ImageView)findViewById(R.id.imageView3);//enhanced image 2
        imageView[4]=(ImageView)findViewById(R.id.imageView4);//enhanced image 3
        imageView[5]=(ImageView)findViewById(R.id.imageView5);//combined stickers layer
        imageView[6]=(ImageView)findViewById(R.id.imageView6);//sticker layer 1
        imageView[7]=(ImageView)findViewById(R.id.imageView7);//sticker layer 2
        imageView[8]=(ImageView)findViewById(R.id.imageView8);//sticker layer 3
        imageView[9]=(ImageView)findViewById(R.id.imageView9);//sticker layer 4
        imageView[10]=(ImageView)findViewById(R.id.imageView10);//border layer


    }

    //add a new sticker in layer 9, high layers imported to lower one and import the newest into layer 9
    public void newSticker(Bitmap nS){
        imageView[0].setImageBitmap(combine(imageView[5],imageView[6]));
        for (int i=6;i<10;i++){
            imageView[i]=imageView[i+1];
        }
        imageView[9].setImageBitmap(nS);
    }

    //combine two imageView and output a bitmap
    public Bitmap combine (ImageView a, ImageView b){
        Bitmap combined=null;
        try{
            //output resolution needed _Ree
            combined = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(combined);
            Resources res = getResources();
            //drawable1.setBounds(100, 100, 400, 400);
            //drawable2.setBounds(150, 150, 350, 350);
            a.getDrawable().draw(c);
            b.getDrawable().draw(c);
        }
        catch (Exception e){
        }
        return combined;
    }

    //combine all the layers into a bitmap
    public Bitmap outputImage (ImageView[] imageView){
        Bitmap output=null;
        output = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(output);
        for(int i=0;i<=10;i++){
            imageView[i].getDrawable().draw(c);
        }
        return output;
    }
}


