package edu.cwu.cs301.bb010g.pr1;

/**
 * A stupid tuple class because Java seems to think you should always write your own for these sort
 * of situations. Well yeah, I could write my own hashmap every time I needed to make a phonebook
 * class too. But we're programmers and like to be lazy. Maybe I just want to stick two things
 * together, okay?
 */
public class Pair<A, B> {
  public A fst;
  public B snd;

  public Pair(final A fst, final B snd) {
    this.fst = fst;
    this.snd = snd;
  }

  public static <A, B> Pair<A, B> of(final A fst, final B snd) {
    return new Pair<>(fst, snd);
  }

  @Override
  public String toString() {
    return "Pair[" + this.fst + ", " + this.snd + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.fst == null) ? 0 : this.fst.hashCode());
    result = prime * result + ((this.snd == null) ? 0 : this.snd.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Pair<?, ?> other = (Pair<?, ?>) obj;
    if (this.fst == null) {
      if (other.fst != null) {
        return false;
      }
    } else if (!this.fst.equals(other.fst)) {
      return false;
    }
    if (this.snd == null) {
      if (other.snd != null) {
        return false;
      }
    } else if (!this.snd.equals(other.snd)) {
      return false;
    }
    return true;
  }
}
