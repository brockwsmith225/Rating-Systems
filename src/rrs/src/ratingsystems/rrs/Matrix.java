package ratingsystems.rrs;

import java.util.Collection;

public class Matrix {
    protected double[][] matrix;

    /**
     * Constructs a new matrix with {@code rows} rows and {@code columns} columns.
     *
     * @param rows Number of rows in the new matrix
     * @param columns Number of columns in the new matrix
     */
    public Matrix(int rows, int columns) {
        matrix = new double[rows][columns];
    }

    /**
     * Constructs a new matrix from a 2D array.
     *
     * @param values 2D array of values to use for the new matrix
     */
    public Matrix(double[][] values) {
        this(values.length, values[0].length);
        int r = 0;
        int c = 0;
        for (double[] vals : values) {
            for (double entry : vals) {
                matrix[r][c] = entry;
                c = (c + 1) % values[0].length;
            }
            r++;
        }
    }

    /**
     * Constructs a new matrix from a 2D collection.
     *
     * @param values 2D collection of values to use for the new matrix
     */
    public Matrix(Collection<Collection<Double>> values) {
        this(values.size(), values.iterator().next().size());
        int r = 0;
        int c = 0;
        for (Collection<Double> vals : values) {
            for (double entry : vals) {
                matrix[r][c++] = entry;
            }
            r++;
        }
    }

    /**
     * Returns the number of rows in the matrix.
     *
     * @return Number of rows in the matrix
     */
    public int rows() {
        return matrix.length;
    }

    /**
     * Returns the number of columns in the matrix.
     *
     * @return Number of columns in the matrix
     */
    public int columns() {
        return matrix[0].length;
    }

    /**
     * Returns the value at a particular index in the matrix
     *
     * @param row the row of the value
     * @param column the column of the value
     * @return the value at the specified index
     */
    public double get(int row, int column) { return matrix[row][column]; }

    /**
     * Sets the value at a particular index in the matrix
     *
     * @param row the row of the value
     * @param column the column of the value
     * @param value the value
     */
    public void set(int row, int column, double value) {
        matrix[row][column] = value;
    }

    /**
     * Returns a copy of the matrix
     *
     * @return a copy of the matrix
     */
    public Matrix copy() {
        return new Matrix(matrix);
    }

    /**
     * Calculates the sum of two matrices.
     *
     * @param m Matrix to be added
     * @return Resultant matrix
     */
    public Matrix add(Matrix m) {
        if (matrix.length != m.matrix.length || matrix[0].length != m.matrix[0].length) {
            throw new IllegalArgumentException("Cannot add matrices of different sizes.");
        }

        double[][] temp = new double[matrix.length][matrix[0].length];
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                temp[r][c] = matrix[r][c] + m.matrix[r][c];
            }
        }
        return new Matrix(temp);
    }

    /**
     * Creates a new matrix equal to the current matrix multiplied by a scalar
     *
     * @param s the scalar multiplier
     * @return a new matrix
     */
    public Matrix multiply(double s) {
        double[][] res = new double[matrix.length][matrix[0].length];
        for (int r = 0; r < res.length; r++) {
            for (int c = 0; c < res[0].length; c++) {
                res[r][c] = s * matrix[r][c];
            }
        }
        return new Matrix(res);
    }

    /**
     * Calculates the product of a matrix and a vector.
     *
     * @param v Vector to be multiplied
     * @return Resultant vector
     */
    public Vector multiply(Vector v) {
        if (matrix[0].length != v.vector.length) {
            throw new IllegalArgumentException("Number of columns in the matrix and the vector size must match.");
        }

        double[] res = new double[matrix.length];
        for (int r = 0; r < matrix.length; r++) {
            res[r] = 0;
            for (int c = 0; c < matrix[r].length; c++) {
                res[r] += matrix[r][c] * v.vector[c];
            }
        }
        return new Vector(res);
    }

    /**
     * Calculates the product of two matrices.
     *
     * @param m Matrix to be multiplied
     * @return Resultant matrix
     */
    public Matrix multiply(Matrix m) {
        if (matrix[0].length != m.matrix.length) {
            throw new IllegalArgumentException("Number of columns in the first matrix and the number of rows in the second matrix must match.");
        }

        double[][] res = new double[matrix.length][m.matrix[0].length];
        for (int r = 0; r < res.length; r++) {
            for (int c = 0; c < res[r].length; c++) {
                res[r][c] = 0;
                for (int i = 0; i < matrix[0].length; i++) {
                    res[r][c] += matrix[r][i] * m.matrix[i][c];
                }
            }
        }
        return new Matrix(res);
    }

    /**
     * Calculates the row reduced version of the matrix.
     *
     * @return Row reduced version of the matrix
     */
    public Matrix rowReduce() {
        Matrix m = new Matrix(matrix);
        for (int p = 0; p < m.matrix.length; p++) {
            if (m.matrix[p][p] != 0) {
                for (int r = 0; r < m.matrix.length; r++) {
                    double multiplier = m.matrix[r][p] / m.matrix[p][p];
                    if (r != p) {
                        for (int c = p; c < m.matrix.length; c++) {
                            m.matrix[r][c] -= m.matrix[p][c] * multiplier;
                            if (Math.abs(m.matrix[r][c]) < 0.00000000001) {
                                m.matrix[r][c] = 0;
                            }
                        }
                    }
                }
            }
        }
        return m;
    }

    /**
     * Gets the eigenvector corresponding to a given eigenvalue
     *
     * @param eigenvalue the eigenvalue
     * @return the eigenvector that corresponds to the eigenvalue
     */
    public Vector getEigenvector(double eigenvalue) {
        Matrix m = new Matrix(matrix);
        m = m.add(generateIdentityMatrix(m.rows()).multiply(-1 * eigenvalue));
        m = m.rowReduce();
        if (m.get(m.rows() - 1, m.columns() - 1) != 0.0) {
            return null;
        }
        double[] v = new double[m.rows()];
        double sum = 0.0;
        for (int i = 0; i < m.rows() - 1; i++) {
            v[i] = -1.0 * m.get(i, m.columns() - 1) / m.get(i, i);
            sum += v[i];
        }
        v[m.rows()-1] = 1.0;
        sum += 1.0;
        for (int i = 0; i < v.length; i++) {
            v[i] /= sum;
        }
        return new Vector(v);
    }

    @Override
    public String toString() {
        String res = "";
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                res += matrix[r][c] + " ";
            }
            res += "\n";
        }
        return res;
    }

    /**
     * Generates an identity matrix of a certain size
     *
     * @param size the size of the identity matrix
     * @return an identity matrix of the specified size
     */
    public static Matrix generateIdentityMatrix(int size) {
        Matrix identity = new Matrix(size, size);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (r == c) {
                    identity.matrix[r][c] = 1;
                } else {
                    identity.matrix[r][c] = 0;
                }
            }
        }
        return identity;
    }
}
