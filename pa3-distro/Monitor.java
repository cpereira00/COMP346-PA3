/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	public static int numberOfChopsticks, numberOfPhilosophers;

	public enum Action{HUNGRY, EATING, THINKING, TALKING};
	public Action[] state;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers

		Monitor.numberOfChopsticks = piNumberOfPhilosophers;
		Monitor.numberOfPhilosophers = piNumberOfPhilosophers;

		System.out.println("There are "+numberOfChopsticks+" number of chopsticks.");


		//initialize all philosophers to thinking
		state = new Action[piNumberOfPhilosophers];

		for(int i=0;i< piNumberOfPhilosophers;i++){
			state[i] = Action.THINKING;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	//to pick up chopsticks, must check neighbors to see if not eating, also must pick up both chopsticks
	public synchronized void pickUp(final int piTID)
	{
		// ...
		state[piTID-1] = Action.HUNGRY;
		//do a test to check if neighbors are eaiting if so wait


		while(state[piTID-1] != Action.EATING){
			//self[piTID].wait();
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		// ...



		state[piTID-1] = Action.THINKING;

		notifyAll();

	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating). ->>>
	 */
	public synchronized void requestTalk(final int piTID)
	{
		// while state[piTID] != Action.EATING (if so wait), check all states of every philosopher to see if == Action.TALKING, if true wait(),
		// else state[piTID] = Action.TALKING;

		while(state[piTID-1] == Action.EATING){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		for(int i=0;i< state.length;i++){
			while(state[i] == Action.TALKING){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		state[piTID-1] = Action.TALKING;


	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		System.out.println("Philosophers are free to talk.");


		state[piTID-1] = Action.THINKING;
		notifyAll();
		// ...
	}
}

//notifallAll():It wakes up all the threads that called wait() on the same object.
// the lock is not actually given up until the notifier’s synchronized block has completed.

// wait(): It tells the calling thread to give up the lock and go to sleep until some other thread enters the same monitor and calls notify().


// EOF
