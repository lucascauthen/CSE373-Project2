package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;

// Represents a data structure that contains a bunch of key-value
// mappings. Each key must be unique.
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
	private IDictionary<K, V>[] chains;
	private int size;
	private static final int MIN_SIZE = 16;

	public ChainedHashDictionary() {
		size = 0;
		chains = makeArrayOfChains(MIN_SIZE);
	}

	// This method will return a new, empty array of the given size that can contain
	// IDictionary<K, V> objects
	// Note that each element in the array will initially be null.
	@SuppressWarnings("unchecked")
	private IDictionary<K, V>[] makeArrayOfChains(int size) {
		return (IDictionary<K, V>[]) new IDictionary[size];
	}

	// Pass a K object as a parameter
	// Returns the value corresponding to the given key.
	// throw NoSuchKeyException if the given key is not in the data structure
	@Override
	public V get(K key) {
		int i = getHashCodeForKey(key);
		if (this.containsKey(key)) {
			return chains[i].get(key);
		}
		throw new NoSuchKeyException();
	}

	// Pass a K and a V as parameters and put K-V pairs into the data structure
	// If the K already exists in data structure, replace its value with V
	// if the size of data structure is too small to fit new data, the size
	// will be expanded.
	@Override
	public void put(K key, V value) {
		int i = getHashCodeForKey(key);
		if (chains[i] == null) {
			chains[i] = new ArrayDictionary<K, V>();
		}
		if (!chains[i].containsKey(key)) {
			size++;
		}
		chains[i].put(key, value);
		resizeIfNeeded();
	}

	// Pass a K object as parameter
	// Remove the K-V pair corresponding to the given key from the data structure.
	// Throw NoSuchKeyException if the given K is not in data structure
	@Override
	public V remove(K key) {
		int i = getHashCodeForKey(key);
		if (this.containsKey(key)) {
			V item = chains[i].remove(key);
			size--;
			return item;
		}
		throw new NoSuchKeyException();
	}

	// Pass an object K as parameter. Return True if data structure contains the K.
	// False otherwise.
	@Override
	public boolean containsKey(K key) {
		int i = getHashCodeForKey(key);
		if (chains[i] != null && chains[i].containsKey(key)) {
			return true;
		}
		return false;
	}

	// return the size of data structure
	@Override
	public int size() {
		return size;
	}

	// Returns a list of all key-value pairs within this data structure
	@Override
	public Iterator<KVPair<K, V>> iterator() {
		// Note: you do not need to change this method
		return new ChainedIterator<>(this.chains);
	}

	// Pass an K object as a parameter. Return a hash code of the K.
	private int getHashCodeForKey(K key) {
		return getHashCodeForKey(key, chains.length);
	}

	// Pass an K and an integer as parameters.
	// Return a specific hash code based on K and the integer. Return 0 if K is null
	private int getHashCodeForKey(K key, int mod) {
		if (key != null) {
			return Math.abs(key.hashCode() % mod);
		}
		return 0;
	}

	// check the size of HashDictionary. If there are too much data inside the
	// HashDictionary, create a larger dictionary and move data in old dictionary
	// to new dictionary in order to preserve lookup efficiency
	private void resizeIfNeeded() {
		if (size > 10 * chains.length) { //
			// Need to make the dictionary larger (if we can)
			IDictionary<K, V>[] newChains = makeArrayOfChains(chains.length * 2);
			for (KVPair<K, V> item : this) {
				int index = getHashCodeForKey(item.getKey(), newChains.length);
				if (newChains[index] == null) {
					newChains[index] = new ArrayDictionary<K, V>();
				}
				newChains[index].put(item.getKey(), item.getValue());
			}
			chains = newChains;
		}
		// Don't need to do anything if we didn't resize
	}

	//Class used to iterate through the items in a ChainedHashDictionary object
	private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
		private IDictionary<K, V>[] chains; //List of chains in the ChainedHashDictionary
		private int curChain; //Tracks the current chain in the chain array
		private Iterator<KVPair<K, V>> curChainIterator; //Stores the iterator to the current chain

		//Setup the ChainedIterator
		public ChainedIterator(IDictionary<K, V>[] chains) {
			this.chains = chains;
			this.curChain = getNextChainIndex(0);
			if (this.curChain != -1) {
				curChainIterator = chains[curChain].iterator();
			}
		}

		// Returns true if there is another item in the data structure that can
		// be returned by the next() function
		// Returns false otherwise
		@Override
		public boolean hasNext() {
			if (curChainIterator != null && curChainIterator.hasNext()) {
				//If the curChainIterator is setup and has a value ready to return
				return true;
			} else if (curChain != -1 && (curChain = getNextChainIndex(curChain + 1)) != -1) { 
				//If all the chains have not been iterated over and there is 
				// another chain (with elements) after the last chain we searched to iterate over
				this.curChainIterator = chains[curChain].iterator();
				return true;
			}
			return false;
		}

		// Return the next item in the iteration
		// throw NoSuchElementException if a next item does not exist.
		@Override
		public KVPair<K, V> next() {
			if (this.hasNext()) {
				return curChainIterator.next();
			}
			throw new NoSuchElementException();
		}

		// Updates the state of the objects used to track the current iteration.
		// Takes in a start value for which chain to start searching from.
		// Returns the first index of a chain (including the start index)
		// that has item(s) to iterate over.
		// Returns -1 otherwise.
		private int getNextChainIndex(int start) {
			for (int i = start; i < chains.length; i++) {
				if (chains[i] != null && chains[i].size() > 0) {
					return i;
				}
			}
			return -1;
		}
	}
}
