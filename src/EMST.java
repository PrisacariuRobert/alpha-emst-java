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
            long dx = (long) this.x - other.x;
            long dy = (long) this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
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
        
        int[] x = new int[n];
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            Point p = points.get(i);
            x[i] = p.x;
            y[i] = p.y;
        }

        double C = Math.max(alpha, 1.0);
        int[] cellX = new int[n];
        int[] cellY = new int[n];
        int M = Math.max(n, 1024);
        int[] head = new int[M];
        int[] next = new int[n];
        for (int i = 0; i < M; i++) head[i] = -1;

        for (int i = 0; i < n; i++) {
            cellX[i] = (int) Math.floor(x[i] / C);
            cellY[i] = (int) Math.floor(y[i] / C);
            
            long h = ((long) cellX[i] * 73856093L) ^ ((long) cellY[i] * 19349663L);
            int idx = (int) ((h & Long.MAX_VALUE) % M);
            
            next[i] = head[idx];
            head[idx] = i;
        }

        boolean[] inMST = new boolean[n];
        long[] minDistSq = new long[n];
        int[] parent = new int[n];

        for (int i = 0; i < n; i++) {
            minDistSq[i] = Long.MAX_VALUE;
            parent[i] = -1;
        }

        double alphaSq = alpha * alpha;
        
        IndexMinPQ pq = new IndexMinPQ(n);
        pq.insert(0, 0);
        minDistSq[0] = 0;
        
        List<Edge> mstEdges = new ArrayList<>();
        double totalWeight = 0;
        int visitedCount = 0;

        while (!pq.isEmpty()) {
            int u = pq.delMin();
            inMST[u] = true;
            visitedCount++;
            
            if (parent[u] != -1) {
                double dist = Math.sqrt(minDistSq[u]);
                if (dist > alpha) {
                    System.out.println("FAIL");
                    return;
                }
                totalWeight += dist;
                mstEdges.add(new Edge(points.get(parent[u]), points.get(u), dist));
            }

            int ux = x[u];
            int uy = y[u];
            int cx = cellX[u];
            int cy = cellY[u];

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = cx + dx;
                    int ny = cy + dy;
                    
                    long h = ((long) nx * 73856093L) ^ ((long) ny * 19349663L);
                    int idx = (int) ((h & Long.MAX_VALUE) % M);
                    
                    for (int v = head[idx]; v != -1; v = next[v]) {
                        if (!inMST[v] && cellX[v] == nx && cellY[v] == ny) {
                            long dX = (long) ux - x[v];
                            long dY = (long) uy - y[v];
                            long distSq = dX * dX + dY * dY;
                            
                            // Check inequality according to specs 
                            if (distSq <= alphaSq || Math.sqrt(distSq) <= alpha) {
                                if (distSq < minDistSq[v]) {
                                    minDistSq[v] = distSq;
                                    parent[v] = u;
                                    if (pq.contains(v)) pq.decreaseKey(v, distSq);
                                    else pq.insert(v, distSq);
                                } else if (distSq == minDistSq[v]) {
                                    parent[v] = u;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (visitedCount < n) {
            System.out.println("FAIL");
            return;
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

    static class IndexMinPQ {
        private int N;
        private int[] pq;
        private int[] qp;
        private long[] keys;

        public IndexMinPQ(int maxN) {
            keys = new long[maxN];
            pq = new int[maxN + 1];
            qp = new int[maxN];
            for (int i = 0; i < maxN; i++) qp[i] = -1;
        }

        public boolean isEmpty() { return N == 0; }
        public boolean contains(int i) { return qp[i] != -1; }
        
        public void insert(int i, long key) {
            N++;
            qp[i] = N;
            pq[N] = i;
            keys[i] = key;
            swim(N);
        }
        
        public int delMin() {
            int min = pq[1];
            exch(1, N--);
            sink(1);
            qp[min] = -1;
            return min;
        }
        
        public void decreaseKey(int i, long key) {
            keys[i] = key;
            swim(qp[i]);
        }

        private void swim(int k) {
            while (k > 1 && greater(k/2, k)) {
                exch(k, k/2);
                k = k/2;
            }
        }

        private void sink(int k) {
            while (2*k <= N) {
                int j = 2*k;
                if (j < N && greater(j, j+1)) j++;
                if (!greater(k, j)) break;
                exch(k, j);
                k = j;
            }
        }

        private boolean greater(int i, int j) {
            if (keys[pq[i]] != keys[pq[j]]) {
                return keys[pq[i]] > keys[pq[j]];
            }
            return pq[i] > pq[j];
        }

        private void exch(int i, int j) {
            int swap = pq[i];
            pq[i] = pq[j];
            pq[j] = swap;
            qp[pq[i]] = i;
            qp[pq[j]] = j;
        }
    }
}