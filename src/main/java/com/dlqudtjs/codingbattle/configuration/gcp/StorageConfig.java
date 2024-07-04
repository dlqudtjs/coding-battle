package com.dlqudtjs.codingbattle.configuration.gcp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Configuration
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

	@Bean
	public Storage storage() throws IOException {
		InputStream keyFile = ResourceUtils.getURL(keyFileLocation)
			.openStream();
		return StorageOptions.newBuilder()
			.setCredentials(GoogleCredentials.fromStream(keyFile))
			.build()
			.getService();
	}

	// gcs-key json 파일에 private_key_id, private_key, client_id 추가
	@PostConstruct
	public void init() throws IOException {
		log.info("keyFileLocation = {}", keyFileLocation);
		log.info("private_key_id = {}", private_key_id);
		log.info("private_key = {}", private_key);
		log.info("client_id = {}", client_id);
		String jsonString = new String(
			Files.readAllBytes(Paths.get(keyFileLocation)));

		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

		jsonObject.addProperty("private_key_id", private_key_id);
		jsonObject.addProperty("private_key", private_key);
		jsonObject.addProperty("client_id", client_id);

		Files.write(Paths.get(keyFileLocation),
			jsonObject.toString().getBytes());
	}
}
