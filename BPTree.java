import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu), Shannon Morison (smorison@epic.com)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;
    
    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;
    
    
    /**
     * Public constructor
     * 
     * @param branchingFactor the number of children each node can have
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
               "Illegal branching factor: " + branchingFactor);
        }
        this.branchingFactor = branchingFactor;
        root = null;
    }
    
    
    /*
     * Inserts the key and value in the appropriate nodes in the tree
     * 
     * Note: key-value pairs with duplicate keys can be inserted into the tree.
     * 
     * @param key the key to insert (for the food list, the amount of a given nutrient)
     * @param value the value to insert at this key (for the food list, the food ID)
     */
    @Override
    public void insert(K key, V value) {
    	
        if (root == null) { //If we haven't added anything yet, make the first node
        	root = new LeafNode();
        	root.insert(key, value);
        }
        else if (root instanceof BPTree.LeafNode) { //Deal with the special case where the root is a leaf
    		root.insert(key, value);
    		if (root.isOverflow()) { //If adding the value makes the root too big, split it
    			root = root.split();
    		}
    	}
    	else { //The root is an internal node, so just use its insert function
    		root.insert(key, value);
    		if (root.isOverflow()) {
    			root = root.split();
    		}
    	}
    }
    
    
    /*
     * (non-Javadoc)
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.contentEquals(">=") && 
            !comparator.contentEquals("==") && 
            !comparator.contentEquals("<=") )
            return new ArrayList<V>();
        else {
        	List<V> returnValues = new LinkedList<V>(); 
        	returnValues = root.rangeSearch(key, comparator); 
        	return returnValues;
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }
    
    
    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan, smorison
     */
    private abstract class Node {
        
        // List of keys
        List<K> keys;
        
        /**
         * Package constructor
         */
        Node() {
           this.keys = new LinkedList<K>();
        }
        
        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();
        
        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();
        
        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * Determines if the current node has too many keys
         * 
         * @return boolean- true if the node has too many keys, false otherwise
         */
        abstract boolean isOverflow();
        
        public String toString() {
            return keys.toString();
        }
    
    } // End of abstract class Node
    
    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan, smorison
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;
       
        
        /**
         * Package constructor
         */
        InternalNode() {
            super();
            this.children = new LinkedList<Node>(); //Initialize the list that holds the children
        }
        
        /**
         * Package constructor that adds a key to the keys list
         */
        InternalNode(K key) {
            super();
            this.children = new LinkedList<Node>(); //Initialize the list that holds the children
            keys.add(key); //Add a key to the list (makes it easier to do split functions)
        }
        
        /**
         * Gets the first  key of the node
         * 
         * @return key- the lowest value in the node
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }
        
        /**
         * Determines if this node has too many keys
         * 
         * @return true if the node has too many keys, false otherwise
         */
        boolean isOverflow() {
        	int numKeys = keys.size();
        	if (numKeys > (branchingFactor - 1)) {
        		return true;
        	}
            return false;
        }
        
        /**
         * Inserts key and value in the current node 
         * Splitting (if needed) is handled by the insert function in the BPTree parent
         *  
         * @param key- the key to insert into the node (will determine the ordering)
         * @param value- the value to associate with the given key
         */
        void insert(K key, V value) {
        	Stack<Node> nodesTraversed = new Stack<Node>(); //Create a stack to store the nodes we're traversing 
        	Node currNode = this; //Start traversing the tree at the current node
        	nodesTraversed.push(currNode); //Add the current node to the stack so we deal with splitting it later as needed
    		while (!(currNode instanceof BPTree.LeafNode)) { //Keep traversing the tree until we reach a leaf
    			int insertLoc = 0;
    			for (K currKey : currNode.keys) {
    				if (key.compareTo(currKey) > 0) { //Traverse the keys to figure out which child to examine
    					insertLoc++;
    				}
    			}
    			InternalNode currInternal = (InternalNode)currNode; //Cast this to an internal node (we know it's not a leaf node since we're in the while loop)
    			currNode = currInternal.children.get(insertLoc); //Get the correct child for this insertion
    			nodesTraversed.add(currNode);
    		}
    		
    		currNode.insert(key, value); //Insert the key/value pair into the leaf
    		
    		int numNodesTraversed = nodesTraversed.size();
    		Node nodeToCheck;
    		InternalNode newParent;
    		InternalNode oldParent;
    		int childIndex;
    		for (int i = 0; i < numNodesTraversed; i++) { //Go through all the nodes we've traversed, starting with the leaf, to see if they need to be split
    			nodeToCheck = nodesTraversed.pop();
    			if (nodeToCheck.isOverflow()) {
    				newParent = (InternalNode)nodeToCheck.split(); //Since this is a parent node, it's not a leaf, so we can safely cast it to an InternalNode
    				if (!nodesTraversed.isEmpty()) { //If we aren't at the root, we need to merge our new parent with the current node
    					oldParent = (InternalNode)nodesTraversed.peek(); //Get the original node this one was linked to--we can safely cast to an InternalNode since we know it's not a leaf
    					childIndex = oldParent.children.indexOf(nodeToCheck);
    					oldParent.children.remove(nodeToCheck); //Since we split the node, we want to remove the old node link and readd the new link to the split node with the merge function
    					oldParent.merge(newParent, childIndex);
    				}
    				else {
    					nodeToCheck = newParent;
    				}
    			}
    		}
        }
        
        /**
         * Gets the new parent created after splitting the node
         * 
         * @return Node- the new parent created after splitting the node
         */
        Node split() {
            InternalNode left = new InternalNode(); //Get two new nodes for the split of the values
            InternalNode right = new InternalNode();
            
            int medianIndex = (int)(keys.size()/2); //Find the median index--should be the middle of our key list since it's sorted
            K median = keys.get(medianIndex);
            InternalNode newParent = new InternalNode(median); //Make a new internal node and add the promoted key
            
            newParent.children.add(left); //Link the new parent to the two new leaf nodes
            newParent.children.add(right);
            
            for (int i = 0; i < medianIndex; i++) { //Add all the values in the keys list that are less than the median to the new left child
            	left.keys.add(keys.get(i));
            }
            
            for (int j = (medianIndex + 1); j < keys.size(); j++) { //Add all the values in the keys list greater than the median to the new right child
            	right.keys.add(keys.get(j));
            }
            
            int numChildren = (int) (children.size()/2); //Figure out how many children should go to each node
            
            for (int k = 0; k < numChildren; k++) {
            	left.children.add(children.get(k)); //Since the children should be in order, we can just get the first half and add them to the new left node
            }
            
            for (int l = numChildren; l < children.size(); l++) { //Attach the second half of children to the right side
            	right.children.add(children.get(l));
            }
            
           return newParent;
        }
        
        /**
         * Takes another internal node resulting from splitting and adds its key and children to the current node
         * Assumes the mergeNode only has one key (this function is only used for merging nodes returned by splitting so this will always be the case)
         * 
         * @param mergeNode- the node to merge with the current one (assumes this resulted from splitting a node)
         * @param childIndex- the index that the split node 
         */
        private void merge(InternalNode mergeNode, int childIndex) {
        	this.keys.add(childIndex, mergeNode.keys.get(0)); //We should add this new key in the same position as the original child node was (so if it was the second child, the promoted node should be key 2)
        	for (Node child: mergeNode.children) {//Iterate over all the children in the merge node and add them to this node, starting at the index of the child we're replacing
        		this.children.add(childIndex, child);
        		childIndex++;
        	}
        }
        
        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
        	List<V> returnValues = new LinkedList<V>();
        	Node currNode = this;
        	while (!(currNode instanceof BPTree.LeafNode)) { //Keep traversing the tree until we reach a leaf where our key should fit which will be our starting leaf
    			int searchLoc = 0;
    			for (K currKey : currNode.keys) {
    				if (key.compareTo(currKey) > 0) { //Traverse the keys to figure out which child has the value we're looking for
    					searchLoc++;
    				}
    			}
    			InternalNode currInternal = (InternalNode)currNode; //Cast this to an internal node (we know it's not a leaf node since we're in the while loop)
    			currNode = currInternal.children.get(searchLoc); //Get the correct child for this insertion
    		}
            
        	LeafNode startingNode = (LeafNode)currNode; //We should be able to safely cast this since we looped until we reached a leaf
        	LeafNode checkedNode = new LeafNode();
        	if (comparator.contentEquals("==")) {
        		K lowKey = startingNode.getFirstLeafKey();
        		while (lowKey.compareTo(key) == 0) { //Keep looking at previous nodes until we find one that doesn't start with a key that equals our key--that's where we start
        			startingNode = startingNode.previous;
        			if (startingNode != null) {
        				lowKey = startingNode.getFirstLeafKey();
        			}
        		} 
        		returnValues.addAll(startingNode.rangeSearch(key, comparator));
        		checkedNode = startingNode.next;
        		while ((checkedNode != null) && (key.compareTo(checkedNode.getFirstLeafKey()) == 0)) { //Keep looping over all the nodes until the keys get too high or there are no more
        			returnValues.addAll(checkedNode.rangeSearch(key, comparator));
        			checkedNode = checkedNode.next;
        		}
        	}
        	
        	else if (comparator.contentEquals("<=")) {
        		int numStartingKeys = startingNode.keys.size();
        		K highKey = startingNode.keys.get(numStartingKeys - 1); //Find the last (highest) key in the keys array
        		while ((highKey.compareTo(key) <= 0) && (startingNode != null)) { //Keep looking at the next node until we find a node where the last key is higher than the value we're checking for--that's where we start
        			startingNode = startingNode.next;
        			if (startingNode != null) {
        				numStartingKeys = startingNode.keys.size();
        				highKey = startingNode.keys.get(numStartingKeys - 1);
        			}
        		}
        		returnValues.addAll(startingNode.rangeSearch(key, comparator)); //Add all the relevant values in our starting node
        		checkedNode = startingNode.previous; 
        		while (checkedNode != null) { //Add all the values in all previous nodes to our return values list as well
        			returnValues.addAll(checkedNode.values);
        			checkedNode = checkedNode.previous;
        		}
        	}
        	
        	else if (comparator.contentEquals(">=")) {
        		K lowKey = startingNode.getFirstLeafKey(); //Find the first (lowest) key in the array
        		while ((lowKey.compareTo(key) >= 0) && (startingNode != null)) { //Keep looking at the previous node until we find a node where the first key is lower than the value we're searching for-that's where we start
        			startingNode = startingNode.previous;
        			if (startingNode != null) {
            			lowKey = startingNode.getFirstLeafKey();
        			}
        		}
        		returnValues.addAll(startingNode.rangeSearch(key, comparator)); //Add all the relevant values in our starting node
        		checkedNode = startingNode.next; 
        		while (checkedNode != null) { //Add all the values in all subsequent nodes to our return values list as well
        			returnValues.addAll(checkedNode.values);
        			checkedNode = checkedNode.next;
        		}
        	}
        	
            return returnValues;
        }
    
    } // End of class InternalNode
    
    
    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan, smorison
     */
    private class LeafNode extends Node {
        
        // List of values
        List<V> values;
        
        // Reference to the next leaf node
        LeafNode next;
        
        // Reference to the previous leaf node
        LeafNode previous;
                
        /**
         * Package constructor
         * 
         * @param parent- the parent node for this leaf (will always be an internal node)
         */
        LeafNode() {
            super();
            values = new LinkedList<V>(); //Initialize the list of values in the leaf node
        }
        
        
        /**
         * Gets the first key of the leaf
         * 
         * @return key- the lowest value in the leaf
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }
        
        /**
         * Determines if this node has too many keys
         * 
         * @return true if the node has too many keys, false otherwise
         */
        boolean isOverflow() {
        	int numKeys = keys.size();
        	if (numKeys > (branchingFactor - 1)) {
        		return true;
        	}
            return false;
        }
        
        /**
         * Inserts key and value in the appropriate node 
         * The splitting to balance the node if required is handled by the insert function in the 
         * main B Tree class since it knows what the parent is 
         *  
         * @param key- the key to insert into the node (will determine the ordering)
         * @param value- the value to associate with the given key
         */
        void insert(K key, V value) {
        	int index = 0;
        	for (K currKey : keys) { //Loop over all the keys to determine where to insert the new key (basically, find the point where the keys are bigger than the new one)
        		if (currKey.compareTo(key) < 0) {
        			index++;
        		}
        	}
        	keys.add(index, key); //Add the key at the specified place--this ensures our key linked list is sorted
            values.add(index, value);
        }
        
        /**
         * Gets the new parent created after splitting the node
         * 
         * @return Node- the new parent created after splitting the node
         */
        Node split() {
            LeafNode currPrev = previous; 
            LeafNode currNext = next;
            LeafNode left = new LeafNode(); //Get two new nodes for the split of the values
            LeafNode right = new LeafNode();

            int medianIndex = (int)(keys.size()/2); //Find the median index--should be the middle of our key list since it's sorted
            K median = keys.get(medianIndex);
            InternalNode newParent = new InternalNode(median); //Make a new internal node for the promoted key and go ahead and add it
            
            newParent.children.add(left); //Link the new parent to the two new leaf nodes
            newParent.children.add(right);
            
            for (int i = 0; i <= medianIndex; i++) { //Add all the values in the keys list that are less than the median to the new left child
            	left.insert(keys.get(i), values.get(i));
            }
            
            for (int j = (medianIndex + 1); j < keys.size(); j++) { //Add the second half of the values to the new right child
            	right.insert(keys.get(j), values.get(j));
            }
            
            left.previous = currPrev; //Reset all the previous and next nodes correctly
            left.next = right;
            right.previous = left;
            right.next = currNext;
            if (currPrev != null) {
            	currPrev.next = left;
            }
            if (currNext != null) {
            	currNext.previous = right;
            }
            
            
            return newParent;
        }
        
        /**
         * Gets the values that satisfy the given range 
         * search arguments.
         * 
         * Value of comparator can be one of these: 
         * "<=", "==", ">="
         * 
         * Example:
         *     If given key = 2.5 and comparator = ">=":
         *         return all the values with the corresponding 
         *      keys >= 2.5
         *      
         * If key is null or not found, return empty list.
         * If comparator is null, empty, or not according
         * to required form, return empty list.
         * 
         * @param key to be searched
         * @param comparator is a string
         * @return list of values that are the result of the 
         * range search; if nothing found, return empty list
         */
        List<V> rangeSearch(K key, String comparator) {
            List<V> matchedValues = new LinkedList<V>();
            K currKey;
            if (comparator.contentEquals("==")) { //Start by looping over all the keys to see which equal the compared key
            	for (int i = 0; i < keys.size(); i++) {
            		currKey = keys.get(i);
            		if (currKey.compareTo(key) == 0) {
            			matchedValues.add(values.get(i));
            		}
            		else if (currKey.compareTo(key) > 0) {
            			break; //Quit looping when we hit a key that's bigger than the one we're looking for
            		}
            	}
            }
            else if (comparator.contentEquals("<=")) { //Now check which keys are less than or equal to the compared key
            	for (int j = 0; j < keys.size(); j++) {
            		currKey = keys.get(j);
            		if (currKey.compareTo(key) <= 0) {
            			matchedValues.add(values.get(j));
            		}
            		else if (currKey.compareTo(key) > 0) {
            			break; //Quit looping when we hit a key that's bigger than the one we're looking for
            		}
            	}
            }
            else if (comparator.contentEquals(">=")) { //Now find the values with keys greater than the compared to key (search the other way through the keys list so we can stop if the keys get too small)
            	for (int l = keys.size() - 1 ; l >= 0; l--) {
            		currKey = keys.get(l);
            		if (currKey.compareTo(key) >= 0) {
            			matchedValues.add(values.get(l));
            		}
            		else if (currKey.compareTo(key) < 0) {
            			break; //Quit looping when we hit a key that's smaller than the one we're looking for
            		}
            	}
            }
            return matchedValues;
        }
        
    } // End of class LeafNode
    
    
    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(5);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            bpTree.insert(j, j);
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        List<Double> filteredValues = bpTree.rangeSearch(0.0d, ">=");
        System.out.println("Filtered values: " + filteredValues.toString());
        List<Double> filteredValuesb = bpTree.rangeSearch(0.5d, "<=");
        System.out.println("Filtered values: " + filteredValuesb.toString());
        List<Double> filteredValuesc = bpTree.rangeSearch(0.8d, "==");
        System.out.println("Filtered values: " + filteredValuesc.toString());
    }

} // End of class BPTree
