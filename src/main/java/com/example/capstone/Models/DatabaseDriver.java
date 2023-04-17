package com.example.capstone.Models;
// this connects to the database, and defines the functions we want to use in the model

import java.sql.*;
import java.time.LocalDate;

// if we did this in the model class instead, the model object would get too big so we are doing it here instead
public class DatabaseDriver {
    private int currentUserId;
    private int currentReportID;
    private Connection conn;

    public DatabaseDriver(){
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:mazebank.db"); // this defines where and what type of db we are connecting
            // will need to change the path above when we change the db
        } catch (SQLException e){
            e.printStackTrace(); // it would be better to make another error factory to display different errors
        }
    }

    // closed system section
// this is how we fetch the data below those are all SQL methods (java.sql)
    public ResultSet getClosedSystemData(String username, String password){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Users where UsernameAddress ='"+ username +"' AND Password ='"+ password+"';");
            currentUserId = resultSet.getInt("user_id");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getReportData(int limit){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("select * from Reports where user_id ='"+this.currentUserId+"' limit "+limit+";");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    // method returns savings account balance
    public double getSavingsAccountBalance(String pAddress){
        Statement statement;
        ResultSet resultSet;
        double balance = 0;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM SavingsAccounts where Owner ='"+pAddress+"';");
            balance = resultSet.getDouble("Balance");

        } catch (SQLException e){
            e.printStackTrace();
        }
        return balance;
    }

    // method to either add or subtract from balance given the operation
    public void updateReport(String pAddress, double amount, String operation){
        Statement statement;
        ResultSet resultSet;
        try{
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("select * from SavingsAccounts where Owner ='"+pAddress+"';");
            double newBalance;

            if(operation.equals("ADD")){
                newBalance = resultSet.getDouble("Balance") + amount;
                statement.executeUpdate("UPDATE SavingsAccounts SET Balance="+newBalance+" where Owner ='"+pAddress+"';");
            }
            else if(resultSet.getDouble("Balance") > amount){
                newBalance = resultSet.getDouble("Balance") - amount;
                statement.executeUpdate("UPDATE SavingsAccounts SET Balance="+newBalance+" where Owner ='"+pAddress+"';");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // creates and records new report
    // check the dashboard send money to see how to actually handle the event
    public void newReport(String reportName, int numStates, String cycleYesNo, double heat, double work){
        Statement statement;
        try{
            statement = this.conn.createStatement();
            LocalDate date = LocalDate.now();
            statement.executeUpdate("INSERT INTO "+
                    "Reports(user_id, report_name, cycle, num_states, heat, work) "+
                    "VALUES ("+currentUserId+", '"+reportName+"', '"+cycleYesNo+"', "+numStates+", "+heat+", "+work+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet searchClient(String pAddress){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("select * from Clients where PayeeAddress='"+pAddress+"';");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet searchReport(String reportName){
        Statement statement;
        ResultSet resultSet = null;
        try{
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Reports where report_name='"+reportName+"';");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getStateDataByName(String stateLabel){
        Statement statement;
        ResultSet resultSet = null;
        try{
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM States where state_name='"+stateLabel+"';");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public int getCurrentReportID() {
        if (currentReportID == 0) {
            Statement statement;
            ResultSet resultSet = null;
            try {
                statement = this.conn.createStatement();
                resultSet = statement.executeQuery("SELECT MAX(report_id) as max_id FROM reports;");
                currentReportID = resultSet.getInt("max_id");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return currentReportID;
    }

    public void saveStateToDB(int reportID, String stateName, double pressure, double temp, double volume, double heat, double work, double S, double H, double U){
        Statement statement;
        try{
            statement = this.conn.createStatement();
            LocalDate date = LocalDate.now();
            statement.executeUpdate("INSERT INTO "+
                    "States(report_id, state_name, pressure, temp, volume, heat, work, S, H, U) "+
                    "VALUES ("+currentReportID+", '"+stateName+"', "+pressure+", "+temp+", "+heat+", "+work+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentUserId() {
        return currentUserId;
    }
}







// open system section










    // utility methods (for both)


