package subcomponents.tabs.results.logic.task;

import dto.Dto;
import engine.simulation.Simulation;
import engine.simulation.execution.SimulationExecution;
import engine.simulation.execution.Status;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;

public class TaskSimulationRunningDetails {
    private int simulationId;
    private Simulation simulation;
    private SimpleLongProperty propertyCurrTick;
    private Timeline timeline;

    public TaskSimulationRunningDetails(int simulationId, Simulation simulation, SimpleLongProperty propertyCurrTick, Timeline timeline) {
        this.simulationId = simulationId;
        this.simulation = simulation;
        this.propertyCurrTick = propertyCurrTick;
        this.timeline = timeline;
    }

    public void runTask(){
        Status status;
        // Do while + change to funcition that return DTO in Simulation by ID
        //SimulationExecution currSimulation = simulation.getSimulationById(simulationId);
        // get status
        do{

            status = simulation.getSimulationById(simulationId).getSimulationStatus();
            Dto dto = simulation.getSimulationById(simulationId).createWorldDto();

            // function in Simulation.getDtoDetailByID
            Platform.runLater(() -> {
                propertyCurrTick.set(dto.getCurrTicks());
            });
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // Handle the InterruptedException if needed
            }
        }
        while (status!= Status.FINISH &&  status != Status.PAUSE);

        timeline.stop();
        /*while(currSimulation.getSimulationStatus()!= Status.FINISH &&  currSimulation.getSimulationStatus() != Status.PAUSE){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // Handle the InterruptedException if needed
            }

            // function in Simulation.getDtoDetailByID
            Dto dto = currSimulation.getWorld().createDto();
            Platform.runLater(() -> {
                propertyCurrTick.set(dto.getCurrTicks());
            });


        }*/
    }

}
