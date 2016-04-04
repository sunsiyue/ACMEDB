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
		//AddToPrimary(entry);
		// if (primary.size() > p_size) {
		// 	AddToSecondary(RemoveFromPrimary());
		// }
		if (primary.size() < p_size) {
			AddToPrimary(entry);
		} else {
			AddToSecondary(entry);
		}

		//incrUsed(job.JobSize());
	}

	synchronized CacheEntry findVictim(boolean forUncache) {
		Item victim = findMin();
		if (forUncache)
			entries.remove(victim);
		return victim.entry;

//************************************************************
		//return secondary.DequeueFront();
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
		//assert (found!= null);


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
	public void AddToPrimary(CacheEntry job){
		primary.add(0, job);

	}
	public CacheEntry RemoveFromPrimary(){
		
		primary.remove();


		//return primary.DequeueFront();

	}

	/* methods for the secondary buffer */
	public Item FindInSecondary(CacheEntry e){
		return secondary.getJob(id);
		Item found = null;
		for (Item i:entries){
			if (i.entry == e){
				found = i;
				break;
			}
		}
		return found;
	}
	public void AddToSecondary(CacheEntry e){
		secondary.add(0, e);

	}
	public Item RemoveFromSecondary(CacheEntry e){
		//return secondary.DequeueJob(id);
		Item found = null;
		for (Item i:entries){
			if (i.entry == e){
				found = i;
				break;
			}
		}
		secondary.remove(e);
		return found;

	}

	public Item getJob(CacheEntry e){
		Item found = null;
		for (Item i:entries){
			if (i.entry == e){
				found = i;
				break;
			}
		}

		return found;
		// secondary.remove(e);
		// return found;


		// Job retJob = null;

		// retJob = primary.getJob(id);
		// if (retJob == null) {
		// 	retJob = secondary.getJob(id);
		// }
		// return retJob;

	}
	public CacheEntry release(CacheEntry e){
		Item found;
		found = primary.getJob(e);
		if (found.entry!= null) {
			return primary.remove(found);
		}
		else {
			return secondary.add(found);
		}
		return found;
	}
	public void Request(CacheEntry e){
		if (secondary.getJob(e) != null) {
			AddToPrimary(RemoveFromSecondary(e));
			if (primary.size() >= p_size) {
				AddToSecondary(RemoveFromPrimary());
			}
		}
		else {
			Item i = new Item(e,0);
			AddToPrimary(i);
			if (primary.size() > p_size) {
				AddToSecondary(RemoveFromPrimary());
				if (secondary.size() > s_size) {
					secondary.remove();
				}
			}
		}

	}

}