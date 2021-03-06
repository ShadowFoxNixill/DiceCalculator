package net.nixill.dice.objects;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A list of resolved, unchanging values.
 */
public class DCList extends DCValue {
  private ArrayList<DCValue> listItems;
  
  /**
   * Creates a new list of values.
   */
  public DCList(Collection<DCValue> items) {
    listItems = new ArrayList<>(items);
  }
  
  /**
   * Casts this DCList to a {@link DCSingle} by taking the sum of all of
   * its items.
   */
  @Override
  public DCSingle getSingle() {
    double sum = 0;
    for (DCValue val : listItems) {
      sum += val.getSingle().getAmount();
    }
    return new DCNumber(sum);
  }
  
  /**
   * Returns this DCList itself.
   */
  @Override
  public DCList getList() {
    return this;
  }
  
  /**
   * Returns this object converted to a DCString.
   */
  @Override
  public DCString getString() {
    StringBuilder out = new StringBuilder();
    
    for (DCValue val : listItems) {
      char chr = (char) val.getSingle().value;
      out.append(chr);
    }
    
    return new DCString(out.toString());
  }
  
  /**
   * Returns a new {@link ArrayList} containing this list's items.
   */
  public ArrayList<DCValue> getItems() {
    return new ArrayList<>(listItems);
  }
  
  /**
   * Returns the number of items in this list.
   * 
   * @return The size of this list.
   */
  public int size() {
    return listItems.size();
  }
  
  /**
   * Returns a single item from the list.
   * 
   * @param i
   *   The index of the item to get.
   * @return The selected item.
   */
  public DCValue get(int i) {
    return listItems.get(i);
  }
  
  /**
   * Returns whether or not there's a string anywhere in the list.
   * 
   * @return <code>true</code> iff any sub-item is a string.
   */
  public boolean containsString() {
    for (DCValue val : listItems) {
      if (val instanceof DCString) {
        return true;
      } else if (val instanceof DCList) {
        if (((DCList) val).containsString()) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  @Override
  public String toString(int lvl) {
    if (listItems.isEmpty()) {
      return "[]";
    }
    
    String out = "";
    if (containsString()) {
      out = "[";
    } else {
      out = numFormat.format(getSingle().getAmount()) + " [";
    }
    
    for (DCValue val : listItems) {
      out += val.toString(lvl - 1) + ", ";
    }
    out = out.substring(0, out.length() - 2) + "]";
    return out;
  }
  
  @Override
  public String toCode() {
    if (listItems.isEmpty()) {
      return "[]";
    }
    
    String out = "[";
    for (DCValue val : listItems) {
      out += val.toCode() + ",";
    }
    out = out.substring(0, out.length() - 1) + "]";
    return out;
  }
  
  @Override
  public void printTree(int level) {
    printSpaced(level, "List: " + listItems.size() + " item(s)");
    for (DCValue val : listItems) {
      val.printTree(level + 1);
    }
  }
}