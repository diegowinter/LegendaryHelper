package model.tictactoe;

public class Board {

	private int[][] fields = new int[3][3];
	private int availableFields;

	public Board() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				fields[i][j] = -1;
			}
		}

		this.availableFields = 9;
	}

	public boolean putElement(int x, int y, int type) {
		if (fields[x][y] == -1) {
			fields[x][y] = type;
			availableFields--;
			return true;
		} else {
			return false;
		}
	}

	public boolean verifySolution() {
		// Fist line
		if (((fields[0][0] == 0) && (fields[0][1] == 0) && (fields[0][2] == 0))
				|| ((fields[0][0] == 1) && (fields[0][1] == 1) && (fields[0][2] == 1))) {
			return true;
		}

		// Second line
		if (((fields[1][0] == 0) && (fields[1][1] == 0) && (fields[1][2] == 0))
				|| ((fields[1][0] == 1) && (fields[1][1] == 1) && (fields[1][2] == 1))) {
			return true;
		}

		// Third line
		if (((fields[2][0] == 0) && (fields[2][1] == 0) && (fields[2][2] == 0))
				|| ((fields[2][0] == 1) && (fields[2][1] == 1) && (fields[2][2] == 1))) {
			return true;
		}

		// First column
		if (((fields[0][0] == 0) && (fields[1][0] == 0) && (fields[2][0] == 0))
				|| ((fields[0][0] == 1) && (fields[1][0] == 1) && (fields[2][0] == 1))) {
			return true;
		}

		// Second column
		if (((fields[0][1] == 0) && (fields[1][1] == 0) && (fields[2][1] == 0))
				|| ((fields[0][1] == 1) && (fields[1][1] == 1) && (fields[2][1] == 1))) {
			return true;
		}

		// Third column
		if (((fields[0][2] == 0) && (fields[1][2] == 0) && (fields[2][2] == 0))
				|| ((fields[0][2] == 1) && (fields[1][2] == 1) && (fields[2][2] == 1))) {
			return true;
		}

		// Diagonal (from top left to bottom right)
		if (((fields[0][0] == 0) && (fields[1][1] == 0) && (fields[2][2] == 0))
				|| ((fields[0][0] == 1) && (fields[1][1] == 1) && (fields[2][2] == 1))) {
			return true;
		}

		// Diagonal (from top right to bottom left)
		if (((fields[0][2] == 0) && (fields[1][1] == 0) && (fields[2][0] == 0))
				|| ((fields[0][2] == 1) && (fields[1][1] == 1) && (fields[2][0] == 1))) {
			return true;
		}

		// No solution found
		return false;
	}

	public int getAvailableFields() {
		return availableFields;
	}

}
