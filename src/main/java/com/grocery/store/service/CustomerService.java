package com.grocery.store.service;

import com.grocery.store.dao.CartDAO;
import com.grocery.store.dao.CustomerDAO;
import com.grocery.store.exception.BadRequestException;
import com.grocery.store.exception.ResourceNotFoundException;
import com.grocery.store.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private CartDAO cartDAO;

    public Customer createCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new BadRequestException("Customer name is required.");
        }

        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Customer email is required.");
        }

        if (customerDAO.findByEmail(customer.getEmail()).isPresent()) {
            throw new BadRequestException("Email address already registered: " + customer.getEmail());
        }

        Customer createdCustomer = customerDAO.save(customer);
        cartDAO.getOrCreateCartForCustomer(createdCustomer.getId());

        return createdCustomer;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    public Customer getCustomerById(int id) {
        return customerDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(int id, Customer customer) {
        Customer existingCustomer = getCustomerById(id);

        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new BadRequestException("Customer name is required.");
        }

        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Customer email is required.");
        }

        customerDAO.findByEmail(customer.getEmail()).ifPresent(existing -> {
            if (existing.getId() != id) {
                throw new BadRequestException("Email address already registered: " + customer.getEmail());
            }
        });

        customer.setId(existingCustomer.getId());

        return customerDAO.update(customer);
    }

    public void deleteCustomer(int id) {
        getCustomerById(id);
        customerDAO.delete(id);
    }
}