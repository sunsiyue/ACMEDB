//author: Sun Siyue

package org.apache.derby.impl.services.cache;

final class LIRS extends Policy {

	public LIRS(int maxSize) {
		super(maxSize);
	}

	synchronized void addEntry(CacheEntry entry) {
		count ++;

		if ()







		double recency = 0.0;
		Item i = new Item(entry, recency);
		i.recency = recency;
		i.last = count;
		entries.add(i);
	}

	synchronized CacheEntry findVictim(boolean forUncache) {
		Item victim = findMin();
		if (forUncache)
			entries.remove(victim);
		return victim.entry;	
	}

	void incrHit(CacheEntry e) {
		count++;
		Item found = null;
		for (Item i:entries){
			if (i.entry == e){
				found = i;
				break;
			}
		}
		assert (found!= null);
		// update stats for cache-resident job
		double recency = count - 0.0;

		found.recency = recency;
		found.last = count;
		found.prio = recency;
	}

	String Name() {
		return "LIRS";
	}


}