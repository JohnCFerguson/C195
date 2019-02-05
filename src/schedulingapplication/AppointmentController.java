/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapplication;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

class OutsideBusinessHoursException extends Exception {}

/**
 * FXML Controller class
 *
 * @author snobo
 */
public class AppointmentController implements Initializable {
    
    @FXML
    private TextField apptNameTextBox;
    @FXML
    private ComboBox customerComboBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField contactTextBox;
    @FXML
    private TextField locationTextBox;
    @FXML
    private TextField typeTextBox;
    @FXML
    private TextField urlTextBox;
    @FXML
    private DatePicker startDayPicker;
    @FXML
    private DatePicker endDayPicker;
    @FXML
    private TextField endTimeTextBox;
    @FXML
    private TextField startTimeTextBox;
    @FXML
    private Button cancelBtn;
    
    private DatabaseConn db = null;
    private Customer cust = null;
    private User currentUser = null;
    private ArrayList<Customer> customerList = null;
    private ArrayList<Appointment> appointmentList = null;
    private Appointment appointment = null;
    private UserScheduleController scheduleController = null;
    private LocalTime businessStart = LocalTime.of(9, 0);
    private LocalTime businessEnd = LocalTime.of(17, 0);
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    public void addAppointment() throws ClassNotFoundException, ParseException {
        try {
            if(this.startTimeTextBox.getText().length() == 5 && this.startTimeTextBox.getText().contains(":") 
                    && this.endTimeTextBox.getText().length() == 5 && this.endTimeTextBox.getText().contains(":")) {
                Calendar now = Calendar.getInstance();
                SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sqlDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
                String createDate = sqlDateFormat.format(now.getTime());

                LocalDate startDay = this.startDayPicker.getValue();
                LocalDate endDay = this.endDayPicker.getValue();
                int startHour = Integer.parseInt(this.startTimeTextBox.getText(0,2));
                int startMin = Integer.parseInt(this.startTimeTextBox.getText(3,5));
                int endHour = Integer.parseInt(this.endTimeTextBox.getText(0,2));
                int endMin = Integer.parseInt(this.endTimeTextBox.getText(3,5));

                int userId = this.currentUser.getUserId();
                String title = this.apptNameTextBox.getText();
                String description = this.descriptionTextArea.getText();
                String location = this.locationTextBox.getText();
                String contact = this.contactTextBox.getText();
                String type = this.typeTextBox.getText();
                String tempUrl = this.urlTextBox.getText();
                Calendar startDate = Calendar.getInstance();
                startDate.set(startDay.getYear(), startDay.getMonthValue()-1, 
                        startDay.getDayOfMonth(), startHour, startMin, 00);
                Calendar endDate = Calendar.getInstance();
                endDate.set(endDay.getYear(), endDay.getMonthValue()-1, 
                        endDay.getDayOfMonth(), endHour, endMin, 00);

                if(startDate.get(Calendar.HOUR_OF_DAY) < businessStart.getHour() 
                        || startDate.get(Calendar.HOUR_OF_DAY) > businessEnd.getHour()) {
                    throw new OutsideBusinessHoursException();
                }
                
                System.out.println(startDay.getYear());
                System.out.println(startDay.getMonthValue());
                System.out.println(startDay.getDayOfMonth());

                System.out.println(startDate.getTime());
                System.out.println(endDate.getTime());

                /*int tempUserId, String tempTitle, Customer tempCustomer, 
                String tempDesc, String tempLoc, String tempContact, String tempType,
                String tempUrl, Calendar tempStart, Calendar tempEnd*/


                Appointment appt = new Appointment(userId, title, this.cust, description, 
                        location, contact, type, tempUrl, startDate, endDate);

                Boolean overlap = checkOverlap(appt);
                if(overlap == false){
                    db.addAppointment(appt, createDate, currentUser);

                    this.currentUser.addAppointment(appt);
                    if(this.scheduleController.getWeeklyCalendarValue() == false) {
                        this.scheduleController.setMonthlyCalendar();
                    }
                    else if(this.scheduleController.getWeeklyCalendarValue() == true) {
                        this.scheduleController.setWeeklyCalendar();
                    }
                    cancel();
                }
                else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Overlapping Appointments");
                    alert.setContentText("It seems you already have an appointment "
                            + "scheduled at this time. Please reschedule.");

                    alert.showAndWait();
                }            
            }
            else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Improper Time Format");
                alert.setHeaderText("Improper Time Format");
                alert.setContentText("It seems your time has been improperly formatted, "
                        + "Please adjust this and try again.");

                alert.showAndWait();
            }
        }
        catch(OutsideBusinessHoursException obhs) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Outside Business Hours");
            alert.setContentText("It seems you are trying to shcedule an appointment "
                    + "outside of business hours. Please reschedule.");
            alert.showAndWait();
        }
    }
    
    @FXML 
    public void updateAppointment() throws ClassNotFoundException, ParseException {
        try {
            if(this.startTimeTextBox.getText().length() == 5 && this.startTimeTextBox.getText().contains(":") 
                    && this.endTimeTextBox.getText().length() == 5 && this.endTimeTextBox.getText().contains(":")) {
                Calendar now = Calendar.getInstance();
                SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sqlDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
                String createDate = sqlDateFormat.format(now.getTime());

                LocalDate startDay = this.startDayPicker.getValue();
                LocalDate endDay = this.endDayPicker.getValue();
                int startHour = Integer.parseInt(this.startTimeTextBox.getText(0,2));
                int startMin = Integer.parseInt(this.startTimeTextBox.getText(3,5));
                int endHour = Integer.parseInt(this.endTimeTextBox.getText(0,2));
                int endMin = Integer.parseInt(this.endTimeTextBox.getText(3,5));

                int userId = this.currentUser.getUserId();
                String title = this.apptNameTextBox.getText();
                String description = this.descriptionTextArea.getText();
                String location = this.locationTextBox.getText();
                String contact = this.contactTextBox.getText();
                String type = this.typeTextBox.getText();
                String tempUrl = this.urlTextBox.getText();
                Calendar startDate = Calendar.getInstance();
                startDate.set(startDay.getYear(), startDay.getMonthValue()-1, 
                        startDay.getDayOfMonth(), startHour, startMin, 00);
                Calendar endDate = Calendar.getInstance();
                endDate.set(endDay.getYear(), endDay.getMonthValue()-1, 
                        endDay.getDayOfMonth(), endHour, endMin, 00);
                
                if(startDate.get(Calendar.HOUR_OF_DAY) < businessStart.getHour() 
                        || startDate.get(Calendar.HOUR_OF_DAY) > businessEnd.getHour()) {
                    throw new OutsideBusinessHoursException();
                }

                System.out.println(startDay.getYear());
                System.out.println(startDay.getMonthValue());
                System.out.println(startDay.getDayOfMonth());

                System.out.println(startDate.getTime());
                System.out.println(endDate.getTime());

                Appointment appt = new Appointment(userId, title, this.cust, description, 
                        location, contact, type, tempUrl, startDate, endDate);

                Boolean overlap = checkOverlap(appt);

                if(overlap == false){
                    this.appointment.setContact(contact);
                    this.appointment.setCustomer(this.cust);
                    this.appointment.setDescription(description);
                    this.appointment.setEndTime(endDate);
                    this.appointment.setLocation(location);
                    this.appointment.setStartTime(startDate);
                    this.appointment.setTitle(title);
                    this.appointment.setType(type);
                    this.appointment.setUrl(tempUrl);
                    this.appointment.setUserId(userId);

                    db.updateAppointment(this.appointment, createDate, currentUser);

                    this.currentUser.addAppointment(appt);
                    if(this.scheduleController.getWeeklyCalendarValue() == false) {
                        this.scheduleController.setMonthlyCalendar();
                    }
                    else if(this.scheduleController.getWeeklyCalendarValue() == true) {
                        this.scheduleController.setWeeklyCalendar();
                    }
                    cancel();
                }
                else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Overlapping Appointments");
                    alert.setContentText("It seems you already have an appointment "
                            + "scheduled at this time. Please reschedule.");

                    alert.showAndWait();
                }
            }
            else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Improper Time Format");
                alert.setContentText("It seems your time has been improperly formatted, "
                        + "Please adjust this and try again.");
                alert.showAndWait();
            }
        }
        catch(OutsideBusinessHoursException obhs) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Outside Business Hours");
            alert.setContentText("It seems you are trying to shcedule an appointment "
                    + "outside of business hours. Please reschedule.");
            alert.showAndWait();
        }
    }
    
    @FXML
    public void cancel() {
        Stage IMS = (Stage) cancelBtn.getScene().getWindow();
        
        IMS.close();
    }
    
    private boolean checkOverlap(Appointment appt) {
        boolean overlap = false; 
        for(Appointment tempAppt : appointmentList){
            System.out.println("appt: " + appt.getStartTime().toInstant());
            System.out.println("other appt: " + tempAppt.getStartTime().toInstant());
            if(appt.getStartTime().toInstant().isBefore(tempAppt.getStartTime().toInstant())
                    && appt.getStartTime().toInstant().isAfter(tempAppt.getEndTime().toInstant())){
                System.out.println("Oerlapping appointment");
                overlap = true;
            }
        }
        System.out.println("Overlap is: " + overlap);
        return overlap;
    }
    
    public void setDb(DatabaseConn tempConn) {
        this.db = tempConn;
    } 
    
    public void setCurrentUser(User tempUser){
        this.currentUser = tempUser;
    }
    
    public void setCustomerList(ArrayList<Customer> tempList) {
        this.customerList = tempList;
        
        setCustomerComboBox();
    }
    
    public void setAppointmentList(ArrayList<Appointment> tempList) {
        this.appointmentList = tempList;
    }
    
    public void setAppointment(Appointment tempAppt) {
        this.appointment = tempAppt;
        this.cust = tempAppt.getCustomer();
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
                
        this.apptNameTextBox.setText(tempAppt.getTitle());
        this.contactTextBox.setText(tempAppt.getContact());
        this.customerComboBox.setValue(tempAppt.getCustomer().getCustomerName());
        this.locationTextBox.setText(tempAppt.getLocation());
        this.typeTextBox.setText(tempAppt.getType());
        this.startDayPicker.setValue(LocalDate.of(tempAppt.getStartTime().get(Calendar.YEAR),
                tempAppt.getStartTime().get(Calendar.MONTH)+1, tempAppt.getStartTime().get(Calendar.DATE)));
        this.endDayPicker.setValue(LocalDate.of(tempAppt.getEndTime().get(Calendar.YEAR),
                tempAppt.getEndTime().get(Calendar.MONTH)+1, tempAppt.getEndTime().get(Calendar.DATE)));
        this.urlTextBox.setText(tempAppt.getUrl());
        this.descriptionTextArea.setText(tempAppt.getDescription());
        String apptTime = df.format(tempAppt.getStartTime().getTime());
        this.startTimeTextBox.setText(apptTime);
        apptTime = df.format(tempAppt.getEndTime().getTime());
        this.endTimeTextBox.setText(apptTime);
        
    }
    
    public void setCustomerComboBox() {
        ObservableList<String> cbData = FXCollections.observableArrayList(); 
        
        //lamda allows setting the combo box with each customer. 
        //basically calls a function to accomplish this behavioir
        //do not have to write out a full loop
        this.customerList.forEach(customer -> {
            cbData.add(customer.getCustomerName());
        });
        
        customerComboBox.setItems(cbData);
        
        customerComboBox.getSelectionModel().selectedItemProperty().addListener( 
                (options, oldValue, newValue) -> {
           System.out.println(newValue);
           for(Customer customer : this.customerList){
               if(newValue.equals(customer.getCustomerName())){
                   this.cust = customer;
                   break;
               }
           }
        }); 
    }
    
    public void setUserScheduleController(UserScheduleController tempController) {
        this.scheduleController = tempController;
    }
}
