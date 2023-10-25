package som.vmobjects.storagestrategies.sarray;

import som.vmobjects.SAbstractObject;
import som.vmobjects.SArray;
import som.vmobjects.SDouble;

import java.util.Arrays;

/**
 * Double SArray Strategy
 *
 * Stores a double[]
 */
public class DoubleStrategy implements SArrayStorageStrategy {

  // Magic value used to indicate an empty element
  // Array is transitioned to an AbstractObjectStrategy if the magic value is ever inserted
  public static final double EMPTY_SLOT = Double.MIN_VALUE + 2L;

  public void initialize(SArray arr, int numElements) {
    double[] storage = new double[numElements];
    Arrays.fill(storage, EMPTY_SLOT);
    arr.storage = storage;
  }

  public void initialize(SArray arr, long[] elements) {
    double[] storage = new double[elements.length];

    for (int i = 0; i < elements.length; i++) {
      storage[i] = elements[i] == IntegerStrategy.EMPTY_SLOT ? DoubleStrategy.EMPTY_SLOT : (double) elements[i];
    }

    arr.storage = storage;
  }

  @Override
  public int getNumberOfIndexableFields(SArray arr) {
    return ((double[]) arr.storage).length;
  }

  @Override
  public SAbstractObject getIndexableField(SArray arr, int index) {
    return new SDouble(((double[]) arr.storage)[index]);
  }

  @Override
  public SArrayStorageStrategy setIndexableFieldMaybeTransition(SArray arr, int index, SAbstractObject value) {
    if (value instanceof SDouble) {
      final double embeddedDouble = ((SDouble) value).getEmbeddedDouble();

      if (embeddedDouble != EMPTY_SLOT) {
        ((double[]) arr.storage)[index] = embeddedDouble;
        return this;
      }
    }

    SArray.abstractObjectStrategy.initialize(arr, (double[]) arr.storage);
    SArray.abstractObjectStrategy.setIndexableFieldNoTransition(arr, index, value);
    return SArray.abstractObjectStrategy;
  }

  public void setIndexableFieldNoTransition(SArray arr, int index, double value) {
    ((double[]) arr.storage)[index] = value;
  }

}
