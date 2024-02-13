/**
 * SplayTree.java
 * Implementation of a keyless Splay tree for representing a seuqence
 * of characterss
 */
public class SplayTree {

    /**
     * Represents a node in the SplayTree.
     */
    public class Node {
        int size;
        Node[] children;
        Node parent;
        char character;

        int swap ;

        /**
         * Constructs a new Node with the given character.
         * @param character the character to store in this Node.
         */
        public Node(char character) {
            this.parent = null;
            this.character = character;
            this.size = 1;
            this.children = new Node[2];
            int swap=0;
        }
    }

    // The root of the SplayTree
    Node root;

    /**
     * Constructs an empty SplayTree.
     */
    public SplayTree() {
        this.root = null;
    }
    
    
    /**
     * Constructs a SplayTree from the given string.
     * @param s the string to use for constructing the tree.
     */
    public SplayTree(String s)
    {

        this. root = (buildSplayTree(s,0,s.length()-1));
    }

    public Node buildSplayTree(String s, int q, int r)
    {
        if(q>r)
            return null;
        int m = (q+r)/2;
        Node curr=new Node (s.charAt(m));
        curr.children[0] = buildSplayTree(s,q,m-1);
        curr.children[1] = buildSplayTree(s,m+1,r);
        if(curr.children[0]!=null)
        {
            curr.children[0].parent=curr;
            curr.size+=curr.children[0].size;
        }
        if(curr.children[1]!=null)
        {
            curr.size+=curr.children[1].size;
            curr.children[1].parent=curr;
        }

        return curr;
    }

    /**
     * Join Constructor.
     * Creates a new splay tree by joining two existing splay trees T1 and T2.
     * The inorder of nodes of the new tree is the concatenation of the inorders of T1 and of T2.
     * This operation invalidates T1 and T2.
     *
     * @param T1 The splay tree containing nodes less than those in T2.
     * @param T2 The splay tree containing nodes greater than those in T1.
     */
    public SplayTree(SplayTree T1, SplayTree T2) {
        // If T1 is null or its root is null, use T2 as the new splay tree
        if (null == T1 || null == T1.root){
            this.root = T2.root;
            return;
        }

        // If T2 is null or its root is null, use T1 as the new splay tree
        if (null == T2 || null == T2.root) {
            this.root = T1.root;
            return;
        }
        
        // Splay the rightmost node of T1 to the root so it has no left child
        Node max = T1.root;
        int curr_swap = max.swap; //saves the swap status down the tree
        while(max.children[1^curr_swap] != null) {
            max = max.children[1^curr_swap];
            curr_swap^=max.swap; //the current swap will be updated down the tree
        }
        T1.splay(max);

        T2.root.swap = T2.root.swap^T1.root.swap;

        // Make T2 the right subtree of the root of T1
        T1.root.children[curr_swap^1] = T2.root; // changed
        T2.root.parent = T1.root;
        updateSize(T1.root);
        this.root = T1.root;

        // Invalidate T1 and T2
        T1.root = T2.root = null;
    }
    
    /**
     * Update the size field of the given Node based on its children
     * @param x the Node to update
     */
    private void updateSize(Node x) {
        x.size = 1 + size(x.children[0]) + size(x.children[1]);
    }

    /**
     * Get the size of the given Node (protected for null arguments)
     * @param x the Node to measure
     * @return the size of the Node
     */
    private int size(Node x) {
        return (x == null) ? 0 : x.size;
    }

    
    /**
     * Perform a rotation operation between a Node x in the Splay Tree
     * and its parent.
     * @param x the Node to rotate
     */
    private void rotate(Node x) {
        Node p = x.parent;
        Node gp = p.parent;

        //idx is index of x in p's children
        int idx = p.children[1] == x ? 1 : 0;

        //set x as child of gp instead of p
        if (gp != null) {
            gp.children[gp.children[1] == p ? 1 : 0] = x;
        }
        x.parent = gp;

        // x's child in the direction opposite to idx becomes the child of p instead of x
        p.children[idx] = x.children[idx ^ 1 ^ x.swap];
        if (p.children[idx] != null) {
            p.children[idx].parent = p;
            p.children[idx].swap =  p.children[idx].swap ^ x.swap;
        }

        //p becomes the child of x in the position opposite to idx
        x.children[idx ^ 1 ^ x.swap] = p;
        p.parent = x;

        //updates the swp filed if x and the parent of x
        x.swap = x.swap^p.swap;
        p.swap = p.swap ^ x.swap;

        //update the sizes of p then x
        updateSize(p);
        updateSize(x);

        //update the root if necessary
        if (root == p) {
            root = x;
        }
    }

    

    /**
     * Performs a splay operation on the given node, bringing it to the root of the tree 
     * using zigzig and zigzag operations.
     * @param x The node to be splayed.
     */
    private void splay(Node x) {
        //Keep rotating until x is the root.
        while (x.parent != null) {

            Node p = x.parent; //parent
            Node gp = p.parent; //grandparent

            // If the node has a grandparent, determine the type of rotation needed.
            if (gp != null) {
                // If the relation between gp and p is the same as the relation between p and x - zigzig
                boolean zigzig = (gp.children[1] == p) == (p.children[1] == x);
                if (zigzig) {
                    rotate(p); // In zigzig, rotate p first.
                } else {
                    rotate(x); // In zigzag, rotate x first.
                }
            }

            //in zigzig, zigzag, and when x is a child of the root, the last rotation is of x
            rotate(x);
        }
    }
    
    /**
     * Splays and returns the node whose rank is k in the tree.
     * Rank is defined by the position of a node in the in-order traversal of the tree.
     *
     * @param k The rank of the node to be selected.
     * @return The node at rank k, or null if such a node does not exist.
     */
    public Node select(int k) {

        Node x = root;
        int isSwaped = x.swap; //saves the current swap status over the pass in the tree
        while (x != null) {
            int t = size(x.children[isSwaped]); // Size of left subtree
            if (t > k) {
                // If left subtree has more than k nodes, go to the left subtree
                x = x.children[isSwaped];
            } else if (t < k) {
                // If left subtree has fewer than k nodes, go to the right subtree
                x = x.children[isSwaped^1];
                k = k - t - 1; //relative rank in the right subtree
            } else {
                // If the size of the subtree is equal to k, x is the desired node
                // Splay x and return it
                splay(x);
                return x;
            }
        }
        // The node with rank k doesn't exist (k is out of bounds).
        return null;
    }

    /**
     * Splits the current splay tree into two splay trees T1 and T2 around a node x,
     * where T1 contains all nodes whose rank is smaller than x, and T2 contains nodes 
     * whose rank is at least the rank of x.
     * This operation invalidates the current tree.
     *
     * @param x The node around which the split operation is performed.
     * @return An array containing the T1 and T2 in this order.
     */
    public SplayTree[] split(Node x) {

        // After splaying x, T1 is the left subtree and T2 is x and its right subtree.
        splay(x);

        SplayTree T1 = new SplayTree();
        T1.root = x.children[x.swap];
        if (T1.root != null) {
            T1.root.swap = T1.root.swap ^ x.swap;
            T1.root.parent = null;
        }

        // Create a new splay tree for the right subtree of x (nodes greater than or equal to x)
        SplayTree T2 = new SplayTree();
        T2.root = x;
        T2.root.children[x.swap] = null;
        updateSize(T2.root);

        //invalidate current tree
        this.root = null;
        
        return new SplayTree[] {T1, T2};
    }


    /**
     * Set the i’th character in the sequence to be character c.
     * 
     * @param i The index of the character to be replaced in the splay tree. Index starts from 1.
     * @param c The new character to replace the old character.
     */
    public void substitute(int i, char c) {
        // Check if the index i is within the valid range.
        if (i < 0 || i >= this.root.size) {
            System.out.println("Invalid index: " + i);
            return;
        }

        // Select the node at index i.
        Node node = select(i);

        // Replace the character in the selected node with c.
        node.character = c; 
    }

    /**
     * Insert the character c at position i in the sequence (shifting all the following
    characters).
     * This method creates a new splay tree t_c containing only the node to be inserted.
     * Then, it joins the current tree with t_c, which places the new character at the end of the tree.
     * If the target position is not at the end, the method translocates the new character to the target position.
     *
     * @param i The position at which the character is to be inserted.
     * @param c The character to be inserted.
     */
    public void insert(int i, char c){

        // Check if the index i is within the valid range.
        if (i < 0 || i > this.root.size) {
            throw new IllegalArgumentException("Invalid index: " + i);
        }

        // Create a new splay tree containing only the character to be inserted.
        SplayTree t_c = new SplayTree(String.valueOf(c));

        // Join the current tree with t_c.
        SplayTree new_t = new SplayTree(this, t_c);

        // Get the max rank of the new tree.
        int max_rank = new_t.root.size-1;

        // Update the root of the current tree.
        this.root = new_t.root;

        // If the target position is not at the end, translocate the new character to the target position.
        if (i < max_rank)
            translocate(max_rank,max_rank, i);
    }


    /**
     * Remove the i’th character from the sequence (shifting all the following characters)
     * @param i The index of the node to be deleted from the splay tree. Index starts from 0.
     */
    public void delete(int i) {
        // Check if the index i is within the valid range.
        if (i < 0 || i >= this.root.size) {
            throw new IllegalArgumentException("Invalid index: " + i);
        }

        // Splay the node x with rank i.
        Node x = select(i);
        
        // Disconnect x from its children
        SplayTree t1 = new SplayTree();
        SplayTree t2 = new SplayTree();
        t1.root = x.children[x.swap];
        if (t1.root != null) {
            t1.root.parent = null;
            t1.root.swap = t1.root.swap^x.swap;
        }   
        t2.root = x.children[x.swap^1];
        if (t2.root != null) {
            t2.root.parent = null;
            t2.root.swap=t2.root.swap^x.swap;
        }


        // Join the two subtrees
        SplayTree t = new SplayTree(t1,t2);
        this.root = t.root;
    }

    /**
     * Move the subsequence starting at i and ending at j to start at position k. 
     * k is relative to the sequence prior to the operation, and is guaranteed not to be in the range [i,j].
     * This method selects three nodes x_i, x_j, and x_k corresponding to the indices i, j, and k, respectively.
     * Then, it splits the tree at x_i and x_j, effectively separating the sequence from i to j.
     * The tree is then restructured by joining the left part (up to i), the right part (after j), and the selected subsequence at the new position k.
     *
     * @param i The start index (rank) of the subsequence to be translocated.
     * @param j The end index (rank) of the subsequence to be translocated.
     * @param k The index (rank) to which the subsequence is to be translocated.
     */

     public void translocate(int i, int j, int k){

        // Deal with invalid arguments
        if (i < 0 || j < i || j >= this.root.size || k < 0 || k > this.root.size) {
            throw new IllegalArgumentException("Invalid index: " + k);
        }
        if (k >= i && k <= j) {
            throw new IllegalArgumentException("Invalid index: " + k);
        }
        
        Node x_i, x_j, x_k;
        x_i = select(i);
        x_j = select(j+1); //select j+1 because we want to split immediately after j (not at j)
        x_k = select(k);

        // Split the tree at x_i.
        // tt[0] contains nodes with ranks smaller than i  [...i)
        // tt[1] contains nodes with ranks at least i  [i...]
        SplayTree[] tt = split(x_i);  //tt[0] is [0...i), tt[1] is [i...]

        SplayTree t,t_ij;
        SplayTree[] tt2;

        if (x_j != null) {
            // j is not the maximum rank
            // Split the [i...] part at x_j.
            // tt2[0] contains nodes with ranks from i to j (including)   [i...j]
            // tt2[1] contains nodes with ranks greater than j  (j...]
            tt2 = tt[1].split(x_j); //tt2[0] is [i,j], tt2[1] is (j...]	

            // Join [0...i) with (j...]
            t = new SplayTree(tt[0],tt2[1]); //t contains all nodes not in [i...j]
            t_ij = tt2[0];  //t_ij contains [i...j]
        }
        else {
            // j is the maximum element, so there is no (j...] part
            t = tt[0]; //t contains [0...i)
            t_ij = tt[1]; //t_ij contains [i...j] = [i...]
        }

        SplayTree[] tt3;

        if (x_k != null) {
            //k is not one location past the length of the sequence

            // Split the joined tree t at node x_k.
            tt3 = t.split(x_k);
            
            // Join the left part of the tree (up to k), the selected subsequence, and the remaining part of the tree.
            t = new SplayTree(tt3[0],t_ij);
            t = new SplayTree(t,tt3[1]);
        }
        else {
            //Otherwise should translocate [i,j] to the very end of the sequence
            t = new SplayTree(t,t_ij);
        }
        
        this.root = t.root;

     }

    /**
     * Reverse the subsequences [i...j] of the splay tree.
     * @param i The starting index of the range to be inverted.
     * @param j The ending index of the range to be inverted.
     */
    public void invert(int i, int j){
        Node idx = select(i);
        Node jdx = select(j+1); //so the last character also will be swapped
        if(i<0||j>this.root.size||i>=j) //out of bounds indexes
            return;
        SplayTree[] s1 = this.split(idx);
        SplayTree[] s2 ;
        SplayTree invertedTree1, invertedTree;
        if(jdx!=null){ //j is the last character needs diffrent handling
            s2=s1[1].split(jdx);
            s2[0].root.swap^=1;
            invertedTree1 = new SplayTree(s1[0],s2[0]);
            invertedTree = new SplayTree(invertedTree1,s2[1]);
        }
        else { //j is the last character
            s1[1].root.swap^=1;
            invertedTree = new SplayTree(s1[0], s1[1]);
        }
        this.root=invertedTree.root;

    }
    
    /**
     * Returns the sequence of characters represented by the data structure. That is, the characters stored
     * at each node in inorder.
     *
     * @return The sequence of characters represented by the tree.
     */
    public String toString() {
        return toString(root, 0);
    }


    /**
     * Helper method for toString(). 
     * Recursively collects the characters of the nodes of the tree rooted at x using inorder traversal.
     *
     * @param x The root of the subtree to be visited.
     * @return The sequence of characters represented by the subtree rooted at x.
     */    
    public String toString(Node x) {
        return toString(x,0);
    }

    //overload of to string in order to pring according to the swap field
    private String toString(Node x, int s) {
        if (x == null) {
            return "";
        }
        int curr_swap = x.swap^s;
        return toString(x.children[curr_swap], curr_swap) + x.character + toString(x.children[curr_swap^1], curr_swap);
    }



    /*
     * The main method used for testing.
     */
    public static void main(String[] args) {
        SplayTree test1 = new SplayTree("ACCCGA");
        test1.invert (0,5);
        System.out.println(test1);
    }


}
