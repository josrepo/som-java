package som.vmobjects.storagestrategies.sarray;

import som.vm.Universe;
import som.vmobjects.*;

/**
 * Empty SArray Strategy
 *
 * Stores the array length as an int
 */
public class EmptyStrategy extends SArrayStorageStrategy {

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

      if (embeddedInteger != SIntegerStrategy.EMPTY_SLOT) {
        final SIntegerStrategy sIntegerStrategy = Universe.current().getSIntegerStrategy();
        sIntegerStrategy.initialize(arr, (int) arr.storage);
        sIntegerStrategy.setIndexableFieldNoTransition(arr, index, embeddedInteger);
        return sIntegerStrategy;
      }
    } else if (value instanceof SDouble) {
      final double embeddedDouble = ((SDouble) value).getEmbeddedDouble();

      if (embeddedDouble != SDoubleStrategy.EMPTY_SLOT) {
        final SDoubleStrategy sDoubleStrategy = Universe.current().getSDoubleStrategy();
        sDoubleStrategy.initialize(arr, (int) arr.storage);
        sDoubleStrategy.setIndexableFieldNoTransition(arr, index, embeddedDouble);
        return sDoubleStrategy;
      }
    }

    final SAbstractObjectStrategy sAbstractObjectStrategy = Universe.current().getSAbstractObjectStrategy();
    sAbstractObjectStrategy.initialize(arr, (int) arr.storage);
    sAbstractObjectStrategy.setIndexableFieldNoTransition(arr, index, value);
    return sAbstractObjectStrategy;
  }

}
