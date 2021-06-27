package com.example.myplayer.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.FlingAnimation;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myplayer.R;
import com.example.myplayer.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.File;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private String []items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RequestPermissions();
    }

    private void RequestPermissions() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                ShowSongs();
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "Permission is Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList=new ArrayList<>();
        File[] files=file.listFiles();
        for (File file1:files){
            if (file1.isDirectory() && ! file1.isHidden()){
                arrayList.addAll(findSong(file1));
            }
            else {
                if (file1.getName().endsWith(".mp3") || file1.getName().endsWith(".mp4") || file1.getName().endsWith(".wav")){
                    arrayList.add(file1);
                }
            }
        }
        return arrayList;
    }
    public void ShowSongs(){
        final ArrayList<File> songs=findSong(Environment.getExternalStorageDirectory());
        items=new String[songs.size()];
        for (int i=0;i<songs.size();i++){
            items[i]= songs.get(i).getName().toString()
                    .replace(".mp3","")
                    .replace(".mp4","")
                    .replace(".wav","");
        }
        SongAdapter songAdapter=new SongAdapter();
        binding.listview.setAdapter(songAdapter);
        binding.listview.setOnItemClickListener((parent, view, position, id) -> {
            String sname=(String)binding.listview.getItemAtPosition(position);
            startActivity(new Intent(MainActivity.this,PlaySongs.class).putExtra("songs",songs)
            .putExtra("name",sname).putExtra("pos",position));
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
            View view=getLayoutInflater().inflate(R.layout.songs_item,null);
            TextView textView=view.findViewById(R.id.song_name);
            textView.setSelected(true);
            textView.setText(items[position]);
            return view;
        }
    }
}