package interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Java has no tree import, so I just stole this code from here
 * https://www.cs.dartmouth.edu/~cs10/notes/8/code/BinaryTree.java
 * 
 * So that I could have a tree.
 * 
 * Generic binary tree, storing data of generic type in each node
 *
 * @author Tim Pierson, Winter 2025, based on binary tree from prior terms
 */

public class BinaryTree<E> {
	private BinaryTree<E> left, right;	// children; can be null
	E data;

	/**
	 * Constructor leaf node -- left and right are null
	 */
	public BinaryTree(E data) {
		this.data = data; this.left = null; this.right = null;
	}

	/**
	 * Constructor inner node
	 */
	public BinaryTree(E data, BinaryTree<E> left, BinaryTree<E> right) {
		this.data = data; this.left = left; this.right = right;
	}

	/**
	 * Getters
	 */
	public BinaryTree<E> getLeft() { return left; }
	public BinaryTree<E> getRight() { return right; }
	public E getData() { return data; }

	/**
	 * Setters
	 */
	public void setData(E data) {this.data = data;}
	public void setLeft(BinaryTree<E> child) { left = child; }
	public void setRight(BinaryTree<E> child) { right = child; }

	/**
	 * helper methods
	 */
	public boolean isInner() { return left != null || right != null; }
	public boolean isLeaf() { return left == null && right == null; }
	public boolean isFull() { return left != null && right !=null;}
	public boolean hasLeft() { return left != null; }
	public boolean hasRight() { return right != null; }

	/**
	 * Number of nodes (inner and leaf) in tree
	 */
	public int size() {
		int num = 1;
		if (hasLeft()) num += left.size();
		if (hasRight()) num += right.size();
		return num;
	}

	/**
	 * Longest length to a leaf node from here
	 */
	public int height() {
		if (isLeaf()) return 0;
		int h = 0;
		if (hasLeft()) h = Math.max(h, left.height());
		if (hasRight()) h = Math.max(h, right.height());
		return h+1;		// inner: one higher than highest child
	}

	/**
	 * Same structure and data
	 * @param t2 compare with this tree
	 * @return true if this tree and t2 have the same structure and same data in each node, false otherwise
	 */
	public boolean equals(BinaryTree<E> t2) {
		if (hasLeft() != t2.hasLeft() || hasRight() != t2.hasRight()) return false;
		if (!data.equals(t2.data)) return false;
		if (hasLeft() && !left.equals(t2.left)) return false;
		if (hasRight() && !right.equals(t2.right)) return false;
		return true;
	}

	/**
	 * Check to see if value is in the tree
	 * @param value value to seek
	 * @return true if value in tree, false otherwise
	 */
	public boolean contains(E value) {
		//see if this node's data is equal to value
		if (data.equals(value)) {
			return true;
		}
		else {
			//see if left or right child has value
			boolean leftResult = false;
			boolean rightResult = false;
			if (hasLeft()) leftResult = left.contains(value);
			if (hasRight()) rightResult = right.contains(value);
			return leftResult || rightResult; //true if left or right is true
		}
	}

	/**
	 * Leaves, in order from left to right
	 */
	public List<E> fringe() {
		List<E> f = new ArrayList<E>();
		addToFringe(f);
		return f;
	}

	/**
	 * Helper for fringe, adding fringe data to the list
	 */
	private void addToFringe(List<E> fringe) {
		if (isLeaf()) {
			fringe.add(data);
		}
		else {
			if (hasLeft()) left.addToFringe(fringe);
			if (hasRight()) right.addToFringe(fringe);
		}
	}

	/**
	 * Returns a string representation of the tree by calling a helper method for recursion
	 * Two versions: (1) simple -- prints indented, (2) pretty printed -- prints parent/child lines
	 * uncomment one version
	 */
	public String toString() {
		//return toStringHelper(""); //simple version prints indented
		return toStringHelper("", ""); //pretty printed version includes lines to show parent/child
	}

	/**
	 * Simple version: Recursively constructs a String representation of the tree from this node,
	 * starting with the given indentation and indenting further going down the tree
	 */
	public String toStringHelper(String indent) {
		String res = indent + data + "\n";
		if (hasLeft()) res += left.toStringHelper(indent + "  ");
		if (hasRight()) res += right.toStringHelper(indent + "  ");
		return res;
	}

	/**
	 * Pretty printed version: Recursively constructs a String representation of the tree from this node,
	 * starting with the given indentation and indenting further going down the tree
	 * formatting inspired by: https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram-in-java
	 */
	public String toStringHelper(String indent, String childIndent) {
		String res = indent + data + "\n";
		if (hasLeft()) {
			//not null left child
			res += left.toStringHelper(childIndent + "|-- ", childIndent + "|   ");
		}
		else if (!isLeaf()){
			//null left child
			res += childIndent + "|-- null\n";
		}
		if (hasRight()) {
			//not null right child
			res += right.toStringHelper(childIndent + "|-- ", childIndent + "    ");
		}
		else if (!isLeaf()){
			//null right child
			res += childIndent + "|-- null\n";
		}
		return res;
	}

	/**
	 * Tree traversals
	 */
	public void preOrderTraversal() {
		System.out.print(data +" ");
		if (hasLeft()) left.preOrderTraversal();
		if (hasRight()) right.preOrderTraversal();
	}

	public void inOrderTraversal() {
		if (hasLeft()) left.inOrderTraversal();
		System.out.print(data + " ");
		if (hasRight()) right.inOrderTraversal();
	}

	public void postOrderTraversal() {
		if (hasLeft()) left.postOrderTraversal();
		if (hasRight()) right.postOrderTraversal();
		System.out.print(data + " ");
	}

}