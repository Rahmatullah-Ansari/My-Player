package com.example.myplayer.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.myplayer.R;
import com.example.myplayer.databinding.ActivityPlaySongsBinding;
import java.io.File;
import java.util.ArrayList;
import static java.lang.Thread.sleep;
public class PlaySongs extends AppCompatActivity{
    ActivityPlaySongsBinding binding;
    String songName;
    static MediaPlayer mediaPlayer;
    int pos;
    ArrayList<File> songs;
    Animation animation;
    Thread thread;
    int sessionid;
    String endtime;
    public int repeat_flag=0;
    private String[] items;
    View view;
    ListView listView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPlaySongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mediaPlayer != null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        try {
            songs=(ArrayList)bundle.getParcelableArrayList("songs");
        }catch (Exception e){
            e.printStackTrace();
        }
        pos=bundle.getInt("pos",0);
        binding.songplay.setSelected(true);
        Uri uri=Uri.parse(songs.get(pos).toString());
        songName=songs.get(pos).getName();
        binding.songplay.setText(songName);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        animation = AnimationUtils.loadAnimation(this, R.anim.animation1);
        mediaPlayer.start();
        binding.playimage.startAnimation(animation);
        binding.btnPlay.setOnClickListener(this::onClick);
        binding.seekbar.setMax(mediaPlayer.getDuration());
        Thread thread=new Thread(() -> {
            int totalduration=mediaPlayer.getDuration();
            int currentpos=0;
            while (currentpos<totalduration) {
                try {
                    currentpos = mediaPlayer.getCurrentPosition();
                    binding.seekbar.setProgress(currentpos);
                    sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        binding.seekbar.getProgressDrawable().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.MULTIPLY);
        binding.seekbar.getThumb().setColorFilter(getResources().getColor(R.color.purple_500),
                PorterDuff.Mode.SRC_IN);
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(binding.seekbar.getProgress());
            }
        });
        endtime=createTime(mediaPlayer.getDuration());
        binding.songend.setText(endtime);
        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime=createTime(mediaPlayer.getCurrentPosition());
                binding.songstart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);
        binding.songlist.setOnClickListener(this::onClick2);//songList
        mediaPlayer.setOnCompletionListener(mp -> {
           ChangeSong();
        });
        sessionid=mediaPlayer.getAudioSessionId();
        UpdateUI();
        binding.btnNext.setOnClickListener(view -> {
            PlayNextSong();
        });
        binding.btnPrevious.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            pos=((pos-1)<0)?(songs.size()-1):pos-1;
            Uri uri2=Uri.parse(songs.get(pos).toString());
            mediaPlayer=MediaPlayer.create(this,uri2);
            songName=songs.get(pos).getName();
            binding.songplay.setText(songName);
            mediaPlayer.start();
            binding.songend.setText(endtime);
            sessionid=mediaPlayer.getAudioSessionId();
            UpdateUI();
            binding.playimage.clearAnimation();
            binding.playimage.startAnimation(animation);

            endtime=createTime(mediaPlayer.getDuration());
            binding.songend.setText(endtime);
            final Handler handler1=new Handler();
            final int delay1=1000;
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String currentTime=createTime(mediaPlayer.getCurrentPosition());
                    binding.songstart.setText(currentTime);
                    handler1.postDelayed(this,delay1);
                }
            },delay1);


            binding.seekbar.setMax(mediaPlayer.getDuration());
            Thread thread1=new Thread(() -> {
                int totalduration=mediaPlayer.getDuration();
                int currentpos=0;
                while (currentpos<totalduration) {
                    try {
                        currentpos = mediaPlayer.getCurrentPosition();
                        binding.seekbar.setProgress(currentpos);
                        sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread1.start();
        });
        binding.btnBack.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                UpdateUI();
            }
        });
        binding.btnForward.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
               UpdateUI();
            }
        });
        binding.share.setOnClickListener(v -> {
            Uri uri2 = Uri.parse(songs.get(pos).toString());
            Intent intent1=new Intent(Intent.ACTION_SEND);
            intent1.setType("audio/*");
            intent1.putExtra(Intent.EXTRA_STREAM,uri2);
            startActivity(Intent.createChooser(intent1,"Share Via"));
        });
    }
    private void UpdateUI(){
        if (sessionid !=-1){
            binding.wave.setAudioSessionId(sessionid);
        }
    }
    private void PlayNextSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        pos = ((pos + 1) % songs.size());
        Uri uri1=Uri.parse(songs.get(pos).toString());
        mediaPlayer=MediaPlayer.create(this,uri1);
        songName=songs.get(pos).getName();
        endtime=createTime(mediaPlayer.getDuration());
        binding.songplay.setText(songName);
        mediaPlayer.start();
        binding.songend.setText(endtime);
        sessionid=mediaPlayer.getAudioSessionId();
        UpdateUI();
        binding.playimage.clearAnimation();
        binding.playimage.startAnimation(animation);
        binding.seekbar.setMax(mediaPlayer.getDuration());
        Thread thread=new Thread(() -> {
            int totalduration=mediaPlayer.getDuration();
            int currentpos=0;
            while (currentpos<totalduration) {
                try {
                    currentpos = mediaPlayer.getCurrentPosition();
                    binding.seekbar.setProgress(currentpos);
                    sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private void onClick(View view) {
        if (mediaPlayer.isPlaying()){
            binding.btnPlay.setBackgroundResource(R.drawable.ic_play);
            mediaPlayer.pause();
            binding.playimage.clearAnimation();
        }
        else {
            binding.btnPlay.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
            songName=songs.get(pos).getName();
            binding.playimage.startAnimation(animation);
        }
    }
    public String createTime(int dur){
        String time="";
        int min=dur/1000/60;
        int sec=dur/1000%60;
        time=time+min+":";
        if (sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(binding.wave !=null){
            binding.wave.release();
        }
        super.onDestroy();
    }

    private void onClick2(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        view = LayoutInflater.from(this).inflate(R.layout.song_list_holder, null);
        listView1 = view.findViewById(R.id.listview2);
        AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        items = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            items[i] = songs.get(i).getName()
                    .replace(".mp3", "")
                    .replace(".wav", "");
        }
        SongAdapter songAdapter = new SongAdapter();
        listView1.setAdapter(songAdapter);
        listView1.setOnItemClickListener((parent, view, position, id) -> {
            pos=position;
            binding.songplay.setText(songs.get(pos).getName());
            mediaPlayer.stop();
            mediaPlayer.release();
            Uri uri1 = Uri.parse(songs.get(pos).toString());
            mediaPlayer = MediaPlayer.create(this, uri1);
            endtime = createTime(mediaPlayer.getDuration());
            mediaPlayer.start();
            binding.songend.setText(endtime);
            sessionid = mediaPlayer.getAudioSessionId();
            UpdateUI();
            binding.playimage.clearAnimation();
            binding.playimage.startAnimation(animation);
            binding.seekbar.setMax(mediaPlayer.getDuration());
            Thread thread=new Thread(() -> {
                int totalduration=mediaPlayer.getDuration();
                int currentpos=0;
                while (currentpos<totalduration) {
                    try {
                        currentpos = mediaPlayer.getCurrentPosition();
                        binding.seekbar.setProgress(currentpos);
                        sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                ChangeSong();
            });
            dialog.dismiss();
        });

    }
    public void ChangeSong(){
        pos=((pos+1)%songs.size());
        mediaPlayer.stop();
        mediaPlayer.release();
        Uri uri2=Uri.parse(songs.get(pos).toString());
        mediaPlayer=MediaPlayer.create(this,uri2);
        songName=songs.get(pos).getName();
        endtime=createTime(mediaPlayer.getDuration());
        binding.songplay.setText(songName);
        mediaPlayer.start();
        binding.songend.setText(endtime);
        sessionid=mediaPlayer.getAudioSessionId();
        UpdateUI();
        binding.playimage.clearAnimation();
        binding.playimage.startAnimation(animation);
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