package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Model;
import com.example.capstone.Models.Reports;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        heatLabel.textProperty().bind(report.heatProperty().asString());
        workLabel.textProperty().bind(report.workProperty().asString());
        expandReportButton.setOnAction(event -> Model.getInstance().getViewFactory().showExpandedReportWindow(report.reportNameProperty().get(),report.cycleProperty().get()));

        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            AccountsController accountsController = Model.getInstance().getViewFactory().getAccountsController();
            ResultSet states = Model.getInstance().getDatabaseDriver().getStatesByReportName(reportNameLabel.textProperty().get());

            @Override
            public void handle(ActionEvent actionEvent) {
                Map<String, List<Double>> stateDataMap = new HashMap<>();

                try {
                    while (states.next()) {
                        String stateName = states.getString("state_name");
                        System.out.println(stateName);
                        List<Double> stateData = new ArrayList<>();
                        stateData.add(states.getDouble("pressure"));
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

//                    clearStateValues();
                    System.out.println("state data map: "+stateDataMap);
                    accountsController.setStateDataMap(stateDataMap);
                    accountsController.populateTextFields();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


    }






}