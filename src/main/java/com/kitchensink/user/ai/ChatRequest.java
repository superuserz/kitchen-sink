package com.kitchensink.user.ai;

import java.util.List;

public class ChatRequest {
	private String model;
	private List<Message> messages;
	private double temperature;
	private int max_tokens;

	// Constructors
	public ChatRequest() {
	}

	public ChatRequest(String model, List<Message> messages, double temperature, int max_tokens) {
		this.model = model;
		this.messages = messages;
		this.temperature = temperature;
		this.max_tokens = max_tokens;
	}

	// Getters and setters
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public int getMax_tokens() {
		return max_tokens;
	}

	public void setMax_tokens(int max_tokens) {
		this.max_tokens = max_tokens;
	}
}