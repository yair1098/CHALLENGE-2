package com.LiterAluraChallenge.SERVICIOS;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiUsage {
    public String getData(String url){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> res = null;
        try{
            res = client
                    .send(req,HttpResponse.BodyHandlers.ofString());
        }catch (IOException e){
            throw new RuntimeException(e);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
        String json = res.body();
        return json;
    }
}
