package com.app.service;

import okhttp3.*;

import java.io.File;
import java.nio.file.Path;

public class ImageService {

    private static final String BASE_URL = "http://localhost:8080";

    public String uploadImage(Path path) throws Exception {

        OkHttpClient client = new OkHttpClient();

        File file = path.toFile();

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/png"));

        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                                    .addFormDataPart("file", file.getName(), fileBody)
                                                    .build();

        Request request = new Request.Builder().url(BASE_URL + "/images/upload")
                                    .post(requestBody)
                                    .build();
        Response response = client.newCall(request).execute();

        String body = response.body().string();

        System.out.println(body);

        if (!response.isSuccessful()) {

            throw new RuntimeException(body);
        }

        return body;
    }
}