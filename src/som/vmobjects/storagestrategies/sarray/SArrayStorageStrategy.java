package som.vmobjects.storagestrategies.sarray;

import som.vmobjects.*;

public abstract class SArrayStorageStrategy {
  public abstract int getNumberOfIndexableFields(SArray arr);

  public abstract SAbstractObject getIndexableField(SArray arr, int index);

  public abstract SArrayStorageStrategy setIndexableFieldMaybeTransition(SArray arr, int index, SAbstractObject value);

}
