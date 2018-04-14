package dev.lazyllamas.rizzyclient.Business;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {

    @POST("/auth/register")
    Call<ResponseBody> userSignUp(@Body NewUser person);

    @POST("/auth/login")
    Call<ResponseBody> userSignIn(@Body NewUser person);


}
