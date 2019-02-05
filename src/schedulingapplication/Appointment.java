/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapplication;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author snobo
 */
public class Appointment {
    
    private int userId;
    private String title;
    private Customer customer;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private Calendar start;
    private Calendar end;
    
    public Appointment(int tempUserId, String tempTitle, Customer tempCustomer, 
            String tempDesc, String tempLoc, String tempContact, String tempType,
            String tempUrl, Calendar tempStart, Calendar tempEnd){
        this.userId = tempUserId;
        this.title = tempTitle;
        this.customer = tempCustomer;
        this.description = tempDesc;
        this.location = tempLoc;
        this.contact = tempContact;
        this.type = tempType;
        this.url = tempUrl;
        this.start = tempStart;
        this.end = tempEnd;
    }
    
    public void setUserId (int tempUserId) {
        this.userId = tempUserId;
    }
    
    public int getUserId() {
        return this.userId;
    }
    
    public void setTitle (String tempTitle) {
        this.title = tempTitle;
    }
    
    public String getTitle () {
        return this.title;
    }
    
    public void setCustomer (Customer tempCustomer) {
        this.customer = tempCustomer;
    }
    
    public Customer getCustomer() {
        return this.customer;
    }
 
    public void setDescription (String tempDesc) {
        this.description = tempDesc;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setLocation (String tempLoc) {
        this.location = tempLoc;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setContact (String tempContact) {
        this.contact = tempContact;
    }
    
    public String getContact() {
        return this.contact;
    }    
    
    public void setType (String tempType) {
        this.type = tempType;
    }
    
    public String getType() {
        return this.type;
    }

    public void setUrl (String tempUrl) {
        this.url = tempUrl;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setStartTime(Calendar tempStart) {
        this.start = tempStart;
    }
    
    public Calendar getStartTime() {
        return this.start;
    }
    
    public void setEndTime(Calendar tempEnd) {
        this.end = tempEnd;
    }
    
    public Calendar getEndTime() {
        return this.end;
    }
}
