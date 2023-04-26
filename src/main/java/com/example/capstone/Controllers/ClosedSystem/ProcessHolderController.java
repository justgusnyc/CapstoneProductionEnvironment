package com.example.capstone.Controllers.ClosedSystem;

import com.example.capstone.Models.Logic.Process;
import com.example.capstone.Models.Logic.State;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class ProcessHolderController implements Initializable {
    @FXML
    public TextField S2U;
    @FXML
    public TextField S2S;
    @FXML
    public TextField S2H;
    @FXML
    public TextField S1S;
    @FXML
    public TextField S1H;
    @FXML
    public TextField S1U;
    @FXML
    public TextField S1pressure;
    @FXML
    public TextField S1volume;
    @FXML
    public TextField S1temperature;
    @FXML
    public ChoiceBox ProcessPicker;
    @FXML
    public TextField NValue;
    @FXML
    public TextField S2pressure;
    @FXML
    public TextField S2volume;
    @FXML
    public TextField S2temperature;
    @FXML
    public HBox ProcessContainer;
    @FXML
    public VBox State1Container;
    @FXML
    public VBox ProcessConfigurations;
    @FXML
    public VBox State2Container;
    public Text firstStateLabel;
    public Text secondStateLabel;
    public Label S1SLabel;
    public Label S1HLabel;
    public Label S1ULabel;
    public Label S2SLabel;
    public Label S2HLabel;
    public Label S2ULabel;
    public TextField workTextField;
    public TextField heatTextfield;

    private BorderPane accountsView;
    public int c = 1;
    public int ind = 0;
    ObservableList<Character> processTypes = FXCollections.observableArrayList('p','v','t','x', 'y');
    ObservableList<String> processTypes2 = FXCollections.observableArrayList("Isobaric","Isochoric","Isothermal","Polytropic", "Isentropic");

    private char processType;

//    private ProcessHolderController processHolderController;


//    public void injectIntoMainController(AccountsController mainController) { // what does all of this do?
//        mainController.setProcessHolderController(this);
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        ProcessContainer.setStyle("-fx-background-color: white;"+"-fx-border-style: solid inside;"+"-fx-border-color: green;");
//        State1Container.setStyle("-fx-background-color: white");
//        ProcessConfigurations.setStyle("-fx-background-color: white");
//        State2Container.setStyle("-fx-background-color: white");

        ProcessPicker.setItems(processTypes2);

        ProcessPicker.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newChoiceIndex) {
                processType = processTypes.get(newChoiceIndex.intValue());
            }
        });
    }

//    public void setProcessHolderController(ProcessHolderController processHolderController) {
//        this.processHolderController = processHolderController;
//    } // why do we need this method here?

    public void setData(Process process){
//        S1pressure.requestLayout(); what would this do here?a
        State state1 = process.getState1();
        State state2 = process.getState2();
//        firstStateLabel.setText(state1.getStateName());
//        secondStateLabel.setText(state2.getStateName());

//        S2U.setText("");
//        S2S.setText("");
//        S2H.setText("");
//        S1H.setText("");
//        S1S.setText("");
//        S1U.setText("");
        S1pressure.setText(""+state1.getPressure());
        S1volume.setText(""+state1.getVolume());
        S1temperature.setText(""+state1.getTemp());
        S2pressure.setText(""+state2.getPressure());
        S2volume.setText(""+state2.getVolume());
        S2temperature.setText(""+state2.getTemp());

//        Map<String, List<Double>> solvedStatesValues= process.getSolvedStatesValues();
//        List<State> statesSolved = process.solveProcess();
//        System.out.println(statesSolved);
//        System.out.println(statesSolved.get(0).getPressure());
//        System.out.println(statesSolved.get(1).getPressure());
    }

    public List<Boolean> getInputStatus() {
        List<Boolean> values = new ArrayList<>();

        values.add(!S1temperature.getText().isEmpty());
        values.add(!S1pressure.getText().isEmpty());
        values.add(!S1volume.getText().isEmpty());

        values.add(!S2temperature.getText().isEmpty());
        values.add(!S2pressure.getText().isEmpty());
        values.add(!S2volume.getText().isEmpty());

        return values;
    }

    public void setDataByMap(Map<String, List<Double>> stateDataMap){
        for (Map.Entry<String, List<Double>> stateData : stateDataMap.entrySet()) {
            String stateName = stateData.getKey();
            List<Double> stateValues = stateData.getValue();

            System.out.println("Statename before match: " + stateName);
            System.out.println("first label phc: " + this.getFirstStateLabelString());
            System.out.println("second label: " + this.getSecondStateLabelString());

            if (stateName.equals(this.getFirstStateLabelString())) {
                // Do something when the state name matches the input
                System.out.println("Match found for state name: " + stateName);
                System.out.println("Values for " + stateName + ": " + stateValues);

                S1pressure.setText(""+stateValues.get(0));
                S1volume.setText(""+stateValues.get(1));
                S1temperature.setText(""+stateValues.get(2));

            } else if(stateName.equals(this.getSecondStateLabelString())){
                // Do something else when the state name does not match the input
                System.out.println("No match found for state name: " + stateName);

                S2pressure.setText(""+stateValues.get(0));
                S2volume.setText(""+stateValues.get(1));
                S2temperature.setText(""+stateValues.get(2));
            }
        }


    }
    public void updateTextFieldColors(List<Boolean> userInputStatus) {
        setGreenIfUserInput(S1pressure, userInputStatus.get(1));
        setGreenIfUserInput(S1volume, userInputStatus.get(2));
        setGreenIfUserInput(S1temperature, userInputStatus.get(0));

        setGreenIfUserInput(S2pressure, userInputStatus.get(4));
        setGreenIfUserInput(S2volume, userInputStatus.get(5));
        setGreenIfUserInput(S2temperature, userInputStatus.get(3));
    }

    private void setGreenIfUserInput(TextField textField, boolean isUserInput) {
        if (isUserInput) {
            textField.setStyle("-fx-background-color: #98FB98;");
        } else {
            textField.setStyle("");
        }
    }

    public void setValuesAfterCompleteSolve(Map<String, List<Double>> completeSolve){
        for (Map.Entry<String, List<Double>> stateData : completeSolve.entrySet()) {
            String stateName = stateData.getKey();
            List<Double> stateValues = stateData.getValue();

            System.out.println("Statename before match: " + stateName);
            System.out.println("first label phc: " + this.getFirstStateLabelString());
            System.out.println("second label: " + this.getSecondStateLabelString());

            if (stateName.equals(this.getFirstStateLabelString())) {
                // Do something when the state name matches the input
                System.out.println("Match found for state name: " + stateName);
                System.out.println("Values for " + stateName + ": " + stateValues);

                S1pressure.setText(""+stateValues.get(1));
                S1volume.setText(""+stateValues.get(2));
                S1temperature.setText(""+stateValues.get(0));

            } else if(stateName.equals(this.getSecondStateLabelString())){
                // Do something else when the state name does not match the input
                System.out.println("No match found for state name: " + stateName);

                S2pressure.setText(""+stateValues.get(1));
                S2volume.setText(""+stateValues.get(2));
                S2temperature.setText(""+stateValues.get(0));
            }
        }


    }

    public void setBlank(int maxVal){

        S2U.setText("");
        S2S.setText("");
        S2H.setText("");
        S1H.setText("");
        S1S.setText("");
        S1U.setText("");
        S1pressure.setText("");
        S1volume.setText("");
        S1temperature.setText("");
        S2pressure.setText("");
        S2volume.setText("");
        S2temperature.setText("");

//        String processTypes[] = {"Temperature", "Pressure", "Volume"};
//        ProcessPicker.setItems(FXCollections.observableArrayList(processTypes));



//        int ind2 = ind+1;
//        firstStateLabel.setText("State "+ind);
//        secondStateLabel.setText("State "+ind2);
//        if(c % 2 == 0){
//            ind++;
//        }
//        c++;



    }

    public Map<String, List<Double>> getData(){
        // the indexing of each of those list of doubles will follow same order as UI, that is to say item @ index 0 would be pressure, last index is U
        Map<String, List<Double>> stateData = new HashMap<>();
        List<Double> state1Data = new ArrayList<>();
        List<Double> state2Data = new ArrayList<>();
//
//        double S2uIn = makeDouble(S2U);
//        double S2sIn = makeDouble(S2S);
//        double S2hIn = makeDouble(S2H);
//        double S1hIn = makeDouble(S1H);
//        double S1sIn = makeDouble(S1S);
//        double S1uIn = makeDouble(S1U);
        double S1pressureIn = makeDouble(S1pressure);
        System.out.println(S1pressureIn);
        double S1volumeIn = makeDouble(S1volume);
        double S1temperatureIn = makeDouble(S1temperature);
        double S2pressureIn = makeDouble(S2pressure);
        double S2volumeIn = makeDouble(S2volume);
        double S2temperatureIn = makeDouble(S2temperature);

        state1Data.add(S1pressureIn);
        state1Data.add(S1volumeIn);
        state1Data.add(S1temperatureIn);
//        state1Data.add(S1sIn);
//        state1Data.add(S1hIn);
//        state1Data.add(S1uIn);

        state2Data.add(S2pressureIn);
        state2Data.add(S2volumeIn);
        state2Data.add(S2temperatureIn);
//        state2Data.add(S2sIn);
//        state2Data.add(S2hIn);
//        state2Data.add(S2uIn);


        stateData.put("StateLeft", state1Data);
        stateData.put("StateRight", state2Data);

        return stateData;
    }

    public void setAccountsView(BorderPane accountsView) {
        this.accountsView = accountsView;
    }

    public BorderPane getAccountsView() {
        return accountsView;
    }

    private double makeDouble(TextField input){
        try {
            double inputValue = Double.parseDouble(input.getText());
            return inputValue;

        } catch (NumberFormatException e) {
//            System.out.println("Error not a double number");
            return 0;
        }
    }

    public char getProcessType() {
        return processType;
    }

    public void setProcessType(char processType) {
        this.processType = processType;
    }

    public void setFirstStateLabel(String firstStateLabel) {
        this.firstStateLabel.setText(firstStateLabel);
    }



    public String getFirstStateLabelString() {
        return firstStateLabel.getText();
    }

    public void setSecondStateLabel(String secondStateLabel) {
        this.secondStateLabel.setText(secondStateLabel);
    }

    public String getSecondStateLabelString() {
        return secondStateLabel.getText();
    }

    public void setS1pressure(TextField s1pressure) {
        S1pressure = s1pressure;
    }

    public void setS1temperature(TextField s1temperature) {
        S1temperature = s1temperature;
    }

    public void setS1volume(TextField s1volume) {
        S1volume = s1volume;
    }

    public void setS2pressure(TextField s2pressure) {
        S2pressure = s2pressure;
    }

    public void setS2temperature(TextField s2temperature) {
        S2temperature = s2temperature;
    }

    public void setS2volume(TextField s2volume) {
        S2volume = s2volume;
    }

    public TextField getS1pressure() {
        return S1pressure;
    }

    public TextField getS1temperature() {
        return S1temperature;
    }

    public TextField getS1volume() {
        return S1volume;
    }

    public TextField getS2pressure() {
        return S2pressure;
    }

    public TextField getS2temperature() {
        return S2temperature;
    }

    public TextField getS2volume() {
        return S2volume;
    }

    public VBox getState1Container() {
        return State1Container;
    }

    public VBox getState2Container() {
        return State2Container;
    }

    public VBox getProcessConfigurations() {
        return ProcessConfigurations;
    }

    public List<TextField> getStateLeftTextFields(){
        List<TextField> leftTextFields = new ArrayList<>();
        leftTextFields.add(S1pressure);
        leftTextFields.add(S1volume);
        leftTextFields.add(S1temperature);
        return leftTextFields;
    }

    public List<TextField> getStateRightTextFields(){
        List<TextField> rightTextFields = new ArrayList<>();
        rightTextFields.add(S2pressure);
        rightTextFields.add(S2volume);
        rightTextFields.add(S2temperature);
        return rightTextFields;
    }

    public void setProcessPicker(char processtype) {

        switch (processtype){
            case('p') -> ProcessPicker.getSelectionModel().select(0);
            case('v') -> ProcessPicker.getSelectionModel().select(1);
            case('t') -> ProcessPicker.getSelectionModel().select(2);
            case('x') -> ProcessPicker.getSelectionModel().select(3);
            case('y') -> ProcessPicker.getSelectionModel().select(4);
            default -> System.out.println("Something wrong setting up process types");
        }

    }

    public void setHeatTextfield(double heat) {
        this.heatTextfield.setText(String.format("%.2f", heat));
    }

    public void setWorkTextField(double work) {
        this.workTextField.setText(String.format("%.2f", work));
    }

    //    public void clearStateValues() {
//
//    }
}
