package som.vmobjects.storagestrategies.sarray;

import som.vm.Universe;
import som.vmobjects.*;

/**
 * Empty SArray Strategy
 *
 * Stores the array length as an int
 */
public class EmptyStrategy implements SArrayStorageStrategy {

  private final SObject nilObject;

  public EmptyStrategy(SObject nilObject) {
    this.nilObject = nilObject;
  }

  public void initialize(SArray arr, int numElements) {
    arr.storage = numElements;
  }

  @Override
  public int getNumberOfIndexableFields(SArray arr) {
    return (int) arr.storage;
  }

  @Override
  public SAbstractObject getIndexableField(SArray arr, int index) {
    return nilObject;
  }

  @Override
  public SArrayStorageStrategy setIndexableFieldMaybeTransition(SArray arr, int index, SAbstractObject value) {
    if (value == nilObject) {
        return this;
    }

    if (value instanceof SInteger) {
      final long embeddedInteger = ((SInteger) value).getEmbeddedInteger();

      if (embeddedInteger != IntegerStrategy.EMPTY_SLOT) {
        final IntegerStrategy integerStrategy = Universe.current().getIntegerStrategy();
        integerStrategy.initialize(arr, (int) arr.storage);
        integerStrategy.setIndexableFieldNoTransition(arr, index, embeddedInteger);
        return integerStrategy;
      }
    } else if (value instanceof SDouble) {
      final double embeddedDouble = ((SDouble) value).getEmbeddedDouble();

      if (embeddedDouble != DoubleStrategy.EMPTY_SLOT) {
        final DoubleStrategy doubleStrategy = Universe.current().getDoubleStrategy();
        doubleStrategy.initialize(arr, (int) arr.storage);
        doubleStrategy.setIndexableFieldNoTransition(arr, index, embeddedDouble);
        return doubleStrategy;
      }
    }

    final AbstractObjectStrategy abstractObjectStrategy = Universe.current().getAbstractObjectStrategy();
    abstractObjectStrategy.initialize(arr, (int) arr.storage);
    abstractObjectStrategy.setIndexableFieldNoTransition(arr, index, value);
    return abstractObjectStrategy;
  }

}
