package com.example.capstone.Models.Logic;

import java.util.HashMap;
// import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Solver extends ACSolver{

    public Solver(List<Process> p) {
        super(p);
    }

    public Solver(Process p){
        super(p);
    }

    public Map<String, List<Double>> CompleteSolve(){
        Map<String, List<Double>> finalProccesses = new HashMap<>();

        // Process processFlag = PROCESSES.get(0);
        // while(!processFlag.allSolved()){ // may need to switch the order here to avoid infinite loop
        // 	for(Iterator<Process> p = PROCESSES.iterator(); p.hasNext(); ){
        // 		Process currentProcess = p.next();
        // 		currentProcess.solveProcess();
        // 		finalProccesses.add(currentProcess.getSolvedStatesValues());
        // 	}
        // }
        int counter = 0;
        for(Iterator<Process> p = PROCESSES.iterator(); p.hasNext(); ){
            Process currentProcess = p.next();
            while(!currentProcess.allSolved()){ // this all solved method is really the issue here of why this is an endless loop
//            while(counter < 10){
                counter++;
                currentProcess.solveProcess();
                finalProccesses.put(currentProcess.state1.toString(),currentProcess.state1.getValues());
                finalProccesses.put(currentProcess.state2.toString(),currentProcess.state2.getValues());
                if(counter > 10){
                    break;
                }
            }
        }
        return finalProccesses;
    }

    public void PrintResult(List<Map<String, List<Double>>> proccesses){
        for(Iterator<Map<String, List<Double>>> iter = proccesses.iterator(); iter.hasNext(); ){


        }
    }




}

