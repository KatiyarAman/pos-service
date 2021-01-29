package com.ris.inventory.pos.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ris.inventory.pos.model.Error;
import com.ris.inventory.pos.util.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401
    @ResponseBody
    public Error authenticationException(AuthenticationException exception) {
        logger.error("Exception For : Unauthorised access for this resource");
        exception.printStackTrace();
        String defaultMsg = "Unauthorised access for this resource";
        return Error.setErrorResponse(HttpStatus.UNAUTHORIZED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400
    @ResponseBody
    public Error badRequestException(BadRequestException exception) {
        logger.error("Exception For : request received with invalid parameters or May be missing in request payload.");
        exception.printStackTrace();
        String defaultMsg = "Bad Request, Request received with invalid parameters or May be missing in request payload.";
        return Error.setErrorResponse(HttpStatus.BAD_REQUEST.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(CancellationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)//406
    @ResponseBody
    public Error cancellationException(CancellationException exception) {
        String defaultMsg = "Cancellation of order can not be possible after payment. You can Exchange or Refund this order.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    @ResponseBody
    public Error customerNotFoundException(CustomerNotFoundException exception) {
        String defaultMsg = "Requested customer is not found in our customer base. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.NOT_FOUND.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    @ResponseBody
    public Error dataNotFoundException(DataNotFoundException exception) {
        String defaultMsg = "Requested data is not found in our data base. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.NOT_FOUND.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(DuplicateRecordException.class)
    @ResponseStatus(HttpStatus.IM_USED)//226
    @ResponseBody
    public Error duplicateRecordException(DuplicateRecordException exception) {
        String defaultMsg = "Duplicate record can not be allowed";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.IM_USED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(EntityNotPersistException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)//417
    @ResponseBody
    public Error entityException(EntityNotPersistException exception) {
        String defaultMsg = "Entity can not be persisted due to some condition failed.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(EntityUpdateException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)//417
    @ResponseBody
    public Error entityUpdateException(EntityUpdateException exception) {
        String defaultMsg = "Entity can not be update due to some condition failed.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(ExchangeException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409
    @ResponseBody
    public Error exchangeException(ExchangeException exception) {
        String defaultMsg = "Exchange quantity error";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.CONFLICT.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(InventoryException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)//417
    @ResponseBody
    public Error inventoryException(InventoryException exception) {
        String defaultMsg = "Inventory quantity not available error";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    @ResponseBody
    public Error orderNotFoundException(OrderNotFoundException exception) {
        String defaultMsg = "Requested order data is not found in our order data base. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.NOT_FOUND.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(OrderStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)//422
    @ResponseBody
    public Error orderStateException(OrderStateException exception) {
        String defaultMsg = "Order state is not valid for this operation.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(OrderUpdateException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)//417
    @ResponseBody
    public Error orderUpdateException(OrderUpdateException exception) {
        String defaultMsg = "Order can not be update due to some condition failed.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(PaymentUpdateException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)//402
    @ResponseBody
    public Error paymentUpdateException(PaymentUpdateException exception) {
        String defaultMsg = "Payment can not be update due to some condition failed or may be missing.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.PAYMENT_REQUIRED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(SplitPaymentException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)//402
    @ResponseBody
    public Error splitPaymentException(SplitPaymentException exception) {
        String defaultMsg = "Split Payment mode required complete payable amount by using multiple payment methods.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.PAYMENT_REQUIRED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    @ResponseBody
    public Error productNotFoundException(ProductNotFoundException exception) {
        String defaultMsg = "Requested product data is not found in our order data base. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.NOT_FOUND.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    @ResponseBody
    public Error notFoundException(NotFoundException exception) {
        String defaultMsg = "Requested resource data is not found in our data base. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.NOT_FOUND.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(RefundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409
    @ResponseBody
    public Error refundException(RefundException exception) {
        String defaultMsg = "Refund quantity error";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.CONFLICT.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    @ResponseBody
    public Error transactionNotFoundException(TransactionNotFoundException exception) {
        String defaultMsg = "Requested transaction data is not found in our order data base. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.NOT_FOUND.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(NotSupportedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401
    @ResponseBody
    public Error notSupportedException(NotSupportedException exception) {
        String defaultMsg = "Provided role does not have access to access this resource.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.UNAUTHORIZED.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(DiscoveryException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)//503
    @ResponseBody
    public Error discoveryException(DiscoveryException exception) {
        String defaultMsg = "Requested resource can not be process due service connection problem. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(DiscoveryRequestException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)//424
    @ResponseBody
    public Error discoveryRequestException(DiscoveryRequestException exception) {
        String defaultMsg = "Requested resource output not received due to some error. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.FAILED_DEPENDENCY.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)//424
    @ResponseBody
    public Error jsonProcessingException(JsonProcessingException exception) {
        String defaultMsg = "Requested resource output not received due to JSON conversion error. Please try again.";
        logger.error("Exception For : {}", defaultMsg);
        exception.printStackTrace();
        return Error.setErrorResponse(HttpStatus.FAILED_DEPENDENCY.value(), getExceptionMessage(exception.getMessage(), defaultMsg));
    }

    private String getExceptionMessage(String message, String staticMessage) {
        return (message != null && !message.equals("")) ? message : staticMessage;
    }
}
