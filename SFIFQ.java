//Author: Sun Siyue
package org.apache.derby.impl.services.cache;

final class SFIFO extends Policy {

	public SFIFO(int maxSize * 0.7) {
		super(maxSize * 0.7);

		//todo: init LRU with maxSize * 0.3

		//

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
		return "SFIFO";
	}

}