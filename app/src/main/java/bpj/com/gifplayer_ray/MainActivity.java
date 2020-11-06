package bpj.com.gifplayer_ray;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    GifHandler gifHandler;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        init();
        getPermission();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mNextFrame = gifHandler.updateFrame(bitmap);
            handler.sendEmptyMessageDelayed(1, mNextFrame);
            imageView.setImageBitmap(bitmap);
        }
    };


    public void ndkLoadGif(View view) {
        load("demo.gif");
    }

    public void ndkLoadGif2(View view) {
        load("demo2.gif");
    }

    private void load(String name){
        File file = new File(Environment.getExternalStorageDirectory(), name);
        gifHandler = new GifHandler(file.getAbsolutePath());
        Log.i("tuch", "ndkLoadGif: " + file.getAbsolutePath());
        //得到gif   width  height  生成Bitmap
        int width = gifHandler.getWidth();
        int height = gifHandler.getHeight();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int nextFrame = gifHandler.updateFrame(bitmap);
        handler.sendEmptyMessageDelayed(1, nextFrame);
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private void getPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }

            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        while ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED) {
        }
    }



    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                write("demo.gif");
                write("demo2.gif");
            }
        }).start();
    }

    private void write(String name) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), name);
            if (!file.exists()) {
                InputStream in = getAssets().open(name);
                OutputStream os = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[8192];
                while ((len = in.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
