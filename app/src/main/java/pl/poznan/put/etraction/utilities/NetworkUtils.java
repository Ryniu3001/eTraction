package pl.poznan.put.etraction.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Marcin on 12.04.2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    public final static String STATEMENTS_BASE_URL = "https://etraction.herokuapp.com/api/v1/statements";
    public final static String MOVIES_BASE_URL = "https://etraction.herokuapp.com/api/v1/movies";
    public final static String CAMERAS_BASE_URL = "https://etraction.herokuapp.com/api/v1/cameras";

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

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
