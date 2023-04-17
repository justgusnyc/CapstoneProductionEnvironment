package com.example.capstone.Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class ClosedSystem {

    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty usernameAddress;
    private final ObjectProperty<Account> checkingAccount;
    private final ObjectProperty<Account> savingsAccount;
    private final ObjectProperty<LocalDate> dateCreated;

    public ClosedSystem(String fname, String lname, String username, Account cAccount, Account sAcount, LocalDate date){
        this.firstName = new SimpleStringProperty(this, "FirstName", fname);
        this.lastName = new SimpleStringProperty(this, "LastName", lname);
        this.usernameAddress = new SimpleStringProperty(this, "UsernameAddress", username);
        this.savingsAccount = new SimpleObjectProperty<>(this, "SavingsAccount", sAcount);
        this.checkingAccount = new SimpleObjectProperty<>(this, "CheckingAccount", cAccount);
        this.dateCreated = new SimpleObjectProperty<>(this, "Date", date);
    }

    public StringProperty firstNameProperty() {return firstName;}

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty usernameAddressProperty() {
        return usernameAddress;
    }

    public ObjectProperty<Account> checkingAccountProperty() {
        return checkingAccount;
    }

    public ObjectProperty<Account> savingsAccountProperty() {
        return savingsAccount;
    }

    public ObjectProperty<LocalDate> dateCreatedProperty() {
        return dateCreated;
    }
}
