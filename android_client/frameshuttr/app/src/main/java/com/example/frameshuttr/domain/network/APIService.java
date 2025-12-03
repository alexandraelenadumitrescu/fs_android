package com.example.frameshuttr.domain.network;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface APIService {
    @Multipart//trimitem fisiere binare
    @POST("upload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part image);

    @POST("analyze_vector")
    Call<ResponseBody> sendVector(@Body VectorRequest request);
}
