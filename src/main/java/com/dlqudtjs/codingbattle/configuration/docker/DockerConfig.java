package com.dlqudtjs.codingbattle.configuration.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Value("${docker.host}")
    private String dockerHost;

    @Bean
    public DockerClientConfig dockerClient() {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();
    }

    @Bean
    public DockerHttpClient dockerHttpClient(DockerClientConfig dockerClientConfig) {
        return new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .build();
    }

    @Bean
    public DockerClient customDockerClient(DockerClientConfig dockerClientConfig, DockerHttpClient dockerHttpClient) {
        return DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
    }
}
