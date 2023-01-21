package com.example.ibuy2;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ServerCalls {

    public static String[] stores;
    public static String store_id;
    public static ArrayList<String[]> recommendations = new ArrayList<String[]>();
    public static String username;
    public static String password;
    public static String user_id;

    public static String[] getStores() throws JSONException, IOException {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/stores";
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        String responseBody = null;

        try{
            response = client.newCall(request).execute();
            responseBody = response.body().string();
            stores = parseResponse(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stores;
    }

    private static String[] parseResponse(String myResponse) {

        ArrayList<String> res = new ArrayList<>();

        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(myResponse);
        int i = 1;

        while (m.find()) {
            if(i % 8 == 3)
                res.add(m.group(1));
            i++;
        }

        return res.toArray(new String[0]);

    }

    public static boolean addItem(String store_id, String barcode_str, String cnt) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/user/cart/";
        String product_id = getProductId(barcode_str);

        if(product_id == null)
            return false;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("product", product_id)
                .addFormDataPart("quantity", cnt)
                .addFormDataPart("user", user_id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    public static String getProductId(String barcode) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/product?barcode=" + barcode;

        Request request = new Request.Builder().url(url).build();
        String responseBody = null;
        String res = null;

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            res = responseBody.split(",")[0].split(":")[1].trim();
        } catch (IOException e) {

        } catch(RuntimeException e) {

        }

        return res;

    }

//    private static ArrayList<String> getItemNameAndBarcode(String product_id) {
//
//        OkHttpClient client = new OkHttpClient();
//        String url = "https://watad.herokuapp.com/manager/product/details?id=" + product_id;
//
//        Request request = new Request.Builder().url(url).build();
//        String responseBody = null;
//        ArrayList<String> res = new ArrayList<String>();
//
//        try (Response response = client.newCall(request).execute()) {
//            responseBody = response.body().string();
//            res.add(responseBody.split(",")[1].split(":")[1].trim()); // barcode
//            res.add(responseBody.split(",")[2].split(":")[1].trim()); // barcode
//        } catch (IOException e) {
//
//        } catch(RuntimeException e) {
//
//        }
//
//        return res;
//
//    }


    public static String getStoreId(String store_name) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/store?name="+store_name+"&location=loc";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            store_id = parseStoreId(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return store_id;
    }

    private static String parseStoreId(String responseBody) {

        String[] str_array = responseBody.split(",");
        String store_id = str_array[0].split(":")[1].trim();

        return store_id;

    }

    public static ArrayList<String[]> getRecommendations(String store_id) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/user/cart/suggest/"+ServerCalls.user_id;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            String store_name = responseBody.split(" ")[4].split(",")[0];
            String id = getStoreId(store_name);
            String price = responseBody.split(" ")[9];

            if(store_id.equals(id))
                return recommendations;

            recommendations.add(new String[] {store_name, price});
//            recommendations = parseRecommendations(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recommendations;

    }

    private static ArrayList<String[]> parseRecommendations(String responseBody) {

        ArrayList<String[]> res = new ArrayList<String[]>();
        ArrayList<String> str_arr = new ArrayList<String>();

        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(responseBody);
        String supermarket;
        String saved;

        while (m.find()) {
                str_arr.add(m.group(1));
        }

        if(str_arr.size() == 1){
            return null;
        }

        int i = 0;

        while(i < str_arr.size()) {
            res.add(new String[]{str_arr.get(i), str_arr.get(i+1)});
            i += 2;
        }

        return res;

    }

    public static boolean reportPrice(String store_id, String barcode, String product_id, String price) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/user/report";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("store", store_id)
                .addFormDataPart("product", product_id)
                .addFormDataPart("real_price", price)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if(responseBody.equals("Created"))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void createUser() {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/user/";

        username = Long.toHexString(Double.doubleToLongBits(Math.random()));
        password = Long.toHexString(Double.doubleToLongBits(Math.random()));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void getUserId() {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/user/g/" + username;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            user_id = responseBody.split(",")[0].split(":")[1].trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
