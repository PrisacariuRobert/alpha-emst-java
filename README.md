# Alpha-Euclidean Minimum Spanning Tree

## Overview
This project implements an algorithm to compute the Alpha-Euclidean Minimum Spanning Tree ($\alpha$-EMST) for a given set of 2D points. The solution applies Prim's algorithm to construct a Minimum Spanning Tree (MST) on a complete Euclidean graph, subject to the constraint that no edge weight exceeds a specified threshold factor $\alpha$.

## Mathematical Formulation
Given a set of points $P = \{p_1, p_2, \dots, p_n\}$ in $\mathbb{Z}^2$, let $G = (V, E)$ be a complete undirected graph where $V = P$. The weight of an edge $e_{ij} = (p_i, p_j)$ is defined as the Euclidean distance $d(p_i, p_j) = \|p_i - p_j\|_2$.

The objective is to find a spanning tree $T \subseteq E$ such that:
1.  The sum of weights $W(T) = \sum_{e \in T} w(e)$ is minimized.
2.  For every edge $e \in T$, $w(e) \le \alpha$.

If the graph cannot be connected under the constraint $\alpha$, or if the graph is inherently disconnected, the algorithm reports a failure.

## Implementation Details
The solution is implemented in Java and utilizes Prim's Algorithm optimized for dense graphs.

-   **Algorithm**: Prim's Algorithm (Adjacency Matrix implicit representation).
-   **Time Complexity**: $\Theta(n^2)$, where $n$ is the number of points. This constitutes an optimal approach for dense graphs where $|E| \approx n^2/2$, avoiding the overhead of logarithmic data structures like binary heaps which are advantageous only in sparse graphs.
-   **Space Complexity**: $\Theta(n)$ for maintaining `minDist` and `parent` arrays.

### Tie-Breaking and Determinism
To ensure deterministic output consistent with rigorous grading standards:
1.  **Coordinate Sorting**: Points are pre-sorted in Y-major order ($y$ then $x$).
2.  **Edge Selection**: The algorithm employs strict inequality checks (`dist < minDist`) during relaxation steps.
3.  **Lexicographical Output**: When $n \le 10$, output edges are sorted lexicographically to guarantee a unique, canonical representation of the tree structure.

## Usage

### Compilation
Ensure a Java Development Kit (JDK) 8 or higher is installed. Compile the source code:

```bash
mkdir -p bin
javac -d bin src/EMST.java
```

### Execution
Execute the program by providing the input file path and the alpha threshold parameter.

```bash
java -cp bin EMST <input_file_path> <alpha_value>
```

**Example:**
```bash
java -cp bin EMST data/input_points1.txt 1.5
```

## Input/Output Specification

### Input
The input file must contain 2D points with integer coordinates, one point per line, in comma-separated format.

```text
x1, y1
x2, y2
...
```

### Output
The program writes the result to Standard Output (stdout).

1.  **Total Weight**: The total weight of the MST is printed first, formatted to exactly 10 decimal places.
2.  **Edges**: If the number of points $n \le 10$, the edges constituting the MST are listed, one per line, formatted as `(x1, y1)(x2, y2)`.
3.  **Failure Condition**: If a valid MST cannot be formed satisfying the $\alpha$ constraint, the program outputs `FAIL`.

**Sample Output:**
```text
3.0000000000
(0, 0)(0, 1)
(0, 0)(1, 0)
(0, 1)(1, 1)
```

## Integration Information
This component is designed as a standalone Command Line Interface (CLI) utility, facilitating integration into broader data processing pipelines via system calls.

### Integration Strategy
To integrate this module:
1.  **Data Serialization**: Serialize your dataset points into a temporary CSV-formatted file.
2.  **Process Invocation**: Spawn a subprocess to execute `java EMST`.
3.  **Stream Capture**: Capture the `stdout` stream.
    -   Parse the first non-empty line as a `Double` to retrieve the objective function value (Total Weight).
    -   If the output is `FAIL`, handle the exception according to domain logic.
    -   For small datasets ($n \le 10$), subsequent lines can be parsed to reconstruct the graph topology.

### Error Handling
The program handles malformed input lines by skipping them but terminates if valid points cannot be read or if arguments are missing. Ensure input validation occurs upstream before invoking this utility.
