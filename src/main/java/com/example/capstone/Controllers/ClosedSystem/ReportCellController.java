package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Model;
import com.example.capstone.Models.Reports;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ReportCellController implements Initializable {
    public Label reportNameLabel;
    public Label report_date_label;
    public Label numStatesLabel;
    public Label cycleYesNoLabel;
    public Label heatLabel;
    public Label workLabel;
    public Button expandReportButton;

    private final Reports report; // this is used with the reportcellfactory and the ReportCellController, each individual report

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

    }
}