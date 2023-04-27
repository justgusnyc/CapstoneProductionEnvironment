package com.example.capstone.Views;

import com.example.capstone.Controllers.ClosedSystem.AccountsController;
import com.example.capstone.Controllers.ClosedSystem.ClosedSystemController;
import com.example.capstone.Controllers.ClosedSystem.ClosedSystemMenuController;
import com.example.capstone.Controllers.ClosedSystem.DashboardController;
import com.example.capstone.Models.Model;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// combines the dashboard and the menu displayed side by side
public class ViewFactory {


    private SystemType loginSystemType; // how we are going to set which system type we are using

    // Closed System views
    private final ObjectProperty<ClosedSystemMenuOptions> closedSystemSelectedMenuItem;
    private StackPane dashboardView; // why was this changed from AnchorPane to StackPane?
    private AnchorPane viewReportsView;
    private BorderPane accountsView;
    private AccountsController accountsController;

    private DashboardController dashboardController;

    // open system views
    private final StringProperty openSystemSelectedMenuItem;
    private AnchorPane createClosedSystemUserView;

    public ViewFactory(){
        this.loginSystemType = SystemType.CLOSED_SYSTEM;
        this.closedSystemSelectedMenuItem = new SimpleObjectProperty<>();
        this.openSystemSelectedMenuItem = new SimpleStringProperty("");
    }

    public SystemType getLoginSystemType() {
        return loginSystemType;
    } // how we get and set which system type we are using

    public void setLoginSystemType(SystemType loginSystemType) {
        this.loginSystemType = loginSystemType;
    }

    public ObjectProperty<ClosedSystemMenuOptions> getClosedSystemSelectedMenuItem() {
        return closedSystemSelectedMenuItem;
    }

    public StackPane getDashboardView(){
        if(dashboardView == null){ // if we do not have the view then we load the correct fxml
            try{ // we first check is dashboard is equal to null, every time that the user goes from dashboard to somewhere else and comes back, we should be able to use this instantiation without loading it again to save computation
                // basically if the object has already been created then we use it, makes app faster
//                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/ClosedSystem/Dashboard.fxml")).load();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ClosedSystem/Dashboard.fxml"));
                dashboardView = loader.load();
                dashboardController = loader.getController();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public AnchorPane getViewReportsView(){ // opens the view reports fxml page in a pseudo singleton way
        if(viewReportsView == null){
            try{
                viewReportsView = new FXMLLoader(getClass().getResource("/Fxml/ClosedSystem/ViewReports.fxml")).load();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return  viewReportsView;
    }

    public BorderPane getAccountsView() {
        if (accountsView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ClosedSystem/Accounts.fxml"));
                accountsView = loader.load();
                accountsController = loader.getController();

//                PauseTransition pause = new PauseTransition(Duration.seconds(3));

                // Set the action to be performed after the pause
//                pause.setOnFinished(event -> {
//                    setAccountsController(accountsController);
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accountsView;
    }

    private void createStage(FXMLLoader loader){
        Scene scene = null;
        try{
            scene = new Scene(loader.load()); // we put this inside of the try catch in case the file that the loader is looking for does not exist

        } catch (Exception e){
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/Images/slu-logomark-blue-rgb.png")))); // returns observable list of images, how we add an icon as well to the top
        // border part of the application
        stage.setResizable(true); // LATER ON WE WILL NEED TO DELETE this and make all the css resizeable
        stage.setScene(scene);
        stage.setTitle("Capstone H.E.A.T.S.");
        stage.show();
    }

    public void showExpandedReportWindow(String reportName){
        // get the results from the database, then while the resultset has next
        ResultSet resultSet = Model.getInstance().getDatabaseDriver().getStatesByReportName(reportName);
        try {
            StackPane pane = new StackPane();
            VBox vBox = new VBox(5);
//            VBox.setAlignment(Pos.CENTER);
            while (resultSet.next()){
                String stateName = resultSet.getString("state_name");
                double pressure = resultSet.getDouble("pressure");
                double volume = resultSet.getDouble("volume");
                double temp = resultSet.getDouble("temp");
                double heat = resultSet.getDouble("heat");
                double work = resultSet.getDouble("work");

                Label StateLabel = new Label();
//                StateLabel.setText("Name: "+stateName+ " P: "+pressure+" v: "+volume+" T: "+temp+" Work(associated process): "+work+" Heat: "+heat);
                StateLabel.setText("Name: "+stateName+ " P: "+String.format("%.2f", pressure)+" v: "+String.format("%.5f", volume)+" T: "+String.format("%.2f", temp));
                vBox.setStyle("-fx-padding: 10");
                vBox.getChildren().add(StateLabel);
            }
            pane.getChildren().add(vBox);
            pane.setStyle("-fx-background-color: white;"+"-fx-border-style: solid inside;"+"-fx-border-color: green;");
            Scene scene = new Scene(pane, 300, 100);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/Images/slu-logomark-blue-rgb.png")))); // returns observable list of images, how we add an icon as well to the top
            stage.setResizable(true);
            stage.initModality(Modality.APPLICATION_MODAL); // makes it so you cannot click away when it opens
            stage.setTitle("Expanded Report");
            stage.setScene(scene);
            stage.show();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void showLoginWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader); // we just call this function to load each individual fxml file when we need it
    }

    public void showRegisterWindow(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Register.fxml"));
        createStage(loader); // we just call this function to load each individual fxml file when we need it

    }

    public void showSplashInstructionsPage(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/SplashInstructions.fxml"));
        createStage(loader);
    }


    public void showClosedSystemWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ClosedSystem/ClosedSystem.fxml"));

        ClosedSystemController closedSystemController = new ClosedSystemController();
        loader.setController(closedSystemController); // we had to set this manually because of removing the fx controller from the closed system fxml file
        ClosedSystemMenuController closedSystemMenuController = new ClosedSystemMenuController();
        createStage(loader);
    }

    public void closeStage(Stage stage){ // closes a stage (window) for us as a utility function
        stage.close();
    }

    public AccountsController getAccountsController() {
        return this.accountsController;
    }

    public void setAccountsController(AccountsController accountsController) {
        this.accountsController = accountsController;
    }

    public DashboardController getDashboardController() {
        return dashboardController;
    }
}
