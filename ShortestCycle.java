import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

// Class to find the shortest cycle in a graph using adjacency matrix representation.
public class ShortestCycle {
    static int V; // Static variable to store the number of vertices in the graph.
    static double[][] graph; // Static 2D array to represent the weighted graph using an adjacency matrix.

    // Main method: the entry point of the program.
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        // Check if the filename is provided as a command-line argument.
        if (args.length < 1) {
            System.out.println("Please provide the filename as a command-line argument.");
            return; // Exit if no filename is provided.
        }

        String filename = args[0]; // Store the filename provided as a command-line argument.

        // Validate the format and data of the input file.
        if (!validateFile(filename)) {
            System.out.println("Input file is not in the correct format or contains invalid data.");
            return; // Exit if the file is not valid.
        }

        // Initialize vertices and determine the number of vertices in the graph.
        Vertices(filename);
        graph = new double[V][V]; // Initialize the adjacency matrix based on the number of vertices.
        Graph(filename); // Populate the adjacency matrix with edge weights.

        // Find the length of the smallest cycle in the graph.
        double smallestCycle = SmallestCycle();

        // Print the length of the shortest cycle. Format the output based on whether
        // the length is an integer or floating-point number.
        if (smallestCycle == (long) smallestCycle) {
            System.out.println("The length of the shortest cycle is: " + (long) smallestCycle);
        } else {
            System.out.printf("The length of the shortest cycle is: %.1f\n", smallestCycle);
        }

        long endTime = System.currentTimeMillis();
        long ptime = endTime - startTime;
        System.out.println(ptime);
    }

    // Method to validate the input file format and data.
    private static boolean validateFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Regex to ensure each line follows the expected format: vertex: edge1 weight1
                // edge2 weight2 ...
                if (!line.matches("(\\d+:)( \\d+ \\d+(\\.\\d+)?)*")) {
                    return false; // Return false if any line does not match the regex.
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename); // Print an error if the file is not found.
            System.exit(0); // Exit the program.
        }
        return true; // Return true if all lines in the file match the expected format.
    }

    // Method to count and initialize the number of vertices based on the input
    // file.
    private static void Vertices(String filename) {
        int maxVertexIndex = -1; // Keep track of the highest vertex index found.
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");
                // Check each vertex and update maxVertexIndex if necessary.
                int vertexIndex = Integer.parseInt(parts[0].replace(":", ""));
                maxVertexIndex = Math.max(maxVertexIndex, vertexIndex);
                for (int i = 1; i < parts.length; i += 2) {
                    vertexIndex = Integer.parseInt(parts[i]);
                    maxVertexIndex = Math.max(maxVertexIndex, vertexIndex);
                }
            }
            V = maxVertexIndex + 1; // Set V to the highest vertex index + 1 to accommodate 0-based indexing.
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
            System.exit(1);
        }
    }

    // Method to read the graph data from the file and populate the adjacency
    // matrix.
    private static void Graph(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            for (double[] row : graph) {
                Arrays.fill(row, 0); // Initialize all weights in the adjacency matrix to 0.
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");
                int u = Integer.parseInt(parts[0].replace(":", "")); // Parse the vertex index.
                // Parse edge data and populate the adjacency matrix.
                for (int i = 1; i < parts.length; i += 2) {
                    int v = Integer.parseInt(parts[i]); // Parse the connected vertex index.
                    double weight = Double.parseDouble(parts[i + 1]); // Parse the weight of the edge.
                    addEdge(u, v, weight); // Add the edge to the adjacency matrix.
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename); // Print an error if the file is not found.
            System.exit(1); // Exit the program.
        }
    }

    // Method to add an edge to the adjacency matrix.
    private static void addEdge(int u, int v, double weight) {
        if (u < V && v < V) { // Ensure u and v are within bounds
            graph[u][v] = weight; // Directly assign weight since the file seems to ensure no duplicate edges
        }
    }

    // Method to find the length of the smallest cycle in the graph.
    private static double SmallestCycle() {
        double smallestCycle = Double.MAX_VALUE; // Initialize the smallest cycle length to the maximum value.

        // Attempt to find the shortest cycle starting from each vertex.
        for (int i = 0; i < V; i++) {
            double[] shortest_weight = new double[V]; // Array to store the shortest distance from the source vertex.
            boolean[] visited = new boolean[V]; // Array to keep track of visited vertices.
            dijkstra(i, shortest_weight, visited); // Find shortest paths from vertex i to all other vertices.

            // Check for cycles that include vertex i.
            for (int j = 0; j < V; j++) {
                if (graph[j][i] > 0 && shortest_weight[j] != Double.MAX_VALUE) {
                    // If there is an edge from vertex j to i and a path from i to j, a cycle
                    // exists.
                    double cycleWeight = shortest_weight[j] + graph[j][i]; // Calculate the cycle length.
                    if (cycleWeight < smallestCycle) {
                        smallestCycle = cycleWeight; // Update the smallest cycle length if a smaller cycle is found.
                    }
                }
            }
        }

        return smallestCycle == Double.MAX_VALUE ? 0 : smallestCycle; // Return 0 if no cycle is found, otherwise return
                                                                      // the smallest cycle length.
    }

    // Dijkstra's algorithm to find the shortest path from a source vertex to all
    // other vertices in the graph.
    private static void dijkstra(int src, double[] shortest_weight, boolean[] visited) {
        Arrays.fill(shortest_weight, Double.MAX_VALUE); // Initialize distances to the maximum value.
        Arrays.fill(visited, false); // Mark all vertices as unvisited.
        shortest_weight[src] = 0; // Distance from the source to itself is always 0.

        // Update distances from the source to all other vertices.
        for (int count = 0; count < V - 1; count++) {
            int u = minDistance(shortest_weight, visited); // Find the vertex with the minimum distance from the source
                                                           // that has not been visited.
            visited[u] = true; // Mark the selected vertex as visited.
            for (int v = 0; v < V; v++) {
                // Update the distance of the adjacent vertices of the selected vertex.
                if (!visited[v] && graph[u][v] != 0 && shortest_weight[u] != Double.MAX_VALUE
                        && shortest_weight[u] + graph[u][v] < shortest_weight[v]) {
                    shortest_weight[v] = shortest_weight[u] + graph[u][v];
                }
            }
        }
    }

    // Method to find the vertex with the minimum distance from the source that has
    // not been visited.
    private static int minDistance(double[] shortest_weight, boolean[] visited) {
        double min = Double.MAX_VALUE; // Initialize minimum distance to the maximum value.
        int min_index = -1; // Initialize the index of the vertex with the minimum distance as -1
                            // (indicating none).
        for (int v = 0; v < V; v++)
            if (!visited[v] && shortest_weight[v] <= min) {
                min = shortest_weight[v]; // Update minimum distance.
                min_index = v; // Update the index of the vertex with the minimum distance.
            }
        return min_index; // Return the index of the vertex with the minimum distance.
    }
}
