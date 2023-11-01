package som.vmobjects.storagestrategies.sarray;

import som.interpreter.Frame;
import som.vm.Universe;
import som.vmobjects.*;

public abstract class SArrayStorageStrategy {
  public abstract int getNumberOfIndexableFields(SArray arr);

  public abstract SAbstractObject getIndexableField(SArray arr, int index);

  public abstract SArrayStorageStrategy setIndexableFieldMaybeTransition(SArray arr, int index, SAbstractObject value);

  public SArrayStorageStrategy fillIndexableFieldsAndTransition(SArray arr, SAbstractObject value, int numElements) {
    if (value == Universe.current().nilObject) {
      final EmptyStrategy emptyStrategy = Universe.current().getEmptyStrategy();
      emptyStrategy.initialize(arr, numElements);
      return emptyStrategy;
    }

    if (value instanceof SInteger) {
      final long embeddedInteger = ((SInteger) value).getEmbeddedInteger();

      if (embeddedInteger != SIntegerStrategy.EMPTY_SLOT) {
        final SIntegerStrategy sIntegerStrategy = Universe.current().getSIntegerStrategy();
        sIntegerStrategy.initializeAll(arr, embeddedInteger, numElements);
        return sIntegerStrategy;
      }
    } else if (value instanceof SDouble) {
      final double embeddedDouble = ((SDouble) value).getEmbeddedDouble();

      if (embeddedDouble != SDoubleStrategy.EMPTY_SLOT) {
        final SDoubleStrategy sDoubleStrategy = Universe.current().getSDoubleStrategy();
        sDoubleStrategy.initializeAll(arr, embeddedDouble, numElements);
        return sDoubleStrategy;
      }
    } else if (value instanceof SBlock) {
      throw new UnsupportedOperationException("Not implemented");
    }

    final SAbstractObjectStrategy sAbstractObjectStrategy = Universe.current().getSAbstractObjectStrategy();
    sAbstractObjectStrategy.initializeAll(arr, value, numElements);
    return sAbstractObjectStrategy;
  }
}
