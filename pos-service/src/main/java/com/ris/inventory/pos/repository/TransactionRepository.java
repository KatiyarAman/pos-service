package com.ris.inventory.pos.repository;

import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.domain.Transaction;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import com.ris.inventory.pos.util.enumeration.TransactionStatus;
import com.ris.inventory.pos.util.enumeration.TransactionType;
import org.hibernate.Interceptor;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends GenericRepository<Transaction> {

    public String save(float totalDiscount, float totalTaxes, float totalProductCost, float payableAmount, float deliveryAmount, Order initiatedOrder, boolean isExchange,
                       Interceptor interceptor);

    public String save(float totalDiscount, float totalTaxes, float totalProductCost, float payableAmount, float deliveryAmount, Order order,
                       TransactionType type, boolean isExchange, Interceptor interceptor);

    public String save(float totalProductCost, float payableAmount, Order order, boolean isExchange, Interceptor interceptor);

    public String update(Order paymentWaitingOrder, TransactionType type);

    public String updatePayment(Transaction transaction, PaymentMethod method, TransactionStatus status);

    public Transaction findByOrder(Order order, TransactionType type);

    public Transaction findByOrder(Order order);

    public List<Transaction> findAllByOrder(Order order);

    public Transaction get(String transactionId);

    public Transaction get(Long id);

    public List<Transaction> list(Pagination pagination);

    public List<Transaction> findAllByLocation(Pagination pagination, String location);

    public List<Transaction> findAllByUserId(Pagination pagination, String userId);

    public List<Transaction> findAllByUserIdAndLocation(Pagination pagination, String userId, String location);

    public List<Transaction> findAllByTransactionType(Pagination pagination, TransactionType transactionType);

    public List<Transaction> findAllByDate(Pagination pagination, Date start, Date end);

    public List<Transaction> findAllByDate(Pagination pagination, Date date);

    public List<Transaction> findAllByDateAndLocation(Pagination pagination, Date start, Date end, String location);

    public List<Transaction> findAllByDateAndUserId(Pagination pagination, Date start, Date end, String userId);

    public List<Transaction> findAllByTransactionTypeAndDate(Pagination pagination, TransactionType transactionType, Date start, Date end);

    public List<Transaction> findAllByTransactionTypeAndLocation(Pagination pagination, TransactionType transactionType, String location);

    public List<Transaction> findAllByTransactionTypeAndUserId(Pagination pagination, TransactionType transactionType, String userId);

    public List<Transaction> findAllByTransactionTypeAndDateAndLocation(Pagination pagination, TransactionType transactionType, Date start, Date end, String location);

    public List<Transaction> findAllByTransactionTypeAndDateAndUserId(Pagination pagination, TransactionType transactionType, Date start, Date end, String userId);
}
