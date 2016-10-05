package edu.cwu.cs301.bb010g.pr1;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import edu.cwu.cs301.bb010g.pr1.PolyMultP1.Binomial;

@RunWith(Suite.class)
@SuiteClasses({PolyMultTest.PolyMultT.class, PolyMultTest.SpacedNumT.class,
    PolyMultTest.BinomialT.class, PolyMultTest.FormatT.class})
public class PolyMultTest {

  @RunWith(JUnitQuickcheck.class)
  public static class PolyMultT {
    @Property
    public void identity(final @Polynomial List<Integer> f, final boolean optimize) {
      Assert.assertEquals(f, PolyMultP1.polyMult(f, Collections.singletonList(1), optimize));
    }

    @Property
    public void associativity(final @Polynomial List<Integer> f, final @Polynomial List<Integer> g,
        final boolean optimize) {
      Assert.assertEquals(PolyMultP1.polyMult(f, g, optimize), PolyMultP1.polyMult(g, f, optimize));
    }

    @Property
    public void communitivity(final @Polynomial List<Integer> f, final @Polynomial List<Integer> g,
        @Polynomial final List<Integer> h, final boolean optimize) {
      Assert.assertEquals(PolyMultP1.polyMult(PolyMultP1.polyMult(f, g, optimize), h, optimize),
          PolyMultP1.polyMult(f, PolyMultP1.polyMult(g, h, optimize), optimize));
    }

    @Property
    public void monomialSanity(final int f_a, final int g_a, final boolean optimize) {
      final List<Integer> f = Collections.singletonList(f_a);
      final List<Integer> g = Collections.singletonList(g_a);
      Assert.assertEquals(Collections.singletonList(f_a * g_a),
          PolyMultP1.polyMult(f, g, optimize));
    }

    @Property
    public void scalar(@Polynomial final List<Integer> f, final int g_a, final boolean optimize) {
      final List<Integer> g = Collections.singletonList(g_a);
      Assert.assertEquals(f.stream().map(n -> n * g_a).collect(Collectors.toList()),
          PolyMultP1.polyMult(f, g, optimize));
    }

    @Property
    public void binomial(final int f_a, final int f_b, final int g_a, final int g_b,
        final boolean optimize) {
      final List<Integer> f = new Binomial(f_a, f_b);
      final List<Integer> g = new Binomial(g_a, g_b);
      Assert.assertEquals(Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b, f_b * g_b),
          PolyMultP1.polyMult(f, g, optimize));
    }

    @Property
    public void trinomialBinomial(final int f_a, final int f_b, final int g_a, final int g_b,
        final int g_c, final boolean optimize) {
      final List<Integer> f = new Binomial(f_a, f_b);
      final List<Integer> g = Arrays.asList(g_a, g_b, g_c);
      Assert.assertEquals(
          Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b, f_a * g_c + f_b * g_b, f_b * g_c),
          PolyMultP1.polyMult(f, g, optimize));
    }

    @Property
    public void trinomial(final int f_a, final int f_b, final int f_c, final int g_a, final int g_b,
        final int g_c, final boolean optimize) {
      final List<Integer> f = Arrays.asList(f_a, f_b, f_c);
      final List<Integer> g = Arrays.asList(g_a, g_b, g_c);
      final List<Integer> prod = Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b,
          f_a * g_c + g_a * f_c + f_b * g_b, f_b * g_c + g_b * f_c, f_c * g_c);
      Assert.assertEquals(prod, PolyMultP1.polyMult(f, g, optimize));
    }
  }

  @RunWith(JUnitQuickcheck.class)
  public static class SpacedNumT {
    @Property
    public void positive(final @InRange(min = "0") int n) {
      final StringBuilder sb = new StringBuilder();
      PolyMultP1.spacedNum(sb, n);
      Assert.assertEquals("+ " + Integer.toString(n), sb.toString());
    }

    @Property
    public void negative(final @InRange(min = "1") int n) {
      final StringBuilder sb = new StringBuilder();
      PolyMultP1.spacedNum(sb, -n);
      Assert.assertEquals("- " + Integer.toString(n), sb.toString());
    }
  }

  @RunWith(JUnitQuickcheck.class)
  public static class BinomialT {
    @Property
    public void preserve(final int constant, final int coefficient) {
      final Binomial binom = new Binomial(constant, coefficient);
      Assert.assertEquals(constant, binom.constant);
      Assert.assertEquals(coefficient, binom.coefficient);
    }

    @Property
    public void mapping(final @Binom Binomial binom, final @InRange(min = "2") int oob) {
      Assert.assertEquals(binom.constant, (int) binom.get(0));
      Assert.assertEquals(binom.coefficient, (int) binom.get(1));
      try {
        binom.get(oob);
        Assert.fail("Binomial::get(int)");
      } catch (final IndexOutOfBoundsException e) {
      }
    }

    @Property
    public void modification(final @Binom Binomial binom, final int constant, final int coefficient,
        final @InRange(min = "2") int oob) {
      binom.set(0, constant);
      binom.set(1, coefficient);
      Assert.assertEquals(constant, binom.constant);
      Assert.assertEquals(coefficient, binom.coefficient);
      try {
        binom.set(oob, 0);
        Assert.fail("Binomial::get(int)");
      } catch (final IndexOutOfBoundsException e) {
      }
    }

    @Property
    public void toArray(final @Binom Binomial binom) {
      Assert.assertArrayEquals(new Integer[] {binom.constant, binom.coefficient}, binom.toArray());
    }

    @Property
    public void toArrayIdentity(final @Binom Binomial binom) {
      Assert.assertArrayEquals(binom.toArray(), binom.toArray(new Integer[] {}));
    }

    @Property
    public void constantSize(final @Binom Binomial binom) {
      Assert.assertEquals(2, binom.size());
    }

    @Property
    public void neverEmpty(final @Binom Binomial binom) {
      Assert.assertFalse(binom.isEmpty());
    }

    @Property
    public void iterator(final @Binom Binomial binom) {
      final Iterator<Integer> iter = binom.iterator();
      Assert.assertTrue(iter.hasNext());
      try {
        iter.remove();
        Assert.fail("Expected UnsupportedOperationException");
      } catch (UnsupportedOperationException e) {
      }
      Assert.assertEquals(binom.constant, (int) iter.next());
      Assert.assertTrue(iter.hasNext());
      try {
        iter.remove();
        Assert.fail("Expected UnsupportedOperationException");
      } catch (UnsupportedOperationException e) {
      }
      Assert.assertEquals(binom.coefficient, (int) iter.next());
      Assert.assertFalse(iter.hasNext());
      try {
        iter.next();
        Assert.fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {
      }
    }

    @Property
    public void toString(final @Binom Binomial binom) {
      Assert.assertEquals(binom.toString(), PolyMultP1.formatPolyInc(binom));
    }

    @Property
    public void contains(final @Binom Binomial binom) {
      final Random random = new Random();
      int other = random.nextInt();
      while (other == binom.constant || other == binom.coefficient) {
        other = random.nextInt();
      }
      Assert.assertTrue(binom.contains(binom.constant));
      Assert.assertTrue(binom.contains(binom.coefficient));
      Assert.assertFalse(binom.contains(other));
    }

    @Property
    public void containsAll(final @Binom Binomial binom, final Collection<Integer> c) {
      Assert.assertTrue(binom.containsAll(Collections.emptySet()));
      Assert.assertTrue(binom.containsAll(Collections.singleton(binom.constant)));
      Assert.assertTrue(binom.containsAll(Collections.singleton(binom.coefficient)));
      final TreeSet<Object> set = new TreeSet<>();
      set.add(binom.constant);
      set.add(binom.coefficient);
      Assert.assertTrue(binom.containsAll(set));
      boolean cContains = true;
      for (final int n : c) {
        if (n != binom.constant && n != binom.coefficient) {
          cContains = false;
          break;
        }
      }
      Assert.assertEquals(cContains, binom.containsAll(c));
    }

    @Test
    public void indexOf() {
      final Random random = new Random();
      final int constant = random.nextInt();
      int coefficient = random.nextInt();
      // ensure they're not identical
      while (coefficient == constant) {
        coefficient = random.nextInt();
      }
      final Binomial binom = new Binomial(constant, coefficient);
      int other = random.nextInt();
      while (other == binom.constant || other == binom.coefficient) {
        other = random.nextInt();
      }
      Assert.assertEquals(0, binom.indexOf(binom.constant));
      Assert.assertEquals(0, binom.lastIndexOf(binom.constant));
      Assert.assertEquals(1, binom.indexOf(binom.coefficient));
      Assert.assertEquals(1, binom.lastIndexOf(binom.coefficient));
      Assert.assertEquals(-1, binom.indexOf(other));
      Assert.assertEquals(-1, binom.lastIndexOf(other));
    }

    @Property
    public void indexOfMany(final int n) {
      final Binomial binom = new Binomial(n, n);
      Assert.assertEquals(0, binom.indexOf(n));
      Assert.assertEquals(1, binom.lastIndexOf(n));
    }

    @Property
    public void unsupported(final @Binom Binomial binom, final int dummyI, final int dummyVal,
        final Collection<Integer> dummyC) {
      try {
        binom.add(dummyVal);
        Assert.fail("Binomial::add(int)");
      } catch (final UnsupportedOperationException e) {
      }
      try {
        binom.add(dummyI, dummyVal);
        Assert.fail("Binomial::add(int, int)");
      } catch (final UnsupportedOperationException e) {
      }
      try {
        binom.addAll(dummyC);
        Assert.fail("Binomial::addAll(Collection<? extends Integer>)");
      } catch (final UnsupportedOperationException e) {
      }
      try {
        binom.addAll(dummyI, dummyC);
        Assert.fail("Binomial::addAll(int, Collection<? extends Integer>)");
      } catch (final UnsupportedOperationException e) {
      }

      try {
        binom.remove(dummyI);
        Assert.fail("Binomial::remove(int)");
      } catch (final UnsupportedOperationException e) {
      }
      try {
        binom.remove(new Integer(dummyVal));
        Assert.fail("Binomial::remove(Object)");
      } catch (final UnsupportedOperationException e) {
      }
      try {
        binom.removeAll(dummyC);
        Assert.fail("Binomial::removeAll(Collection<?>)");
      } catch (final UnsupportedOperationException e) {
      }
      try {
        binom.retainAll(dummyC);
        Assert.fail("Binomial::retainAll(Collection<?>)");
      } catch (final UnsupportedOperationException e) {
      }
      try {
        binom.clear();
        Assert.fail("Binomial::clear()");
      } catch (final UnsupportedOperationException e) {
      }
    }
  }

  @RunWith(JUnitQuickcheck.class)
  public static class FormatT {
    @Property
    public void decMonomial(final int a) {
      Assert.assertEquals(String.format("% d", a),
          PolyMultP1.formatPolyDec(Collections.singletonList(a)));
    }

    @Property
    public void decBinomialPos(final int b, final @InRange(min = "0") int a) {
      Assert.assertEquals(String.format("% dx + %d", b, a),
          PolyMultP1.formatPolyDec(new Binomial(a, b)));
    }

    @Property
    public void decBinomialNeg(final int b, final @InRange(min = "1") int a) {
      Assert.assertEquals(String.format("% dx - %d", b, a),
          PolyMultP1.formatPolyDec(new Binomial(-a, b)));
    }
  }
}
