package com.example.capstone.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.example.capstone.Models.Model;
import com.example.capstone.Views.SystemType;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    public ChoiceBox<SystemType> system_selector;
    public Label username_label;
    public TextField username_field;
    public TextField email_field;
    public PasswordField password_field;
    public PasswordField confirm_password_field;
    public Button register_btn;
    public Label error_lbl;
    public ImageView IconRegisterImageView;
    public Button login_button;
    public TextField first_name_field;
    public TextField last_name_field;
    public Label already_have_account_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        IconRegisterImageView.setImage(new Image(String.valueOf(getClass().getResource("/Images/newlogoclean.png"))));
        system_selector.setItems(FXCollections.observableArrayList(SystemType.CLOSED_SYSTEM, SystemType.OPEN_SYSTEM));
        system_selector.setValue(Model.getInstance().getViewFactory().getLoginSystemType());
        system_selector.valueProperty().addListener(observable -> Model.getInstance().getViewFactory().setLoginSystemType(system_selector.getValue()));

        register_btn.setOnAction(event -> onRegister());
        login_button.setOnAction(event -> onLoginButtonClick());
    }

    private void onLoginButtonClick() {
        Stage stage = (Stage) login_button.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    private void onRegister() {
        String firstName = first_name_field.getText().trim();
        String lastName = last_name_field.getText().trim();
        String user = username_field.getText().trim();
        String password = password_field.getText();
        String confirmPassword = confirm_password_field.getText();

        // Check if the user already exists in the database
        if (userExists(user, password)) {
            error_lbl.setText("User already exists.");
            return;
        }

        // Check if the password and confirmPassword fields match
        if (!password.equals(confirmPassword)) {
            error_lbl.setText("Passwords do not match.");
            return;
        }

        // Create a new account in the database
        boolean accountCreated = createAccount(firstName, lastName, user, password);

        if (accountCreated) {
            // Close the registration window and show a success message or redirect the user to the login page
            Stage stage = (Stage) error_lbl.getScene().getWindow();
            error_lbl.setText("User Created Sucessfully");
            //Model.getInstance().getViewFactory().closeStage(stage);
        } else {
            error_lbl.setText("Error creating account. Please try again.");
        }
    }

    private boolean userExists(String user, String password) {
        // logic to check if a user with the given payeeAddress and password already exists in the database
        // Return true if the user exists, otherwise return false
        try {
            ResultSet resultSet = Model.getInstance().getDatabaseDriver().getClosedSystemData(user, password);
            if (resultSet != null && resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean createAccount(String firstName, String lastName, String user, String password) {
        // logic to create a new account in the database with the given firstName, lastName, payeeAddress, and password
        return Model.getInstance().getDatabaseDriver().addUser(firstName, lastName, user, password);
    }
}
