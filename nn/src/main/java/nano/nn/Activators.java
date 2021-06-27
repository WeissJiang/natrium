package nano.nn;

import org.apache.commons.math3.analysis.function.Sigmoid;

public abstract class Activators {

    public static Neuron.Activator step() {
        return input -> input < 0 ? 0 : 1;
    }

    public static Neuron.Activator sgn() {
        return input -> input <= 0 ? -1 : 1;
    }

    public static Neuron.Activator linear() {
        return input -> input;
    }

    public static Neuron.Activator sigmoid() {
        return new Sigmoid(0, 1)::value;
    }

}
