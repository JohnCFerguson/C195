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
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author snobo
 */
public class LoginController implements Initializable {
    
    @FXML
    private Label invalidLogin;
    @FXML 
    private TextField userNameText;
    @FXML 
    private TextField passwordText;
    @FXML
    private Button signInBtn;
    @FXML
    private Label usernameLbl;
    @FXML
    private Label passwordLbl;
    
    DatabaseConn db;
    
    Locale loc = Locale.getDefault();
    ResourceBundle failedLoginMessage = ResourceBundle.getBundle(
        "schedulingapplication/translationBundles/LoginMessagesBundle", loc);

    User currentUser = null;
    ArrayList<Customer> customerList= null;
    
    @FXML
    private void signIn(ActionEvent event) throws ClassNotFoundException, IOException {
        
        try{
            customerList = db.getCustomerInfo();
            currentUser = db.signIn(userNameText.getText(), passwordText.getText(),
                    customerList);  
            
            //System.out.println(currentUser.getStatus());
            
            if(currentUser.getStatus() == 1){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("UserSchedule.fxml"));
                Stage stage = new Stage();

                Scene scene = new Scene(loader.load());

                UserScheduleController userController = loader.<UserScheduleController>getController();

                userController.setDb(this.db);
                userController.setCustomerList(this.customerList);
                userController.setCurrentUser(this.currentUser);

                stage.setScene(scene);
                stage.show();

                File loginFile = new File("logins.txt");
                //System.out.println(loginFile.exists());
                if(!loginFile.exists()){
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("logins.txt"), "utf-8"))) {
                            writer.write(currentUser.getUserName() + " signed in at " + 
                                    Calendar.getInstance().getTime() +  "\r\n");
                    }
                }        
                else {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("logins.txt", true), "utf-8"))) {
                            writer.write(currentUser.getUserName() + " signed in at " + 
                                    Calendar.getInstance().getTime() + "\r\n");
                    }
                }
                Stage currentStage = (Stage) signInBtn.getScene().getWindow();
                currentStage.close();
            }
            else {
                Locale testLoc = new Locale("de", "DE");
                invalidLogin.setText(failedLoginMessage.getString("failedLogin"));
            }
        }
        catch(Exception e){
            invalidLogin.setText(failedLoginMessage.getString("failedLogin"));
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            db = new DatabaseConn();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Locale testLoc = new Locale("de", "DE");

        //ResourceBundle failedLoginMessage = ResourceBundle.getBundle(
        //    "schedulingapplication/translationBundles/LoginMessagesBundle", testLoc);
        
        usernameLbl.setText(failedLoginMessage.getString("username"));
        passwordLbl.setText(failedLoginMessage.getString("password"));
        signInBtn.setText(failedLoginMessage.getString("signin"));
        
//        System.out.println(loc.getDisplayLanguage());
//        System.out.println(loc.getDisplayCountry());
//
//        System.out.println(loc.getLanguage());
//        System.out.println(loc.getCountry());
//
//        System.out.println(System.getProperty("user.country"));
//        System.out.println(System.getProperty("user.language"));
    }    
    
}
