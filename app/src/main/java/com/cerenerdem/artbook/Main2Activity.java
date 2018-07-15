package com.cerenerdem.artbook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {


    ImageView imageView;
    EditText edt_name;
    Button btn_save;
    Bitmap selectedImage;

    static SQLiteDatabase database;//static tanımladığımız için her yerden ulaşabiliriz.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = (ImageView) findViewById(R.id.imgv_1);
        edt_name =(EditText) findViewById(R.id.edt_Name);
        btn_save = (Button) findViewById(R.id.btn_Save);



        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if (info.equalsIgnoreCase("new")) {

            btn_save.setVisibility(View.VISIBLE);
            edt_name.setText("");

        } else {

            String name = intent.getStringExtra("name");
            edt_name.setText(name);

            int position = intent.getIntExtra("position", 0);
            imageView.setImageBitmap(MainActivity.artImage.get(position));

            btn_save.setVisibility(View.INVISIBLE);
        }
    }


    //galeriden resim seçmek için imageview üzerine tıklandığında yapılacak işlem
    @SuppressLint("NewApi")
    public void select(View view){

        //izin alma işlemleri
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);

        } else {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }

        //izin alma işlemleri bitti
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //galeriden resim seçme sonucu gelecek olan sonuç değerine göre işlem yapılacaktır.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK && data != null){

            //sonuç olarak bize dönen resmi image değişkenine atıyorum.
            Uri image = data.getData();
            try {
                //seçilen resmi imageview içerisinde göster
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view){


        String artName = edt_name.getText().toString(); //ismi al.

        //resimleri liste içerisine al.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();





        try {

            //database oluştur.
            database=this.openOrCreateDatabase("Arts", MODE_PRIVATE, null);
            //tablo yoksa oluştur.
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)");
            //veri ekle
            //database.execSQL("INSERT INTO arts (name, image) VALUES (?, ?)");



            //veri ekleme sorgusunu string içerisine aldık.
            String sqlString = "INSERT INTO arts (name, image) VALUES (?, ?)";
            SQLiteStatement statement = database.compileStatement(sqlString);

            //birinci index 1. soru işaretidir ve hangi değişkenin içerisine kaydolacağını burada veririz.
            statement.bindString(1,artName);
            statement.bindBlob(2,byteArray);

            //sorguyu çalıştır.
            statement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);


    }
}
