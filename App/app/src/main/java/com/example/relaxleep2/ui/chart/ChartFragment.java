package com.example.relaxleep2.ui.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.relaxleep2.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartFragment extends Fragment {

    private ChartViewModel chartViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chartViewModel =
                ViewModelProviders.of(this).get(ChartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chart, container, false);


        // 날짜 갱신-------------------------
        final TextView textView = root.findViewById(R.id.text_chart);

        chartViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        // 라인차트-------------------------------
        final LineChartView lineChartView = root.findViewById(R.id.chart1);

        chartViewModel.getLinechart().observe(this, new Observer<LineChartData>() {
            @Override
            public void onChanged(@Nullable LineChartData lineChartData) {
                lineChartView.setLineChartData(lineChartData);
            }
        });

        // 실제수면시간----------------------------------
        final TextView textView4 = root.findViewById(R.id.real_Sleeptime);

        chartViewModel.getRealSleepTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView4.setText(s);
            }
        });

        // 평균수면시간----------------------------------
        final TextView textView5 = root.findViewById(R.id.average_Sleeptime);

        chartViewModel.getAverageSleepTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView5.setText(s);
            }
        });

        // 수면 정보----------------------------------
        final TextView textView6 = root.findViewById(R.id.info_Sleeptime);

        chartViewModel.getInfoSleepTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView6.setText(s);
            }
        });

        return root;
    }
}