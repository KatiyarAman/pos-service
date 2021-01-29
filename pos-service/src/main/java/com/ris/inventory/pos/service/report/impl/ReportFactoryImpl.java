package com.ris.inventory.pos.service.report.impl;

import com.ris.inventory.pos.service.report.*;
import com.ris.inventory.pos.util.enumeration.ReportType;
import com.ris.inventory.pos.util.exception.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportFactoryImpl implements ReportFactory {

    @Autowired
    private OrderReportService orderReportService;

    @Autowired
    private TransactionReportService transactionReportService;

    @Autowired
    private SalespersonReportService salespersonReportService;

    @Autowired
    private CustomerReportService customerReportService;

    @Autowired
    private PaymentReportService paymentReportService;

    @Override
    @SuppressWarnings("unchecked")
    public ReportService getInstance(ReportType reportType) {
        switch (reportType) {
            case ORDER_REPORT:
                return orderReportService;
            case TRANSACTION_REPORT:
                return transactionReportService;
            case SALES_PERSON_REPORT:
                return salespersonReportService;
            case CUSTOMER_REPORT:
                return customerReportService;
            case PAYMENT_REPORT:
                return paymentReportService;
            default:
                throw new NotSupportedException("Invalid Report Type.");
        }
    }
}
