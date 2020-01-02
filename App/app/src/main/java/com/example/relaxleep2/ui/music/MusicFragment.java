package com.example.relaxleep2.ui.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.relaxleep2.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class MusicFragment extends Fragment {

    private MusicViewModel musicViewModel;

    // MediaPlayer 객체생성
    MediaPlayer mediaPlayer;

    // 시작버튼
    Button v1Button;
    Button v2Button;
    Button v3Button;

    Button p1Button;

    Button s1Button;
    Button s2Button;
    Button s3Button;

    Button n1Button;
    Button n2Button;
    Button n3Button;

    //종료버튼
    Button stopButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        musicViewModel =
                ViewModelProviders.of(this).get(MusicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_music, container, false);

//        Button b = root.findViewById(R.id.button3);
//        b.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//
//                Context c = v.getContext();
//
//                MediaPlayer m = MediaPlayer.create(c , R.raw.v1 );
//                m.start();
//
//                m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp)
//                    {
//                        mp.stop();
//                        mp.release();
//                    }
//                });
//
//
//            }
//        });

        v1Button = root.findViewById(R.id.button3);
        v2Button = root.findViewById(R.id.button);
        v3Button = root.findViewById(R.id.button2);
        p1Button = root.findViewById(R.id.button12);
        s1Button = root.findViewById(R.id.button8);
        s2Button = root.findViewById(R.id.button7);
        s3Button = root.findViewById(R.id.button9);
        n1Button = root.findViewById(R.id.button6);
        n2Button = root.findViewById(R.id.button5);
        n3Button = root.findViewById(R.id.button4);

        stopButton = root.findViewById(R.id.button13);

        // 비닐소리 ----------------------------------------------
        v1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.v1);
                mediaPlayer.start();
            }
        });

        // 드라이기소리 ----------------------------------------------
        v3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.v3);
                mediaPlayer.start();
            }
        });

        // 청소기소리 ----------------------------------------------
        v2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.v2);
                mediaPlayer.start();
            }
        });

        // 자장가소리1 ----------------------------------------------
        s1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.s1);
                mediaPlayer.start();
            }
        });

        // 자장가소리2 ----------------------------------------------
        s2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.s2);
                mediaPlayer.start();
            }
        });

        // 자장가소리3 ----------------------------------------------
        s3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.s3);
                mediaPlayer.start();
            }
        });


        // 부모목소리 ----------------------------------------------
        p1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.p1);
                mediaPlayer.start();
            }
        });

        // 새소리 ----------------------------------------------
        n1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.n1);
                mediaPlayer.start();
            }
        });

        // 바람소리 ----------------------------------------------
        n2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.n2);
                mediaPlayer.start();
            }
        });

        // 물소리 ----------------------------------------------
        n3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 할당
                Context c = view.getContext();
                mediaPlayer = MediaPlayer.create(c, R.raw.n3);
                mediaPlayer.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 정지버튼
                mediaPlayer.stop();
                // 초기화
                mediaPlayer.reset();
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // MediaPlayer 해지
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}