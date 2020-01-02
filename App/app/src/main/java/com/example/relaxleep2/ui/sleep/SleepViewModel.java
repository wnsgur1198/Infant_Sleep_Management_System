package com.example.relaxleep2.ui.sleep;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.relaxleep2.SleepData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class SleepViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> expectedSleepTime_label;
    private MutableLiveData<String> expectedSleepTime;
    private MutableLiveData<String> totalSleepTime;

    private MutableLiveData<PieChartData> pieChartData;
    private List<SliceValue> pieData;
    private int sleep_data_color;

    private int day = 100;
    private long expectedHour = 0;
    private long expectedMinute= 0;

    private int totalHour = 0;
    private int totalMinute= 0;

    // DB 연동-----------------------------------
    private static String IP_ADDRESS = "15.164.216.254";
    private static String TAG = "phptest";
    private ArrayList<SleepData> mArrayList;
    private String mJsonString;


    public SleepViewModel() {

        // 날짜 출력--------------------------------------
        SimpleDateFormat format = new SimpleDateFormat ( "MM월 dd일 ");

        Calendar time = Calendar.getInstance();

        String format_time1 = format.format(time.getTime());

        String day_of_week = "";

        switch (time.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                day_of_week = "(일)";
                break;
            case 2:
                day_of_week = "(월)";
                break;
            case 3:
                day_of_week = "(화)";
                break;
            case 4:
                day_of_week = "(수)";
                break;
            case 5:
                day_of_week = "(목)";
                break;
            case 6:
                day_of_week = "(금)";
                break;
            case 7:
                day_of_week = "(토)";
                break;
        }

        mText = new MutableLiveData<>();
        mText.setValue(format_time1 + day_of_week);


        // 파이차트 초기화---------------------------------
        pieChartData = new MutableLiveData<>();
        pieData = new ArrayList<>();


        // 수면시간 DB 연동-----------------------------------------
        if(mArrayList != null)
            mArrayList.clear();
        else
            mArrayList = new ArrayList<>();

        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getjson.php", "");

        // 예상수면시간----------------------------------
        expectedSleepTime_label = new MutableLiveData<>();
        expectedSleepTime_label.setValue("예상 수면/각성 시간");

        expectedSleepTime = new MutableLiveData<>();
        expectedSleepTime.setValue(expectedHour + "시간 " + expectedMinute + "분 남음...");

        // 총 수면시간----------------------------------
        totalSleepTime = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<PieChartData> getPiechart() {
        return pieChartData;
    }

    public LiveData<String> getExpectedSleepTime_label() {
        return expectedSleepTime_label;
    }

    public LiveData<String> getExpectedSleepTime() {
        return expectedSleepTime;
    }

    public LiveData<String> getTotalSleepTime() {
        return totalSleepTime;
    }

    // 남은 시간 계산-----------------
    private void leftTime_Calc() {

        try {
            String targetTime_str = "";

            // 1) 현재 시간이 자는 시간인지 깨는 시간인지 판별
            Date currentTime = new Date();
            SimpleDateFormat time_format = new SimpleDateFormat("HH:mm:ss");
            String currentTime_str = time_format.format(currentTime);
            String [] currentHour = time_format.format(currentTime).split(":");

            for(int i=0; i<mArrayList.size(); i++) {
                SleepData temp = mArrayList.get(i);

                String [] times = temp.getMember_time().split("-");
                String [] hour = times[3].split(":");

                if(hour[0].equals(currentHour[0])) {

                    // 현재 시간이 자는 시간일 때
                    if(Integer.parseInt(temp.getMember_state()) == 0) {
                        // 2) 다음 깨는/자는 시간이 언제인지 판별
                        for(int k=0; k<mArrayList.size(); k++) {

                            SleepData temp2 = mArrayList.get(k);

                            if(Integer.parseInt(temp2.getMember_state()) == 1) {
                                String [] times2 = temp2.getMember_time().split("-");
                                targetTime_str = times2[3] + ":00";
                                String [] times2_hour = times2[3].split(":");
                                if(Integer.parseInt(times2_hour[0])>Integer.parseInt(currentHour[0]))
                                    break;
                            }
                        }
                    }
                    else {  // 현재 시간이 깬 시간일 때
                        // 2) 다음 깨는/자는 시간이 언제인지 판별
                        for(int k=0; k<mArrayList.size(); k++) {

                            SleepData temp2 = mArrayList.get(k);

                            if(Integer.parseInt(temp2.getMember_state()) == 0) {
                                String [] times2 = temp2.getMember_time().split("-");
                                targetTime_str = times2[3] + ":00";
                                String [] times2_hour = times2[3].split(":");
                                if(Integer.parseInt(times2_hour[0])>Integer.parseInt(currentHour[0]))
                                    break;
                            }
                        }
                    }

                }
            }

            // 3) 다음 시간과 현재 시간의 차를 구함
            String [] currentTimes = currentTime_str.split(":");
            int currentTime_hour = Integer.parseInt(currentTimes[0]);
            int currentTime_min = Integer.parseInt(currentTimes[1]);

            String [] targetTimes = targetTime_str.split(":");
            int targetTime_hour = Integer.parseInt(targetTimes[0]);
            int targetTime_min = Integer.parseInt(targetTimes[1]);

            if(targetTime_min < currentTime_min) {
                expectedMinute = targetTime_min - currentTime_min + 60;

                if(targetTime_hour >= currentTime_hour) {
                    expectedHour = targetTime_hour - currentTime_hour - 1;
                }
                else {
                    expectedHour = 24 + targetTime_hour - currentTime_hour - 1;
                }

            }
            else {
                expectedMinute = targetTime_min - currentTime_min;

                if(targetTime_hour >= currentTime_hour) {
                    expectedHour = targetTime_hour - currentTime_hour;
                }
                else {
                    expectedHour = 24 + targetTime_hour - currentTime_hour;
                }

            }

            expectedSleepTime.setValue(expectedHour + "시간 " + expectedMinute + "분 남았습니다.");
        }   catch (Exception e) { Log.e("dateParse", e.toString()); }
    }

    // 파이차트 갱신------------------------------
    private void updatingPiechart() {

        Date today = new Date();
        SimpleDateFormat time_format = new SimpleDateFormat("HH:mm:ss");
        String [] currentHour = time_format.format(today).split(":");

        for(int i=0; i<mArrayList.size(); i++) {
            SleepData temp = mArrayList.get(i);
            String [] times = temp.getMember_time().split("-");
            // 현재 년월일과 비교 생략
            String [] hour = times[3].split(":");

            for(int j=0; j<24; j++) {
                if(Integer.parseInt(hour[0]) == j) {

                    // 수면상태 판단 후 차트에 반영
                    int state = Integer.parseInt(temp.getMember_state());

                    if(state == 0)  {
                        if(j == Integer.parseInt(currentHour[0])) {
                            sleep_data_color = Color.YELLOW;

                            if(state==0)    expectedSleepTime_label.setValue("예상 깰 시간");
                            else    expectedSleepTime_label.setValue("예상 잠들 시간");
                        }
                        else  sleep_data_color = Color.GREEN;

                        totalHour += 1;  // 총수면시간 갱신
                    }

                    else {
                        if(j == Integer.parseInt(currentHour[0])) {
                            sleep_data_color = Color.YELLOW;

                            if(state==0)    expectedSleepTime_label.setValue("예상 깰 시간");
                            else    expectedSleepTime_label.setValue("예상 잠들 시간");
                        }
                        else   sleep_data_color = Color.BLUE;
                    }

                    pieData.add(new SliceValue(1, sleep_data_color).setLabel(""));
                }
            }
        }

        // 파이차트데이터에 추가
//        pieData.add(new SliceValue(1, Color.BLUE).setLabel("깸"));
//        pieData.add(new SliceValue(1, Color.GREEN).setLabel("잠"));

        PieChartData pieChartData2 = new PieChartData(pieData);
        pieChartData2.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData2.setHasCenterCircle(true).setCenterText1("Day " + day).
                setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));

        pieChartData.setValue(pieChartData2);

        // 예상수면시간 갱신
        leftTime_Calc();

        // 총 수면시간 갱신
        totalSleepTime.setValue(totalHour + "시간 " + totalMinute + "분 잠잤습니다.");
    }

    private void showResult(){

        String TAG_JSON="sleep";
        String TAG_TIME = "time";
        String TAG_STATE = "state";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String time = item.getString(TAG_TIME);
                String state = item.getString(TAG_STATE);

                SleepData sleepData = new SleepData();

                sleepData.setMember_time(time);
                sleepData.setMember_state(state);

                mArrayList.add(sleepData);
            }

            // 파이차트 추가-----------------
            updatingPiechart();
            Log.d(TAG, "Complete-------------");

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }


    // DB의 데이터 받아오기-----------------------------------------------
    private class GetData extends AsyncTask<String, Void, String> {
        //        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(SleepViewModel.this,
//                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null){
                Log.d(TAG, "response - NULL");
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }

    }

}
