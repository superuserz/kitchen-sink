package com.kitchensink.user.ai;

import java.util.List;

public class ChatResponse {
	private List<Choice> choices;

	public List<Choice> getChoices() {
		return choices;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}

	public String getContent() {
		if (choices != null && !choices.isEmpty()) {
			return choices.get(0).getMessage().getContent();
		}
		return null;
	}
}
