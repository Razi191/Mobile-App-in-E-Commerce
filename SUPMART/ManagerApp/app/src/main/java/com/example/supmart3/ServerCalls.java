package com.example.supmart3;

import android.util.Log;

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

    public static String store_id;
    public static String[] stores;

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

    public static boolean authenticate(String store_name, String password) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/store?name="+ store_name + "&location=loc";
        Request request = new Request.Builder().url(url).build();

        boolean res = false;

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            String pass = getPassword(responseBody);

            if(password.equals(pass))
                res = true;

            else
                res = false;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return res;
    }

    private static String getPassword(String responseBody) {
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(responseBody);
        int i = 1;
        while(i <= 7) {
            m.find();
            i++;
        }
        return m.group(1);
    }

    public static String[] getStores() {

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

    public static String getWarnings(String store_id) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/store/" + store_id + "/punishments";
        Request request = new Request.Builder().url(url).build();
        String responseBody = null;
        String res = null;

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            res = responseBody.replaceAll("[^0-9]", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;

    }

    public static ArrayList<String[]> getStoreItems(String store_id) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/inventory/store/" + store_id;
        String responseBody = null;

        Request request = new Request.Builder().url(url).build();
        ArrayList<String[]> list_items = new ArrayList<String[]>();
        ArrayList<String> tmp_array = new ArrayList<String>();

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            String[] res = responseBody.split("\\{");
            if(res.length == 1)
                return list_items;
            else
                for(int i = 2; i < res.length; i++) {
                    String product_id = res[i].split(",")[1].split(":")[1].trim();
                    ArrayList<String> barcode_name = getItemNameAndBarcode(product_id);
                    Pattern p = Pattern.compile("\"([^\"]*)\"");
                    Matcher m = p.matcher(barcode_name.get(0));
                    m.find();
                    String barcode = m.group(1);

                    m = p.matcher(barcode_name.get(1));
                    m.find();
                    String item_name = m.group(1);

                    String price = res[i].split(",")[2].split(":")[1].trim();
                    String[] row = new String[] {item_name, barcode, price};
                    list_items.add(row);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list_items;

    }

    private static ArrayList<String> getItemNameAndBarcode(String product_id) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/product/details?id=" + product_id;

        Request request = new Request.Builder().url(url).build();
        String responseBody = null;
        ArrayList<String> res = new ArrayList<String>();

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            res.add(responseBody.split(",")[1].split(":")[1].trim()); // barcode
            res.add(responseBody.split(",")[2].split(":")[1].trim()); // barcode
        } catch (IOException e) {

        } catch(RuntimeException e) {

        }

        return res;

    }

    public static boolean addItem(String store_name, String item_name, String barcode, String price) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/product/";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("barcode",barcode)
                .addFormDataPart("name", item_name)
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

        String store_id = getStoreId(store_name);
        String product_id = getProductId(barcode);

        boolean res = addItemToStore(store_id, product_id, price, 1000000);

        return res;

    }

    private static boolean addItemToStore(String store_id, String product_id, String price, int quantity) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/inventory/";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("store",store_id)
                .addFormDataPart("product", product_id)
                .addFormDataPart("price", price)
                .addFormDataPart("quantity", Integer.toString(quantity))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        String responseBody = null;
        boolean res = false;

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            if(responseBody.equals(("Created")))
                res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    private static String getProductId(String barcode) {

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

    public static boolean updateItem(String store_name, String barcode, String price) {

        String store_id =  getStoreId(store_name);
        String product_id = getProductId(barcode);

        if(product_id == null)
            return false;

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/inventory/store/" + store_id + "/product/" + product_id;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("price", price)
                .addFormDataPart("quantity", "1000000")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        String responseBody = null;
        boolean res = false;

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            if(responseBody.equals(("Updated successfully")))
                res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;

    }

    public static boolean deleteItem(String store_name, String barcode) {

        String store_id =  getStoreId(store_name);
        String product_id = getProductId(barcode);
        boolean res = false;

        if(product_id == null)
            return false;

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/inventory/store/" + store_id + "/product/" + product_id + "/d";

        Request request = new Request.Builder().url(url).build();
        String responseBody = null;

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            if(responseBody.equals("Deleted Successfully!"))
                res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;

    }

    public static ArrayList<String[]> getStoreReports(String store_id) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/report?store_id=" + store_id;
        String responseBody = null;

        Request request = new Request.Builder().url(url).build();
        ArrayList<String[]> list_reports = new ArrayList<String[]>();
        ArrayList<String> tmp_array = new ArrayList<String>();

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
            String[] str_arr = responseBody.split("\\{");
            if(str_arr.length <= 1)
                return list_reports;
            else {
                for(int i = 1; i < str_arr.length; i++) {
                    String product_id = str_arr[i].split(",")[2].split(":")[1].trim();
                    String reported_price = str_arr[i].split(",")[3].split(":")[1].trim();
                    ArrayList<String> barcode_name = getItemNameAndBarcode(product_id);
                    String barcode = barcode_name.get(0);
                    Pattern p = Pattern.compile("\"([^\"]*)\"");
                    Matcher m = p.matcher(barcode);
                    m.find();
                    barcode = m.group(1);
                    String product_name = barcode_name.get(1);
                    p = Pattern.compile("\"([^\"]*)\"");
                    m = p.matcher(product_name);
                    m.find();
                    product_name = m.group(1);
                    String price = store_get_price(ServerCalls.store_id, barcode);
                    list_reports.add(new String[] {product_name, barcode, price, reported_price});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list_reports;

    }

    private static String store_get_price(String store_id, String barcode) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/store/" + store_id + "/product?barcode=" + barcode;
        String responseBody = null;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "50";

    }

    public static boolean addStore(String store_name, String password, String loc) {

        OkHttpClient client = new OkHttpClient();
        String url = "https://watad.herokuapp.com/manager/store/";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", store_name)
                .addFormDataPart("location", loc)
                .addFormDataPart("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        String responseBody = null;

        try (Response response = client.newCall(request).execute()) {
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(responseBody.equals("Created")){
            return true;
        }

        return false;

    }
}
