package com.example.vishwas.voxrec.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vishwas.voxrec.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final int AUDIO_PERMISSION_CODE = 89 ;
    private NavController navController;
    private ImageButton list_btn;
    private ImageButton record_btn;
    private boolean is_recording = false;
    private String recording_permission = Manifest.permission.RECORD_AUDIO;
    private MediaRecorder mediaRecorder;
    private String record_file;
    private Chronometer chronoTimer;
    private TextView record_file_name;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        list_btn = view.findViewById(R.id.record_list_button);
        record_btn = view.findViewById(R.id.record_button);
        chronoTimer = view.findViewById(R.id.record_timer);
        record_file_name = view.findViewById(R.id.record_filename);

        list_btn.setOnClickListener(this);
        record_btn.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.record_list_button:
                if (is_recording)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            navController.navigate(R.id.action_recordFragment_to_recordListFragment);
                        }
                    });

                    alertDialog.setNegativeButton("Cancel",null);

                    alertDialog.setTitle("Audio still recording");
                    alertDialog.setMessage("Are you sure, you want to stop recording?");
                    alertDialog.create().show();
                }
                else
                {
                    navController.navigate(R.id.action_recordFragment_to_recordListFragment);
                }

                break;
            case R.id.record_button:
                if (is_recording)
                {//Stop Recording

                    //Stop Recording Method
                    stop_recording();
                    record_btn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped));
                    is_recording = false;
                }
                else
                {
                    //Start Recording
                    if(checkAudioPermission()) {
                        //Start Recording Method
                        start_recording();
                        record_btn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording));
                        is_recording = true;
                    }
                }
                break;
        }
    }

    private void start_recording()
    {
        chronoTimer.setBase(SystemClock.elapsedRealtime());
        chronoTimer.start();



        String rec_path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_YYYY_hh_mm_ss", Locale.CANADA);
        Date date = new Date();

        record_file = "voxRec"+ simpleDateFormat.format(date) +".3gp";

        record_file_name.setText("Recording File Name: \n"+ record_file);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(rec_path + "/" + record_file);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }


    private void stop_recording()
    {
        chronoTimer.stop();
        is_recording = false;
        record_file_name.setText("Recording Stopped, File Saved: \n"+ record_file);



        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private boolean checkAudioPermission()
    {
        if(ActivityCompat.checkSelfPermission(getContext(),recording_permission )== PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recording_permission}, AUDIO_PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(is_recording) {
            stop_recording();
        }
    }
}