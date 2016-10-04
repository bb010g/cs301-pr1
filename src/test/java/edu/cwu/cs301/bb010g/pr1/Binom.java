package edu.cwu.cs301.bb010g.pr1;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.pholser.junit.quickcheck.From;

@Retention(RUNTIME)
@Target({FIELD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@From(BinomialGenerator.class)
public @interface Binom {
}
