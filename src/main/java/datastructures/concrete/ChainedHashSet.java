package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Represent a data structure that stores unique set of items

public class ChainedHashSet<T> implements ISet<T> {
	// This should be the only field you need
	private IDictionary<T, Boolean> map;

	public ChainedHashSet() {
		this.map = new ChainedHashDictionary<>();
	}

	// Pass an T object as parameter
	// Adds the given item to the set.
	// If the item is already in the list, it does nothing
	@Override
	public void add(T item) {
		if (!map.containsKey(item)) {
			map.put(item, true);
		}
	}

	// Pass a T object as parameter
	// Removes the given item from the data structure.
	@Override
	public void remove(T item) {
		if (!map.containsKey(item)) {
			throw new NoSuchElementException();
		}
		map.remove(item);
	}

	// Pass an T object as a Parameter
	// return true if the data structure contains the given object
	// false otherwise
	@Override
	public boolean contains(T item) {
		return map.containsKey(item);
	}

	// return the size of data structure
	@Override
	public int size() {
		return map.size();
	}

	// Returns a list of all data pairs contained within this data structure
	@Override
	public Iterator<T> iterator() {
		return new SetIterator<>(this.map.iterator());
	}

	// return all items in the HashSet
	private static class SetIterator<T> implements Iterator<T> {
		// This should be the only field you need
		private Iterator<KVPair<T, Boolean>> iter;

		public SetIterator(Iterator<KVPair<T, Boolean>> iter) {
			this.iter = iter;
		}

		// Returns 'true' if the iterator still has elements to look at; returns 'false'
		// otherwise
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		// Returns the next item in the iteration
		@Override
		public T next() {
			return iter.next().getKey();
		}
	}
}
