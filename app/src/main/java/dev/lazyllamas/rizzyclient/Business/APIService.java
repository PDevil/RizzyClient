package dev.lazyllamas.rizzyclient.Business;

import android.provider.ContactsContract;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {

    @POST("/auth/register")
    Call<ResponseBody> userSignUp(@Body NewUser person);

    @POST("/auth/login")
    Call<ResponseBody> userSignIn(@Body NewUser person);

    @GET("/profile/{id}")
    Call<Person> getProfile(@Path("id") String userId);

    @PUT("/profile/{id}")
    Call<ResponseBody> pushProfile(@Path("id") String userId, @Body Person person);

    @GET("/nearby/{id}")
    Call<ArrayList<Person>> getNearby(@Path("id") String userId);

    @GET("/matches/{id}")
    Call<ArrayList<Person>> getMatches(@Path("id") String userId);

    @POST("/profile/{id}")
    Call<ResponseBody> updateLocation(@Path("id") String userId, @Body Person person);

    @Multipart
    @POST("/profile/avatar/{id}")
    Call<ResponseBody> uploadImage(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file);


}
