package com.ris.inventory.pos.repository.criteria.impl;

import com.google.gson.Gson;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.repository.TransactionRepository;
import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import com.ris.inventory.pos.util.enumeration.TransactionStatus;
import com.ris.inventory.pos.util.enumeration.TransactionType;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class TransactionRepositoryImpl extends GenericRepositoryImpl<Transaction> implements TransactionRepository, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);

    @Override
    public String save(float totalDiscount, float totalTaxes, float totalProductCost, float payableAmount, float deliveryAmount, Order initiatedOrder,
                       boolean isExchange, Interceptor interceptor) {
        logger.info("Persisting a new order transaction 'order : {} ' 'payable amount is : {}'", initiatedOrder, payableAmount);

        Transaction transaction = new Transaction(totalProductCost, totalDiscount, totalTaxes,
                payableAmount, deliveryAmount, TransactionType.SALE, isExchange, PaymentMethod.WAITING, interceptor);
        transaction.setOrder(initiatedOrder);
        super.save(transaction, interceptor);
        return transaction.getTransactionId();
    }

    @Override
    public String save(float totalDiscount, float totalTaxes, float totalProductCost, float payableAmount, float deliveryAmount, Order order,
                       TransactionType type, boolean isExchange, Interceptor interceptor) {
        logger.info("Persisting a refund/sale transaction for exchange 'order : {} 'payable amount is : {}'", order, payableAmount);

        Transaction transaction = new Transaction(totalProductCost, totalDiscount, totalTaxes,
                payableAmount, deliveryAmount, type, isExchange, PaymentMethod.WAITING, interceptor);
        transaction.setOrder(order);
        super.save(transaction, interceptor);
        return transaction.getTransactionId();
    }

    @Override
    public String save(float totalProductCost, float payableAmount, Order order, boolean isExchange, Interceptor interceptor) {
        logger.info("Persisting a refund transaction 'order : {} ' 'payable amount is : {}'", order, payableAmount);

        Transaction transaction = new Transaction(totalProductCost, payableAmount, TransactionType.REFUND, isExchange, PaymentMethod.WAITING, interceptor);
        transaction.setOrder(order);
        super.save(transaction, interceptor);
        return transaction.getTransactionId();
    }

    @Override
    public String update(Order paymentWaitingOrder, TransactionType type) {
        logger.info("Updating last transaction for order changed status{}", paymentWaitingOrder);

        org.hibernate.Transaction hibernateTransaction = null;
        String transactionId = null;

        Transaction transaction = findByOrder(paymentWaitingOrder, type);
        if (transaction != null) {
            try (Session session = getCurrentSession()) {
                hibernateTransaction = session.beginTransaction();
                Transaction lastWaitingTransaction = (Transaction) session.merge(transaction);
                lastWaitingTransaction.setStatus(TransactionStatus.ORDER_CHANGED);
                lastWaitingTransaction.setMethod(PaymentMethod.ORDER_CHANGED);
                transactionId = lastWaitingTransaction.getTransactionId();
                hibernateTransaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Exception Occurred : while updating last transaction");
                rollBackTransaction(hibernateTransaction);
            }
        }

        return transactionId;
    }

    @Override
    public String updatePayment(Transaction transaction, PaymentMethod method, TransactionStatus status) {
        logger.info("Updating order payment transaction {}", transaction);

        String transactionId = null;

        if (transaction.getStatus().equals(TransactionStatus.INITIATED) && transaction.getMethod().equals(PaymentMethod.WAITING)) {
            org.hibernate.Transaction hibernateTransaction = null;
            try (Session session = getCurrentSession()) {
                hibernateTransaction = session.beginTransaction();
                Transaction mergedTransaction = (Transaction) session.merge(transaction);
                mergedTransaction.setMethod(method);
                mergedTransaction.setStatus(status);
                session.saveOrUpdate(mergedTransaction);
                hibernateTransaction.commit();
                transactionId = transaction.getTransactionId();
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Exception while updating final order's transaction payment method status");
                rollBackTransaction(hibernateTransaction);
            }
        }

        return transactionId;
    }

    @Override
    public Transaction findByOrder(Order order, TransactionType type) {
        logger.info("Getting order's last on going transaction {}", order);

        Transaction transaction = null;

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> transactionRoot = criteria.from(Transaction.class);
            criteria.select(transactionRoot).where(builder.and(builder.equal(transactionRoot.get("order"), order),
                    builder.equal(transactionRoot.get("type"), type), builder.equal(transactionRoot.get("method"),
                            PaymentMethod.WAITING)), builder.isFalse(transactionRoot.get("isDeleted")));

            transaction = session.createQuery(criteria).getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting final order's last on going transaction");
        }

        return transaction;
    }

    @Override
    public Transaction findByOrder(Order order) {
        logger.info("Getting order's last on going transaction which required payment {}", order);

        Transaction transaction = null;

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> transactionRoot = criteria.from(Transaction.class);
            criteria.select(transactionRoot).where(builder.and(builder.equal(transactionRoot.get("order"), order),
                    builder.equal(transactionRoot.get("method"), PaymentMethod.WAITING)),
                    builder.isFalse(transactionRoot.get("isDeleted")));

            transaction = session.createQuery(criteria).getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting final order's last on going transaction which required payment");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByOrder(Order order) {
        logger.info("Getting all transactions by order {}", order);

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> transactionRoot = criteria.from(Transaction.class);
            criteria.select(transactionRoot).where(builder.and(builder.equal(transactionRoot.get("order"), order),
                    builder.isFalse(transactionRoot.get("isDeleted"))));

            transaction = session.createQuery(criteria).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public Transaction get(String transactionId) {
        return super.findByColumn("transactionId", transactionId);
    }

    @Override
    public List<Transaction> list(Pagination pagination) {
        logger.info("Getting all transactions");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> root = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(root));
            countCriteria.where(builder.and(builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"),
                    PaymentMethod.ORDER_CHANGED), builder.isFalse(root.get("isDeleted"))));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> transactionRoot = criteria.from(Transaction.class);
            criteria.select(transactionRoot)
                    .where(builder.and(builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.isFalse(root.get("isDeleted"))));
            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByLocation(Pagination pagination, String location) {
        logger.info("Getting all transactions by order {}");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            countCriteria.where(builder.and(builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"),
                    PaymentMethod.ORDER_CHANGED), builder.isFalse(transactionRoot.get("isDeleted")), builder.equal(transactionRoot.get("location"), location)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);
            criteria.select(root).where(builder.and(builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.isFalse(root.get("isDeleted")), builder.equal(root.get("location"), location)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByUserId(Pagination pagination, String userId) {
        logger.info("Getting all transactions by order {}");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {
                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
                countCriteria.select(builder.count(transactionRoot));

                countCriteria.where(builder.and(builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"),
                        PaymentMethod.ORDER_CHANGED), builder.isFalse(transactionRoot.get("isDeleted")), builder.equal(transactionRoot.get("userId"), userId)));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);
            criteria.select(root).where(builder.and(builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.isFalse(root.get("isDeleted")), builder.equal(root.get("userId"), userId)));

            if (pagination.isIgnorable())
                transaction = session.createQuery(criteria).getResultList();
            else
                transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByUserIdAndLocation(Pagination pagination, String userId, String location) {
        logger.info("Getting all transactions by order {}");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {
                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
                countCriteria.select(builder.count(transactionRoot));

                countCriteria.where(builder.and(builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"),
                        PaymentMethod.ORDER_CHANGED), builder.isFalse(transactionRoot.get("isDeleted")), builder.equal(transactionRoot.get("userId"), userId),
                        builder.equal(transactionRoot.get("location"), location)));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);
            criteria.select(root).where(builder.and(builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.isFalse(root.get("isDeleted")), builder.equal(root.get("userId"), userId), builder.equal(root.get("location"), location)));

            if (pagination.isIgnorable())
                transaction = session.createQuery(criteria).getResultList();
            else
                transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByTransactionType(Pagination pagination, TransactionType transactionType) {
        logger.info("Getting all transactions by order {}");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            Predicate predicate = getTransactionPredicate(builder, transactionType, transactionRoot);

            countCriteria.where(builder.and(builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"),
                    PaymentMethod.ORDER_CHANGED), builder.isFalse(transactionRoot.get("isDeleted")), predicate));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);
            criteria.select(root).where(builder.and(builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.isFalse(root.get("isDeleted")), predicate));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    private Predicate getTransactionPredicate(CriteriaBuilder builder, TransactionType transactionType, Root<Transaction> transactionRoot) {
        boolean isExchange = transactionType.equals(TransactionType.EXCHANGE);
        Predicate predicate;
        if (isExchange)
            predicate = builder.isTrue(transactionRoot.get("isExchange"));
        else
            predicate = builder.equal(transactionRoot.get("type"), transactionType);

        return predicate;
    }

    @Override
    public List<Transaction> findAllByDate(Pagination pagination, Date start, Date end) {
        logger.info("Getting all transactions by pagination {} and Start date {} and End date {}", new Gson().toJson(pagination), start, end);

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            if (!pagination.isIgnorable()) {
                CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
                Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
                countCriteria.select(builder.count(transactionRoot));

                countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")),
                        builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                        builder.between(transactionRoot.get("dateCreated"), start, end)));

                pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());
            }

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")),
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(root.get("dateCreated"), start, end)));

            if (pagination.isIgnorable())
                transaction = session.createQuery(criteria).getResultList();
            else
                transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByDate(Pagination pagination, Date date) {
        logger.info("Getting all transactions by pagination {} and date {}", new Gson().toJson(pagination), date);

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")),
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.equal(transactionRoot.get("dateCreated"), date)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")),
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.equal(root.get("dateCreated"), date)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByDateAndLocation(Pagination pagination, Date start, Date end, String location) {
        logger.info("Getting all transactions by Location :{} and Date Start :{}, End :{}", location, start, end);

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")),
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(transactionRoot.get("dateCreated"), start, end), builder.equal(transactionRoot.get("location"), location)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")),
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(root.get("dateCreated"), start, end), builder.equal(root.get("location"), location)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByDateAndUserId(Pagination pagination, Date start, Date end, String userId) {
        logger.info("Getting all transactions by UserId :{} and Date Start :{}, End :{}", userId, start, end);

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")),
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(transactionRoot.get("dateCreated"), start, end), builder.equal(transactionRoot.get("userId"), userId)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")),
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(root.get("dateCreated"), start, end), builder.equal(root.get("userId"), userId)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByTransactionTypeAndDate(Pagination pagination, TransactionType transactionType, Date start, Date end) {
        logger.info("Getting all transactions by Transaction Type :{} and Date Start :{}, End :{}", transactionType, start, end);

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            Predicate predicate = getTransactionPredicate(builder, transactionType, transactionRoot);

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")), predicate,
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(transactionRoot.get("dateCreated"), start, end)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), predicate,
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(root.get("dateCreated"), start, end)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByTransactionTypeAndLocation(Pagination pagination, TransactionType transactionType, String location) {
        logger.info("Getting all transactions by order {}");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            Predicate predicate = getTransactionPredicate(builder, transactionType, transactionRoot);

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")), predicate,
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.equal(transactionRoot.get("location"), location)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);
            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), predicate,
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.equal(root.get("location"), location)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByTransactionTypeAndUserId(Pagination pagination, TransactionType transactionType, String userId) {
        logger.info("Getting all transactions by order {}");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            Predicate predicate = getTransactionPredicate(builder, transactionType, transactionRoot);

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")), predicate,
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.equal(transactionRoot.get("userId"), userId)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), predicate,
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.equal(root.get("userId"), userId)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByTransactionTypeAndDateAndLocation(Pagination pagination, TransactionType transactionType, Date start, Date end, String location) {
        logger.info("Getting all transactions by Transaction Type :{} and Location :{} and Date Start :{}, End :{}", transactionType, location, start, end);

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            Predicate predicate = getTransactionPredicate(builder, transactionType, transactionRoot);

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")), predicate,
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(transactionRoot.get("dateCreated"), start, end), builder.equal(transactionRoot.get("location"), location)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), predicate,
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(root.get("dateCreated"), start, end), builder.equal(root.get("location"), location)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public List<Transaction> findAllByTransactionTypeAndDateAndUserId(Pagination pagination, TransactionType transactionType, Date start, Date end, String userId) {
        logger.info("Getting all transactions by order {}");

        List<Transaction> transaction = new ArrayList<>();

        try (Session session = getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();

            CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
            Root<Transaction> transactionRoot = countCriteria.from(Transaction.class);
            countCriteria.select(builder.count(transactionRoot));

            Predicate predicate = getTransactionPredicate(builder, transactionType, transactionRoot);

            countCriteria.where(builder.and(builder.isFalse(transactionRoot.get("isDeleted")), predicate,
                    builder.notEqual(transactionRoot.get("method"), PaymentMethod.WAITING), builder.notEqual(transactionRoot.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(transactionRoot.get("dateCreated"), start, end), builder.equal(transactionRoot.get("userId"), userId)));

            pagination.verify(session.createQuery(countCriteria).getSingleResult().intValue());

            CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
            Root<Transaction> root = criteria.from(Transaction.class);

            criteria.select(root).where(builder.and(builder.isFalse(root.get("isDeleted")), predicate,
                    builder.notEqual(root.get("method"), PaymentMethod.WAITING), builder.notEqual(root.get("method"), PaymentMethod.ORDER_CHANGED),
                    builder.between(root.get("dateCreated"), start, end), builder.equal(root.get("userId"), userId)));

            transaction = session.createQuery(criteria).setFirstResult(pagination.getOffset()).setMaxResults(pagination.getLimit()).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while getting all transactions of order by order");
        }
        return transaction;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setEntity(Transaction.class);
    }
}
