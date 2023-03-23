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
import javafx.scene.chart.CategoryAxis;
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

    private List<Process> processesList;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numProcessesChoice.setItems(maxProcesses);

        visualTypeChoiceBox.setItems(chartOptions);

        numProcessesChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number value, Number newValue) {
                int max = maxProcesses.get(newValue.intValue());
                switch(max){
                    case(1):
                        maxVal = 1;
                        break;
                    case(2):
                        maxVal = 2;
                        break;
                    case(3):
                        maxVal = 3;
                        break;
                    case(4):
                        maxVal = 4;
                        break;
                    case(5):
                        maxVal = 5;
                        break;

                    default:
                        System.out.println("Something was wrong in pretty");
                }
                try {

                    for (int i = 0; i < maxVal; i++){
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/Fxml/ClosedSystem/ProcessHolder.fxml"));
                        HBox box = fxmlLoader.load();

                        ProcessHolderController processHolderController = fxmlLoader.getController();
                        processControllers.add(processHolderController);
                        processHolderController.setBlank(maxVal);

                        processHolderController.setFirstStateLabel("State"+state1Indexes[i]);
                        processHolderController.setSecondStateLabel("State"+state2Indexes[i]);





                        ProcessLayout.getChildren().add(box);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        cycleYesButton.setOnAction(e -> {
            if(cycleYesButton.isSelected()){
                processControllers.get(maxVal-1).setSecondStateLabel("State"+state1Indexes[0]);
            }
            else{
                processControllers.get(maxVal-1).setSecondStateLabel("State"+state2Indexes[maxVal-1]);
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
            }
        });

        // Create the chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Line Chart Example");





//        visualScrollPane.setContent(chart);
//        visualScrollPane.setFitToWidth(true);
//        visualScrollPane.setFitToHeight(true);

        visualTypeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.equals("P-v")) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("Data Series");
                for (int i = 0; i < pressureOverTime.size(); i++) {
                    Double pressure = pressureOverTime.get(i);
                    Double volume = volumeOverTime.get(i);
                    series.getData().add(new XYChart.Data<>(pressure, volume));
                }
                chart.getData().add(series);
                visualScrollPane.setContent(chart);
            } else if (newValue.equals("T-v")) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("Data Series");
                for (int i = 0; i < tempOverTime.size(); i++) {
                    Double temp = tempOverTime.get(i);
                    Double volume = volumeOverTime.get(i);
                    series.getData().add(new XYChart.Data<>(temp, volume));
                }
                chart.getData().add(series);
                visualScrollPane.setContent(chart);
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
}
