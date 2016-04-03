//Author: Sun Siyue
package org.apache.derby.impl.services.cache;

final class SFIFO extends Policy {

	public SFIFO() {
		primary = new ArrayList<Item>;
		secondary = new ArrayList<Item>;
	}

	public SFIFO(int maxSize) {
		primary = new ArrayList<Item>;
		secondary = new ArrayList<Item>;
		p_size = (int) (0.7 * maxSize);
		s_size = maxSize - p_size; //0.3 of maxSize
	}

	synchronized void addEntry(CacheEntry entry) {
		count++;
		entries.add(new Item(entry,(double) count));
//************************************************************
		count++;
		AddToPrimary(job);
		if (primary.getLength() > p_size) {
			AddToSecondary(RemoveFromPrimary());
		}

		incrUsed(job.JobSize());
	}

	synchronized CacheEntry findVictim(boolean forUncache) {
		Item victim = findMin();
		if (forUncache)
			entries.remove(victim);
		return victim.entry;

//************************************************************
		return secondary.DequeueFront();
	}

	void incrHit(CacheEntry e) {
		count++;
		//hit++;
		//byteHit += size;

		for (Item i:primary){
			if (i.entry == e){
				prio = i.prio;
				found = i;
				break;
			}
		}
		assert (found!= null);


		// if (primary.getJob(id)!= null) {
		// 	return;
		// }

		AddToPrimary(RemoveFromSecondary(e));
		if (primary.size() >= p_size) {
			AddToSecondary(RemoveFromPrimary());
		}




	}

	String Name() {
		return "SFIFO";
	}


	//***************************************************************
	//child class functions 

	public ArrayList<Item> primary;
	public ArrayList<Item> secondary;


	/* methods for the primary buffer */
	public void AddToPrimary(Job job){
		primary.add(0, job);

	}
	public CacheEntry RemoveFromPrimary(){
		return primary.DequeueFront();

	}

	/* methods for the secondary buffer */
	public CacheEntry FindInSecondary(int id){
		return secondary.getJob(id);

	}
	public void AddToSecondary(Job job){
		secondary.EnqueueJob(0, job);

	}
	public CacheEntry RemoveFromSecondary(int id){
		return secondary.DequeueJob(id);

	}

	public CacheEntry getJob(int id){
		Job retJob = null;

		retJob = primary.getJob(id);
		if (retJob == null) {
			retJob = secondary.getJob(id);
		}
		return retJob;

	}
	public CacheEntry release(int id){
		Job retjob;
		retjob = primary.getJob(id);
		if (retjob!= null) {
			return primary.DequeueJob(id);
		}
		else {
			return secondary.DequeueJob(id);
		}
	}
	public void Request(int id){
		if (secondary.getJob(id) != null) {
			AddToPrimary(RemoveFromSecondary(id));
			if (primary.getLength() >= p_size) {
				AddToSecondary(RemoveFromPrimary());
			}
		}
		else {
			Job newjob = new Job(id,0);
			AddToPrimary(newjob);
			if (primary.getLength() > p_size) {
				AddToSecondary(RemoveFromPrimary());
				if (secondary.getLength() > s_size) {
					secondary.DequeueFront();
				}
			}
		}

	}

}