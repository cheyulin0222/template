package com.arplanets.template.advice;

import com.arplanets.template.exception.RegistrationException;
import com.arplanets.template.exception.TemplateApiException;
import com.arplanets.template.log.Logger;
import com.arplanets.template.res.ErrorResponse;
import com.arplanets.template.utils.ClassUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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

import static com.arplanets.template.exception.ErrorType.*;
import static com.arplanets.template.log.enums.BasicActionType.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandleAdvice {

    /**
     * 處理 Controller Json 驗證失敗拋出的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(HttpServletRequest req, MethodArgumentNotValidException ex) {

        String errMsg = extractedBindingResult(ex.getBindingResult());

        Logger.error(errMsg, VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, errMsg);
    }

    /**
     * 處理 Controller URL 參數驗證失敗拋出的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(HttpServletRequest req,ConstraintViolationException ex) {

        String errMsg = extractedConstraintViolations(ex.getConstraintViolations());

        Logger.error(errMsg, VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, errMsg);
    }

    /**
     * 處理 Controller Form Data 驗證失敗拋出的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,BindException ex) {


        String errMsg = extractedBindingResult(ex.getBindingResult());

        Logger.error(errMsg, VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, errMsg);
    }

    /**
     * 處理 Spring 無法正確解析 HTTP 請求體（通常是 JSON）時拋出的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,HttpMessageNotReadableException ex) {
        Logger.error(ex.getMessage(), VALIDATE_REQUEST, REQUEST);

        return wrapperExceptionResponse(BAD_REQUEST, ExceptionUtils.getRootCauseMessage(ex));
    }

    /**
     * 處理登入失敗拋出的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,BadCredentialsException ex) {
        Logger.error(ex.getMessage(), AUTHENTICATE_USER, AUTH);

        return wrapperExceptionResponse(UNAUTHORIZED, ExceptionUtils.getRootCauseMessage(ex));
    }

    /**
     * 處理註冊失敗拋出的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponse>  handleValidationError(HttpServletRequest req,RegistrationException ex) {
        Logger.error(ex.getMessage(), REGISTER_USER, AUTH);

        return wrapperExceptionResponse(CONFLICT, ExceptionUtils.getRootCauseMessage(ex));
    }

    /**
     * 處理 Service 層級的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(TemplateApiException.class)
    public ResponseEntity<ErrorResponse> handleDataAcesssException(HttpServletRequest req, TemplateApiException ex){
        Logger.error(ex.getMessage(), ex.getActionType(), BUSINESS);

        return wrapperExceptionResponse(INTERNAL_SERVER_ERROR , ex.getCode().message());
    }

    /**
     * 處理 Database 層級的異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAcesssException(HttpServletRequest req,DataAccessException ex){
        Logger.error(ex.getMessage(), ACCESS_DATABASE, DATABASE, ex);
        return wrapperExceptionResponse(INTERNAL_SERVER_ERROR ,ExceptionUtils.getRootCauseMessage(ex));
    }

    /**
     * 處理其他異常
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknowException(HttpServletRequest req, Exception ex){

        Logger.error(ex.getMessage(), UNKNOWN, SYSTEM, ex);

        if (isAuthRelated(req)) {
            return wrapperExceptionResponse(INTERNAL_SERVER_ERROR , ex.getMessage());
        }

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

    private boolean isAuthRelated(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/auth");
    }
}
