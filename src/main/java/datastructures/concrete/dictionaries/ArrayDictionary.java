//this class will keep track of an array thats contains Pair
//objects.
package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import datastructures.concrete.KVPair;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;

    // You're encouraged to add extra fields (and helper methods) though!
    private int size;		// size of array
    private int arrayMax;	// maximum size of the array

    public ArrayDictionary() {
        size = 0;		//set the default size of array as 0 				
        arrayMax = 10;	//set the default maximum size of array as 10
        pairs = makeArrayOfPairs(arrayMax);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    //passes an object K as a parameter. Searches the list and return 
    //the value based on the index of the object K. Throws NoSuchKeyException
    // otherwise.
    @Override
    public V get(K key) {
        int index = this.indexOf(key);
        if(index != -1) {
            return pairs[index].value;
        } else {
            throw new NoSuchKeyException();
        }
    }

    	//pass an object key and the other object V as parameters. Pair K and V
    //and put them to the dictionary. If K has already existed in 
    //dictionary, V will become the new value. Otherwise, a new pair
    //will be created
    @Override
    public void put(K key, V value) {
        if(size < arrayMax) {
        		int index = this.indexOf(key);
        		if(index != -1) {
        			pairs[index] = new Pair<>(key, value); //Change a key that already exists, does not modify size
        		} else {
        			pairs[size] = new Pair<>(key, value); //Add a new unique key to end
        			size++;
        		}
        } else { //Need to resize and copy everything over to the new array
        		arrayMax *= 2;
        		Pair<K, V>[] newArray = makeArrayOfPairs(arrayMax);
        		for(int i = 0; i < this.size; i++) {
        			newArray[i] = pairs[i];
        		}
        		pairs = newArray;
        		this.put(key, value); //Rerun put again now that we have more space
        }
    }

    //passes an object K as a parameter. Then it finds the matched
    //pair in the dictionary based on the value of K. Throws
    //NoSuchKeyException if K is not found in dictionary.
    @Override
    public V remove(K key) {
    		int index = this.indexOf(key);
    		if(index != -1) {
    			V value = pairs[index].value;
    			for(int i = index; i < size - 1; i++) {
    				pairs[i] = pairs[i+1];
    			}
    			pairs[size - 1] = null; //Hard delete last item
    			size--;
    			return value;
    		} else {
    			throw new NoSuchKeyException();
    		}
    }
    
    //Pass an object K as a parameter. Return true if the 
    //dictionary contains object K. False otherwise.
    @Override
    public boolean containsKey(K key) {
        return this.indexOf(key) != -1;
    }

    //return the size of ArrayDictoinary
    @Override
    public int size() {
        return size;
    }
    
    /*
     * Returns the index of a given key
     * Returns -1 if there is no pair with the given key
     */
    private int indexOf(K key) {
    		for(int i = 0; i < size; i++) { 
    			if((pairs[i].key != null && pairs[i].key.equals(key)) || pairs[i].key == key) { //Allow for null keys
    				return i;
    			}
    		}
    		return -1;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    // Returns a list of all K-V dictionary pairs 
    // within this data structure
	@Override
	public Iterator<KVPair<K, V>> iterator() {
		return new DictonaryIterator<KVPair<K, V>>(pairs);
	}

	private class DictonaryIterator<T> implements Iterator<KVPair<K, V>> {
		private int currentIndex = 0;
		private Pair<K, V>[] pairs;
		
		public DictonaryIterator(Pair<K, V>[] pairs) {
			this.pairs = pairs;
		}

		// return true if the dictionary has more K-V pairs. False Otherwise
		public boolean hasNext() {
			return currentIndex < size;
		}

		// Return the next item in the iteration
		// throw NoSuchElementException if next item does not exist.
		public KVPair<K, V> next() {
			if (hasNext()) {
				int oldIndex = currentIndex;
				KVPair<K, V> pair =  new KVPair<K, V>(pairs[oldIndex].key, pairs[oldIndex].value);
				currentIndex++;
				return pair;
			}
			throw new NoSuchElementException();
		}
	}
}
