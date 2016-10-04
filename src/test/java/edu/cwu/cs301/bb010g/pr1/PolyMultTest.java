package edu.cwu.cs301.bb010g.pr1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import edu.cwu.cs301.bb010g.pr1.PolyMultP1.Binomial;

@RunWith(Suite.class)
@SuiteClasses({PolyMultTest.PolyMultT.class, PolyMultTest.SpacedNumT.class,
    PolyMultTest.BinomialT.class})
public class PolyMultTest {

  @RunWith(JUnitQuickcheck.class)
  public static class PolyMultT {
    @Property
    public void identity(@Polynomial final List<Integer> f) {
      Assert.assertEquals(f, PolyMultP1.polyMult(f, Collections.singletonList(1)));
    }

    @Property
    public void associativity(@Polynomial final List<Integer> f,
        @Polynomial final List<Integer> g) {
      Assert.assertEquals(PolyMultP1.polyMult(f, g), PolyMultP1.polyMult(g, f));
    }

    @Property
    public void communitivity(@Polynomial final List<Integer> f, @Polynomial final List<Integer> g,
        @Polynomial final List<Integer> h) {
      Assert.assertEquals(PolyMultP1.polyMult(PolyMultP1.polyMult(f, g), h),
          PolyMultP1.polyMult(f, PolyMultP1.polyMult(g, h)));
    }

    @Property
    public void monomialSanity(final int f_a, final int g_a) {
      final List<Integer> f = Collections.singletonList(f_a);
      final List<Integer> g = Collections.singletonList(g_a);
      Assert.assertEquals(Collections.singletonList(f_a * g_a), PolyMultP1.polyMult(f, g));
    }

    @Property
    public void scalar(@Polynomial final List<Integer> f, final int g_a) {
      final List<Integer> g = Collections.singletonList(g_a);
      Assert.assertEquals(f.stream().map(n -> n * g_a).collect(Collectors.toList()),
          PolyMultP1.polyMult(f, g));
    }

    @Property
    public void binomial(final int f_a, final int f_b, final int g_a, final int g_b) {
      final List<Integer> f = new Binomial(f_a, f_b);
      final List<Integer> g = new Binomial(g_a, g_b);
      Assert.assertEquals(Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b, f_b * g_b),
          PolyMultP1.polyMult(f, g));
    }

    @Property
    public void trinomialBinomial(final int f_a, final int f_b, final int f_c, final int g_a,
        final int g_b) {
      final List<Integer> f = Arrays.asList(f_a, f_b, f_c);
      final List<Integer> g = new Binomial(g_a, g_b);
      Assert.assertEquals(
          Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b, g_a * f_c + f_b * g_b, g_b * f_c),
          PolyMultP1.polyMult(f, g));
    }

    @Property
    public void trinomial(final int f_a, final int f_b, final int f_c, final int g_a, final int g_b,
        final int g_c) {
      final List<Integer> f = Arrays.asList(f_a, f_b, f_c);
      final List<Integer> g = Arrays.asList(g_a, g_b, g_c);
      final List<Integer> prod = Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b,
          f_a * g_c + g_a * f_c + f_b * g_b, f_b * g_c + g_b * f_c, f_c * g_c);
      Assert.assertEquals(prod, PolyMultP1.polyMult(f, g));
    }
  }

  @RunWith(JUnitQuickcheck.class)
  public static class SpacedNumT {
    @Property
    public void positive(@InRange(min = "0") final int n) {
      final StringBuilder sb = new StringBuilder();
      PolyMultP1.spacedNum(sb, n);
      Assert.assertEquals("+ " + Integer.toString(n), sb.toString());
    }

    @Property
    public void negative(@InRange(min = "0") final int n) {
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
    public void mapping(final @Binom Binomial binom) {
      Assert.assertEquals(binom.constant, (int) binom.get(0));
      Assert.assertEquals(binom.coefficient, (int) binom.get(1));
    }

    @Property
    public void modification(final @Binom Binomial binom, final int constant,
        final int coefficient) {
      binom.set(0, constant);
      binom.set(1, coefficient);
      Assert.assertEquals(constant, binom.constant);
      Assert.assertEquals(coefficient, binom.coefficient);
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
      Assert.assertEquals(binom.constant, (int) iter.next());
      Assert.assertTrue(iter.hasNext());
      Assert.assertEquals(binom.coefficient, (int) iter.next());
      Assert.assertFalse(iter.hasNext());
    }
  }
}
