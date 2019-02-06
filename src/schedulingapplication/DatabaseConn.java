/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapplication;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
/**
 *
 * @author snobo
 */
public class DatabaseConn {
        
    private String driver;
    private String db;
    private String url;
    private String user;
    private String pass; 
    
    public DatabaseConn() throws ClassNotFoundException {
        driver = "com.mysql.jdbc.Driver";
        db = "U052VC";
        url = "jdbc:mysql://52.206.157.109/" + db;
        user = "U052VC";
        pass = "53688413454";
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                Statement stmt = conn.createStatement();
                
                System.out.println("Database Setup");
            }
        } catch (SQLException e){
            System.out.println(e);
        }
    }
    
    public User signIn(String tempUserName, String tempPassword, ArrayList<Customer> tempCustList)
            throws ClassNotFoundException{
        User currentUser = null;
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                PreparedStatement pStmt = null;
                String loginQuery = "SELECT user.userId, user.userName, user.active "
                        + "FROM user WHERE user.userName = ? AND user.password = ?";
                pStmt = conn.prepareStatement(loginQuery);
                pStmt.setString(1,tempUserName);
                pStmt.setString(2,tempPassword);
                ResultSet rs = pStmt.executeQuery();
                
                int userId = 0;
                String userName = "Not Set";
                int active = 0;
                
                if(rs.next()) {
                    ResultSetMetaData rsMetaData = rs.getMetaData();
                    for(int i = 1; i <= rsMetaData.getColumnCount(); i++){
                        String cv = rs.getString(i);
                        switch(rsMetaData.getColumnName(i)){
                            case "userId":
                                userId = rs.getInt(i);
                                break;
                            case "userName":
                                userName = cv;
                                break;
                            case "active":
                                active = rs.getInt(i);
                                break;
                        }
                    }
                    //getUserAppointmentInfo well not requre an array list of customers... add this asap.
                    currentUser = new User(userId, userName, active, 
                            getCustomerAppointmentInfo(userId, tempCustList));
                    conn.close();
                }
                else{
                    System.out.println("Invalid username or password");
                }
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return currentUser;
    }
    
    public ArrayList<Customer> getCustomerInfo() throws ClassNotFoundException {
        ArrayList<Customer> ci = new ArrayList<>();
        ResultSet customerInfo = null;
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                PreparedStatement pStmt = null;
                String customerInfoQuery = "SELECT customer.customerId, customer.customerName, "
                        + "customer.addressId, customer. active "
                        + "FROM customer";
                pStmt = conn.prepareStatement(customerInfoQuery);
                customerInfo = pStmt.executeQuery();
                
                
                
                while(customerInfo.next()){

                    int customerId = 0;
                    String customerName = "";
                    int active = 0;
                    int addressId = 0;

                    ResultSetMetaData rsMetaData = customerInfo.getMetaData();

                    for(int i = 1; i <= rsMetaData.getColumnCount(); i++){
                        String cv = customerInfo.getString(i);
                        switch(rsMetaData.getColumnName(i)){
                            case "customerId":
                                customerId = customerInfo.getInt(i);
                            case "customerName":
                                customerName = cv;
                                break;
                            case "addressId":
                                addressId = customerInfo.getInt(i);
                                break;
                            case "active":
                                active = customerInfo.getInt(i);
                        }
                    }
                    Address tempAddress = getCustomerAddressInfo(addressId);
                        
                    ci.add(new Customer(customerId, customerName, 
                        tempAddress, active));
                }
            }
        }   catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return ci;
    }
    
    public Address getCustomerAddressInfo(int tempAddressId) throws ClassNotFoundException {
        ResultSet userAddressInfo = null;
        Address tempAddress = null;
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                PreparedStatement pStmt = null;
                String customerAddressQuery = "SELECT address.addressId, address.address, "
                        + "address.address2, city.city, country.country,address.postalCode, "
                        + "address.phone FROM address JOIN city ON "
                        + "city.cityId = (SELECT address.cityId FROM address WHERE addressId = ?) "
                        + "JOIN country ON country.countryId = (SELECT city.countryId FROM city "
                        + "WHERE city.cityId = (SELECT address.cityId FROM address WHERE addressId = ?)) "
                        + "WHERE address.addressId = ?";
                pStmt = conn.prepareStatement(customerAddressQuery);
                pStmt.setInt(1,tempAddressId);
                pStmt.setInt(2,tempAddressId);
                pStmt.setInt(3,tempAddressId);
                userAddressInfo = pStmt.executeQuery();
                
                while(userAddressInfo.next()){

                    int addressId = 0;
                    String address = "";
                    String address2 = "";
                    String city = "";
                    String country = "";
                    String postalCode = "";
                    String phone = "";

                    ResultSetMetaData rsMetaData = userAddressInfo.getMetaData();

                    for(int i = 1; i <= rsMetaData.getColumnCount(); i++){
                        String cv = userAddressInfo.getString(i);
                        switch(rsMetaData.getColumnName(i)){
                            case "addressId":
                                addressId = userAddressInfo.getInt(i);
                            case "address":
                                address = cv;
                                break;
                            case "customerName":
                                address2 = cv;
                                break;
                            case "city":
                                city = cv;
                                break;
                            case "country":
                                country = cv;
                                break;
                            case "postalCode":
                                postalCode = cv;
                                break;
                            case "phone":
                                phone = cv;
                                break;
                        }
                        
                        tempAddress = new Address(addressId, address, address2, city,
                            country, postalCode, phone);
                    }
                }
            }
        }   catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return tempAddress;
    }
    
    public ArrayList<Appointment> getCustomerAppointmentInfo(int tempUserId, 
            ArrayList<Customer> custList) throws ClassNotFoundException {
        ResultSet userAppointmentInfo = null;
        ArrayList<Appointment> appointments = new ArrayList<>();
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                PreparedStatement pStmt = null;
                String userAppointmentsQuery = "SELECT appointment.userId, customer.customerId, "
                        + "customer.customerName, appointment.title, appointment.description,"
                        + "appointment.location, appointment.contact, appointment.type,"
                        + "appointment.url, appointment.start, appointment.end "
                        + "FROM appointment INNER JOIN customer ON "
                        + "appointment.customerId = customer.customerId WHERE userId = ?;";
                pStmt = conn.prepareStatement(userAppointmentsQuery);
                pStmt.setInt(1,tempUserId);
                userAppointmentInfo = pStmt.executeQuery();
                
                while(userAppointmentInfo.next()){

                    int userId = 0;
                    int customerId = 0;
                    String title = "";
                    String description = "";
                    String location = "";
                    String contact = "";
                    String type = "";
                    String tempUrl = "";
                    Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));             
                    long offset = TimeZone.getDefault().getOffset(start.getTime().getTime());
                    System.out.println(offset);
                    
                    ResultSetMetaData rsMetaData = userAppointmentInfo.getMetaData();

                    for(int i = 1; i <= rsMetaData.getColumnCount(); i++){
                        String cv = userAppointmentInfo.getString(i);
                        System.out.println(cv);
                        switch(rsMetaData.getColumnName(i)){
                            case "userId":
                                userId = userAppointmentInfo.getInt(i);
                                break;
                            case "customerId":
                                customerId = userAppointmentInfo.getInt(i);
                                break;
                            case "title":
                                title = cv;
                                break;
                            case "description":
                                description = cv;
                                break;
                            case "locatoin":
                                location = cv;
                                break;
                            case "contact":
                                contact = cv;
                                break;
                            case "type":
                                type = cv;
                                break;
                            case "url":
                                tempUrl = cv;
                                break;
                            case "start":                                
                                start.setTimeInMillis(userAppointmentInfo.getTimestamp(i).getTime() + offset);
                                break;
                            case "end":
                                end.setTimeInMillis(userAppointmentInfo.getTimestamp(i).getTime() + offset);
                                break;
                        }
                    } 
                    
                    Appointment tempAppointment;
                    
                    for(int i = 0; i < custList.size(); i++){
                        if(customerId == custList.get(i).getCustomerId()) {
                            tempAppointment = new Appointment(userId, title, 
                                    custList.get(i), description, location, 
                                    contact, type, tempUrl, start, end);
                            appointments.add(tempAppointment);
                        }
                    }
                    
                } 
            }
        }  catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return appointments;
    }
    
    public Boolean addAppointment(Appointment appt, String ts, User currentUser) 
            throws ClassNotFoundException {
        Boolean added = false;
        int userId = appt.getUserId();
        int customerId = appt.getCustomer().getCustomerId();
        String title  = appt.getTitle();
        String description = appt.getDescription();
        String location = appt.getLocation();
        String contact = appt.getContact();
        String type = appt.getType();
        String tempUrl = appt.getUrl();
        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sqlDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        String startFormat = sqlDateFormat.format(appt.getStartTime().getTime());
        String endFormat = sqlDateFormat.format(appt.getEndTime().getTime());
        
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                            PreparedStatement pStmt = null;
                String customerStatement = "INSERT INTO appointment(customerId, "
                        + "userId, title, description, location, contact, "
                        + "type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pStmt = conn.prepareStatement(customerStatement);
                pStmt.setInt(1, customerId);
                pStmt.setInt(2, userId);
                pStmt.setString(3, title);
                pStmt.setString(4, description);
                pStmt.setString(5, location);
                pStmt.setString(6, contact);
                pStmt.setString(7, type);
                pStmt.setString(8, tempUrl);
                pStmt.setString(9, startFormat);
                pStmt.setString(10, endFormat);
                pStmt.setString(11, ts);
                pStmt.setString(12, currentUser.getUserName());
                pStmt.setString(13, ts);
                pStmt.setString(14, currentUser.getUserName());
                
                pStmt.executeUpdate();
                
                added = true;
                conn.close();
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }    
        return added;
    }
    
    public Boolean updateAppointment(Appointment curAppt, Appointment updateAppt, String ts, User currentUser) 
            throws ClassNotFoundException {
        Boolean updated = false;
        int apptId = 0;
        int userId = updateAppt.getUserId();
        int customerId = updateAppt.getCustomer().getCustomerId();
        String title  = updateAppt.getTitle();
        String description = updateAppt.getDescription();
        String location = updateAppt.getLocation();
        String contact = updateAppt.getContact();
        String type = updateAppt.getType();
        String tempUrl = updateAppt.getUrl();
        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sqlDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        String startFormat = sqlDateFormat.format(updateAppt.getStartTime().getTime());
        String endFormat = sqlDateFormat.format(updateAppt.getEndTime().getTime());
        String origStart = sqlDateFormat.format(curAppt.getStartTime().getTime());
        String origEnd = sqlDateFormat.format(curAppt.getEndTime().getTime());
        
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                apptId = getAppointmentId(conn, currentUser, curAppt, origStart, origEnd);
                if(apptId != 0){
                    PreparedStatement pStmt = null;
                    String customerStatement = "UPDATE appointment SET customerId = ?, "
                            + "userId = ?, title = ?, description = ?, location = ?, contact = ?, "
                            + "type = ?, url = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? "
                            + "WHERE appointmentId = ?";
                    pStmt = conn.prepareStatement(customerStatement);
                    pStmt.setInt(1, customerId);
                    pStmt.setInt(2, userId);
                    pStmt.setString(3, title);
                    pStmt.setString(4, description);
                    pStmt.setString(5, location);
                    pStmt.setString(6, contact);
                    pStmt.setString(7, type);
                    pStmt.setString(8, tempUrl);
                    pStmt.setString(9, startFormat);
                    pStmt.setString(10, endFormat);
                    pStmt.setString(11, ts);
                    pStmt.setString(12, currentUser.getUserName());
                    pStmt.setInt(13, apptId);
                    pStmt.executeUpdate();
                    
                    updated = true;
                }
                else {
                    System.out.println("Unable to update appointment with appointmentId: " + apptId);
                }
                conn.close();
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }    
        return updated;
    }
    
    public Boolean deleteAppointment(Appointment appt, User currentUser) 
            throws ClassNotFoundException {
        Boolean deleted = false;
        int appointmentId = 0;
        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sqlDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        String startFormat = sqlDateFormat.format(appt.getStartTime().getTime());
        String endFormat = sqlDateFormat.format(appt.getEndTime().getTime());  
        
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                appointmentId = getAppointmentId(conn, currentUser, appt, startFormat, endFormat);
                System.out.println(appointmentId);
                if(appointmentId != 0){
                    PreparedStatement pStmt = null;
                    String customerStatement = "DELETE FROM appointment WHERE "
                            + "appointmentId = ?";
                    pStmt = conn.prepareStatement(customerStatement);
                    pStmt.setInt(1, appointmentId);
                    pStmt.executeUpdate();
                    deleted = true;
                }
                System.out.println("delete executed: " + deleted);
                conn.close();
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return deleted;
    }
    
    public void addCustomer(Customer customer, String ts, 
            User currentUser) throws ClassNotFoundException {
        int countryId = 0;
        int cityId = 0;
        int addressId = 0;
        String customerName = customer.getCustomerName();
        String country = customer.getAddress().getCountry();
        String city = customer.getAddress().getCity();
        String address = customer.getAddress().getAddress();
        String address2 = customer.getAddress().getAddress2();
        String postalCode = customer.getAddress().getPostalCode();
        String phone = customer.getAddress().getPhoneNumber();
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                PreparedStatement pStmt = null;
                countryId = getCountryId(conn, country, ts, currentUser);
                cityId = getCityId(conn, city, countryId, ts, currentUser);
                addressId = getAddressId(conn, address, address2,
                        cityId, postalCode, phone, ts, currentUser);
                String customerStatement = "INSERT IGNORE INTO customer(customerName, "
                        + "addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                pStmt = conn.prepareStatement(customerStatement);
                pStmt.setString(1, customerName);
                pStmt.setInt(2, addressId);
                pStmt.setInt(3, 1);
                pStmt.setString(4, ts);
                pStmt.setString(5, currentUser.getUserName());
                pStmt.setString(6, ts);
                pStmt.setString(7, currentUser.getUserName());
                pStmt.executeUpdate();
                
                conn.close();
            }
            System.out.println("Add customer Executed");
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
    }
    
    public void updateCustomer(Customer customer, String ts, 
            User currentUser) throws ClassNotFoundException {
        int countryId = 0;
        int cityId = 0;
        int addressId = 0;
        String customerName = customer.getCustomerName();
        String country = customer.getAddress().getCountry();
        String city = customer.getAddress().getCity();
        String address = customer.getAddress().getAddress();
        String address2 = customer.getAddress().getAddress2();
        String postalCode = customer.getAddress().getPostalCode();
        String phone = customer.getAddress().getPhoneNumber();
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                PreparedStatement pStmt = null;
                countryId = getCountryId(conn, country, ts, currentUser);
                cityId = getCityId(conn, city, countryId, ts, currentUser);
                addressId = getAddressId(conn, address, address2,
                        cityId, postalCode, phone, ts, currentUser);
                
                String customerStatement = "UPDATE customer SET customerName = ?, "
                        + "addressId = ?, active = ?, lastUpdate = ?, lastUpdateBy = ? "
                        + "WHERE customerId = ?";
                pStmt = conn.prepareStatement(customerStatement);
                pStmt.setString(1, customerName);
                pStmt.setInt(2, addressId);
                pStmt.setInt(3, 1);
                pStmt.setString(4, ts);
                pStmt.setString(5, currentUser.getUserName());
                pStmt.setInt(6, customer.getCustomerId());
                pStmt.executeUpdate();
                
                conn.close();
            }
            System.out.println("Update Customer Executed");
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
    }
    
    public void deleteCustomerInfo(Customer customer) throws ClassNotFoundException {
        int customerId = 0;
        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                customerId = customer.getCustomerId();
                PreparedStatement pStmt = null;
                String delete = "DELETE FROM customer WHERE customerId = ?";
                pStmt = conn.prepareStatement(delete);
                pStmt.setInt(1, customerId);
                pStmt.executeUpdate();
                conn.close();
            }
            System.out.println("Delete Customer Executed");
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
    }
    
    public int getAppointmentId(Connection conn, User currentUser, Appointment appt,
            String startTime, String endTime)
            throws ClassNotFoundException {
        int appointmentId = 0;
        try {
            PreparedStatement pStmt = null;
            String appointmentIdQuery = "SELECT appointmentId FROM appointment WHERE "
                    + "customerId = ? AND userId = ? AND title = ? AND description = ?"
                    + "AND start = ? AND end = ?";
            pStmt = conn.prepareStatement(appointmentIdQuery);
            pStmt.setInt(1, appt.getCustomer().getCustomerId());
            pStmt.setInt(2, currentUser.getUserId());
            pStmt.setString(3, appt.getTitle());
            pStmt.setString(4, appt.getDescription());
            pStmt.setString(5, startTime);
            pStmt.setString(6, endTime);
            ResultSet appointmentIdResult = pStmt.executeQuery();
            
            if(appointmentIdResult.next()){
                appointmentId = appointmentIdResult.getInt(1);
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }        
        return appointmentId;       
    }
    
    public int getCountryId(Connection conn, String country, String ts, User currentUser) 
            throws ClassNotFoundException {
        int countryId = 0;
        try {
            PreparedStatement pStmt = null;
            String countryIdQuery = "SELECT countryId FROM country WHERE country = ?";
            pStmt = conn.prepareStatement(countryIdQuery);
            pStmt.setString(1, country);
            ResultSet countryIdResult = pStmt.executeQuery();
            
            if(countryIdResult.next()){
                countryId = countryIdResult.getInt(1);
            }
            else {
                String insertCountryStatement = "INSERT INTO country (country, "
                        + "createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, ?, ?)";
                pStmt = conn.prepareStatement(insertCountryStatement);
                pStmt.setString(1, country);
                pStmt.setString(2, ts);
                pStmt.setString(3, currentUser.getUserName());
                pStmt.setString(4, ts);
                pStmt.setString(5, currentUser.getUserName());
                pStmt.executeUpdate();
                
                countryId = getCountryId(conn, country, ts, currentUser);
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return countryId;
    }
    
    public int getCityId(Connection conn, String city, int countryId, String ts, User currentUser)
            throws ClassNotFoundException {
        ResultSet cityIdResult = null;
        int cityId = 0;
        try {
            PreparedStatement pStmt = null;
            String cityIdQuery = "SELECT cityId FROM city WHERE city = ? AND countryId = ?";
            pStmt = conn.prepareStatement(cityIdQuery);
            pStmt.setString(1, city);
            pStmt.setInt(2, countryId);
            cityIdResult = pStmt.executeQuery();
            
            if(cityIdResult.next()){
                    cityId = cityIdResult.getInt(1);
                }
            else{
                String cityStatement = "INSERT INTO city(city, countryId, "
                        + "createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                pStmt = conn.prepareStatement(cityStatement);
                pStmt.setString(1, city);
                pStmt.setInt(2, countryId);
                pStmt.setString(3, ts);
                pStmt.setString(4, currentUser.getUserName());
                pStmt.setString(5, ts);
                pStmt.setString(6, currentUser.getUserName());
                pStmt.executeUpdate();

                cityId = getCityId(conn, city, countryId, ts, currentUser);
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return cityId;
    }
    
    public int getAddressId(Connection conn, String address, String address2,
            int cityId, String postalCode, String phone, String ts, User currentUser) 
            throws ClassNotFoundException {
        ResultSet addressIdResult = null;
        int addressId = 0;
        try {
            PreparedStatement pStmt = null;
            String addressIdQuery = "SELECT addressId FROM address WHERE address = ? "
                        + "AND address2 = ? AND cityId = ? AND postalCode = ? AND phone = ?";
            pStmt = conn.prepareStatement(addressIdQuery);
            pStmt.setString(1, address);
            pStmt.setString(2, address2);
            pStmt.setInt(3, cityId);
            pStmt.setString(4, postalCode);
            pStmt.setString(5, phone);
            addressIdResult = pStmt.executeQuery();
            
            if(addressIdResult.next()){
                    addressId = addressIdResult.getInt(1);
                }
            else {
                String addressStatement = "INSERT INTO address(address, address2,"
                        + "cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pStmt = conn.prepareStatement(addressStatement);
                pStmt.setString(1, address);
                pStmt.setString(2, address2);
                pStmt.setInt(3, cityId);
                pStmt.setString(4, postalCode);
                pStmt.setString(5, phone);
                pStmt.setString(6, ts);
                pStmt.setString(7, currentUser.getUserName());
                pStmt.setString(8, ts);
                pStmt.setString(9, currentUser.getUserName());
                pStmt.executeUpdate();

                addressId = getAddressId(conn, address, address2, cityId, postalCode, phone, ts, currentUser);
            }
        } catch (SQLException e){
            System.out.println("There was an error: " + e);
        }
        return addressId;
    }
}

