/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapplication;

/**
 *
 * @author snobo
 */
public class Customer {
    private int customerId;
    private String customerName;
    private Address address;
    private int active;
    
    public Customer(int tempCustomerId, String tempCustomerName, Address tempAddress,
        int tempActive) {
        this.customerId = tempCustomerId;
        this.customerName = tempCustomerName;
        this.address = tempAddress;
        this.active = tempActive;
    }
    
    public int getCustomerId() {
        return this.customerId;
    }
       
    public void setCustomerName(String tempCustomerName) {
        this.customerName = tempCustomerName;
    }
    
    public String getCustomerName() {
        return this.customerName;
    }
    
    public Address getAddress() {
        return this.address;
    }
    
    public int getStatus() {
        return this.active;
    }
    
    public void setStatus(int tempActive) {
        this.active = tempActive;
    }
    
}
