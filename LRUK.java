// auther: Sun Siyue
package org.apache.derby.impl.services.cache;

final class LRUK extends Policy{
	private int K_value = 2;



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


		count++;
		hit++;
		byteHit += size;

		int cprp;
		Job histjob = Hist.getJob(id), pqjob = pq.getJob(id);

		if (count - pqjob.last > CRP) {
			cprp = pqjob.last - histjob.k[0];
			// for (int i=0;i<=1;i++)
			histjob.k[1] = histjob.k[0] + cprp;
			histjob.k[0] = count;

			int curtime = (int) (System.currentTimeMillis() / 1000L);;

			histjob.k_time[1] = curtime;
			histjob.k_time[0] = curtime;

			pqjob.last = count;
		}
		else {
			pqjob.last = count;
		}

		

	}

	String Name() {
		return "LRUK";
	}

}
