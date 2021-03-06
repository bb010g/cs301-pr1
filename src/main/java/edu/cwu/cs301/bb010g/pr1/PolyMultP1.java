package edu.cwu.cs301.bb010g.pr1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.google.common.io.Files;

/**
 * A polynomial represented by a {@link List<Integer>} in this class is expressed as a list of
 * {@link Integer}s of increasing degree (starting with the constant term).
 */
public class PolyMultP1 {
  public static void main(final String[] args) {
    main(Arrays.asList(args));
  }

  public static void main(final List<String> args) {
    try (final BufferedReader br =
        Files.newReader(new File("binomials.txt"), StandardCharsets.UTF_8);) {

      final List<Integer> product = PolyMultP1.readPolyMult(br);
      System.out.println(PolyMultP1.formatPolyDec(product));

    } catch (final FileNotFoundException e) {
      System.err.println("File binomials.txt is missing.");
    } catch (final IOException e) {
      // Don't really care about generic IO problems
      e.printStackTrace();
    }
  }

  /**
   * Multiplies a series of binomials separated by line breaks.
   *
   * @param br The source of the binomials.
   * @return The polynomial product.
   * @throws IOException Reading error.
   */
  public static List<Integer> readPolyMult(final BufferedReader br) throws IOException {
    final Deque<List<Integer>> polys = new ArrayDeque<>();
    // foldBuildups makes sure that we're doing smaller, cheaper multiplications
    // along the way. Each level of folding has its own amount of buildup
    // before it reaches the threshold of 2 and is folded and cleaned up.
    final List<Integer> foldQueue = new ArrayList<>();
    // Start out the process with zero binomials waiting in the queue.
    foldQueue.add(0);

    while (true) {
      final String line = br.readLine();
      if (line == null || line.isEmpty()) {
        break;
      }

      // Read in a binomial and mark it as a part of the queue
      polys.push(PolyMultP1.prunePoly(PolyMultP1.parseBinomialDec(line.toCharArray())));
      foldQueue.set(0, foldQueue.get(0) + 1);

      // Fold polynomials of a certain degree if their queue is large enough to
      // trigger the threshold of 2.
      int fqi;
      while ((fqi = foldQueue.indexOf(2)) != -1) {
        // Smaller polynomials will always be at the front of the queue due to
        // them being the most recently read and the folding process always
        // dealing with the same degree, ensuring the polynomials behind its
        // intermediary product are of the same degree or larger.
        final List<Integer> poly1 = polys.pop(), poly2 = polys.pop();
        /*
         * System.out.println("Folding (" + fti + ") (" + PolyMultP1.formatPolyInc(poly1) + ") * ("
         * * + PolyMultP1.formatPolyInc(poly2) + ")");
         */
        polys.push(PolyMultP1.polyMult(poly1, poly2));
        // Mark that the queue for this degree has been cleared and the next
        // degree has a new member.
        foldQueue.set(fqi, 0);
        if (foldQueue.size() == fqi + 1) {
          foldQueue.add(fqi + 1, 1);
        } else {
          foldQueue.set(fqi + 1, foldQueue.get(fqi + 1) + 1);
        }
      }
    }

    if (polys.isEmpty()) {
      // There were no lines with polynomials, so use the multiplicative identity.
      polys.push(Collections.singletonList(1));
    } else {
      while (polys.size() > 1) {
        // Clean up the remaining polynomials that didn't trigger a folding previously.
        final List<Integer> poly1 = polys.pop(), poly2 = polys.pop();
        /*
         * System.out.println("Folding (end) (" + PolyMultP1.formatPolyInc(poly1) + ") * (" +
         * PolyMultP1.formatPolyInc(poly2) + ")");
         */
        polys.push(PolyMultP1.polyMult(poly1, poly2));
      }
    }
    // The product is the only polynomial remaining.
    return polys.pop();
  }

  // Assume that we want to optimize
  public static List<Integer> polyMult(final List<Integer> f, final List<Integer> g) {
    return PolyMultP1.polyMult(f, g, true);
  }

  /**
   * Multiply two polynomials.
   * 
   * @param f A factor polynomial.
   * @param g A factor polynomial.
   * @param optimize Whether to use the constant time optimizations for multiplications involving
   *        smaller degrees.
   * @return The product polynomial.
   */
  public static List<Integer> polyMult(List<Integer> f, List<Integer> g, final boolean optimize) {
    int fDegree = f.size() - 1;
    int gDegree = g.size() - 1;

    // ensure f has a smaller degree if possible
    if (gDegree < fDegree) {
      final List<Integer> swap = f;
      final int swapDegree = fDegree;
      f = g;
      g = swap;
      fDegree = gDegree;
      gDegree = swapDegree;
    }

    if (optimize) {
      // Avoid expensive looping if possible.
      if (fDegree == -1) {
        // 0 * x = 0
        return Collections.emptyList();
      }
      if (fDegree == 0) {
        // scalar multiplication * n-omial = n-omial
        final int f_a = f.get(0);
        final List<Integer> res = new ArrayList<>(g.size());
        for (final int n : g) {
          res.add(f_a * n);
        }
        return res;
      }
      if (fDegree == 1 && gDegree == 1) {
        // binomial * binomial = trinomial
        final int f_a = f.get(0), f_b = f.get(1);
        final int g_a = g.get(0), g_b = g.get(1);
        return Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b, f_b * g_b);
      }
      if (fDegree == 1 && gDegree == 2) {
        // binomial * trinomial = 4-term
        final int f_a = f.get(0), f_b = f.get(1);
        final int g_a = g.get(0), g_b = g.get(1), g_c = g.get(2);
        return Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b, f_a * g_c + f_b * g_b, f_b * g_c);
      }
      if (fDegree == 2 && gDegree == 2) {
        // trinomial * trinomial = 5-term
        final int f_a = f.get(0), f_b = f.get(1), f_c = f.get(2);
        final int g_a = g.get(0), g_b = g.get(1), g_c = g.get(2);
        return Arrays.asList(f_a * g_a, f_a * g_b + g_a * f_b, f_a * g_c + g_a * f_c + f_b * g_b,
            f_b * g_c + g_b * f_c, f_c * g_c);
      }
    }
    // System.out.println("Unoptimized");

    final int hDegree = fDegree + gDegree;
    final List<Integer> h = new ArrayList<>(hDegree + 1);

    for (int termDegree = 0; termDegree <= hDegree; termDegree++) {
      // additive identity
      int sum = 0;
      for (int focusedTerm = 0; focusedTerm <= termDegree; focusedTerm++) {
        final int f_n;
        if (focusedTerm <= fDegree) {
          f_n = f.get(focusedTerm);
        } else {
          // focusedTerm only grows, so break now
          break;
        }
        final int g_n;
        if (termDegree - focusedTerm <= gDegree) {
          g_n = g.get(termDegree - focusedTerm);
        } else {
          // termDegree - focusedTerm only shrinks, so keep going, but skip to the good bits
          // starts up again when
          // termDegree - focusedTerm == gDegree ===
          // termDegree - gDegree == focusedTerm
          focusedTerm = termDegree - gDegree - 1;
          continue; // adds the missing 1 to focusedTerm
        }
        sum += f_n * g_n;
      }
      h.add(termDegree, sum);
    }
    return h;
  }


  /**
   * Compute the product of a series of polynomials.
   *
   * @param polys A series of polynomials.
   * @return Their product.
   */
  public static List<Integer> foldPolyMult(final Iterator<List<Integer>> polys) {
    if (!polys.hasNext()) {
      return Collections.singletonList(1);
    }
    List<Integer> product = polys.next();
    while (polys.hasNext()) {
      product = PolyMultP1.polyMult(product, polys.next());
    }
    return product;
  }

  /**
   * Pretty-print a polynomial with the terms ordered by increasing degree.
   * 
   * @param poly The polynomial to be printed.
   * @return The pretty-printing.
   */
  public static String formatPolyInc(final List<Integer> poly) {
    final int polySize = poly.size();
    if (polySize == 0) {
      return " 0";
    }
    final StringBuilder sb = new StringBuilder();

    sb.append(String.format("% d", poly.get(0)));
    if (polySize > 1) {
      sb.append(' ');
      PolyMultP1.spacedNum(sb, poly.get(1));
      sb.append('x');
    }
    for (int i = 2; i < poly.size(); i++) {
      sb.append(' ');
      PolyMultP1.spacedNum(sb, poly.get(i));
      sb.append("x^");
      sb.append(i);
    }
    return sb.toString();
  }

  /**
   * Pretty-print a polynomial with the terms ordered by decreasing degree.
   * 
   * @param poly The polynomial to be printed.
   * @return The pretty-printing.
   */
  public static String formatPolyDec(final List<Integer> poly) {
    final int polySize = poly.size();
    final StringBuilder sb = new StringBuilder();

    switch (polySize) {
      case 0:
        return " 0";
      case 1:
        return String.format("% d", poly.get(0));
      case 2:
        sb.append(String.format("% dx ", poly.get(1)));
        PolyMultP1.spacedNum(sb, poly.get(0));
        return sb.toString();
    }

    sb.append(String.format("% dx^%d", poly.get(polySize - 1), polySize - 1));
    for (int i = polySize - 2; i >= 2; i--) {
      sb.append(' ');
      PolyMultP1.spacedNum(sb, poly.get(i));
      sb.append("x^");
      sb.append(i);
    }
    sb.append(' ');
    PolyMultP1.spacedNum(sb, poly.get(1));
    sb.append("x ");
    PolyMultP1.spacedNum(sb, poly.get(0));
    return sb.toString();
  }

  // Utility method to append a number's sign, space, and then the digits of the
  // number.
  public static void spacedNum(final StringBuilder sb, final int n) {
    sb.append(n < 0 ? '-' : '+');
    sb.append(' ');
    sb.append(Math.abs(n));
  }

  /**
   * Removes trailing zero terms so the size of the List accurately represents the degree of the
   * polynomial represented.
   * 
   * @param poly A polynomial
   * @return A mathematically equivalent polynomial
   */
  public static List<Integer> prunePoly(final List<Integer> poly) {
    int lastActual = poly.size() - 1;
    while (poly.get(lastActual) == 0 && lastActual > 0) {
      lastActual--;
    }
    return poly.subList(0, lastActual + 1);
  }

  // "Premature optimization is the root of all evil." - Donald Knuth
  // That being said, this was quite fun to write. (Except for the iterators.)

  /**
   * A two element tuple that acts like a list. List implementations are annoying. See {@link List}
   * for details on what everything does. Anything that would mutate the size throws
   * {@link UnsupportedOperationException}.
   */
  public static class Binomial implements List<Integer> {
    public int constant;
    public int coefficient;

    public Binomial(final int constant, final int coefficient) {
      this.constant = constant;
      this.coefficient = coefficient;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(String.format("% d", this.constant));
      sb.append(' ');
      sb.append(this.coefficient > 0 ? '+' : '-');
      sb.append(' ');
      sb.append(Math.abs(this.coefficient));
      sb.append('x');
      return sb.toString();
    }

    @Override
    public int size() {
      return 2;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean contains(final Object o) {
      final Integer n = (Integer) o;
      return n == this.coefficient || n == this.constant;
    }

    @Override
    public Iterator<Integer> iterator() {
      return new ListIter();
    }

    @Override
    public Object[] toArray() {
      return new Object[] {this.constant, this.coefficient};
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(final T[] a) {
      return (T[]) this.toArray();
    }

    @Override
    public boolean add(final Integer e) {
      throw new UnsupportedOperationException("Ungrowable binomial");
    }

    @Override
    public boolean remove(final Object o) {
      throw new UnsupportedOperationException("Unshrinkable binomial");
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
      for (final Object o : c) {
        final Integer n = (Integer) o;
        if ((n != this.constant) && (n != this.coefficient)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public boolean addAll(final Collection<? extends Integer> c) {
      throw new UnsupportedOperationException("Ungrowable binomial");
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Integer> c) {
      throw new UnsupportedOperationException("Ungrowable binomial");
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
      throw new UnsupportedOperationException("Unshrinkable binomial");
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
      throw new UnsupportedOperationException("Unshrinkable binomial");
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException("Unshrinkable binomial");
    }

    @Override
    public Integer get(final int index) {
      switch (index) {
        case 0:
          return this.constant;
        case 1:
          return this.coefficient;
        default:
          throw new IndexOutOfBoundsException();
      }
    }

    @Override
    public Integer set(final int index, final Integer element) {
      int previous;
      switch (index) {
        case 0:
          previous = this.constant;
          this.constant = element;
          break;
        case 1:
          previous = this.coefficient;
          this.coefficient = element;
          break;
        default:
          throw new IndexOutOfBoundsException();
      }
      return previous;
    }

    @Override
    public void add(final int index, final Integer element) {
      throw new UnsupportedOperationException("Ungrowable binomial");
    }

    @Override
    public Integer remove(final int index) {
      throw new UnsupportedOperationException("Unshrinkable binomial");
    }

    @Override
    public int indexOf(final Object o) {
      final Integer n = (Integer) o;
      if (n == this.constant) {
        return 0;
      } else if (n == this.coefficient) {
        return 1;
      } else {
        return -1;
      }
    }

    @Override
    public int lastIndexOf(final Object o) {
      final Integer n = (Integer) o;
      if (n == this.coefficient) {
        return 1;
      } else if (n == this.constant) {
        return 0;
      } else {
        return -1;
      }
    }

    private class ListIter implements ListIterator<Integer> {
      // cursor position is right between the index of the same number and the previous index
      private int cursor;

      public ListIter() {
        this(0);
      }

      public ListIter(final int index) {
        this.cursor = 0;
      }

      @Override
      public boolean hasNext() {
        return this.cursor < 2;
      }

      @Override
      public Integer next() {
        if (this.cursor > 1) {
          throw new NoSuchElementException();
        }
        final Integer n = Binomial.this.get(this.cursor++);
        return n;
      }

      @Override
      public boolean hasPrevious() {
        return this.cursor > 0;
      }

      @Override
      public Integer previous() {
        if (this.cursor < 0) {
          throw new NoSuchElementException();
        }
        final Integer n = Binomial.this.get(--this.cursor);
        return n;
      }

      @Override
      public int nextIndex() {
        return this.cursor;
      }

      @Override
      public int previousIndex() {
        return this.cursor - 1;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void set(final Integer e) {
        Binomial.this.set(this.cursor, e);
      }

      @Override
      public void add(final Integer e) {
        throw new UnsupportedOperationException();
      }
    }

    @Override
    public ListIterator<Integer> listIterator() {
      return new ListIter();
    }

    @Override
    public ListIterator<Integer> listIterator(final int index) {
      return new ListIter(index);
    }

    private class SingletonSubList implements List<Integer> {
      private final boolean constant;

      public SingletonSubList(final boolean constant) {
        this.constant = constant;
      }

      private int get() {
        return this.constant ? Binomial.this.constant : Binomial.this.coefficient;
      }

      private int set(final int n) {
        final int previous;
        if (this.constant) {
          previous = Binomial.this.constant;
          Binomial.this.constant = n;
        } else {
          previous = Binomial.this.coefficient;
          Binomial.this.coefficient = n;
        }
        return previous;
      }

      @Override
      public int size() {
        return 1;
      }

      @Override
      public boolean isEmpty() {
        return false;
      }

      @Override
      public boolean contains(final Object o) {
        return (Integer) o == this.get();
      }

      @Override
      public Iterator<Integer> iterator() {
        return new ListIter();
      }

      @Override
      public Object[] toArray() {
        return new Integer[] {this.get()};
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> T[] toArray(final T[] a) {
        return (T[]) this.toArray();
      }

      @Override
      public boolean add(final Integer e) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
          final Integer n = (Integer) o;
          if (n != this.get()) {
            return false;
          }
        }
        return true;
      }

      @Override
      public boolean addAll(final Collection<? extends Integer> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(final int index, final Collection<? extends Integer> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
        throw new UnsupportedOperationException();
      }

      @Override
      public Integer get(final int index) {
        if (index == 0) {
          return this.get();
        }
        throw new IndexOutOfBoundsException();
      }

      @Override
      public Integer set(final int index, final Integer element) {
        if (index == 0) {
          return this.set(element);
        }
        throw new IndexOutOfBoundsException();
      }

      @Override
      public void add(final int index, final Integer element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public Integer remove(final int index) {
        throw new UnsupportedOperationException();
      }

      @Override
      public int indexOf(final Object o) {
        final Integer n = (Integer) o;
        return (n == this.get()) ? 0 : -1;
      }

      @Override
      public int lastIndexOf(final Object o) {
        final Integer n = (Integer) o;
        return (n == this.get()) ? 0 : -1;
      }

      private class ListIter implements ListIterator<Integer> {
        private final boolean fresh;

        public ListIter(final boolean fresh) {
          this.fresh = fresh;
        }

        public ListIter() {
          this(true);
        }

        @Override
        public boolean hasNext() {
          return this.fresh;
        }

        @Override
        public Integer next() {
          if (this.fresh) {
            return SingletonSubList.this.get();
          }
          throw new NoSuchElementException();
        }

        @Override
        public boolean hasPrevious() {
          return !this.fresh;
        }

        @Override
        public Integer previous() {
          if (!this.fresh) {
            return SingletonSubList.this.get();
          }
          throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
          return this.fresh ? 0 : 1;
        }

        @Override
        public int previousIndex() {
          return this.fresh ? -1 : 0;
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void set(final Integer e) {
          SingletonSubList.this.set(e);
        }

        @Override
        public void add(final Integer e) {
          throw new UnsupportedOperationException();
        }

      }

      @Override
      public ListIterator<Integer> listIterator() {
        return new ListIter();
      }

      @Override
      public ListIterator<Integer> listIterator(final int index) {
        switch (index) {
          case 0:
            return new ListIter(true);
          case 1:
            return new ListIter(false);
          default:
            throw new IndexOutOfBoundsException();
        }
      }

      @Override
      public List<Integer> subList(final int fromIndex, final int toIndex) {
        if (fromIndex == toIndex) {
          return Collections.emptyList();
        }
        if (fromIndex == 0 && toIndex == 1) {
          return this;
        }
        throw new IndexOutOfBoundsException();
      }
    }

    @Override
    public List<Integer> subList(final int fromIndex, final int toIndex) {
      if (fromIndex == 0 && toIndex == 2) {
        return this;
      }
      if (fromIndex == toIndex) {
        return Collections.emptyList();
      }
      if (fromIndex == 0 && toIndex == 1) {
        return new SingletonSubList(true);
      }
      if (fromIndex == 1 && toIndex == 2) {
        return new SingletonSubList(false);
      }
      throw new IndexOutOfBoundsException();
    }
  }

  // Representation of the parsing state machine's state
  private static enum BinomialDecParseState {
    SIGN, COEFFICIENT, OPERATOR, CONSTANT
  }

  /**
   * Parses binomials of the first degree of the following form:
   * 
   * The first character shall be a space or a {@code -} if the coefficient is negative. The
   * characters until the {@code x} shall be the digits of the coefficient. The {@code x} shall
   * immediately be followed by a space and the operator for the constant term. The operator for the
   * constant term shall be either {@code +} or {@code -}. After the operator shall follow one space
   * and the digits of the constant term. The digits of the constant term shall not have a sign
   * attached. The sign shall instead be conveyed by the operator. After these digits, the string
   * shall end.
   */
  public static List<Integer> parseBinomialDec(final char[] chars) {
    int constant = 0;
    int coefficient = 0;

    BinomialDecParseState s = BinomialDecParseState.SIGN;
    boolean negNum = false;
    final StringBuilder numBuilder = new StringBuilder();

    for (int i = 0; i < chars.length; i++) {
      final char c = chars[i];
      switch (s) {
        case SIGN:
          if (c == '-') {
            negNum = true;
          }
          s = BinomialDecParseState.COEFFICIENT;
          break;
        case COEFFICIENT:
          switch (c) {
            case 'x':
              coefficient = Integer.parseInt(numBuilder.toString());
              numBuilder.setLength(0); // clear the StringBuilder
              if (negNum) {
                coefficient *= -1;
                negNum = false;
              }
              i++; // skip the space
              s = BinomialDecParseState.OPERATOR;
              break;
            default:
              numBuilder.append(c);
          }
          break;
        case OPERATOR:
          switch (c) {
            case '-':
              negNum = true;
            default:
              i++; // skip the space
              s = BinomialDecParseState.CONSTANT;
              break;
          }
          break;
        case CONSTANT:
          numBuilder.append(c);
          break;
      }
    }
    // ends on a finished numBuilder
    constant = Integer.parseInt(numBuilder.toString());
    if (negNum) {
      constant *= -1;
    }

    return new Binomial(constant, coefficient);
  }

  // Once more, with feeling!

  // Representation of the parsing state machine's state
  private static enum PolyIncParseState {
    SIGN, CONSTANT, OPERATOR, COEFFICIENT, EXPONENT
  }

  // It's a lot like the other one, except it can handle more stuff. This is extra, so read the code
  // to figure out what it can handle.
  public static List<Integer> parsePolyInc(final char[] chars) {
    final List<Integer> poly = new ArrayList<>();

    PolyIncParseState s = PolyIncParseState.SIGN;
    boolean negNum = false;
    final StringBuilder numBuilder = new StringBuilder();

    for (int i = 0; i < chars.length; i++) {
      final char c = chars[i];
      switch (s) {
        case SIGN:
          if (c == '-') {
            negNum = true;
          }
          s = PolyIncParseState.CONSTANT;
          break;
        case CONSTANT:
          switch (c) {
            case ' ':
              int constant = Integer.parseInt(numBuilder.toString());
              numBuilder.setLength(0); // clear the StringBuilder
              if (negNum) {
                constant *= -1;
                negNum = false;
              }
              poly.add(constant);
              s = PolyIncParseState.OPERATOR;
              break;
            default:
              numBuilder.append(c);
          }
          break;
        case OPERATOR:
          switch (c) {
            case '-':
              negNum = true;
            default:
              i++; // skip the space
              s = PolyIncParseState.COEFFICIENT;
              break;
          }
          break;
        case COEFFICIENT:
          switch (c) {
            case 'x':
              int coeff = Integer.parseInt(numBuilder.toString());
              numBuilder.setLength(0); // clear the StringBuilder
              if (negNum) {
                coeff *= -1;
                negNum = false;
              }
              poly.add(coeff);
              s = PolyIncParseState.EXPONENT;
              break;
            default:
              numBuilder.append(c);
          }
          break;
        case EXPONENT:
          if (c == ' ') {
            s = PolyIncParseState.OPERATOR;
          }
          break;
      }
    }

    return poly;
  }
}
