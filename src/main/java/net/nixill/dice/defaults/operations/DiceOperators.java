package net.nixill.dice.defaults.operations;

import java.rmi.server.Operation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.nixill.NixMath;
import net.nixill.dice.exception.DiceCalcException;
import net.nixill.dice.objects.DCDie;
import net.nixill.dice.objects.DCEntity;
import net.nixill.dice.objects.DCList;
import net.nixill.dice.objects.DCNumber;
import net.nixill.dice.objects.DCValue;
import net.nixill.dice.objects.Randomizer;
import net.nixill.dice.operations.BinaryOperator;
import net.nixill.dice.operations.ComparisonOperators;
import net.nixill.dice.operations.ComparisonOperators.Comparison;
import net.nixill.dice.operations.Functions;
import net.nixill.dice.operations.OperationLimits;
import net.nixill.dice.operations.PrefixOperator;

/**
 * Implementation of the default operators for rolling dice.
 */
public class DiceOperators {
  /**
   * The binary "d" operator, which rolls a set number of dice.
   * <ul>
   * <li>left operand - number: The number of dice to roll.</li>
   * <li>right operand - number: The number of sides for each die.</li>
   * <li>Returns - list: The list of rolled dice.</li>
   * </ul>
   */
  public static final BinaryOperator<DCList> DICE = new BinaryOperator<>(
      "d", Priorities.DICE, 1, (left, right) -> {
        double count = Math.floor(left.getValue().getSingle().getAmount());
        double sides = Math
            .floor(right.getValue().getSingle().getAmount());
        
        if (count < 1) {
          throw new DiceCalcException(new IllegalArgumentException(
              "You must roll at least one die."));
        } else if (count > OperationLimits.getLimit()) {
          throw new DiceCalcException(new IllegalArgumentException(
              OperationLimits.getLimitMessage()));
        }
        
        if (sides < 1) {
          throw new DiceCalcException(new IllegalArgumentException(
              "Dice must have at least one side."));
        }
        
        ArrayList<DCValue> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
          out.add(new DCDie(sides));
        }
        
        return new DCList(out);
      });
  
  /**
   * The prefix "d" operator, which rolls a single die.
   * <ul>
   * <li>operand - number: The number of sides for the die.</li>
   * <li>Returns - list: The list of rolled dice.</li>
   * </ul>
   */
  public static final PrefixOperator<DCDie> ONE_DIE = new PrefixOperator<>(
      "d", Priorities.DICE, 1, (ent) -> {
        double sides = Math.floor(ent.getValue().getSingle().getAmount());
        
        if (sides < 1) {
          throw new DiceCalcException(new IllegalArgumentException(
              "Dice must have at least one side."));
        }
        
        if (OperationLimits.getLimit() == 0) {
          throw new DiceCalcException(new IllegalArgumentException(
              OperationLimits.getLimitMessage()));
        }
        
        return new DCDie(sides);
      });
  
  /**
   * The set of comparison-based "u" operators, which rolls dice until one
   * matches the comparison.
   * <p>
   * The die that ends the streak will not be included in the list, but
   * saved to the variable <code>{_u}</code>.
   * <ul>
   * <li>left operand - number: The number of sides for each die.</li>
   * <li>right operand - comparison: The comparison which, when a die is
   * rolled that satisfies it, ends the rolling streak.</li>
   * <li>Returns - list: The list of rolled dice.</li>
   * </ul>
   */
  public static final ComparisonOperators<DCList> ROLL_UNTIL = new ComparisonOperators<>(
      "u", Priorities.DICE, 1, (left, comp, right) -> {
        double sides = Math.floor(left.getValue().getSingle().getAmount());
        double cutoff = right.getValue().getSingle().getAmount();
        
        Functions.save2("_u", null);
        
        boolean error = false;
        if (sides < 1) {
          throw new DiceCalcException(new IllegalArgumentException(
              "Dice must have at least one side."));
        } else if (sides == 1) {
          if (comp == Comparison.MODULO || comp == Comparison.NOT_MODULO) {
            // Don't allow modulo for decimal dice; modulo requires a
            // precision too exact to be practical
            throw new DiceCalcException(new IllegalArgumentException(
                "Can't roll-until an exact multiple of a decimal."));
          } else if (cutoff < 0 || cutoff >= 1) {
            // The cutoff range is entirely outside the range of possible
            // values
            // It's always an error because
            // - >=low, >low, !=low, <=high, <high, !=high - match
            // everything
            // - <=low, <low, =low, >=high, >high, =high - match nothing
            error = true;
          } else if (cutoff == 0) {
            // The cutoff range is exactly the edge of a possible value
            // Only an error if the comparison is
            // - (value) >= 1 (matches everything)
            // - (value) < 1 (matches nothing)
            // - (value) <= max (matches everything)
            // - (value) > max (matches nothing)
            error = (comp.compares(0, cutoff) == comp
                .compares(Math.nextDown(1), cutoff));
          }
        } else {
          
          // The conditions under which this operator shouldn't work are as
          // follows:
          if (cutoff < 1 || cutoff > sides) {
            // The cutoff range is entirely outside the range of possible
            // values
            // It's always an error because
            // - >=low, >low, !=low, <=high, <high, !=high - match
            // everything
            // - <=low, <low, =low, >=high, >high, =high - match nothing
            error = true;
          } else if (cutoff == 1 || cutoff == sides) {
            // The cutoff range is exactly the edge of a possible value
            // Only an error if the comparison is
            // - (value) >= 1 (matches everything)
            // - (value) < 1 (matches nothing)
            // - (value) <= max (matches everything)
            // - (value) > max (matches nothing)
            error = (comp.compares(1, cutoff) == comp.compares(sides,
                cutoff));
          } else if (cutoff == Math.floor(cutoff)) {
            // If the cutoff value is an int within the range of possible
            // values, then do NOT error.
          } else if (comp == Comparison.MODULO
              || comp == Comparison.NOT_MODULO) {
            // For modulo-based comparisons, if the cutoff isn't an int,
            // multiply it until it is since we only make ints anyway.
            // For example, the multiples of 1.5 (3/2) that a d6 can make
            // are
            // just 3 and 6 - the multiples of 3.
            cutoff = NixMath.float2num(cutoff);
            if (cutoff > sides) {
              error = true;
            }
          }
          // For a not-modulo-based comparison, a decimal is fine; the only
          // thing about it is that there's no difference between >= and >.
        }
        
        if (error) {
          throw new DiceCalcException(new IllegalArgumentException(
              "The range you have selected would result in a pointless roll."));
        }
        
        ArrayList<DCValue> out = new ArrayList<>();
        
        int limit = OperationLimits.getLimit();
        
        if (limit == 0) {
          throw new DiceCalcException(new IllegalArgumentException(
              OperationLimits.getLimitMessage()));
        }
        
        for (int i = 0; i < limit; i++) {
          DCDie die = new DCDie(sides);
          if (comp.compares(die.getAmount(), cutoff)) {
            Functions.save2("_u", die);
            break;
          } else {
            out.add(die);
          }
        }
        
        return new DCList(out);
      });
  
  public static final BinaryOperator<DCList> PICK = new BinaryOperator<>(
      "p", Priorities.PICK, 1, (left, right) -> {
        return PICK_FUNC(left, right, false);
      });
  
  public static final BinaryOperator<DCList> PICK_REPLACE = new BinaryOperator<>(
      "pr", Priorities.PICK, 1, (left, right) -> {
        return PICK_FUNC(left, right, true);
      });
  
  public static final PrefixOperator<DCValue> PICK_ONE = new PrefixOperator<>(
      "p", Priorities.PICK, 1, (left) -> {
        return PICK_FUNC(left, new DCNumber(1), true).get(0);
      });
  
  public static DCList PICK_FUNC(DCEntity left, DCEntity right,
      boolean replace) {
    
    ArrayList<DCValue> vals = right.getValue().getList().getItems();
    int count = (int) (left.getValue().getSingle().getAmount());
    
    Random rand = Randomizer.get();
    
    ArrayList<DCValue> out = new ArrayList<>();
    
    if (replace) {
      while (out.size() < count) {
        DCValue picked = vals.get(rand.nextInt(vals.size()));
        out.add(picked);
      }
    } else {
      while (out.size() < count) {
        if (out.size() + vals.size() <= count) {
          Collections.shuffle(vals, rand);
          out.addAll(vals);
          
          if (out.size() == count) {
            vals.clear();
          }
        } else {
          for (int i = out.size(); i < count; i++) {
            out.add(vals.remove(rand.nextInt(vals.size())));
          }
        }
      }
      
      Functions.save2("_p", new DCList(vals));
    }
    
    return new DCList(out);
  }
}