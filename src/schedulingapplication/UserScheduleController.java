/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.RowConstraints;

/**
 * FXML Controller class
 *
 * @author snobo
 */
public class UserScheduleController implements Initializable {
 
    @FXML
    private TableView<Customer> custTableView;
    @FXML
    private TableColumn custNameCol;
    @FXML
    private GridPane calendarGridPane;
    @FXML
    private Button changeViewBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button deleteBtn;
    
    private DatabaseConn db = null;
    private User currentUser = null;
    private ArrayList<Customer> customerList = null;
    private Appointment selectedAppointment = null;
    private Boolean weeklyCalendar = false;
    private ArrayList<Label> apptLabelList = null;
    //private ArrayList<Appointment> appointments = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up Appointment Table Columns
        setCustomerTableColumns();
    }    
    
    @FXML
    private void changeView() throws ParseException {
        if(calendarGridPane.getRowConstraints().size() > 1) {
            setWeeklyCalendar();
            changeViewBtn.setText("Monthly");
        }
        else if(calendarGridPane.getRowConstraints().size() <= 1) {
            setMonthlyCalendar();
            changeViewBtn.setText("Weekly");
        }
    }
    
    @FXML
    private void updateCustomerInfo() throws IOException {
        if(custTableView.getSelectionModel().getSelectedItem() != null) {
            Customer cust = custTableView.getSelectionModel().getSelectedItem();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInfo.fxml"));
            Stage stage = new Stage();

            Scene scene = new Scene(loader.load());

            UserInfoController userInfoController = loader.<UserInfoController>getController();

            userInfoController.setDb(this.db);
            userInfoController.setCurrentUser(this.currentUser);
            userInfoController.setCurrentCustomer(cust);
            userInfoController.setCustomerInfoButton("edit");    
            userInfoController.setUserScheduleController(this);

            stage.setScene(scene);
            stage.show();
        }
    }
    
    @FXML 
    private void addCustomerInfo() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInfo.fxml"));
        Stage stage = new Stage();

        Scene scene = new Scene(loader.load());

        UserInfoController userInfoController = loader.<UserInfoController>getController();

        userInfoController.setDb(this.db);
        userInfoController.setCurrentUser(this.currentUser);
        userInfoController.setCustomerInfoButton("add");
        userInfoController.setUserScheduleController(this);
        
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private void addAppointment() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment.fxml"));
        Stage stage = new Stage();

        Scene scene = new Scene(loader.load());

        AppointmentController appointmentController = loader.<AppointmentController>getController();

        appointmentController.setDb(this.db);
        appointmentController.setCurrentUser(this.currentUser);
        appointmentController.setAppointmentList(this.currentUser.getUserScheudle());
        appointmentController.setCustomerList(this.customerList);
        appointmentController.setUserScheduleController(this);
        
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private void editAppointment() throws IOException {
        try {
            openAppointment(selectedAppointment);
        } catch (IOException ex) {
            Logger.getLogger(UserScheduleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML 
    private void deleteAppointment() throws ClassNotFoundException, ParseException{
        db.deleteAppointment(selectedAppointment, currentUser);
        currentUser.getUserScheudle().remove(selectedAppointment);
        if(this.weeklyCalendar == false) {
            setMonthlyCalendar();
        }
        else {
            setWeeklyCalendar();
        }
    }
    
    @FXML
    private void generateAppointmentTypeReport() throws UnsupportedEncodingException, IOException {
        System.out.println("generating report...");
        
        File appointmentTypeReport = new File("AppointmentTypeReport.txt");
        
        ArrayList<String> apptTypes = new ArrayList<>(); 
        
        for(Appointment appt : currentUser.getUserScheudle()){
            if(!apptTypes.contains(appt.getType()) && 
                    appt.getStartTime().get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)){
                apptTypes.add(appt.getType());                
            }
        }
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("AppointmentTypeReport.txt"), "utf-8"))) { 
                writer.write("The are " + apptTypes.size() +  " appointments this month \r\n");
                for(String type : apptTypes) {
                    writer.write(type +"\r\n");
                }
                    
            }
        System.out.println("Created Appointment Type Report");
    }        
    
    @FXML
    private void generateConsultantScheduleReport() throws UnsupportedEncodingException, IOException {
        System.out.println("generating report...");
        
        File consultantScheduleReport = new File("ConsultantScheduleReport.txt");
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("ConsultantScheduleReport.txt"), "utf-8"))) { 
                for(Appointment appt : currentUser.getUserScheudle()) {
                    writer.write(appt.getTitle() + " " + appt.getStartTime().getTime()
                            + " - " + appt.getEndTime().getTime() +"\r\n");
                }
                    
            }
        System.out.println("Created Consultant Schedule Report");
    }
    
    @FXML
    private void generateCustomerAddressReport() throws UnsupportedEncodingException, IOException {
        System.out.println("generating report...");
        
        File customerAddressReport = new File("CustomerAddressReport.txt");
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("CustomerAddressReport.txt"), "utf-8"))) { 
                for(Customer cust : this.customerList) {
                    writer.write(cust.getCustomerName() + "\r\n" + cust.getAddress().getFullAddress()
                            + "\r\n\r\n");
                }
                    
            }
        System.out.println("Created Customer Address Report");
    }
    
    private void setCustomerTableColumns() {
        custNameCol.setCellValueFactory(
                new PropertyValueFactory<>("customerName"));
    }

    public void setUserData() throws NumberFormatException, ParseException {
        //Call database connection and getUserAppointmentInfo(int tempUserId)
        //To get the current user's appointment schedule
        setMonthlyCalendar();
    }
    
    public void setCustomerData() throws ParseException {      
        custTableView.setItems(FXCollections.observableArrayList(this.customerList));
    }
    
    public void setWeeklyCalendar() throws ParseException {
        
        while(calendarGridPane.getRowConstraints().size() > 0){
            calendarGridPane.getRowConstraints().remove(0);
        }
        
        calendarGridPane.getChildren().clear();
        
        RowConstraints rc = new RowConstraints(130);
        calendarGridPane.getRowConstraints().add(0, rc);
        
        Calendar weekStart = Calendar.getInstance();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        
        int month = weekStart.get(Calendar.MONTH);
        int year = weekStart.get(Calendar.YEAR);
        int weekStartDate = weekStart.get(Calendar.DAY_OF_MONTH);
                
        Label[][] dateArray = new Label[7][1];
        VBox[][] containerArray = new VBox[7][1]; 
        apptLabelList = new ArrayList();
        
        for(int i = 0; i < 1; i++) {
            for(int j = 0; j < 7; j++) {
                
                containerArray[j][i] = new VBox();
                if(j == 0){
                    containerArray[j][i].setStyle( "-fx-border-color: black;\n" +
                                            "-fx-border-style: solid, solid, solid, solid;\n");
                }
                else if(j+1 == 7){
                    containerArray[j][i].setStyle("-fx-border-color: black;\n" +
                                            "-fx-border-style: solid, hidden, solid, solid;\n");
                }   
                else{
                    containerArray[j][i].setStyle("-fx-border-color: black;\n" +
                                            "-fx-border-style: solid, hidden, solid, solid;\n");
                }
                dateArray[j][i] = new Label(String.valueOf(weekStartDate));
                calendarGridPane.add(containerArray[j][i], j, i);
                ArrayList<Appointment> appts = getDailyAppointments(weekStartDate, month, year);
                containerArray[j][i].getChildren().add(dateArray[j][i]);
                if(appts.size() > 0){
                    for(Appointment tempAppt : appts){
                        DateFormat df = new SimpleDateFormat("HH:mm:ss");
                        Label tempLabel = new Label(tempAppt.getTitle());
                        tempLabel.setTooltip(new Tooltip("Start time: " + 
                                df.format(tempAppt.getStartTime().getTime())));
                        apptLabelList.add(tempLabel);
                        
                        tempLabel.setOnMouseReleased(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                //sets each labels backkground color witout having on each label in the liist
                                //prevents the necessity of running a full loop
                                apptLabelList.forEach((lbl) -> {
                                    lbl.setStyle("-fx-background-color: white;");
                                });
                                tempLabel.setStyle("-fx-background-color: gray;");
                                selectedAppointment = tempAppt;
                            }
                        }); 
                        containerArray[j][i].getChildren().add(tempLabel);
                        //containerArray[j][i].getChildren().get(k);
                    }
                }
                if(weekStartDate == weekStart.getActualMaximum(Calendar.DATE)){
                    weekStartDate = 0;
                }
                weekStartDate++;
            }    
        }
        weeklyCalendar = true;
    }
    
    public void setMonthlyCalendar() throws ParseException {
        RowConstraints rc = new RowConstraints(120);
        while(calendarGridPane.getRowConstraints().size() < 5) {
            calendarGridPane.getRowConstraints().add(0, rc);
        }
        
        calendarGridPane.getChildren().clear();
        
        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        
        int monthStartDayOfWeek = monthStart.get(Calendar.DAY_OF_WEEK);
        int month = monthStart.get(Calendar.MONTH);
        int year = monthStart.get(Calendar.YEAR);
        int dayCounter = 1;
        
        Label[][] dateArray = new Label[7][5];
        VBox[][] containerArray = new VBox[7][5]; 
        apptLabelList = new ArrayList();
        
        for(int i = 0; i < 5; i++) {
            int j = 0;
            for(j = 0; j < 7; j++) {
                if(i == 0 && j == 0)
                    j = monthStartDayOfWeek - 1;
                
                if(dayCounter > monthStart.getActualMaximum(Calendar.DAY_OF_MONTH))
                    break;
                    
                containerArray[j][i] = new VBox();
                if(j == 0 && i == 0){
                    containerArray[j][i].setStyle( "-fx-border-color: black;" +
                                            "-fx-border-style: solid, solid, solid, solid;");
                }
                else if(j != 0 && i == 0){
                    containerArray[j][i].setStyle("-fx-border-color: black;" +
                                            "-fx-border-style: solid, hidden, solid, solid;");
                }   
                else if(j == 0 && i != 0){
                    containerArray[j][i].setStyle("-fx-border-color: black;" +
                                            "-fx-border-style: hidden, solid, solid, solid;\n");
                }
                else if(j != 0 && i != 0){
                    containerArray[j][i].setStyle("-fx-border-color: black;\n" +
                                            "-fx-border-style: hidden, hidden, solid, solid;\n");
                }
                dateArray[j][i] = new Label(String.valueOf(dayCounter));
                calendarGridPane.add(containerArray[j][i], j, i);
                containerArray[j][i].getChildren().add(dateArray[j][i]);
                ArrayList<Appointment> appts = getDailyAppointments(dayCounter, month, year);
                if(appts.size() > 0){
                    for(Appointment tempAppt : appts){
                        Instant fifteenMinute = tempAppt.getStartTime().toInstant().minusSeconds(900);
                        Instant appointment = tempAppt.getStartTime().toInstant();
                        
                        if(appointment.isAfter(Instant.now()) && Instant.now().isAfter(fifteenMinute)) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Appointment Soon");
                            alert.setContentText("It seems you have an appointment "
                                    + "withing the next fifteen minutes!");
                            alert.showAndWait();
                        } 
                        
                        DateFormat df = new SimpleDateFormat("HH:mm:ss");
                        Label tempLabel = new Label(tempAppt.getTitle());
                        tempLabel.setTooltip(new Tooltip("Start time: " + 
                                df.format(tempAppt.getStartTime().getTime())));
                        apptLabelList.add(tempLabel);
                        
                        tempLabel.setOnMouseReleased(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                apptLabelList.forEach((lbl) -> {
                                    lbl.setStyle("-fx-background-color: white;");
                                });
                                tempLabel.setStyle("-fx-background-color: gray;");
                                selectedAppointment = tempAppt;
                            }
                        });
                        
                        containerArray[j][i].getChildren().add(tempLabel);
                        //containerArray[j][i].getChildren().get(k);
                    }
                }
                dayCounter++;
            }
            if(dayCounter > monthStart.getActualMaximum(Calendar.DAY_OF_MONTH))
                    break;    
        }
        weeklyCalendar = false;
    }
    
    public ArrayList<Appointment> getDailyAppointments(int day, int month, int year) throws ParseException{
        ArrayList<Appointment> dailyAppointments = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        ArrayList<Appointment> appointments = currentUser.getUserScheudle();
        date.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(Appointment appt : appointments){
//            System.out.println("Date to compare to: " + dateFormat.parse(dateFormat.format(
//                    appointments.get(i).getStartTime())));
            if(dateFormat.parse(dateFormat.format(appt.getStartTime().getTime())).compareTo(
                    dateFormat.parse(dateFormat.format(date.getTime()))) == 0)
                dailyAppointments.add(appt);
        }
        
        return dailyAppointments;
    }
    
    public void addCustomer(Customer cust) throws ParseException {
        this.customerList.add(cust);
        this.setCustomerData();
        //refreshCustomerTable();
    }
    
    public void removeCustomer(Customer cust) throws ParseException {
        this.customerList.remove(cust);
        this.setCustomerData();
        //refreshCustomerTable();
    }
    
    public void setDb(DatabaseConn tempDb) {
        this.db = tempDb;
    }
    
    public void setCurrentUser(User tempUser) throws NumberFormatException, ParseException {
        this.currentUser = tempUser;
        setUserData();
    }
    
    public void setCustomerList(ArrayList<Customer> tempList) throws ParseException {
        this.customerList = tempList;
        setCustomerData();
    }
    
    public void openAppointment(Appointment appt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Appointment.fxml"));
        Stage stage = new Stage();

        Scene scene = new Scene(loader.load());

        AppointmentController appointmentController = loader.<AppointmentController>getController();

        appointmentController.setDb(this.db);
        appointmentController.setCurrentUser(this.currentUser);
        appointmentController.setAppointmentList(this.currentUser.getUserScheudle());
        appointmentController.setCustomerList(this.customerList);
        appointmentController.setAppointment(appt);
        
        stage.setScene(scene);
        stage.show();
    }
    
    public void refreshCustomerTable() {
        System.out.println("refreshing customer Table");
        this.custTableView.refresh();
    }
    
    public Boolean getWeeklyCalendarValue() {
        return this.weeklyCalendar;
    }
}
