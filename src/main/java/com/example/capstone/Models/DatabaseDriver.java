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

    public boolean addUser(String firstName, String lastName, String user, String password) {
        try {
            String sql = "INSERT INTO Users (FirstName, LastName, UsernameAddress, Password, Date) VALUES (?, ?, ?, ?, strftime('%Y-%m-%d', 'now'))";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, user);
            pstmt.setString(4, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
    public void newReport(String reportName, int numStates, String cycleYesNo, double heat, double work, int numProcesses, String processChars){
        Statement statement;
        try{
            statement = this.conn.createStatement();
            LocalDate date = LocalDate.now();
            statement.executeUpdate("INSERT INTO "+
                    "Reports(user_id, report_name, cycle, num_states, heat, work, numProcesses, processChars) "+
                    "VALUES ("+currentUserId+", '"+reportName+"', '"+cycleYesNo+"', "+numStates+", "+heat+", "+work+", "+numProcesses+", '"+processChars+"')");
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



    public ResultSet getStatesByReportName(String reportName){
        ResultSet reports = searchReport(reportName);
        Statement statement;
        ResultSet resultSet = null;
        try {
            int reportID = reports.getInt("report_id");
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM States where report_id="+reportID+";");

        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public void deleteReportAndStates(String reportName) throws SQLException {
        PreparedStatement selectReportStatement = this.conn.prepareStatement("SELECT report_id FROM Reports WHERE report_name=?");
        selectReportStatement.setString(1, reportName);
        ResultSet reportResult = selectReportStatement.executeQuery();
        int reportId = -1;
        if (reportResult.next()) {
            reportId = reportResult.getInt("report_id");
        }

        PreparedStatement reportStatement = this.conn.prepareStatement("DELETE FROM Reports WHERE report_name=?");
        reportStatement.setString(1, reportName);
        int deletedReports = reportStatement.executeUpdate();

        if (deletedReports == 0) {
            System.out.println("No reports deleted.");
            return;
        }

        if (reportId != -1) {
            PreparedStatement stateStatement = this.conn.prepareStatement("DELETE FROM States WHERE report_id=?");
            stateStatement.setInt(1, reportId);
            stateStatement.executeUpdate();
        }


        System.out.println("Report and associated states deleted.");
    }

    public void vacuum() throws SQLException {
        Statement statement = this.conn.createStatement();
        statement.executeUpdate("VACUUM");
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

    public ResultSet getStateDataByReportID(int reportId) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM States WHERE report_id = " + reportId + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public int makeCurrentReportID() {
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

    public void saveStateToDB(int reportID, String stateName, double pressure, double volume, double temp, double heat, double work, double S, double H, double U){
        Statement statement;
        try{
            statement = this.conn.createStatement();
            LocalDate date = LocalDate.now();
            statement.executeUpdate("INSERT INTO "+
                    "States(state_name, pressure, volume, temp, heat, work, S, H, U) "+
                    "VALUES ('"+stateName+"', "+pressure+", "+volume+", "+temp+", "+heat+", "+work+", "+S+", "+H+", "+U+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public void newReport(String reportName, int numStates, String cycleYesNo, double heat, double work){
//        Statement statement;
//        try{
//            statement = this.conn.createStatement();
//            LocalDate date = LocalDate.now();
//            statement.executeUpdate("INSERT INTO "+
//                    "Reports(user_id, report_name, cycle, num_states, heat, work) "+
//                    "VALUES ("+currentUserId+", '"+reportName+"', '"+cycleYesNo+"', "+numStates+", "+heat+", "+work+")");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void deleteStateValuesByReportID(int reportID) {
        Statement statement;
        try {
            statement = this.conn.createStatement();
            statement.executeUpdate("DELETE FROM States WHERE report_id = " + reportID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public int getCurrentReportID(){
        this.currentReportID = makeCurrentReportID();
        System.out.println(makeCurrentReportID());
        System.out.println(this.currentReportID);

        return this.currentReportID;
    }
}







// open system section










    // utility methods (for both)


