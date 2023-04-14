package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Model;
import com.example.capstone.Models.Reports;
import com.example.capstone.Views.ReportsCellFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Text user_name;
    public Label login_date;
    public Label checking_balance;
    public Label checking_acc_num;
    public Label savings_val;
    public Label savings_acc_num;
    public Label income_label;
    public Label expense_label;
    public ListView<Reports> transaction_listview;
    public TextField payee_field;
    public TextField amount_field;
    public TextArea message_field;
    public Button sendMoney_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { /* what will be called when this fxml is initialized*/
        // inside of the model section we are creating a singleton of the view factory
        bindData();
        initLatestReportsList();
        transaction_listview.setItems(Model.getInstance().getLatestReports());
        transaction_listview.setCellFactory(e -> new ReportsCellFactory());

        sendMoney_button.setOnAction(event -> onSendMoney());
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

    private void onSendMoney(){
        String receiver = payee_field.getText(); // pAddress everywhere else
        double amount = Double.parseDouble(amount_field.getText());
        String message = message_field.getText();
        String sender = Model.getInstance().getClosedSystem().payeeAddressProperty().get();
        ResultSet resultSet = Model.getInstance().getDatabaseDriver().searchClient(receiver);
        try {
            if(resultSet.isBeforeFirst()){
                Model.getInstance().getDatabaseDriver().updateReport(receiver, amount, "ADD");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // subtracting from senders savings account
        Model.getInstance().getDatabaseDriver().updateReport(sender, amount, "SUB");
        // update the savings account
        Model.getInstance().getClosedSystem().savingsAccountProperty().get().setBalance(Model.getInstance().getDatabaseDriver().getSavingsAccountBalance(sender));
        // record new transaction
        Model.getInstance().getDatabaseDriver().newReport(sender, receiver, amount, message);
        // clear the fields
        payee_field.setText("");
        amount_field.setText("");
        message_field.setText("");

    }


}
