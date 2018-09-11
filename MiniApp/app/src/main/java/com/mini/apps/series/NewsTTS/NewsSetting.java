package com.mini.apps.series.NewsTTS;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Html;

import com.mini.apps.series.UtilLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsSetting implements Handler.Callback{

    private static String clientId = "Your-client-id";
    private static String clientSecret = "Your-client-secret";
    private static String apiURL = "https://openapi.naver.com/v1/search/news";

    public static final int COMPLETED_GET_NEWS = 0;
    private static Handler mHandler;

    private static String queryStr = "오늘 날씨"; // 검색을 원하는 문자열. UTF-8로 인코딩
    private static int display = 5; // 검색 결과 출력 건수 지정
    private static int start = 1; // 검색시작위치. 1 (기본값), 1000(최대)
    private static String sort="sim"; // sim : 유사도순 , date : 날짜순

    private static JSONArray jsonArray = null;
    private static JSONObject jsonObjectItems = null;

    private static String[] resultTitle = null;
    private static String[] resultLink = null;
    private static String[] resultDescription = null;
    private static String[] resultDate = null;

    public static void setSettingInfo(Handler handler, String qStr, int count, String sortStr){
        queryStr = qStr;
        display = count;
        sort = sortStr;

        apiURL = apiURL+"?query="+ queryStr +"&display="+display + "&start="+start +"&sort="+sort;
        UtilLog.i("apiURL: "+ apiURL);

        resultTitle = new String[display];
        resultLink = new String[display];
        resultDescription = new String[display];
        resultDate = new String[display];

        mHandler = handler;

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                getNewsFromNaver();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

        }.execute();
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    private static void getNewsFromNaver(){
        try{UtilLog.d("");
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            int respondCode = con.getResponseCode();
            BufferedReader br;
            UtilLog.d("respondCode: "+respondCode);
            if (respondCode == 200) {
                // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }else{
                // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
                UtilLog.d("response: "+response.toString());
            }
            br.close();

            /* jsonObject info
            {
            "lastBuildDate": ,
            "total": ,
            "start": ,
            "display": 5,
            "items": [
                {
                    "title": "",
                    "originallink": "",
                    "link": "",
                    "description": "",
                    "pubDate": ""
                },...}
        */
            try {
                UtilLog.d("response.toString(): "+response.toString());
                JSONObject jsonObject = new JSONObject(response.toString());
                UtilLog.d("");
                /* get info in "items" */
                jsonArray = jsonObject.getJSONArray("items");
                getNewsDetail();

                Message msg = mHandler.obtainMessage();
                msg.what = NewsSetting.COMPLETED_GET_NEWS;
                mHandler.sendMessage(msg);

                apiURL = "https://openapi.naver.com/v1/search/news";

            }catch (JSONException e){
                UtilLog.e("");
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void getNewsDetail(){
        UtilLog.d("display : "+display);

        try {
            for (int i = 0; i < display; i++) {UtilLog.d("");
                UtilLog.d("jsonArray.get(i).toString(): "+jsonArray.get(i).toString());
                jsonObjectItems = new JSONObject(jsonArray.get(i).toString());UtilLog.d("");
                String title = jsonObjectItems.getString("title");UtilLog.d("title: "+title);
                resultTitle[i] = changeHtml(title);UtilLog.d("resultTitle[i]: "+resultTitle[i]);
                resultLink[i] = changeHtml(jsonObjectItems.getString("link"));
                resultDescription[i] = changeHtml(jsonObjectItems.getString("description"));
                resultDate[i] = changeHtml(jsonObjectItems.getString("pubDate"));

                UtilLog.e("resultTitle: " +resultTitle[i]+" i: "+i);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String changeHtml(String html){ // change html format to just string
        CharSequence htmlStr = Html.fromHtml(html);
        UtilLog.e("htmlStr: " +htmlStr+" htmlStr.to: "+htmlStr.toString());
        return htmlStr.toString();
    }

    public static String[] getResultTitle(){
        UtilLog.e("resultTitle: " +resultTitle[0]);
        return resultTitle;
    }

    public static String[] getResultLink() {
        return resultLink;
    }

    public static String[] getResultDescription() {
        return resultDescription;
    }

    public static String[] getResultDate() {
        return resultDate;
    }
}
