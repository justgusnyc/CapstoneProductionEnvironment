package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Logic.Process;
import com.example.capstone.Models.Logic.Solver;
import com.example.capstone.Models.Logic.State;
import com.example.capstone.Models.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AccountsController implements Initializable {
    //    public Label checking_account_number;
//    public Label transaction_limit;
//    public Label checking_account_date;
//    public Label checking_account_balance;
//    public Label savings_account_number;
//    public Label withdrawal_limit;
//    public Label savings_account_date;
//    public Label savings_account_balance;
//    public TextField checking_to_savings_textField;
//    public Button checking_to_savings_button;
//    public TextField savings_to_checkings_textField;
//    public Button savings_to_checkings_button;
    public HBox ProcessLayout;
    public BorderPane ProcessBorderPane;
    public ChoiceBox numProcessesChoice;
    public ChoiceBox materialChoice;
    public ToggleButton cycleYesButton;
    public Button computeButton;
    public ScrollPane processesScrollPane;
    public ChoiceBox visualTypeChoiceBox;
    public ScrollPane visualScrollPane;
    public VBox visualScrollPaneVBox;
    public Button SaveReportButton;
    public TextField SaveReportNameTextField;
    public TextField netHeatTextField;
    public TextField netWorkTextField;

    private List<Process> processesList;
    public Button clearButton;

    private List<HBox> hboxParents = new ArrayList<>();

    ObservableList<Integer> maxProcesses = FXCollections.observableArrayList(1, 2, 3, 4, 5);

    private ObservableList<String> chartOptions = FXCollections.observableArrayList("P-v", "T-v", "T-s", "P-h");

    int[] state1Indexes = {1, 2, 3, 4, 5};
    int[] state2Indexes = {2, 3, 4, 5, 6};
    private int maxVal;

    private BorderPane accountsView;

    private List<ProcessHolderController> processControllers = new ArrayList<>();

    private LineChart<Number, Number> chart;
    List<Double> pressureOverTime = new ArrayList<>();
    List<Double> volumeOverTime = new ArrayList<>();
    List<Double> tempOverTime = new ArrayList<>();

    List<List<TextField>> textFields = new ArrayList<>();
    List<String> textFieldsStatesNames = new ArrayList<>();

    private boolean cycleFlag;
    Map<String, List<TextField>> stateValuesMap = new HashMap<>();

    private Map<String, List<List<TextField>>> connectedTextField = new HashMap<>();

    private Map<String, List<Double>> stateDataMap = new HashMap<>();

    private ProcessHolderController processHolderController;

    private DashboardController dashboardController;
//    private String processChars = "";

//    public void setProcessHolderController(ProcessHolderController processHolderController) {
//        this.processHolderController = processHolderController;
//    }

    private double netWork, netHeat, netHeatIn, netHeatOut, netWorkIn, netWorkOut;

    private List<Double> processHeats = new ArrayList<>();
    private List<Double> processWorks = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        numProcessesChoice.setItems(maxProcesses);

        visualTypeChoiceBox.setItems(chartOptions);

        numProcessesChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            int max;
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number value, Number newValue) {
                if(newValue.intValue() == -1){
                    max = -1;
                }
                else{
                    max = maxProcesses.get(newValue.intValue());
                }
                switch (max) {
                    case 1 -> maxVal = 1;
                    case 2 -> maxVal = 2;
                    case 3 -> maxVal = 3;
                    case 4 -> maxVal = 4;
                    case 5 -> maxVal = 5;
                    case -1 -> maxVal = 0;
                    default -> System.out.println("Something was wrong in pretty");
                }
                try {


                    for (int i = 0; i < maxVal; i++) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/Fxml/ClosedSystem/ProcessHolder.fxml"));
                        HBox box = fxmlLoader.load();
                        hboxParents.add(box);

                        ProcessHolderController processHolderController = fxmlLoader.getController();
                        processControllers.add(processHolderController);
                        processHolderController.setBlank(maxVal);
                        processHolderController.setFirstStateLabel("State" + state1Indexes[i]);
                        processHolderController.setSecondStateLabel("State" + state2Indexes[i]);

                        String stateLabel1 = processHolderController.getFirstStateLabelString();
                        String stateLabel2 = processHolderController.getSecondStateLabelString();
                        List<List<TextField>> textFields1 = connectedTextField.getOrDefault(stateLabel1, new ArrayList<>());
                        List<List<TextField>> textFields2 = connectedTextField.getOrDefault(stateLabel2, new ArrayList<>());
                        List<TextField> stateLeftTextFields = processHolderController.getStateLeftTextFields();
                        List<TextField> stateRightTextFields = processHolderController.getStateRightTextFields();
                        textFields1.add(stateLeftTextFields);
                        textFields2.add(stateRightTextFields);
                        connectedTextField.put(stateLabel1, textFields1);
                        connectedTextField.put(stateLabel2, textFields2);

                        ProcessLayout.getChildren().add(box);
                    }

// Connect text fields based on state label
                    for (List<List<TextField>> textFieldsList : connectedTextField.values()) {
                        for (int i = 0; i < textFieldsList.size(); i++) {
                            List<TextField> textFields1 = textFieldsList.get(i);
                            for (int j = i + 1; j < textFieldsList.size(); j++) {
                                List<TextField> textFields2 = textFieldsList.get(j);
                                for (int k = 0; k < textFields1.size(); k++) {
                                    TextField textField1 = textFields1.get(k);
                                    TextField textField2 = textFields2.get(k);
                                    textField1.textProperty().bindBidirectional(textField2.textProperty());
                                }
                            }
                        }
                    }
                    makeStateValuesMap();
                    hideConnectedVBoxes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        cycleYesButton.setOnAction(e -> {
            if (cycleYesButton.isSelected()) {
                cycleYesButton.setText("Yes");
                cycleFlag = true;
                // Get the text fields list for the left most state
                List<List<TextField>> textFieldsList = connectedTextField.get("State1");
                List<TextField> stateLeftTextFields = textFieldsList.get(0);

                // Get the text fields list for the right most state
                int lastIndex = maxVal - 1;
                String stateLabel2 = "State" + state2Indexes[lastIndex];
                textFieldsList = connectedTextField.get(stateLabel2);
                List<TextField> stateRightTextFields = textFieldsList.get(textFieldsList.size() - 1);

                // Connect the text fields
                for (int k = 0; k < stateLeftTextFields.size(); k++) {
                    TextField textField1 = stateLeftTextFields.get(k);
                    TextField textField2 = stateRightTextFields.get(k);
                    textField1.textProperty().bindBidirectional(textField2.textProperty());
                }

                // Update the second state label of the last process controller
                processControllers.get(lastIndex).setSecondStateLabel("State1");
                List<TextField> valueToRemove = stateValuesMap.get("State" + state2Indexes[maxVal - 1]);
                stateValuesMap.remove("State" + state2Indexes[maxVal - 1], valueToRemove);
            } else {
                cycleYesButton.setText("No");
                cycleFlag = false;
                // Get the text fields list for the left most state
                List<List<TextField>> textFieldsList = connectedTextField.get("State1");
                List<TextField> stateLeftTextFields = textFieldsList.get(0);

                // Get the text fields list for the right most state
                int lastIndex = maxVal - 1;
                String stateLabel2 = "State" + state2Indexes[lastIndex];
                textFieldsList = connectedTextField.get(stateLabel2);
                List<TextField> stateRightTextFields = textFieldsList.get(textFieldsList.size() - 1);

                // Connect the text fields
                for (int k = 0; k < stateLeftTextFields.size(); k++) {
                    TextField textField1 = stateLeftTextFields.get(k);
                    TextField textField2 = stateRightTextFields.get(k);
                    textField1.textProperty().unbindBidirectional(textField2.textProperty());
                }

                // Update the second state label of the last process controller

                processControllers.get(lastIndex).setSecondStateLabel("State" + state2Indexes[maxVal - 1]);
                stateValuesMap.computeIfAbsent("State" + state2Indexes[maxVal - 1], k -> new ArrayList<>());
            }
        });


        computeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Process> processes = new ArrayList<>();
                Map<String, State> states = new HashMap<>();


                for (int i = 0; i < processControllers.size(); i++) {
                    ProcessHolderController controller = processControllers.get(i);
                    Map<String, List<Double>> stateData = controller.getData();
                    List<Double> stateLeftData = stateData.get("StateLeft");
                    List<Double> stateRightData = stateData.get("StateRight");
                    pressureOverTime.add(stateLeftData.get(0));
                    pressureOverTime.add(stateRightData.get(0));
                    volumeOverTime.add(stateLeftData.get(1));
                    volumeOverTime.add(stateRightData.get(1));
                    tempOverTime.add(stateLeftData.get(2));
                    tempOverTime.add(stateRightData.get(2));

                    String leftStateLabel = controller.getFirstStateLabelString();
                    String rightStateLabel = controller.getSecondStateLabelString();

                    // Create or retrieve existing states
                    State stateLeft = states.computeIfAbsent(leftStateLabel, label -> new State(stateLeftData.get(2), stateLeftData.get(0), stateLeftData.get(1), label));
                    State stateRight = states.computeIfAbsent(rightStateLabel, label -> new State(stateRightData.get(2), stateRightData.get(0), stateRightData.get(1), label));

                    char processType = controller.getProcessType();

                    double nValue = 0; // Initialize nValue with a default value (e.g., 0)
                    boolean isValidNValue = true;
                    if (processType == 'x' || processType == 'y') {
                        if (processType == 'y') {
                            nValue = 1.005/0.718;
                        } else {
                            if (!controller.NValue.getText().isEmpty()) {
                                try {
                                    nValue = Double.parseDouble(controller.NValue.getText());
                                } catch (NumberFormatException e) {
                                    // Show an error message if the entered text is not a valid number
                                    showErrorAlert("Invalid n value entered. Please enter a valid number.");
                                    isValidNValue = false;
                                }
                            } else {
                                showErrorAlert("Please enter an n value for process type " + processType + ".");
                                isValidNValue = false;
                            }
                        }
                    }

                    if (isValidNValue) {
                        Process process;
                        if (processType == 'x' || processType == 'y') {
                            // if the process type is polytropic or isentropic, create process with n
                            process = new Process(stateRight, stateLeft, nValue, processType);
                        } else {
                            // else use the constructor without the n value
                            process = new Process(stateRight, stateLeft, processType);
                        }
                        processes.add(process);
                    }


//                    State stateLeft = new State(stateLeftData.get(2), stateLeftData.get(0), stateLeftData.get(1), controller.getFirstStateLabelString());
//                    State stateRight = new State(stateRightData.get(2), stateRightData.get(0), stateRightData.get(1), controller.getSecondStateLabelString());
//                    states.put(controller.getFirstStateLabelString(), stateLeft);
//                    states.put(controller.getSecondStateLabelString(), stateRight);
//                    System.out.println("left " + stateLeft);
//                    System.out.println("right " + stateRight);
//                    System.out.println(stateLeft.getPressure());
//                    Process process = new Process(states.get(controller.getSecondStateLabelString()), states.get(controller.getFirstStateLabelString()), controller.getProcessType());

//                    processes.add(process);
                }

                Collections.sort(pressureOverTime);
                Collections.sort(tempOverTime);
                Collections.sort(volumeOverTime);


//                List<Process> processes2 = processesList();
                Solver solver = new Solver(processes);
                System.out.println(solver.CompleteSolve());
                Map<String, List<Double>> m = solver.CompleteSolve();

                for (int i = 0; i < processControllers.size(); i++) {
                    ProcessHolderController controller = processControllers.get(i);
                    double heat = processes.get(i).getHeat();
                    double work=processes.get(i).getWork();
                    netWork += work;
                    netHeat += heat;
                    if (heat > 0) netHeatIn += heat;
                    if (heat < 0) netHeatOut += heat;
                    if (work > 0) netWorkIn += work;
                    if (work < 0) netWorkOut += work;


                    controller.setHeatTextfield(processes.get(i).getHeat());
                    controller.setWorkTextField(processes.get(i).getWork());
                    processHeats.add(processes.get(i).getHeat());
                    processWorks.add(processes.get(i).getWork());

                    try {
                        controller.setValuesAfterCompleteSolve(m);
//                        updateTextFields(solver.CompleteSolve());

                    } catch (IllegalArgumentException e) {
                        showErrorAlert(e.getMessage());
                    }
//                    System.out.println(processes.get(i));
                }
                netHeatTextField.setText(""+netHeat);
                netWorkTextField.setText(""+netWork);
            }


        });

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clearPage();

            }
        });

        SaveReportButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    onSaveReports();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });

// Create the chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Visualization Presets");

        xAxis.setLowerBound(0);
        xAxis.setUpperBound(0.2);
        xAxis.setTickUnit(0.025);

        yAxis.setLowerBound(0);
        yAxis.setUpperBound(2500);
        yAxis.setTickUnit(500);


//        visualScrollPane.setContent(chart);
//        visualScrollPane.setFitToWidth(true);
//        visualScrollPane.setFitToHeight(true);

        visualTypeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {


            if (newValue.equals("P-v")) {
                visualScrollPaneVBox.getChildren().clear();

                if (newValue.equals("P-v")) {
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    series.setName("P-v");
                    for (int i = 0; i < volumeOverTime.size(); i++) {
                        Double temp = tempOverTime.get(i);
                        Double volume = volumeOverTime.get(i);
                        Double pressure = 0.287 * temp / volume;
                        series.getData().add(new XYChart.Data<>(volume, pressure));
                    }
                    chart.getData().add(series);
//                    visualScrollPane.setContent(chart);
                    visualScrollPaneVBox.getChildren().add(chart);
                }

//                for (int i = 0; i < volumeOverTime.size(); i++) {
//                    Double temp = tempOverTime.get(i);
//                    Double volume = volumeOverTime.get(i);
//                    Double pressure = pressureOverTime.get(i);
//                    System.out.println("Data point " + (i + 1) + ": Volume = " + volume + ", Pressure = " + pressure);
//                    series.getData().add(new XYChart.Data<>(volume, pressure));
//                }
//                chart.getData().add(series);
//                visualScrollPane.setContent(chart);
//                visualScrollPane.setFitToWidth(true);
//                visualScrollPane.setFitToHeight(true);
            } else if (newValue.equals("T-v")) {
                visualScrollPaneVBox.getChildren().clear();
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("T-v");
                for (int i = 0; i < tempOverTime.size(); i++) {
                    Double temp = tempOverTime.get(i);
                    Double volume = volumeOverTime.get(i);
                    series.getData().add(new XYChart.Data<>(temp, volume));
                }
                chart.getData().add(series);
                visualScrollPaneVBox.getChildren().add(chart);
//                visualScrollPane.setContent(chart);
//                visualScrollPane.setFitToWidth(true);
//                visualScrollPane.setFitToHeight(true);
            } else if (newValue.equals("T-s")) {
                // Change the content of the scroll pane to another visualization
                // preset based on option 3
                // ...
            } else if (newValue.equals("P-h")) {
                // Change the content of the scroll pane to another visualization
                // preset based on option 4
                // ...
            }
        });


    }

    public List<Process> processesList() {
        List<Process> ls = new ArrayList<>();
        Map<String, State> states = new HashMap<>();
        for (int i = 0; i < processControllers.size(); i++) {
            ProcessHolderController controller = processControllers.get(i);
            Map<String, List<Double>> stateData = controller.getData();
            //System.out.println("Controller " + i + " state data: " + stateData);
            List<Double> stateLeftData = stateData.get("StateLeft");
            List<Double> stateRightData = stateData.get("StateRight");
            pressureOverTime.add(stateLeftData.get(0));
            pressureOverTime.add(stateRightData.get(0));
            volumeOverTime.add(stateLeftData.get(1));
            volumeOverTime.add(stateRightData.get(1));
            tempOverTime.add(stateLeftData.get(2));
            tempOverTime.add(stateRightData.get(2));
            String leftStateLabel = controller.getFirstStateLabelString();
            String rightStateLabel = controller.getSecondStateLabelString();

            // Create or retrieve existing states
            State stateLeft = states.computeIfAbsent(leftStateLabel, label -> new State(stateLeftData.get(2), stateLeftData.get(0), stateLeftData.get(1), label));
            State stateRight = states.computeIfAbsent(rightStateLabel, label -> new State(stateRightData.get(2), stateRightData.get(0), stateRightData.get(1), label));

            char processType = controller.getProcessType();

            double nValue = 0; // Initialize nValue with a default value (e.g., 0)
            boolean isValidNValue = true;
            if (processType == 'x' || processType == 'y') {
                if (processType == 'y') {
                    nValue = 1.005/0.718;
                } else {
                    if (!controller.NValue.getText().isEmpty()) {
                        try {
                            nValue = Double.parseDouble(controller.NValue.getText());
                        } catch (NumberFormatException e) {
                            // Show an error message if the entered text is not a valid number
                            showErrorAlert("Invalid n value entered. Please enter a valid number.");
                            isValidNValue = false;
                        }
                    } else {
                        showErrorAlert("Please enter an n value for process type " + processType + ".");
                        isValidNValue = false;
                    }
                }
            }

            if (isValidNValue) {
                Process process;
                if (processType == 'x' || processType == 'y') {
                    // if the process type is polytropic or isentropic, create process with n
                    process = new Process(stateRight, stateLeft, nValue, processType);
                } else {
                    // else use the constructor without the n value
                    process = new Process(stateRight, stateLeft, processType);
                }
                ls.add(process);
            }

        }

        return ls;
    }




    public void compute() {
        List<Process> processes = new ArrayList<>();

        // Retrieve the input values from the text fields and create processes
        for (int i = 0; i < hboxParents.size(); i++) {
            ProcessHolderController controller = processControllers.get(i);
            HBox box = hboxParents.get(i);
            TextField S1Pressure = (TextField) box.lookup("#S1pressure");
            TextField S1Volume = (TextField) box.lookup("#S1Volume");
            TextField S1Temp = (TextField) box.lookup("#S1temperature");

            TextField S2Pressure = (TextField) box.lookup("#S2pressure");
            TextField S2Volume = (TextField) box.lookup("#S2volume");
            TextField S2Temp = (TextField) box.lookup("#S2temperature");


            State initState;
            if (i > 0) {
                // If it is not the first process, create the initial state of the current process with the final state values of the previous process
                State prevStateFinal = processes.get(i - 1).getState2();
                initState = new State(prevStateFinal.getTemp(), prevStateFinal.getPressure(), prevStateFinal.getVolume(), "State " + (i * 2 + 1));
            } else {
                initState = new State(Double.parseDouble(S1Temp.getText()), Double.parseDouble(S1Pressure.getText()), Double.parseDouble(S1Volume.getText()), "State " + (i * 2 + 1));
            }

            State finalState = new State(Double.parseDouble(S2Temp.getText()), Double.parseDouble(S2Pressure.getText()), Double.parseDouble(S2Volume.getText()), "State " + (i * 2 + 2));
            Process process = new Process(initState, finalState, controller.getProcessType()); // 'x' as a placeholder for the process type

            processes.add(process);
        }


        // Solve the processes using the Solver class
        Solver solver = new Solver(processes);
        System.out.println(solver.CompleteSolve());

        // Update the text fields with the computed values
        try {
            updateTextFields(solver.CompleteSolve());
        } catch (IllegalArgumentException e) {
            showErrorAlert(e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTextFields(Map<String, List<Double>> solvedStates) {
        int i = 0;
        for (Map.Entry<String, List<Double>> entry : solvedStates.entrySet()) {
            if (i < hboxParents.size()) {
                HBox box = hboxParents.get(i);

                TextField S1Pressure = (TextField) box.lookup("#S1pressure");
                TextField S1Volume = (TextField) box.lookup("#S1Volume");
                TextField S1Temp = (TextField) box.lookup("#S1temperature");

                TextField S2Pressure = (TextField) box.lookup("#S2pressure");
                TextField S2Volume = (TextField) box.lookup("#S2volume");
                TextField S2Temp = (TextField) box.lookup("#S2temperature");

                List<Double> initStateValues = entry.getValue();
                String nextKey = "State" + (Integer.parseInt(entry.getKey().replaceAll("\\D+", "")) + 1);

                if (solvedStates.containsKey(nextKey)) {
                    List<Double> finalStateValues = solvedStates.get(nextKey);

                    S1Pressure.setText(String.valueOf(initStateValues.get(1)));
                    S1Volume.setText(String.valueOf(initStateValues.get(2)));
                    S1Temp.setText(String.valueOf(initStateValues.get(0)));

                    S2Pressure.setText(String.valueOf(finalStateValues.get(1)));
                    S2Volume.setText(String.valueOf(finalStateValues.get(2)));
                    S2Temp.setText(String.valueOf(finalStateValues.get(0)));
                } else {
                    S1Pressure.setText(String.valueOf(initStateValues.get(1)));
                    S1Volume.setText(String.valueOf(initStateValues.get(2)));
                    S1Temp.setText(String.valueOf(initStateValues.get(0)));
                }

                i++;
            }
        }
    }



    public int getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(int maxVal) {
        this.maxVal = maxVal;
    }

    public void setAccountsView(BorderPane accountsView) {
        this.accountsView = accountsView;
    }

    public BorderPane getAccountsView() {
        return accountsView;
    }

    private void connectTextFields(List<TextField> source, List<TextField> target) {
        for (TextField field : source) {
            for (TextField otherField : target) {
                if (field != otherField) {
                    field.textProperty().bindBidirectional(otherField.textProperty());
                }
            }
        }
    }

    private String getProcessChars(){
        String processChars = "";
        for(ProcessHolderController process :processControllers){
            char c = process.getProcessType();
            processChars += c;
        }
        return processChars;
    }

    private void onSaveReports() throws SQLException {
        int currentReportID;

        currentReportID = Model.getInstance().getDatabaseDriver().getCurrentReportID();
//
        System.out.println("in accounts: "+currentReportID);
        String reportName = this.SaveReportNameTextField.getText();
        int numStates = stateValuesMap.size();
        double work = 0;
        double heat = 0;
        double S = 0;
        double H = 0;
        double U = 0;

        String processChars = getProcessChars();


        ResultSet resultSet = Model.getInstance().getDatabaseDriver().searchReport(reportName);
        if (cycleFlag) {
            ResultSet state1Data = Model.getInstance().getDatabaseDriver().getStateDataByName("State 1");
            try {
                work = state1Data.getDouble("work"); // this should be changed to get it from the connectedTextField at some point, there just isn't heat and work in there yet
                heat = state1Data.getDouble("heat");
                if (resultSet.isBeforeFirst()) { // basically if the query looking for this report name already has a value, then we are gonna update it instead
//                    Model.getInstance().getDatabaseDriver().updateReport();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // otherwise, if there are no results in that query, then we make a new report
            Model.getInstance().getDatabaseDriver().newReport(reportName, numStates, "Yes", netHeat, netWork, this.maxVal, processChars);

        } else {
            ResultSet state1Data = Model.getInstance().getDatabaseDriver().getStateDataByName("State " + maxVal + 1);
            try {
                work = state1Data.getDouble("work");
                heat = state1Data.getDouble("heat");
                if (resultSet.isBeforeFirst()) { // basically if the query looking for this report name already has a value, then we are gonna update it instead
//                    Model.getInstance().getDatabaseDriver().updateReport();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // otherwise, if there are no results in that query, then we make a new report
            Model.getInstance().getDatabaseDriver().newReport(reportName, numStates, "No", netHeat, netWork, this.maxVal, processChars);

        }

        ResultSet statesInReport = Model.getInstance().getDatabaseDriver().getStateDataByReportID(currentReportID);
//        if(statesInReport.isBeforeFirst()){
//            Model.getInstance().getDatabaseDriver().updateStates();
//        }
//        else{
        int i = 0, ind = 0;
            for (Map.Entry<String, List<TextField>> entry : stateValuesMap.entrySet()) {
                String stateName = entry.getKey();
                List<TextField> textFieldList = entry.getValue();

                double currentPressure = Double.parseDouble(textFieldList.get(0).getText());
                double currentVolume = Double.parseDouble(textFieldList.get(1).getText());
                double currentTemp = Double.parseDouble(textFieldList.get(2).getText());

                if(cycleFlag){
                    work = processWorks.get(i);
                    heat = processHeats.get(i);
                }
                else {
                    if(i >= processControllers.size()){
                        work = processWorks.get(processControllers.size()-1);
                        heat = processHeats.get(processControllers.size()-1);
                    }
                }
                    i++;
//                if((i != 0) && (i % 2 == 0)){
//                    ind += 1; // if cycle then this is not needed at all, each state gets each work and heat
//                }
//                work = processWorks.get(ind);
//                heat = processHeats.get(ind);

//                System.out.println("State: " + stateName);
//                System.out.println("Current Pressure: " + currentPressure);
//                System.out.println("Current Volume: " + currentVolume);
//                System.out.println("Current Temperature: " + currentTemp);

                Model.getInstance().getDatabaseDriver().saveStateToDB(currentReportID, stateName, currentPressure, currentVolume, currentTemp, heat, work, S, H, U);
            }
                Model.getInstance().setLatestReports();
                Model.getInstance().setAllReports();
        }


    public void makeStateValuesMap () {

        for (Map.Entry<String, List<List<TextField>>> entry : connectedTextField.entrySet()) {
            String key = entry.getKey();
            List<List<TextField>> value = entry.getValue();
            List<TextField> updatedValue = new ArrayList<>();

            if (!value.isEmpty()) {
                List<TextField> innerList = value.get(0);
                updatedValue.addAll(innerList);
            }
            this.stateValuesMap.put(key, updatedValue);
        }

    }



    public void populateTextFields() {

        for (int i = 0; i < processControllers.size(); i++) {
            ProcessHolderController controller = processControllers.get(i);

            controller.setDataByMap(this.stateDataMap);
            System.out.println("Controller first label: "+controller.getFirstStateLabelString());
        }
    }

    public Map<String, List<TextField>> getStateValuesMap() {
        return this.stateValuesMap;
    }

    public void setStateDataMap(Map<String, List<Double>> stateDataMap) {
        this.stateDataMap = stateDataMap;
    }

    private void clearPage() {
        hboxParents.clear();
        processControllers.clear();
        textFields.clear();
        textFieldsStatesNames.clear();
        cycleFlag = false;
        stateValuesMap.clear();
        connectedTextField.clear();
//        stateDataMap.clear();
        ProcessLayout.getChildren().clear();
        chart.getData().clear();
        pressureOverTime.clear();
        volumeOverTime.clear();
        tempOverTime.clear();
        maxVal = 0;
        numProcessesChoice.setValue(null);
        visualScrollPaneVBox.getChildren().clear();
    }

    public void setProcessChoice(int numProcess){

        switch (numProcess){
            case(1) -> numProcessesChoice.getSelectionModel().select(0);
            case(2) -> numProcessesChoice.getSelectionModel().select(1);
            case(3) -> numProcessesChoice.getSelectionModel().select(2);
            case(4) -> numProcessesChoice.getSelectionModel().select(3);
            case(5) -> numProcessesChoice.getSelectionModel().select(4);
            default -> System.out.println("Something went wrong in process choice");

        }
    }

    public void loadData(int numProcesses, String processChars, String cycleYesNo){ // we need to make a "load chart" feature
        ClosedSystemMenuController closedSystemMenuController = new ClosedSystemMenuController();
        clearPage();
        setProcessChoice(numProcesses);
        System.out.println("Cycle yes no: "+cycleYesNo);
        if(cycleYesNo.equals("Yes") && !cycleYesButton.isSelected()){
            System.out.println("THE CYCLE BUTTON WAS YES AND IT WAS NOT SELECTED");
            cycleYesButton.fire();
            cycleYesButton.setSelected(true);
            cycleFlag = true;
        }
        else if(cycleYesNo.equals("Yes") && cycleYesButton.isSelected()){
            System.out.println("THE CYCLE BUTTON WAS YES AND IT WAS SELECTED");
            cycleYesButton.fire();
            cycleYesButton.fire();
            cycleYesButton.setSelected(true);
            cycleFlag = true;
        }
        else if (cycleYesNo.equals("No") && cycleYesButton.isSelected()){
            System.out.println("THE CYCLE BUTTON WAS NO AND IT WAS SELECTED");
            cycleYesButton.fire();
            cycleYesButton.setSelected(false);
            cycleFlag = false;
        }
        else if (cycleYesNo.equals("No") && !cycleYesButton.isSelected()){
            System.out.println("THE CYCLE BUTTON WAS NO AND IT WAS NOT SELECTED");
            cycleYesButton.fire();
            cycleYesButton.fire();
            cycleYesButton.setSelected(false);
            cycleFlag = false;
        }
        System.out.println("Num processes: "+numProcesses);
//        numProcessesChoice.getSelectionModel().select(numProcesses); // this is the INDEX not the valhe

        for(int i = 0; i < processControllers.size(); i++){
            System.out.println(i);
            ProcessHolderController controller = processControllers.get(i);
            controller.setProcessPicker(processChars.charAt(i));

            controller.setDataByMap(this.stateDataMap);
            System.out.println("Controller first label: "+controller.getFirstStateLabelString());
        }
        closedSystemMenuController.goToCalculate(); // click the calculate button

    }


    private void updateTextField(TextField textField, Double newValue, boolean userInput) {
        textField.setText(String.valueOf(newValue));
        if (userInput) {
            textField.setStyle("-fx-background-color: #98FB98;"); // Pale green background for user input
        } else {
            textField.setStyle(""); // Clear the background style for non-user input
        }
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setDashboardSummary(){
        setDashboardController(Model.getInstance().getViewFactory().getDashboardController());
        if(this.dashboardController != null){
//            dashboardController.se
        }
    }

    public void hideConnectedVBoxes() {
        // Get the secondStateLabel of the current controller
        if(processControllers.size() > 0){
            ProcessHolderController currentController = processControllers.get(0);
            for (ProcessHolderController controller : processControllers) {
                // Check if the firstStateLabel of the current controller matches the secondStateLabel of the looped controller
                if (currentController.getSecondStateLabelString().equals(controller.getFirstStateLabelString())) {
                    // Hide the corresponding VBox on the looped controller
                    controller.getState1Container().setVisible(false);
                    controller.getState1Container().setStyle("-fx-opacity: 0;"+
                    "-fx-pref-width: 0;"+
                    "-fx-pref-height: 0;"+
                    "-fx-max-width: 0;"+
                    "-fx-max-height: 0;"+
                    "-fx-min-width: 0;"+
                    "-fx-min-height: 0;"+
                    "-fx-border-color: transparent;"+
                    "-fx-border-width: 0;"+
                    "-fx-background-color: transparent;");
                }
                currentController = controller;
            }

        }

    }

    public void setStatesToSaveWithProcessHeatAndWork(){

        for(ProcessHolderController process: processControllers){

        }
    }



}

