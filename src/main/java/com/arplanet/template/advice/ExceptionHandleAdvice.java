package com.arplanet.template.advice;

import com.arplanet.template.exception.RegistrationException;
import com.arplanet.template.exception.ApiServiceException;
import com.arplanet.template.log.Logger;
import com.arplanet.template.res.ErrorResponse;
import com.arplanet.template.utils.ClassUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import static com.arplanet.template.exception.ErrorType.*;
import static com.arplanet.template.log.enums.BasicActionType.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandleAdvice {

    private final Logger logger;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(HttpServletRequest req, MethodArgumentNotValidException ex) {

        String errMsg = extractedBindingResult(ex.getBindingResult());

        logger.error(errMsg, VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, errMsg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(HttpServletRequest req,ConstraintViolationException ex) {

        String errMsg = extractedConstraintViolations(ex.getConstraintViolations());

        logger.error(errMsg, VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, errMsg);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,BindException ex) {


        String errMsg = extractedBindingResult(ex.getBindingResult());

        logger.error(errMsg, VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, errMsg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,HttpMessageNotReadableException ex) {
        logger.error(ex.getMessage(), VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, ExceptionUtils.getRootCauseMessage(ex));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,BadCredentialsException ex) {
        logger.error(ex.getMessage(), AUTHENTICATE_USER, AUTH);

        return wrapperExceptionResponse(UNAUTHORIZED, ExceptionUtils.getRootCauseMessage(ex));
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,RegistrationException ex) {
        logger.error(ex.getMessage(), REGISTER_USER, AUTH);

        return wrapperExceptionResponse(CONFLICT, ExceptionUtils.getRootCauseMessage(ex));
    }

    @ExceptionHandler(ApiServiceException.class)
    public ResponseEntity<ErrorResponse> handleDataAcesssException(HttpServletRequest req, ApiServiceException ex){
        logger.error(ex.getMessage(), ex.getActionType(), BUSINESS);

        return wrapperExceptionResponse(INTERNAL_SERVER_ERROR , ex.getCode().message());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAcesssException(HttpServletRequest req,DataAccessException ex){
        logger.error(ex.getMessage(), ACCESS_DATABASE, ex, DATABASE);
        return wrapperExceptionResponse(INTERNAL_SERVER_ERROR ,ExceptionUtils.getRootCauseMessage(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknowException(HttpServletRequest req,Exception ex){

        logger.error(ex.getMessage(), UNKNOWN, ex, SYSTEM);

        return wrapperExceptionResponse(INTERNAL_SERVER_ERROR ,ExceptionUtils.getRootCauseMessage(ex));
    }

    private String extractedBindingResult(BindingResult bindingResult) {

        StringBuilder errorMessagesData = new StringBuilder();
        HashMap<String , Field> Fields = ClassUtil.getAllField(Objects.requireNonNull(bindingResult.getTarget()));

        for (ObjectError error : bindingResult.getAllErrors()) {
            if(!errorMessagesData.isEmpty()) {
                errorMessagesData.append(" ,");
            }

            if (error instanceof FieldError fieldError) {
                String Field = fieldError.getField();
                Field classFile = Fields.get(Field);
                if( classFile != null ) {
                    Schema schema = classFile.getAnnotation(Schema.class);
                    if( schema != null ) {
                        Field = schema.description();

                    }
                }
                errorMessagesData.append(Field.concat(":"));
            }
            errorMessagesData.append(error.getDefaultMessage());
        }

        return errorMessagesData.toString();

    }

    private String extractedConstraintViolations(Set<ConstraintViolation<?>> violations) {

        int index = 0;
        int size = violations.size();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : violations) {
            var filename = constraintViolation.getPropertyPath();
            sb.append(" ").append(filename).append(" : ").append(constraintViolation.getMessage());
            if (index != size - 1) {
                sb.append(";");
            }
            index++;
        }

        return sb.toString();
    }

    public static ResponseEntity<ErrorResponse> wrapperExceptionResponse(HttpStatus status, String data){
        ErrorResponse result = ErrorResponse.builder()
                .status(status.value())
                .message(data)
                .build();

        return new ResponseEntity<>(result, status);

    }
}
