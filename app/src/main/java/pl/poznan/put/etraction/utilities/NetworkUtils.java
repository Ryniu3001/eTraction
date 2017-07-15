package pl.poznan.put.etraction.utilities;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;

import pl.poznan.put.etraction.model.HttpUrlResponse;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Marcin on 12.04.2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    public final static String STATEMENTS_BASE_URL = "https://etraction.herokuapp.com/api/v1/statements";
    public final static String MOVIES_BASE_URL = "https://etraction.herokuapp.com/api/v1/movies";
    public final static String CAMERAS_BASE_URL = "https://etraction.herokuapp.com/api/v1/cameras";
    public final static String RESTAURANT_MENU_BASE_URL = "https://etraction.herokuapp.com/api/v1/restaurant_menu_items";
    public final static String CHAT_MESSAGES_BASE_URL  = "https://etraction.herokuapp.com/api/v1/messages";
    public final static String USER_BASE_URL  = "https://etraction.herokuapp.com/api/v1/users";
    public final static String TRACK_BASE_URL = "https://etraction.herokuapp.com/api/v1/tracks";
    public final static String GOOGLE_DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json";

    public final static String CHAT_PAGE_PARAM = "page";
    public final static String CHAT_MESSAGES_PER_PAGE_PARAM = "per_page";

    public final static String CHAT_MESSAGES_PER_PAGE_VALUE = "7";

    public final static String REQUEST_HEADER_DEVICE_ID_PARAM = "device_id";



    /**
     * Build the URL used to query.
     * @return URL to use to query
     */
    public static URL buildUrl(String baseUrl){         //Poki mamy URLe bez zadnych paramerow, mozna je bydowac jedną metoda
        Uri builtUri = Uri.parse(baseUrl).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Can't build URL! return null", e);
        }

        return url;
    }
    //TODO: Refaktor, mozna scalic w jedna metode?
    public static URL buildUrlWithParams(String baseUrl, Map<String, String> params){
        Uri.Builder uriBuilder = Uri.parse(baseUrl).buildUpon();
        if (params != null && !params.isEmpty()){
            for (Map.Entry<String, String> param : params.entrySet()){
                uriBuilder.appendQueryParameter(param.getKey(), param.getValue());
            }
        }
        Uri builtUri = uriBuilder.build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Can't build URL! return null", e);
        }

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            return inputStreamToString(in);
        } finally {
            urlConnection.disconnect();
        }
    }

    //TODO: Refaktor
    public static HttpUrlResponse getResponseFromHttpUrl(URL url, String deviceId) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (deviceId != null)
            urlConnection.setRequestProperty(REQUEST_HEADER_DEVICE_ID_PARAM, deviceId);
        try {
            int responseCode = urlConnection.getResponseCode();
            InputStream in;
            if (responseCode == HTTP_OK)
                in = urlConnection.getInputStream();
            else
                in = urlConnection.getErrorStream();
            return new HttpUrlResponse(inputStreamToString(in), responseCode);
        } finally {
            urlConnection.disconnect();
        }
    }

    @Nullable
    private static String inputStreamToString(InputStream in) {
        if (in == null) return null;
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\\A");

        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }

    public static HttpUrlResponse saveDataToServer(URL url, String deviceId, String method, JsonObject jsonObject) {
        HttpURLConnection urlConnection = null;
        int responseCode = 0;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty(REQUEST_HEADER_DEVICE_ID_PARAM, deviceId);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setFixedLengthStreamingMode(jsonObject.toString().getBytes().length);

            OutputStreamWriter streamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
            streamWriter.write(jsonObject.toString());
            streamWriter.close();

            responseCode = urlConnection.getResponseCode();
            if (responseCode == HTTP_CREATED || responseCode == HTTP_OK) {
                Log.d(TAG, "Successful " + method + ": " + jsonObject.toString());
                return new HttpUrlResponse(inputStreamToString(urlConnection.getInputStream()), responseCode);
            } else {
                Log.d(TAG, "CODE: " + responseCode);
                Log.d(TAG, inputStreamToString(urlConnection.getErrorStream()));
            }

        } catch (UnknownHostException e) {
            Log.e(TAG, "error", e);
        }catch (IOException e) {
            Log.e(TAG, "error", e);
            Log.e(TAG, inputStreamToString(urlConnection.getErrorStream()));
        } finally {
            urlConnection.disconnect();
        }
        return new HttpUrlResponse(inputStreamToString(urlConnection.getErrorStream()), responseCode);
    }

}
