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
public class Address {
    private final int addressId;
    private String address;
    private String address2;
    private String city;
    private String country;
    private String postalCode;
    private String phone;
    
    public Address(int tempId, String tempAddress, String tempAddress2, String tempCity,
            String tempCountry, String tempPostal, String tempPhone) {
        this.addressId = tempId;
        this.address = tempAddress;
        this.address2=tempAddress2;
        this.city = tempCity;
        this.country = tempCountry;
        this.postalCode = tempPostal;
        this.phone = tempPhone;
    }
    
    public int getAddressId () {
        return this.addressId;
    }
    
    public void setAddress(String tempAddress) {
        this.address = tempAddress;
    } 
    
    public void setAddress(String tempAddress, String tempAddress2) {
        this.address = tempAddress;
        this.address2 = tempAddress2;
    }
    
    public String getAddress() {
        return this.address;                
    }
    
    public String getAddress2() {
        return this.address2;
    }
    
    public void setCity(String tempCity) {
        this.city = tempCity;
    }
    
    public String getCity() {
        return this.city;
    }
    
    public void setCountry(String tempCountry) {
        this.country = tempCountry;
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public void setPostalCode(String tempPostal) {
        this.postalCode = tempPostal;
    }
    
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public void setPhoneNumber(String tempPhone) {
        this.phone = tempPhone;
    }
    
    public String getPhoneNumber() {
        return this.phone;
    }
    
    public String getFullAddress() {
        String fullAddress = this.address + "\r\n" + this.address2 + "\r\n" +
                this.city + ", " + this.country + ", " + this.postalCode + "\r\n" +
                this.phone;
        
        return fullAddress;
    }
}
