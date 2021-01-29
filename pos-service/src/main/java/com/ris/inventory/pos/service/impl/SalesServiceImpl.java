package com.ris.inventory.pos.service.impl;

import com.google.gson.Gson;
import com.ris.inventory.pos.domain.*;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.*;
import com.ris.inventory.pos.model.dto.InvoiceDTO;
import com.ris.inventory.pos.model.dto.OrderDTO;
import com.ris.inventory.pos.model.dto.ProductDTO;
import com.ris.inventory.pos.model.dto.TransactionDTO;
import com.ris.inventory.pos.repository.*;
import com.ris.inventory.pos.service.DiscoveryService;
import com.ris.inventory.pos.service.SalesService;
import com.ris.inventory.pos.util.StreamUtil;
import com.ris.inventory.pos.util.enumeration.*;
import com.ris.inventory.pos.util.exception.*;
import org.hibernate.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class SalesServiceImpl implements SalesService {

    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private Environment environment;

    private AuditInterceptor initializeInterceptor(CurrentUser currentUser) {
        return new AuditInterceptor(currentUser.getUserId(), currentUser.getAuthority().toString(),
                (String) currentUser.getLocation().get("locationId"));
    }

    /* Placing new order for a customer */
    @Override
    public OrderDTO placeOrder(OrderCO orderCO, CurrentUser currentUser) {
        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Customer customer = fetchCustomerByCustomerId(orderCO.getCustomerId());
        Delivery delivery = deliveryRepository.get(orderCO.getDeliveryId());
        List<Product> products = getFilteredInventory(orderCO.getProducts(), false, currentUser);

        Order order = initializeOrder(customer, delivery, products, interceptor);
        if (order == null) {
            logger.error("order is 'null' when entity is in process for initializing.");
            throw new EntityNotPersistException("Error Occurred, During order initializing.");
        }

        return generateBill(products, order, customer, getDeliveryAmount(delivery), interceptor);
    }

    /* Update, delete products Or cancel order for a customer */
    @Override
    public OrderDTO updateOrder(ChangeQuantityCO changeQuantityCO, CurrentUser currentUser) {
        logger.info("updating changed order for order : {} with Current User: {}", new Gson().toJson(changeQuantityCO), new Gson().toJson(currentUser));

        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Customer customer = fetchCustomerByCustomerId(changeQuantityCO.getCustomerId());
        Order order = fetchOrderByOrderId(changeQuantityCO.getOrderId());

        if (!order.getOrderStatus().equals(OrderStatus.INITIALIZED)) {
            logger.error("Exception Occurred: Order state is other then initialized. Order change can not be possible");
            throw new OrderStateException("Exception Occurred: Order state is other then initialized. Order change can not be possible");
        }

        List<Product> reCalculableProducts = new ArrayList<>();

        List<Product> newProducts = getFilteredInventory(fetchNewProducts(changeQuantityCO), false, currentUser);

        if (isOnlyNewProducts(changeQuantityCO, newProducts)) {
            logger.info("Updating only new products in the order for this order change request.");
            updateOnlyNewProducts(order, newProducts, interceptor);
            reCalculableProducts.addAll(newProducts);
        } else {
            logger.info("Updating only new products as well as old products in the order for this order change request.");

            List<Product> actualOrderedProducts = fetchProductsByOrder(order);

            List<Product> productsQuantityUpdated = updateProductQuantity(actualOrderedProducts, changeQuantityCO.getProductQuantity());
            if (!isProductsQuantityUpdated(newProducts.size(), productsQuantityUpdated.size(), changeQuantityCO.getProductQuantity())) {
                logger.error("Product updated for quantity and requested products quantity size is not equal.");
                throw new ProductNotFoundException("Product not found to update quantity");
            }

            List<Product> productsToBePersist = getMergedProductsToBePersist(order, newProducts, productsQuantityUpdated);
            orderRepository.changeQuantity(productsToBePersist, interceptor);
            reCalculableProducts.addAll(productsToBePersist);
        }

        String transactionId = transactionRepository.update(order, TransactionType.SALE);
        if (transactionId == null) {
            logger.error("Exception Occurred: While updating transaction for Order changed status");
            throw new OrderUpdateException("Exception Occurred: While updating transaction for Order changed");
        }

        float deliveryAmount = getDeliveryAmount(deliveryRepository.get(order));
        OrderDTO orderDTO = generateBill(reCalculableProducts, order, customer, deliveryAmount, interceptor);
        if (orderDTO.getPayableAmount() == 0)
            cancelOrder(orderDTO.getOrderId(), currentUser);

        return orderDTO;
    }

    /* Validation check for product quantity update */
    private boolean isProductsQuantityUpdated(int newProductsSize, int quantityToBeUpdateSize, List<QuantityCO> quantityCOList) {
        return (newProductsSize + quantityToBeUpdateSize) == quantityCOList.size();
    }

    private boolean isOnlyNewProducts(ChangeQuantityCO changeQuantityCO, List<Product> newProducts) {
        return (changeQuantityCO.getProductQuantity().size() == newProducts.size());
    }

    private void updateOnlyNewProducts(Order order, List<Product> newProductList, Interceptor interceptor) {
        for (Product product : newProductList) {
            product.setOrder(order);
        }

        orderRepository.changeQuantity(newProductList, interceptor);
    }

    private List<ProductCO> fetchNewProducts(ChangeQuantityCO changeQuantityCO) {
        List<QuantityCO> newQuantityCOList = changeQuantityCO.getProductQuantity().stream().filter(QuantityCO::isNew).collect(Collectors.toList());
        List<ProductCO> productCOList = new ArrayList<>();
        for (QuantityCO quantityCO : newQuantityCOList) {
            productCOList.add(new ProductCO(quantityCO.getProductId(), quantityCO.getQuantity()));
        }
        return productCOList;
    }

    /*Merging new products which are got from change order with existing products (Product merging with DB and calculated products)*/
    private List<Product> getMergedProductsToBePersist(Order order, List<Product> newProducts, List<Product> productsQuantityUpdated) {
        List<Product> productsToBePersist = new ArrayList<>(productsQuantityUpdated);

        for (Product product : newProducts) {
            product.setOrder(order);
            productsToBePersist.add(product);
        }
        return productsToBePersist;
    }

    /* Updating order payment method for payment transaction and final status of order */
    @Override
    public InvoiceDTO orderPayment(TransactionCO transactionCO, CurrentUser currentUser) {
        logger.info("updating payment for order {} with method {}", transactionCO.getOrderId(), transactionCO.getPaymentMethod());

        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Order order = fetchOrderByOrderId(transactionCO.getOrderId());
        Transaction transaction = fetchTransactionByOrder(order, transactionCO.getPaidFor());

        if (transactionCO.getPaymentMethod().equals(PaymentMethod.SPLIT_PAYMENT) && transactionCO.getPayments().size() > 0) {
            verifySplitPayment(transactionCO.getPayments(), transaction.getPayableAmount());
            createPayments(transactionCO, transaction, interceptor);
        }

        String transactionId = transactionRepository.updatePayment(transaction, transactionCO.getPaymentMethod(), TransactionStatus.SUCCESS);
        if (transactionId == null) {
            logger.error("Exception Occurred: While updating Transaction of Order for payment method");
            throw new PaymentUpdateException("Exception Occurred: payment updating error");
        }

        List<Product> products = orderRepository.getProductByOrder(order);
        OrderStatus orderStatus = transactionCO.getPaidFor().equals(TransactionType.REFUND) ? (isCompleteRefundable(order, products) ? OrderStatus.REFUNDED :
                OrderStatus.PARTIAL_REFUNDED) : OrderStatus.CREATED;
        orderRepository.updateOrder(order, orderStatus, interceptor);

        List<UpdateInventoryCO> updateInventoryList = getUpdatableInventory(products, transaction.getType());
        if (updateInventoryList.isEmpty())
            throw new InventoryException("Inventory update exception. Please contact admin to log this issue with Transaction Or Order Id.");

        discoveryService.updateProductInventory(updateInventoryList, currentUser);

        return getOrderInvoice(order.getOrderId());
    }

    private List<Product> fetchPurchasedProducts(List<Product> products) {
        return products.stream().filter(it -> it.getProductStatus().equals(ProductStatus.PURCHASED) ||
                it.getProductStatus().equals(ProductStatus.REPLACEMENT)).sorted((o1, o2) -> o1.getId() < o2.getId() ? -1 : 0).collect(Collectors.toList());

    }

    /**
     * Here we are fetching unique products from ordered products.
     * like We have products which have these type of status [Refund, Partial Refund, Exchange, Partial Exchange]
     **/
    private List<List<Product>> fetchOtherThanPurchasedAndReplacementProducts(List<Product> products) {
        List<Product> productList = products.stream().filter(it -> !it.getProductStatus().equals(ProductStatus.PURCHASED)
                && !it.getProductStatus().equals(ProductStatus.REPLACEMENT)).collect(Collectors.toList());

        return fetchUniqueProducts(productList);
    }

    /**
     * For segregate the products list by productId, so get List of product List
     **/
    private List<List<Product>> fetchUniqueProducts(List<Product> productList) {

        List<Product> distinctProducts = productList.stream().filter(StreamUtil.distinctByKey(Product::getProductId)).collect(Collectors.toList());

        logger.debug("Distinct Products {}", distinctProducts.toString());

        ConcurrentMap<String, List<Product>> concurrentStorage = new ConcurrentHashMap<>();
        List<List<Product>> uniqueProductList = new ArrayList<>();

        for (Product distinctProduct : distinctProducts) {
            concurrentStorage.putIfAbsent(distinctProduct.getProductId(), new ArrayList<>());

            for (Product product : productList) {
                List<Product> products = concurrentStorage.get(product.getProductId());
                if (products != null) {
                    Optional<Product> optionalProduct = products.stream().filter(it -> it.getProductId().contentEquals(product.getProductId()) && it.getId().equals(product.getId())).findAny();
                    if (!optionalProduct.isPresent()) {
                        products.add(product);
                        concurrentStorage.replace(product.getProductId(), products);
                    }
                }
            }
        }

        logger.debug("Concurrent Storage : {}", concurrentStorage.toString());
        for (Map.Entry<String, List<Product>> entry : concurrentStorage.entrySet()) {
            uniqueProductList.add(entry.getValue());
        }

        return uniqueProductList;
    }

    /**
     * When Refund OR Exchange API is called,
     * Then product status is updated by the flow that can be [Can be REFUND, REPLACEMENT, EXCHANGED, etc..] and
     * the complete order is marked with IN_progress
     * state that can be Refund_In_progress or Exchange_In_progress.
     * **/
    private List<UpdateInventoryCO> getUpdatableInventory(List<Product> products, TransactionType type) {
        logger.debug("Total products : {}", products.toString());

        List<Product> purchasedProducts = fetchPurchasedProducts(products);

        logger.debug("Purchased products : {}", purchasedProducts.toString());

        List<List<Product>> uniqueProductsList = fetchOtherThanPurchasedAndReplacementProducts(products);

        logger.debug("Unique products : {}", uniqueProductsList.toString());

        List<UpdateInventoryCO> updateInventory = new ArrayList<>();

        for (List<Product> uniqueProducts : uniqueProductsList) {
            UpdateInventoryCO updateInventoryCO = null;
            if (type.equals(TransactionType.REFUND) || type.equals(TransactionType.SALE) && !uniqueProducts.isEmpty()) {
                int lastRefund = lastRefundQuantity(uniqueProducts);
                int lastExchange = lastExchangeQuantity(uniqueProducts);

                logger.debug("Last Refund Quantity : {}", lastRefund);
                logger.debug("Last Exchange Quantity : {}", lastExchange);

                if (lastExchange == 0 && lastRefund == 0)
                    throw new RefundException("last refund and exchange quantity not found. Please contact our technical support.");

                if (lastRefund != 0)
                    updateInventoryCO = new UpdateInventoryCO(lastRefund, type, uniqueProducts.get(0).getProductLocationId());

                if (lastExchange != 0)
                    updateInventoryCO = new UpdateInventoryCO(lastExchange, TransactionType.REFUND, uniqueProducts.get(0).getProductLocationId());
            }

            if (updateInventoryCO != null)
                updateInventory.add(updateInventoryCO);
        }

        purchasedProducts.forEach(
                it -> {
                    if (it.isActive()) {
                        if (it.getProductStatus().equals(ProductStatus.PURCHASED) || it.getProductStatus().equals(ProductStatus.REPLACEMENT))
                            updateInventory.add(new UpdateInventoryCO(it.getOrderQuantity(), TransactionType.SALE, it.getProductLocationId()));
                    }
                });
        return updateInventory;
    }

    private int lastRefundQuantity(List<Product> products) {
        logger.debug("lastRefundQuantity with products : {}", products.toString());

        List<Product> productList = products.stream().filter(it -> it.getProductStatus().equals(ProductStatus.REFUNDED) ||
                it.getProductStatus().equals(ProductStatus.PARTIAL_REFUNDED)).sorted((o1, o2) -> o1.getId() > o2.getId() ? -1 : 0).collect(Collectors.toList());

        if (productList.size() == 1)
            return productList.get(0).getRefundQuantity();

        return getLastQuantityProductEntry(productList).map(Product::getRefundQuantity).orElse(0);
    }

    private int lastExchangeQuantity(List<Product> products) {
        logger.debug("lastExchangeQuantity with products : {}", products.toString());

        List<Product> productList = products.stream().filter(it -> it.getProductStatus().equals(ProductStatus.EXCHANGED) ||
                it.getProductStatus().equals(ProductStatus.PARTIAL_EXCHANGED)).collect(Collectors.toList());

        if (productList.size() == 1)
            return productList.get(0).getExchangeQuantity();

        return getLastQuantityProductEntry(productList).map(Product::getExchangeQuantity).orElse(0);
    }

    private Optional<Product> getLastQuantityProductEntry(List<Product> products) {
        logger.debug("getLastQuantityProductEntry with products : {}", products.toString());

        Optional<Product> lastQuantityProduct = Optional.empty();
        Optional<Product> product = products.stream().filter(Product::isActive).findAny();

        if (product.isPresent()) {
            Long id = product.get().getId() - 1;
            lastQuantityProduct = products.stream().filter(it -> it.getId().equals(id)).findAny();
        }
        return lastQuantityProduct;
    }

    /* complete Order cancellation */
    @Override
    public void cancelOrder(String orderId, CurrentUser currentUser) {
        logger.info("cancelling the order {}", orderId);

        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Order order = fetchOrderByOrderId(orderId);

        Transaction transaction = fetchTransactionByOrder(order, TransactionType.SALE);

        String transactionId = transactionRepository.updatePayment(transaction, PaymentMethod.CANCELLED, TransactionStatus.CANCELLED);
        if (transactionId == null) {
            logger.error("Exception Occurred: While updating Transaction of Order for cancellation");
            throw new PaymentUpdateException("Exception Occurred: order cancellation payment updating error");
        }

        orderRepository.cancelOrder(order, interceptor);
        //TODO update inventory need verification in Cancel order
    }

    /* Fetching invoice by search key */
    @Override
    public List<InvoiceDTO> fetchInvoices(String searchKey, CurrentUser currentUser) {
        logger.info("Finding complete invoice of order by search key {}", searchKey);

        Auditable signature = getSearchKeyEntity(searchKey);

        if (signature == null) {
            logger.error("Order not found by search key {}", searchKey);
            throw new OrderNotFoundException("Exception Occurred: while search order invoice by search key");
        }

        List<Order> order = fetchOrder(signature);

        List<InvoiceDTO> invoices = new ArrayList<>();
        order.forEach(
                it -> {
                    if (it.getOrderStatus().equals(OrderStatus.INITIALIZED)) {
                        logger.error("Exception Occurred: Order state is  initialized. Order invoice can not be possible");
                        throw new OrderStateException("Exception Occurred: Order state is initialized. Order invoice can not be possible");
                    }

                    invoices.add(makeInvoice(it));
                }
        );
        return invoices;
    }

    /* Refund order */
    @Override
    public OrderDTO refund(RefundCO refundCO, CurrentUser currentUser) {
        logger.info("Refunding order for {}", new Gson().toJson(refundCO));

        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Order order = fetchOrderByOrderId(refundCO.getOrderId());

        throwOrderStatusException(order);

        throwOrderAlreadyInProcessException(order);

        List<Product> productsToBeRefunded;
        if (refundCO.isComplete())
            productsToBeRefunded = refundCompleteOrder(order);
        else
            productsToBeRefunded = refundPartialOrder(order, refundCO.getProducts());

        OrderDTO orderDTO = generateRefundBill(productsToBeRefunded, order,
                customerRepository.findByOrder(orderRepository.get(order.getOrderId())), interceptor);

        if (!orderRepository.updateProducts(productsToBeRefunded, interceptor)) {
            logger.error("Exception Occurred: While refund process is in progress");
            throw new EntityNotPersistException("Exception Occurred: While updating products. refund process is in progress");
        }

        orderRepository.updateOrder(order, OrderStatus.REFUND_IN_PROGRESS, interceptor);
        //TODO update inventory need verification in REFUND
        return orderDTO;
    }

    @Override
    public OrderDTO exchange(ExchangeCO exchangeCO, CurrentUser currentUser) {
        logger.info("Doing exchange products of already purchased products of order {} ", new Gson().toJson(exchangeCO));

        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Order order = fetchOrderByOrderId(exchangeCO.getOrderId());
        Delivery delivery = deliveryRepository.get(exchangeCO.getDeliveryId());

        throwOrderStatusException(order);

        throwOrderAlreadyInProcessException(order);

        List<Product> replacement = getFilteredInventory(exchangeCO.getReplacements(), true, currentUser);
        List<Product> exchangeable = fetchExchangeableProducts(order, exchangeCO.getProducts(), interceptor);

        setReplacementProductsToOrder(order, replacement);

        orderRepository.updateProducts(replacement, interceptor);
        orderRepository.updateProducts(exchangeable, interceptor);

        if (delivery != null)
            orderRepository.updateDelivery(order, delivery, interceptor);

        OrderDTO exchangeableOrderDTO = calculateExchangeBill(exchangeable, order);
        OrderDTO replacementOrderDTO = calculateBill(replacement, order, getDeliveryAmount(delivery));
        OrderDTO invoice = generateExchangeBill(order, exchangeableOrderDTO, replacementOrderDTO, interceptor);
        orderRepository.updateOrder(order, OrderStatus.EXCHANGE_IN_PROGRESS, interceptor);
        return invoice.abs();
    }

    @Override
    public OrderDTO partialOrder(String orderId, CurrentUser currentUser) {
        logger.info("Fetching Partial order for {}", orderId);

        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Order order = fetchOrderByOrderId(orderId);

        Transaction transaction = fetchTransactionByOrder(order);

        Customer customer = customerRepository.findByOrder(order);

        OrderDTO orderDTO = new OrderDTO(order.getOrderId(), transaction.getOrderCost(), transaction.getPayableAmount());
        orderDTO.setTransactionId(transaction.getTransactionId());
        orderDTO.setCustomerId(customer.getCustomerId());
        orderDTO.setMobile(customer.getMobile());
        return orderDTO;
    }

    private void verifySplitPayment(List<PaymentCO> paymentCOList, final float payableAmount) {
        float totalPaymentAmount = decimalRounding(paymentCOList.stream().mapToDouble(PaymentCO::getAmount).sum());

        float difference = decimalRounding(payableAmount - totalPaymentAmount);

        logger.info("Difference {}  of SPLIT_PAYMENT {} and payable amount {}", difference, totalPaymentAmount, payableAmount);

        if (Math.round(difference) != 0 || Math.round(difference) != 0.0)
            throw new SplitPaymentException("Split Payment mode required complete payable amount by using multiple payment methods. " +
                    "Difference is :" + difference + " and Split total is :" + totalPaymentAmount + "and Payable is :" + payableAmount);
    }

    private void createPayments(TransactionCO transactionCO, Transaction transaction, Interceptor interceptor) {
        logger.info("creating split payments for transaction :{}", transaction.getTransactionId());
        AuditInterceptor auditInterceptor = (AuditInterceptor) interceptor;
        transactionCO.getPayments().forEach(
                it -> {
                    Payment payment = new Payment(it, auditInterceptor.getUserId(), auditInterceptor.getLocation(), transaction);
                    paymentRepository.save(payment, interceptor);
                }
        );
    }

    private List<Product> fetchExchangeableProducts(Order order, List<ProductCO> productCO, Interceptor interceptor) {
        List<Product> products = fetchProductsByOrder(order);

        List<Product> exchangeableProducts = new ArrayList<>();
        products.forEach(
                it -> productCO.forEach(
                        CO -> {
                            if (it.getProductId().contentEquals(CO.getProductId())) {

                                if (!it.getProductStatus().equals(ProductStatus.REFUNDED) && !it.getProductStatus().equals(ProductStatus.EXCHANGED) && it.isActive()) {

                                    Integer totalRefundedQuantity = orderRepository.getTotalRefundedQuantity(order, it.getProductId());
                                    Integer totalExchangedQuantity = orderRepository.getTotalExchangedQuantity(order, it.getProductId());
                                    int newActualOrderedQuantity = it.getOrderQuantity() - totalRefundedQuantity;
                                    int toBeExchangeQuantity = totalExchangedQuantity + CO.getOrderQuantity();

                                    if (totalExchangedQuantity == -1 || totalRefundedQuantity == -1 || toBeExchangeQuantity > newActualOrderedQuantity) {
                                        logger.error("Exchange quantity limit can not be greater than actual order quantity. Exchange can not " +
                                                "be possible");
                                        throw new ExchangeException("Exception Occurred : Exchange quantity limit can not be greater than actual " +
                                                "order quantity. Exchange can not be possible");
                                    }

                                    int newQuantity = it.getOrderQuantity() - CO.getOrderQuantity();
                                    if (newQuantity == 0 && it.getExchangeQuantity() == 0) {
                                        it.setProductStatus(ProductStatus.EXCHANGED);
                                        it.setExchangeQuantity(CO.getOrderQuantity());
                                        it.setActive(false);
                                    } else {
                                        Product partialProduct = new Product(it);
                                        ProductStatus productStatus = toBeExchangeQuantity == it.getOrderQuantity() ?
                                                ProductStatus.EXCHANGED : ProductStatus.PARTIAL_EXCHANGED;
                                        partialProduct.setProductStatus(productStatus);
                                        partialProduct.setExchangeQuantity(CO.getOrderQuantity());
                                        partialProduct.setOrder(order);
                                        exchangeableProducts.add(partialProduct);

                                        if (!partialProduct.getProductStatus().equals(ProductStatus.PARTIAL_EXCHANGED))
                                            partialProduct.setActive(false);
                                        it.setActive(false);
                                    }
                                    exchangeableProducts.add(it);
                                }
                            }
                        }
                )
        );
        return exchangeableProducts;
    }

    private void setReplacementProductsToOrder(Order order, List<Product> products) {
        products.forEach(
                it -> it.setOrder(order)
        );
    }

    private OrderDTO generateExchangeBill(Order order, OrderDTO exchange, OrderDTO replacement, Interceptor interceptor) {
        Customer customer = customerRepository.findByOrder(order);
        OrderDTO orderDTO = OrderDTO.calculateExchangeBill(order.getOrderId(), customer.getCustomerId(), customer.getMobile(), exchange, replacement);

        String transactionId = createExchangeTransaction(orderDTO.getTotalDiscount(), orderDTO.getTotalTax(), orderDTO.getOrderCost(),
                orderDTO.getPayableAmount(), orderDTO.getDeliveryAmount(), order, interceptor);
        orderDTO.setTransactionId(transactionId);
        return orderDTO;
    }

    private List<Product> refundCompleteOrder(Order order) {
        List<Product> products = fetchProductsByOrder(order);
        List<Product> productsToBeRefunded = new ArrayList<>();
        products.forEach(
                it -> {
                    if (!it.getProductStatus().equals(ProductStatus.REFUNDED) && !it.getProductStatus().equals(ProductStatus.EXCHANGED) && it.isActive()) {
                        Integer totalRefundedQuantity = orderRepository.getTotalRefundedQuantity(order, it.getProductId());

                        if (totalRefundedQuantity != -1) {
                            if (it.getProductStatus().equals(ProductStatus.PARTIAL_REFUNDED)) {
                                int newRefundQuantity = it.getOrderQuantity() - totalRefundedQuantity;
                                Product partialProduct = new Product(it);
                                partialProduct.setProductStatus(ProductStatus.REFUNDED);
                                partialProduct.setRefundQuantity(newRefundQuantity);
                                partialProduct.setOrder(order);
                                productsToBeRefunded.add(partialProduct);
                                it.setActive(false);
                            } else {
                                it.setRefundQuantity(it.getOrderQuantity());
                                it.setProductStatus(ProductStatus.REFUNDED);
                            }
                            productsToBeRefunded.add(it);
                        }
                    }
                }
        );
        return productsToBeRefunded;
    }

    private List<Product> refundPartialOrder(Order order, List<ProductCO> productCO) {
        List<Product> products = fetchProductsByOrder(order);

        List<Product> productsToBeRefunded = new ArrayList<>();
        products.forEach(
                it -> productCO.forEach(
                        CO -> {
                            if (it.getProductId().contentEquals(CO.getProductId())) {
                                if (!it.getProductStatus().equals(ProductStatus.REFUNDED) && !it.getProductStatus().equals(ProductStatus.EXCHANGED) && it.isActive()) {

                                    Integer totalRefundedQuantity = orderRepository.getTotalRefundedQuantity(order, it.getProductId());
                                        int toBeRefunded = CO.getOrderQuantity() + totalRefundedQuantity;

                                    if (totalRefundedQuantity == -1 || toBeRefunded > it.getOrderQuantity()) {
                                        logger.error("Refund quantity limit can not be greater than actual order quantity. Refund can not " +
                                                "be possible");
                                            throw new RefundException("Exception Occurred : Refund quantity limit can not be greater than actual " +
                                                    "order quantity. Refund can not be possible");
                                        }

                                        int newQuantity = it.getOrderQuantity() - CO.getOrderQuantity();

                                        if (newQuantity == 0 && it.getRefundQuantity() == 0) {
                                            it.setProductStatus(ProductStatus.REFUNDED);
                                            it.setRefundQuantity(CO.getOrderQuantity());
                                        } else {
                                            Product partialProduct = new Product(it);
                                            ProductStatus productStatus = toBeRefunded == it.getOrderQuantity() ?
                                                    ProductStatus.REFUNDED : ProductStatus.PARTIAL_REFUNDED;
                                            partialProduct.setProductStatus(productStatus);
                                            partialProduct.setRefundQuantity(CO.getOrderQuantity());
                                            partialProduct.setOrder(order);
                                            productsToBeRefunded.add(partialProduct);
                                            it.setActive(false);
                                        }
                                        productsToBeRefunded.add(it);
                                    }
                                }
                            }
                )
        );

        return productsToBeRefunded;
    }

    private boolean isCompleteRefundable(Order order, List<Product> products) {
        List<Boolean> isCompleteRefunded = new ArrayList<>();

        List<List<Product>> uniqueProducts = fetchUniqueProducts(products);
        for (List<Product> productList : uniqueProducts) {
            if (productList != null && !productList.isEmpty()) {
                Product product = productList.get(0);
                int totalRefund = orderRepository.getTotalRefundedQuantity(order, product.getProductId());
                if (totalRefund == product.getOrderQuantity())
                    isCompleteRefunded.add(true);
            }
        }
        return isCompleteRefunded.size() == uniqueProducts.size();
    }

    //TODO Not implemented correctly as per refunded
    private boolean isCompleteExchangeable(Order order, List<Product> products) {
        List<Boolean> isCompleteExchanged = new ArrayList<>();

        for (Product product : products) {
            int totalExchange = orderRepository.getTotalExchangedQuantity(order, product.getProductId());
            isCompleteExchanged.add(totalExchange == product.getOrderQuantity());
        }
        return isCompleteExchanged.size() == products.size();
    }

    private OrderDTO calculateRefundBill(List<Product> products, Order order) {
        logger.info("calculating refund bill for {} products and order {}", products.size(), order);

        List<Float> productCost = new ArrayList<>();
        products.forEach(
                it -> {

                    float cost = it.getUnitSaleCost() * it.getRefundQuantity();
                    if (cost > 0 && it.isActive()) {
                        productCost.add(cost);
                    }
                }
        );

        float totalProductCost = (float) productCost.stream().mapToDouble(Float::floatValue).sum();
        float payableAmount = totalProductCost;

        return new OrderDTO(order.getOrderId(), totalProductCost, payableAmount);
    }

    private OrderDTO generateRefundBill(List<Product> products, Order order, Customer customer, Interceptor interceptor) {
        OrderDTO orderDTO = calculateRefundBill(products, order);
        orderDTO.setTransactionId(createRefundTransaction(orderDTO.getOrderCost(), orderDTO.getPayableAmount(), order, interceptor));
        orderDTO.setCustomerId(customer.getCustomerId());
        orderDTO.setMobile(customer.getMobile());
        return orderDTO;
    }

    private String createRefundTransaction(float totalProductCost, float payableAmount, Order initiatedOrder, Interceptor interceptor) {
        String transactionId = transactionRepository.save(totalProductCost, payableAmount, initiatedOrder, false, interceptor);
        if (transactionId == null) {
            logger.error("transaction is 'null' when entity is in process for creating refund transaction of order {}.", initiatedOrder.getOrderId());
            throw new EntityNotPersistException("Error Occurred: During refund transaction initializing.");
        }
        return transactionId;
    }

    private String createExchangeTransaction(float totalDiscount, float totalTaxes, float totalProductCost, float payableAmount, float deliveryAmount,
                                             Order order, Interceptor interceptor) {
        TransactionType transactionType = payableAmount > 0 ? TransactionType.SALE : TransactionType.REFUND;
        String transactionId = transactionRepository.save(Math.abs(totalDiscount), Math.abs(totalTaxes), Math.abs(totalProductCost),
                Math.abs(payableAmount), deliveryAmount, order, transactionType, true, interceptor);
        if (transactionId == null) {
            logger.error("transaction is 'null' when entity is in process for creating refund/sale transaction of exchange order {}.",
                    order.getOrderId());
            throw new EntityNotPersistException("Error Occurred: During refund/sale transaction of exchange initializing.");
        }
        return transactionId;
    }

    /* Finding entity for search by search key*/
    private Auditable getSearchKeyEntity(String searchKey) {
        String[] searchKeys = searchKey.split("-");
        if (searchKeys.length == 2 && searchKeys[0].contentEquals("O")) {
            return orderRepository.get(searchKey);
        } else if (searchKeys.length == 2 && searchKeys[0].contentEquals("TXN")) {
            return transactionRepository.get(searchKey);
        } else {
            return customerRepository.findByMobile(searchKey);
        }
    }

    /* Get order by search key for entity signature */
    private List<Order> fetchOrder(Auditable signature) {

        if (signature instanceof Order) {
            List<Order> orders = new ArrayList<>();
            orders.add((Order) signature);
            return orders;
        } else if (signature instanceof Transaction) {
            Order order = orderRepository.findByTransaction((Transaction) signature);
            if (order == null) {
                logger.error("order is 'null' when fetching by transaction for invoice");
                throw new OrderNotFoundException("Error Occurred, During getting order by transaction for invoice");
            }
            List<Order> orders = new ArrayList<>();
            orders.add(order);
            return orders;
        } else if (signature instanceof Customer) {
            List<Order> order = orderRepository.findAllByCustomer((Customer) signature);
            if (order == null || order.isEmpty()) {
                logger.error("order is 'null' when fetching by transaction for invoice");
                throw new OrderNotFoundException("Error Occurred, During getting order by customer for invoice");
            }
            return order;
        } else {
            logger.error("Order not found. Signature case missed to get the order for invoice");
            throw new OrderNotFoundException("Error Occurred, During getting order for invoice");
        }
    }

    /* Initialize Order by condition with or without delivery */
    private Order initializeOrder(Customer customer, Delivery delivery, List<Product> products, Interceptor interceptor) {
        Order order;
        if (delivery == null)
            order = orderRepository.initiateOrder(products, customer, interceptor);
        else
            order = orderRepository.initiateOrder(products, customer, delivery, interceptor);
        return order;
    }

    private float getDeliveryAmount(Delivery delivery) {
        return delivery != null ? delivery.getAmount() : 0;
    }

    /* Create invoice DTO for order */
    private InvoiceDTO getOrderInvoice(String orderId) {
        logger.info("preparing invoice for order id {}", orderId);

        Order refreshedOrder = fetchOrderByOrderId(orderId);

        if (refreshedOrder.getOrderStatus().equals(OrderStatus.INITIALIZED)) {
            logger.error("Exception Occurred: Order state is  initialized. Order invoice can not be possible");
            throw new OrderStateException("Exception Occurred: Order state is initialized. Order invoice can not be possible");
        }

        return makeInvoice(refreshedOrder);
    }

    /* making invoice for a order */
    private InvoiceDTO makeInvoice(Order order) {
        logger.info("making invoice DTO using order and transaction");

        InvoiceDTO invoiceDTO = new InvoiceDTO(order);

        List<Transaction> transactions = fetchAllTransactionsByOrder(order);
        transactions.forEach(
                it -> invoiceDTO.setTransaction(new TransactionDTO(it))
        );
        List<Product> products = fetchProductsByOrder(order);
        products.forEach(
                it -> {
                    if (it.isActive()) {
                        invoiceDTO.setProduct(new ProductDTO(it));
                    }
                }
        );
        return invoiceDTO;
    }

    /* Getting products found in inventory and check conditions for order quantity*/
    private List<Product> updateProductQuantity(List<Product> orderedProducts, List<QuantityCO> quantityToBeUpdate) {
        List<Product> products = new ArrayList<>();
        if (orderedProducts != null && quantityToBeUpdate != null) {
            orderedProducts.forEach(
                    it -> quantityToBeUpdate.forEach(
                            co -> {
                                if (co.getProductId().contentEquals(it.getProductId()) && !co.isNew()) {
                                    int newQuantity = co.getQuantity() - it.getOrderQuantity();

                                    if (newQuantity > 0)
                                        it.setOrderQuantity(it.getOrderQuantity() + newQuantity);
                                    else
                                        it.setOrderQuantity(it.getOrderQuantity() - Math.abs(newQuantity));

                                    products.add(it);
                                }
                            }
                    )
            );
        }
        return products;
    }

    private OrderDTO generateBill(List<Product> products, Order order, Customer customer, float deliveryAmount, Interceptor interceptor) {
        OrderDTO orderDTO = calculateBill(products, order, deliveryAmount);
        orderDTO.setTransactionId(createPaymentTransaction(orderDTO.getTotalDiscount(), orderDTO.getTotalTax(), orderDTO.getOrderCost(),
                orderDTO.getPayableAmount(), deliveryAmount, order, interceptor));
        orderDTO.setCustomerId(customer.getCustomerId());
        orderDTO.setMobile(customer.getMobile());
        return orderDTO;
    }

    private String createPaymentTransaction(float totalDiscount, float totalTaxes, float totalProductCost, float payableAmount, float deliveryAmount,
                                            Order initiatedOrder, Interceptor interceptor) {
        String transactionId = transactionRepository.save(totalDiscount, totalTaxes, totalProductCost, payableAmount, deliveryAmount, initiatedOrder, false,
                interceptor);
        if (transactionId == null) {
            logger.error("transaction is 'null' when entity is in process for creating transaction of order {} .", initiatedOrder.getOrderId());
            throw new EntityNotPersistException("Error Occurred, During transaction initializing.");
        }
        return transactionId;
    }

    private Customer fetchCustomerByCustomerId(String customerId) {
        Customer customer = customerRepository.get(customerId);
        if (customer == null) {
            logger.error("Customer not found by {} ", customerId);
            throw new CustomerNotFoundException("Customer not found when you are placing new order");
        }
        return customer;
    }

    private Order fetchOrderByOrderId(String orderId) {
        Order order = orderRepository.get(orderId);
        if (order == null) {
            logger.error("order is 'null' when fetching by order id");
            throw new OrderNotFoundException("Error Occurred, During getting order by orderId");
        }
        return order;
    }

    private Transaction fetchTransactionByOrder(Order order, TransactionType type) {
        Transaction transaction = transactionRepository.findByOrder(order, type);
        if (transaction == null) {
            logger.error("transaction is 'null' when fetching by order and transaction type " + type);
            throw new TransactionNotFoundException("Error Occurred. While reading payment details for '" + type +
                    "' transaction. According to our records, Your payment is not pending for this type of transaction");
        }
        return transaction;
    }

    private Transaction fetchTransactionByOrder(Order order) {
        Transaction transaction = transactionRepository.findByOrder(order);
        if (transaction == null) {
            logger.error("transaction is 'null' when fetching by order");
            throw new TransactionNotFoundException("Error Occurred, During getting transaction by order for which payment required.");
        }
        return transaction;
    }

    private List<Transaction> fetchAllTransactionsByOrder(Order order) {
        List<Transaction> transaction = transactionRepository.findAllByOrder(order);
        if (transaction == null || transaction.isEmpty()) {
            logger.error("transaction is 'null' when fetching by transaction id");
            throw new TransactionNotFoundException("Error Occurred, During getting transaction by transactionId");
        }
        return transaction;
    }

    private List<Product> fetchProductsByOrder(Order order) {
        List<Product> products = orderRepository.getProductByOrder(order);
        if (products == null || products.isEmpty()) {
            logger.error("products are null/empty of this order when fetching by order id");
            throw new ProductNotFoundException("Error Occurred, products are null/empty of this order");
        }
        return products;
    }

    private void throwOrderStatusException(Order order) {
        if (order.getOrderStatus().equals(OrderStatus.INITIALIZED) || order.getOrderStatus().equals(OrderStatus.ON_THE_WAY) || order.getOrderStatus().equals(OrderStatus.REFUNDED)) {
            logger.error("Exception Occurred: Order state is not allowed to refund this order. Order refund process can not be possible");
            throw new OrderStateException("Exception Occurred: Order state is not allowed to refund this order. Order refund process can not be " +
                    "possible");
        }
    }

    private void throwOrderAlreadyInProcessException(Order order) {
        if (order.getOrderStatus().equals(OrderStatus.REFUND_IN_PROGRESS) || order.getOrderStatus().equals(OrderStatus.EXCHANGE_IN_PROGRESS)) {
            logger.error("Exception Occurred: Order state is not allowed to refund/exchange this order. Payment API required.");
            throw new PaymentUpdateException("This Order is already IN REFUND/EXCHANGE PROCESS. Payment is required.");
        }
    }

    /* Calculating bill for selected products */
    private OrderDTO calculateBill(List<Product> products, Order initiatedOrder, float deliveryAmount) {
        logger.info("calculating bill for {} products", products.size());

        List<Float> productDiscounts = new ArrayList<>();
        List<Float> productCost = new ArrayList<>();
        List<Float> productTaxes = new ArrayList<>();
        products.forEach(
                it -> {
                    float cost = it.getUnitSaleCost() * it.getOrderQuantity();
                    if (cost > 0) {
                        productCost.add(cost);
                        if (it.isPercentageDiscount()) {
                            float amount = ((it.getUnitSaleCost() * it.getDiscount()) / 100);
                            productDiscounts.add(amount * it.getOrderQuantity());
                        } else {
                            productDiscounts.add(it.getDiscount() * it.getOrderQuantity());
                        }

                        if (it.isPercentageTax()) {
                            float amount = ((it.getUnitSaleCost() * it.getAmountForTax()) / 100);
                            productTaxes.add(amount * it.getOrderQuantity());
                        } else {
                            productTaxes.add(it.getAmountForTax() * it.getOrderQuantity());
                        }
                    }
                }
        );

        float totalDiscount = decimalRounding(productDiscounts.stream().mapToDouble(Float::floatValue).sum());
        float totalTaxes = decimalRounding(productTaxes.stream().mapToDouble(Float::floatValue).sum());
        float totalProductCost = decimalRounding(productCost.stream().mapToDouble(Float::floatValue).sum());
        float payableAmount = decimalRounding(((totalProductCost + totalTaxes) - totalDiscount) + deliveryAmount);

        return new OrderDTO(initiatedOrder.getOrderId(), totalDiscount, totalTaxes, totalProductCost, payableAmount, deliveryAmount);
    }

    /* Calculating bill for selected exchangeable products */
    private OrderDTO calculateExchangeBill(List<Product> products, Order order) {
        logger.info("Calculating bill for {} exchangeable products", products.size());

        List<Float> productDiscounts = new ArrayList<>();
        List<Float> productCost = new ArrayList<>();
        List<Float> productTaxes = new ArrayList<>();
        products.forEach(
                it -> {
                    float cost = it.getUnitSaleCost() * it.getExchangeQuantity();
                    if (cost > 0) {
                        productCost.add(cost);
                        if (it.isPercentageDiscount()) {
                            float amount = ((it.getUnitSaleCost() * it.getDiscount()) / 100);
                            productDiscounts.add(amount * it.getExchangeQuantity());
                        } else {
                            productDiscounts.add(it.getDiscount() * it.getExchangeQuantity());
                        }

                        if (it.isPercentageTax()) {
                            float amount = ((it.getUnitSaleCost() * it.getAmountForTax()) / 100);
                            productTaxes.add(amount * it.getExchangeQuantity());
                        } else {
                            productTaxes.add(it.getAmountForTax() * it.getExchangeQuantity());
                        }
                    }
                }
        );

        float totalDiscount = decimalRounding(productDiscounts.stream().mapToDouble(Float::floatValue).sum());
        float totalTaxes = decimalRounding(productTaxes.stream().mapToDouble(Float::floatValue).sum());
        float totalProductCost = decimalRounding(productCost.stream().mapToDouble(Float::floatValue).sum());
        float payableAmount = decimalRounding(((totalProductCost + totalTaxes) - totalDiscount));

        return new OrderDTO(order.getOrderId(), totalDiscount, totalTaxes, totalProductCost, payableAmount);
    }

    private float decimalRounding(double amount) {
        decimalFormat.setRoundingMode(RoundingMode.UP);
        return Float.valueOf(decimalFormat.format(amount));
    }

    /* getting selected products from inventory  */
    private List<Product> getFilteredInventory(List<ProductCO> productCOList, boolean isReplacement, CurrentUser currentUser) {
        logger.info("Getting products inventory for {}:", new Gson().toJson(productCOList));

        List<Product> products;
        if (environment.acceptsProfiles(Profiles.of("dev")))
            products = discoveryService.fetchMockInventory(productCOList);
        else
            products = discoveryService.fetchProductInventory(productCOList, currentUser);

        List<Product> filteredInventory = filterInventory(products, productCOList, isReplacement);
        if (filteredInventory.isEmpty()) {
            logger.error("Requested products are not found in our inventory");
            throw new ProductNotFoundException("Requested products are not found in our inventory");
        }
        return filteredInventory;
    }

    /* Filtering products inventory with setting status & quantity of products */
    private List<Product> filterInventory(List<Product> inventory, List<ProductCO> productCOList, boolean isReplacement) {
        logger.info("Filtering inventory for {}:", new Gson().toJson(productCOList));

        if (productCOList.size() != inventory.size())
            throw new InventoryException("Inventory Product count is Mis-Match Ordered Products are '" + productCOList.size()
                    + "' and Inventory products are '" + inventory.size() + "'");

        List<Product> filteredInventory = new ArrayList<>();
        ProductStatus productStatus = isReplacement ? ProductStatus.REPLACEMENT : ProductStatus.PURCHASED;

        inventory.forEach(
                it -> productCOList.forEach(
                        CO -> {
                            if (CO.getProductId().contentEquals(it.getProductId())) {
                                it.setProductStatus(productStatus);
                                int quantity = it.getActualQuantity()-(it.getConsumeQuantity() + CO.getOrderQuantity());
                                if (quantity < 0) {
                                    logger.error("Requested quantity of inventory is not available. Please try with less than or equals to " + it.getActualQuantity());
                                    throw new InventoryException("Requested quantity of inventory is not available. Please try with less than or " +
                                            "equals to " + it.getActualQuantity());
                                }
                                it.setOrderQuantity(CO.getOrderQuantity());
                                filteredInventory.add(it);
                            }
                        }
                )
        );
        return filteredInventory;
    }
}
