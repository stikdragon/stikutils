package uk.co.stikman.utils.math;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UniqueList<T> implements Iterable<T> {

	private LinkedList<T>	list	= new LinkedList<T>();
	private Set<T>			set		= new HashSet<T>();

	public void add(T x) {
		if (set.contains(x))
			list.remove(x);
		list.add(x);
		set.add(x);
	}

	public T removeLast() {
		return list.removeLast();
	}

	public boolean contains(T x) {
		return set.contains(x);
	}

	public void clear() {
		list.clear();
		set.clear();
	}

	public void extractAll(List<T> result) {
		result.addAll(list);
		clear();
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

}
