package edu.coursera.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import edu.rice.pcdp.Actor;
import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
    	final SieveActorActor sieve = new SieveActorActor(2);
        finish(() -> {
        	for(int i  = 3 ; i <= limit ; i++) {
                 sieve.send(i);
        	}
        	   sieve.send(0);
            }    
        );
        SieveActorActor sievet = sieve;
        int primecount = 0 ;
        while(sievet != null ) {
        	primecount  += sievet.getPrimeCount();
        	sievet = sievet.nextActor();
        }
        return primecount-1;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        
    	private final Integer max = 10000;
    	private final List<Integer> primes = new ArrayList<Integer>();
    	private int numprimes = 0 ; 
        public SieveActorActor next;
    	
    	public SieveActorActor(int val) {
			super();
			this.primes.add(max);
			this.numprimes = 1;
			this.next = null;
    		// TODO Auto-generated constructor stub
		}
    	
    	public int getPrimeCount() {
    		return numprimes;
    	}
    	
    	public SieveActorActor nextActor() {
    		return next;
    	}

		    	
    	/**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final Integer candidate = (Integer) msg;
            if(candidate <= 0 ) {
            	if(this.next != null) {
            	//	next.send(msg);
            	}
                 
            	//return; 
            
            }
            else{
            	if(checkPrime(candidate.intValue())) { 
            	    if(numprimes < max) {
            	    	 primes.add(candidate);
            	    	 numprimes += 1;
            	    }else if(next == null) {
            	    	next = new SieveActorActor(candidate);
            	    	//next.start();
            	    }else{
            	    	next.send(msg);
            	    }
                 
            		
                }
            
            } 
        }
        
        
        public boolean checkPrime(int candidate) {
        	
        	return !(primes.parallelStream().anyMatch(prime -> (candidate%prime == 0 ) ));
        	
        }
    }
}
