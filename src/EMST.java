import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EMST {

    static class Point {
        int x, y;
        int id; 

        Point(int x, int y, int id) {
            this.x = x;
            this.y = y;
            this.id = id;
        }

        double distanceTo(Point other) {
            return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
        }
    }

    static class Edge implements Comparable<Edge> {
        Point p1, p2;
        double weight;

        Edge(Point p1, Point p2, double weight) {
            this.p1 = p1;
            this.p2 = p2;
            this.weight = weight;
        }

        public String toString() {
            
            Point a = p1;
            Point b = p2;
            if (a.x > b.x || (a.x == b.x && a.y > b.y)) {
                a = p2;
                b = p1;
            }
            return String.format("(%d, %d)(%d, %d)", a.x, a.y, b.x, b.y);
        }

        @Override
        public int compareTo(Edge other) {
            return this.toString().compareTo(other.toString());
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length < 2) return;

        String inputFilePath = args[0];
        double alpha;
        try {
            alpha = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("FAIL");
            return;
        }

        List<Point> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        points.add(new Point(x, y, index++));
                    } catch (NumberFormatException e) {
                      
                    }
                }
            }
        } catch (IOException e) {
            return;
        }

        if (points.isEmpty()) {
            System.out.println("FAIL");
            return;
        }
        
        if (points.size() == 1) {
             System.out.printf("%.10f%n", 0.0);
             return;
        }

      
        Collections.sort(points, (a, b) -> {
            if (a.y != b.y) return Integer.compare(a.y, b.y);
            return Integer.compare(a.x, b.x);
        });

        solve(points, alpha);
    }

    private static void solve(List<Point> points, double alpha) {
        int n = points.size();
        boolean[] inMST = new boolean[n];
        double[] minDist = new double[n];
        int[] parent = new int[n];

        for (int i = 0; i < n; i++) {
            minDist[i] = Double.MAX_VALUE;
            parent[i] = -1;
        }

        minDist[0] = 0;
        
        List<Edge> mstEdges = new ArrayList<>();
        double totalWeight = 0;

        for (int i = 0; i < n; i++) {
            int u = -1;
            
            for (int v = 0; v < n; v++) {
                if (!inMST[v] && (u == -1 || minDist[v] < minDist[u])) {
                    u = v;
                }
            }

            if (u == -1 || minDist[u] == Double.MAX_VALUE) {
                System.out.println("FAIL");
                return;
            }
            
            if (parent[u] != -1) {
                if (minDist[u] > alpha) {
                    System.out.println("FAIL");
                    return;
                }
                totalWeight += minDist[u];
                mstEdges.add(new Edge(points.get(parent[u]), points.get(u), minDist[u]));
            }

            inMST[u] = true;

            for (int v = 0; v < n; v++) {
                if (!inMST[v]) {
                    double dist = points.get(u).distanceTo(points.get(v));
                    
                    if (dist <= minDist[v]) { 
                        minDist[v] = dist;
                        parent[v] = u;
                    }
                }
            }
        }

        System.out.printf("%.10f%n", totalWeight);

        // PDF Requirement: Print edges "only if n <= 10"
        if (n <= 10) {
            List<String> outputEdges = new ArrayList<>();
            for (Edge e : mstEdges) {
                outputEdges.add(e.toString());
            }
            
            // Sort edges lexicographically to ensure deterministic output
            Collections.sort(outputEdges);

            for (String s : outputEdges) {
                System.out.println(s);
            }
        }
    }
}