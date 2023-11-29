package som.vmobjects;

import som.interpreter.Interpreter;
import som.vm.Universe;

public class SVector extends SObject {

  public SVector(final long numElements, final SObject nilObject) {
    super(3, nilObject);
    indexableFields = new SAbstractObject[(int) numElements];
    first = last = 1;
  }

  public SAbstractObject getIndexableField(final long index) {
    final int storeIndex = (int) index + first - 1;
    // TODO: checkIndex
    return indexableFields[storeIndex - 1];
  }

  public SAbstractObject getFirstIndexableField(final SObject nilObject) {
    if (getSize() > 0) {
      return indexableFields[first - 1];
    } else {
      return nilObject;
    }
  }

  public SAbstractObject getLastIndexableField(final SObject nilObject) {
    if (getSize() > 0) {
      return indexableFields[last - 2];
    } else {
      return nilObject;
    }
  }

  public int getIndexOfElement(final SAbstractObject element) {
    for (int i = 0; i < indexableFields.length; i++) {
      if (indexableFields[i] == element) {
        return i + 2 - first;
      }
    }

    return -1;
  }

  public boolean containsElement(final SAbstractObject element) {
    if (element instanceof SString) {
      final String elementString = ((SString) element).getEmbeddedString();
      for (int i = first; i <= last - 1; i++) {
        if (indexableFields[i - 1] instanceof SString && elementString.equals(((SString) indexableFields[i - 1]).getEmbeddedString())) {
          return true;
        }
      }
    } else {
      for (int i = first; i <= last - 1; i++) {
        if (indexableFields[i - 1] == element) {
          return true;
        }
      }
    }

    return false;
  }

  public void setIndexableField(final long index, final SAbstractObject value) {
    final int storeIndex = (int) index + first - 1;
    // TODO: checkIndex
    indexableFields[storeIndex - 1] = value;
  }

  public void setLastIndexableField(final SAbstractObject value) {
    if (last > indexableFields.length) {
      final SAbstractObject[] newStorage = new SAbstractObject[2 * indexableFields.length];
      System.arraycopy(indexableFields, 0, newStorage, 0, indexableFields.length);
      indexableFields = newStorage;
    }

    indexableFields[last - 1] = value;
    last++;
  }

  public boolean removeElement(final SAbstractObject element) {
    final SAbstractObject[] newStorage = new SAbstractObject[indexableFields.length];
    int newLast = 1;
    boolean found = false;

    for (int i = first; i < last; i++) {
      if (indexableFields[i] == element) {
        found = true;
      } else {
        newStorage[i] = indexableFields[i];
        newLast++;
      }
    }

    indexableFields = newStorage;
    last = newLast;
    first = 1;

    return found;
  }

  public SAbstractObject removeFirstElement(final SObject nilObject) {
    if (last != first) {
      final SAbstractObject value = indexableFields[first - 1];
      indexableFields[first - 1] = nilObject;
      first++;
      return value;
    } else {
      // TODO: throw error
      System.out.println("FAILING ON REMOVE FIRST ELEMENT");
      return null;
    }
  }

  public SAbstractObject removeLastElement(final SObject nilObject) {
    if (getSize() > 0) {
      last--;
      final SAbstractObject value = indexableFields[last - 1];
      indexableFields[last - 1] = nilObject;
      return value;
    } else {
      // TODO: throw error
      System.out.println("FAILING ON REMOVE LAST ELEMENT");
      return null;
    }
  }

  public boolean isEmpty() {
    return first == last;
  }

  public int getSize() {
    return last - first;
  }

  public int getCapacity() {
    return indexableFields.length;
  }

  public int getFirstIndex() {
    return first;
  }

  public int getLastIndex() {
    return last;
  }

  public SArray asArray() {
    final SArray arr = new SArray(getSize());
    for (int i = 0; i < getSize(); i++) {
      arr.setIndexableField(i, indexableFields[first + i - 1]);
    }
    return arr;
  }

  @Override
  public SClass getSOMClass(final Universe universe) {
    return universe.vectorClass;
  }

  @Override
  public String toString() {
    return "a " + getSOMClass(Universe.current()).getName().getEmbeddedString();
  }

  private SAbstractObject[] indexableFields;
  private int first, last;

}
