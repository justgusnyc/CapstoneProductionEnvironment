package com.example.capstone.Models;

import javafx.beans.property.*;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class Reports {
    //    private final StringProperty sender; // this is a way to define and keep track of the usernames of who created the report
    private final SimpleIntegerProperty userId;
    private final SimpleStringProperty reportName;
    private final SimpleIntegerProperty numStates;
    private final SimpleStringProperty cycle;
    private final SimpleDoubleProperty heat;
    private final SimpleDoubleProperty work;

    private final SimpleIntegerProperty numProcesses;

    private final SimpleStringProperty processChars;

    public Reports(int userId, String reportName, String cycle, int numStates, double heat, double work, int numProcesses, String processChars) {
// it is important to not only use strings or whatever else, but later we need to bind these to JavaFX observable properties
        this.userId = new SimpleIntegerProperty(this, "userID", userId);
        this.reportName = new SimpleStringProperty(this, "reportName", reportName);
        this.cycle = new SimpleStringProperty(this, "cycle", cycle);
        this.numStates = new SimpleIntegerProperty(this, "numStates", numStates);
        this.heat = new SimpleDoubleProperty(this, "heat", heat);
        this.work = new SimpleDoubleProperty(this, "work", work);
        this.numProcesses = new SimpleIntegerProperty(this, "numProcesses", numProcesses);
        this.processChars = new SimpleStringProperty(this, "processChars", processChars);
    }

//    public StringProperty senderProperty () {
//        return this.sender; // it is important that this only returns the property itself and not this.sender.get() which would return the value
//    }

    public StringProperty reportNameProperty() {
        return this.reportName; // it is important that this only returns the property itself and not this.sender.get() which would return the value
    }

    public IntegerProperty userIDProperty(){
        return this.userId;
    }

    public SimpleStringProperty cycleProperty() {
        return cycle;
    }

    public SimpleIntegerProperty numStatesProperty() {
        return numStates;
    }

    public SimpleStringProperty heatProperty() {
        DecimalFormat df = new DecimalFormat("0.000");
        String formattedHeat = df.format(heat.get());
        return new SimpleStringProperty(formattedHeat);
    }

    public SimpleStringProperty workProperty() {
        DecimalFormat df = new DecimalFormat("0.000");
        String formattedHeat = df.format(work.get());
        return new SimpleStringProperty(formattedHeat);
    }

    public SimpleIntegerProperty numProcessesProperty() {
        return numProcesses;
    }

    public SimpleStringProperty processCharsProperty() {
        return processChars;
    }
    //    public StringProperty messageProperty(){return this.message;} // this would be the message attached to the transaction for the bank shell
//}
}
