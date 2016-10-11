package edu.cwu.cs301.bb010g.pr1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.io.Files;
import com.google.common.math.BigIntegerMath;

public class FactorP1 {
  public static void main(final String[] args) {
    main(Arrays.asList(args));
  }

  public static void main(final List<String> args) {
    try {
      final BigInteger n =
          new BigInteger(Files.readFirstLine(new File("integer.txt"), StandardCharsets.UTF_8));
      final FactorIter factoring = new FactorIter(n);
      while (factoring.hasNext()) {
        System.out.println(factoring.next());
      }
      System.err.println("sqrt calls: " + factoring.sqrtCount);

    } catch (final FileNotFoundException e) {
      System.out.println("File integer.txt is missing.");
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * A tag for {@link FactorInnerIter} to tell what type of factor is being put out.
   */
  public static enum FactorType {
    PRIME, FACTOR;
  }

  public static class FactorIter implements Iterator<BigInteger> {
    public Deque<FactorInnerIter> inners = new ArrayDeque<>();
    public long sqrtCount = 0;

    public FactorIter(final BigInteger n) {
      this.inners.push(new FactorInnerIter(n));
    }

    @Override
    public boolean hasNext() {
      return !this.inners.isEmpty();
    }

    @Override
    public BigInteger next() {
      if (!this.hasNext()) {
        throw new NoSuchElementException();
      }
      while (true) {
        final Pair<BigInteger, FactorType> out;
        {
          final FactorInnerIter head = this.inners.peek();
          out = head.next();
          // System.out.print(Integer.toString(inners.size()) + " " + out.toString());
          if (!head.hasNext()) {
            this.inners.pop();
            this.sqrtCount += head.sqrtCount;
          }
        }
        // If it's a prime, we've got a valuable factor and return it. If it's just another
        // indeterminate factor, try to get more out of it.
        switch (out.snd) {
          case PRIME:
            // System.out.println();
            return out.fst;
          case FACTOR:
            this.inners.push(new FactorInnerIter(out.fst));
            // System.out.println(" > "+Integer.toString(inners.size()));
            break;
        }
      }
    }

    // This is a default in Java 8. Isn't that nice?
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }

  public static class FactorInnerIter implements Iterator<Pair<BigInteger, FactorType>> {
    private BigInteger n;
    private boolean done = false;
    public long sqrtCount = 0;

    public FactorInnerIter(final BigInteger n) {
      this.n = n;
    }

    @Override
    public boolean hasNext() {
      return !this.done;
    }

    @Override
    public Pair<BigInteger, FactorType> next() {
      if (this.done) {
        throw new NoSuchElementException();
      }
      // If it's negative, immediately eliminate -1 as a factor. As a bonus, we can kill 0 here too
      // for just an int comparison!
      switch (this.n.signum()) {
        case -1:
          this.n = this.n.abs();
          return Pair.of(NEG_ONE, FactorType.PRIME);
        case 0:
          this.done = true;
          return Pair.of(ZERO, FactorType.PRIME);
      }
      // Check if it's even and immediately divide by two. If it actually is two, take care of it
      // right here.
      if (this.n.remainder(FactorP1.TWO).equals(FactorP1.ZERO)) {
        if (this.n.equals(FactorP1.TWO)) {
          this.done = true;
        } else {
          this.n = this.n.divide(FactorP1.TWO);
        }
        return Pair.of(FactorP1.TWO, FactorType.PRIME);
      }
      // The combination of the named block and ret variable allow for what acts a lot like early
      // return without the actually returning part so we can finish up a bit after.
      final Pair<BigInteger, FactorType> ret;
      // This is the algorithm described in the project specification. Results are tagged with
      // whether they're a prime or just another factor to continue looking at.
      ret: {
        this.sqrtCount++;
        BigInteger x = BigIntegerMath.sqrt(this.n, RoundingMode.FLOOR);
        if (this.n.equals(x.pow(2))) {
          this.n = x;
          ret = Pair.of(x, FactorType.FACTOR);
          break ret;
        }
        while (true) {
          x = x.add(FactorP1.ONE);
          if (x.equals(this.n.add(FactorP1.ONE).divide(FactorP1.TWO))) {
            this.done = true;
            ret = Pair.of(this.n, FactorType.PRIME);
            break ret;
          }
          this.sqrtCount++;
          final BigInteger yInner = x.pow(2).subtract(this.n);
          final BigInteger y = BigIntegerMath.sqrt(yInner, RoundingMode.FLOOR);
          if (y.pow(2).equals(yInner)) {
            BigInteger z = x.subtract(y);
            if (!this.n.remainder(z).equals(FactorP1.ZERO)) {
              z = x.add(y);
            }
            this.n = this.n.divide(z);
            ret = Pair.of(z, FactorType.FACTOR);
            break ret;
          }
        }
      }
      // If we hit one somehow, handle it so we don't start looping (1^2 = 1).
      if (this.n.equals(ONE)) {
        this.done = true;
        if (ret.fst.equals(ONE)) {
          ret.snd = FactorType.PRIME;
        }
      }
      return ret;
    }

    // This is a default in Java 8. Isn't that nice?
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }

  static final BigInteger NEG_ONE = BigInteger.valueOf(-1);
  static final BigInteger ZERO = BigInteger.ZERO;
  static final BigInteger ONE = BigInteger.ONE;
  static final BigInteger TWO = BigInteger.valueOf(2);
}
