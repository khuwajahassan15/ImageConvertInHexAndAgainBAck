package com.sis.selectimagetohexreverse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    Button encodebtn,decodebtn;
    ImageView img;
    TextView base64;
    String x="";
    public static final int GALLERY_REQUEST =1;
    public static final int REQUEST_WRITE_PERMISSION = 786;



    public static final int CAMERA_REQUEST = 101;
    public static final int CONTACT_VIEW = 202;
    Bitmap selectImgBitMap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        encodebtn=findViewById(R.id.encode);
        decodebtn=findViewById(R.id.decode);
        img=findViewById(R.id.imageView);
        base64=findViewById(R.id.textView);
        base64.setMovementMethod(new ScrollingMovementMethod());


        encodebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                }


            }
        });

        decodebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(x.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Getting Error",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Bitmap bitmap=ImageToBase16Hex.base16StringToBitMap(x);
                    img.setImageBitmap(bitmap);
                }
            }
        });




    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){


            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        selectImgBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);

                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }


                    File file=null;
                    try {
                        file=savebitmap(selectImgBitMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(file!=null)
                    {
                        long size=file.length()/1024;
                        int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                        Log.e("Size",String.valueOf(file_size));
                        if(file_size>8) {
                            Toast.makeText(getApplicationContext(),"Image Size Should be Less then 5kb",Toast.LENGTH_LONG).show();

                            // If the image is higher than max number of bytes, start all over again.
                        }else
                        {
                            // img.setImageBitmap(selectImgBitMap);
                            x=ImageToBase16Hex.bitMapToBase16String(selectImgBitMap);
                            Log.e("ImageString:Leng", String.valueOf(x.length()));
                            Log.e("ImageString",x);
                            base64.setText(x);
                        }
                    }




                    break;
            }




        }




    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        }
    }
    public static File savebitmap(Bitmap bmp) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "testimage.jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }

}