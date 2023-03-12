package com.example.capstone.Controllers.ClosedSystem;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
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
    public ListView transaction_listview;
    public TextField payee_field;
    public TextField amount_field;
    public TextArea message_field;
    public Button sendMoney_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { /* what will be called when this fxml is initialized*/
        // inside of the model section we are creating a singleton of the view factory
    }
}
