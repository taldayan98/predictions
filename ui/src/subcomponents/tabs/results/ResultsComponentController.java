package subcomponents.tabs.results;

import dto.Dto;
import dto.entity.DtoEntity;
import engine.simulation.execution.SimulationExecution;
import engine.simulation.execution.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import subcomponents.app.AppController;
import subcomponents.tabs.results.logic.task.TaskSimulationPause;
import subcomponents.tabs.results.logic.task.TaskSimulationResume;
import subcomponents.tabs.results.logic.task.TaskSimulationRunningDetails;
import subcomponents.tabs.results.logic.task.TaskSimulationStop;
import subcomponents.tabs.results.logic.task.population.FillPopulation;

import java.util.HashMap;
import java.util.Map;

public class ResultsComponentController {
    @FXML
    private VBox simulationDetails;
    @FXML
    private VBox simulationProgressDetails;
    @FXML
    private HBox singleSimulationDetails;
    @FXML
    private Label simulationID;
    @FXML
    private ImageView progressImage;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resumeButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button rerunButton;
    @FXML
    private GridPane entityGrid;
    @FXML
    private Label currTickLabel;
    @FXML
    private Label runningTimeLabel;
    @FXML
    private LineChart<?,?> popultionGraph;
    //@FXML
    //private CategoryAxis entityNameCategory;
    @FXML
    private NumberAxis entityNameCategory;

    @FXML
    private NumberAxis ticksNumberCategory;

    @FXML
    private TreeView<?> histoeamEntityTree;
    @FXML
    private Tab populationHistogramTab;
    @FXML
    private Tab consistencyTab;
    @FXML
    private Tab averageValueTab;
    @FXML
    private Label entityNameStaticLabel;
    @FXML
    private Label populationStaticLabel;
    private SimpleLongProperty propertyCurrTick;
    private SimpleLongProperty runningTimeProperty;
    private SimpleLongProperty population;
    private AppController mainController;
    private Timeline runningTime;
    private Integer secondsCount = 1;
    private Map<String, SimpleIntegerProperty> propertyMap;
    private TableView<FillPopulation> populationTableView;
    private TaskSimulationRunningDetails task;


    public ResultsComponentController(){
        propertyCurrTick = new SimpleLongProperty();
        propertyMap = new HashMap<>();
        runningTimeProperty = new SimpleLongProperty();
        populationTableView = new TableView<>();
    }
    @FXML
    void initialize() {
        currTickLabel.textProperty().bind(Bindings.format("%,d", propertyCurrTick));
        runningTimeLabel.textProperty().bind(Bindings.format("%,d", runningTimeProperty));
        entityNameCategory.setTickUnit(1); // Set the tick unit to 1 to display only integers
        entityNameCategory.setLowerBound(0);
        //populationLabel.textProperty().bind(Bindings.format("%,d", population));
    }
   /* @FXML
    void viewSimulationDetails(MouseEvent event) {
        Label label = (Label) event.getSource();
        Pattern pattern = Pattern.compile("\\d+");

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(label.getText());
        // Find the first match
        if (matcher.find()) {
            String index = matcher.group(); // Get the matched number as a string
            int parsedNumber = Integer.parseInt(index); // Convert it to an integer if needed
            Simulation simulation = mainController.getModel().getSimulationById(parsedNumber);
            entityPopulation(simulation);
        }
    }*/

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void addSimulation(int simulationID, boolean isFinished) {
        String text = "Simulation no. " + simulationID;
        simulationDetails.getChildren().add(createSimulationHbox(text));

    }

    public HBox createSimulationHbox(String text) {
        Image image;
        Label labelSimulationId = new Label(text);
        Label labelSimulationStatus = new Label(" Process");
        HBox dynamicVBox = new HBox();
        dynamicVBox.getChildren().addAll(labelSimulationId,labelSimulationStatus);

        EventHandler<MouseEvent> HBoxClickHandler = event -> {
            HBox clicked = (HBox) event.getSource();
            int index = simulationDetails.getChildren().indexOf(clicked);
            // set curr simulation on the currect simulation
            mainController.getModel().setCurrSimulationId(index+1);
            this.populationTableView = addEntityToTable();
            simulationProgressDetails.getChildren().add(populationTableView);
            task.runTask();
            setPropertyLineChart();
            System.out.println("Label clicked: " + ((Label)clicked.getChildren().get(0)).getText() );
        };
        dynamicVBox.setOnMouseClicked(HBoxClickHandler);

        return dynamicVBox;

    }

   /* public void entityPopulation(Simulation simulation) {
        int rowIndex = 0;
        List<DtoEntity> entityList = simulation.
        //for(EntityStructure entity : simulation.getWorld().getEntityStructures().values()) {
        for(EntityStructure entity : simulation.getWorld().getEntityStructures().values()) {
            Label name = new Label(entity.getEntityName());
            entityGrid.add(entityNameStaticLabel, 0, rowIndex);
            entityGrid.add(name, 1, rowIndex);
            entityGrid.add(populationStaticLabel, 2, rowIndex);
            Label pop = new Label();
            pop.textProperty().bind(Bindings.format("%,d", population));
            rowIndex++;
        }

    }*/


    public void runSimulation() {
        mainController.getModel().runSimulation();
//        runningTime = createTimer();
//        runningTime.setCycleCount(Timeline.INDEFINITE);
//        runningTime.play();
        new Thread(()->{
            this.task = new TaskSimulationRunningDetails(mainController.getModel().getCurrSimulationId(),mainController.getModel().getSimulation(),
                    propertyCurrTick, runningTimeProperty,simulationDetails , populationTableView, propertyMap);

        }).start();
    }

    @FXML
    void pauseOnclick(ActionEvent event) {
        new Thread(new TaskSimulationPause(
                mainController.getModel().getCurrSimulationId(),mainController.getModel().getSimulation(), simulationDetails
        )).start();
    }

    @FXML
    void resumeOnclick(ActionEvent event) {
        new Thread(new TaskSimulationResume(
                mainController.getModel().getCurrSimulationId(),mainController.getModel().getSimulation(),propertyCurrTick,
                runningTimeProperty,simulationDetails, populationTableView, propertyMap
        )).start();
    }

    @FXML
    void stopOnClick(ActionEvent event) {
        new Thread(new TaskSimulationStop(
                mainController.getModel().getCurrSimulationId(),mainController.getModel().getSimulation(), simulationDetails, populationTableView, propertyMap
        )).start();
    }


    public void setPropertyLineChart(){
        SimulationExecution simulationExecution = mainController.getModel().getCurrSimulation(); // last function updated the index if the curr simulation
        if(simulationExecution.getSimulationStatus() == Status.FINISH) {
            // Clear the chart by removing all data series
            popultionGraph.getData().clear();
            Dto dto = mainController.getModel().getDtoWorld();

            for (String entityName : dto.getEntities().keySet()) {
                //XYChart.Series<Integer, Integer> series = new XYChart.Series<>();
                XYChart.Series series = new XYChart.Series();
                series.setName(entityName);
                int index = 0;

                for (Integer amount : dto.getEntities().get(entityName).getPopulationHistoryList()) {
                    series.getData().add(new XYChart.Data(index + 1, amount));
                    index++;
                }
                popultionGraph.getData().add(series);
            }
        }
    }

    private Timeline createTimer() {
        EventHandler<ActionEvent> eventHandler = event -> {
            secondsCount++;
            runningTimeLabel.setText(secondsCount.toString());
        };

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), eventHandler);
        return new Timeline(keyFrame);
    }

    public TableView<FillPopulation> addEntityToTable() {
        TableView<FillPopulation> tableView;
        if(this.populationTableView.getItems().isEmpty()) {
            tableView = new TableView<>();
            //entityTable = new TableView<FillPopulation>();
            TableColumn entityName = new TableColumn<FillPopulation, String>("Entity Name");
            entityName.setCellValueFactory(new PropertyValueFactory<>("entityName"));
            TableColumn population = new TableColumn<FillPopulation, Integer>("Current Population");
            population.setCellValueFactory(new PropertyValueFactory<>("population"));
            tableView.getColumns().add(entityName);
            tableView.getColumns().add(population);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
        else {
            tableView = this.populationTableView;
            tableView.getItems().clear();
        }
        for(DtoEntity currEntityName : mainController.getDtoWorld().getEntities().values()) {
            FillPopulation row = new FillPopulation(currEntityName.getEntityName());
            propertyMap.put(currEntityName.getEntityName(), new SimpleIntegerProperty());
            row.populationProperty().bind(propertyMap.get(currEntityName.getEntityName()));
            tableView.getItems().add(row);


        }
        return tableView;
    }

}
