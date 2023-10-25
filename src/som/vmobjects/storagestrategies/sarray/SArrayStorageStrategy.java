package som.vmobjects.storagestrategies.sarray;

import som.vmobjects.SAbstractObject;
import som.vmobjects.SArray;

public interface SArrayStorageStrategy {
  int getNumberOfIndexableFields(SArray arr);

  SAbstractObject getIndexableField(SArray arr, int index);

  SArrayStorageStrategy setIndexableFieldMaybeTransition(SArray arr, int index, SAbstractObject value);
}
