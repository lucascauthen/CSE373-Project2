package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import misc.exceptions.NotYetImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
	// HashDictionary, create a a larger dictionary and move data in old dictionary
	// to new dictionary
	private void resizeIfNeeded() {
		if (size > 10 * chains.length) {
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

	/**
	 * Hints:
	 *
	 * 1. You should add extra fields to keep track of your iteration state. You can
	 * add as many fields as you want. If it helps, our reference implementation
	 * uses three (including the one we gave you).
	 *
	 * 2. Before you try and write code, try designing an algorithm using pencil and
	 * paper and run through a few examples by hand.
	 *
	 * 3. Think about what exactly your *invariants* are. An *invariant* is
	 * something that must *always* be true once the constructor is done setting up
	 * the class AND must *always* be true both before and after you call any method
	 * in your class.
	 *
	 * Once you've decided, write them down in a comment somewhere to help you
	 * remember.
	 *
	 * You may also find it useful to write a helper method that checks your
	 * invariants and throws an exception if they're violated. You can then call
	 * this helper method at the start and end of each method if you're running into
	 * issues while debugging.
	 *
	 * (Be sure to delete this method once your iterator is fully working.)
	 *
	 * Implementation restrictions:
	 *
	 * 1. You **MAY NOT** create any new data structures. Iterators are meant to be
	 * lightweight and so should not be copying the data contained in your
	 * dictionary to some other data structure.
	 *
	 * 2. You **MAY** call the `.iterator()` method on each IDictionary instance
	 * inside your 'chains' array, however.
	 */
	private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
		private IDictionary<K, V>[] chains;
		private int curChain;
		private Iterator<KVPair<K, V>> curChainIterator;

		public ChainedIterator(IDictionary<K, V>[] chains) {
			this.chains = chains;
			this.curChain = getNextChainIndex(0);
			if (this.curChain != -1) {
				curChainIterator = chains[curChain].iterator();
			}
		}

		@Override
		public boolean hasNext() {
			if (curChainIterator != null && curChainIterator.hasNext()) {
				return true;
			} else if (curChain != -1 && (curChain = getNextChainIndex(curChain + 1)) != -1) { // Move to next chain
																								// with item (if
																								// possible)
				this.curChainIterator = chains[curChain].iterator();
				return true;
			}
			return false;
		}

		// Return the next item in the iteration
		// throw NoSuchElementException if next item does not exist.
		@Override
		public KVPair<K, V> next() {
			if (this.hasNext()) {
				return curChainIterator.next();
			}
			throw new NoSuchElementException();
		}

		
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
