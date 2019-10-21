package ratingsystems.common.linalg;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class Vector implements Serializable {
    double[] vector;

    /**
     * Constructs a new vector of size {@code size}.
     *
     * @param size Size of the new vector
     */
    public Vector(int size) {
        vector = new double[size];
    }

    /**
     * Constructs a new vector of size {@code size} with all values initialized to the value
     * {@code value}.
     *
     * @param size Size of the new vector
     * @param value Value to initialize each element
     */
    public Vector(int size, double value) {
        vector = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = value;
        }
    }

    /**
     * Constructs a new vector from an array.
     *
     * @param values Array of values to use for the new vector
     */
    public Vector(double[] values) {
        this(values.length);
        int i = 0;
        for (Double entry : values) {
            vector[i++] = entry;
        }
    }

    /**
     * Constructs a new vector from a collection.
     *
     * @param values Collection of values to use for the new vector
     */
    public Vector(Collection<Double> values) {
        this(values.size());
        int i = 0;
        for (Double entry : values) {
            vector[i++] = entry;
        }
    }

    /**
     * Returns the size of the vector.
     *
     * @return The size of the vector;
     */
    public int size() {
        return vector.length;
    }

    /**
     * Returns the value at a particular index in a vector.
     *
     * @param i the index of the value
     * @return the value at the given index
     */
    public double get(int i) {
        return vector[i];
    }

    /**
     * Sets the value at a particular index in a vector
     *
     * @param i the index in the vector
     * @param value the value to which to set the given index
     */
    public void set(int i, double value) {
        if (i >= 0 && i < vector.length) {
            vector[i] = value;
        }
    }

    /**
     * Returns a copy of the vector
     *
     * @return a copy of the vector
     */
    public Vector copy() {
        return new Vector(vector);
    }

    /**
     * Returns the magnitude of the vector.
     *
     * @return Double representation of the magnitude.
     */
    public double magnitude() {
        double mag = 0.0;
        for (double v : vector) {
            mag += Math.pow(v, 2);
        }
        return Math.sqrt(mag);
    }

    /**
     * Returns the sum of the vectors {@code this} and {@code v}.
     *
     * @param v Vector to be added.
     * @return Sum of the vectors.
     */
    public Vector add(Vector v) {
        if (vector.length != v.vector.length) {
            throw new IllegalArgumentException("Cannot compute the sum of vectors of different sizes.");
        }

        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = vector[i] + v.vector[i];
        }
        return new Vector(temp);
    }

    /**
     * Returns the vector multiplied by the scalar quantity {@code scalar}.
     *
     * @param scalar Scalar quantity by which to multiply the vector.
     * @return Vector multiplied by the scalar quantity {@code scalar}.
     */
    public Vector multiply(double scalar) {
        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = vector[i] * scalar;
        }
        return new Vector(temp);
    }

    /**
     * Returns the dot product of the vectors {@code this} and {@code v}.
     *
     * @param v Vector to be used in the dot product calculation.
     * @return Double representation of the dot product.
     */
    public double dotProduct(Vector v) {
        if (vector.length != v.vector.length) {
            throw new IllegalArgumentException("Cannot compute the dot product of vectors of different sizes.");
        }

        double dotProd = 0.0;
        for (int i = 0; i < vector.length; i++) {
            dotProd += vector[i] * v.vector[i];
        }
        return dotProd;
    }

    /**
     * Returns the cosine similarity of the vectors {@code this} and {@code v}.
     *
     * @param v Vector to be used in the cosine similarity calculation.
     * @return Double representation of the dot product.
     */
    public double cosineSimilarity(Vector v) {
        if (vector.length != v.vector.length) {
            return 0.0;
        }
        return this.dotProduct(v) / (this.magnitude() * v.magnitude());
    }

    public double minimum() {
        double minimum = Double.MAX_VALUE;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] < minimum) {
                minimum = vector[i];
            }
        }
        return minimum;
    }

    public double maximum() {
        double maximum = Integer.MIN_VALUE;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] > maximum) {
                maximum = vector[i];
            }
        }
        return maximum;
    }

    public double mean() {
        double mean = 0.0;
        for (int i = 0; i < vector.length; i++) {
            mean += vector[i];
        }
        return mean / vector.length;
    }

    public double median() {
        return quickselect(vector, vector.length / 2);
    }

    public double percentile(double percentile) {
        if (percentile >= 0 && percentile <= 1) {
            return quickselect(vector, (int) (vector.length * percentile));
        }
        return 0.0;
    }

    private double quickselect(double[] array, int index) {
        if (array.length == 0 || index >= array.length) {
            return 0.0;
        } else if (array.length == 1) {
            return array[0];
        } else if (array.length == 2) {
            if (index == 0) {
                return Math.min(array[0], array[1]);
            } else {
                return Math.max(array[0], array[1]);
            }
        }

        int pivot = (int) (Math.random() * array.length);
        double[] splitArray = new double[array.length];
        int start = 0;
        int end = array.length - 1;
        for (int i = 0; i < array.length; i++) {
            if (i != pivot) {
                if (array[i] < array[pivot]) {
                    splitArray[start++] = array[i];
                } else if (array[i] > array[pivot]) {
                    splitArray[end--] = array[i];
                } else {
                    if (Math.random() < 0.5) {
                        splitArray[start++] = array[i];
                    } else {
                        splitArray[end--] = array[i];
                    }
                }
            }
        }

        if (index == start) {
            return splitArray[index];
        } else if (index < start) {
            double[] newArray = new double[start];
            for (int i = 0; i < start; i++) {
                newArray[i] = splitArray[i];
            }
            return quickselect(newArray, index);
        } else {
            double[] newArray = new double[splitArray.length - end - 1];
            for (int i = 0; i < splitArray.length - end - 1; i++) {
                newArray[i] = splitArray[i + end + 1];
            }
            return quickselect(newArray, index - end - 1);
        }
    }

    public double similarity(Vector v) {
        if (vector.length != v.vector.length) {
            return 0.0;
        }
        double similarity = 0.0;
        for (int i = 0; i < vector.length; i++) {
            double s = vector[i] / v.vector[i];
            if (!Double.isNaN(s) && !Double.isInfinite(s)) {
                similarity += s;
            }
        }
        similarity /= vector.length;
        if (similarity > 1.0) similarity = 1.0 / similarity;
        return similarity;
    }

    public Vector softmax() {
        double sum = 0.0;
        for (int i = 0; i < vector.length; i++) {
            sum += Math.exp(vector[i]);
        }
        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = Math.exp(vector[i]) / sum;
        }
        return new Vector(temp);
    }

    public Vector modifiedSoftmax() {
        double sum = 0.0;
        for (int i = 0; i < vector.length; i++) {
            sum += Math.exp(vector[i]) - 1;
        }
        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = (Math.exp(vector[i]) - 1) / sum;
        }
        return new Vector(temp);
    }

    public Vector normalize() {
        double sum = 0.0;
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i];
        }
        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = vector[i] / sum;
        }
        return new Vector(temp);
    }

    public Vector piecewiseMultiplication(Vector v) {
        if (vector.length != v.vector.length) {
            System.out.println(vector.length + "!=" + v.vector.length);
            return null;
        }

        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = vector[i] * v.vector[i];
        }
        return new Vector(temp);
    }

    public Vector multiplicativeInverse() {
        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = 1.0 / vector[i];
        }
        return new Vector(temp);
    }

    public Vector replaceZeroes(double replacement) {
        double[] temp = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            temp[i] = vector[i] == 0.0 ? replacement : vector[i];
        }
        return new Vector(temp);
    }

    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(vector);
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < vector.length; i++) {
            res += vector[i] + ", ";
        }
        if (res.length() >= 2) {
            res = res.substring(0, res.length() - 2);
        }
        return res;
    }
}
