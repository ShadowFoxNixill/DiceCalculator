package net.nixill.dice.operations;

import java.util.ArrayList;
import java.util.function.Function;

import net.nixill.dice.defaults.functions.DieCoinFunctions;
import net.nixill.dice.exception.DiceCalcException;
import net.nixill.dice.exception.NoSuchFunctionException;
import net.nixill.dice.objects.DCEntity;

/**
 * A class that provides access to save and load {@link Function}s.
 */
// TODO rewrite this
public class SavedFunctions {
  private static FunctionLoader                              loader;
  private static ThreadLocal<ArrayList<ArrayList<DCEntity>>> params;
  
  public static void setLoader(FunctionLoader loader) {
    SavedFunctions.loader = loader;
  }
  
  private static ArrayList<ArrayList<DCEntity>> getStack() {
    return params.get();
  }
  
  public static void stackParams(ArrayList<DCEntity> pars) {
    getStack().add(0, pars);
  }
  
  public static void unstackParams() {
    ArrayList<ArrayList<DCEntity>> stack = getStack();
    if (stack.size() > 0) {
      stack.remove(0);
    }
  }
  
  public static DCEntity get(String name) {
    if (name.equals("!d")) {
      return DieCoinFunctions.DIE;
    } else if (name.equals("!c")) {
      return DieCoinFunctions.COIN;
    }
    
    try {
      int i = Integer.parseInt(name) - 1;
      ArrayList<DCEntity> pars = params.get().get(0);
      if (pars.size() > i) {
        return pars.get(i);
      } else {
        throw new DiceCalcException(new IndexOutOfBoundsException(
            "The current function doesn't have " + (i + 1)
                + " parameters."));
      }
    } catch (NumberFormatException ex) {
      return loader.load(name);
    }
  }
  
  public static void save(String name, DCEntity ent) {
    try {
      throw new NoSuchFunctionException(
          "You can't overwrite parameters in a function.");
    } catch (NumberFormatException ex) {
      loader.save(name, ent);
    }
  }
  
  public static void unsave(String name) {
    try {
      // make sure it's not a param
    } catch (NumberFormatException ex) {
      loader.unsave(name);
    }
  }
}