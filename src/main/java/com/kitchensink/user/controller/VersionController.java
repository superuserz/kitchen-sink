package com.kitchensink.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

	@Value("${app.version}")
	private String version;

	@Value("${app.commitHash}")
	private String commitHash;

	@Value("${app.buildTime}")
	private String buildTime;

	@GetMapping("/api/version")
	public Map<String, String> getVersion() {
		Map<String, String> info = new HashMap<>();
		info.put("version", version);
		info.put("commitHash", commitHash);
		info.put("buildTime", buildTime);
		return info;
	}
}
