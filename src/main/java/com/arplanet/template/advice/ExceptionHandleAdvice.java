package com.arplanet.template.advice;

import com.arplanet.template.exception.ErrorType;
import com.arplanet.template.log.Logger;
import com.arplanet.template.res.ResponseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;
import java.util.StringJoiner;

import static com.arplanet.template.exception.ErrorType.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandleAdvice {

    private final Logger logger;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseModel<String> handleValidationError(HttpServletRequest req, MethodArgumentNotValidException ex) {
        loggerErrorMessage(req,REQUEST,ex);
        return extractedBindingResult(ex.getBindingResult());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseModel<String> handleValidationError(HttpServletRequest req,ConstraintViolationException ex) {
        loggerErrorMessage(req,REQUEST,ex);
        return extractedConstraintViolations(ex.getConstraintViolations());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseModel<String>  handleValidationError(HttpServletRequest req,BindException ex) {
        loggerErrorMessage(req,REQUEST,ex);
        return extractedBindingResult(ex.getBindingResult());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseModel<String>  handleValidationError(HttpServletRequest req,HttpMessageNotReadableException ex) {
        loggerErrorMessage(req,REQUEST,ex);
        return wrapperExceptionResponse(REQUEST, ExceptionUtils.getRootCauseMessage(ex));
    }

    private ResponseModel<String> extractedBindingResult(BindingResult bindingResult) {

        StringBuffer errorMessagesData = new StringBuffer();
        HashMap<String , Field> Fields = ClassTool.getAllField(bindingResult.getTarget());

        for (ObjectError error : bindingResult.getAllErrors()) {
            if( errorMessagesData.length() != 0 ) {
                errorMessagesData.append(" ,");
            }

            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
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

        return wrapperExceptionResponse(REQUEST, errorMessagesData.toString());
    }

    private ResponseModel<String> extractedConstraintViolations(Set<ConstraintViolation<?>> violations) {

        int index = 0;
        int size = violations.size();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : violations) {
            var filename = constraintViolation.getPropertyPath();
            sb.append(" " + filename + " : " + constraintViolation.getMessage());
            if (index != size - 1) {
                sb.append(";");
            }
            index++;
        }

        return wrapperExceptionResponse(REQUEST, sb.toString());
    }

    @ExceptionHandler(SaApiException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel<String> handleDataAcesssException(HttpServletRequest req,SaApiException ex){
        loggerErrorMessage(req,ex.getErrorType(),ex);
        return wrapperBusinessExceptionResponse(ex.getErrorType(),ex.getCode(),ExceptionUtils.getRootCauseMessage(ex));
    }


    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel<String> handleDataAcesssException(HttpServletRequest req,DataAccessException ex){
        loggerErrorMessage(req,DATABASE,ex);
        return wrapperExceptionResponse(DATABASE ,ExceptionUtils.getRootCauseMessage(ex));
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel<String> handleUnknowException(HttpServletRequest req,Exception ex){
        loggerErrorMessage(req,SYSTEM,ex);
        return wrapperExceptionResponse(SYSTEM ,ExceptionUtils.getRootCauseMessage(ex));
    }

    private void loggerErrorMessage(HttpServletRequest req , ErrorType errorType, Exception ex) {
        StringJoiner joiner = new StringJoiner(" ");
        logger.error(joiner.add(req.getRequestURI()).add(errorType.name()).add(ExceptionUtils.getStackTrace(ex)).toString());
    }

    private ResponseModel<String> wrapperBusinessExceptionResponse(ErrorType errorType,BusinessExceptionDisplay code,String data){
        ResponseModel<String> resultResponse = new ResponseModel<String>(data);
        resultResponse.setCode(errorType.name().concat("[").concat(code.description()).concat("]"));
        resultResponse.setMessage(code.message());
        return resultResponse;
    }

    public static ResponseModel<String> wrapperExceptionResponse(ErrorType errorType,String data){
        ResponseModel<String> resultResponse = new ResponseModel<String>(data);
        resultResponse.setCode(errorType.name());
        resultResponse.setMessage(errorType.getLabel());
        return resultResponse;
    }
}
