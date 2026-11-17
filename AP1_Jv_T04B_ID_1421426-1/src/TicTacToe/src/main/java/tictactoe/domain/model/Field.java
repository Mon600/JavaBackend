package tictactoe.domain.model;

import lombok.Getter;

@Getter
public class Field {
    private static final int rows = 3;
    private static final  int cols = 3;
    private final int[][] fieldMatrix;

    public Field() {
        this.fieldMatrix = new int[rows][cols];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }


    public int[][] getFieldMatrix() {
        int[][] fieldCopy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(fieldMatrix[i], 0, fieldCopy[i], 0, cols);
        }
        return fieldCopy;
    }

    public static Field getFieldCopy(Field original) {
        Field copy = new Field();
        int[][] src = original.getFieldMatrix();
        for (int i = 0; i < original.getRows(); i++) {
            System.arraycopy(src[i], 0, copy.fieldMatrix[i], 0, original.getCols());
        }
        return copy;
    }


    public void setFieldValue(FieldPosition turnPos, int turn) {
        int row = turnPos.getRow();
        int col = turnPos.getCol();
        if (row > rows - 1 || row < 0 || col > cols - 1 || col < 0) {
            return;
        }
        if (this.fieldMatrix[row][col] != 0) {
            return;
        }
        if (turn < 1 || turn > 2) {
            return;
        }
        this.fieldMatrix[row][col] = turn;
    }

    public int getFieldValue(FieldPosition position) {
        int row = position.getRow();
        int col = position.getCol();
        if (row > rows - 1 || row < 0 || col > cols - 1 || col < 0) {
            throw new IllegalArgumentException(
                    String.format("Incorrect coordinates [%d %d]. Range: row [0-%d], col[0-%d]",
                            row,
                            col,
                            rows - 1,
                            cols - 1)
            );
        }
        return fieldMatrix[position.getRow()][position.getCol()];
    }
}