package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Logic.Process;
import com.example.capstone.Models.Logic.Solver;
import com.example.capstone.Models.Logic.State;

import com.example.capstone.Models.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
//import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.knowm.xchart.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class AccountsController implements Initializable {

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
    public TextField heatInTextField;
    public TextField workInTextField;
    public TextField heatOutTextField;
    public TextField workOutTextField;
    public Label engineNotEngineLabel;
    public TextField engineEfficiencyTextField;
    public Label calculationSummaryLabel;
    public ScrollPane informationalScrollPane;
    public VBox engineEfficiencyVbox;
    public Label trueEngineEfficiency;

    private List<Process> processesList;

    private List<Process> processes = new ArrayList<>();


    private Map<String, List<Double>> m;
    public Button clearButton;

    private List<HBox> hboxParents = new ArrayList<>();

    ObservableList<Integer> maxProcesses = FXCollections.observableArrayList(1, 2, 3, 4, 5);

//    private ObservableList<String> chartOptions = FXCollections.observableArrayList("P-v", "T-v", "T-s", "P-h");
    private ObservableList<String> chartOptions = FXCollections.observableArrayList("P-v", "T-v");

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


    private double netWork, netHeat, netHeatIn, netHeatOut, netWorkIn, netWorkOut;

    private List<Double> processHeats = new ArrayList<>();
    private List<Double> processWorks = new ArrayList<>();


    String basicInstructions = "Hello and welcome to H.E.A.T.S! This is an interactive application designed to help you solve closed system thermodynamic problems. | " +
            "To start, select the number of processes you would like to work with, then toggle whether or not your problem is a cycle problem or not. Now, just fill in your data and select the process types and click compute! " +
            "After your computations are done, try checking out some of the visuals, or saving your report for later! The 4 most recent reports can be accessed on the Dashboard, and all reports can be accessed on the Saved Reports page. " +
            "Below you will see additional information after your calculations are completed: ";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        engineEfficiencyVbox.setVisible(false);

        numProcessesChoice.setItems(maxProcesses);

        visualTypeChoiceBox.setItems(chartOptions);

        calculationSummaryLabel.prefHeightProperty().bind(informationalScrollPane.prefHeightProperty());
        calculationSummaryLabel.prefWidthProperty().bind(informationalScrollPane.prefWidthProperty());
//        calculationSummaryLabel.setText(basicInstructions);



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
                        box.prefHeightProperty().bind(processesScrollPane.heightProperty());
                        box.prefWidthProperty().bind(processesScrollPane.widthProperty());
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
                compute();
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




        visualTypeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                savePvAndTvDiagrams(m);

                ImageView imageView;

                if (newValue.equals("P-v")) {
                    File pvImageFile = new File("./pv_diagram.png");
                    imageView = createImageView(pvImageFile);
                } else if (newValue.equals("T-v")) {
                    File tvImageFile = new File("./Tv_diagram.png");
                    imageView = createImageView(tvImageFile);
                } else {
                    throw new IllegalArgumentException("Invalid diagram type: " + newValue);
                }
                visualScrollPaneVBox.getChildren().clear();
                visualScrollPaneVBox.getChildren().add(imageView);

//                visualScrollPane.setContent(imageView);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
    public List<Process> processesList() {

        processes.clear();

        Map<String, State> states = new HashMap<>();
        for (int i = 0; i < processControllers.size(); i++) {
            ProcessHolderController controller = processControllers.get(i);
            Map<String, List<Double>> stateData = controller.getData();

            //System.out.println("Controller " + i + " state data: " + stateData);
            List<Double> stateLeftData = stateData.get("StateLeft");
            List<Double> stateRightData = stateData.get("StateRight");


            // Treat empty input fields as zero
            for (int j = 0; j < stateLeftData.size(); j++) {
                if (stateLeftData.get(j) == -1) {
                    stateLeftData.set(j, 0.0);
                }
                if (stateRightData.get(j) == -1) {
                    stateRightData.set(j, 0.0);
                }
            }

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
                    process = new Process(stateLeft, stateRight, nValue, processType);
                } else {
                    // else use the constructor without the n value
                    process = new Process(stateLeft, stateRight, processType);
                }
                processes.add(process);
            }

        }

        return processes;
    }

    public void compute(){

        netWork = 0;
        netHeat = 0;
        netWorkIn = 0;
        netWorkOut = 0;
        netHeatIn = 0;
        netHeatOut = 0;


        List<Process> processes = processesList();
        List<Process> processesCopy = processes.stream().map(Process::copy).collect(Collectors.toList());// for the explnation
        Solver solver;
        if (processes.size() == 1) {
            solver = new Solver(processes.get(0));
        } else {
            solver = new Solver(processes);
        }


        // Store user inputs before solving the processes
        //Map<String, List<Boolean>> userInputStatus = getInputStatus();



        try {


            List<List<Boolean>> allUserInputStatuses = new ArrayList<>();
            for (ProcessHolderController controller : processControllers) {
                allUserInputStatuses.add(controller.getInputStatus());
            }
            m = solver.CompleteSolve();
            System.out.println(m);
            String explaination= solver.generateCalculationDescription(processesCopy);

            calculationSummaryLabel.setText(explaination);




//            double netWork = 0, netHeat = 0, netHeatIn = 0, netHeatOut = 0, netWorkIn = 0, netWorkOut = 0;
            for (int i = 0; i < processes.size(); i++) {
                ProcessHolderController controller = processControllers.get(i);
                //allUserInputStatuses.add(controller.getInputStatus());

                double heat = processes.get(i).getHeat();
                double work = processes.get(i).getWork();
                netWork += work;
                netHeat += heat;
                if (heat > 0) netHeatIn += heat;
                if (heat < 0) netHeatOut += heat;
                if (work > 0) netWorkIn += work;
                if (work < 0) netWorkOut += work;



                controller.setHeatTextfield(heat);
                controller.setWorkTextField(work);
                processHeats.add(heat);
                processWorks.add(work);

                controller.setValuesAfterCompleteSolve(m);
            }
            for (int i = 0; i < processControllers.size(); i++) {
                ProcessHolderController controller = processControllers.get(i);
                controller.updateTextFieldColors(allUserInputStatuses.get(i));
            }
            netHeatTextField.setText(String.format("%.3f", netHeat));
            netWorkTextField.setText(String.format("%.3f", netWork));
            heatInTextField.setText(String.format("%.3f", netHeatIn));
            heatOutTextField.setText(String.format("%.3f", netHeatOut));
            workInTextField.setText(String.format("%.3f", netWorkIn));
            workOutTextField.setText(String.format("%.3f", netWorkOut));



            System.out.println("Net work out:" + netWork);
            System.out.println("Net heat in:" + netHeat);
            System.out.println("Heatin:" + Math.abs(netHeatIn));
            System.out.println("Heatout:" + Math.abs(netHeatOut));
            System.out.println("Work in:" + Math.abs(netWorkIn));
            System.out.println("Work out:" + Math.abs(netWorkOut));

            if(cycleFlag){
                engineEfficiencyVbox.setVisible(true);
                double engine_eff=(1)-(Math.abs(netHeatOut)/netHeatIn);
                double coefPerformanceHeatPump = netHeatIn / (netHeatIn - netHeatOut);
                double coefPerformanceAC = netHeatOut / Math.abs(netHeatIn - netHeatOut);
                // we need carnot and 2nd degree engine effiiency (true efficiency)
                if(engine_eff < 0){
                    engineNotEngineLabel.setText("Heat Pump/AC");
                    engineEfficiencyTextField.setText("COP HP: "+String.format("%.3f", coefPerformanceHeatPump)+" | COP AC: "+String.format("%.3f", coefPerformanceAC));
                }
                else if(engine_eff > 0){
                    List<Double> tempTempOverTime = new ArrayList<>();
                    tempTempOverTime.addAll(tempOverTime);
                    Collections.sort(tempTempOverTime);
                    double carnotEfficiency = 1 - (tempTempOverTime.get(0) / tempTempOverTime.get(tempTempOverTime.size()-1));
                    double trueEfficiency = engine_eff/carnotEfficiency;
                    engineNotEngineLabel.setText("Engine");
                    engineEfficiencyTextField.setText(String.format("%.3f", engine_eff));
                    trueEngineEfficiency.setText("True Engine Efficiency: "+String.format("%.3f", trueEfficiency));

                }
            }
            setDashboardController(Model.getInstance().getViewFactory().getDashboardController());
            this.dashboardController.setDashboardSummary(setDashboardSummary());

        } catch (IllegalArgumentException e) {
            showErrorAlert(e.getMessage());
        } catch (Exception e) {
            showErrorAlert("An unexpected error occurred: " + e.getMessage());
        }
    }
    private ImageView createImageView(File imageFile) {
        try {
            Image image = new Image(new FileInputStream(imageFile));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(400);
            imageView.setFitHeight(300);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            return imageView;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePvAndTvDiagrams(Map<String, List<Double>> m) throws IOException, IOException {
        // Add the initial state again to close the cycle in the graph
        List<List<Double>> statesList = new ArrayList<>(m.values());
        statesList.add(statesList.get(0));

        double[] p_values = new double[statesList.size()];
        double[] v_values = new double[statesList.size()];
        double[] T_values = new double[statesList.size()];

        for (int i = 0; i < statesList.size(); i++) {
            T_values[i] = statesList.get(i).get(0);
            p_values[i] = statesList.get(i).get(1);
            v_values[i] = statesList.get(i).get(2);
        }

        // p-v diagram
        XYChart pvChart = new XYChartBuilder().width(800).height(600).title("p-v Diagram").xAxisTitle("Specific Volume (m^3/kg)").yAxisTitle("Pressure (kPa)").build();
        pvChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        pvChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        pvChart.getStyler().setPlotBackgroundColor(Color.WHITE);
        pvChart.getStyler().setPlotGridLinesColor(new Color(255, 255, 255, 127));
        pvChart.getStyler().setChartBackgroundColor(Color.WHITE);
        pvChart.getStyler().setChartFontColor(Color.BLACK);
        pvChart.getStyler().setLegendBackgroundColor(Color.WHITE);



        int numIntermediatePoints = 1000;
        double k = 1.4; // Specific heat ratio (Cp/Cv) for an ideal gas (e.g., air)

        //plot the lines and each equation is based on the process type
        for (int i = 0; i < statesList.size() - 1; i++) {
            double[] v_intermediate = new double[numIntermediatePoints + 1];
            double[] p_intermediate = new double[numIntermediatePoints + 1];

            char processType = processes.get(i).getProcess();
            double v1 = v_values[i];
            double v2 = v_values[i + 1];
            double p1 = p_values[i];
            double p2 = p_values[i + 1];
            double T1 = T_values[i];
            double n = processes.get(i).getN();

            for (int j = 0; j <= numIntermediatePoints; j++) {
                double t = (double) j / numIntermediatePoints;
                double v;
                double p;

                if (processType == 'v') {
                    v = v1;
                    p = p1 + t * (p2 - p1);
                } else if (processType == 't') {
                    v = v1 + t * (v2 - v1);
                    p = p1 * v1 / v;
                } else if (processType == 'y') { // Isentropic process
                    v = v1 + t * (v2 - v1);
                    p = p1 * Math.pow(v1 / v, k);
                } else if (processType == 'x') { // Polytropic process
                    v = v1 + t * (v2 - v1);
                    p = p1 * Math.pow(v1 / v, n);
                } else {
                    v = v1 + t * (v2 - v1);
                    p = p1;
                }

                v_intermediate[j] = v;
                p_intermediate[j] = p;
            }

            XYSeries series = pvChart.addSeries("Process " + (i + 1) +" "+ processType, v_intermediate, p_intermediate);
            series.setMarker(SeriesMarkers.NONE);

        }

        // Save p-v diagram as an image
        BitmapEncoder.saveBitmap(pvChart, "./pv_diagram", BitmapEncoder.BitmapFormat.PNG);

        // T-v diagram
        XYChart TvChart = new XYChartBuilder().width(800).height(600).title("T-v Diagram").xAxisTitle("Specific Volume (m^3/kg)").yAxisTitle("Temperature (K)").build();
        TvChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        TvChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        TvChart.getStyler().setPlotBackgroundColor(Color.WHITE);
        TvChart.getStyler().setPlotGridLinesColor(new Color(255, 255, 255, 127));
        TvChart.getStyler().setChartBackgroundColor(Color.WHITE);
        TvChart.getStyler().setChartFontColor(Color.BLACK);
        TvChart.getStyler().setLegendBackgroundColor(Color.WHITE);


        double R=0.287;
        //same approach of tv diagram
        for (int i = 0; i < statesList.size() - 1; i++) {
            double[] v_intermediate = new double[numIntermediatePoints + 1];
            double[] T_intermediate = new double[numIntermediatePoints + 1];

            char processType = processes.get(i).getProcess();
            double v1 = v_values[i];
            double v2 = v_values[i + 1];
            double p1 = p_values[i];
            double p2 = p_values[i + 1];
            double T1 = T_values[i];
            double T2 = T_values[i + 1];

            for (int j = 0; j <= numIntermediatePoints; j++) {
                double t = (double) j / numIntermediatePoints;
                double v;
                double T;

                if (processType == 'v') {
                    v = v1;
                    double p_intermediate = p1 + t * (p2 - p1);
                    T = p_intermediate * v / R;
                } else if (processType == 't') {
                    v = v1 + t * (v2 - v1);
                    T = T1;
                } else if (processType == 'p') {
                    v = v1 + t * (v2 - v1);
                    T = p1 * v / R;
                } else if (processType == 'y') { // Isentropic process
                    v = v1 + t * (v2 - v1);
                    T = T1 * Math.pow(v1 / v, k - 1);
                } else { // Polytropic process
                    v = v1 + t * (v2 - v1);
                    double n = processes.get(i).getN();
                    T = T1 * Math.pow(v1 / v, n - 1);
                }

                v_intermediate[j] = v;
                T_intermediate[j] = T;
            }

            XYSeries series = TvChart.addSeries("Process " + (i + 1), v_intermediate, T_intermediate);
            series.setMarker(SeriesMarkers.NONE);
        }




        // Save T-v diagram as an image
        try {
            BitmapEncoder.saveBitmap(TvChart, "./Tv_diagram", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
//            ResultSet state1Data = Model.getInstance().getDatabaseDriver().getStateDataByName("State 1");
            try {

                if (resultSet.isBeforeFirst()) { // basically if the query looking for this report name already has a value, then we are gonna update it instead
//                    Model.getInstance().getDatabaseDriver().updateReport();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // otherwise, if there are no results in that query, then we make a new report
            Model.getInstance().getDatabaseDriver().newReport(reportName, numStates, "Yes", this.netHeat, this.netWork, this.maxVal, processChars);

        } else {
            ResultSet state1Data = Model.getInstance().getDatabaseDriver().getStateDataByName("State " + maxVal + 1);
            try {
//                work = state1Data.getDouble("work");
//                heat = state1Data.getDouble("heat");
                if (resultSet.isBeforeFirst()) { // basically if the query looking for this report name already has a value, then we are gonna update it instead
//                    Model.getInstance().getDatabaseDriver().updateReport();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // otherwise, if there are no results in that query, then we make a new report
            Model.getInstance().getDatabaseDriver().newReport(reportName, numStates, "No", this.netHeat, this.netWork, this.maxVal, processChars);

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
        netHeatTextField.clear();
        netWorkTextField.clear();
        heatInTextField.clear();
        heatOutTextField.clear();
        workInTextField.clear();
        workOutTextField.clear();
        engineEfficiencyTextField.clear();
        engineNotEngineLabel.setText("");
        calculationSummaryLabel.setText("");
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


    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public List<String> setDashboardSummary(){
        List<String> dashboardInfo = new ArrayList<>();
//        String [] processTypes = {"Isobaric", "Isochoric", "Isothermal", "Polytropic", "Isentropic"};
        setDashboardController(Model.getInstance().getViewFactory().getDashboardController());
        String cycleYesNo = "";
        String processType = "";
        String currentNetHeat = ""+netHeat;
        String currentNetWork = ""+netWork;

        if(this.dashboardController != null && processControllers.size() > 0){
            ProcessHolderController processHolderController1 = processControllers.get(processControllers.size()-1);
            String stateName = processHolderController1.getSecondStateLabelString();
            char processChar = processHolderController1.getProcessType();
            switch (processChar){
                case('p') -> processType = "Isobaric";
                case('v') -> processType = "Isochoric";
                case('t') -> processType = "Isothermal";
                case('x') -> processType = "Polytropic";
                case('y') -> processType = "Isentropic";
                default -> System.out.println("Something wrong with setDashboardSummary");
            }
            List<TextField> list = processHolderController1.getStateRightTextFields();

            String pressure = list.get(0).getText();
            String volume = list.get(1).getText();
            String temp = list.get(2).getText();
            if(cycleFlag){
                cycleYesNo = "Yes";
            }
            else{
                cycleYesNo = "No";
            }

            dashboardInfo.add(pressure);
            dashboardInfo.add(volume);
            dashboardInfo.add(temp);
            dashboardInfo.add(cycleYesNo);
            dashboardInfo.add(processType);
            dashboardInfo.add(stateName);
            dashboardInfo.add(currentNetWork);
            dashboardInfo.add(currentNetHeat);
            return dashboardInfo;
        }
        else{
            return dashboardInfo;
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

    private Map<String, List<Double>> getExampleData(){
        Map<String, List<Double>> exampleData = new HashMap<>();

        List<Double> state1 = new ArrayList<>();
        state1.add(1000.0);
        state1.add(0.1435);
        state1.add(500.0);
        List<Double> state2 = new ArrayList<>();
        state1.add(1600.0);
        state1.add(0.1435);
        state1.add(800.0);
        List<Double> state3 = new ArrayList<>();
        state1.add(2000.0);
        state1.add(0.1148);
        state1.add(800.0);
        List<Double> state4 = new ArrayList<>();
        state1.add(2000.0);
        state1.add(0.07175);
        state1.add(500.0);

        exampleData.put("State 1", state1);
        exampleData.put("State 2", state2);
        exampleData.put("State 3", state3);
        exampleData.put("State 4", state4);

        return exampleData;

    }

    public void loadExample(){
        System.out.println("Loading example... ");
        String cycleYesNo = "Yes";
        ClosedSystemMenuController closedSystemMenuController = new ClosedSystemMenuController();
        Map<String, List<Double>> exampleData = getExampleData();


//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        clearPage();

        setProcessChoice(4);
//        closedSystemMenuController.goToCalculate(); // click the calculate button
        String processChars = "vtpt";
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



        for(int i = 0; i < processControllers.size(); i++){
            System.out.println(i);
            ProcessHolderController controller = processControllers.get(i);
            controller.setProcessPicker(processChars.charAt(i));

            controller.setDataByMap(exampleData);
            System.out.println("Controller first label: "+controller.getFirstStateLabelString());
        }

//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
//            // Code to run after 1 second
//        }));
//        timeline.play();

        closedSystemMenuController.goToCalculate();
//        computeButton.fire();
//        visualTypeChoiceBox.getSelectionModel().select(0);


    }

}

