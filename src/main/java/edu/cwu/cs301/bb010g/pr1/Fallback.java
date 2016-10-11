package edu.cwu.cs301.bb010g.pr1;

import java.util.Arrays;

public class Fallback {
  public static void main(final String[] args) {
    if (args.length < 1) {
      System.out.println("Please select a class to run.");
      return;
    }
    final String clazz = args[0].toLowerCase();
    switch (clazz) {
      case "polymult":
        PolyMultP1.main(Arrays.asList(args).subList(1, args.length));
        break;
      case "factor":
        FactorP1.main(Arrays.asList(args).subList(1, args.length));
        break;
    }
  }
}
