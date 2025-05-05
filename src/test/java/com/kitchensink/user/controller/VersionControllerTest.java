package com.kitchensink.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.kitchensink.user.controller.VersionController;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class VersionControllerTest {

	private VersionController versionController;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		versionController = new VersionController();

		// Inject @Value fields manually using reflection
		ReflectionTestUtils.setField(versionController, "version", "1.0.0");
		ReflectionTestUtils.setField(versionController, "commitHash", "abc1234");
		ReflectionTestUtils.setField(versionController, "buildTime", "2024-05-05T10:00:00Z");

		mockMvc = MockMvcBuilders.standaloneSetup(versionController).build();
	}

	@Test
	void testGetVersion_returnsVersionInfo() throws Exception {
		mockMvc.perform(get("/api/version")).andExpect(status().isOk()).andExpect(jsonPath("$.version").value("1.0.0"))
				.andExpect(jsonPath("$.commitHash").value("abc1234"))
				.andExpect(jsonPath("$.buildTime").value("2024-05-05T10:00:00Z"));
	}
}
