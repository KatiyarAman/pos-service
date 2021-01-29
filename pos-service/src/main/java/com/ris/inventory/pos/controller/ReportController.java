package com.ris.inventory.pos.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.dto.*;
import com.ris.inventory.pos.model.response.SalespersonResponse;
import com.ris.inventory.pos.model.response.TransactionResponse;
import com.ris.inventory.pos.service.report.ReportFactory;
import com.ris.inventory.pos.service.report.ReportService;
import com.ris.inventory.pos.service.report.SalespersonReportService;
import com.ris.inventory.pos.service.report.TransactionReportService;
import com.ris.inventory.pos.util.DocumentUtil;
import com.ris.inventory.pos.util.enumeration.FilterType;
import com.ris.inventory.pos.util.enumeration.ReportType;
import com.ris.inventory.pos.util.exception.DataNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@CrossOrigin
@Api(description = "Operations like Get analysis report for Transactions, Refund, Exchange etc", tags = "APIs for Report Analysis")
public class ReportController {

    @Autowired
    private ReportFactory reportFactory;

    @Autowired
    private DocumentUtil documentUtil;

    @ApiOperation(value = "Complete Order Analysis Report")
    @RequestMapping(value = "/order", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<OrderReportDTO> orderReport(@RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                     @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                     @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                     @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                     @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<OrderReportDTO> reportService = reportFactory.getInstance(ReportType.ORDER_REPORT);
        return reportService.report(offset, limit, currentUser);
    }

    @ApiOperation(value = "Filter Order Analysis Report")
    @RequestMapping(value = "/order/filter", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<OrderReportDTO> orderReportFilter(@RequestParam(name = "tag") @NotNull List<FilterType> filterType,
                                                           @RequestParam(name = "value") @NotNull String filterCO,
                                                           @RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                           @RequestHeader("X_AUTHORITY") @NotBlank @NotNull @NotBlank String authority,
                                                           @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                           @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                           @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<OrderReportDTO> reportService = reportFactory.getInstance(ReportType.ORDER_REPORT);
        return reportService.filter(filterType, reportService.fetchJSON(filterCO), offset, limit, currentUser);
    }

    @ApiOperation(value = "Transaction Analysis Report")
    @RequestMapping(value = "/transaction", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<TransactionDTO> transactionReport(@RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                           @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                           @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                           @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                           @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<TransactionDTO> reportService = reportFactory.getInstance(ReportType.TRANSACTION_REPORT);
        return reportService.report(offset, limit, currentUser);
    }

    @ApiOperation(value = "Transaction Report filter by date and tag like monthly")
    @RequestMapping(value = "/transaction/filter", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<TransactionDTO> transactionReportFilter(@RequestParam(name = "tag") @NotNull List<FilterType> filterType,
                                                                 @RequestParam(name = "value") @NotNull String filterCO,
                                                                 @RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                                 @RequestHeader("X_AUTHORITY") @NotBlank @NotNull @NotBlank String authority,
                                                                 @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                                 @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                                 @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<TransactionDTO> reportService = reportFactory.getInstance(ReportType.TRANSACTION_REPORT);
        return reportService.filter(filterType, reportService.fetchJSON(filterCO), offset, limit, currentUser);
    }

    @ApiOperation(value = "Sale's Analysis Report")
    @RequestMapping(value = "/sales", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<SaleReportDTO> salesReport(@RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                    @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority, @RequestHeader("X_USER_ID") @NotBlank @NotNull String userId,
                                                    @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<SaleReportDTO> reportService = reportFactory.getInstance(ReportType.SALES_PERSON_REPORT);
        return reportService.report(offset, limit, currentUser);
    }

    @ApiOperation(value = "Sales Report filter by date and tag like monthly")
    @RequestMapping(value = "/sales/filter", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<SaleReportDTO> salesReportFilter(@RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                          @RequestParam(name = "tag") @NotNull List<FilterType> filterType, @RequestParam(name = "value") @NotNull String filterCO,
                                                          @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority, @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                          @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<SaleReportDTO> reportService = reportFactory.getInstance(ReportType.SALES_PERSON_REPORT);
        return reportService.filter(filterType, reportService.fetchJSON(filterCO), offset, limit, currentUser);
    }


    @ApiOperation(value = "Customer Analysis Report")
    @RequestMapping(value = "/customer", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<CustomerReportDTO> customerReport(@RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                           @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                           @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                           @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                           @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<CustomerReportDTO> reportService = reportFactory.getInstance(ReportType.CUSTOMER_REPORT);
        return reportService.report(offset, limit, currentUser);
    }

    @ApiOperation(value = "Customer Report filter by date and tag provided in ENUM List")
    @RequestMapping(value = "/customer/filter", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<CustomerReportDTO> customerReportFilter(@RequestParam(name = "tag") @NotNull List<FilterType> filterType,
                                                                 @RequestParam(name = "value") @NotNull String filterCO,
                                                                 @RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                                 @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                                 @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                                 @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                                 @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<CustomerReportDTO> reportService = reportFactory.getInstance(ReportType.CUSTOMER_REPORT);
        return reportService.filter(filterType, reportService.fetchJSON(filterCO), offset, limit, currentUser);
    }

    @ApiOperation(value = "Payment Report filter by date and tag like monthly")
    @RequestMapping(value = "/payment/filter", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<PaymentReportDTO> paymentReportFilter(@RequestParam(name = "tag") @NotNull List<FilterType> filterType,
                                                               @RequestParam(name = "value") @NotNull String filterCO,
                                                               @RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                                               @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                               @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                               @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                               @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<PaymentReportDTO> reportService = reportFactory.getInstance(ReportType.PAYMENT_REPORT);
        return reportService.filter(filterType, reportService.fetchJSON(filterCO), offset, limit, currentUser);
    }

    @ApiOperation(value = "System Revenue Report")
    @RequestMapping(value = "/revenue", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public List<RevenueDTO> totalRevenueCalculation(@RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                    @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                    @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                    @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<TransactionResponse> reportService = reportFactory.getInstance(ReportType.TRANSACTION_REPORT);
        TransactionReportService transactionReportService = (TransactionReportService) reportService;
        return transactionReportService.systemRevenue();
    }

    @ApiOperation(value = "Sales person Revenue Report")
    @RequestMapping(value = "/revenue/salesperson", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public List<SalesPersonRevenueDTO> salesPersonRevenueCalculation(@RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                                     @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                                     @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                                     @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<SalespersonResponse> reportService = reportFactory.getInstance(ReportType.SALES_PERSON_REPORT);
        SalespersonReportService salespersonReportService = (SalespersonReportService) reportService;
        return salespersonReportService.salesPersonRevenue(currentUser);
    }

    @ApiOperation(value = "Download PDF format report for selected rows.")
    @RequestMapping(value = "/download/pdf", method = RequestMethod.GET, produces = "application/json")
    public DownloadDTO createPDF(@RequestParam("report") @NonNull ReportType report, @RequestParam("keys") @NonNull List<String> keys, HttpServletResponse response,
                                 @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                 @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                 @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                 @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) throws JsonProcessingException {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService reportService = reportFactory.getInstance(report);
        DownloadDTO downloadable = reportService.download(keys);
        if (!downloadable.isDataDownloadable())
            throw new DataNotFoundException("Data not found to create PDF");
        return downloadable;
    }

    @ApiOperation(value = "Download EXCEL format report for selected rows.")
    @RequestMapping(value = "/download/excel", method = RequestMethod.GET, produces = "application/json")
    public Map<String, String> createExcel(@RequestParam("report") @NonNull ReportType report, @RequestParam("keys") @NonNull List<String> keys, HttpServletResponse response,
                                           @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                           @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                           @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                           @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) throws IOException {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService reportService = reportFactory.getInstance(report);
        DownloadDTO downloadable = reportService.download(keys);
        if (!downloadable.isDataDownloadable())
            throw new DataNotFoundException("Data not found to create EXCEL(xlsx)");

        String filePath = documentUtil.getPath(documentUtil.getFileName(report, DocumentUtil.EXCEL_EXTENSION));
        documentUtil.createExcel(filePath, downloadable);
        //documentUtil.send(response, new File(filePath));
        Map<String, String> output = new HashMap<>();
        output.put("fileName", new File(filePath).getName());
        return output;
    }

    @ApiOperation(value = "Download CSV format report for selected rows.")
    @RequestMapping(value = "/download/csv", method = RequestMethod.GET, produces = "application/json")
    public DownloadDTO createCSV(@RequestParam("report") @NonNull ReportType report, @RequestParam("keys") @NonNull List<String> keys, HttpServletResponse response,
                                 @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                 @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                 @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                 @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) throws JsonProcessingException {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService reportService = reportFactory.getInstance(report);
        DownloadDTO downloadable = reportService.download(keys);
        if (!downloadable.isDataDownloadable())
            throw new DataNotFoundException("Data not found to create CSV");

        return downloadable;
    }

    @ApiOperation(value = "Products list of till given transaction date.")
    @RequestMapping(value = "/transaction/products", method = RequestMethod.GET, produces = "application/json")
    public List<ProductDTO> getProductsOfTransactions(@RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority,
                                                      @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                                                      @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                                      @RequestHeader("X_LOCATION") @NotNull @NotBlank String location) throws JsonProcessingException {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        ReportService<TransactionResponse> reportService = reportFactory.getInstance(ReportType.TRANSACTION_REPORT);
        TransactionReportService transactionReportService = (TransactionReportService) reportService;
        return transactionReportService.fetchTransactionsProducts();
    }
}
