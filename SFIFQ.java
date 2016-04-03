//Author: Sun Siyue
package org.apache.derby.impl.services.cache;

final class SFIFO extends Policy {

	public SFIFO() {
		primary = new PriorityQueue();
		secondary = new PriorityQueue();
	}

	public SFIFO(int maxSize) {
		primary = new PriorityQueue();
		secondary = new PriorityQueue();
		p_size = (int) (0.7 * maxSize);
		s_size = maxSize - p_size; //0.3 of maxSize
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


	/* methods for the primary buffer */
	public void AddToPrimary(Job job){
		primary.EnqueueJob(0, job);

	}
	public Job RemoveFromPrimary(){
		return primary.DequeueFront();

	}

	/* methods for the secondary buffer */
	public Job FindInSecondary(int id){
		return secondary.getJob(id);

	}
	public void AddToSecondary(Job job){
		secondary.EnqueueJob(0, job);

	}
	public Job RemoveFromSecondary(int id){
		return secondary.DequeueJob(id);

	}

	public Job getJob(int id){
		Job retJob = null;

		retJob = primary.getJob(id);
		if (retJob == null) {
			retJob = secondary.getJob(id);
		}
		return retJob;

	}
	public Job release(int id){
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