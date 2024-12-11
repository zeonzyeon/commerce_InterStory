package com.app.interstory.config.globalExeption.customException;

public class PaymentException extends RuntimeException {
	public PaymentException(String message) {
		super(message);
	}
}
