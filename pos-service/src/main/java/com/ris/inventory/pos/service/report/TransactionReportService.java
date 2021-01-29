package com.ris.inventory.pos.service.report;

import com.ris.inventory.pos.model.dto.ProductDTO;
import com.ris.inventory.pos.model.dto.RevenueDTO;
import com.ris.inventory.pos.model.response.TransactionResponse;

import java.util.List;

public interface TransactionReportService extends ReportService<TransactionResponse> {

    public List<RevenueDTO> systemRevenue();

    public List<ProductDTO> fetchTransactionsProducts();
}
