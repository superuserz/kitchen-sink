package com.kitchensink.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.kitchensink.user.ai.ChatResponse;
import com.kitchensink.user.ai.Choice;
import com.kitchensink.user.ai.GenAIServiceImpl;
import com.kitchensink.user.ai.Message;

@ExtendWith(MockitoExtension.class)
class GenAIServiceImplTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private GenAIServiceImpl genAIService;

	@BeforeEach
	void injectConfig() {
		ReflectionTestUtils.setField(genAIService, "openAIKey", "dummy-key");
		ReflectionTestUtils.setField(genAIService, "model", "gpt-4");
		ReflectionTestUtils.setField(genAIService, "OrganizationId", "org-123");
		ReflectionTestUtils.setField(genAIService, "projectId", "proj-123");
		ReflectionTestUtils.setField(genAIService, "url", "http://mock-openai.com/v1/chat");
	}

	@Test
	void testGenerateTestClass_success() throws IOException {
		Path sourcePath = Path.of("src/main/java/com/kitchensink/user/ai/SampleService.java");
		Path testPath = Path.of("src/test/java/com/kitchensink/user/ai/SampleServiceTest.java");

		String dummySourceCode = "public class SampleService {}";
		String generatedTestCode = "public class SampleServiceTest {}";

		Message message = new Message("assistant", generatedTestCode);
		Choice choice = new Choice();
		choice.setMessage(message);
		ChatResponse response = new ChatResponse();
		response.setChoices(List.of(choice));

		when(restTemplate.exchange(eq("http://mock-openai.com/v1/chat"), eq(HttpMethod.POST), any(HttpEntity.class),
				eq(ChatResponse.class))).thenReturn(ResponseEntity.ok(response));

		try (MockedStatic<Files> files = mockStatic(Files.class)) {
			files.when(() -> Files.readString(sourcePath)).thenReturn(dummySourceCode);
			files.when(() -> Files.createDirectories(testPath.getParent())).thenReturn(testPath.getParent());
			files.when(() -> Files.writeString(eq(testPath), eq(generatedTestCode))).thenReturn(testPath);

			// Act
			genAIService.generateTestClass();

			// Verify
			verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(ChatResponse.class));
			files.verify(() -> Files.readString(sourcePath), times(1));
			files.verify(() -> Files.writeString(eq(testPath), eq(generatedTestCode)), times(1));
		}
	}
}
