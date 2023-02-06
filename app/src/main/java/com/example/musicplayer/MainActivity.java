package com.example.musicplayer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //members
    RecyclerView recyclerView;
    songAdapter songAdapter;
    List<Song> allSongs =new ArrayList<>();
    ActivityResultLauncher<String> storagePermissionLauncher;
    final  String permission= Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        //set the toolbar and app title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //recyclerView
        recyclerView = findViewById(R.id.recyclerview);
        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted ->{
            if(granted){
                //fetch songs
                fetchSongs();
            }
            else{
                userResponses();
            }
        });


        //launch storage permission on create
        storagePermissionLauncher.launch(permission);
    }

    private void userResponses(){
if(ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED){
    //fetch songs
    fetchSongs();

}
else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
    if(shouldShowRequestPermissionRationale(permission)){
        //show an educational UI to user explaining whu we need this permission
        //user alert dialog
        new AlertDialog.Builder(this).setTitle("Requesting Permission")
                                            .setMessage("Allow us to fetch songs on your device")
                                            .setPositiveButton("allow", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //request permission
                                                    storagePermissionLauncher.launch(permission);

                                                }
                                            })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"you denied us from showing songs",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }
}

else{
Toast.makeText(this,"you canceled showing songs", Toast.LENGTH_SHORT).show();
}
    }




    private void fetchSongs(){
//define a list to store the songs
        List<Song> songs = new ArrayList<>();
        Uri mediaStoreUri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else{
          mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        //define projection
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID,
        };

        //order
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED+"DESC";

        //getting the songs
        try(Cursor cursor = getContentResolver().query(mediaStoreUri,projection,null,null,sortOrder)){
            //cache cursor indexes
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            //clear the previously loaded list before adding again
            while(cursor.moveToNext()){
                //get the values of a column for a given audio file
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                long albumId = cursor.getLong(albumIdColumn);
                
                //song uri 
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                //album artwork uri
                Uri albumrtworkUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);

                //remove .mp3 extension from the song'sname
                name = name.substring(0,name.lastIndexOf("."));


                //song item
                Song song = new Song(name, uri, albumrtworkUri, size, duration);

                //add song item to song list
                 songs.add(song);

            }
            
            //display songs
            showSongs(songs);
        }

    }

    private void showSongs(List<Song> songs) {
        if(songs.size()==0){
            Toast.makeText(this,"No Songs ", Toast.LENGTH_SHORT).show();
            return;
        }

        //save songs
        allSongs.clear();
        allSongs.addAll(songs);

        //update the toolbar title
        String title = getResources().getString(R.string.app_name)+" - "+songs.size();
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);


        //layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //songs adapter

        songAdapter = new songAdapter(this, songs);

        //set the adapter to recyclerview
        recyclerView.setAdapter(songAdapter);




    }
}