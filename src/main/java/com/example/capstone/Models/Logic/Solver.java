package com.example.capstone.Models.Logic;


import java.util.*;

public class Solver extends ACSolver {

    public Solver(List<Process> p) {
        super(p);
        sortProcesses();
    }

    public Solver(Process p) {
        super(p);
        sortProcesses();
    }

    private void sortProcesses() {
        Collections.sort(PROCESSES, (Process a, Process b) -> {
            int knownVarsA = a.state1.countKnownProperties() + a.state2.countKnownProperties();
            int knownVarsB = b.state1.countKnownProperties() + b.state2.countKnownProperties();
            return knownVarsA - knownVarsB; //was knownVarsB-knownVarsA.
        });
    }

    private void propagateKnownInformation() {
        for (int i = 0; i < PROCESSES.size(); i++) {
            Process currentProcess = PROCESSES.get(i);
            for (int j = i + 1; j < PROCESSES.size(); j++) {
                Process otherProcess = PROCESSES.get(j);

                // Check if both processes share the same state and call passProperties() accordingly
                if (currentProcess.state1.equals(otherProcess.state1)) {
                    currentProcess.passProperties();
                    otherProcess.passProperties();
                } else if (currentProcess.state1.equals(otherProcess.state2)) {
                    currentProcess.passProperties();
                    otherProcess.passPropertiesReverse();
                } else if (currentProcess.state2.equals(otherProcess.state1)) {
                    currentProcess.passPropertiesReverse();
                    otherProcess.passProperties();
                } else if (currentProcess.state2.equals(otherProcess.state2)) {
                    currentProcess.passPropertiesReverse();
                    otherProcess.passPropertiesReverse();
                }
            }
        }
    }
    public Map<String, List<Double>> CompleteSolve() {
        Map<String, List<Double>> finalProccesses = new TreeMap<>();

        while (true) {
            boolean newStatesSolved = false;

            propagateKnownInformation();
            sortProcesses();

            for (Iterator<Process> p = PROCESSES.iterator(); p.hasNext();) {
                Process currentProcess = p.next();

                boolean prevState1Solved = currentProcess.state1.isSolved();
                boolean prevState2Solved = currentProcess.state2.isSolved();

                currentProcess.solveProcess();

                if (!prevState1Solved && currentProcess.state1.isSolved()) {
                    newStatesSolved = true;
                }
                if (!prevState2Solved && currentProcess.state2.isSolved()) {
                    newStatesSolved = true;
                }

                finalProccesses.put(currentProcess.state1.toString(), currentProcess.state1.getValues());
                finalProccesses.put(currentProcess.state2.toString(), currentProcess.state2.getValues());
            }

            if (!newStatesSolved) {
                break;
            }
        }

        return finalProccesses;
    }

    public String generateCalculationDescription(List<Process> processes) {
        StringBuilder description = new StringBuilder();

        for (int i = 0; i < processes.size(); i++) {
            Process process = processes.get(i);
            State initialState = process.getState1();
            State finalState = process.getState2();
            String processType = process.getProcessDescription();

            description.append(String.format("Process %d (%s): %s is connected to %s, and the %s is constant. ",
                    i + 1, processType, initialState.getName(), finalState.getName(), processType.split(" ")[1]));

            Map<String, Double> knownValues = initialState.getKnownValues();

            for (String value : knownValues.keySet()) {
                String unit = "";
                switch (value) {
                    case "pressure":
                        unit = "kPa";
                        break;
                    case "volume":
                        unit = "m^3/kg";
                        break;
                    case "temperature":
                        unit = "K";
                        break;
                }
                description.append(String.format("%s has an initial %s of %.2f %s. ", initialState.getName(), value, knownValues.get(value), unit));
            }

            // Check if the connected state has the required value for the calculation
            boolean canCalculate = false;
            String equation = "";
            String missingValue = "";
            knownValues.get("pressure").doubleValue();
            switch (processType) {
                case "constant volume":
                    if (knownValues.containsKey("pressure") && finalState.getKnownValues().containsKey("volume") && knownValues.get("pressure")>0) {
                        canCalculate = true;
                        equation = "PV=nRT";
                        missingValue = "temperature";
                    }
                    else{
                        canCalculate = true;
                        equation = "P=T*R/v";
                        missingValue = "pressure";
                    }
                    break;
                case "constant temperature":
                    if (knownValues.containsKey("pressure") && finalState.getKnownValues().containsKey("temperature") && knownValues.get("pressure") > 0) {
                        canCalculate = true;
                        equation = "P1V1=P2V2";
                        missingValue = "volume";
                    } else if (knownValues.containsKey("volume") && finalState.getKnownValues().containsKey("temperature") && knownValues.get("volume") > 0) {
                        canCalculate = true;
                        equation = "P=T*R/V";
                        missingValue = "pressure";
                    }
                    break;
                case "constant pressure":
                    if (knownValues.containsKey("temperature") && finalState.getKnownValues().containsKey("pressure") && knownValues.get("temperature")>0) {
                        canCalculate = true;
                        equation = "V1/T1=V2/T2";
                        missingValue = "volume";
                    }
                    else{
                        canCalculate = true;
                        equation = "T= p*v /R";
                        missingValue = "temperature";
                    }
                    break;
                case "constant polytropic":
                    String missingValues = "";
                    if (finalState.getKnownValues().containsKey("pressure")) {
                        missingValues = "temperature and volume";
                        equation = "V2 = V1 * (P2/P1)^(-1/n)";
                    } else if (finalState.getKnownValues().containsKey("volume")) {
                        missingValues = "pressure and temperature";
                        equation = "P2 = P1 * (V1/V2)^n";
                    } else if (finalState.getKnownValues().containsKey("temperature")) {
                        missingValues = "pressure and volume";
                        equation = "P2 = P1 * (T2/T1)^(n/(n-1))";
                    }

                    if (!missingValues.isEmpty()) {
                        canCalculate = true;
                        missingValue = missingValues;
                    }
                    break;
                case "constant isentropic":

//                    String missingValues = "";
                    if (finalState.getKnownValues().containsKey("pressure")) {
                        missingValues = "temperature and volume";
                        equation = "V2 = V1 * (P2/P1)^(-1/n)";
                    } else if (finalState.getKnownValues().containsKey("volume")) {
                        missingValues = "pressure and temperature";
                        equation = "P2 = P1 * (V1/V2)^n";
                    } else if (finalState.getKnownValues().containsKey("temperature")) {
                        missingValues = "pressure and volume";
                        equation = "P2 = P1 * (T2/T1)^(n/(n-1))";
                    }

//                    if (!missingValues.isEmpty()) {
//                        canCalculate = true;
//                        missingValue = missingValues;
//                    }
                    break;
            }

            if (canCalculate) {
                description.append(String.format("Since %s now has the initial values and the passed value from state %s, we can calculate the missing %s in state %s using the equation: %s\n", initialState.getName(), finalState.getName(), missingValue, initialState.getName(), equation, process.getN()));
            } else {
                description.append("The initial state has insufficient information to calculate the missing values. Ensure that the required information is provided for other states and processes.\n");
            }
        }

        return description.toString();
    }

    public void PrintResult(List<Map<String, List<Double>>> proccesses) {
        for (Iterator<Map<String, List<Double>>> iter = proccesses.iterator(); iter.hasNext(); ) {

        }
    }
}
