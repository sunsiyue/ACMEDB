package org.apache.derby.impl.services.cache;
// auther: Sun Siyue
// This will be my very start of the FYP!!


final class LRUK extends Policy{
	
	LRUK(int maxSize) {
		super(maxSize);
		// TODO Auto-generated constructor stub
	}

	synchronized void addEntry(CacheEntry entry) {
		count++;
		entries.add(new Item(entry, (double) count));
	}

	synchronized CacheEntry findVictim(boolean forUncache) {
		Item victim = findMin();
		if (forUncache)
			entries.remove(victim);
		return victim.entry;
	}

	void incrHit(CacheEntry e) {
		count++;
		if (count == 2) {
			adjustPrio(e, count);
		}

	}

	String Name() {
		return "LRUK";
	}

}
