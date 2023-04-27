package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Model;
import com.example.capstone.Models.Reports;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class ReportCellController implements Initializable {
    public Label reportNameLabel;
    public Label report_date_label;
    public Label numStatesLabel;
    public Label cycleYesNoLabel;
    public Label heatLabel;
    public Label workLabel;
    public Button expandReportButton;

    private final Reports report; // this is used with the reportcellfactory and the ReportCellController, each individual report
    public Button loadButton;
    public Button deleteButton;

    public ReportCellController(Reports report){
        this.report = report; // basically this is the constructor for the private final reports above, and this
        // allows us to construct it as a public reportcellcontroller type instead because all of this is supposed to
        // be to replace our custom made controller, and use the builtin javafx listview controller to update the list cells
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportNameLabel.textProperty().bind(report.reportNameProperty());
        numStatesLabel.textProperty().bind(report.numStatesProperty().asString());
        cycleYesNoLabel.textProperty().bind(report.cycleProperty());
        heatLabel.textProperty().bind(report.heatProperty());
        workLabel.textProperty().bind(report.workProperty());
        expandReportButton.setOnAction(event -> Model.getInstance().getViewFactory().showExpandedReportWindow(report.reportNameProperty().get()));

        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            // - clear the page
            // load the right number of processes and select cycle if needed
            // populate that data
            // we need to add what the process chars were, probably store that in the reports table
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Report name: "+reportNameLabel.textProperty());
                ResultSet states = Model.getInstance().getDatabaseDriver().getStatesByReportName(reportNameLabel.textProperty().get());
                AccountsController accountsController = Model.getInstance().getViewFactory().getAccountsController();
                Map<String, List<Double>> stateDataMap = new HashMap<>();

                try {
                    while (states.next()) {
                        String stateName = states.getString("state_name");
                        System.out.println("State name: " +stateName);
                        System.out.println(stateName);
                        List<Double> stateData = new ArrayList<>();
                        stateData.add(states.getDouble("pressure"));
                        System.out.println("Pressure " +states.getDouble("pressure"));
                        stateData.add(states.getDouble("volume"));
                        stateData.add(states.getDouble("temp"));
                        stateData.add(states.getDouble("heat"));
                        stateData.add(states.getDouble("work"));
                        stateData.add(states.getDouble("S"));
                        stateData.add(states.getDouble("H"));
                        stateData.add(states.getDouble("U"));
                        System.out.println(stateData);

                        stateDataMap.put(stateName, stateData);
                    }

//                    System.out.println("Accounts controller null? "+accountsController);

//                    if(accountsController != null){
                        accountsController.setStateDataMap(stateDataMap);
//                    System.out.println("state data map: "+stateDataMap);
                        accountsController.loadData(report.numProcessesProperty().get(), report.processCharsProperty().get(), cycleYesNoLabel.getText());

//                    }

//                    clearStateValues();
//                    System.out.println("state data map: "+stateDataMap);
//                    accountsController.populateTextFields();
//                    System.out.println("Load button click: "+report.numProcessesProperty().get());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String reportName = reportNameLabel.getText();

                try {
                    Model.getInstance().getDatabaseDriver().deleteReportAndStates(reportName);
                    Model.getInstance().setAllReports();
                    Model.getInstance().setLatestReports();
//                    Model.getInstance().getDatabaseDriver().vacuum();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }






}