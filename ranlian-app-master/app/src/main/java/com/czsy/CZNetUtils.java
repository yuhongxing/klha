package com.czsy;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.czsy.bean.LoginUser;

import mylib.app.EventHandler;
import mylib.utils.BadHttpStatusException;
import mylib.utils.ImageUtils;
import mylib.utils.Utils;
import okhttp3.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static mylib.app.MyLog.LOGD;

public class CZNetUtils {
    final private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(5,10,TimeUnit.SECONDS))
                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS)
//                .sslSocketFactory(SSLSocketFactory.getSSLSocketFactory())
//                .retryOnConnectionFailure(true)
                .build();
    }

    public final static int code_ok = 200;
    static final String svr_port = "";//端口(:10000)


//    static final public String svr_host = "https://www.ranliankeji.com/";//正式域名
    static final public String svr_host = "http://47.95.224.199" + svr_port + "/";//测试域名
//    static final public String svr_host = "http://192.168.0.124:8088/";//本地域名
//    static final public String svr_host = "http://dg.ranliankeji.com/";


    static final public String svr_host_url = svr_host + "psi/services/";

    public static class CZNetErr extends IOException {
        public static int code;
        public static String msg;


        public CZNetErr(int code, JSONObject j) {
            this.code = code;
            this.msg = j.optString("message");

        }
    }

    private static List<Pair<String, String>> initHeader() {
        List<Pair<String, String>> ret = new LinkedList<>();

        ret.add(new Pair<String, String>("Content-Type", "application/json;charset=UTF-8"));
        ret.add(new Pair<String, String>("Accept", "application/json"));
        LoginUser lu = LoginUser.get();
        if (lu != null && lu.valid()) {
            ret.add(new Pair<String, String>("token", lu.token));
        } else {
            // ret.add(new Pair<String, String>("token", ""));
        }
        return ret;
    }

    public static JSONObject getCZHttp(String url) throws IOException, JSONException {
        long now = System.currentTimeMillis();
        JSONObject j = null;
        try {

            if (!url.startsWith("http")) {
                url = svr_host_url + url;
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
            Request.Builder b = new Request.Builder();
            LoginUser lu = LoginUser.get();
            if (lu != null && lu.valid()) {
                b.addHeader("Cookie", lu.token);
            }
            Request request = b
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            String result = response.body().string();
            j = new JSONObject(result);
            LOGD("URL: " + url + ": " + result, true);
            int code = j.optInt("code", 0);
            if (401 == code) {
                if (lu != null) {
                    lu.logout();
                } else {
                    EventHandler.notifyEvent(CZEvents.Event.onLoginUserChanged);
                }
                return j;
            }
            return j;
        } finally {
            long t = System.currentTimeMillis() - now;
            LOGD(url + "[get: " + t + "] >>> " + (j == null ? "" : j.toString()), true);
        }
    }

    public static JSONObject postCZHttp(String url, String paras) throws IOException, JSONException {
        long now = System.currentTimeMillis();
        JSONObject j = null;
        try {
            if (!url.startsWith("http")) {
                url = svr_host_url + url;
            }

            Request.Builder b = new Request.Builder();
            b.addHeader("Connection", "close");
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    paras == null ? "{}" : paras);
            LoginUser lu = LoginUser.get();
            if (lu != null && lu.valid()) {
                b.addHeader("Cookie", lu.token);
            }
            Request request = null;
            if (url.indexOf("xiuGaiMiMa") > 0) {
                request = b
                        .url(url)
                        .put(body)
                        .build();
            } else {
                request = b
                        .url(url)
                        .post(body)
                        .build();
            }

            Response response = client.newCall(request).execute();
//            if (!response.isSuccessful()) {
//                client.dispatcher().cancelAll();
//                client.connectionPool().evictAll();
//                Utils.toastSHORT(response.message()+":"+response.code());
//            }
            Log.d("NetWork", "response-->" + response.message() + ":" + response.code());
            String result = "";

            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
            j = new JSONObject(result);
            int code = j.optInt("code", 0);
            if (401 == code) {
                if (lu != null) {
                    lu.logout();
                } else {
                    EventHandler.notifyEvent(CZEvents.Event.onLoginUserChanged);
                }
                return j;
            }

            String cookie = response.header("Set-Cookie");
            if (lu != null && !TextUtils.isEmpty(cookie)) {
                int idx = cookie.lastIndexOf(';');
                if (idx > 0) {
                    cookie = cookie.substring(0, idx);
                }
                if (!TextUtils.equals(cookie, lu.token) && !j.isNull("result")) {
                    JSONObject j_r = j.getJSONObject("result");
                    j_r.put("token", cookie);
                }
            }
            return j;
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                Log.w("xing", "---------e instanceof SocketTimeoutException");

                client.dispatcher().cancelAll();
                client.connectionPool().evictAll();
            } else if (e instanceof EOFException) {
                postCZHttp(url, paras);
            }
            throw e;
        } finally {
            long t = System.currentTimeMillis() - now;
            if (url.indexOf("faSongDingWei") < 0) {//判断如果是发送位置接口就不打印日志

                LOGD(url + "[post:" + t + "]-->" + (paras == null ? "" : paras) + "<-- >>> " + (j == null ? "" : j.toString()), true);
            }
        }
    }


    public static String upload(Bitmap file_path) throws Exception {

        Log.d("xing", "upload-->Bitmap -- " + file_path);
        long now = System.currentTimeMillis();
        long byte_cnt = 0;
        String s_body = "";
        try {
            LoginUser lu = LoginUser.get();
            if (lu == null || !lu.valid()) {
                if (lu != null) {
                    lu.logout();
                } else {
                    EventHandler.notifyEvent(CZEvents.Event.onLoginUserChanged);
                }
                throw new BadHttpStatusException(401);
            }

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            file_path.compress(Bitmap.CompressFormat.PNG, 100, bs);
            MultipartBody.Builder builder = new MultipartBody.Builder();//构建者模式
            builder.setType(MultipartBody.FORM);//传输类型
            byte[] data = bs.toByteArray();
            byte_cnt = data.length;
            builder.addFormDataPart("upload", "file.png",
                    RequestBody.create(MediaType.parse("application/octet-stream"), data));

            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .header("Cookie", lu.token)
                    .header("accept", "application/json")
                    .url(svr_host_url + "file/upload")
//                    .url("https://www.ranliankeji.com/psi/services/file/upload")
//                    .url( "http://39.97.186.148:8081/psi/services/file/upload")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();

            Log.d("xing", "upload-->Bitmap -- " + "response.body();");
            s_body = body.string();
            Log.d("xing", "upload-->Bitmap -- " + "s_body==>" + s_body);
            JSONObject j = new JSONObject(s_body);
            Log.d("xing", "s_body2==>" + j.getString("url"));
            return j.getString("url");
        } finally {
            long t = System.currentTimeMillis() - now;
//            LOGD("upload " + byte_cnt + " bytes cost: " + t);
            LOGD(svr_host_url + "file/upload" + "[post:" + t + "（Bitmap）]-->" + s_body, true);

        }
    }

    public static String upload(String file_path) throws Exception {

        long now = System.currentTimeMillis();
        long byte_cnt = 0;
        String s_body = "";
        try {
            LoginUser lu = LoginUser.get();
            if (lu == null || !lu.valid()) {
                if (lu != null) {
                    lu.logout();
                } else {
                    EventHandler.notifyEvent(CZEvents.Event.onLoginUserChanged);
                }
                throw new BadHttpStatusException(401);
            }
            File f = new File(file_path);
            if (!f.exists()) {
                throw new IOException("file not exist : " + file_path);
            }
            boolean is_pic = file_path.endsWith(".jpg")
                    || file_path.endsWith(".jpeg")
                    || file_path.endsWith(".png");
            if (is_pic) {
                try {
                    Bitmap b = ImageUtils.getFileBmp(file_path);
                    Bitmap b2 = ImageUtils.resizeBitmap(b, 400);
                    if (b2 != null) {
                        Log.d("xing", "b2 != null");
                        String ss = upload(b2);
                        Log.d("xing", "return------>" + ss);
                        return ss;
                    }
                } catch (Throwable t) {

                    Utils.toastSHORT("上传失败");
                }
            }
            byte_cnt = f.length();

            MultipartBody.Builder builder = new MultipartBody.Builder();//构建者模式
            builder.setType(MultipartBody.FORM);//传输类型
            builder.addFormDataPart("upload", f.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"), f));

            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .header("Cookie", lu.token)
                    .header("accept", "application/json")
                    .url(svr_host_url + "file/upload")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            s_body = body.string();
            Log.d("xing", "s_body==>" + s_body);
            JSONObject j = new JSONObject(s_body);
            return j.getString("url");
        } finally {
            long t = System.currentTimeMillis() - now;
//            LOGD("upload file " + byte_cnt + " bytes cost: " + t);
            LOGD(svr_host_url + "file/upload" + "[post:" + t + "（file_path）]-->" + s_body, true);
        }
    }
}
