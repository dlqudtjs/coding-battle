package com.dlqudtjs.codingbattle.configuration.gcp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StorageConfig {

	@Value("${gcs.private.key.id}")
	private String private_key_id;

	@Value("${gcs.private.key}")
	private String private_key;

	@Value("${gcs.client.id}")
	private String client_id;

	@Value("${spring.cloud.gcp.storage.credentials.location}")
	private String keyFileLocation;

	private final ResourceLoader resourceLoader;

	@Bean
	public Storage storage() throws IOException {
		private_key = private_key.replace("\\n", "\n");

		Resource resource = resourceLoader.getResource(keyFileLocation);

		String jsonString = new String(
			Files.readAllBytes(Paths.get(resource.getURI())));

		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		jsonObject.addProperty("private_key_id", private_key_id);
		jsonObject.addProperty("private_key", private_key);
		jsonObject.addProperty("client_id", client_id);

		// 새 파일 경로 (메모리에 저장)
		String newJsonString = jsonObject.toString();
		InputStream inputStream = new ByteArrayInputStream(
			newJsonString.getBytes());

		return StorageOptions.newBuilder()
			.setCredentials(GoogleCredentials.fromStream(inputStream))
			.build()
			.getService();
	}
}
