package pl.poznan.put.etraction.model;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Marcin on 06.05.2017.
 */

public class HttpUrlResponse {

    String body;
    int responseCode;
    boolean isOk;

    public HttpUrlResponse(String body, int code){
        this.body = body;
        this.responseCode = code;
        this.isOk = code == HTTP_OK ? true : false;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
}
