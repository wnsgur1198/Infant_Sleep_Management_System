package com.example.relaxleep2.ui.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.relaxleep2.R;

public class GuideFragment extends Fragment {

    private GuideViewModel guideViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        guideViewModel =
                ViewModelProviders.of(this).get(GuideViewModel.class);
        View root = inflater.inflate(R.layout.fragment_guide, container, false);
        final TextView tv_Time1 = root.findViewById(R.id.txt_Time1);
        final TextView tv_Time2 = root.findViewById(R.id.txt_Time2);
        final TextView tv_Time3 = root.findViewById(R.id.txt_Time3);

        guideViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                tv_Time1.setText(s);
//                tv_Time2.setText(s);
//                tv_Time3.setText(s);
            }
        });
        return root;
    }
}