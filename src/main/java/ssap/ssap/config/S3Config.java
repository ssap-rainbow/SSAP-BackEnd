package ssap.ssap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {
    @Value("${AWS_REGION:us-west-2}")
    private String region;

    @Value("${AWS_ACCESS_KEY_ID:defaultAccessKeyId}")
    private String accessKeyId;

    @Value("${AWS_SECRET_ACCESS_KEY:defaultSecretKey}")
    private String secretKey;

    @Value("${PROXY_HOST:defaultProxyHost}")
    private String proxyHost;

    @Value("${PROXY_PORT:8080}")
    private int proxyPort;


    @Bean
    public S3Client s3Client() {
        SdkHttpClient httpClient = ApacheHttpClient.builder()
                .proxyConfiguration(ProxyConfiguration.builder()
                        .endpoint(URI.create("http://" + proxyHost + ":" + proxyPort))
                        .build())
                .build();

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretKey)))
                .httpClient(httpClient)
                .build();
    }
}
