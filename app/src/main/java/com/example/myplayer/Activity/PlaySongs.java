package com.example.myplayer.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.myplayer.R;
import com.example.myplayer.databinding.ActivityPlaySongsBinding;

import java.io.File;
import java.util.ArrayList;

public class PlaySongs extends AppCompatActivity {
    ActivityPlaySongsBinding binding;
    String songName;
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> songs;
    Animation animation;
    Thread thread;
    int sessionid;
    String endtime;
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
        position=bundle.getInt("pos",0);
        binding.songplay.setSelected(true);
        Uri uri=Uri.parse(songs.get(position).toString());
        songName=songs.get(position).getName();
        binding.songplay.setText(songName);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        animation = AnimationUtils.loadAnimation(this, R.anim.animation1);
        mediaPlayer.start();
        binding.playimage.startAnimation(animation);
        binding.btnPlay.setOnClickListener(this::onClick);
        thread=new Thread(){
            @Override
            public void run() {
                int totalduration=mediaPlayer.getDuration();
                int currentpos=0;
                while (currentpos<=totalduration){
                    try {
                        sleep(500);
                        currentpos=mediaPlayer.getCurrentPosition();
                        binding.seekbar.setProgress(currentpos);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        binding.seekbar.setMax(mediaPlayer.getDuration());
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
        mediaPlayer.setOnCompletionListener(mp -> {
            if(mediaPlayer.getCurrentPosition()==songs.size()){
                position=0;
            }
            else{
                position=((position+1)%songs.size());
            }
            mediaPlayer.reset();
            Uri uri1=Uri.parse(songs.get(position).toString());
            mediaPlayer=MediaPlayer.create(this,uri1);
            songName=songs.get(position).getName();
            endtime=createTime(mediaPlayer.getDuration());
            binding.songplay.setText(songName);
            mediaPlayer.start();
            binding.songend.setText(endtime);
            sessionid=mediaPlayer.getAudioSessionId();
            if (sessionid !=-1){
                binding.wave.setAudioSessionId(sessionid);
            }
            binding.playimage.clearAnimation();
            binding.playimage.startAnimation(animation);
        });
        sessionid=mediaPlayer.getAudioSessionId();
        if (sessionid !=-1){
        binding.wave.setAudioSessionId(sessionid);
        }
        binding.btnNext.setOnClickListener(view -> {
            PlayNextSong();
        });
        binding.btnPrevious.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position-1)<0)?(songs.size()-1):position-1;
            Uri uri2=Uri.parse(songs.get(position).toString());
            mediaPlayer=MediaPlayer.create(this,uri2);
            songName=songs.get(position).getName();
            binding.songplay.setText(songName);
            mediaPlayer.start();
            binding.songend.setText(endtime);
            sessionid=mediaPlayer.getAudioSessionId();
            if (sessionid !=-1){
                binding.wave.setAudioSessionId(sessionid);
            }
            binding.playimage.clearAnimation();
            binding.playimage.startAnimation(animation);
        });
        binding.btnBack.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                if (sessionid !=-1){
                    binding.wave.setAudioSessionId(sessionid);
                }
            }
        });
        binding.btnForward.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                if (sessionid !=-1){
                    binding.wave.setAudioSessionId(sessionid);
                }
            }
        });
    }
    private void PlayNextSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        position = ((position + 1) % songs.size());
        Uri uri1=Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri1);
        songName=songs.get(position).getName();
        endtime=createTime(mediaPlayer.getDuration());
        binding.songplay.setText(songName);
        mediaPlayer.start();
        binding.songend.setText(endtime);
        sessionid=mediaPlayer.getAudioSessionId();
        if (sessionid !=-1){
            binding.wave.setAudioSessionId(sessionid);
        }
        binding.playimage.clearAnimation();
        binding.playimage.startAnimation(animation);
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
            songName=songs.get(position).getName();
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
}