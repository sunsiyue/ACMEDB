// auther: Sun Siyue
package org.apache.derby.impl.services.cache;

final class LRUK extends Policy{
	private int K_value = 2;



	LRUK(int maxSize) {
		super(maxSize);
		// TODO Auto-generated constructor stub


		Hist = new ArrayList<Item>;
		CRP = 5000;
		RIP = 200;
		trimtime = (int) (System.currentTimeMillis() / 1000L);

	}

	synchronized void addEntry(CacheEntry entry) {
		count++;
		entries.add(new Item(entry, (double) count));

		count++;
		//incrUsed(job.JobSize());
		CacheEntry i = entry;
		//int id = job.JobID();
		CacheEntry histjob = null;
		histjob = Hist.getJob(id);

		if (histjob == null) {
			histjob = new Job(id,1);
			Hist.EnqueueJob(0, histjob);
			// for (int i=0;i<=1;i++)
			histjob.k[1] = 0;
		}
		else {
			histjob.k[1] = histjob.k[0];
		}

		int curtime = (int) (System.currentTimeMillis() / 1000L);

		histjob.k_time[1] = curtime;

		histjob.k[0] = count;
		histjob.k_time[0] = curtime;

		job.last = count;


	}

	synchronized CacheEntry findVictim(boolean forUncache) {
		Item victim = findMin();
		if (forUncache)
			entries.remove(victim);
		return victim.entry;



		int min, id, victim=0;
		Node curr = pq.jobList;

		TrimHistory();

		min = count;

		if (curr!=null) {
			id = curr.getJob().JobID();
			victim = id;
			curr = curr.next;
		}

		while (curr!=null) {
			Job pqjob = curr.getJob();
			id = pqjob.JobID();
			Job histjob = Hist.getJob(id);
			if ((count - pqjob.last > CRP) && (histjob.k[1] < min)) {
				victim = id;
				min = histjob.k[1];
				if (min == 0) break;
			}
			curr = curr.next;
		}

		return pq.DequeueJob(victim);



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


	//***************************************************************
	//child class functions
	public void TrimHistory(){
		int curtime = (int) (System.currentTimeMillis() / 1000L);

		if ((curtime - trimtime) < RIP) {
			//cout << "no trim" << endl;
			return;
		}

		//cout << "TrimHistory" << endl;

		trimtime = curtime;

		int id;
		Node curr = Hist.jobList;
		double diff1, diff2;
		Job histjob = null;

		while (curr!=null) {
			histjob = curr.getJob();
			id = histjob.JobID();

			diff1 = curtime - histjob.k_time[0];
			diff2 = curtime - histjob.k_time[1];

			curr = curr.next;

			if ((diff1 > RIP) && (diff2 > RIP) && (pq.getJob(id) == null)) {
				Job jobToDelete = Hist.DequeueJob(id);
				jobToDelete = null;
			}
		}

		return;
	}

	public ArrayList<Item> Hist;
	int CRP, RIP;
	int trimtime;
}
