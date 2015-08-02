package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.IOException;

public class DisplayImageActivity extends Activity{
    private int screenWidth;
    private int screenHeight;
    private int stickerNumber;
    private int i;
    private ImageView currentImage = null;
    private ImageView[] imageView;
    RelativeLayout mainLayout;
    private ImageView myImage;
    private static final int sticker = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        mainLayout = (RelativeLayout)findViewById(R.id.stickerView);
        myImage =(ImageView)findViewById(R.id.imageView);
        i = 0;
        stickerNumber = 5;
        imageView = new ImageView[stickerNumber+1];
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 100;
        currentImage = myImage;
        myImage.setOnTouchListener(movingEventListener);

        (findViewById(R.id.chooser))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        chooseSticker();
                    }
                });
        (findViewById(R.id.add))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        testAdd();
                    }
                });
        (findViewById(R.id.undo))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        testUndo();
                    }
                });
        (findViewById(R.id.clear))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        clearStickers();
                    }
                });

        /*
        final Uri uri = getIntent().getData();
        String filePath = getPath(uri);
        System.out.print(filePath);
        myImage.setImageBitmap(getBitmap(filePath));
        */
        createBack();

        /*
        Send image to the next activity.
         */
        (findViewById(R.id.button04))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        //shareImage(uri);
                        //uri移动到了子模块，这里暂时注释掉了
                    }
                });

        }
    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //print(""+requestCode);
        //print(""+resultCode);
        switch (requestCode){
            case sticker:
                Bundle stickerBundle = data.getExtras();
                String stickerPosition = stickerBundle.getString("id");
                print(stickerPosition);
                testAddSticker(stickerPosition);
                break;
            /*
            default:
                createBack();
                break;
            */
        }
    }
     /*
         * Receive image uri. Get image path. Display image.
         **/

    private Uri createBack(){
        final Uri uri = getIntent().getData();
        String filePath = getPath(uri);
        System.out.print(filePath);
        myImage.setImageBitmap(getBitmap(filePath));
        return uri;
    }

    private void chooseSticker() {
        Intent intent = new Intent();
        intent.setClass(DisplayImageActivity.this, Sticker_Selector.class);
        startActivityForResult(intent,sticker);
    }

    //add a new sticker in layer 9, high layers imported to lower one and import the newest into layer 9
    /*public void newSticker(Bitmap nS){
        if(imageView[9]==null){
            imageView[9].setImageBitmap(nS);
        }else{
            imageView[5].setImageBitmap(combine(imageView[5],imageView[6]));
            for (int i=6;i<9;i++){
                imageView[i].setImageDrawable(imageView[i+1].getDrawable());

            }
            imageView[9].setImageBitmap(nS);
        }
    }*/

    public Bitmap getBitmap(String filePath){
        int degree = readPictureDegree(filePath);
        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inSampleSize=2;
        Bitmap bitmapOld = BitmapFactory.decodeFile(filePath, opts);
        return rotatingImageView(degree, bitmapOld);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //combine two imageView and output a bitmap
    public Bitmap combine (ImageView a, ImageView b){
        Bitmap combined=null;
        try{
            //output resolution needed _Ree
            combined = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(combined);
            Resources res = getResources();
            //a.setBounds(100, 100, 400, 400);
            //b.setBounds(150, 150, 350, 350);
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
        output = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(output);
        for(int i=0;i<=10;i++){
            imageView[i].getDrawable().draw(c);
        }
        return output;
    }

    //undo a sticker
    public void undoSticker(){
        for(int a= i;a>=0;a--){
            if(i==0){
                //print("Can not undo");
                Toast.makeText(getApplicationContext(), "Can not undo", Toast.LENGTH_SHORT).show();
            }else{
            if(imageView[a].getDrawable()== null){
                print(a + " is null");
            }else{
                imageView[a].setImageDrawable(null);
                mainLayout.removeView(imageView[a]);
                i--;
                break;
            }
            }
        }
    }

    //clear all stickers
    public void clearStickers(){
        for(int a= i;a>0;a--){
            imageView[a].setImageDrawable(null);
            mainLayout.removeView(imageView[a]);
        }
        i = 0;
    }

    //print debug info
    public void print(String info){
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
    }

    //save bitmap to local
    public void saveBitmap(Bitmap bmp, String fileName){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //test method
    public void testAdd(){
        if(i<stickerNumber){
            i++;
            imageView[i] = new ImageView(this);
            Bitmap mBitmap = getResource(i);
            imageView[i].setImageBitmap(mBitmap);
            imageView[i].setOnTouchListener(movingEventListener);
            //imageView[i].setBackgroundResource(R.drawable.border);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp1.height = 500;
            lp1.width = 500;
            //lp1.addRule(RelativeLayout.ALIGN_TOP);
            //lp1.setMargins(200,400,0,0);//(int left, int top, int right, int bottom)
            mainLayout.addView(imageView[i],lp1);}
        else{
            print("MAX");
        }
    }

    //test method
    public void testAddSticker(String name){
            //int i = Integer.parseInt(name);
            imageView[i] = new ImageView(this);
            Bitmap mBitmap = getResource(i);
            imageView[i].setImageBitmap(mBitmap);
            imageView[i].setOnTouchListener(movingEventListener);
            //imageView[i].setBackgroundResource(R.drawable.border);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp1.height = 500;
            lp1.width = 500;
            //lp1.addRule(RelativeLayout.ALIGN_TOP);
            //lp1.setMargins(200,400,0,0);//(int left, int top, int right, int bottom)
            mainLayout.addView(imageView[i],lp1);

    }

    public void testUndo(){
        undoSticker();
        //print("test undo");
    }
    /*public void testExport(){
        saveBitmap(outputImage(imageView), "testFile");
        print("test export");
    }*/

    public Bitmap getResource(int i){
        String name="a"+i;
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(),resID);
        //return null;
    }
    /*
     * Get image rotate degree
     **/
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * Rotate image
     **/
    public static Bitmap rotatingImageView(int angle , Bitmap bitmap) {

        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, 0, 0,
                bWidth, bHeight, matrix, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void shareImage(Uri uri){
        Intent intent = new Intent();
        intent.setClass(DisplayImageActivity.this, ShareImageActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }




    private OnTouchListener movingEventListener = new OnTouchListener() {
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RelativeLayout.LayoutParams myLayout = (RelativeLayout.LayoutParams) v.getLayoutParams();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(v!=myImage){
                        currentImage.setBackground(null);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        v.setBackgroundResource(R.drawable.border);
                        currentImage =(ImageView) v;}
                    if(v == myImage){
                        currentImage.setBackground(null);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;

                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }

                    myLayout.setMargins(left,top,0,0);
                    v.setLayoutParams(myLayout);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
}


