package com.sharefiles.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<?> handelFileNotFoundException(FileNotFoundException e){
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}
