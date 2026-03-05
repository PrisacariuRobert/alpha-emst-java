# Alpha-Euclidean Minimum Spanning Tree ($\alpha$-EMST)

## 1. Introduction
This project presents an optimized computational geometry algorithm to solve the Alpha-Euclidean Minimum Spanning Tree ($\alpha$-EMST) problem for a given set of planar points. The overarching objective is to construct a Minimum Spanning Tree (MST) on a complete Euclidean graph while rigorously enforcing a strict edge-weight upper bound $\alpha$. 

Applications of threshold-constrained spanning trees are prevalent in fields such as spatial clustering, wireless sensor network topology control, and agglomerative hierarchical clustering, where connection costs beyond a certain distance are physically or economically prohibitive [1, 2].

## 2. Mathematical Formulation
Let $P = \{p_1, p_2, \dots, p_n\}$ be a finite set of points in the two-dimensional integer lattice $\mathbb{Z}^2$. We define a complete undirected graph $G = (V, E)$ where the vertex set $V = P$ and the edge set $E = \{(p_i, p_j) \mid p_i, p_j \in P, i \neq j\}$.

The weight function $w: E \to \mathbb{R}$ assigns to each edge $e = (p_i, p_j)$ the standard Euclidean distance:
$$w(e) = \|p_i - p_j\|_2 = \sqrt{(x_i - x_j)^2 + (y_i - y_j)^2}$$

The $\alpha$-EMST problem seeks a spanning tree $T \subseteq E$ that satisfies two conditions:
1.  **Minimality**: The total weight $W(T) = \sum_{e \in T} w(e)$ is globally minimized.
2.  **$\alpha$-Constraint**: $\forall e \in T, w(e) \le \alpha$.

If the subgraph $G_\alpha \subseteq G$ induced by edges $e$ where $w(e) \le \alpha$ is disconnected, no such spanning tree exists. In such cases, the algorithm must report a failure condition.

## 3. Algorithmic Approach and Complexity
The naive approach to finding an EMST on $n$ points involves constructing the complete graph of order $\mathcal{O}(n^2)$ and applying classical algorithms such as Kruskal's or Prim's, yielding an overall time complexity of $\mathcal{O}(n^2 \log n)$ or $\mathcal{O}(n^2)$ [3]. By exploiting the geometric properties of the $\alpha$-constraint, this implementation significantly improves practical performance bounds.

### 3.1 Data Structures and Optimization
The solution leverages a heavily optimized variant of Prim's Algorithm integrating spatial data structures:
-   **Spatial Grid Hashing**: The $\mathbb{R}^2$ space is partitioned into a uniform grid where the cell width $C = \max(\alpha, 1.0)$. Points are hashed into these cells. During the vertex relaxation phase of Prim's algorithm, edge evaluations are strictly localized to the current vertex's cell and its 8 adjacent Moore neighborhood cells. This geometric pruning reduces the effective degree of each vertex, dynamically bypassing the full $\mathcal{O}(n^2)$ distance evaluations.
-   **Index Minimum Priority Queue (`IndexMinPQ`)**: A custom-built binary heap maintaining the minimum distance mapping to non-tree vertices in $\mathcal{O}(\log n)$ extraction and decrease-key time.
-   **Primitive Mapping**: To prevent cache misses and object allocation overhead (common in JVM environments), Euclidean distances are evaluated dynamically using squared integer arithmetic ($d^2 \le \alpha^2$), completely circumventing expensive floating-point functions inside the hot loop.

### 3.2 Complexity Bounds
-   **Expected Time Complexity**: $\mathcal{O}(n \log n)$. Under the assumption of uniform spatial distribution, the number of neighbors within distance $\alpha$ bounded by spatial hashing is effectively constant $\mathcal{O}(1)$. Extracting the minimum edge operations thus dominates at $\mathcal{O}(n \log n)$ [4]. 
-   **Worst-Case Time Complexity**: $\mathcal{O}(n^2 \log n)$. Occurs only when the dataset is hyper-densely packed into a singular spatial hash threshold area.
-   **Space Complexity**: $\Theta(n)$. Maintained strictly via flattened parallel primitive arrays representing grids, priority queues, and parent pointers.

## 4. Usage and Execution

### 4.1 Compilation
Requires a Java Development Kit (JDK 8+).
```bash
mkdir -p bin
javac -d bin src/EMST.java
```

### 4.2 Execution
The target accepts the dataset file path and the $\alpha$ constraint.
```bash
java -cp bin EMST <input_file_path> <alpha_value>
```

**Example:**
```bash
java -cp bin EMST data/input_points1.txt 1.5
```

## 5. Input/Output Specification

### 5.1 Input Format
Points must be defined with integer coordinates, one point per line, comma-separated.
```text
x1, y1
x2, y2
...
```

### 5.2 Deterministic Output Output
The program guarantees canonical, deterministic output by breaking vertex selection ties based on Y-major coordinate ordering and strictly tracking chronological grid encounters.
1.  **Total Weight**: Printed first, formatted strictly to 10 decimal places.
2.  **Topology Sequence**: If $n \le 10$, the subset edges are printed lexicographically formatted as `(x1, y1)(x2, y2)`.
3.  **Disconnected Graph**: Outputs `FAIL` if the subgraph under threshold $\alpha$ cannot yield a valid MST.

**Sample Valid Run:**
```text
3.0000000000
(0, 0)(0, 1)
(0, 0)(1, 0)
(1, 0)(1, 1)
```

## 6. References
1.  Zahn, C. T. (1971). Graph-theoretical methods for detecting and describing gestalt clusters. *IEEE Transactions on Computers*, 100(1), 68-86.
2.  Penrose, M. (2003). *Random Geometric Graphs* (Vol. 5). Oxford university press.
3.  Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2022). *Introduction to Algorithms* (4th ed.). MIT press.
4.  Bentley, J. L., & Friedman, J. H. (1979). Data structures for range searching. *ACM Computing Surveys (CSUR)*, 11(4), 397-409.
