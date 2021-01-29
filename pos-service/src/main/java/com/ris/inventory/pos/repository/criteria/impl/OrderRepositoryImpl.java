package com.ris.inventory.pos.repository.criteria.impl;

import com.google.gson.Gson;
import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.domain.Delivery;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.domain.Product;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.repository.DeliveryRepository;
import com.ris.inventory.pos.repository.OrderRepository;
import com.ris.inventory.pos.util.enumeration.OrderStatus;
import com.ris.inventory.pos.util.enumeration.ProductStatus;
import com.ris.inventory.pos.util.exception.CancellationException;
import com.ris.inventory.pos.util.exception.EntityNotPersistException;
import com.ris.inventory.pos.util.exception.NotSupportedException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class OrderRepositoryImpl extends GenericRepositoryImpl<Order> implements OrderRepository, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);

    @Autowired
    private DeliveryRepository deliveryRepository;

    /* Placing new order for a particular customer and selected products */
    @Override
    public Order initiateOrder(List<Product> products, Customer customer, Interceptor interceptor) {
        logger.info("placing new order for customer {} products {}", customer, products.size());

        Order persistedOrder = null;
        Transaction transaction = null;

        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            Order order = new Order(interceptor);
            order.setCustomer((Customer) session.merge(customer));
            for (Product product : products)
                product.setOrder(order);
            order.setProducts(products);
            session.save(order);
            transaction.commit();
            logger.info("New order is initiated successfully");
            persistedOrder = order;
        } catch (Exception exp) {
            exp.printStackTrace();
            super.rollBackTransaction(transaction);
            logger.error("Exception while placing new  order");
        }

        return persistedOrder;
    }

    @Override
    public Order initiateOrder(List<Product> products, Customer customer, Delivery delivery, Interceptor interceptor) {
        logger.info("placing new order for customer {} products {} with delivery {}", customer, products.size(), delivery);

        Order order = initiateOrder(products, customer, interceptor);

        if (order != null) {
            String deliveryId = deliveryRepository.updateDelivery(order, delivery);
            if (deliveryId == null) {
                logger.error("delivery is 'null' when entity is in process for updating delivery with order.");
                throw new EntityNotPersistException("Error Occurred, During updating delivery with order.");
            }
        }
        return order;
    }

    @Override
    public boolean updateDelivery(Order order, Delivery delivery, Interceptor interceptor) {
        logger.info("updating delivery {} for existing order {}", delivery, order);
        boolean status = false;

        if (order != null) {
            String deliveryId = deliveryRepository.updateDelivery(order, delivery);
            if (deliveryId == null) {
                logger.error("delivery is 'null' when entity is in process for updating delivery with order.");
                throw new EntityNotPersistException("Error Occurred, During updating delivery with order.");
            }
            status = true;
        }
        return status;
    }

    /* Changing order product quantity */
    @Override
    public void changeQuantity(List<Product> products, Interceptor interceptor) {
        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            products.forEach(
                    it -> {
                        if (it.getOrderQuantity() == 0) {
                            it.setProductStatus(ProductStatus.CANCELLED);
                        }
                        session.saveOrUpdate(it);
                    }
            );
            transaction.commit();
        } catch (Exception exp) {
            exp.printStackTrace();
            logger.error("Exception while updating ordered product quantity");
            rollBackTransaction(transaction);
        }
    }

    /* cancel order and their products */
    @Override
    public void cancelOrder(Order order, Interceptor interceptor) {
        logger.info("Updating order status cancelled for {}", order.getOrderId());

        if (!order.getOrderStatus().equals(OrderStatus.INITIALIZED)) {
            logger.error("Exception while updating final order status");
            throw new CancellationException("Order can not be cancelled. Please contact store manager for Refund or Exchange policy");
        }
        List<Product> products = getProductByOrder(order);

        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            products.forEach(
                    it -> {
                        it.setProductStatus(ProductStatus.CANCELLED);
                        session.saveOrUpdate(it);
                    }
            );
            transaction.commit();
        } catch (Exception exp) {
            exp.printStackTrace();
            logger.error("Exception while updating products status for cancellation");
            rollBackTransaction(transaction);
        }
        updateOrder(order, OrderStatus.CANCELLED, interceptor);
    }

    /* update complete order status */
    @Override
    public void updateOrder(Order order, OrderStatus status, Interceptor interceptor) {
        logger.info("Updating final order {} status {}", order.getOrderId(), status);

        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            Order mergedOrder = (Order) session.merge(order);
            mergedOrder.setOrderStatus(status);
            session.saveOrUpdate(mergedOrder);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while updating final order status");
            rollBackTransaction(transaction);
        }
    }

    /* Update products info */
    @Override
    public boolean updateProducts(List<Product> products, Interceptor interceptor) {
        Transaction transaction = null;
        boolean status = false;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();

            products.forEach(
                    it -> {
                        Product mergedProduct = (Product) session.merge(it);
                        session.saveOrUpdate(mergedProduct);
                    }
            );
            transaction.commit();
            status = true;
        } catch (Exception exp) {
            exp.printStackTrace();
            logger.error("Exception while updating refund products for order");
            rollBackTransaction(transaction);
        }
        return status;
    }

    /* Getting last placed order customer */
    @Override
    public Order findByCustomer(Customer customer) {
        logger.info("finding last placed order by customer {}", customer);
        throw new NotSupportedException("Need to provide impl for this.");
    }

    /* Getting all placed  orders by customer */
    @Override
    public List<Order> findAllByCustomer(Customer customer) {
        logger.info("finding all placed orders by customer {}", customer);

        List<Order> orders = new ArrayList<>();
        try (Session session = getCurrentSession()) {
            orders = session.createQuery(getCriteriaQuery(session, "customer", customer)).getResultList();
        } catch (Exception exp) {
            exp.printStackTrace();
            logger.error("Exception while getting placed orders");
        }
        return orders;
    }

    @Override
    public Integer getTotalRefundedQuantity(Order order, String productId) {
        int totalRefunded = -1;
        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Integer> criteriaQuery = builder.createQuery(Integer.class);
            Root<Product> productRoot = criteriaQuery.from(Product.class);
            criteriaQuery.select(productRoot.get("refundQuantity")).where(builder.and(builder.equal(productRoot.get("order"), order)
                    , builder.isFalse(productRoot.get("isDeleted")), builder.equal(productRoot.get("productId"), productId)));
            List<Integer> refunded = session.createQuery(criteriaQuery).getResultList();
            totalRefunded = sum(refunded);
        } catch (Exception exp) {
            logger.error("Exception Occurred : while calculating total refunded quantity");
        }
        return totalRefunded;
    }

    @Override
    public Integer getTotalExchangedQuantity(Order order, String productId) {
        int totalExchange = -1;
        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Integer> criteriaQuery = builder.createQuery(Integer.class);
            Root<Product> productRoot = criteriaQuery.from(Product.class);
            criteriaQuery.select(productRoot.get("exchangeQuantity")).where(builder.and(builder.equal(productRoot.get("order"), order)
                    , builder.isFalse(productRoot.get("isDeleted")), builder.equal(productRoot.get("productId"), productId)));
            List<Integer> exchanged = session.createQuery(criteriaQuery).getResultList();
            totalExchange = sum(exchanged);
        } catch (Exception exp) {
            logger.error("Exception Occurred : while calculating total refunded quantity");
        }
        return totalExchange;
    }

    private int sum(List<Integer> integers) {
        int sum = 0;
        for (Integer integer : integers) {
            sum = sum + integer;
        }
        return sum;
    }

    /* Get order by transaction */
    @Override
    public Order findByTransaction(com.ris.inventory.pos.domain.Transaction transaction) {
        logger.info("Getting order by transaction {}", transaction);
        Order order = null;
        try (Session session = getCurrentSession()) {
            com.ris.inventory.pos.domain.Transaction mergedTransaction = (com.ris.inventory.pos.domain.Transaction) session.merge(transaction);
            order = mergedTransaction.getOrder();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting order by transaction");
        }
        return order;
    }

    @Override
    public List<Product> getProductByOrder(Order order) {
        logger.info("finding all placed order's products by order {}", order);

        List<Product> products = new ArrayList<>();
        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> productRoot = criteria.from(Product.class);
            criteria.select(productRoot).where(builder.and(builder.isFalse(productRoot.get("isDeleted")),
                    builder.equal(productRoot.get("order"), order), builder.notEqual(productRoot.get("productStatus"), ProductStatus.CANCELLED)));

            products = session.createQuery(criteria).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting placed order's products list");
        }
        return products;
    }

    /* Getting order by orderId */
    @Override
    public Order get(String orderId) {
        return super.findByColumn("orderId", orderId);
    }

    @Override
    public List<Order> list(Pagination pagination) {
        List<Order> orders = new ArrayList<>();

        try (Session session = getCurrentSession()) {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {
                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Order> countRoot = countCriteria.from(Order.class);

                countCriteria.select(builder.count(countRoot.get("id"))).where(builder.and(builder.isFalse(countRoot.get("isDeleted")),
                        builder.notEqual(countRoot.get("orderStatus"), OrderStatus.INITIALIZED)));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.notEqual(root.get("orderStatus"), OrderStatus.INITIALIZED)));

            if (pagination.isIgnorable())
                orders = session.createQuery(criteria).getResultList();
            else
                orders = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();

        } catch (Exception ex) {
            logger.error("Exception while getting order list.");
            ex.printStackTrace();
        }

        return orders;
    }

    @Override
    public List<Order> list(Pagination pagination, String userIdORLocation) {
        List<Order> orders = new ArrayList<>();

        try (Session session = getCurrentSession()) {

            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {

                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Order> countRoot = countCriteria.from(Order.class);

                countCriteria.select(builder.count(countRoot.get("id"))).where(builder.and(builder.isFalse(countRoot.get("isDeleted")),
                        builder.notEqual(countRoot.get("orderStatus"), OrderStatus.INITIALIZED),
                        builder.or(builder.equal(countRoot.get("location"), userIdORLocation), builder.equal(countRoot.get("userId"), userIdORLocation))));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.notEqual(root.get("orderStatus"), OrderStatus.INITIALIZED),
                    builder.or(builder.equal(root.get("location"), userIdORLocation), builder.equal(root.get("userId"), userIdORLocation))));

            if (pagination.isIgnorable())
                orders = session.createQuery(criteria).getResultList();
            else
                orders = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();

        } catch (Exception ex) {
            logger.error("Exception while getting order list.");
            ex.printStackTrace();
        }

        return orders;
    }

    @Override
    public List<Order> findAllByDate(Pagination pagination, Date start, Date end) {
        logger.info("Getting all orders by pagination {} and Start date {} & End date {}", new Gson().toJson(pagination), start, end);

        List<Order> orders = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {
                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Order> orderRoot = countCriteria.from(Order.class);
                countCriteria.select(builder.count(orderRoot));

                countCriteria.where(builder.and(builder.isFalse(orderRoot.get("isDeleted")), builder.notEqual(orderRoot.get("orderStatus"), OrderStatus.INITIALIZED),
                        builder.between(orderRoot.get("dateCreated"), start, end)));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.notEqual(root.get("orderStatus"), OrderStatus.INITIALIZED),
                    builder.between(root.get("dateCreated"), start, end)));

            if (pagination.isIgnorable())
                orders = session.createQuery(criteria).getResultList();
            else
                orders = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all orders by Start & End date");
        }
        return orders;
    }

    @Override
    public List<Order> findAllByDateAndLocation(Pagination pagination, Date start, Date end, String location) {
        logger.info("Getting all orders by Location :{} and Date Start :{} & End :{}", location, start, end);

        List<Order> orders = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {
                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Order> orderRoot = countCriteria.from(Order.class);
                countCriteria.select(builder.count(orderRoot));

                countCriteria.where(builder.and(builder.isFalse(orderRoot.get("isDeleted")), builder.notEqual(orderRoot.get("orderStatus"), OrderStatus.INITIALIZED),
                        builder.between(orderRoot.get("dateCreated"), start, end), builder.equal(orderRoot.get("location"), location)));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.notEqual(root.get("orderStatus"), OrderStatus.INITIALIZED),
                    builder.between(root.get("dateCreated"), start, end), builder.equal(root.get("location"), location)));

            if (!pagination.isIgnorable())
                orders = session.createQuery(criteria).getResultList();
            else
                orders = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all orders by Location and Start & End date");
        }
        return orders;
    }

    @Override
    public List<Order> findAllByDateAndUserId(Pagination pagination, Date start, Date end, String userId) {
        logger.info("Getting all orders by UserId :{} and Date Start :{}, & End :{}", userId, start, end);

        List<Order> orders = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {
                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Order> orderRoot = countCriteria.from(Order.class);
                countCriteria.select(builder.count(orderRoot));

                countCriteria.where(builder.and(builder.isFalse(orderRoot.get("isDeleted")), builder.notEqual(orderRoot.get("orderStatus"), OrderStatus.INITIALIZED),
                        builder.between(orderRoot.get("dateCreated"), start, end), builder.equal(orderRoot.get("userId"), userId)));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), builder.notEqual(root.get("orderStatus"), OrderStatus.INITIALIZED),
                    builder.between(root.get("dateCreated"), start, end), builder.equal(root.get("userId"), userId)));

            if (!pagination.isIgnorable())
                orders = session.createQuery(criteria).getResultList();
            else
                orders = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all orders by userId and Start & End date");
        }
        return orders;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setEntity(Order.class);
    }
}
