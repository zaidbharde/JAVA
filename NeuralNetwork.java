import java.util.*;

public class NeuralNetwork {

    private double[][] weightsIH;
    private double[][] weightsHO;
    private double[] biasH;
    private double[] biasO;
    private final int inputSize, hiddenSize, outputSize;
    private final double learningRate;
    private final List<Double> lossHistory = new ArrayList<>();

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, double lr) {
        this.inputSize  = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = lr;

        Random rng = new Random(42);
        weightsIH = initWeights(inputSize, hiddenSize, rng);
        weightsHO = initWeights(hiddenSize, outputSize, rng);
        biasH = new double[hiddenSize];
        biasO = new double[outputSize];
    }

    private double[][] initWeights(int rows, int cols, Random rng) {
        double[][] w = new double[rows][cols];
        double scale = Math.sqrt(2.0 / rows);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                w[i][j] = rng.nextGaussian() * scale;
        return w;
    }

    private double sigmoid(double x) { return 1.0 / (1.0 + Math.exp(-Math.max(-500, Math.min(500, x)))); }
    private double sigmoidDeriv(double x) { return x * (1 - x); }

    private double[] forward(double[] input, double[][] weights, double[] bias) {
        double[] output = new double[bias.length];
        for (int j = 0; j < bias.length; j++) {
            output[j] = bias[j];
            for (int i = 0; i < input.length; i++)
                output[j] += input[i] * weights[i][j];
            output[j] = sigmoid(output[j]);
        }
        return output;
    }

    public double[] predict(double[] input) {
        double[] hidden = forward(input, weightsIH, biasH);
        return forward(hidden, weightsHO, biasO);
    }

    public void train(double[][] inputs, double[][] targets, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;

            for (int s = 0; s < inputs.length; s++) {
                double[] hidden = forward(inputs[s], weightsIH, biasH);
                double[] output = forward(hidden, weightsHO, biasO);

                double[] outputError = new double[outputSize];
                double[] outputDelta = new double[outputSize];
                for (int j = 0; j < outputSize; j++) {
                    outputError[j] = targets[s][j] - output[j];
                    outputDelta[j] = outputError[j] * sigmoidDeriv(output[j]);
                    totalLoss += outputError[j] * outputError[j];
                }

                double[] hiddenError = new double[hiddenSize];
                double[] hiddenDelta = new double[hiddenSize];
                for (int j = 0; j < hiddenSize; j++) {
                    for (int k = 0; k < outputSize; k++)
                        hiddenError[j] += outputDelta[k] * weightsHO[j][k];
                    hiddenDelta[j] = hiddenError[j] * sigmoidDeriv(hidden[j]);
                }

                for (int j = 0; j < hiddenSize; j++)
                    for (int k = 0; k < outputSize; k++)
                        weightsHO[j][k] += learningRate * outputDelta[k] * hidden[j];
                for (int k = 0; k < outputSize; k++)
                    biasO[k] += learningRate * outputDelta[k];

                for (int i = 0; i < inputSize; i++)
                    for (int j = 0; j < hiddenSize; j++)
                        weightsIH[i][j] += learningRate * hiddenDelta[j] * inputs[s][i];
                for (int j = 0; j < hiddenSize; j++)
                    biasH[j] += learningRate * hiddenDelta[j];
            }

            double avgLoss = totalLoss / inputs.length;
            lossHistory.add(avgLoss);

            if ((epoch + 1) % 1000 == 0)
                System.out.printf("  Epoch %5d | Loss: %.6f%n", epoch + 1, avgLoss);
        }
    }

    public void plotLoss() {
        int height = 10;
        int width = Math.min(60, lossHistory.size());
        double maxLoss = lossHistory.stream().mapToDouble(Double::doubleValue).max().orElse(1);

        System.out.println("\n  Loss curve:");
        for (int row = height; row >= 0; row--) {
            double threshold = maxLoss * row / height;
            StringBuilder line = new StringBuilder("  ");
            line.append(String.format("%6.3f│", threshold));
            for (int col = 0; col < width; col++) {
                int idx = col * lossHistory.size() / width;
                line.append(lossHistory.get(idx) >= threshold ? "█" : " ");
            }
            System.out.println(line);
        }
        System.out.println("        └" + "─".repeat(width));
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  🧠 Neural Network from Scratch");
        System.out.println("=".repeat(50));

        System.out.println("\n  --- XOR Problem ---");
        NeuralNetwork xor = new NeuralNetwork(2, 8, 1, 0.5);
        double[][] xorIn  = {{0,0},{0,1},{1,0},{1,1}};
        double[][] xorOut = {{0},{1},{1},{0}};
        xor.train(xorIn, xorOut, 5000);

        System.out.println("\n  XOR Predictions:");
        for (double[] in : xorIn) {
            double[] out = xor.predict(in);
            System.out.printf("    [%.0f, %.0f] → %.4f (expected: %.0f) %s%n",
                in[0], in[1], out[0], (in[0] != in[1] ? 1.0 : 0.0),
                Math.abs(out[0] - (in[0] != in[1] ? 1 : 0)) < 0.2 ? "✅" : "❌");
        }
        xor.plotLoss();

        System.out.println("\n  --- Binary Classifier ---");
        Random rng = new Random(42);
        int n = 200;
        double[][] data   = new double[n][2];
        double[][] labels = new double[n][1];

        for (int i = 0; i < n; i++) {
            double x = rng.nextDouble() * 4 - 2;
            double y = rng.nextDouble() * 4 - 2;
            data[i] = new double[]{x, y};
            labels[i] = new double[]{x * x + y * y < 2 ? 1 : 0};
        }

        NeuralNetwork circle = new NeuralNetwork(2, 16, 1, 0.3);
        circle.train(data, labels, 3000);

        int correct = 0;
        for (int i = 0; i < n; i++) {
            double pred = circle.predict(data[i])[0];
            if ((pred > 0.5) == (labels[i][0] > 0.5)) correct++;
        }
        System.out.printf("\n  Circle classifier accuracy: %d/%d (%.1f%%)%n",
            correct, n, correct * 100.0 / n);

        System.out.println("\n  Decision boundary (10x10 grid):");
        for (double y = 2; y >= -2; y -= 0.4) {
            System.out.print("  ");
            for (double x = -2; x <= 2; x += 0.4) {
                double pred = circle.predict(new double[]{x, y})[0];
                System.out.print(pred > 0.5 ? "██" : "░░");
            }
            System.out.println();
        }
    }
}
