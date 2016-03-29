//Author: Sun Siyue
package org.apache.derby.impl.services.cache;

final class 2Q extends Policy {

	//2 Q implementation need to be delayed
	//THIS IS A TEMPLATE!!!!
	public 2Q(int maxSize) {
		super(maxSize);
	}

	synchronized void addEntry(CacheEntry entry) {
		count++;
		entries.add(new Item(entry,(double) count));
	}

	synchronized CacheEntry findVictim(boolean forUncache) {
		Item victim = findMin();
		if (forUncache)
			entries.remove(victim);
		return victim.entry;
	}

	void incrHit(CacheEntry e) {
		count++;
	}

	String Name() {
		return "2Q";
	}

}