package edu.cwu.cs301.bb010g.pr1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.google.common.math.BigIntegerMath;

public class FactorP1 {
  public static void main(final String[] args) {
    try (final FileInputStream fis = new FileInputStream("integer.txt");
        final InputStreamReader isr = new InputStreamReader(fis);
        final BufferedReader br = new BufferedReader(isr);) {

      final BigInteger n = new BigInteger(br.readLine());
      final Pair<List<BigInteger>, Integer> factoring = FactorP1.factor(n);
      System.err.println("sqrt calls: " + factoring.snd);
      System.out.println(factoring.fst);

    } catch (FileNotFoundException e) {
      System.out.println("File integer.txt is missing.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static final BigInteger ZERO = BigInteger.ZERO;
  static final BigInteger ONE = BigInteger.ONE;
  static final BigInteger TWO = BigInteger.valueOf(2);

  public static Pair<List<BigInteger>, Integer> factor(BigInteger n) {
    final List<BigInteger> factors = new ArrayList<>();
    int sqrtCount = 0;
    factoring: while (true) {
      if (n.remainder(TWO).equals(ZERO)) {
        factors.add(TWO);
        if (n.equals(TWO)) {
          break factoring;
        }
        n = n.divide(TWO);
        continue factoring;
      }
      sqrtCount++;
      BigInteger x = BigIntegerMath.sqrt(n, RoundingMode.FLOOR);
      if (x.pow(2).equals(n)) {
        factors.add(x);
        n = x;
        continue factoring;
      }
      while (true) {
        x = x.add(ONE);
        if (x.equals(n.add(ONE).divide(TWO))) {
          factors.add(n);
          break factoring;
        }
        sqrtCount++;
        final BigInteger yInner = x.pow(2).subtract(n);
        final BigInteger y = BigIntegerMath.sqrt(yInner, RoundingMode.FLOOR);
        if (y.pow(2).equals(yInner)) {
          BigInteger z = x.add(y);
          if (!n.remainder(z).equals(ZERO)) {
            z = x.subtract(y);
          }
          factors.add(z);
          n = n.divide(z);
          continue factoring;
        }
      }
    }
    return Pair.of(factors, sqrtCount);
  }
}
