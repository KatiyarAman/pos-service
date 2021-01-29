package com.ris.inventory.pos.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ris.inventory.pos.model.response.PaymentResponse;
import com.ris.inventory.pos.util.enumeration.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class PaymentReportDTO implements PaymentResponse {

    private String location;

    private int transactionCount;

    @JsonProperty("paymentMethod")
    private PaymentMethod method;

    private float totalAmount;

    private List<TransactionDTO> transactions = new ArrayList<>();

}
