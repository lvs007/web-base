package com.liang.mvc.commons;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ErrorUtils {
	public static ErrorField getErrorField(FieldError fieldError) {
		return new ErrorField(fieldError.getField(),
				fieldError.getDefaultMessage());
	}

	public static List<ErrorField> getErrorFields(List<FieldError> fieldErrors) {
		List<ErrorField> results = new ArrayList<ErrorField>();
		if (fieldErrors != null) {
			for (FieldError fieldError : fieldErrors) {
				results.add(getErrorField(fieldError));
			}
		}
		return results;
	}
}
