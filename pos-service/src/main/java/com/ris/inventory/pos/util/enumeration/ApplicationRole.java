package com.ris.inventory.pos.util.enumeration;

import com.ris.inventory.pos.util.exception.NotFoundException;

import java.util.LinkedList;
import java.util.List;

public enum ApplicationRole {

    SUPER_ADMIN("ROLE_SUPER_ADMIN"), //1
    ADMIN("ROLE_ADMIN"), //2
    MANAGER("ROLE_MANAGER"),//3
    SALE("ROLE_SALES_PERSON"),//4
    CUSTOMER("ROLE_CUSTOMER");

    private final String role;

    ApplicationRole(String role) {
        this.role = role;
    }

    public static ApplicationRole from(String authority) {
        if (authority.equalsIgnoreCase("role_admin"))
            return ADMIN;
        else if (authority.equalsIgnoreCase("role_customer"))
            return CUSTOMER;
        else if (authority.equalsIgnoreCase("role_sales_person"))
            return SALE;
        else if (authority.equalsIgnoreCase("role_manager"))
            return MANAGER;
        else
            throw new NotFoundException("role not found");
    }

    public static List<ApplicationRole> getApplicationRoles() {
        List<ApplicationRole> roles = new LinkedList<ApplicationRole>();
        roles.add(SUPER_ADMIN);
        roles.add(ADMIN);
        roles.add(CUSTOMER);
        roles.add(SALE);
        roles.add(MANAGER);
        return roles;
    }

    public boolean isVerified() {
        return getApplicationRoles().contains(this);
    }

    public boolean hasSalePermission() {
        return SALE.getRole().equals(this.role);
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role;
    }
}
