package com.example.relaxleep2.ui.chart;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

public class ChartViewModel extends ViewModel {

    // 날짜
    private MutableLiveData<String> mText;

    // 라인차트
    private MutableLiveData<LineChartData> lineChartData;
    private List axisValues;
    private List yAxisValues;
    private Line line;

    // 수면시간
    private MutableLiveData<String> realSleepTime;
    private MutableLiveData<String> averageSleepTime;
    private MutableLiveData<String> infoSleepTime;

    // 실제 시간
    private int realHour;
    private int realMinute= 0;

    private int averageHour = 0;
    private int averageMinute= 0;

    private boolean awake = false;
    private int awakeCount = 0;


    // DB 연동-----------------------------------
    private static String IP_ADDRESS = "15.164.216.254";
    private static String TAG = "phptest";
    private ArrayList<SleepData> mArrayList;
    private String mJsonString;


    public ChartViewModel() {

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
                day_of_week = "(일)";
                break;
        }

        mText = new MutableLiveData<>();
        mText.setValue(format_time1 + day_of_week);


        // 라인차트 초기화---------------------------------
        lineChartData = new MutableLiveData<>();
        axisValues = new ArrayList();
        yAxisValues = new ArrayList();


        // 수면시간 DB 연동-----------------------------------------
        if(mArrayList != null)
            mArrayList.clear();
        else
            mArrayList = new ArrayList<>();

        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getjson.php", "");


        // 실제수면시간----------------------------------
        realSleepTime = new MutableLiveData<>();
        realSleepTime.setValue(realHour + "시간 " + realMinute + "분");

        // 평균수면시간----------------------------------
        averageSleepTime = new MutableLiveData<>();
        averageHour = 14;
        averageMinute = 20;
        averageSleepTime.setValue(averageHour + "시간 " + averageMinute + "분");

        // 수면 정보----------------------------------
        infoSleepTime = new MutableLiveData<>();
        infoSleepTime.setValue("깬 횟수 : " + awakeCount);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<LineChartData> getLinechart() {
        return lineChartData;
    }

    public LiveData<String> getRealSleepTime() {
        return realSleepTime;
    }

    public LiveData<String> getAverageSleepTime() { return averageSleepTime; }

    public LiveData<String> getInfoSleepTime() { return infoSleepTime; }


    // 라인차트 갱신------------------------------
    private void updatingLinechart() {

        ArrayList<String> axisData = new ArrayList<>();
        ArrayList<Integer> yAxisData = new ArrayList<>();

        for(int i=0; i<mArrayList.size(); i++) {
            SleepData temp = mArrayList.get(i);
            String [] times = temp.getMember_time().split("-");
            // 현재 년월일과 비교 생략
            String [] hour = times[3].split(":");

            for(int j=0; j<24; j++) {
                if(Integer.parseInt(hour[0]) == j) {

                    axisData.add(j + "시");

                    // 수면상태 판단 후 차트에 반영
                    int state = Integer.parseInt(temp.getMember_state());

                    if(state == 0)  {
                        yAxisData.add(0);
                        realHour++;
                        realSleepTime.setValue(realHour + "시간 " + realMinute + "분");
                        awake = false;
                    }
                    else {
                        yAxisData.add(1);
                        if(awake){
                            infoSleepTime.setValue("깬 횟수 : " + awakeCount);
                        }
                        else {
                            awake = true;
                            awakeCount++;
                            infoSleepTime.setValue("깬 횟수 : " + awakeCount);
                        }
                    }

                }
            }
        }

        // 라인차트 데이터 추가---------------------------------
        for(int i = 0; i < axisData.size(); i++){
            axisValues.add(i, new AxisValue(i).setLabel(axisData.get(i)));
        }

        for (int i = 0; i < yAxisData.size(); i++){
            yAxisValues.add(new PointValue(i, yAxisData.get(i)));
        }

        // 라인차트 스타일 지정------------------------------------
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        List lines = new ArrayList();
        lines.add(line);

        // 임시 라인차트 데이터
        LineChartData lineChartData2 = new LineChartData();

        // x축 추가, y축 생략
        Axis axis = new Axis();
        axis.setValues(axisValues);
        lineChartData2.setAxisXBottom(axis);

        lineChartData2.setLines(lines);

        // 라인차트 데이터 전달
        lineChartData.setValue(lineChartData2);

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

            // 라인차트 추가-----------------
            updatingLinechart();
            Log.d(TAG, "Complete-------------");

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }

    // DB의 데이터 받아오기-----------------------------------------------
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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