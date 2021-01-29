package com.ris.inventory.pos.service.report.impl;

import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.model.co.FilterCO;
import com.ris.inventory.pos.model.dto.*;
import com.ris.inventory.pos.model.response.SalespersonResponse;
import com.ris.inventory.pos.repository.TransactionRepository;
import com.ris.inventory.pos.service.DiscoveryService;
import com.ris.inventory.pos.service.report.SalespersonReportService;
import com.ris.inventory.pos.util.ObjectBinder;
import com.ris.inventory.pos.util.enumeration.ApplicationRole;
import com.ris.inventory.pos.util.enumeration.FilterType;
import com.ris.inventory.pos.util.exception.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//TODO fix naming convensions for all methods of this file
@Service
public class SalespersonReportServiceImpl implements SalespersonReportService {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectBinder binder;

    private AuditInterceptor initializeInterceptor(CurrentUser currentUser) {
        return new AuditInterceptor(currentUser.getUserId(), currentUser.getAuthority().toString(),
                (String) currentUser.getLocation().get("locationId"));
    }

    @Override
    public PaginationDTO<SalespersonResponse> report(int offset, int limit, CurrentUser currentUser) {
        List<SalespersonResponse> saleReportDTOList = new ArrayList<>();
        Pagination pagination = new Pagination();

        PaginationDTO<UserDTO> usersByRole = discoveryService.fetchUsersByRole(offset, limit, ApplicationRole.SALE, currentUser);

        List<UserDTO> userDTO = usersByRole.getList();
        if (currentUser.getAuthority().equals(ApplicationRole.ADMIN)) {
            userDTO.forEach(
                    it -> saleReportDTOList.add(generateSaleReport(it,
                            binder.bindTransaction(transactionRepository.findAllByUserId(pagination, it.getUserId()))))
            );
        } else if (currentUser.getAuthority().equals(ApplicationRole.MANAGER)) {
            userDTO.forEach(
                    it -> saleReportDTOList.add(generateSaleReport(it,
                            binder.bindTransaction(transactionRepository.findAllByUserIdAndLocation(pagination, it.getUserId(),
                                    (String) currentUser.getLocation().get("locationId")))))
            );
        } else if (currentUser.getAuthority().equals(ApplicationRole.SALE)) {
            throw new NotSupportedException("Sales person role is unauthorised user access to use this data report");
        }

        return new PaginationDTO<>(saleReportDTOList, usersByRole.getCount());
    }

    @Override
    public List<SalespersonResponse> report(CurrentUser currentUser) {
        throw new NotSupportedException("Not implemented yet");
    }

    @Override
    public List<SalespersonResponse> filter(List<FilterType> filterType, FilterCO value, CurrentUser currentUser) {
        throw new NotSupportedException("Not implemented yet");
    }

    @Override
    public DownloadDTO download(List keys) {
        return null;
    }

    @Override
    public PaginationDTO<SalespersonResponse> filter(List filterType, FilterCO value, int offset, int limit, CurrentUser currentUser) {
        List<SalespersonResponse> saleReportDTOList = new ArrayList<>();

        PaginationDTO<UserDTO> userPaginationDTO = new PaginationDTO<>();
        switch (currentUser.getAuthority()) {
            case ADMIN:
                if (filterType.size() == 1 && filterType.get(0).equals(FilterType.LOCATION) && value.isValidLocationFilter()) {
                    userPaginationDTO = discoveryService.fetchUsersByLocationAndRole(offset, limit, ApplicationRole.SALE, value.getLocationId(), currentUser);
                    saleReportDTOList.addAll(locationFilter(userPaginationDTO.getList(), value));

                } else if (filterType.size() == 1 && filterType.get(0).equals(FilterType.DATE) && value.isValidDateFilter()) {
                    userPaginationDTO = discoveryService.fetchUsersByDateAndRole(offset, limit, ApplicationRole.SALE, value.getStart(), value.getEnd(), currentUser);
                    saleReportDTOList.addAll(dateFilter(userPaginationDTO.getList(), value.getStart(), value.getEnd()));

                } else if (filterType.size() == 1 && filterType.get(0).equals(FilterType.NAME) && value.isValidNameFilter()) {
                    String[] name = {value.getFirstName(), value.getLastName()};
                    userPaginationDTO = discoveryService.fetchUserByNameAndRole(offset, limit, name, ApplicationRole.SALE, currentUser);
                    saleReportDTOList.addAll(nameFilter(userPaginationDTO.getList()));

                }
                break;

            case SALE:
                throw new NotSupportedException("Sales person role is unauthorised user access to use this data report");

            case MANAGER:
                throw new NotSupportedException("Manager role is unauthorised user access to use this data report");
        }
        return new PaginationDTO<>(saleReportDTOList, userPaginationDTO.getCount());
    }

    private SaleReportDTO generateSaleReport(UserDTO it, List<TransactionDTO> transactions) {
        SaleReportDTO saleReportDTO = new SaleReportDTO(it);
        saleReportDTO.setTransactions(transactions);
        saleReportDTO.setTotalSale(saleReportDTO.calculateSale());
        return saleReportDTO;
    }

    @Override
    public List<SalesPersonRevenueDTO> salesPersonRevenue(CurrentUser currentUser) {
        List<UserDTO> users = discoveryService.fetchUsersByRole(ApplicationRole.SALE, currentUser);

        List<SalesPersonRevenueDTO> salesPersonRevenues = new ArrayList<>();
        for (UserDTO user : users) {
            List<Transaction> transactionList = transactionRepository.findAllByUserId(new Pagination(), user.getUserId());
            String name = user.getLastName().isEmpty() ? user.getFirstName() : user.getFirstName() + " " + user.getLastName();
            salesPersonRevenues.add(new SalesPersonRevenueDTO(name, calculateRevenueOf(transactionList)));
        }

        return salesPersonRevenues;
    }

    private List<SalespersonResponse> nameFilter(List<UserDTO> userDTOList) {
        List<SalespersonResponse> saleReportDTOList = new ArrayList<>();
        userDTOList.forEach(
                it -> saleReportDTOList.add(generateSaleReport(it, binder.bindTransaction(transactionRepository.findAllByUserId(new Pagination(), it.getUserId()), it))));

        return saleReportDTOList;
    }

    private List<SalespersonResponse> dateFilter(List<UserDTO> userDTO, Date start, Date end) {
        List<SalespersonResponse> saleReportDTOList = new ArrayList<>();

        userDTO.forEach(
                it -> {
                    Date date = it.getDateCreated();
                    if (date != null) {
                        LocalDate sqlDate = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
                        LocalDate startDate = start.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
                        LocalDate endDate = end.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();

                        if (sqlDate.isAfter(startDate) && sqlDate.isBefore(endDate)) {
                            saleReportDTOList.add(generateSaleReport(it, binder.bindTransaction(transactionRepository.findAllByDateAndUserId(new Pagination(), start,
                                    end, it.getUserId()), it)));
                        }
                    }
                });

        return saleReportDTOList;
    }

    private List<SalespersonResponse> locationFilter(List<UserDTO> userDTO, FilterCO value) {
        List<SalespersonResponse> saleReportDTOList = new ArrayList<>();

        userDTO.forEach(
                it -> {
                    LocationDTO location = it.getLocation();

                    if (location != null && location.getLocationId().equals(value.getLocationId())) {
                        saleReportDTOList.add(generateSaleReport(it, binder.bindTransaction(transactionRepository.findAllByUserIdAndLocation(new Pagination(),
                                it.getUserId(), value.getLocationId()), it)));
                    }
                });

        return saleReportDTOList;
    }
}
