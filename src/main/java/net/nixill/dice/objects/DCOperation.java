package net.nixill.dice.objects;

import net.nixill.dice.operations.BinaryOperator;
import net.nixill.dice.operations.FunctionHistory;
import net.nixill.dice.operations.FunctionHistory.HistoryEntry;
import net.nixill.dice.operations.Operator;
import net.nixill.dice.operations.PostfixOperator;
import net.nixill.dice.operations.PrefixOperator;

/**
 * Represents a DCOperation; an expression with one or two operands and an
 * operator.
 */
public class DCOperation extends DCExpression {
  private DCEntity left;
  private DCEntity right;
  private Operator oper;
  
  /**
   * Creates a new operator expression. The order of entities and operator
   * should match how it's typed, with the ignored side of a unary operator
   * receiving a null value. For example:
   * 
   * <pre>
   *typed  left  oper  right
   *2+3    2     +     3
   *-5     null  -     5
   *4!     4     !     null
   * </pre>
   * 
   * @param left
   *   The left side of the operator (null for prefix operators)
   * @param oper
   *   The operator itself
   * @param right
   *   The right side of the operator (null for postfix operators)
   */
  public DCOperation(DCEntity left, Operator oper, DCEntity right) {
    this.left = left;
    this.oper = oper;
    this.right = right;
  }
  
  /**
   * Returns the left operand of the operator expression.
   * 
   * @return The left operand
   */
  public DCEntity getLeft() {
    return left;
  }
  
  /**
   * Returns the right operand of the operator expression.
   * 
   * @return The right operand
   */
  public DCEntity getRight() {
    return right;
  }
  
  /**
   * Returns the operator of the expression.
   * 
   * @return The operator
   */
  public Operator getOperator() {
    return oper;
  }
  
  @Override
  public DCValue getValue() {
    DCValue val = null;
    
    if (oper instanceof BinaryOperator) {
      val = ((BinaryOperator<?>) oper).run(left, right);
    } else if (oper instanceof PrefixOperator) {
      val = ((PrefixOperator<?>) oper).run(right);
    } else if (oper instanceof PostfixOperator) {
      val = ((PostfixOperator<?>) oper).run(left);
    }
    
    String code = toCode();
    String out = val.toCode();
    
    if (!code.equals(out)) {
      FunctionHistory.add(new HistoryEntry(oper.getLevel(),
          toCode() + " => " + val.toString()));
    }
    
    return val;
  }
  
  @Override
  public String toString(int level) {
    if (oper instanceof BinaryOperator) {
      return "(" + left.toString(level - 1) + oper.getSymbol()
          + right.toString(level - 1) + ")";
    } else if (oper instanceof PrefixOperator) {
      return "(" + oper.getSymbol() + right.toString(level - 1) + ")";
    } else if (oper instanceof PostfixOperator) {
      return "(" + left.toString(level - 1) + oper.getSymbol() + ")";
    } else {
      return "";
    }
  }
  
  @Override
  public String toCode() {
    if (oper instanceof BinaryOperator) {
      return "(" + left.toCode() + oper.getSymbol() + right.toCode() + ")";
    } else if (oper instanceof PrefixOperator) {
      return "(" + oper.getSymbol() + right.toCode() + ")";
    } else if (oper instanceof PostfixOperator) {
      return "(" + left.toCode() + oper.getSymbol() + ")";
    } else {
      return "";
    }
  }
  
  @Override
  public void printTree(int level) {
    printSpaced(level, "Operator \"" + oper.getSymbol() + "\"");
    if (left != null) {
      left.printTree(level + 1);
    }
    if (right != null) {
      right.printTree(level + 1);
    }
  }
}