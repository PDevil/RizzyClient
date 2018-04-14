package dev.lazyllamas.rizzyclient.Business;

public class APIUtils {

    public static final String BASE_URL = "http://10.239.232.131:2448/";

    private APIUtils() {
    }

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}