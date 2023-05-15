package com.northernneckgarbage.nngc.google_routing;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class PathInfo {
    private static int[][] two_dim_cost_matrix;
    private static boolean[] visited_copy_array = new boolean[100];
    private static int no_of_vertices;
    private static int current_vertex = 1;
    private static int total_min_cost = 0;
    private static ArrayList < Integer > pathList = new ArrayList < Integer > ();

    public PathInfo(int[][] two_dim_cost_matrix, int no_of_vertices) {
        this.two_dim_cost_matrix = two_dim_cost_matrix;
        this.no_of_vertices = no_of_vertices;
    }

    public static int next_visit(int c_vertex) {
        int MIN = Integer.MAX_VALUE;
        int cost_spent = 0, next_vertext = Integer.MAX_VALUE;
        for (int i = 0; i < no_of_vertices; i++) {
            if (two_dim_cost_matrix[c_vertex - 1][i] != 0 && !visited_copy_array[i]) {
                if (two_dim_cost_matrix[c_vertex - 1][i] + two_dim_cost_matrix[i][c_vertex - 1] < MIN) {
                    MIN = two_dim_cost_matrix[c_vertex - 1][i] + two_dim_cost_matrix[i][c_vertex - 1];
                    cost_spent = two_dim_cost_matrix[c_vertex - 1][i];
                    next_vertext = i;
                }
            }
        }
        if (MIN != Integer.MAX_VALUE) {
            total_min_cost += cost_spent;
        }
        return next_vertext + 1;
    }

    public static void shortest_distance(int c_vertex) {
        visited_copy_array[c_vertex - 1] = true;
        pathList.add(c_vertex);
        int nxt_visit = next_visit(c_vertex);
        if (nxt_visit == Integer.MAX_VALUE + 1) {
            pathList.add(current_vertex);
            total_min_cost += two_dim_cost_matrix[c_vertex - 1][current_vertex - 1];
            return;
        }
        shortest_distance(nxt_visit);
    }

	public void calculatePath(){
		// System.out.println("\nSalesman's path: ");
		shortest_distance(current_vertex);
		// System.out.println(pathList);

		// System.out.println("\nTour cost: " + total_min_cost);
	}

	public ArrayList<Integer> getOptimalPath(){
		return pathList;
	}
	
	public int getTotalMinimalCost(){
		return total_min_cost;
	}
	
	public void reset(){
		two_dim_cost_matrix = new int[][]{};
		visited_copy_array = new boolean[100];
		Arrays.fill(visited_copy_array, false);
		no_of_vertices = 0;
		current_vertex = 1;
		total_min_cost = 0;
		pathList = new ArrayList<Integer>();
	}

    public int getNoOfVertices() {
        return no_of_vertices;
    }

}
