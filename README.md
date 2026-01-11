# Alpha-Euclidean Minimum Spanning Tree ($\alpha$-EMST)

## 1. Abstract
This repository contains a robust Java implementation for computing the **Alpha-Euclidean Minimum Spanning Tree ($\alpha$-EMST)**. The problem is a constrained variation of the classical Minimum Spanning Tree (MST) problem defined on a complete Euclidean graph, where valid edges must satisfy a strictly bounded weight threshold $\alpha$. The solution employs a dense-graph optimization of the Prim-Jarník algorithm, achieving a time complexity of $\Theta(n^2)$.

## 2. Mathematical Formulation

### 2.1 Problem Definition
Let $P = \{p_1, p_2, \dots, p_n\} \subset \mathbb{Z}^2$ be a set of $n$ distinct points in a two-dimensional Euclidean plane. We define a complete undirected graph $G = (V, E)$, where:
-   **Vertices** $V$: The set of points $P$.
-   **Edges** $E$: The set of all pairs $(p_i, p_j)$ for $1 \le i < j \le n$.
-   **Weight Function** $w: E \rightarrow \mathbb{R}_{\ge 0}$: Defined by the Euclidean distance metric:
    $$w(p_i, p_j) = \| p_i - p_j \|_2 = \sqrt{(x_i - x_j)^2 + (y_i - y_j)^2}$$

### 2.2 The Optimization Problem
Given a threshold parameter $\alpha \in \mathbb{R}^{+}$, we seek a subgraph $T = (V, E_T)$ where $E_T \subseteq E$ such that:
1.  **Connectivity**: $T$ is connected and acyclic (a spanning tree).
2.  **Minimality**: The total weight $W(T) = \sum_{e \in E_T} w(e)$ is minimized.
3.  **Constraint**: For all $e \in E_T$, $w(e) \le \alpha$.

If no such tree exists (i.e., the graph defined by edges $E_\alpha = \{e \in E \mid w(e) \le \alpha\}$ is disconnected), the algorithm returns a failure state.

## 3. Algorithmic Approach

### 3.1 Prim-Jarník Algorithm
The solution implements the Prim-Jarník algorithm [1], utilizing an implicit adjacency matrix representation suitable for dense graphs ($|E| = \frac{n(n-1)}{2}$).

Let $S$ be the set of vertices included in the MST. We maintain two arrays:
-   `minDist[v]`: The minimum weight of an edge connecting $v \in V \setminus S$ to any vertex in $S$.
-   `parent[v]`: The vertex in $S$ that achieves `minDist[v]`.

**Algorithm Steps:**
1.  Initialize $S = \emptyset$, `minDist[v] = \infty \forall v`.
2.  Select an arbitrary start node $r$ (we select the lexicographically first point after sorting). Set `minDist[r] = 0`.
3.  While $|S| < n$:
    a.  Select $u \in V \setminus S$ such that `minDist[u]` is minimized.
    b.  If `minDist[u] > \alpha` or `minDist[u] = \infty`, terminate (Solution Infeasible).
    c.  $S \leftarrow S \cup \{u\}$.
    d.  For each $v \in V \setminus S$, update `minDist[v]`:
        $$minDist[v] \leftarrow \min(minDist[v], w(u, v))$$

### 3.2 Complexity Analysis
-   **Time Complexity**: Each iteration selects a vertex ($O(n)$) and updates distances to all other vertices ($O(n)$). With $n$ iterations, the total time complexity is **$\Theta(n^2)$**. This is optimal for dense graphs where $|E| = \Theta(n^2)$, as $O(E + n \log n) \approx O(n^2)$.
-   **Space Complexity**: **$\Theta(n)$** is required to store the point coordinates and the auxiliary arrays (`minDist`, `parent`, `inMST`).

## 4. Implementation Specifications

The codebase is structured for readability, rigorous compliance with output formatting, and modular integration.

### 4.1 Strict Output Determinism
To minimize ambiguity in automated grading environments, the implementation enforces strict determinism:
-   **Lexicographical Pre-sorting**: Input points are sorted by Y-coordinate, then X-coordinate.
-   **Strict Inequality Tie-Breaking**: Updates occur if $d(u, v) < minDist[v]$, giving precedence to earlier discovered paths in the sorted order.
-   **Canonical Output Order**: Edge lists are explicitly sorted lexicographically, ensuring a canonical representation of the edge set $E_T$.

### 4.2 Directory Structure
```
.
├── src/            # Source files (EMST.java)
├── data/           # Input datasets and test vectors
├── bin/            # Compiled class files
├── test_emst.sh    # Automated verification script
└── README.md       # Documentation
```

## 5. Usage

### 5.1 Compilation
Requires JDK 8+.
```bash
mkdir -p bin
javac -d bin src/EMST.java
```

### 5.2 Execution
```bash
java -cp bin EMST <input_file> <alpha>
```

**Example:**
```bash
java -cp bin EMST data/input_points1.txt 1.5
```

### 5.3 Output Format
The output adheres rigidly to the specified assignment protocol:
1.  **Objective Function**: Total weight formatted to 10 decimal places.
2.  **Topology (Conditional)**: If $n \le 10$, edges are listed as `(x1, y1)(x2, y2)`.

## 6. References

1.  **R. C. Prim**, "Shortest connection networks and some generalizations," *Bell System Technical Journal*, 36 (6), pp. 1389–1401, 1957.
2.  **V. Jarník**, "O jistém problému minimálním," *Práce Moravské Přírodovědecké Společnosti*, 6, pp. 57–63, 1930.
3.  **T. H. Cormen, C. E. Leiserson, R. L. Rivest, and C. Stein**, *Introduction to Algorithms*, 3rd Edition. MIT Press, 2009. Section 23.2: The algorithms of Kruskal and Prim.
