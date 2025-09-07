package com.uade.tpo.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El código de descuento ya existe")
public class DiscountDuplicateException extends Exception { }
