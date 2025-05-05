package com.kitchensink.user.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GenAIServiceImpl implements GenAIService {

	@Autowired
	private final RestTemplate restTemplate;

	@Value("${open.ai.api.key}")
	private String openAIKey;

	@Value("${open.ai.api.model}")
	private String model;

	@Value("${open.ai.organisation.id}")
	private String OrganizationId;

	@Value("${open.ai.project.id}")
	private String projectId;

	@Value("${open.ai.chat.url}")
	private String url;

	private static String ORGANIZATION_HEADER = "OpenAI-Organization";

	private static String PROJECT_HEADER = "OpenAI-Project";

	public GenAIServiceImpl(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	@Override
	public void generateTestClass() throws IOException {
		// Step 1: Read source class
		Path sourcePath = Path.of("src/main/java/com/kitchensink/user/ai/SampleService.java");
		String controllerCode = Files.readString(sourcePath);

		// Step 2: Generate test class from OpenAI
		String prompt = "Given the following Spring Boot 3+ Java Class:\n\n" + controllerCode
				+ "\n\nGenerate a JUnit 5 test class using Mockito.";
		Message message = new Message("user", prompt);
		ChatRequest requestBody = new ChatRequest(model, List.of(message), 0.0, 1000);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(openAIKey);
		headers.add(ORGANIZATION_HEADER, OrganizationId);
		headers.add(PROJECT_HEADER, projectId);

		HttpEntity<ChatRequest> request = new HttpEntity<>(requestBody, headers);
		ResponseEntity<ChatResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				ChatResponse.class);

		// Step 3: Extract content from response
		String testClassContent = response.getBody().getChoices().get(0).getMessage().getContent();

		// Step 4: Derive test file name and package path
		String originalFileName = sourcePath.getFileName().toString(); // SampleService.java
		String testFileName = originalFileName.replace(".java", "Test.java"); // SampleServiceTest.java
		Path testFilePath = Path.of("src/test/java/com/kitchensink/user/ai", testFileName);

		// Ensure directories exist
		Files.createDirectories(testFilePath.getParent());

		// Step 5: Write test class to file
		Files.writeString(testFilePath, testClassContent);

		System.out.println("Test class generated at: " + testFilePath.toAbsolutePath());
	}
}
