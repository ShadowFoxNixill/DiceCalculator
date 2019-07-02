package net.nixill.dice.operations.defaults;

import java.util.ArrayList;

import net.nixill.dice.objects.DCEntity;
import net.nixill.dice.objects.DCList;
import net.nixill.dice.objects.DCNumber;
import net.nixill.dice.objects.DCSingle;
import net.nixill.dice.objects.DCValue;
import net.nixill.dice.operations.BinaryOperator;
import net.nixill.dice.operations.PrefixOperator;

public class ListOperators {
  /**
   * The binary "+" operator, which joins two lists.
   * <ul>
   * <li><code>left</code> operand - list: A starting list</li>
   * <li><code>right</code> operand - list: The list to join to
   * <code>left</code>.</li>
   * <li>Returns - list: A list formed by joining <code>left</code> and
   * <code>right</code>.</li>
   * </ul>
   */
  public static final BinaryOperator<DCList> JOIN = new BinaryOperator<>(
      "+", Priorities.JOIN, ListOperators::joinOp);
  
  public static DCList joinOp(DCEntity leftEnt, DCEntity rightEnt) {
    ArrayList<DCValue> combined;
    DCValue left = leftEnt.getValue();
    DCValue right = rightEnt.getValue();
    
    if (left instanceof DCSingle) {
      combined = new ArrayList<>();
      combined.add(left);
    } else {
      combined = left.getList().getItems();
    }
    
    if (right instanceof DCSingle) {
      combined.add(right);
    } else {
      combined.addAll(right.getList().getItems());
    }
    
    return new DCList(combined);
  }
  
  /**
   * The prefix "-" operator, which makes its operand negative.
   * <p>
   * Lists are made negative by making every item they contain negative.
   * <ul>
   * <li>operand - list: The list to make negative.</li>
   * <li>Returns - list: That list, negative.</li>
   * </ul>
   */
  public static final PrefixOperator<DCList> NEGATIVE = new PrefixOperator<>(
      "-", Priorities.NEGATIVE, ListOperators::negativeOp);
  
  public static DCList negativeOp(DCEntity ent) {
    ArrayList<DCValue> in;
    DCValue val = ent.getValue();
    
    if (val instanceof DCSingle) {
      in = new ArrayList<>();
      in.add(val);
    } else {
      in = val.getList().getItems();
    }
    
    ArrayList<DCValue> out = new ArrayList<>();
    
    for (DCValue inVal : in) {
      if (inVal instanceof DCSingle) {
        DCSingle inSingle = (DCSingle) inVal;
        out.add(
            new DCNumber(-inSingle.getAmount(), inSingle.getPotential()));
      } else {
        out.add(negativeOp(inVal));
      }
    }
    
    return new DCList(out);
  }
  
  /**
   * The bianry "-" operator, which subtracts two numbers or joins a list
   * to a negative list.
   * <p>
   * This works the same way as {@link #JOIN}, except that the right-hand
   * operand is made {@link #NEGATIVE} first.
   * <ul>
   * <li><code>left</code> operand - list: A starting list</li>
   * <li><code>right</code> operand - list: The list to subtract from
   * <code>left</code>.</li>
   * <li>Returns - list: A list formed by joining <code>left</code> to
   * negative <code>right</code>.</li>
   * </ul>
   */
  public static final BinaryOperator<DCList> NEG_JOIN = new BinaryOperator<>(
      "-", Priorities.JOIN, (left, right) -> {
        return joinOp(left, negativeOp(right));
      });
  
  /**
   * The prefix "#" operator, which returns the length of the list.
   * <p>
   * Sub-lists are only counted as a single item in this list.
   * <ul>
   * <li>Operand - list: A list</li>
   * <li>Returns - number: The length of the list</li>
   * </ul>
   */
  public static final PrefixOperator<DCNumber> SIZE = new PrefixOperator<>(
      "#", Priorities.LIST, (ent) -> {
        DCValue val = ent.getValue();
        if (val instanceof DCSingle) {
          return new DCNumber(1);
        } else {
          return new DCNumber(val.getList().getItems().size());
        }
      });
  
  /**
   * The prefix "$" operator, which returns the sum of the list.
   * <ul>
   * <li>Operand - list: A list</li>
   * <li>Returns - number: The sum of all the items in the list</li>
   * </ul>
   */
  public static final PrefixOperator<DCSingle> SUM = new PrefixOperator<>(
      "$", Priorities.LIST, (ent) -> {
        return ent.getValue().getSingle();
      });
  
  /**
       * The postfix "?" operator, which shuffles the list.
       * <ul>
       * <li>Operand - list: A list</li>
       * <li>Returns - list: The same list with its items in random order</li>
       * </ul>
       */
      public static final PostfixOperator<DCList> SHUFFLE = new PrefixOperator<>("?", Priorities.LIST, (ent) -> {
        DCList list = ent.getValue().getList();

        
      })
}