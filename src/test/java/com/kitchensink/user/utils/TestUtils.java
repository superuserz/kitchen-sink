package com.kitchensink.user.utils;

import java.lang.reflect.Field;

public class TestUtils {
	public static void setFields(Object target, String fieldName, String value) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}