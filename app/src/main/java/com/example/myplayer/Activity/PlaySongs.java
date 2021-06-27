package com.example.myplayer.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.myplayer.databinding.ActivityPlaySongsBinding;
public class PlaySongs extends AppCompatActivity {
    ActivityPlaySongsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPlaySongsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}