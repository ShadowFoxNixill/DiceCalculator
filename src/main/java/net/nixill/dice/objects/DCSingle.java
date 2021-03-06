package net.nixill.dice.objects;

import java.util.ArrayList;

/**
 * A single value.
 * <p>
 * Single values have an actual value as well as a "potential". For
 * non-random values, the "potential" is arbitrarily selected.
 */
public abstract class DCSingle extends DCValue {
  protected double value;
  protected double potential;
  
  /**
   * Returns this DCSingle itself.
   */
  @Override
  public DCSingle getSingle() {
    return this;
  }
  
  /**
   * Returns this DCSingle as a DCList (containing only itself).
   */
  @Override
  public DCList getList() {
    ArrayList<DCValue> items = new ArrayList<>();
    items.add(this);
    return new DCList(items);
  }
  
  /**
   * Returns the actual amount of this DCSingle.
   * 
   * @return The actual amount.
   */
  public double getAmount() {
    return value;
  }
  
  /**
   * Returns the potential amount of this DCSingle.
   * 
   * @return The potential amount.
   */
  public double getPotential() {
    return potential;
  }
  
  /**
   * Returns this DCSingle as a DCString (by converting it to a number).
   */
  @Override
  public DCString getString() {
    return new DCString(this.toString());
  }
}