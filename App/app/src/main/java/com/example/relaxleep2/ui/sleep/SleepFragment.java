package com.example.relaxleep2.ui.sleep;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.relaxleep2.R;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.view.PieChartView;

public class SleepFragment extends Fragment {

    private SleepViewModel sleepViewModel;

    // 알림생성버튼
    private Button pushBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sleepViewModel = ViewModelProviders.of(this).get(SleepViewModel.class);

        View root = inflater.inflate(R.layout.fragment_sleep, container, false);

        pushBtn = (Button)root.findViewById(R.id.notifyBtn);

        pushBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                createNotification_channel();
                createNotification();

            }

            // 채널 열기 -------------------------
            private void createNotification_channel() {
                NotificationManager mNotificationManager =(NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                String id = "my_channel_01";
                CharSequence name = "test";

                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);

                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
                mNotificationManager.createNotificationChannel(mChannel);

            }

            private void createNotification() {

                // 열려진 채널을 이용해 NOTI -------------------------
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), "my_channel_01");

                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_timer_black_24dp)
                        .setTicker("Relaxleep")
                        .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                        .setContentTitle("수면 알림")
                        .setContentText("15분 후에 일어나야 합니다.")
                        .setContentInfo("Info");

                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notificationBuilder.build());

            }

        });


        // 날짜 갱신-------------------------
        final TextView date = root.findViewById(R.id.text_sleep);

        sleepViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                date.setText(s);
            }
        });


        // 타임테이블 이미지 추가----------------------
        ImageView timetable = root.findViewById(R.id.imageView_timetable);
        timetable.setImageResource(R.drawable.bg_24_timetable);

        // 파이차트-----------------------
        final PieChartView pieChartView = root.findViewById(R.id.chart);

        sleepViewModel.getPiechart().observe(this, new Observer<PieChartData>() {
            @Override
            public void onChanged(@Nullable PieChartData pieChartData) {
                pieChartView.setPieChartData(pieChartData);
                pieChartView.setChartRotation(270, false);
                pieChartView.setChartRotationEnabled(false);
            }
        });

        // 예상수면시간----------------------------------
        final TextView expectedTime_label = root.findViewById(R.id.expected_Sleeptime_label);

        sleepViewModel.getExpectedSleepTime_label().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                expectedTime_label.setText(s);
            }
        });

        final TextView expectedTime = root.findViewById(R.id.expected_Sleeptime);

        sleepViewModel.getExpectedSleepTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                expectedTime.setText(s);
            }
        });

        // 총 수면시간----------------------------------
        final TextView totalTime = root.findViewById(R.id.total_Sleeptime);

        sleepViewModel.getTotalSleepTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                totalTime.setText(s);
            }
        });

        return root;
    }
}