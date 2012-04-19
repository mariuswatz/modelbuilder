package unlekker.util;

import java.lang.*;
import java.lang.reflect.*;
import java.io.Serializable;
import ec.util.*;
 
/**
 * <p><code>unlekker.util.Rnd</code> provides an alternative to java.util.rand by wrapping ec.util.MersenneTwisterFast in a Processing-friendly convenience class. </p>
 * <p><A HREF="http://www.math.keio.ac.jp/matumoto/ehtm">Mersenne Twister</A> is an advanced pseudo-random number generator with a period of 2^19937-1. The code 
 * used here is from Sean Luke, and is part of his <a href="http://cs.gmu.edu/~eclab/projects/ecj/">ECJ Evolutionary Computation Research System.</a></p>
 *
 * @author <a href="http://workshop.evolutionzone.com/">Marius Watz</a>
 */

public class Rnd implements Serializable {
  private static long seedCnt;
  private MersenneTwisterFast mt;
  public long seed;

    /**
      * Initialize rand number generator.
      * When called the first time the seed 0 will be used. An internal static variable is then incremented, so that the next instance will have seed 1 etc.
  */

  	public Rnd(java.util.Date d) {  		
  		this(d.getTime());
  	}
  	
    public Rnd() {
    	this(seedCnt++);
    }

    public Rnd(long seed) {
    	mt=new MersenneTwisterFast(seed);
    	mt.setSeed(seed);
    }
    
    /**
     * Randomly returns true or false.
     * @see #rand
     * @see #randInt
     * @see #prob   * @return boolean
     *
     **/

    public boolean bool() {
    	return mt.nextBoolean();
    }
    
    public float randomSign() {
    	if(bool()) return -1;
    	else return 1;
    }

    public double dbl() {
			return mt.nextDouble();
		}

    public double dbl(double range) {
      return range*mt.nextDouble();
    }

    public double dbl(double min,double max) {
      return (max-min)*mt.nextDouble()+min;
    }

    /**
     * Returns true if random00) returns a result greater than the parameter "chance".
     * @param chance double
     * @see #rand
     * @see #randInt
     * @see #randBool
     * @return boolean
     */

    public boolean prob(float chance) {
      return mt.nextBoolean(1-chance/100f);
    }

    /**
     * Returns a rand value in the ranges [0..1&gt;, [0..range&gt; or [min..max&gt; depending on the version used.
     * @see #randInt 
     * @see #randBool
     * @see #probprob @return float
     * */

    public float random() {
      return mt.nextFloat();
    }

    public float random(float range) {
      return range*mt.nextFloat();
    }

    public float random(float min,float max) {
      return (max-min)*mt.nextFloat()+min;
    }

    /**
     * Returns a rand integer value in the range [0..range-1&gt; or [min..max-1&gt;.
     * @param range 
     * @see #rand
     * @see #randBool
     * @see #prob
  probturn int
     */

    public int integer(int range) {
    	return mt.nextInt(range);
    }

    public int integer(int min,int max) {
      return mt.nextInt(max-min)+min;
    }

    public int integer(float min,float max) {
      return mt.nextInt((int)(max-min))+(int)min;
    }

}
