package jari.duyvejonck.SunnyPortaltoDB.sunnyportal.rest;

import lombok.Data;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Data
public class RequestInterceptor implements ClientHttpRequestInterceptor {


    private String token;

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);

        if (token.isEmpty()) {
            response.getHeaders().add("Authentication", String.format("Bearer %s", token));
        }

        return response;
    }

}
