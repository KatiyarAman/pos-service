package com.ris.inventory.pos.util;

import com.ris.inventory.pos.domain.*;
import com.ris.inventory.pos.model.dto.*;
import com.ris.inventory.pos.repository.AddressRepository;
import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ObjectBinder {

    @Autowired
    private ModelMapper modelMapper;

    public List<TransactionDTO> bindTransaction(List<Transaction> transactions, List<LocationDTO> locationDTOS, List<UserDTO> users, boolean isPaymentWaitingRecordsRequired) {
        List<TransactionDTO> transactionDTOS = new ArrayList<>();

        transactions.forEach(
                it -> {
                    if (it != null && !it.getMethod().equals(PaymentMethod.ORDER_CHANGED)) {

                        if (isPaymentWaitingRecordsRequired) {
                            transactionBinding(it, users, transactionDTOS, locationDTOS);
                        } else {
                            if (!it.getMethod().equals(PaymentMethod.WAITING))
                                transactionBinding(it, users, transactionDTOS, locationDTOS);
                        }
                    }
                }
        );
        return transactionDTOS;
    }

    private void transactionBinding(Transaction it, List<UserDTO> users, List<TransactionDTO> transactionDTOList, List<LocationDTO> locationDTOS) {
        TransactionDTO transactionDTO = modelMapper.map(it, TransactionDTO.class);
        users.stream().filter(user -> user.getUserId().equals(it.getUserId())).findAny().ifPresent(transactionDTO::setSalesPerson);
        locationDTOS.forEach(
                locationDTO -> {
                    if (locationDTO.getLocationId().equals(it.getLocation())) {
                        transactionDTO.setLocation(locationDTO);
                    }
                });
        transactionDTOList.add(transactionDTO);
    }

    public List<TransactionDTO> bindTransaction(List<Transaction> transactions) {
        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        transactions.forEach(
                it -> {
                    if (it != null && !it.getMethod().equals(PaymentMethod.WAITING) && !it.getMethod().equals(PaymentMethod.ORDER_CHANGED)) {
                        transactionDTOS.add(modelMapper.map(it, TransactionDTO.class));
                    }
                }
        );
        return transactionDTOS;
    }

    public List<TransactionDTO> bindTransaction(List<Transaction> transactions, UserDTO userDTO) {
        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        transactions.forEach(
                it -> {
                    if (it != null && !it.getMethod().equals(PaymentMethod.WAITING) && !it.getMethod().equals(PaymentMethod.ORDER_CHANGED)) {
                        transactionDTOS.add(bindTransaction(it, userDTO));
                    }
                }
        );
        return transactionDTOS;
    }

    public TransactionDTO bindTransaction(Transaction transaction, UserDTO userDTO) {
        if (transaction == null)
            return null;

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
        transactionDTO.setSalesPerson(userDTO);
        return transactionDTO;
    }

    public CustomerDTO bindCustomer(Customer customer, AddressRepository addressRepository) {
        if (customer == null)
            return null;
        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
        customerDTO.setFullName(customerDTO.getFirstName() + " " + customerDTO.getLastName());
        List<AddressDTO> addressDTOList = new ArrayList<>();
        List<Address> addresses = addressRepository.fetchByCustomer(customer);
        addresses.forEach(
                it -> addressDTOList.add(modelMapper.map(it, AddressDTO.class))
        );
        customerDTO.setAddress(addressDTOList);
        return customerDTO;
    }

    public List<CustomerDTO> bindCustomer(List<Customer> customers, AddressRepository addressRepository) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        customers.forEach(
                it -> {
                    if (it != null)
                        customerDTOList.add(bindCustomer(it, addressRepository));
                }
        );
        return customerDTOList;
    }

    public OrderReportDTO bindOrderReport(Order order, List<UserDTO> userDTOS, List<LocationDTO> locationDTOS, List<Transaction> transactions, List<Product> products, boolean isPaymentWaitingRecordsRequired) {
        OrderReportDTO orderReport = new OrderReportDTO(order);
        orderReport.setTransactions(bindTransaction(transactions, locationDTOS, userDTOS, isPaymentWaitingRecordsRequired));
        orderReport.setProductList(bindProduct(products));
        return orderReport;
    }

    public OrderReportDTO bindOrderReport(Order order) {
        if (order != null)
            return modelMapper.map(order, OrderReportDTO.class);
        return null;
    }

    public List<ProductDTO> bindProduct(List<Product> products) {
        if (products == null)
            return new ArrayList<>();

        List<ProductDTO> productDTOList = new ArrayList<>();
        products.forEach(
                it -> {
                    if (it != null)
                        productDTOList.add(new ProductDTO(it));
                }
        );
        return productDTOList;
    }

    public List<ProductDTO> bindTransactionProducts(List<Product> products) {
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product it : products) {
            if (it != null)
                productDTOList.add(modelMapper.map(it, ProductDTO.class));
        }
        return productDTOList;
    }
}
