import java.util.Arrays;
import java.util.Collections;
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
	private static int numberOfChopsticks, numberOfPhilosophers;

	private enum Action{HUNGRY, EATING, THINKING, TALKING};
	private Action[] state;

	private Integer[] priorityTalkCheck;
	private int[] priorityEatCheck;
	private Boolean[] isChopstickAvailable;

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
		state = new Action[numberOfPhilosophers];
		priorityTalkCheck = new Integer[numberOfPhilosophers];
		isChopstickAvailable = new Boolean[numberOfChopsticks];
		priorityEatCheck = new int[numberOfPhilosophers];

		for(int i=0;i< piNumberOfPhilosophers;i++){
			state[i] = Action.THINKING;
			isChopstickAvailable[i] = true;
			priorityTalkCheck[i] = 0;
			priorityEatCheck[i] = 0;
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
		while(!isChopstickAvailable[piTID - 1] && !isChopstickAvailable[piTID % numberOfPhilosophers]){
			
			try {
				wait();
				priorityEatCheck[piTID - 1] ++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}

		int leftIndex = piTID - 2;
		if (leftIndex < 0) leftIndex = numberOfPhilosophers - 1;

		while(priorityEatCheck[piTID - 1] < .75 * priorityEatCheck[leftIndex] || priorityEatCheck[piTID - 1] < .75 * priorityEatCheck[piTID % numberOfPhilosophers]){
			
			try {
				wait();
				priorityEatCheck[piTID - 1] ++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}

		state[piTID - 1] = Action.EATING;
		isChopstickAvailable[piTID - 1] = false;
		isChopstickAvailable[piTID % numberOfPhilosophers] = false;
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		
		isChopstickAvailable[piTID - 1] = true;
		isChopstickAvailable[piTID % numberOfPhilosophers] = true;
		state[piTID - 1] = Action.THINKING;
		priorityEatCheck[piTID - 1] = 0;

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
		/*
		ISSUE at hand, doesnt prevent starvation since notifyall calls wakes up any thread
		Solution: assign an int to each philosopher acting as a priority, and check amongst the highest before automatically allowing to talk,
				  Everytime theres a wait() increment their counter, everytime there's a notifall() return to 0,
		 */

		for(int i=0;i < state.length;i++){
			while(state[i] == Action.TALKING){
				try {
					wait();
					priorityTalkCheck[piTID-1]++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		//while your priority isnt high enough compared to other philos you wait, else you can talk
		while(priorityTalkCheck[piTID-1] < .75*(Collections.max(Arrays.asList(priorityTalkCheck)))) {
			try {
				wait();
				priorityTalkCheck[piTID-1]++;
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		priorityTalkCheck[piTID-1] = 0;
		notifyAll();
		// ...
	}
}

//notifallAll():It wakes up all the threads that called wait() on the same object.
// the lock is not actually given up until the notifier’s synchronized block has completed.

// wait(): It tells the calling thread to give up the lock and go to sleep until some other thread enters the same monitor and calls notify().


// EOF
