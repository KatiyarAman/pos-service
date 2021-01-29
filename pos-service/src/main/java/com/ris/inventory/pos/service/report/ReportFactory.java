package com.ris.inventory.pos.service.report;

import com.ris.inventory.pos.model.response.Response;
import com.ris.inventory.pos.util.enumeration.ReportType;

public interface ReportFactory {

    public <T extends Response> ReportService<T> getInstance(ReportType reportType);
}
