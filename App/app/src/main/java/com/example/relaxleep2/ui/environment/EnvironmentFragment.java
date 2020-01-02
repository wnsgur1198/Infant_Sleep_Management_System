package com.example.relaxleep2.ui.environment;

import android.annotation.SuppressLint;
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

public class EnvironmentFragment extends Fragment {

    private EnvironmentViewModel environmentViewModel;
    // 알림생성버튼
    private Button pushBtn;

    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        Toast.makeText(this.getContext(), "This is SearchFragment", Toast.LENGTH_SHORT).show();
//        View v = inflater.inflate(R.layout.fragment_search, container, false);
//        ImageView FrontImage = (ImageView) v.findViewById(R.id.FBear);

        environmentViewModel =
                ViewModelProviders.of(this).get(EnvironmentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_environment, container, false);

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
                String id = "my_channel_02";
                CharSequence name = "test_env";

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
                        .setContentTitle("환경 위험 알림")
                        .setContentText("주위가 너무 밝습니다.")
                        .setContentInfo("Info");

                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notificationBuilder.build());

            }

        });


        // 이산화탄소 가져오기
        final TextView tv_CO2 = root.findViewById(R.id.value_CO2);
        environmentViewModel.getCo2().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_CO2.setText(s);
            }
        });

        // 온도 가져오기
        final TextView tv_Temp = root.findViewById(R.id.value_Temp);
        environmentViewModel.getTemp().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_Temp.setText(s);
            }
        });

        // 습도 가져오기
        final TextView tv_Humi = root.findViewById(R.id.value_Humi);
        environmentViewModel.getHumi().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_Humi.setText(s);
            }
        });

        // 조도 가져오기
//        final TextView tv_Lux = root.findViewById(R.id.badge);
//        environmentViewModel.getLux().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                tv_Lux.setText(s);
//            }
//        });

        // 이산화탄소에 따른 가이드 표시
        final TextView tv_CO22 = root.findViewById(R.id.value_CO22);
        environmentViewModel.getGuideCo2().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_CO22.setText(s);
            }
        });

        // 온도에 따른 가이드 표시
        final TextView tv_Temp2 = root.findViewById(R.id.value_Temp2);
        environmentViewModel.getGuideTemp().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_Temp2.setText(s);
            }
        });

        // 습도에 따른 가이드 표시
        final TextView tv_Humi2 = root.findViewById(R.id.value_Humi2);
        environmentViewModel.getGuideHumi().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_Humi2.setText(s);

            }
        });

        // 조도에 따른 가이드 표시
        final TextView tv_Lux2 = root.findViewById(R.id.value_Lux2);
        final ImageView iv_Lux = root.findViewById(R.id.MBear);

        environmentViewModel.getGuideLux().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_Lux2.setText(s);
                if (s=="주위가 너무 밝습니다."){
                    iv_Lux.setX(750f);
                }
                else if (s=="주위가 밝습니다."){
                    iv_Lux.setX(400f);
                }
                else if (s=="적당합니다."){
                    iv_Lux.setX(0);
                }
            }
        });

        final ImageView bar = root.findViewById(R.id.view_Lux);
        //final ImageView bear = root.findViewById(R.id.badge);

        environmentViewModel.getText().observe(this, new Observer<String>() {
            @SuppressLint("ResourceType")
            @Override
            public void onChanged(@Nullable String s) {
                bar.setImageResource(R.raw.bar);
                //bear.setImageResource(R.raw.bear);
            }
        });
        return root;
    }
}