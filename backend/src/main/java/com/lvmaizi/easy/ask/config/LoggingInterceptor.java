package com.lvmaizi.easy.ask.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            ClientHttpResponse response = execution.execute(request, body);
            response = (ClientHttpResponse) Proxy.newProxyInstance(response.getClass().getClassLoader(), new Class[]{ClientHttpResponse.class}, new ClientHttpResponseHandler(response));
            display(request, body, response);
            return response;
        } catch (Exception e) {
            displayRequest(request, body);
            throw e;
        }
    }

    private void display(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        StringBuilder logStr = new StringBuilder();
        log.info("URI: {}", request.getURI());
        logStr.append("\n")
                .append("====request start====").append("\n")
//                .append(String.format("URI         : %s", request.getURI())).append("\n")
                .append(String.format("Method      : %s", request.getMethod())).append("\n")
                .append(String.format("Req Headers : %s", headersToString(request.getHeaders()))).append("\n")
                .append(String.format("Request body: %s", body == null ? "" : new String(body, StandardCharsets.UTF_8)))
        ;

        StringBuilder inputStringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
        }

        logStr.append("\n\n")
                .append("====response info====").append("\n")
                .append(String.format("Status code  : %s", response.getStatusCode())).append("\n")
                .append(String.format("Status text  : %s", response.getStatusText())).append("\n")
                .append(String.format("Resp Headers : %s", headersToString(response.getHeaders()))).append("\n")
                .append(String.format("Response body: %s", inputStringBuilder))
        ;
        log.debug("{}", logStr);
    }

    private void displayRequest(HttpRequest request, byte[] body) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("\n")
                .append("====request start====").append("\n")
                .append(String.format("URI         : %s", request.getURI())).append("\n")
                .append(String.format("Method      : %s", request.getMethod())).append("\n")
                .append(String.format("Req Headers : %s", headersToString(request.getHeaders()))).append("\n")
                .append(String.format("Request body: %s", body == null ? "" : new String(body, StandardCharsets.UTF_8)))
        ;
        log.debug("{}", logStr);
    }

    private String headersToString(HttpHeaders httpHeaders) {
        if (Objects.isNull(httpHeaders)) {
            return "[]";
        }
        return httpHeaders.headerSet().stream()
                .map(entry -> {
                    List<String> values = entry.getValue();
                    return "\t" + entry.getKey() + ":" + (values.size() == 1 ?
                            "\"" + values.get(0) + "\"" :
                            values.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")));
                })
                .collect(Collectors.joining(", \n", "\n[\n", "\n]\n"));
    }

    private static class ClientHttpResponseHandler implements InvocationHandler {
        private static final String methodName = "getBody";
        private final ClientHttpResponse clientHttpResponse;
        private byte[] body;

        ClientHttpResponseHandler(ClientHttpResponse clientHttpResponse) {
            this.clientHttpResponse = clientHttpResponse;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (StringUtils.equals(methodName, method.getName())) {
                if (Objects.isNull(this.body)) {
                    this.body = StreamUtils.copyToByteArray(this.clientHttpResponse.getBody());
                }
                return new ByteArrayInputStream(this.body);
            }
            return method.invoke(this.clientHttpResponse, args);
        }
    }
}
