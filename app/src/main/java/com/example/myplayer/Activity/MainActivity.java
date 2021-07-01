package com.example.myplayer.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myplayer.R;
import com.example.myplayer.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private String[] items;
    SharedPreferences sharedPref;
    ArrayList<File> songs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RequestPermissions();
    }
    private void RequestPermissions() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                ShowSongs();
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences sharedPreferences=getSharedPreferences("icon",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("iconclick",false);
        editor.apply();
        sharedPref=getSharedPreferences("playstate",MODE_PRIVATE);
        boolean val=sharedPref.getBoolean("isplay",false);
        if (val){
            binding.cardView2.setVisibility(View.VISIBLE);
            binding.playimage.setOnClickListener(v -> {
                SharedPreferences sharedPreferences1=getSharedPreferences("icon",MODE_PRIVATE);
                SharedPreferences.Editor editor1=sharedPreferences1.edit();
                editor1.putBoolean("iconclick",true);
                editor1.apply();
                startActivity(new Intent(MainActivity.this,PlaySongs.class).putExtra("songs",songs)
                        .putExtra("pos",0));
            });
        }
        else {
            binding.cardView2.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences1=getSharedPreferences("icon",MODE_PRIVATE);
        SharedPreferences.Editor editor1=sharedPreferences1.edit();
        editor1.putBoolean("iconclick",false);
        editor1.apply();
        sharedPref=getSharedPreferences("playstate",MODE_PRIVATE);
        boolean val=sharedPref.getBoolean("isplay",false);
        if (val){
            binding.cardView2.setVisibility(View.VISIBLE);
            binding.playimage.setOnClickListener(v -> {
                SharedPreferences sharedPreferences=getSharedPreferences("icon",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("iconclick",true);
                editor.apply();
                startActivity(new Intent(MainActivity.this,PlaySongs.class).putExtra("songs",songs)
                        .putExtra("pos",0));
            });
        }
        else {
            binding.cardView2.setVisibility(View.GONE);
        }
    }

    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList=new ArrayList<>();
        File[] files=file.listFiles();
        assert files != null;
        for (File file1:files){
            if (file1.isDirectory() && ! file1.isHidden()){
                arrayList.addAll(findSong(file1));
            }
            else {
                if (file1.getName().endsWith(".mp3") ||
                        file1.getName().endsWith(".wav")
                        ||file1.getName().endsWith(".m4a")||file1.getName().endsWith(".aac")
                ||file1.getName().endsWith(".mp4")||file1.getName().endsWith(".flac")||file1.getName().endsWith(".wma")){
                    arrayList.add(file1);
                }
            }
        }
        return arrayList;
    }
    public void ShowSongs(){
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.show();
        songs=findSong(Environment.getExternalStorageDirectory());
        items=new String[songs.size()];
        for (int i=0;i<songs.size();i++){
            items[i]= songs.get(i).getName()
                    .replace(".mp3","")
                    .replace(".wav","");
        }
        SongAdapter songAdapter=new SongAdapter();
        binding.listview.setAdapter(songAdapter);
        progressDialog.dismiss();
        binding.listview.setOnItemClickListener((parent, view, position, id) -> {
            String sname=(String)binding.listview.getItemAtPosition(position);
            startActivity(new Intent(MainActivity.this,PlaySongs.class).putExtra("songs",songs)
            .putExtra("pos",position));
        });
    }
    class SongAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view=getLayoutInflater().inflate(R.layout.songs_item,null);
            TextView textView=view.findViewById(R.id.song_name);
            textView.setSelected(true);
            textView.setText(items[position]);
            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences=getSharedPreferences("playstate",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isplay",false);
        editor.apply();
        SharedPreferences sharedPreferences1=getSharedPreferences("icon",MODE_PRIVATE);
        SharedPreferences.Editor editor1=sharedPreferences1.edit();
        editor1.putBoolean("iconclick",false);
        editor1.apply();
    }
}