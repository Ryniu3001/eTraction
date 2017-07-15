package pl.poznan.put.etraction.model;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Marcin on 06.05.2017.
 */

public class HttpUrlResponse <T> {

    String jsonResponse;
    int responseCode;
    boolean isOk;

    T objectResponse;

    public HttpUrlResponse(String jsonResponse, int code){
        this.jsonResponse = jsonResponse;
        this.responseCode = code;
        if (code == HTTP_OK || code == HTTP_CREATED || code == HTTP_ACCEPTED)
            this.isOk = true;
        else
            this.isOk = false;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }
    public T getObjectResponse() {
        return objectResponse;
    }

    public void setObjectResponse(T objectResponse) {
        this.objectResponse = objectResponse;
    }
}
