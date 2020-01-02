package com.example.relaxleep2.ui.streaming;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.relaxleep2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class StreamingFragment extends Fragment {

    private StreamingViewModel streamingViewModel;

    // 영상스트리밍
    private WebView webView;
    private WebSettings webSettings;

    // 캡처사진 번호
    private int imageNum = 1;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // 웹뷰 캡처 위한 메소드 사용 위함
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        streamingViewModel =
                ViewModelProviders.of(this).get(StreamingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_streaming, container, false);

        // 스트리밍 영상 웹뷰 설정-------------------------------
        webView = root.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);

        webSettings = webView.getSettings();
        webSettings.getJavaScriptEnabled();

//        webView.loadUrl("http://192.168.123.70:8090/?action=stream");
        webView.loadUrl("http://192.168.43.162:8090/?action=stream");
//        webView.loadUrl("http://www.naver.com");


        // 캡처 버튼 클릭 메소드 추가-----------------------
        final ImageButton imageButton = root.findViewById(R.id.imageBtn);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("capture", "capture");

                try {
                    webView.measure(MeasureSpec.makeMeasureSpec(
                            MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    webView.layout(0, 0, webView.getMeasuredWidth(),
                            webView.getMeasuredHeight());

                    webView.setDrawingCacheEnabled(true);
                    webView.buildDrawingCache();
                    Bitmap bm = Bitmap.createBitmap(1080,
                            1500, Bitmap.Config.ARGB_8888);

                    Canvas bigcanvas = new Canvas(bm);
                    Paint paint = new Paint();
                    int iHeight = bm.getHeight();

                    bigcanvas.drawBitmap(bm, 0, iHeight, paint);
                    webView.draw(bigcanvas);

                    if (bm != null) {
                        try {
                            String path = Environment.getExternalStorageDirectory().toString();

                            // 파일 이름 명명
                            File file = new File(path, "/relaxleep_capture_image0.png");
//                            File file = new File(path, "/relaxleep_capture_image"+ imageNum +".png");
//                            imageNum++;

                            OutputStream fOut = new FileOutputStream(file);

                            bm.compress(Bitmap.CompressFormat.PNG, 50, fOut);

                            Log.d("complete", "Complete Capture");
                            Toast.makeText(v.getContext(), "캡처 완료", Toast.LENGTH_SHORT).show();

                            fOut.flush();
                            fOut.close();
                            bm.recycle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (IllegalArgumentException e) {
                    Log.e("IllegalArgumentException", "IllegalArgumentException");
                }

            }
        });

        // 녹화 버튼 클릭 메소드 추가--------------------------------------
        final ImageButton videoButton = root.findViewById(R.id.videoBtn);

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("record", "record");

                Toast.makeText(v.getContext(), "녹화 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

}