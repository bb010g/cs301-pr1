package edu.cwu.cs301.bb010g.pr1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

// TODO JavaDoc
public class PolyMultP1 {
  public static void main(final String[] args) {
    try (final FileInputStream fis = new FileInputStream("binomials.txt");
        final InputStreamReader isr = new InputStreamReader(fis);
        final BufferedReader br = new BufferedReader(isr);) {

      final List<Integer> product = br.lines().filter(s -> !s.isEmpty())
          .map(str -> PolyMultP1.parseBinomialDec(str.toCharArray()))
          .reduce(Collections.singletonList(1), PolyMultP1::polyMult);

      System.out.println(PolyMultP1.formatPolyDec(product));

    } catch (final FileNotFoundException e) {
      System.out.println("File binomials.txt is missing.");
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  // TODO JavaDoc (make sure to explain algorithm)
  public static List<Integer> polyMult(final List<Integer> f, final List<Integer> g) {
    final int fDegree = f.size() - 1;
    final int gDegree = g.size() - 1;
    final int hDegree = fDegree + gDegree;
    final List<Integer> h = new ArrayList<>(hDegree + 1);
    
    for (int i = 0; i <= hDegree; i++) {
      // additive identity
      int c = 0;
      for (int j = 0; j <= i; j++) {
        final int a;
        if (j <= fDegree) {
          a = f.get(j);
        } else {
          // j only grows, so break now
          break;
        }
        final int b;
        if (i - j <= gDegree) {
          b = g.get(i - j);
        } else {
          // i - j only shrinks, so keep going, but skip to the good bits
          // starts up again when i - j == gDegree === i - gDegree == j
          j = i - gDegree - 1;
          continue;
        }
        c += a * b;
      }
      h.add(i, c);
    }
    return h;
  }

  // TODO JavaDoc
  public static List<Integer> foldPolyMult(final Iterator<List<Integer>> funs) {
    if (!funs.hasNext()) {
      return Collections.singletonList(1);
    }
    List<Integer> product = funs.next();
    while (funs.hasNext()) {
      product = PolyMultP1.polyMult(product, funs.next());
    }
    return product;
  }

  // TODO JavaDoc
  public static String formatPolyInc(final List<Integer> poly) {
    final int polySize = poly.size();
    final StringBuilder sb = new StringBuilder();

    if (polySize == 0) {
      return " 0";
    }

    if (polySize > 0) {
      sb.append(String.format("% d", poly.get(0)));
    }
    if (polySize > 1) {
      sb.append(' ');
      spacedNum(sb, poly.get(1));
      sb.append('x');
    }
    for (int i = 2; i < poly.size(); i++) {
      sb.append(' ');
      spacedNum(sb, poly.get(i));
      sb.append("x^");
      sb.append(i - 1);
    }
    return sb.toString();
  }

  // TODO JavaDoc
  public static String formatPolyDec(final List<Integer> poly) {
    final int polySize = poly.size();
    final StringBuilder sb = new StringBuilder();

    if (polySize == 0) {
      return " 0";
    } else if (polySize == 1) {
      return String.format("% d", poly.get(0));
    } else if (polySize == 2) {
      sb.append(String.format("% dx ", poly.get(1)));
      spacedNum(sb, poly.get(0));
      return sb.toString();
    }

    sb.append(String.format("% dx^%d", poly.get(polySize - 1), polySize - 1));
    for (int i = polySize - 2; i >= 2; i--) {
      sb.append(' ');
      spacedNum(sb, poly.get(i));
      sb.append("x^");
      sb.append(i);
    }
    sb.append(' ');
    spacedNum(sb, poly.get(1));
    sb.append("x ");
    spacedNum(sb, poly.get(0));
    return sb.toString();
  }

  public static void spacedNum(final StringBuilder sb, final int n) {
    sb.append(n > 0 ? '+' : '-');
    sb.append(' ');
    sb.append(Math.abs(n));
  }

  // "Premature optimization is the root of all evil." - Donald Knuth
  // That being said, this was quite fun to write. (Except for the iterators.)
  
  // TODO JavaDoc (class & unique methods)
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

  private static enum BinomialDecParseState {
    SIGN, COEFFICIENT, OPERATOR, CONSTANT
  }

  // TODO JavaDoc
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
              coefficient = Integer.parseUnsignedInt(numBuilder.toString());
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
    constant = Integer.parseUnsignedInt(numBuilder.toString());
    if (negNum) {
      constant *= -1;
    }

    return new Binomial(constant, coefficient);
  }

  // Once more, with feeling!

  private static enum PolyIncParseState {
    SIGN, CONSTANT, COEFFICIENT, EXPONENT, OPERATOR
  }

  // TODO JavaDoc
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
              int constant = Integer.parseUnsignedInt(numBuilder.toString());
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
              int coeff = Integer.parseUnsignedInt(numBuilder.toString());
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
