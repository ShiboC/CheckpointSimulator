# CheckpointSimulator
a simulator to compare the detailed execution time under different checkpointing strategies.

The main method is in simulator.java. User can set up three types of checkpoint strategy with CheckpointStrategyFactory. All parmeters are pre configured in simulator.java main method, user can change the configuration code based on their needs. For compute time, checkpoint time and failure time interval, we also provide DataGenerator.java to help users generate data with specified distribution.

Notice that the simulator can fail by time interval or superstep. If the simulator fail with time interval, user can choose 'simulator.generateResultByTime()'. If failure by superstep is needed, user can choose 'simulator.generateResultByStep()'. The results will be stored in a list (ArrayList<IterationUnit>). User can print out the list on their own or use the CSVUtils we provide to export the results in a CSV.
