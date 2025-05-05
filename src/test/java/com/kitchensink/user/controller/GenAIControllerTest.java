package com.kitchensink.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.kitchensink.user.ai.GenAIService;
import com.kitchensink.user.ai.GenAITestController;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GenAIControllerTest {

	@Mock
	private GenAIService genAIService;

	@InjectMocks
	private GenAITestController controller;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void testCreateTestClass_success() throws Exception {
		doNothing().when(genAIService).generateTestClass();

		MvcResult result = mockMvc.perform(get("/api/generate/tests").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		assertEquals("Request Submitted", content);

		verify(genAIService, times(1)).generateTestClass();
	}
}