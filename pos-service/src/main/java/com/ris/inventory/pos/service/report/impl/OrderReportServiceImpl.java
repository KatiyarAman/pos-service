package com.ris.inventory.pos.service.report.impl;

import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.domain.Product;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.model.co.FilterCO;
import com.ris.inventory.pos.model.dto.*;
import com.ris.inventory.pos.model.response.OrderResponse;
import com.ris.inventory.pos.repository.OrderRepository;
import com.ris.inventory.pos.repository.TransactionRepository;
import com.ris.inventory.pos.service.DiscoveryService;
import com.ris.inventory.pos.service.report.OrderReportService;
import com.ris.inventory.pos.util.ObjectBinder;
import com.ris.inventory.pos.util.enumeration.ApplicationRole;
import com.ris.inventory.pos.util.enumeration.FilterType;
import com.ris.inventory.pos.util.exception.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderReportServiceImpl implements OrderReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DiscoveryService discoveryService;

    private boolean isPaymentWaitingRecordsRequired = true;

    @Autowired
    private ObjectBinder binder;

    private AuditInterceptor initializeInterceptor(CurrentUser currentUser) {
        return new AuditInterceptor(currentUser.getUserId(), currentUser.getAuthority().toString(),
                (String) currentUser.getLocation().get("locationId"));
    }

    @Override
    public PaginationDTO<OrderResponse> report(int offset, int limit, CurrentUser currentUser) {

        List<LocationDTO> locationDTOS = discoveryService.getActiveLocations();

        List<Order> orders = new ArrayList<>();

        Pagination pagination = new Pagination(limit, offset);

        if (currentUser.getAuthority().equals(ApplicationRole.ADMIN)) {
            orders = orderRepository.list(pagination);

        } else if (currentUser.getAuthority().equals(ApplicationRole.MANAGER)) {
            orders = orderRepository.list(pagination, (String) currentUser.getLocation().get("locationId"));

        } else if (currentUser.getAuthority().equals(ApplicationRole.SALE)) {
            orders = orderRepository.list(pagination, currentUser.getUserId());

        } else
            throw new NotSupportedException("Unauthorised authority " + currentUser.getAuthority());

        orderRepository.sort(orders, "id");

        return new PaginationDTO<>(new ArrayList<>(makeOrderReportResponse(orders, locationDTOS, currentUser)), pagination.getCount());
    }

    @Override
    public List<OrderResponse> report(CurrentUser currentUser) {
        throw new NotSupportedException("Report not supported with this instance.");
    }

    @Override
    public PaginationDTO<OrderResponse> filter(List<FilterType> filterType, FilterCO value, int offset, int limit, CurrentUser currentUser) {
        List<LocationDTO> locationDTOS = discoveryService.getActiveLocations();

        List<Order> orders = new ArrayList<>();

        Pagination pagination = new Pagination(limit, offset);

        if (filterType.contains(FilterType.DATE) && value.isValidDateFilter()) {
            orders = dateFilter(currentUser, pagination, value.getStart(), value.getEnd());

        } else
            throw new NotSupportedException("This filter type is not implemented yet");

        orderRepository.sort(orders, "id");

        return new PaginationDTO<>(new ArrayList<>(makeOrderReportResponse(orders, locationDTOS, currentUser)), pagination.getCount());
    }

    @Override
    public List<OrderResponse> filter(List<FilterType> filterType, FilterCO value, CurrentUser currentUser) {
        throw new NotSupportedException("Report not supported with this instance.");
    }

    @Override
    public DownloadDTO download(List<String> keys) {
        throw new NotSupportedException("Download Report not supported with this instance.");
    }

    private List<Order> dateFilter(CurrentUser currentUser, Pagination pagination, Date start, Date end) {
        switch (currentUser.getAuthority()) {
            case ADMIN:
                return orderRepository.findAllByDate(pagination, start, end);

            case MANAGER:
                return orderRepository.findAllByDateAndLocation(pagination, start, end, (String) currentUser.getLocation().get("locationId"));

            case SALE:
                return orderRepository.findAllByDateAndUserId(pagination, start, end, currentUser.getUserId());

            default:
                throw new NotSupportedException("Unauthorised authority " + currentUser.getAuthority());
        }
    }

    private List<OrderReportDTO> makeOrderReportResponse(List<Order> orders, List<LocationDTO> locationDTOS, CurrentUser currentUser) {

        List<OrderReportDTO> orderReportDTOList = new ArrayList<>();

        for (Order order : orders) {
            List<Transaction> transactions = transactionRepository.findAllByOrder(order);
            List<UserDTO> users = fetchUsers(transactions, currentUser);
            List<Product> products = orderRepository.getProductByOrder(order);

            orderReportDTOList.add(binder.bindOrderReport(order, users, locationDTOS, transactions, products, isPaymentWaitingRecordsRequired));
        }

        return orderReportDTOList;
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
}
