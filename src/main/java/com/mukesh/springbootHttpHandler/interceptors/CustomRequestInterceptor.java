package com.mukesh.springbootHttpHandler.interceptors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Mukesh Bhoge
 **/
public class CustomRequestInterceptor implements ClientHttpRequestInterceptor {

    private final HttpHeaders headers;

    private final String REQUEST_ID = "REQUEST-ID";

    /**
     * @param httpHeaders
     */
    public CustomRequestInterceptor(HttpHeaders httpHeaders) {
        this.headers = httpHeaders;
    }


    /**
     * @param request
     * @param body
     * @param execution
     * @return
     * @throws IOException
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders requestHeaders = request.getHeaders();
        if (requestHeaders!=null){
            requestHeaders.putAll(headers);
        } else {
            requestHeaders =  new HttpHeaders();
            requestHeaders.putAll(headers);
        }

        if (CollectionUtils.isEmpty(requestHeaders.get(HttpHeaders.CONTENT_TYPE))) {
            requestHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }
        // Here we can add any required  parameters like request tracking id
        String uniqueRequestId = request.getHeaders().getFirst("REQUEST-ID");
        if (null == uniqueRequestId) {
            uniqueRequestId = UUID.randomUUID().toString();
            request.getHeaders().add("REQUEST-ID",uniqueRequestId);
        }
        ClientHttpResponse clientHttpResponse = null;

        try {
            clientHttpResponse = execution.execute(request,body);
        } finally {
            if (clientHttpResponse!=null){
                clientHttpResponse.getHeaders().add(REQUEST_ID,uniqueRequestId);
            }
        }
        return clientHttpResponse;
    }
}
