package som.vmobjects;

import som.vm.Universe;

public class SVector extends SObject {

  public SVector(final long numElements, SObject nilObject) {
    super(3, nilObject);
    indexableFields = new SAbstractObject[(int) numElements];
    first = last = 0;
  }

  public SAbstractObject getIndexableField(final long index) {
    final int storeIndex = (int) index + first;
    // TODO: checkIndex
    return indexableFields[storeIndex];
  }

  public SAbstractObject getFirstIndexableField() {
    if (last - first > 0) {
      return indexableFields[first];
    } else {
      return Universe.current().nilObject;
    }
  }

  public SAbstractObject getLastIndexableField() {
    if (last - first > 0) {
      return indexableFields[last - 1];
    } else {
      return Universe.current().nilObject;
    }
  }

  public int getIndexOfElement(final SAbstractObject element) {
    for (int i = 0; i < indexableFields.length; i++) {
      if (indexableFields[i] == element) {
        return i - first;
      }
    }

    return -1;
  }

  public void setIndexableField(final long index, final SAbstractObject value) {
    final int storeIndex = (int) index + first;
    // TODO: checkIndex
    indexableFields[storeIndex] = value;
  }

  public void setLastIndexableField(final SAbstractObject value) {
    if (last >= indexableFields.length) {
      final SAbstractObject[] newStorage = new SAbstractObject[2 * indexableFields.length];

      for (int i = 0; i < indexableFields.length; i++) {
        newStorage[i] = indexableFields[i];
      }

      indexableFields = newStorage;
    }

    indexableFields[last] = value;
    last++;
  }

  public SObject removeElement(final SAbstractObject element) {
    final SAbstractObject[] newStorage = new SAbstractObject[indexableFields.length];
    int newLast = 1;
    boolean found = false;

    for (int i = 0; i < indexableFields.length; i++) {
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

    return found ? Universe.current().trueObject : Universe.current().falseObject;
  }

  public SAbstractObject removeFirstElement() {
    if (last != first) {
      final SAbstractObject value = indexableFields[first];
      indexableFields[first] = Universe.current().nilObject;
      first++;
      return value;
    } else {
      // TODO: throw error
      return null;
    }
  }

  public SAbstractObject removeLastElement() {
    if (last - first > 0) {
      last--;
      final SAbstractObject value = indexableFields[last];
      indexableFields[last] = Universe.current().nilObject;
      return value;
    } else {
      // TODO: throw error
      return null;
    }
  }

  public int getCapacity() {
    return indexableFields.length;
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
