package com.mukesh.springbootHttpHandler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mukesh.springbootHttpHandler.adaptor.HttpHandler;
import com.mukesh.springbootHttpHandler.interceptors.CustomRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mukesh Bhoge
 * Reference : https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-httpclient-java-config/
 * https://springframework.guru/using-resttemplate-with-apaches-httpclient/
 * https://shankulk.com/auto-retries-in-rest-api-clients-using-spring-retry-c78cacb0cc29
 * Creating Customized rest template
 * It has multiple things to consider if looking for production ready client to consume  REST API
 *  1) Configurable connection timeout
 *  2) Configurable Request Timeout
 *  3) Configurable Number of connection
 *  4) Retry Logic
 *
 *  This Sample class will cover most of the above mentioned things.
 *
 **/
@Configuration
@Slf4j
public class HttpClientConfiguration {

    @Value("${http.http-pool.max.connection.per.route}")
    private int maxConnectionPerRoute;

    @Value("${http.http-pool.connection.timeout.ms}")
    private int connectionTimeout;

    @Value("${http.http-pool.connection.pool.timeout.ms}")
    private int connectionPoolTimeout;

    @Value("${http.http-pool.connection.request.timeout.ms}")
    private int connectionRequestTimeout;

    @Value("${http.http-pool.connection.socket.timeout.ms}")
    private int socketTimeout;

    @Value("${http.http-pool.connection.max.connections}")
    private int maxConnections;

    @Value("${http.http-pool.retry.backoff.initial.interval}")
    private long retryBackoffInitialInterval;

    @Value("${http.http-pool.retry.backoff.max.elapsed.time}")
    private long retryBackoffMaxElapsedTime;

    @Value("${http.http-pool.connection.max.connections}")
    private double retryMultiplier;

    @Value("${http.http-pool.retry.max.attempts}")
    private int retryMaxAttempts;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<Class<? extends Throwable>, Boolean> mapOfRetryAbleExceptions;

    public HttpClientConfiguration() {
        mapOfRetryAbleExceptions = createRetryAbleExceptions();
    }

    /**
     * List of Exceptions which can be retried
     *
     * @return
     */
    private Map<Class<? extends Throwable>, Boolean> createRetryAbleExceptions() {
        Map<Class<? extends Throwable>, Boolean> retryAbleExceptions = new HashMap<>();
        retryAbleExceptions.put(HttpServerErrorException.class, true);
        retryAbleExceptions.put(HttpServerErrorException.class, true);
        retryAbleExceptions.put(HttpServerErrorException.class, true);
        retryAbleExceptions.put(HttpServerErrorException.class, true);
        retryAbleExceptions.put(HttpServerErrorException.class, true);
        return retryAbleExceptions;
    }

    /**
     * Connection manager have different features like setting maximum connection per route
     * Maximum connections
     * https://hc.apache.org/httpcomponents-client-4.5.x/current/httpclient/apidocs/org/apache/http/impl/conn/PoolingHttpClientConnectionManager.html
     */
    @Bean("userHttpConnectionManager")
    public HttpClientConnectionManager userHttpConnectionManager() {
        SSLContext sslContext = null;
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial((chain, authType) -> true).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory =
                    new SSLConnectionSocketFactory(sslContext, new String[]
                            {"SSLv2Hello", "SSLv3", "TLSv1","TLSv1.1", "TLSv1.2" }, null,
                            NoopHostnameVerifier.INSTANCE);
            poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(RegistryBuilder.
                    <ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build());
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxConnectionPerRoute);
            poolingHttpClientConnectionManager.setMaxTotal(maxConnections);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return poolingHttpClientConnectionManager;
    }


    /**
     * HTTP client that will be customized as per need
     *
     * @return
     */
    @Bean("userHttpClient")
    public HttpClient userHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        return HttpClientBuilder.create()
                .disableAutomaticRetries()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(userHttpConnectionManager())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }

    @Bean("userHttpsHandler")
    public HttpHandler userHttpsHandler() {
        RetryTemplate userRetryTemplate = getRetryTemplate(retryBackoffInitialInterval,retryBackoffMaxElapsedTime,retryMultiplier,retryMaxAttempts,mapOfRetryAbleExceptions);
        return new HttpHandler(createRestTemplate(userHttpClient()),userRetryTemplate);
    }


    /**
     * @param httpClient
     * @return
     */
    private ClientHttpRequestFactory createHttpClientRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        return factory;
    }


    /**
     * Create Custom RestTemplate with  required configuration
     * https://gist.github.com/jkuipers/cd462d163c2c1c81f34092de12f7bab2
     *
     * @param httpClient
     * @return
     */
    public RestTemplate createRestTemplate(HttpClient httpClient) {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new FormHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(messageConverters());
        ClientHttpRequestFactory clientHttpRequestFactory = createHttpClientRequestFactory(httpClient);
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.setInterceptors(createInterceptors());
        return restTemplate;
    }

    /**
     * @return
     */
    private List<ClientHttpRequestInterceptor> createInterceptors() {
        List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = new ArrayList<>();
        clientHttpRequestInterceptors.add(new CustomRequestInterceptor(httpHeaders()));
        return clientHttpRequestInterceptors;
    }

    /**
     * @return
     */
    private HttpHeaders httpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return httpHeaders;
    }

    /**
     * @return
     */
    private HttpMessageConverter<?> messageConverters() {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        return jackson2HttpMessageConverter;
    }

    /**
     * @param retryBackoffInitialInterval
     * @param retryBackoffMaxElapsedTime
     * @param retryMultiplier
     * @param retryMaxAttempts
     * @param retryAbleExceptions
     * @return
     */
    public RetryTemplate getRetryTemplate(long retryBackoffInitialInterval, long retryBackoffMaxElapsedTime,
                                          double retryMultiplier, int retryMaxAttempts,
                                          Map<Class<? extends Throwable>, Boolean> retryAbleExceptions) {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(retryBackoffInitialInterval);
        exponentialBackOffPolicy.setMaxInterval(retryBackoffMaxElapsedTime);
        exponentialBackOffPolicy.setMultiplier(retryMultiplier);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        // simple retry policy
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(retryMaxAttempts, retryAbleExceptions, true);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
