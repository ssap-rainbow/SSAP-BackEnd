package ssap.ssap.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProxyConfig {
    @Value("${PROXY_HOST:defaultProxyHost}")
    private String proxyHost;

    @Value("${PROXY_PORT:8080}")
    private int proxyPort;

    @Bean
    public RestTemplate restTemplate() {
        // Apache HttpClient5를 사용하는 HttpClientBuilder 생성
        var clientBuilder = HttpClients.custom();

        // 프록시 호스트가 설정되어 있다면 프록시를 적용합니다.
        if (!proxyHost.isEmpty()) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            clientBuilder.setProxy(proxy);
        }

        // HttpClient 인스턴스 생성
        var httpClient = clientBuilder.build();

        // HttpClient 인스턴스를 사용하여 HttpComponentsClientHttpRequestFactory 생성
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(requestFactory);
    }
}