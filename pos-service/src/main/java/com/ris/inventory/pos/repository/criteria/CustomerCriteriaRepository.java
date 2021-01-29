package com.ris.inventory.pos.repository.criteria;

import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.model.co.CustomerCO;
import org.hibernate.Interceptor;

import java.util.Date;
import java.util.List;

public interface CustomerCriteriaRepository {

    public Customer findByMobile(String mobile);

    public List<Customer> findAllByColumn(Pagination pagination, String column, Object value);

    public Customer save(CustomerCO customer, Interceptor interceptor);

    public Customer get(Long id);

    public Customer findByOrder(Order order);

    public Customer get(String customerId);

    public List<Customer> list();

    public void sort(List<Customer> customers, String column);

    public List<Customer> list(Pagination pagination);

    public List<Customer> findAllByLocation(Pagination pagination, String location);

    public List<Customer> findAllByDate(Pagination pagination, Date start, Date end);

    public List<Customer> findAllByDate(Pagination pagination, Date date);

    public List<Customer> findAllByDateAndLocation(Pagination pagination, Date date, String location);

    public List<Customer> findAllByDateAndLocation(Pagination pagination, Date start, Date end, String location);

    public List<Customer> findAllByDateAndUserId(Pagination pagination, Date date, String userId);

    public List<Customer> findAllByDateAndUserId(Pagination pagination, Date start, Date end, String userId);
}
