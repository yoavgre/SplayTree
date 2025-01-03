# SplayTree Implementation in Java

## Overview
This project implements a **Splay Tree** in Java. A Splay Tree is a self-adjusting binary search tree that performs operations such as insertion, deletion, and lookup with **amortized O(log n)** time complexity. This implementation is specifically designed to handle sequences of characters efficiently.

---

## Features
- **Node-Based Structure**: Each node stores a character, its size, and pointers to parent and children nodes.
- **Key Operations**:
  - Build a Splay Tree from a string.
  - Select nodes by rank.
  - Split and join trees.
  - Insert, delete, substitute, and translocate characters.
  - Reverse subsequences of characters.
- **Tree Traversal**: Supports inorder traversal to return the sequence of characters.
- **Optimizations**:
  - Maintains a `size` property for efficient rank-based operations.
  - Uses a `swap` field for subtree reversal without reconstructing the tree.

---

## Usage

### Prerequisites
Ensure you have:
- Java Development Kit (JDK) installed.
- A text editor or IDE (e.g., IntelliJ IDEA, Eclipse).

### How to Run
1. Clone this repository:
   ```bash
   git clone https://github.com/your_username/splay-tree.git
   ```
2. Navigate to the project directory:
   ```bash
   cd splay-tree
   ```
3. Compile the Java file:
   ```bash
   javac SplayTree.java
   ```
4. Run the program:
   ```bash
   java SplayTree
   ```

### Example Usage
```java
public static void main(String[] args) {
    SplayTree test1 = new SplayTree("ACCCGA");
    test1.invert(0, 5);
    System.out.println(test1); // Outputs the reversed sequence.
}
```

---

## Methods and Descriptions

### Constructors
- `SplayTree()` - Initializes an empty Splay Tree.
- `SplayTree(String s)` - Constructs a Splay Tree from the given string.
- `SplayTree(SplayTree T1, SplayTree T2)` - Joins two Splay Trees into one.

### Core Operations
- `insert(int i, char c)` - Inserts a character at a specified position.
- `delete(int i)` - Removes a character at the specified position.
- `substitute(int i, char c)` - Replaces the character at the specified position.
- `invert(int i, int j)` - Reverses the sequence of characters between two positions.
- `translocate(int i, int j, int k)` - Moves a subsequence to a new position.

### Tree Utilities
- `select(int k)` - Selects and splays the node at the k-th rank.
- `split(Node x)` - Splits the tree into two trees around a node.
- `toString()` - Returns the sequence of characters represented by the tree in inorder traversal.
