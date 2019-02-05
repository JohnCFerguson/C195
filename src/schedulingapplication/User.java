/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapplication;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author snobo
 */
public class User {
    
    private final int userId;
    private String userName;
    private int active;  
    private ArrayList<Appointment> userSchedule;
    
    
    public User(int tempUserId, String tempUserName, int tempActive, 
            ArrayList<Appointment> tempSchedule)
            throws ClassNotFoundException, SQLException {
        this.userId = tempUserId;
        this.userName = tempUserName;
        this.active = tempActive;
        this.userSchedule = tempSchedule;
    }
        
    public int getUserId() {
        return this.userId;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(String tempUserName) {
        this.userName = tempUserName;
    }
    
    public int getStatus() {
        return this.active;
    }
    
    public void setStatus(int tempActive) {
        this.active = tempActive;
    } 
    
    public void addAppointment(Appointment tempAppointment) {
        this.userSchedule.add(tempAppointment);
    }
    
    public ArrayList<Appointment> getUserScheudle () {
        return this.userSchedule;
    }
}
