package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;


    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static ChessPosition parsePosition(String position) throws InvalidMoveException {
        if (position.length() != 2) {
            throw new InvalidMoveException("Invalid position format. Use: <row> <col>");
        }

        char file = position.charAt(0);
        char rank = position.charAt(1);

        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new InvalidMoveException("Invalid position format. Use: <row> <col>");
        }

        int col = file - 'a' + 1;
        int row = rank - '1';

        return new ChessPosition(row, col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }
}

