/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapplication;

import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author snobo
 */
public class UserInfoController implements Initializable {
    
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField nameTextBox;
    @FXML
    private TextField addressTextBox;
    @FXML 
    private TextField address2TextBox;
    @FXML
    private TextField zipTextBox;
    @FXML 
    private TextField phoneTextBox;
    @FXML
    private TextField cityTextBox;
    @FXML
    private TextField countryTextBox;
    @FXML
    private Button addCustomerBtn;
    @FXML
    private Button deleteCustomerBtn;
    @FXML
    private Button updateCustomerBtn;
    
    private DatabaseConn db = null;
    private User currentUser = null;
    private Customer currentCustomer = null;
    private UserScheduleController scheduleController = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    private void addCustomer() throws ClassNotFoundException, ParseException {
        //creates a new customer object and adds this to the database, then updates
        //the customer list which refreshes the customer table
        if(addressTextBox.getText().length() != 0 && !addressTextBox.getText().equals(" ") &&
                nameTextBox.getText().length() != 0 && !nameTextBox.getText().equals(" ") &&
                address2TextBox.getText().length() != 0 && !address2TextBox.getText().equals(" ") &&
                cityTextBox.getText().length() != 0 && !cityTextBox.getText().equals(" ") &&
                countryTextBox.getText().length() != 0 && !countryTextBox.getText().equals(" ") &&
                zipTextBox.getText().length() != 0 && !zipTextBox.getText().equals(" ")){
            Calendar currentTime = Calendar.getInstance();
            SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createDate = sqlDateFormat.format(currentTime.getTime());
            Address tempAddress = new Address(0, addressTextBox.getText(), address2TextBox.getText(),
                    cityTextBox.getText(), countryTextBox.getText(), zipTextBox.getText(),
                    phoneTextBox.getText());
            Customer tempCust = new Customer(0, nameTextBox.getText(), tempAddress, 1);

            System.out.println("Country: " + tempCust.getAddress().getCountry());
            System.out.println("City: " + tempCust.getAddress().getCity());
            System.out.println("Postal Code: " + tempCust.getAddress().getPostalCode());

            db.addCustomer(tempCust, createDate, currentUser);
            this.scheduleController.addCustomer(tempCust);
            this.scheduleController.refreshCustomerTable();
            cancel();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fields Empty");
            alert.setContentText("It seems you have not filled out all the appropriate fields, "
                    + "please adjust this and try again.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void updateCustomerInfo() throws ClassNotFoundException {
        /*String customerName, String address, String address2, 
            String zip, String city, String country, String phone, String userName,
            String password, String ts, User currentUser*/
        if(addressTextBox.getText().length() != 0 && !addressTextBox.getText().equals(" ") &&
            nameTextBox.getText().length() != 0 && !nameTextBox.getText().equals(" ") &&
            address2TextBox.getText().length() != 0 && !address2TextBox.getText().equals(" ") &&
            cityTextBox.getText().length() != 0 && !cityTextBox.getText().equals(" ") &&
            countryTextBox.getText().length() != 0 && !countryTextBox.getText().equals(" ") &&
            zipTextBox.getText().length() != 0 && !zipTextBox.getText().equals(" ")){
            Calendar currentTime = Calendar.getInstance();
            SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createDate = sqlDateFormat.format(currentTime.getTime());
            currentCustomer.setCustomerName(nameTextBox.getText());
            currentCustomer.getAddress().setAddress(addressTextBox.getText(), address2TextBox.getText());
            currentCustomer.getAddress().setPostalCode(zipTextBox.getText());
            currentCustomer.getAddress().setCity(cityTextBox.getText());
            currentCustomer.getAddress().setCountry(countryTextBox.getText());
            currentCustomer.getAddress().setPhoneNumber(phoneTextBox.getText());

            db.updateCustomer(currentCustomer, createDate, currentUser);
            this.scheduleController.refreshCustomerTable();
            cancel();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fields Empty");
            alert.setContentText("It seems you have not filled out all the appropriate fields, "
                    + "please adjust this and try again.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void deleteCustomer() throws ClassNotFoundException, ParseException {
        //deletes customer from database
        db.deleteCustomerInfo(currentCustomer);
        this.scheduleController.removeCustomer(currentCustomer);
        this.scheduleController.refreshCustomerTable();
        cancel();
    }
    
    @FXML
    private void cancel() {
        Stage currentStage = (Stage) cancelBtn.getScene().getWindow();
        currentStage.close();
    }
    
    public void setCustomerInfoButton(String custInfoType) {
        switch(custInfoType) {
            case "add":
                this.addCustomerBtn.setVisible(true);
                this.updateCustomerBtn.setVisible(false);
                this.deleteCustomerBtn.setVisible(false);
                break;
            case "edit":
                this.updateCustomerBtn.setVisible(true);
                this.deleteCustomerBtn.setVisible(true);
                this.addCustomerBtn.setVisible(false);
                break;
        }
    }
    
    public void setDb(DatabaseConn tempDb) {
        this.db = tempDb;
    }
    
    public void setCurrentUser(User tempUser) {
        this.currentUser = tempUser;
    }
    
    public void setCurrentCustomer(Customer tempCust) {
        this.currentCustomer = tempCust;
        setCustomerInfo();
    }
    
    public void setCustomerInfo() {
        this.nameTextBox.setText(currentCustomer.getCustomerName());
        this.addressTextBox.setText(currentCustomer.getAddress().getAddress());
        this.address2TextBox.setText(currentCustomer.getAddress().getAddress2());
        this.cityTextBox.setText(currentCustomer.getAddress().getCity());
        this.countryTextBox.setText(currentCustomer.getAddress().getCountry());
        this.zipTextBox.setText(currentCustomer.getAddress().getPostalCode());
        this.phoneTextBox.setText(currentCustomer.getAddress().getPhoneNumber());
    }
    
    public void setUserScheduleController(UserScheduleController schedController) {
        this.scheduleController = schedController;
    }
}
