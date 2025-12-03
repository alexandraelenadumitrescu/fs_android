package com.example.frameshuttr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.frameshuttr.domain.imageProcessor.FeatureExtractor;
import com.example.frameshuttr.domain.network.APIService;
import com.example.frameshuttr.domain.network.VectorRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NodeActivity extends AppCompatActivity {
    private static final String BASE_URL="http://192.168.1.132:5000/";
    Button btnSend;
    Button btnVector;
    private FeatureExtractor featureExtractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_node);
        btnSend=findViewById(R.id.button2);
        btnVector=findViewById(R.id.buttonVector);
        featureExtractor = new FeatureExtractor(this);
        btnVector.setOnClickListener(v->{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
            //float[] vector=generateFakeEmbeddings(128);
            float[] vector=featureExtractor.extractFeatures(bitmap);
            sendEmbeddings(vector);//todo
        });
        btnSend.setOnClickListener(v->{
            Bitmap dummy= BitmapFactory.decodeResource(getResources(),R.drawable.img);
            uploadImage(dummy);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendEmbeddings(float[] vector) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.132:5000/") // URL-ul tău
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        // Creăm obiectul de cerere
        VectorRequest request = new VectorRequest(vector);

        apiService.sendVector(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        Log.d("VECTOR_TEST", "Server zice: " + result);
                        Toast.makeText(getApplicationContext(), "Analiză OK: " + result, Toast.LENGTH_LONG).show();
                    } catch (IOException e) { e.printStackTrace(); }
                } else {
                    Toast.makeText(getApplicationContext(), "Eroare: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Fail: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float[] generateFakeEmbeddings(int size) {
        float[] vector = new float[size];
        for (int i = 0; i < size; i++) {
            vector[i] = (float) Math.random(); // Valori între 0.0 și 1.0
        }
        return vector;
    }

    private void uploadImage(Bitmap dummy) {
        Retrofit retrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        APIService apiService=retrofit.create(APIService.class);
        
        File file=saveBitmapToFile(dummy);


        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);



        apiService.uploadImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Citim răspunsul de la Python
                        String serverResponse = response.body().string();
                        Log.d("SERVER_TEST", "Victorie: " + serverResponse);
                        Toast.makeText(getApplicationContext(), "Răspuns: " + serverResponse, Toast.LENGTH_LONG).show();
                    } catch (IOException e) { e.printStackTrace(); }
                } else {
                    Log.e("SERVER_TEST", "Eroare cod: " + response.code());
                    Toast.makeText(getApplicationContext(), "Eroare server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SERVER_TEST", "Nu merge netul: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Eroare conexiune! Vezi Logcat.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "test_image.jpg");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bitmapdata);
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    }
