import java.awt.event.ItemEvent;
import java.io.*;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class implements a hashtable that using chaining for collision handling.
 * Any non-<tt>null</tt> item may be added to a hashtable.  Chains are 
 * implemented using <tt>LinkedList</tt>s.  When a hashtable is created, its 
 * initial size, maximum load factor, and (optionally) maximum chain length are 
 * specified.  The hashtable can hold arbitrarily many items and resizes itself 
 * whenever it reaches its maximum load factor or whenever it reaches its 
 * maximum chain length (if a maximum chain length has been specified).
 * 
 * Note that the hashtable allows duplicate entries.
 */
public class HashTable<T> {
	private double MAX_LOAD_FACTOR = 0;
	private int INIT_SIZE = 0;
	private int MAX_CHAIN_LENGTH = 0;
	private int MAX_CHAIN_LENGTH_RESIZE = 5;
	
	private double currentLoadFactor = 0;
	private int keyCount = 0;
	private int maxChainResizeCount = 0;
	
	private LinkedList<T>[] htArray;
    
    /**
     * Constructs an empty hashtable with the given initial size, maximum load
     * factor, and no maximum chain length.  The load factor should be a real 
     * number greater than 0.0 (not a percentage).  For example, to create a 
     * hash table with an initial size of 10 and a load factor of 0.85, one 
     * would use:
     * 
     * <dir><tt>HashTable ht = new HashTable(10, 0.85);</tt></dir>
     *
     * @param initSize the initial size of the hashtable.
     * @param loadFactor the load factor expressed as a real number.
     * @throws IllegalArgumentException if <tt>initSize</tt> is less than or 
     *         equal to 0 or if <tt>loadFactor</tt> is less than or equal to 0.0
     **/
    public HashTable(int initSize, double loadFactor) {
    	CreateTable(initSize, loadFactor, 0); // no max chain length if not specified
    }
    
    
    /**
     * Constructs an empty hashtable with the given initial size, maximum load
     * factor, and maximum chain length.  The load factor should be a real 
     * number greater than 0.0 (and not a percentage).  For example, to create 
     * a hash table with an initial size of 10, a load factor of 0.85, and a 
     * maximum chain length of 20, one would use:
     * 
     * <dir><tt>HashTable ht = new HashTable(10, 0.85, 20);</tt></dir>
     *
     * @param initSize the initial size of the hashtable.
     * @param loadFactor the load factor expressed as a real number.
     * @param maxChainLength the maximum chain length.
     * @throws IllegalArgumentException if <tt>initSize</tt> is less than or 
     *         equal to 0 or if <tt>loadFactor</tt> is less than or equal to 0.0 
     *         or if <tt>maxChainLength</tt> is less than or equal to 0.
     **/
    public HashTable(int initSize, double loadFactor, int maxChainLength) {
    	CreateTable(initSize, loadFactor, maxChainLength);
    }
    
    /**
     * TODO: Fill this out.
     * @param initsize
     * @param loadFactor
     * @param maxChainLength
     */
    @SuppressWarnings("unchecked")
	private void CreateTable(int initsize, double loadFactor, int maxChainLength) {
    	if (initsize < 0 || loadFactor < 0.0 || maxChainLength < 0) {
    		throw new IllegalArgumentException();
    	}
    	this.INIT_SIZE = initsize;
    	this.MAX_LOAD_FACTOR = loadFactor;
    	this.MAX_CHAIN_LENGTH = maxChainLength;
    	this.htArray = (LinkedList<T>[]) new LinkedList[this.INIT_SIZE];
    }
    /**
     * TODO: fill out
     * @param item
     * @return
     */
    private int hash(T item) {
    	int tabLen = this.htArray.length;
    	int itemHash = item.hashCode() % tabLen;
    	if (itemHash < 0) {
    		itemHash += tabLen;
    	}
    	return itemHash;
    }
    
    /**
     * Determines if the given item is in the hashtable and returns it if 
     * present.  If more than one copy of the item is in the hashtable, the 
     * first copy encountered is returned.
     *
     * @param item the item to search for in the hashtable.
     * @return the item if it is found and <tt>null</tt> if not found.
     **/
    public T lookup(T item) {
    	if (keyCount == 0) {return null;}
    	int itemHash = hash(item);
    	LinkedList<?> valueList = this.htArray[itemHash];
    	if (valueList == null) {return null;}
    	if (valueList.contains(item)) {
    		return item;
    	}
    	return null;
    }
    
    
    /**
     * Inserts the given item into the hashtable.  The item cannot be 
     * <tt>null</tt>.  If there is a collision, the item is added to the end of
     * the chain.
     * <p>
     * If the load factor of the hashtable after the insert would exceed 
     * (not equal) the maximum load factor (given in the constructor), then the 
     * hashtable is resized.  
     * 
     * If the maximum chain length of the hashtable after insert would exceed
     * (not equal) the maximum chain length (given in the constructor), then the
     * hashtable is resized.
     * 
     * 	https://piazza.com/class/iri4w46g18v1nd?cid=191
     * 
     * When resizing, to make sure the size of the table is reasonable, the new 
     * size is always 2 x <i>old size</i> + 1.  For example, size 101 would 
     * become 203.  (This guarantees that it will be an odd size.)
     * </p>
     * <p>Note that duplicates <b>are</b> allowed.</p>
     *
     * @param item the item to add to the hashtable.
     * @throws NullPointerException if <tt>item</tt> is <tt>null</tt>.
     **/
    public void insert(T item) {
    	// check for bad value
        if (item == null) {throw new NullPointerException();}
        
        // get hash of item to insert
        int itemHash = hash(item);
        LinkedList<T> llTemp = this.htArray[itemHash];
        
        // try to insert into ht
        // case where no values for this key are yet in the ht
        if (llTemp == null) {
        	llTemp = new LinkedList<>();
        	llTemp.add(item);
        	this.keyCount += 1;
        	UpdateLoadFactor();
        	// check if we need to resize due to load factor
        	if (this.currentLoadFactor > MAX_LOAD_FACTOR) {
        		resize();
        		// rehash new item for new table size
        		itemHash = hash(item);
        	}
        	// add linked list to ht
        	this.htArray[itemHash] = llTemp;
        }
        else {
        	// update existing chain in ht
            if (llTemp.size() + 1 > this.MAX_CHAIN_LENGTH && maxChainResizeCount < MAX_CHAIN_LENGTH_RESIZE) {
            	// if we have max chain length conflict, resize, but only a limited number of times
        		maxChainResizeCount += 1;
        		resize();
            }
            llTemp.add(item);
        }
    }
    
    private void resize() {
    	int oldSize = this.htArray.length;
    	int newSize = oldSize * 2 + 1;
    	LinkedList<T>[] newArray = (LinkedList<T>[]) new LinkedList[newSize];
    	LinkedList<T> llTemp = null;
    	int newHash = 0;
    	for (int i = 0; i < oldSize; i++) {
    		llTemp = this.htArray[i];
    		if (llTemp != null) {
    			if ( llTemp.size() != 0) {
    	    		T firstval = llTemp.getFirst();
    	    		if (firstval != null) {
    	        		newHash = hash(firstval);
    	        		newArray[newHash] = llTemp;	
    	    		}
    			}
    		}
    	}
    	this.htArray = newArray;
    }
    
    /**
     * Removes and returns the given item from the hashtable.  If the item is 
     * not in the hashtable, <tt>null</tt> is returned.  If more than one copy 
     * of the item is in the hashtable, only the first copy encountered is 
     * removed and returned.
     *
     * @param item the item to delete in the hashtable.
     * @return the removed item if it was found and <tt>null</tt> if not found.
     **/
    public T delete(T item) {
    	T retVal = null;
    	if (keyCount == 0) {return null;}
    	int itemHash = hash(item);
    	LinkedList<?> valueList = this.htArray[itemHash];
    	if (valueList == null) {return null;}
    	if (valueList.contains(item)) {
    		valueList.remove(item);	// removes first occurrence
    		retVal = item;
    	}
    	this.keyCount -= 1;
    	UpdateLoadFactor();
    	return retVal;
    }
    
    private void UpdateLoadFactor() {
    	this.currentLoadFactor = this.keyCount / (double) this.htArray.length;
    }
    
    
    /**
     * Prints all the items in the hashtable to the <tt>PrintStream</tt> 
     * supplied.  The items are printed in the order determined by the index of
     * the hashtable where they are stored (starting at 0 and going to 
     * (table size - 1)).  The values at each index are printed according 
     * to the order in the <tt>LinkedList</tt> starting from the beginning. 
     *
     * @param out the place to print all the output.
     **/
    public void dump(PrintStream out) {
    	out.println("Hashtable contents:");
    	for (int i = 0; i < this.htArray.length; i++) {
    		LinkedList<T> llTemp = this.htArray[i];
    		if (llTemp != null) {
        		String line = "";
        		if (llTemp.size() > 0) {
        			line = Integer.toString(i) + ": [";
        			Iterator<T> llIter = llTemp.iterator();
        			while (llIter.hasNext()) {
        				line += llIter.next() + ", ";	// TODO: fix trailing comma
        			}
        			line += "]";
        			out.println(line);
        		}
    		}
    	}
    }
    
  
    /**
     * Prints statistics about the hashtable to the <tt>PrintStream</tt> 
     * supplied.  The statistics displayed are: 
     * <ul>
     * <li>the current table size
     * <li>the number of items currently in the table 
     * <li>the current load factor
     * <li>the length of the largest chain
     * <li>the number of chains of length 0
     * <li>the average length of the chains of length > 0
     * </ul>
     *
     * @param out the place to print all the output.
     **/
    public void displayStats(PrintStream out) {
        out.println("Parameters used: ");
        // TODO: output required params
        out.println("Hashtable statistics: ");
        // TODO: output required stats
    }
}