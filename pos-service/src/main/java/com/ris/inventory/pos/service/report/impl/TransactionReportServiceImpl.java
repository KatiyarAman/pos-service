package com.ris.inventory.pos.service.report.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.domain.Product;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.model.co.FilterCO;
import com.ris.inventory.pos.model.dto.*;
import com.ris.inventory.pos.model.response.TransactionResponse;
import com.ris.inventory.pos.repository.OrderRepository;
import com.ris.inventory.pos.repository.TransactionRepository;
import com.ris.inventory.pos.service.DiscoveryService;
import com.ris.inventory.pos.service.report.TransactionReportService;
import com.ris.inventory.pos.util.ObjectBinder;
import com.ris.inventory.pos.util.enumeration.ApplicationRole;
import com.ris.inventory.pos.util.enumeration.FilterType;
import com.ris.inventory.pos.util.enumeration.Period;
import com.ris.inventory.pos.util.enumeration.TransactionType;
import com.ris.inventory.pos.util.exception.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionReportServiceImpl implements TransactionReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiscoveryService discoveryService;

    private boolean isPaymentWaitingRecordsRequired = false;

    @Autowired
    private ObjectBinder binder;

    private AuditInterceptor initializeInterceptor(CurrentUser currentUser) {
        return new AuditInterceptor(currentUser.getUserId(), currentUser.getAuthority().toString(),
                (String) currentUser.getLocation().get("locationId"));
    }

    @Override
    public PaginationDTO<TransactionResponse> report(int offset, int limit, CurrentUser currentUser) {

        List<Transaction> transactions = new ArrayList<>();

        List<LocationDTO> locationDTOList = discoveryService.getActiveLocations();

        Pagination pagination = new Pagination(limit, offset);

        if (currentUser.getAuthority().equals(ApplicationRole.ADMIN)) {
            transactions = transactionRepository.list(pagination);

        } else if (currentUser.getAuthority().equals(ApplicationRole.MANAGER)) {
            transactions = transactionRepository.findAllByLocation(pagination, (String) currentUser.getLocation().get("locationId"));

        } else if (currentUser.getAuthority().equals(ApplicationRole.SALE)) {
            transactions = transactionRepository.findAllByUserId(pagination, currentUser.getUserId());

        } else
            throw new NotSupportedException("Unauthorised authority " + currentUser.getAuthority());

        transactionRepository.sort(transactions, "id");
        List<UserDTO> users = fetchUsers(transactions, currentUser);

        return new PaginationDTO<>(new ArrayList<>(binder.bindTransaction(transactions, locationDTOList, users, isPaymentWaitingRecordsRequired)), pagination.getCount());
    }

    @Override
    public List<TransactionResponse> report(CurrentUser currentUser) {
        throw new NotSupportedException("Report not supported with this instance.");
    }

    @Override
    public PaginationDTO<TransactionResponse> filter(List<FilterType> filterType, FilterCO value, int offset, int limit, CurrentUser currentUser) {
        List<Transaction> transactions = new ArrayList<>();

        List<LocationDTO> locationDTOList = discoveryService.getActiveLocations();

        AuditInterceptor auditInterceptor = initializeInterceptor(currentUser);

        Pagination pagination = new Pagination(limit, offset);

        if (filterType.contains(FilterType.TRANSACTION) && filterType.contains(FilterType.DATE) && value.isValidTransactionAndDateFilter()) {
            transactions = transactionAndDateFilter(auditInterceptor, pagination, value.getTransactionType(), value.getStart(), value.getEnd());

        } else if (filterType.contains(FilterType.TRANSACTION) && value.isValidTransactionFilter()) {
            transactions = transactionFilter(auditInterceptor, pagination, value.getTransactionType());

        } else if (filterType.contains(FilterType.DATE) && value.isValidDateFilter()) {
            transactions = dateFilter(auditInterceptor, pagination, value.getStart(), value.getEnd());

        } else if (filterType.contains(FilterType.PERIOD) && value.isValidPeriodFilter()) {
            transactions = periodFilter(auditInterceptor, pagination, value.getPeriod());

        } else if (filterType.contains(FilterType.LOCATION) && value.isValidLocationFilter()) {
            transactions = locationFilter(auditInterceptor, pagination, value.getLocationId());

        } else
            throw new NotSupportedException("This filter type is not implemented yet");


        transactionRepository.sort(transactions, "id");
        List<UserDTO> users = fetchUsers(transactions, currentUser);

        return new PaginationDTO<>(new ArrayList<>(binder.bindTransaction(transactions, locationDTOList, users, isPaymentWaitingRecordsRequired)), pagination.getCount());
    }

    @Override
    public List<TransactionResponse> filter(List<FilterType> filterType, FilterCO value, CurrentUser currentUser) {
        throw new NotSupportedException("Report not supported with this instance.");
    }

    @Override
    public DownloadDTO download(List<String> keys) throws JsonProcessingException {
        DownloadDTO downloadable = new DownloadDTO();
        List<Transaction> transactions = new ArrayList<>();
        keys.forEach(
                it -> transactions.add(transactionRepository.get(it))
        );

        List<TransactionDTO> transactionDTOList = binder.bindTransaction(transactions);
        downloadable.setColumns(getColumns(transactionDTOList));
        downloadable.setActualColumns(getActualColumns(transactionDTOList));
        downloadable.setData(toJSON(transactionDTOList));
        return downloadable;
    }

    private List<UserDTO> fetchUsers(List<Transaction> transactions, CurrentUser currentUser) {
        List<UserDTO> users = new ArrayList<>();
        if (!transactions.isEmpty()) {
            Set<String> userIds = new HashSet<>();

            transactions.forEach(
                    it -> userIds.add(it.getUserId())
            );
            users = discoveryService.fetchAllUserByUserIds(new ArrayList<>(userIds), currentUser);
        }
        return users;
    }

    private List<Transaction> transactionFilter(AuditInterceptor interceptor, Pagination pagination, TransactionType transactionType) {

        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                return transactionRepository.findAllByTransactionType(pagination, transactionType);

            case MANAGER:
                return transactionRepository.findAllByTransactionTypeAndLocation(pagination, transactionType, interceptor.getLocation());

            case SALE:
                return transactionRepository.findAllByTransactionTypeAndUserId(pagination, transactionType, interceptor.getUserId());

            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }

    private List<Transaction> transactionAndDateFilter(AuditInterceptor interceptor, Pagination pagination, TransactionType transactionType, Date start, Date end) {
        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                return transactionRepository.findAllByTransactionTypeAndDate(pagination, transactionType, start, end);

            case MANAGER:
                return transactionRepository.findAllByTransactionTypeAndDateAndLocation(pagination, transactionType, start, end, interceptor.getLocation());

            case SALE:
                return transactionRepository.findAllByTransactionTypeAndDateAndUserId(pagination, transactionType, start, end, interceptor.getUserId());

            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }

    private List<Transaction> dateFilter(AuditInterceptor interceptor, Pagination pagination, Date start, Date end) {
        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                return transactionRepository.findAllByDate(pagination, start, end);

            case MANAGER:
                return transactionRepository.findAllByDateAndLocation(pagination, start, end, interceptor.getLocation());

            case SALE:
                return transactionRepository.findAllByDateAndUserId(pagination, start, end, interceptor.getUserId());

            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }

    private List<Transaction> periodFilter(AuditInterceptor interceptor, Pagination pagination, Period period) {
        Date filter = getPeriod(period);
        Date currentDate = Date.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant());

        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                if (period.equals(Period.TODAY) || period.equals(Period.YESTERDAY))
                    return transactionRepository.findAllByDate(pagination, filter);
                else
                    return transactionRepository.findAllByDate(pagination, filter, currentDate);

            case MANAGER:
                if (period.equals(Period.TODAY) || period.equals(Period.YESTERDAY))
                    return transactionRepository.findAllByDate(pagination, filter);
                else
                    return transactionRepository.findAllByDateAndLocation(pagination, filter, currentDate, interceptor.getLocation());

            case SALE:
                if (period.equals(Period.TODAY) || period.equals(Period.YESTERDAY))
                    return transactionRepository.findAllByDate(pagination, filter);
                else
                    return transactionRepository.findAllByDateAndUserId(pagination, filter, currentDate, interceptor.getUserId());

            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }

    private List<Transaction> locationFilter(AuditInterceptor interceptor, Pagination pagination, String location) {
        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                return transactionRepository.findAllByLocation(pagination, location);
            case MANAGER:
                throw new NotSupportedException("Manager role is unauthorised user access to use this filter");
            case SALE:
                throw new NotSupportedException("Sales person role is unauthorised user access to use this filter");
            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }

    @Override
    public List<RevenueDTO> systemRevenue() {
        List<RevenueDTO> revenues = new ArrayList<>();
        Date lastWeek = getPeriod(Period.WEEKLY);

        List<Transaction> transactions = transactionRepository.findAllByDate(new Pagination(), lastWeek, new Date());

        List<List<Transaction>> dayWiseTransactions = sortTransactionsDayWise(transactions, lastWeek);

        for (List<Transaction> transactionList : dayWiseTransactions)
            revenues.add(new RevenueDTO(transactionList.get(0).getDateCreated().toString(), calculateRevenueOf(transactionList)));

        return revenues;
    }

    private List<List<Transaction>> sortTransactionsDayWise(List<Transaction> transactions, Date lastDate) {
        List<List<Transaction>> dayWiseTransactions = new ArrayList<>();

        List<Date> dates = getDatesTill(lastDate.toInstant().atZone(ZoneOffset.UTC).toLocalDate());
        for (Date date : dates) {
            List<Transaction> transactionList = transactions.stream().filter(it -> it.getDateCreated().equals(date)).collect(Collectors.toList());
            if (!transactionList.isEmpty())
                dayWiseTransactions.add(transactionList);
        }

        return dayWiseTransactions;
    }

    @Override
    public List<ProductDTO> fetchTransactionsProducts() {
        Date lastWeek = getPeriod(Period.WEEKLY);

        List<Transaction> transactions = transactionRepository.findAllByDate(new Pagination(), lastWeek, new Date());

        List<Product> products = new ArrayList<>();
        transactions.forEach(
                it -> products.addAll(orderRepository.getProductByOrder(it.getOrder()))
        );

        return binder.bindTransactionProducts(products);
    }
}
