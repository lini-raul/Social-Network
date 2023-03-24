package utils;

public class Graph {
    /**
     * DFS in a graph.
     * @param adj   adjacency matrix
     * @param visited boolean vector
     * @param vCount number of vertexes in the graph
     * @param start start vertex
     */
    public static void dfs(int[][] adj, boolean[] visited, int vCount, int start) {
        visited[start] = true;
        for (int i = 0; i < vCount; i++) {
            if (adj[start][i] > 0 && !visited[i]) {
                dfs(adj, visited, vCount, i);
            }
        }
    }
}
