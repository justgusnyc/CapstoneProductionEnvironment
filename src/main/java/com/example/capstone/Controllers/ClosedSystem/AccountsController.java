package com.example.capstone.Controllers.ClosedSystem;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public Label checking_account_number;
    public Label transaction_limit;
    public Label checking_account_date;
    public Label checking_account_balance;
    public Label savings_account_number;
    public Label withdrawal_limit;
    public Label savings_account_date;
    public Label savings_account_balance;
    public TextField checking_to_savings_textField;
    public Button checking_to_savings_button;
    public TextField savings_to_checkings_textField;
    public Button savings_to_checkings_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
