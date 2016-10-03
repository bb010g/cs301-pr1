package edu.cwu.cs301.bb010g.pr1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PolyMultP1 {
  public static void main(String[] args) {
    System.out
        .println(Stream.<List<Integer>>builder().add(Arrays.asList(5, 3)).add(Arrays.asList(-9, -8))
            .add(Arrays.asList(3, 2, 5)).build().reduce(Arrays.asList(1), PolyMultP1::polyMult));
  }

  public static List<Integer> polyMult(List<Integer> f, List<Integer> g) {
    final int fDegree = f.size() - 1;
    final int gDegree = g.size() - 1;
    final int hDegree = fDegree + gDegree;
    final List<Integer> h = new ArrayList<>(hDegree + 1);
    for (int i = 0; i <= hDegree; i++) {
      // additive identity
      int c = 0;
      for (int j = 0; j <= i; j++) {
        c += ((j <= fDegree) ? f.get(j) : 0) * (((i - j) <= gDegree) ? g.get(i - j) : 0);
      }
      h.add(i, c);
    }
    return h;
  }
}
