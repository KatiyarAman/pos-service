package com.ris.inventory.pos.service.report.impl;

import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.FilterCO;
import com.ris.inventory.pos.model.dto.DownloadDTO;
import com.ris.inventory.pos.model.dto.PaginationDTO;
import com.ris.inventory.pos.model.response.PaymentResponse;
import com.ris.inventory.pos.service.report.PaymentReportService;
import com.ris.inventory.pos.util.enumeration.FilterType;
import com.ris.inventory.pos.util.exception.NotSupportedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentReportServiceImpl implements PaymentReportService {

    @Override
    public PaginationDTO<PaymentResponse> report(int offset, int limit, CurrentUser currentUser) {
        return null;
    }

    @Override
    public List<PaymentResponse> report(CurrentUser currentUser) {
        throw new NotSupportedException("Service not implemented yet");
    }

    @Override
    public List<PaymentResponse> filter(List<FilterType> filterType, FilterCO value, CurrentUser currentUser) {
        throw new NotSupportedException("Service not implemented yet");
    }

    @Override
    public DownloadDTO download(List keys) {
        throw new NotSupportedException("Service not implemented yet");
    }

    @Override
    public PaginationDTO<PaymentResponse> filter(List filterType, FilterCO value, int offset, int limit, CurrentUser currentUser) {
        throw new NotSupportedException("Service not implemented yet");
    }
}
