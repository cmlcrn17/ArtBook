package com.cerenerdem.artbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView liste;
    static ArrayList<Bitmap> artImage;
    static Bitmap choosenImage;

    //Menüyü Oluştur
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Res içerisinde Menü klasoru içerisinde tanımladığım menümü çağır diyorum.
        MenuInflater menuInflater = getMenuInflater();
        //oluşturduğum menü dosyasını burada bildiriyorum.
        menuInflater.inflate(R.menu.add_art,menu);
        return super.onCreateOptionsMenu(menu);
    }


    //Menünün seçili elemanına tıklandığında şu işlemi yap.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //add_art id li menü item ı seçildiyse... yani Add Art butonuna tıkladığımda ne yapacağım sorusunun cevabını burada tanımlıyorum.
        if (item.getItemId() == R.id.add_art){

            //burada bir intent tanımlıyorum.
        Intent intent = new Intent(getApplicationContext(),Main2Activity.class);

        //yeni bir resim mi seçiliyor? yoksa eski bir resim mi görüntülenmek isteniyor?
        intent.putExtra("info","new");


        startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
    //Menü ile ilgili kodlama bitti.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        liste = (ListView) findViewById(R.id.listv_1);
        final ArrayList<String> artName = new ArrayList<String>();
        artImage = new ArrayList<Bitmap>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,artName);
        liste.setAdapter(arrayAdapter);




        try{

            Main2Activity.database = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null);

            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)");


            Cursor cursor = Main2Activity.database.rawQuery("SELECT * FROM arts", null);
            int nameIx = cursor.getColumnIndex("name");
            int imageIx = cursor.getColumnIndex("image");

            cursor.moveToFirst();

            while (cursor != null){

                artName.add(cursor.getString(nameIx));


                byte[] byteArray = cursor.getBlob(imageIx);
                Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                artImage.add(image);

                cursor.moveToNext();


                //eğer herhangi bir içerikte güncelleme olursa listview güncellenecek
                arrayAdapter.notifyDataSetChanged();

            }




        }catch (Exception e)
        {
            e.toString();
        }


        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("info", "old");
                intent.putExtra("name", artName.get(position));
                //choosenImage=artImage.get(position);
                intent.putExtra("position", position);

                startActivity(intent);
            }
        });

    }
}
