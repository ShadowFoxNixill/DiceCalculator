package net.nixill.dice.operations;

import java.util.function.Function;

import net.nixill.dice.objects.DCValue;

public class PostfixOperator<R extends DCValue> extends UnaryOperator<R> {
  public PostfixOperator(String symbol, int priority, Function<DCValue, R> func) {
    super(symbol, priority, true, func);
  }
}