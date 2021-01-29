package com.ris.inventory.pos.service.report;

import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.dto.SalesPersonRevenueDTO;
import com.ris.inventory.pos.model.response.SalespersonResponse;

import java.util.List;

public interface SalespersonReportService extends ReportService<SalespersonResponse> {

    public List<SalesPersonRevenueDTO> salesPersonRevenue(CurrentUser currentUser);
}
