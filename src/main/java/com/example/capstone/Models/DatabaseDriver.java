package com.example.capstone.Models;
// this connects to the database, and defines the functions we want to use in the model

import java.sql.*;
import java.time.LocalDate;

// if we did this in the model class instead, the model object would get too big so we are doing it here instead
public class DatabaseDriver {
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
    public ResultSet getClosedSystemData(String pAddress, String password){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Clients WHERE PayeeAddress ='"+ pAddress + "' AND Password ='"+ password+"';");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getReportData(String pAddress, int limit){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("select * from Transactions where Sender ='"+pAddress+"' or Receiver ='"+pAddress+"' limit "+limit+";");
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
    public void newReport(String sender, String receiver, double amount, String message){
        Statement statement;
        try{
            statement = this.conn.createStatement();
            LocalDate date = LocalDate.now();
            statement.executeUpdate("INSERT INTO "+
                    "Transactions(Sender, Receiver, Amount, Date, Message) "+
                    "VALUES ('"+sender+"', '"+receiver+"', "+amount+", '"+date+"', '"+message+"')");
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






    // open system section










    // utility methods (for both)

}
