package com.example.vishwas.voxrec.Fragment;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.vishwas.voxrec.Adapter.Rec_list_adapter;
import com.example.vishwas.voxrec.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class RecordListFragment extends Fragment implements Rec_list_adapter.onItemList_click {

    private ConstraintLayout audio_playersheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView record_list;
    private File[] allFiles;
    private Rec_list_adapter rec_list_adapter;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private File file_toPlay;
    private ImageButton play_btn;
    private ImageButton play_prev_btn;
    private ImageButton play_forw_btn;
    private TextView player_file_name;
    private TextView player_title;
    private SeekBar seekBar;
    private Handler seekbarHandler;
    private Runnable updateseekbar;


    public RecordListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        audio_playersheet = view.findViewById(R.id.playersheet);
        bottomSheetBehavior = BottomSheetBehavior.from(audio_playersheet);
        record_list = view.findViewById(R.id.record_recycler_list);

        play_btn = view.findViewById(R.id.play_btn);
        play_prev_btn = view.findViewById(R.id.play_back_btn);
        play_forw_btn = view.findViewById(R.id.play_forward_btn);
        player_file_name = view.findViewById(R.id.player_file_name);
        player_title = view.findViewById(R.id.player_title);
        seekBar = view.findViewById(R.id.seekbar_player);


        //Storing recording files into Files array

        String rec_path_files = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File dir = new File(rec_path_files);
        allFiles = dir.listFiles();

        rec_list_adapter = new Rec_list_adapter(allFiles,this);

        record_list.setHasFixedSize(true);
        record_list.setLayoutManager(new LinearLayoutManager(getContext()));
        record_list.setAdapter(rec_list_adapter);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                {
                    pauseAudio();
                }
                else {
                    if(file_toPlay!= null)
                    {
                        resumeAudio();
                    }
                }
            }
        });

        play_prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseAudio();
                int curr_pos = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(curr_pos-1000);
                int seek_prog = seekBar.getProgress();
                seekBar.setProgress(seek_prog-10);
                updateRunnable();
                resumeAudio();
            }
        });

        play_forw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseAudio();
                int curr_pos = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(curr_pos+1000);
                int seek_prog = seekBar.getProgress();
                seekBar.setProgress(seek_prog+10);
                updateRunnable();
                resumeAudio();

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

    }

    @Override
    public void onClick_Listener(File file, int position)
    {
        file_toPlay = file;
        if(isPlaying)
        {
            stopAudio();
            playAudio(file_toPlay);
        }
        else
        {
            playAudio(file_toPlay);
        }
    }

    private void stopAudio()
    {
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn,null));
        player_title.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
    }

    private void playAudio(File file_toPlay)
    {


        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(file_toPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn,null));
        player_file_name.setText(file_toPlay.getName());
        player_title.setText("Playing");
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                player_title.setText("Finsihed");
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateseekbar,0);
    }

    private void updateRunnable() {
        updateseekbar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this,500);
            }
        };
    }

    private void pauseAudio()
    {
        mediaPlayer.pause();
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn,null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateseekbar);
    }

    private void resumeAudio()
    {
        mediaPlayer.start();
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn,null));
        isPlaying = true;

        updateRunnable();
        seekbarHandler.postDelayed(updateseekbar,0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying)
        {
            stopAudio();
        }
    }
}