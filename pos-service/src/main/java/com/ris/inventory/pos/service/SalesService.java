package com.ris.inventory.pos.service;

import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.*;
import com.ris.inventory.pos.model.dto.InvoiceDTO;
import com.ris.inventory.pos.model.dto.OrderDTO;

import java.util.List;

public interface SalesService {

    public OrderDTO placeOrder(OrderCO orderCO, CurrentUser currentUser);

    public OrderDTO updateOrder(ChangeQuantityCO changeQuantityCO, CurrentUser currentUser);

    public InvoiceDTO orderPayment(TransactionCO transactionCO, CurrentUser currentUser);

    public void cancelOrder(String orderId, CurrentUser currentUser);

    public List<InvoiceDTO> fetchInvoices(String searchKey, CurrentUser currentUser);

    public OrderDTO refund(RefundCO refundCO, CurrentUser currentUser);

    public OrderDTO exchange(ExchangeCO exchangeCO, CurrentUser currentUser);

    public OrderDTO partialOrder(String orderId, CurrentUser currentUser);
}
