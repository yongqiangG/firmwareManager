package com.johnny.utils;


import com.alibaba.fastjson.JSONObject;
import com.johnny.entity.Machine;
import com.johnny.entity.MachineGroup;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    //小度Token
    public static final String Authorization = "Bearer"+" "+"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjYwODYyOTU1LCJhcHBJZCI6IkFLeXdjdTVyZ2RYNTU2ZmVDZGxTMmlaeXV1RlJxNTAzIiwidXVpZCI6MTY1MzA2OTUwMDQxMjI0MjM2NX0.kegBb79qT322jx5X4mTdetJtPu4sUhdHfFbslm1udQo";

    public static List<Machine> getRequest1(){
        String result =null;
        String url ="http://liuxing.txpark.kekcn.cn:8888/tx/firmware/getMachineInfo.action";//请求接口地址
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("method", "welcome");
        jsonParam.put("outputSpeechText", "");
        jsonParam.put("cuid", "MA718CNBFCL010122");
        jsonParam.put("cardContent", "");
        Map<String, Object> params = new HashMap<String, Object>();//请求参数
        params.put("cuid","389787557879d8a178f33fa01f3e26ec");
        params.put("timestamp",Long.toString(System.currentTimeMillis()/1000L));
        params.put("v","2.0");
        params.put("source","baidu");
        params.put("paramJson",jsonParam);
        try {
            result =net(url, params, "POST");
            JSONObject object = JSONObject.parseObject(result);
            System.out.println(object);
            Machine machine = JSONObject.parseObject(result,Machine.class);
            MachineGroup machines = JSONObject.parseObject(result, MachineGroup.class);
            System.out.println(machine);
            return machines.getMachineList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("User-agent", userAgent);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization",Authorization);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    public static String urlencode(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        getRequest1();
    }

}
