package com.example.capstone.Models.Logic;

import com.example.capstone.Models.Logic.ACProcess;
import com.example.capstone.Models.Logic.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
// import java.util.Map;
// import java.security.KeyStore.Entry;
import java.util.Map;

public class Process extends ACProcess {

    State state1;
    State state2;
    char process;
    List<State> solvedStates = new ArrayList<>();
    Map<String, List<Double>> CpCvMap = new HashMap<>();
    private double heat;
    private double work;
    private double deltaEntropy;
    private double deltaEnthalpy;

    private double n;

    public Process(State state1, State state2, char process) {
        super(state1, state2, process);
        this.state1 = state1;
        this.state2 = state2;
        this.process = process;

        // this.heat = heat;
        // this.work = work;

        List<Double> CpCvColdAir = new ArrayList<>();
        CpCvColdAir.add(1.005); // Cp value for cold air
        CpCvColdAir.add(0.718); // Cv value for cold air, this will change with other types of air
        CpCvMap.put("ColdAir", CpCvColdAir);

    }

    public Process(State state1, State state2, double n, char process) {
        super(state1, state2, process);
        this.state1 = state1;
        this.state2 = state2;
        this.process = process;
        this.n = n;

        // this.heat = heat;
        // this.work = work;

        List<Double> CpCvColdAir = new ArrayList<>();
        CpCvColdAir.add(1.005); // Cp value for cold air
        CpCvColdAir.add(0.718); // Cv value for cold air, this will change with other types of air
        CpCvMap.put("ColdAir", CpCvColdAir);

    }

    // public Process(List<Process> p){
    // super(p);
    // }

    public void passProperties() {

        double p1 = state1.getPressure();
        double t1 = state1.getTemp();
        double v1 = state1.getVolume();
        double p2 = state2.getPressure();
        double t2 = state2.getTemp();
        double v2 = state2.getVolume();
        double R = 0.287;
        double n = this.getN();
        // double Cp = 1.005;
        // double Cv = 0.718;

        switch (this.process) {
            case 't': // need to check if 0 or NaN
                t2 = t1;
                this.state2.setTOriginal(t2);
                if (p1 != 0 & p2 != 0) {
                    this.setWork(R * t1 * Math.log(p1 / p2));
                } else {
                    this.setWork(R * t1 * Math.log(v2 / v1));
                }
                this.setHeat(this.getWork());
                break;
            case 'p':
                p2 = p1;
                this.state2.setPOriginal(p2);
                this.setWork(p1 * (v2 - v1));
                double CpColdAir = CpCvMap.get("ColdAir").get(0);
                this.setHeat(CpColdAir * (t2 - t1));
                break;
            case 'v':
                v2 = v1;
                this.state2.setVOriginal(v2);
                this.setWork(0);
                double CvColdAir = CpCvMap.get("ColdAir").get(1);
                this.setHeat(CvColdAir * (t2 - t1));
                break;

            case 'x': // polytropic process


                break;

            case 'y': // This should all be moved to the solve process and then deleted I believe

                // Isentropic
                // tabled materials connected to a database

                // double CpColdAir = CpCvMap.get("ColdAir").get(0);
                // double CvColdAir = CpCvMap.get("ColdAir").get(1);

                double CpColdAir1 = 1.005;
                double CvColdAir11 = 0.718;
                this.setN(CpColdAir1 / CvColdAir11);

                // this.state1.solve();

                if (p2 != 0 & v2 == 0 & t2 == 0) {
                    this.state2
                            .setVolume((this.state1.getVolume()) * (Math.pow(p2 / this.state1.getPressure(), -1 / n)));
                } else if (p2 == 0 & v2 != 0 & t2 == 0) {
                    this.state2.setPressure(this.state1.getPressure() * (Math.pow(this.state1.getVolume() / v2, n)));
                } else {
                    this.state2
                            .setPressure(this.state1.getPressure() * Math.pow(t2 / this.state1.getTemp(), n / (n - 1)));
                }
                this.state2.getValues();

                double workk = (R * (t2 - t1)) / (1 - this.getN());
                this.setWork(workk);
                this.setHeat(0);

                break;

            default:
                System.out.println("Something was wrong in solveState process");

        }

        // delta entropy and delta enthalpy calculated after passing properties and work
        // & heat
        double CpColdAir = CpCvMap.get("ColdAir").get(0);
        this.setDeltaEntropy((CpColdAir * Math.log(t1 / t2)) - (R * Math.log(p2 / p1)));
        this.setDeltaEnthalpy(CpColdAir * (t2 - t1));

        state2.setTemp(t2);
        state2.setPressure(p2);
        state2.setVolume(v2);
    }

    public List<State> solveProcess() {

        switch (this.process) {
            case 'x':
                double p2 = state2.getPressure();
                double t2 = state2.getTemp();
                double v2 = state2.getVolume();
                double n = this.getN();
                double CvColdAir = CpCvMap.get("ColdAir").get(1);

                double p1 = state1.getPressure();
                double t1 = state1.getTemp();
                double v1 = state1.getVolume();
                double R = 0.287;



                this.state1.solve();
                System.out.println("Initially solving state 1 for polytropic " + state1.getValues());

                if (p2 != 0 & v2 == 0 & t2 == 0) {
                    this.state2
                            .setVolume((this.state1.getVolume()) * (Math.pow(p2 / this.state1.getPressure(), -1 / n)));
                } else if (p2 == 0 & v2 != 0 & t2 == 0) {
                    this.state2.setPressure(this.state1.getPressure() * (Math.pow(this.state1.getVolume() / v2, n)));
                } else {
                    this.state2
                            .setPressure(this.state1.getPressure() * Math.pow(t2 / this.state1.getTemp(), n / (n - 1)));

                }
                this.state2.solve();

                double work = (R * (this.state2.getTemp() - this.state1.getTemp())) / (1 - this.getN());
                this.setWork(work);

                double heatt = (CvColdAir) * (this.state2.getTemp() - this.state1.getTemp()) + this.getWork();

                this.setHeat(heatt);
                this.setWork((R*(t2-this.state1.getTemp()))/(1-this.getN()));
                this.setHeat(CvColdAir*(t2-t1)+this.getWork());



                break;

            default:
                // List<State> solvedStates = new ArrayList<>();

                this.state1.solve();
//                System.out.println(
//                        "We are asserting that all values in " + state1.toString() + " are: " + state1.assertState());

                this.passProperties();
                this.state2.solve();
//                System.out.println(
//                        "We are asserting that all values in " + state2.toString() + " are: " + state2.assertState());

                this.solvedStates.add(state1);
                this.solvedStates.add(state2);
                return solvedStates;
        }
        return solvedStates;

    }

    public boolean allSolved() {
        boolean flag = true;

        for (Iterator<State> iter = STATES.iterator(); iter.hasNext();) {
            State s = iter.next();
            if (s.isSolved() == false) {
                flag = false;
            }
//            System.out.println(s.toString() + " State solved: " + s.getSolvedFlag() + " Flag: " + flag);
        }

        return flag;
    }

    public Map<String, List<Double>> getSolvedStatesValues() {
        Map<String, List<Double>> statesValues = new HashMap<>();
        // List<Double> vals = new ArrayList<>();
        for (Iterator<State> s = this.solvedStates.iterator(); s.hasNext();) {
            State stat = s.next();
            statesValues.put(stat.toString(), stat.getValues());
        }
        return statesValues;
    }

    public void cycle() {

        while (!this.allSolved()) {

        }
        // for(Entry<String, List<Integer>> entry : STATS1.entrySet()){
        // String n = entry.getKey();
        // List<Integer> el = entry.getValue();
        // System.out.println("Name: "+n+ "\tStats(Wins,Losses,Draws): "+el);
        // }

    }

    // getters
    public double getN() {
        return this.n;
    }

    public boolean getState1Solved() {
        return this.state1.getSolvedFlag();
    }

    public boolean getState2Solved() {
        return this.state2.getSolvedFlag();
    }

    public State getState1() {
        return this.state1;
    }

    public State getState2() {
        return this.state2;
    }

    public double getWork() {
        return this.work;
    }

    public double getHeat() {
        return this.heat;
    }

    public double getDeltaEntropy() {
        return this.deltaEntropy;
    }

    public double getDeltaEnthalpy() {
        return this.deltaEnthalpy;
    }

    // setters

    public void setHeat(double heat) {
        this.heat = heat;
    }

    public void setN(double n) {
        this.n = n;
    }

    public void setWork(double work) {
        this.work = work;
    }

    public void setDeltaEntropy(double deltaEntropy) {
        this.deltaEntropy = deltaEntropy;
    }

    public void setDeltaEnthalpy(double deltaEnthalpy) {
        this.deltaEnthalpy = deltaEnthalpy;
    }

    public char getProcess() {
        return process;
    }

    @Override
    public String toString() {
        return "Process Type: "+getProcess()+ " StateLeft: "+getState1().getStateName() + " StateRight: "+getState2().getStateName() +
                "/n"+" Pressure1: "+getState1().getPressure()+" Volume1: "+getState1().getVolume()+" Temp1: "+getState1().getTemp() +
                "/n"+" Pressure2: "+getState2().getPressure()+" Volume2: "+getState2().getVolume()+" Temp2: "+getState2().getTemp();
    }
}
