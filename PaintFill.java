import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PaintFill {
    // Static variables to keep track of the number of updated cells and a matrix to mark updated cells
  
    static private int cellCount = 0; 
    static private boolean[][] updatedCellsMatrix;

    // Method to check if a character is an allowed color
    private static boolean isAllowedCharacter(char color) {
        String allowedChars = "RGBYWgX";
        return allowedChars.indexOf(color) >= 0;
    }

    // Method to print the coordinates of cells that have been updated
    private static void printUpdatedCellsMatrix() {
        int count = 0; 
        for (int i = 0; i < updatedCellsMatrix.length; i++) {
            boolean rowUpdated = false; 
            for (int j = 0; j < updatedCellsMatrix[i].length; j++) {
                if (updatedCellsMatrix[i][j]) {
                    if (cellCount - 1 == count) {
                        System.out.print("(" + i + "," + j + ") ");
                        break;
                    } else { 
                        System.out.print("(" + i + "," + j + "), ");
                        count++;
                        rowUpdated = true;
                    }
                }
            }
            if (rowUpdated) {
                System.out.println(); 
            }
        }
    }

    // Depth-first search algorithm to fill the area of the matrix with the replacement color
    private static void dfs(ArrayList<ArrayList<Character>> matrix, int i, int j, char target_color,char replacement_color) {
        // Base conditions to stop recursion
        if (i < 0 || j < 0 || i >= matrix.size() || j >= matrix.get(i).size() || matrix.get(i).get(j) != target_color) {
            return;
        }
        // Updating the color of the current cell
        matrix.get(i).set(j, replacement_color);
        cellCount++;
        updatedCellsMatrix[i][j] = true;

        // Recursive calls to adjacent cells
        dfs(matrix, i + 1, j, target_color, replacement_color);
        dfs(matrix, i - 1, j, target_color, replacement_color);
        dfs(matrix, i, j + 1, target_color, replacement_color);
        dfs(matrix, i, j - 1, target_color, replacement_color);
    }

    public static void main(String[] args) {
        // Check for command line argument presence
        long startTime = System.nanoTime();
        if (args.length < 1) {
            System.out.println("Please provide the test case file name as a command line argument.");
            return;
        }

        File file = new File(args[0]);
        try {
            Scanner s = new Scanner(file);
            List<String> errorMessages = new ArrayList<>();
            ArrayList<String> lines = new ArrayList<>();
            while (s.hasNextLine()) {
                lines.add(s.nextLine());
            }

            // Extracting the starting node and replacement color information
            String[] startNodeInfo;
            String replacementColorInfo;
            if (lines.size() >= 4) {
                startNodeInfo = lines.remove(lines.size() - 2).split(",");
                replacementColorInfo = lines.remove(lines.size() - 1);
            } else {
                System.out.println("Input file does not contain enough information.");
                return;
            }
            if (!lines.isEmpty()) {
                updatedCellsMatrix = new boolean[lines.size()][lines.get(0).length()];
            }

            // Validation of input matrix colors and dimensions
            boolean invalidColorDetected = false;
            int expectedLength = lines.isEmpty() ? 0 : lines.get(0).length();
            ArrayList<ArrayList<Character>> matrix = new ArrayList<>();
            for (String line : lines) {
                ArrayList<Character> row = new ArrayList<>();
                for (char ch : line.toCharArray()) {
                    if (!isAllowedCharacter(ch)) {
                        invalidColorDetected = true;
                    }
                    row.add(ch);
                }
                if (row.size() != expectedLength) {
                    errorMessages.add("Inconsistent row length detected. All rows must have the same number of columns.");
                    break;
                }
                matrix.add(row);
            }
            if (invalidColorDetected) {
                errorMessages.add("Invalid color in matrix. Only R,G,B,Y,W,g,X are allowed.");
            }

            // Validation of starting node and replacement color
            if (startNodeInfo.length != 2) {
                errorMessages.add("Invalid start node format. Expected format: row,column");
            } else {
                try {
                    int startRow = Integer.parseInt(startNodeInfo[0]);
                    int startColumn = Integer.parseInt(startNodeInfo[1]);

                    if (startRow < 0 || startRow >= matrix.size() || startColumn < 0 || startColumn >= expectedLength) {
                        errorMessages.add("Start node (" + startRow + "," + startColumn
                                + ") is outside the matrix bounds.");
                    } else if (replacementColorInfo.length() != 1 || !isAllowedCharacter(replacementColorInfo.charAt(0))) {
                        errorMessages.add("Invalid replacement color. Only R,G,B,Y,W,g,X are allowed.");
                    } else if (errorMessages.isEmpty()) {
                        // If all validations pass, proceed with the paint fill operation
                        char replacementColor = replacementColorInfo.charAt(0);
                        char targetColor = matrix.get(startRow).get(startColumn);
                        if(replacementColor == targetColor) {
                            System.out.println("Target node and replacement color are same so no changes required");
                            return;
                        }
                        
                        dfs(matrix, startRow, startColumn, targetColor, replacementColor);


                        System.out.println("Modified Matrix:");
                        for (ArrayList<Character> row : matrix) { // printing the updated matrix with the replacement color
                            for (char cell : row) {
                                System.out.print(cell + " ");
                            }
                            System.out.println();
                        }
                        System.out.println("List of cell locations modified:");
                        printUpdatedCellsMatrix(); // printing the updated matrix cells indices
                        System.out.println("Total cells updated: " + cellCount);
                    }
                } catch (NumberFormatException e) {
                    errorMessages.add("Invalid start node format. Expected format: row,column"); 
                }
            }

            // Printing any collected error messages
            if (!errorMessages.isEmpty()) {
                for (String msg : errorMessages) {
                    System.out.println(msg);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getAbsolutePath());
        }

        long endTime = System.nanoTime();
        long pTime = endTime - startTime;
        System.out.println(pTime);
    }
}
