package nano.nn;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Neuron {

    private final List<Input> inputList;
    private final Activator activator;
    private final double threshold;

    public Neuron(@NotNull List<@NotNull Input> inputList,
                  @NotNull Activator activator,
                  double threshold) {
        this.inputList = inputList;
        this.activator = activator;
        this.threshold = threshold;
    }

    public double getOutput() {
        var sum = 0d;
        for (var input : this.inputList) {
            sum += input.value() * input.weight();
        }
        return this.activator.apply(sum - this.threshold);
    }

    public static record Input(double value, double weight) {
    }

    @FunctionalInterface
    public interface Activator {

        double apply(double input);
    }
}
