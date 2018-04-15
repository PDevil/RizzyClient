package dev.lazyllamas.rizzyclient.Business;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

    @POST("/auth/register")
    Call<ResponseBody> userSignUp(@Body NewUser person);

    @POST("/auth/login")
    Call<ResponseBody> userSignIn(@Body NewUser person);

    @POST("/profile/{id}")
    Call<ResponseBody> updateLocation(@Path("id") String userId, @Body Person person);


}
