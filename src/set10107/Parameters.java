package set10107;

import java.util.Random;

import set10107.data.Reader;

public class Parameters {

    public static int numInput = 1;
    public static int numHidden = 6;
    public static int numOutput = 1;
    public static double minGene = -5; // specifies minimum and maximum weight values
    public static double maxGene = +5;
    public static int popSize = 50;
    public static int maxGeneration = 10000;
    public static double exitError = 0.0; // terminate if MSE smaller than this value
    public static double mutateRate = 0.20; // mutation rate for mutation operator
    public static double mutateChange = 0.01; // mutation rate for mutation operator
    public static final long seed = System.currentTimeMillis();
    public static double[][] trainData;
    public static double[][] testData;
    public static int numGenes;
    public static Random random = new Random(seed);
    public static String activationFunction = "tanh";

    public static void setDataSet(String dataSet) {
        trainData = Reader.getTrainingData(dataSet);
        testData = Reader.getTestData(dataSet);
        if (dataSet.equals("C")) {
            numInput = 2;
        }
    }
}
