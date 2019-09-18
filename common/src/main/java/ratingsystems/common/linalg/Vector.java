package ratingsystems.common.linalg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class Vector implements Serializable {
    protected double[] vector;

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
        res = res.substring(0, res.length() - 2);
        return res;
    }
}
