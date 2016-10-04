package edu.cwu.cs301.bb010g.pr1;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import edu.cwu.cs301.bb010g.pr1.PolyMultP1.Binomial;

public class BinomialGenerator extends Generator<Binomial> {
  public BinomialGenerator() {
    super(Binomial.class);
  }

  @Override
  public Binomial generate(final SourceOfRandomness random, final GenerationStatus status) {
    final int constant = random.nextInt();
    final int coefficient = random.nextInt();
    return new Binomial(constant, coefficient);
  }
}
