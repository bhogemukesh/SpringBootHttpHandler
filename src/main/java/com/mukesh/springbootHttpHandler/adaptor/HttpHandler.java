package com.mukesh.springbootHttpHandler.adaptor;

import com.mukesh.springbootHttpHandler.Exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.*;

import java.util.Map;

/**
 * @author Mukesh Bhoge
 **/
@Slf4j
public class HttpHandler {
    private RestTemplate restTemplate;
    private RetryTemplate retryTemplate;

    public HttpHandler(RestTemplate restTemplate, RetryTemplate retryTemplate) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }


    /**
     * @param serviceName
     * @param url
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @param <T>
     * @return
     */
    public <T> T callRestTemplate(String serviceName, String url, HttpMethod method,
                                                  HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables, HttpStatus expectedStatus) {
        try {
            ResponseEntity<T> response =  retryAbleExecute(serviceName, url, method, requestEntity, responseType, uriVariables);
            if (null == response){
                throw  new CustomRuntimeException(serviceName,"Response Is Null");
            } else if(response!=null && response.getStatusCode() != expectedStatus) {
                throw  new CustomRuntimeException(serviceName,"Expected Response code not matched");
            } else {
                return response.getBody();
            }
        } catch (HttpServerErrorException httpServerEx) {
            // Here we can create custom exception object where we can pass error object and all those details
            throw new CustomRuntimeException(serviceName,httpServerEx);
        } catch (UnknownHttpStatusCodeException unknownStatusEx) {
            throw new CustomRuntimeException(serviceName, unknownStatusEx);
        }  catch (ResourceAccessException resourceAccessEx) {
            throw new CustomRuntimeException(serviceName, resourceAccessEx);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new CustomRuntimeException(serviceName, exception);
        }
    }

    /**
     * @param serviceName
     * @param url
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @param <T>
     * @return
     */
    public <T> ResponseEntity<T> retryAbleExecute(String serviceName, String url,
                                                  HttpMethod method, @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) {

        ResponseEntity<T> response = retryTemplate.execute(new RetryCallback<ResponseEntity<T>, RuntimeException>() {
            @Override
            public ResponseEntity<T> doWithRetry(RetryContext retryContext) {
                Exception exception = null;
                ResponseEntity<T> responseEntity = null;
                try {
                    responseEntity = restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
                } catch (HttpClientErrorException clientException) {
                    throw new CustomRuntimeException(serviceName,clientException);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CustomRuntimeException(serviceName,e);
                } finally {
                   // Put logs for tracking purpose
                }
                return responseEntity;
            }
        });
        return response;
    }

}
