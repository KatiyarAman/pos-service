package com.ris.inventory.pos.repository.criteria.impl;

import com.google.gson.Gson;
import com.ris.inventory.pos.domain.Address;
import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.model.co.CustomerCO;
import com.ris.inventory.pos.repository.criteria.CustomerCriteriaRepository;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CustomerRepositoryImpl extends GenericRepositoryImpl<Customer> implements CustomerCriteriaRepository, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(CustomerRepositoryImpl.class);

    @Override
    public Customer findByMobile(String mobile) {
        return super.findByColumn("mobile", mobile);
    }

    @Override
    public Customer save(CustomerCO customer, Interceptor interceptor) {
        AuditInterceptor auditInterceptor = (AuditInterceptor) interceptor;
        Customer customerEntity = new Customer(customer.getFirstName(), customer.getLastName(),
                customer.getMobile(), customer.getEmail(), customer.getCustomerType(), auditInterceptor.getUserId(), auditInterceptor.getLocation());
        customerEntity.setAddress(new Address(customer.getAddress()));
        return super.save(customerEntity, auditInterceptor);
    }

    @Override
    public Customer findByOrder(Order order) {
        Customer customer = null;
        try (Session session = super.getCurrentSession()) {
            Order mergedOrder = (Order) session.merge(order);
            customer = mergedOrder.getCustomer();
        } catch (Exception exp) {
            exp.printStackTrace();
            logger.error("Exception Occurred : While getting customer by order");
        }
        return customer;
    }

    @Override
    public Customer get(String customerId) {
        return super.findByColumn("customerId", customerId);
    }

    @Override
    public List<Customer> list(Pagination pagination) {
        logger.info("Getting all customers {}", new Gson().toJson(pagination));

        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot));

            countCriteria.where(builder.isFalse(customerRoot.get("isDeleted")));
            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.isFalse(root.get("isDeleted")));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by pagination");
        }
        return customers;
    }

    @Override
    public List<Customer> findAllByLocation(Pagination pagination, String location) {
        logger.info("Getting all customers by location: {}", location);

        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot));

            countCriteria.where(builder.and(builder.isFalse(customerRoot.get("isDeleted")), builder.equal(customerRoot.get("location"), location)));
            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.equal(root.get("location"), location)));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by location.");
        }
        return customers;
    }

    @Override
    public List<Customer> findAllByDate(Pagination pagination, Date start, Date end) {
        logger.info("Getting all customers by start: {} & end: {}", start, end);

        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot));

            countCriteria.where(builder.and(builder.isFalse(customerRoot.get("isDeleted")), builder.between(customerRoot.get("dateCreated"), start, end)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.between(root.get("dateCreated"), start, end)));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by date.");
        }
        return customers;
    }

    @Override
    public List<Customer> findAllByDate(Pagination pagination, Date date) {
        logger.info("Getting all customers by date : {} ", date);

        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot));

            countCriteria.where(builder.and(builder.isFalse(customerRoot.get("isDeleted")), builder.equal(customerRoot.get("dateCreated"), date)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.equal(root.get("dateCreated"), date)));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by date.");
        }
        return customers;
    }

    @Override
    public List<Customer> findAllByDateAndLocation(Pagination pagination, Date date, String location) {
        logger.info("Getting all customers by date : {} & location {}", date, location);

        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot)).where(builder.and(builder.isFalse(customerRoot.get("isDeleted")),
                    builder.equal(customerRoot.get("dateCreated"), date), builder.equal(customerRoot.get("location"), location)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.equal(root.get("dateCreated"), date),
                    builder.equal(root.get("location"), location)));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by date and location.");
        }
        return customers;
    }

    @Override
    public List<Customer> findAllByDateAndLocation(Pagination pagination, Date start, Date end, String location) {
        logger.info("Getting all customer by start : {} & End : {} and  location {}", start, end, location);

        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot));

            countCriteria.where(builder.and(builder.isFalse(customerRoot.get("isDeleted")), builder.between(customerRoot.get("dateCreated"), start, end),
                    builder.equal(customerRoot.get("location"), location)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.between(root.get("dateCreated"), start, end),
                    builder.equal(root.get("location"), location)));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by date and location.");
        }
        return customers;
    }

    @Override
    public List<Customer> findAllByDateAndUserId(Pagination pagination, Date date, String userId) {
        logger.info("Getting all customer by date: {} and  userId {}", date, userId);


        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot)).where(builder.and(builder.isFalse(customerRoot.get("isDeleted")),
                    builder.equal(customerRoot.get("dateCreated"), date), builder.equal(customerRoot.get("userId"), userId)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.and(builder.isFalse(customerRoot.get("isDeleted")), builder.equal(customerRoot.get("dateCreated"), date),
                    builder.equal(customerRoot.get("userId"), userId)));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by date and userId.");
        }
        return customers;
    }

    @Override
    public List<Customer> findAllByDateAndUserId(Pagination pagination, Date start, Date end, String userId) {
        logger.info("Getting all customer by start : {} & End : {} and  userId {}", start, end, userId);

        List<Customer> customers = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Customer> customerRoot = countCriteria.from(Customer.class);
            countCriteria.select(builder.count(customerRoot));

            countCriteria.where(builder.and(builder.isFalse(customerRoot.get("isDeleted")), builder.between(customerRoot.get("dateCreated"), start, end),
                    builder.equal(customerRoot.get("userId"), userId)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
            Root<Customer> root = criteria.from(Customer.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.between(root.get("dateCreated"), start, end),
                    builder.equal(root.get("userId"), userId)));

            customers = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all customers by date and userId.");
        }
        return customers;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setEntity(Customer.class);
    }
}
