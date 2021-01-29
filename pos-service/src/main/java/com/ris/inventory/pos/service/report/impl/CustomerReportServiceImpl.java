package com.ris.inventory.pos.service.report.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.model.co.FilterCO;
import com.ris.inventory.pos.model.dto.CustomerReportDTO;
import com.ris.inventory.pos.model.dto.DownloadDTO;
import com.ris.inventory.pos.model.dto.OrderReportDTO;
import com.ris.inventory.pos.model.dto.PaginationDTO;
import com.ris.inventory.pos.model.response.CustomerResponse;
import com.ris.inventory.pos.repository.CustomerRepository;
import com.ris.inventory.pos.repository.OrderRepository;
import com.ris.inventory.pos.repository.TransactionRepository;
import com.ris.inventory.pos.service.report.CustomerReportService;
import com.ris.inventory.pos.util.ObjectBinder;
import com.ris.inventory.pos.util.enumeration.ApplicationRole;
import com.ris.inventory.pos.util.enumeration.FilterType;
import com.ris.inventory.pos.util.enumeration.Period;
import com.ris.inventory.pos.util.exception.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerReportServiceImpl implements CustomerReportService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectBinder binder;

    private AuditInterceptor initializeInterceptor(CurrentUser currentUser) {
        return new AuditInterceptor(currentUser.getUserId(), currentUser.getAuthority().toString(),
                (String) currentUser.getLocation().get("locationId"));
    }

    @Override
    public PaginationDTO<CustomerResponse> report(int offset, int limit, CurrentUser currentUser) {

        List<CustomerResponse> customerReportList = new ArrayList<>();

        AuditInterceptor auditInterceptor = initializeInterceptor(currentUser);

        Pagination pagination = new Pagination(limit, offset);

        ApplicationRole role = ApplicationRole.from(auditInterceptor.getAuthority());
        if (role.equals(ApplicationRole.SUPER_ADMIN) || role.equals(ApplicationRole.ADMIN) || role.equals(ApplicationRole.MANAGER) || role.equals(ApplicationRole.SALE)) {
            customerReportList.addAll(generateCustomerReport(customerRepository.list(pagination)));
        }

        return new PaginationDTO<>(customerReportList, pagination.getCount());
    }

    @Override
    public List<CustomerResponse> report(CurrentUser currentUser) {
        throw new NotSupportedException("Report not supported with this instance.");
    }

    @Override
    public PaginationDTO<CustomerResponse> filter(List filterType, FilterCO value, int offset, int limit, CurrentUser currentUser) {

        AuditInterceptor auditInterceptor = initializeInterceptor(currentUser);

        List<CustomerResponse> customerReportList = new ArrayList<>();

        Pagination pagination = new Pagination(limit, offset);

        if (filterType.contains(FilterType.DATE) && value.isValidDateFilter()) {
            customerReportList.addAll(dateFilter(auditInterceptor, pagination, value.getStart(), value.getEnd()));

        } else if (filterType.contains(FilterType.PERIOD) && value.isValidPeriodFilter()) {
            customerReportList.addAll(periodFilter(auditInterceptor, pagination, value.getPeriod()));

        } else if (filterType.contains(FilterType.LOCATION) && value.isValidLocationFilter()) {
            customerReportList.addAll(locationFilter(auditInterceptor, pagination, value.getLocationId()));

        }
        return new PaginationDTO<>(customerReportList, pagination.getCount());
    }

    @Override
    public List<CustomerResponse> filter(List<FilterType> filterType, FilterCO value, CurrentUser currentUser) {
        throw new NotSupportedException("Report not supported with this instance.");
    }

    @Override
    public DownloadDTO download(List<String> keys) throws JsonProcessingException {
        DownloadDTO downloadable = new DownloadDTO();

        List<Customer> customers = new ArrayList<>();
        keys.forEach(
                it -> customers.add(customerRepository.get(it))
        );

        List<CustomerReportDTO> customerReportDTOList = generateCustomerReport(customers);
        downloadable.setColumns(getColumns(customerReportDTOList));
        downloadable.setActualColumns(getActualColumns(customerReportDTOList));
        downloadable.setData(toJSON(customerReportDTOList));

        return downloadable;
    }

    private List<CustomerReportDTO> generateCustomerReport(List<Customer> customers) {
        List<CustomerReportDTO> customerReportList = new ArrayList<>();

        customerRepository.sort(customers, "id");

        customers.forEach(it -> {
            CustomerReportDTO customerReport = new CustomerReportDTO(it);
            List<Order> orders = orderRepository.findAllByCustomer(it);
            customerReport.setVisitCount(orders.size());
            List<OrderReportDTO> orderReportList = new ArrayList<>();
            orders.forEach(
                    order -> {
                        OrderReportDTO orderReport = binder.bindOrderReport(order);
                        List<Transaction> transactions = transactionRepository.findAllByOrder(order);
                        orderReport.setTransactions(binder.bindTransaction(transactions));
                        orderReportList.add(orderReport);
                    }
            );
            customerReport.setOrders(orderReportList);
            customerReportList.add(customerReport);
        });
        return customerReportList;
    }

    private List<CustomerReportDTO> dateFilter(AuditInterceptor interceptor, Pagination pagination, Date start, Date end) {
        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                return generateCustomerReport(customerRepository.findAllByDate(pagination, start, end));

            case MANAGER:
                return generateCustomerReport(customerRepository.findAllByDateAndLocation(pagination, start, end, interceptor.getLocation()));

            case SALE:
                return generateCustomerReport(customerRepository.findAllByDateAndUserId(pagination, start, end, interceptor.getUserId()));

            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }

    private List<CustomerReportDTO> periodFilter(AuditInterceptor interceptor, Pagination pagination, Period period) {
        Date filter = getPeriod(period);
        Date currentDate = new Date(Instant.now().toEpochMilli());

        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                if (period.equals(Period.TODAY))
                    return generateCustomerReport(customerRepository.findAllByDate(pagination, filter));
                else
                    return generateCustomerReport(customerRepository.findAllByDate(pagination, filter, currentDate));

            case MANAGER:
                if (period.equals(Period.TODAY))
                    return generateCustomerReport(customerRepository.findAllByDateAndLocation(pagination, filter, interceptor.getLocation()));
                else
                    return generateCustomerReport(customerRepository.findAllByDateAndLocation(pagination, filter, currentDate, interceptor.getLocation()));

            case SALE:
                if (period.equals(Period.TODAY))
                    return generateCustomerReport(customerRepository.findAllByDateAndUserId(pagination, filter, interceptor.getUserId()));
                else
                    return generateCustomerReport(customerRepository.findAllByDateAndUserId(pagination, filter, currentDate, interceptor.getUserId()));

            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }

    private List<CustomerReportDTO> locationFilter(AuditInterceptor interceptor, Pagination pagination, String location) {
        switch (ApplicationRole.from(interceptor.getAuthority())) {
            case ADMIN:
                return generateCustomerReport(customerRepository.findAllByLocation(pagination, location));

            case MANAGER:
                throw new NotSupportedException("Manager role is unauthorised user access to use this filter");

            case SALE:
                throw new NotSupportedException("Sales person role is unauthorised user access to use this filter");

            default:
                throw new NotSupportedException("Unauthorised authority " + interceptor.getAuthority());
        }
    }
}
