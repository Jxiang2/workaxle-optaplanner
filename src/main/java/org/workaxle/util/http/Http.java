package org.workaxle.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.workaxle.vo.Result;

import java.io.IOException;
import java.net.URL;

public class Http {


    public static Result httpGet(URL url, String token) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(url.toString());
            request.addHeader("accept", "application/json");
            request.addHeader("Authorization", "Bearer " + token);

            CloseableHttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            String reasonPhrase = response.getStatusLine().getReasonPhrase();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String data = EntityUtils.toString(entity);
                    return Result.success(data);
                }
                response.close();
                return Result.success(null);
            }

            response.close();
            return Result.failure(statusCode, reasonPhrase);

        } catch (IOException ex) {
            return Result.failure(500, "System error");
        }
    }

    public static Result httpPost(URL url, Object data, String token) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(url.toString());
            StringEntity body = new StringEntity(data.toString());
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", "Bearer " + token);
            request.setEntity(body);

            CloseableHttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            String reasonPhrase = response.getStatusLine().getReasonPhrase();

            if (statusCode < 299) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseData = EntityUtils.toString(entity);
                    return Result.success(responseData);
                }
                response.close();
                return Result.success(null);
            }

            response.close();
            return Result.failure(statusCode, reasonPhrase);
        } catch (IOException ex) {
            return Result.failure(500, "System error");
        }
    }


}
