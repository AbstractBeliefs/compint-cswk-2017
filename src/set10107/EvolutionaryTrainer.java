package set10107;

import java.util.Arrays;

public class EvolutionaryTrainer extends NeuralNetwork {

    public EvolutionaryTrainer() {
        super();
    }

    public static void main(String[] args) {

        String dataSet;
        if (args.length >= 1) {
            dataSet = args[0];
        } else {
            dataSet = "A";
        }
        Parameters.setDataSet(dataSet);
        if (args.length >= 3) {
            if (args[1].equals("hiddennodes")) { Parameters.numHidden = Integer.parseInt(args[2]); }
            if (args[1].equals("activation")) { Parameters.activationFunction = args[2]; }
            if (args[1].equals("mutateRate")) { Parameters.mutateRate = Double.parseDouble(args[2]); }
            if (args[1].equals("mutateChange")) { Parameters.mutateChange = Double.parseDouble(args[2]); }
        }

        //System.out.println("\nThe training data is:");
        //showMatrix(Parameters.trainData, Parameters.trainData.length, 1, true);

        //System.out.println("The test data is:");
        //showMatrix(Parameters.testData, Parameters.testData.length, 1, true);

        /**
         * train the NN using our EA
         */
        EvolutionaryTrainer nn = new EvolutionaryTrainer();
        //System.out.println("\nBeginning training");
        double[] bestWeights = nn.train();

        /**
         * Show best weights found
         */
        //System.out.println("Training complete");
        //System.out.println("\nFinal weights and bias values:");
        //showVector(bestWeights, 10, 3, true);

        /**
         * Show accuracy on training data
         */
        nn.setWeights(bestWeights);
        double trainAcc = nn.testNetwork(Parameters.trainData);
        //System.out.print("training accuracy: ");
        System.out.print(trainAcc);
        System.out.print(", ");

        /**
         * Show accuracy on unseen test data
         */
        double testAcc = nn.testNetwork(Parameters.testData);
        System.out.println(testAcc);
        //System.out.println("\nEnd NN training demo");

    }

    public double[] train() {
        /**
         *  initialize the population
         */
        Individual[] population = initialise();

        /**
         * used to store a copy of the best Individual in the population
         */
        Individual bestIndividual = getBest(population);

        /**
         * main EA processing loop
         */
        int gen = 0;
        boolean done = false;
        while (gen < Parameters.maxGeneration && done == false) {

            /**
             *  this is a skeleton EA - you need to add the methods
             *  you can also change the EA if you want
             */

            //Select 2 good Individuals
            Individual parent1 = select(population); // 2 good Individuals
            Individual parent2 = select(population);

            //Generate 2 new children by crossover (includes call to mutation)
            Individual[] children = reproduce(parent1, parent2);

            //Evaluate the new individuals
            evaluateIndividuals(children);


            //Replace (sorts the population and replaces the two worst individuals)
            replace(children[0], children[1], population);

            //introduce some diversity
            injectImmigrant(population);

            //check that the best hasn't improved
            bestIndividual = getBest(population);

            //System.out.println(gen + "\t" + bestIndividual);

            //check our termination criteria
            if (bestIndividual.error < Parameters.exitError) {
                done = true;
            }

            ++gen;
        }
        return bestIndividual.chromosome;
    } // Train


    /**
     * Sets the fitness (mean squared error) of the individual passed as parameters
     */
    private void evaluateIndividuals(Individual[] individuals) {
        for (Individual individual : individuals) {
            individual.error = meanSquaredError(Parameters.trainData, individual.chromosome);
        }
    }

    /**
     * Returns the individual with the lowest error (best fitness) from the array passed
     */
    private Individual getBest(Individual[] population) {
        Individual bestIndividual = null;
        for (Individual individual : population) {
            if (bestIndividual == null) {
                bestIndividual = individual.copy();
            } else if (individual.error < bestIndividual.error) {
                bestIndividual = individual.copy();
            }
        }
        return bestIndividual;
    }

    /**
     * Generates a randomly initialised population
     */
    private Individual[] initialise() {
        Individual[] population = new Individual[Parameters.popSize];
        for (int i = 0; i < population.length; ++i) {
            population[i] = new Individual();
            double error = meanSquaredError(Parameters.trainData, population[i].chromosome);
            population[i].error = error;
        }
        return population;
    }


    /**
     * Selection --
     * <p>
     * NEEDS REPLACED with proper selection
     * this just returns a copy of a random member of the population
     */
    private Individual select(Individual[] population) {
        int popSize = population.length;
        Individual parent = population[Parameters.random.nextInt(popSize)].copy();
        return parent;
    }


    /**
     * Crossover / Reproduction
     * <p>
     * NEEDS REPLACED with proper method
     * this code just returns exact copies of the parents
     */
    private Individual[] reproduce(Individual parent1, Individual parent2) {
        int numGenes = parent1.chromosome.length;
        Individual[] result = {new Individual(), new Individual()};

        for (Individual child: result) {
            int cross = Parameters.random.nextInt(numGenes);
            for (int i = 0; i < numGenes; ++i) {
                Individual parent = i < cross ? parent1 : parent2;
                child.chromosome[i] = parent.chromosome[i];
            }
            mutate(child);
        }

        return result;
    } // Reproduce


    /**
     * Mutation
     * <p>
     * Not Implemented
     */
    private void mutate(Individual child) {
        for (int i = 0; i < child.chromosome.length; i++){
            Boolean mutate = Parameters.random.nextFloat() < Parameters.mutateRate;
            if (mutate){
                // Get a number in the range of 0-2mutateChange, then subtract mutateChange to normalise to +-mutateChange
                child.chromosome[i] += (Parameters.random.nextFloat() * (Parameters.mutateChange*2)) - Parameters.mutateChange;

                // Ensure we didn't exceed gene range
                child.chromosome[i] = Math.min(Parameters.maxGene, child.chromosome[i]);
                child.chromosome[i] = Math.max(Parameters.minGene, child.chromosome[i]);
            }
        }
    }

    /**
     * Replaces the worst two members of the population with the 2 children
     */
    private void replace(Individual child1, Individual child2, Individual[] population) {
        // place child1 and child2 replacing two worst individuals
        int popSize = population.length;
        Arrays.sort(population);
        population[popSize - 1] = child1;
        population[popSize - 2] = child2;
    }

    /**
     * Replaces the third worst member of the population with a new randomly initialised individual
     *
     * @param population
     */
    private void injectImmigrant(Individual[] population) {

        Arrays.sort(population);
        Individual immigrant = new Individual();
        evaluateIndividuals(new Individual[]{immigrant});

        // replace third worst individual
        population[population.length - 3] = immigrant;
    }

    static void showVector(double[] vector, int valsPerRow, int decimals, boolean newLine) {
        for (int i = 0; i < vector.length; ++i) {
            if (i % valsPerRow == 0)
                System.out.println("");
            if (vector[i] >= 0.0)
                System.out.print(" ");
            System.out.print(vector[i] + " ");
        }
        if (newLine == true)
            System.out.println("");
    }

    static void showMatrix(double[][] matrix, int numRows, int decimals, boolean newLine) {
        for (int i = 0; i < numRows; ++i) {
            System.out.print(i + ": ");
            for (int j = 0; j < matrix[i].length; ++j) {
                if (matrix[i][j] >= 0.0)
                    System.out.print(" ");
                else
                    System.out.print("-");
                ;
                System.out.print(Math.abs(matrix[i][j]) + " ");
            }
            System.out.println("");
        }
        if (newLine == true)
            System.out.println("");
    }
}
