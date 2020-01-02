package com.example.relaxleep2.ui.environment;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.relaxleep2.CO2Data;
import com.example.relaxleep2.IllumData;
import com.example.relaxleep2.TemHumData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EnvironmentViewModel extends ViewModel {


    private MutableLiveData<String> test;
    //실시간 이산화탄소 정의
    private MutableLiveData<String> live_Co2;
    //실시간 온도 정의
    private MutableLiveData<String> live_Temp;
    // 실시간 습도 정의
    private MutableLiveData<String> live_Humi;
    // 실시간 조도 정의
    private MutableLiveData<String> live_Lux;
    // 이산화탄소 가이드 정의
    private MutableLiveData<String> live_Guide_Co2;
    // 온도 가이드 정의
    private MutableLiveData<String> live_Guide_Temp;
    // 습도 가이드 정의
    private MutableLiveData<String> live_Guide_Humi;
    // 조도 가이드 정의
    private MutableLiveData<String> live_Guide_Lux;

    // DB 연동-----------------------------------
    private static String IP_ADDRESS = "15.164.216.254";
    private static String TAG = "phptest";
    private ArrayList<TemHumData> mArrayList;
    private ArrayList<CO2Data> mArrayList_co2;
    private ArrayList<IllumData> mArrayList_illum;
    private String mJsonString;


    public EnvironmentViewModel() {
        // 테스트 값 출력
        test = new MutableLiveData<>();
        test.setValue("test");

        // 실시간 이산화탄소 값 반영
        live_Co2 = new MutableLiveData<>();
        //live_Co2.setValue("Co2");

        // 실시간 온도 값 반영
        live_Temp = new MutableLiveData<>();
        //live_Temp.setValue("Temp");

        //실시간 습도 값 반영
        live_Humi = new MutableLiveData<>();
        //live_Humi.setValue("Humi");

        //실시간 조도 값 반영
        live_Lux = new MutableLiveData<>();
        //live_Lux.setValue("Lux");

        // 이산화탄소 값에 따른 가이드
        live_Guide_Co2 = new MutableLiveData<>();
        //live_Guide_Co2.setValue("적당합니다.");

        // 온도 값에 따른 가이드
        live_Guide_Temp = new MutableLiveData<>();
        //live_Guide_Temp.setValue("적당합니다.");


        // 습도 값에 따른 가이드
        live_Guide_Humi = new MutableLiveData<>();
        //live_Guide_Humi.setValue("적당합니다.");

        // 조도 값에 따른 가이드
        live_Guide_Lux= new MutableLiveData<>();
        //live_Guide_Lux.setValue("적당합니다.");


        // 수면시간 DB 연동-----------------------------------------
        if(mArrayList != null)
            mArrayList.clear();
        else
            mArrayList = new ArrayList<>();

        if(mArrayList_co2 != null)
            mArrayList_co2.clear();
        else
            mArrayList_co2 = new ArrayList<>();

        if(mArrayList_illum != null)
            mArrayList_illum.clear();
        else
            mArrayList_illum = new ArrayList<>();

        EnvironmentViewModel.GetData task = new EnvironmentViewModel.GetData();
        task.execute( "http://" + IP_ADDRESS + "/getjson_temhum.php", "");

        EnvironmentViewModel.GetData_CO2 task2 = new EnvironmentViewModel.GetData_CO2();
        task2.execute( "http://" + IP_ADDRESS + "/getjson_co2.php", "");

        EnvironmentViewModel.GetData_Illum task3 = new EnvironmentViewModel.GetData_Illum();
        task3.execute( "http://" + IP_ADDRESS + "/getjson_illum.php", "");
    }

    public LiveData<String> getText() {return test;}
    public LiveData<String> getCo2() {return live_Co2;}
    public LiveData<String> getTemp() {return live_Temp;}
    public LiveData<String> getHumi() {return live_Humi;}
//    public LiveData<String> getLux() {return live_Lux;}
    public LiveData<String> getGuideCo2() {return live_Guide_Co2;}
    public LiveData<String> getGuideTemp() {return live_Guide_Temp;}
    public LiveData<String> getGuideHumi() {return live_Guide_Humi;}
    public LiveData<String> getGuideLux() {return live_Guide_Lux;}


    private void showResult(){

        // 온습도
        String TAG_JSON="temhum";
        String TAG_TIME = "time";
        String TAG_TEMP = "temp";
        String TAG_HUMI = "humi";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String time = item.getString(TAG_TIME);
                String temp = item.getString(TAG_TEMP);
                String humi= item.getString(TAG_HUMI);

                TemHumData temhumData = new TemHumData();

                temhumData.setMember_time(time);
                temhumData.setMember_temp(temp);
                temhumData.setMember_humi(humi);

                mArrayList.add(temhumData);
            }

            // 환경 라벨 갱신-------------------------------------------------------
            for(int i=0; i<mArrayList.size(); i++) {
                TemHumData temporary = mArrayList.get(i);
                String[] times = temporary.getMember_time().split("-");
                // 현재 년월일과 비교 생략
                String[] hour = times[3].split(":");

                for (int j = 0; j < 24; j++) {
                    if (Integer.parseInt(hour[0]) == j) {

                        String temp = temporary.getMember_temp();
                        String humi = temporary.getMember_humi();

                        // 가장 최근 시간 판별-추후구현
                        live_Temp.setValue(temp + "℃");
                        live_Humi.setValue(humi + "%");
                        // String to Int
                        int tempInt = Integer.parseInt(temp);
                        if(tempInt>26){
                            live_Guide_Temp.setValue("너무 덥습니다.");
                        }else if (tempInt>=22){
                            live_Guide_Temp.setValue("쾌적합니다.");
                        } else if (tempInt < 22) {
                            live_Guide_Temp.setValue("너무 춥습니다.");
                        }

                        int humiInt = Integer.parseInt(humi);
                        if(humiInt>65){
                            live_Guide_Humi.setValue("너무 습합니다.");
                        }else if (humiInt>=35){
                            live_Guide_Humi.setValue("쾌적합니다.");
                        } else if (humiInt < 35) {
                            live_Guide_Humi.setValue("너무 건조합니다.");
                        }
                    }
                }
            }

            Log.d(TAG, "Complete-------------");

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }

    private void showResult_CO2(){

        // 이산화탄소
        String TAG_JSON="CO2";
        String TAG_TIME = "time";
        String TAG_CO2 = "co2";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String time = item.getString(TAG_TIME);
                String co2 = item.getString(TAG_CO2);

                CO2Data co2Data = new CO2Data();

                co2Data.setMember_time(time);
                co2Data.setMember_co2(co2);

                mArrayList_co2.add(co2Data);
            }

            // 환경 라벨 갱신-------------------------------------------------------
            for(int i=0; i<mArrayList_co2.size(); i++) {
                CO2Data temporary = mArrayList_co2.get(i);
                String[] times = temporary.getMember_time().split("-");
                // 현재 년월일과 비교 생략
                String[] hour = times[3].split(":");

                for (int j = 0; j < 24; j++) {
                    if (Integer.parseInt(hour[0]) == j) {

                        String co2 = temporary.getMember_co2();

                        // 가장 최근 시간 판별
                        live_Co2.setValue(co2 + "ppm");

                        // String to Int
                        int co2Int = Integer.parseInt(co2);

                        if(co2Int>2000){
                            live_Guide_Co2.setValue("환기가 필요합니다.");
                        }else if (co2Int>1500){
                            live_Guide_Co2.setValue("적당합니다.");
                        }
//                        else if (co2Int >700) {
//                            live_Guide_Co2.setValue("쾌적합니다.");
//                        }
                        else {
                            live_Guide_Co2.setValue("쾌적합니다.");
                        }
                    }
                }
            }

            Log.d(TAG, "Complete-------------");

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }

    private void showResult_Illum() {

        // 조도
        String TAG_JSON="Illum";
        String TAG_TIME = "time";
        String TAG_Illum = "illum";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String time = item.getString(TAG_TIME);
                String illum = item.getString(TAG_Illum);

                IllumData illumData = new IllumData();

                illumData.setMember_time(time);
                illumData.setMember_illum(illum);

                mArrayList_illum.add(illumData);
            }

            // 환경 라벨 갱신-------------------------------------------------------
            for(int i=0; i<mArrayList_illum.size(); i++) {
                IllumData temporary = mArrayList_illum.get(i);
                String[] times = temporary.getMember_time().split("-");
                // 현재 년월일과 비교 생략
                String[] hour = times[3].split(":");

                for (int j = 0; j < 24; j++) {
                    if (Integer.parseInt(hour[0]) == j) {

                        String illum = temporary.getMember_illum();

                        // 가장 최근 시간 판별-추후구현
                        live_Lux.setValue(illum);
                        // String to Int
                        int illumInt = Integer.parseInt(illum);
                        if(illumInt>100){
                            live_Guide_Lux.setValue("주위가 너무 밝습니다.");
                        }else if (illumInt>=50){
                            live_Guide_Lux.setValue("주위가 밝습니다.");
                        } else if (illumInt < 50) {
                            live_Guide_Lux.setValue("적당합니다.");

                        }
                    }
                }
            }

            Log.d(TAG, "Complete-------------");

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }


    // DB의 온습도 데이터 받아오기-----------------------------------------------
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


    // DB의 이산화탄소 데이터 받아오기-----------------------------------------------
    private class GetData_CO2 extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "response - " + result);

            if (result == null){
                Log.d(TAG, "response - NULL");
            }
            else {
                mJsonString = result;
                showResult_CO2();
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

    // DB의 조도 데이터 받아오기-----------------------------------------------
    private class GetData_Illum extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "response - " + result);

            if (result == null){
                Log.d(TAG, "response - NULL");
            }
            else {
                mJsonString = result;
                showResult_Illum();
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