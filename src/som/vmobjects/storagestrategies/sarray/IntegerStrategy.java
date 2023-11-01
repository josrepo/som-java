package som.vmobjects.storagestrategies.sarray;

import som.vm.Universe;
import som.vmobjects.SAbstractObject;
import som.vmobjects.SArray;
import som.vmobjects.SDouble;
import som.vmobjects.SInteger;

import java.util.Arrays;

/**
 * Integer SArray Strategy
 *
 * Stores a long[]
 */
public class IntegerStrategy implements SArrayStorageStrategy {

  // Magic value used to indicate an empty element
  // Array is transitioned to an AbstractObjectStrategy if the magic value is ever inserted
  public static final long EMPTY_SLOT = Long.MIN_VALUE + 2L;

  public void initialize(SArray arr, int numElements) {
    long[] storage = new long[numElements];
    Arrays.fill(storage, EMPTY_SLOT);
    arr.storage = storage;
  }

  @Override
  public int getNumberOfIndexableFields(SArray arr) {
    return ((long[]) arr.storage).length;
  }

  @Override
  public SAbstractObject getIndexableField(SArray arr, int index) {
    return SInteger.getInteger(((long[]) arr.storage)[index]);
  }

  @Override
  public SArrayStorageStrategy setIndexableFieldMaybeTransition(SArray arr, int index, SAbstractObject value) {
    if (value instanceof SInteger) {
      final long embeddedInteger = ((SInteger) value).getEmbeddedInteger();

      if (embeddedInteger != EMPTY_SLOT) {
        ((long[]) arr.storage)[index] = embeddedInteger;
        return this;
      }
    } else if (value instanceof SDouble) {
      final double embeddedDouble = ((SDouble) value).getEmbeddedDouble();

      if (embeddedDouble != DoubleStrategy.EMPTY_SLOT) {
        final DoubleStrategy doubleStrategy = Universe.current().getDoubleStrategy();
        doubleStrategy.initialize(arr, (long[]) arr.storage);
        doubleStrategy.setIndexableFieldNoTransition(arr, index, embeddedDouble);
        return doubleStrategy;
      }
    }

    final AbstractObjectStrategy abstractObjectStrategy = Universe.current().getAbstractObjectStrategy();
    abstractObjectStrategy.initialize(arr, (long[]) arr.storage);
    abstractObjectStrategy.setIndexableFieldNoTransition(arr, index, value);
    return abstractObjectStrategy;
  }

  public void setIndexableFieldNoTransition(SArray arr, int index, long value) {
    ((long[]) arr.storage)[index] = value;
  }

}
