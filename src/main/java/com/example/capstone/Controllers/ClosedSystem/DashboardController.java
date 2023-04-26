package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Model;
import com.example.capstone.Models.Reports;
import com.example.capstone.Views.ReportsCellFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;





public class DashboardController implements Initializable {
    @FXML
    public Text user_name;
    @FXML
    public Label login_date;
    public Label checking_balance;
    public Label checking_acc_num;
    public Label savings_val;
    public Label savings_acc_num;
    public Label income_label;
    public Label expense_label;
    @FXML
    public ListView<Reports> transaction_listview;
    public TextField payee_field;
    public TextField amount_field;
    public TextArea message_field;
    public Button sendMoney_button;
    @FXML
    public Label netWorkLabel;
    @FXML
    public Label netHeatLabel;
    @FXML
    public Label pressureSummaryLabel;
    @FXML
    public Label volumeSummaryLabel;
    @FXML
    public Label tempSummaryLabel;
    @FXML
    public Label cycleSummaryLabel;
    @FXML
    public Label processSummaryLabel;
    @FXML
    public Label stateNameLabel;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private AnchorPane imageViewParent;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { /* what will be called when this fxml is initialized*/
        // inside of the model section we are creating a singleton of the view factory
        bindData();
        initLatestReportsList();
        if(Model.getInstance().getLatestReports() == null){
            transaction_listview.setItems(null);
        }
        else{
            transaction_listview.setItems(Model.getInstance().getLatestReports());
            transaction_listview.setCellFactory(e -> new ReportsCellFactory());

        }
    }

    public void bindData(){
        user_name.textProperty().bind(Bindings.concat("Hi,").concat(Model.getInstance().getClosedSystem().firstNameProperty()));
        login_date.setText("Today, "+ LocalDate.now());
//        checking_balance.textProperty().bind(Model.getInstance().getClosedSystem().checkingAccountProperty().get().balanceProperty().asString());
//        checking_acc_num.textProperty().bind(Model.getInstance().getClosedSystem().checkingAccountProperty().get().accountNumberProperty());
//        savings_val.textProperty().bind(Model.getInstance().getClosedSystem().savingsAccountProperty().get().balanceProperty().asString());
//        savings_acc_num.textProperty().bind(Model.getInstance().getClosedSystem().savingsAccountProperty().get().accountNumberProperty());

    }

    private void initLatestReportsList(){
        if(Model.getInstance().getLatestReports().isEmpty()){
            Model.getInstance().setLatestReports(); // we are trying to avoid this list being appended every time we load the page, repeating many times
        }
    }

    public void setNetHeatLabelText(String netHeatLabel) {
        this.netHeatLabel.setText(netHeatLabel);
    }

    public void setNetWorkLabelText(String netHeatLabel) {
        this.netWorkLabel.setText(netHeatLabel);
    }

    public void setPressureLabelText(String netHeatLabel) {
        this.pressureSummaryLabel.setText(netHeatLabel);
    }

    public void setVolumeLabelText(String netHeatLabel) {
        this.volumeSummaryLabel.setText(netHeatLabel);
    }

    public void setTempLabelText(String netHeatLabel) {
        this.tempSummaryLabel.setText(netHeatLabel);
    }

    public void setCycleLabelText(String netHeatLabel) {
        this.cycleSummaryLabel.setText(netHeatLabel);
    }

    public void setProcessLabelText(String netHeatLabel) {
        this.processSummaryLabel.setText(netHeatLabel);
    }

    public void setStateNameLabelText(String netHeatLabel) {
        this.stateNameLabel.setText(netHeatLabel);
    }


}
