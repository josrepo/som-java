package som.vmobjects.storagestrategies.sarray;

import som.vmobjects.*;

import java.util.Arrays;

/**
 * Object SArray Strategy
 *
 * Stores an SAbstractObject[]
 */
public class AbstractObjectStrategy implements SArrayStorageStrategy {
  private final SObject nilObject;

  public AbstractObjectStrategy(SObject nilObject) {
    this.nilObject = nilObject;
  }
  public void initialize(SArray arr, int numElements) {
    SAbstractObject[] storage = new SAbstractObject[numElements];
    Arrays.fill(storage, nilObject);
    arr.storage = storage;
  }

  public void initialize(SArray arr, long[] elements) {
    SAbstractObject[] storage = new SAbstractObject[elements.length];

    for (int i = 0; i < elements.length; i++) {
      storage[i] = elements[i] == IntegerStrategy.EMPTY_SLOT ? nilObject : SInteger.getInteger(elements[i]);
    }

    arr.storage = storage;
  }

  public void initialize(SArray arr, double[] elements) {
    SAbstractObject[] storage = new SAbstractObject[elements.length];

    for (int i = 0; i < elements.length; i++) {
      storage[i] = elements[i] == DoubleStrategy.EMPTY_SLOT ? nilObject : new SDouble(elements[i]);
    }

    arr.storage = storage;
  }

  @Override
  public int getNumberOfIndexableFields(SArray arr) {
    return ((SAbstractObject[]) arr.storage).length;
  }

  @Override
  public SAbstractObject getIndexableField(SArray arr, int index) {
    return ((SAbstractObject[]) arr.storage)[index];
  }

  @Override
  public SArrayStorageStrategy setIndexableFieldMaybeTransition(SArray arr, int index, SAbstractObject value) {
    ((SAbstractObject[]) arr.storage)[index] = value;
    return this;
  }

  public void setIndexableFieldNoTransition(SArray arr, int index, SAbstractObject value) {
    ((SAbstractObject[]) arr.storage)[index] = value;
  }

}
