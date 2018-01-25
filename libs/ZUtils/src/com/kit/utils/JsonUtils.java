package com.kit.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonUtils {

    public static URL url;

    // 解析处理过的json字符串，需调用getJosnStr()，以取得处理过的json字符串
    public static JSONObject str2JSONObj(String str) {

        String jsonStr = "{jsonStr:" + str + "}";

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonStr);

            jsonObject = jsonObject.getJSONObject("jsonStr");

        } catch (JSONException e) {

            Log.e("error", "JsonUtils.resolveJson() have something wrong...");

        }
        return jsonObject;
    }

    // 解析处理过的json字符串，需调用getJosnStr()，以取得处理过的json字符串
    public static JSONArray str2JSONArray(String str) {
        if (StringUtils.isNullOrEmpty(str))
            return null;

        String jsonStr = "{jsonStr:" + str + "}";

        System.out.println(jsonStr);
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        try {
            jsonObject = new JSONObject(jsonStr);
            jsonArray = jsonObject.getJSONArray("jsonStr");
            // jsonObject = jsonObject.getJSONObject("jsonStr");

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e("error", "JsonUtils.str2JSONArray() have something wrong...");

        }
        return jsonArray;
    }

    // 解析JsonArray,返回ArrayList<JSONObject>
    public static ArrayList<JSONObject> resolveJsonArray(JSONArray jsonArray) {
        ArrayList<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tempJsonObject = null;
            try {
                tempJsonObject = jsonArray.getJSONObject(i);
                jsonObjectList.add(tempJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonObjectList;
    }

    public static String formatJsonStr(String inputString) {

        if (TextUtils.isEmpty(inputString))
            return "";
        // String dataStr = inputString;
        // System.out.println(dataStr);
        String paramsStr = inputString;

        paramsStr = paramsStr.replaceAll("\\\\", "");
        // System.out.println("UMailMessageData1: " + paramsStr);
        paramsStr = paramsStr.replaceAll("\"\\{", "{");
        // System.out.println("UMailMessageData2: " + paramsStr);
        paramsStr = paramsStr.replaceAll("\\}\"", "}");

        paramsStr = paramsStr.replaceAll("\\\"", "\"");

        String jsonStr = paramsStr;

        return jsonStr;

    }

    public static void replaceStringValue(JSONObject jobj, String keyName, String replaceValue) throws JSONException {
        String value;
        Iterator it = jobj.keys();

        while (it.hasNext()) {

            String key = it.next().toString();

            // 将所有的空串去掉
            if (jobj.getString(key) == null) {

                continue;
            }

            if (keyName.equals(key)) {      //试剂类型
                ZogUtils.printLog(JsonUtils.class, "img_url got!!!!!!!!!!!!!!");
                jobj.put(keyName, replaceValue);
            }
        }
//        try {
//            params.put(SDK.IMG_URL_TAG, URLEncoder.encode(params.getString(SDK.IMG_URL_TAG), SDK.DEFAULT_CODE));
//        } catch (Exception e) {
//        }


    }

    /**
     * 把对象按照Json格式输出
     *
     * @param obj
     */
    public static void printAsJson(Object obj) {
        Gson gson = new Gson();
        ZogUtils.printLog(JsonUtils.class, gson.toJson(obj));
    }


    public static boolean isJSON(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.getJSONObject(jsonString);
        } catch (Exception e) {

            return false;
        }
        return true;
    }
    /**
     * HttpClient方式实现，支持所有Https免验证方式链接
     *
     * @throws ClientProtocolException
     * @throws IOException
     */
    public void initSSLAllWithHttpClient() throws ClientProtocolException, IOException {
        int timeOut = 30 * 1000;
        HttpParams param = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(param, timeOut);
        HttpConnectionParams.setSoTimeout(param, timeOut);
        HttpConnectionParams.setTcpNoDelay(param, true);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
        DefaultHttpClient client = new DefaultHttpClient(manager, param);

        HttpGet request = new HttpGet("https://certs.cac.washington.edu/CAtest/");
        // HttpGet request = new HttpGet("https://www.alipay.com/");
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.e("HTTPS TEST", result.toString());
    }
    public static JSONObject initSSLWithHttpClinet(String path)
            throws ClientProtocolException, IOException {
        HTTPSTrustManager.allowAllSSL();
        JSONObject jsonObject = null;
        int timeOut = 30 * 1000;
        HttpParams param = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(param, timeOut);
        HttpConnectionParams.setSoTimeout(param, timeOut);
        HttpConnectionParams.setTcpNoDelay(param, true);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory .getSocketFactory(), 80));
        registry.register(new Scheme("https", TrustAllSSLSocketFactory .getDefault(), 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager( param, registry);
        DefaultHttpClient client = new DefaultHttpClient(manager, param);

        HttpGet request = new HttpGet(path);
        // HttpGet request = new HttpGet("https://www.alipay.com/");
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        BufferedReader reader = new BufferedReader(new InputStreamReader( entity.getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
            try {
                jsonObject = new JSONObject(line);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("HTTPS TEST", result.toString());
        return jsonObject;
    }
}


