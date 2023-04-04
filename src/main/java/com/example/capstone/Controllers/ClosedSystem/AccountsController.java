package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Logic.Process;
import com.example.capstone.Models.Logic.Solver;
import com.example.capstone.Models.Logic.State;
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
    public Button clearButton;
    public ScrollPane processesScrollPane;
    public ChoiceBox visualTypeChoiceBox;
    public ScrollPane visualScrollPane;
    public VBox visualScrollPaneVBox;
    public Button FormatButton;

    private List<Process> processesList;

    private List<HBox> hboxParents = new ArrayList<>();

    ObservableList<Integer> maxProcesses = FXCollections.observableArrayList(1, 2, 3, 4, 5);

    private ObservableList<String> chartOptions = FXCollections.observableArrayList("P-v", "T-v", "T-s", "P-h");

    int [] state1Indexes = {1,2,3,4,5};
    int [] state2Indexes = {2,3,4,5,6};
    private int maxVal;

    private BorderPane accountsView;

    private List<ProcessHolderController> processControllers = new ArrayList<>();

    private LineChart<Number, Number> chart;
    List<Double> pressureOverTime = new ArrayList<>();
    List<Double> volumeOverTime = new ArrayList<>();
    List<Double> tempOverTime = new ArrayList<>();

    List<List<TextField>> textFields = new ArrayList<>();
    List<String> textFieldsStatesNames = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numProcessesChoice.setItems(maxProcesses);

        visualTypeChoiceBox.setItems(chartOptions);

        Map<String, List<List<TextField>>> connectedTextField = new HashMap<>();

        numProcessesChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number value, Number newValue) {
                int max = maxProcesses.get(newValue.intValue());
                switch(max) {
                    case 1:
                        maxVal = 1;
                        break;
                    case 2:
                        maxVal = 2;
                        break;
                    case 3:
                        maxVal = 3;
                        break;
                    case 4:
                        maxVal = 4;
                        break;
                    case 5:
                        maxVal = 5;
                        break;
                    default:
                        System.out.println("Something was wrong in pretty");
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
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



        cycleYesButton.setOnAction(e -> {
            if(cycleYesButton.isSelected()) {
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
            } else {
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

                processControllers.get(lastIndex).setSecondStateLabel("State"+state2Indexes[maxVal-1]);
            }
        });

        FormatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getTextFields();

                for(int i = 0; i < textFieldsStatesNames.size(); i++){
                    int controllerIndexOuter = (int) Math.ceil((i+1)/2);
                    String name = textFieldsStatesNames.get(i);
                    for (int j = 0; j < textFields.size(); j++){
                        if((i != j) && (name == textFieldsStatesNames.get(j))){
                            int controllerIndexInner = (int) Math.ceil((j+1)/2);
                            int leftOrRightState = j%2;
                            ProcessHolderController outerController = processControllers.get(controllerIndexOuter);
                            ProcessHolderController innerController = processControllers.get(controllerIndexInner);
                            if(leftOrRightState == 0){
                                outerController.setS2pressure(textFields.get(i).get(0));
                                innerController.setS1pressure(textFields.get(i).get(0));

                                outerController.setS2volume(textFields.get(i).get(1));
                                innerController.setS1Volume(textFields.get(i).get(1));

                                outerController.setS2temperature(textFields.get(i).get(2));
                                innerController.setS1temperature(textFields.get(i).get(2));
                            }
                            else{
                                outerController.setS1pressure(textFields.get(i).get(0));
                                innerController.setS2pressure(textFields.get(i).get(0));

                                outerController.setS1Volume(textFields.get(i).get(1));
                                innerController.setS2volume(textFields.get(i).get(1));

                                outerController.setS1temperature(textFields.get(i).get(2));
                                innerController.setS2temperature(textFields.get(i).get(2));
                            }


                        }
                    }
                }

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
                    State stateLeft = new State(stateLeftData.get(2), stateLeftData.get(0), stateLeftData.get(1), controller.getFirstStateLabelString());
                    State stateRight = new State(stateRightData.get(2), stateRightData.get(0), stateRightData.get(1), controller.getSecondStateLabelString());
                    states.put(controller.getFirstStateLabelString(), stateLeft);
                    states.put(controller.getSecondStateLabelString(), stateRight);
                    System.out.println("left "+stateLeft);
                    System.out.println("right" +stateRight);
//                    System.out.println(stateLeft.getPressure());
                    Process process = new Process(states.get(controller.getSecondStateLabelString()), states.get(controller.getFirstStateLabelString()), controller.getProcessType());

                    processes.add(process);
                }

                Collections.sort(pressureOverTime);
                Collections.sort(tempOverTime);
                Collections.sort(volumeOverTime);


                List<Process> processes2 = processesList();
                Solver solver = new Solver(processes);
                System.out.println(solver.CompleteSolve());
                for (int i = 0; i < processControllers.size(); i++) {
                    ProcessHolderController controller = processControllers.get(i);
                    controller.setData(processes.get(i));
                    System.out.println(processes.get(i));
                }


            }
        });

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ProcessLayout.getChildren().clear();
                visualScrollPane.setContent(null);
                
            }
        });

// Create the chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Visualization Presets");





//        visualScrollPane.setContent(chart);
//        visualScrollPane.setFitToWidth(true);
//        visualScrollPane.setFitToHeight(true);

        visualTypeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.equals("P-v")) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("P-v");
                for (int i = 0; i < pressureOverTime.size(); i++) {
                    Double pressure = pressureOverTime.get(i);
                    Double volume = volumeOverTime.get(i);
                    series.getData().add(new XYChart.Data<>(pressure, volume));
                }
                chart.getData().add(series);
                visualScrollPane.setContent(chart);
//                visualScrollPane.setFitToWidth(true);
//                visualScrollPane.setFitToHeight(true);
            } else if (newValue.equals("T-v")) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("T-v");
                for (int i = 0; i < tempOverTime.size(); i++) {
                    Double temp = tempOverTime.get(i);
                    Double volume = volumeOverTime.get(i);
                    series.getData().add(new XYChart.Data<>(temp, volume));
                }
                chart.getData().add(series);
                visualScrollPane.setContent(chart);
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

    public List<Process> processesList(){
        List<Process> ls = new ArrayList<>();


        State state1 = new State(0, 1000, 0, "State 1"); // we assume that passed properties from processes to be overriding
        State state2 = new State(0, 0, 0.1435, "State 2");
        State state3 = new State(800, 0, 0, "State 3");
        State state4 = new State(0, 2000, 0, "State 4");
//        Process process1 = new Process(state2, state1, 'v');
//        Process process2 = new Process(state3, state2, 't');
//        Process process3 = new Process(state4, state3, 'p');
//        Process process4 = new Process(state1, state4, 't');

        Process process1 = new Process(state2, state1, 'v');
        Process process2 = new Process(state3, state2, 't');
        Process process3 = new Process(state4, state3, 'p');
        Process process4 = new Process(state1, state4, 't');

        ls.add(process1);
        ls.add(process2);
        ls.add(process3);
        ls.add(process4);


        return ls;
    }

    private void getTextFields(){

        for(int i = 0; i < hboxParents.size(); i++){
            List<TextField> textsLeft = new ArrayList<>();
            List<TextField> textsRight = new ArrayList<>();
            String leftStateName = processControllers.get(i).getFirstStateLabelString();
            String rightStateName = processControllers.get(i).getSecondStateLabelString();

            HBox box = hboxParents.get(i);
            TextField S1Pressure = (TextField) box.lookup("#S1pressure");
            TextField S1Volume = (TextField) box.lookup("#S1Volume");
            TextField S1Temp = (TextField) box.lookup("#S1temperature");
            textsLeft.add(S1Pressure);
            textsLeft.add(S1Volume);
            textsLeft.add(S1Temp);
            textFields.add(textsLeft);
            textFieldsStatesNames.add(leftStateName);

            TextField S2Pressure = (TextField) box.lookup("#S2pressure");
            TextField S2Volume = (TextField) box.lookup("#S2volume");
            TextField S2Temp = (TextField) box.lookup("#S2temperature");
            textsRight.add(S2Pressure);
            textsRight.add(S2Volume);
            textsRight.add(S2Temp);
            textFields.add(textsRight);
            textFieldsStatesNames.add(rightStateName);
        }


//        return textFields;
    }



    public List<HBox> getHBoxes(HBox hbox){
        List<HBox> hboxList = new ArrayList<>();
        for (Node child : hbox.getChildren()) {
            if (child instanceof HBox) {
                hboxList.add((HBox) child);
            }
        }
        return hboxList;
    }

    public void compute(){

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
}
