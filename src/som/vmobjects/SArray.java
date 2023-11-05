/**
 * Copyright (c) 2009 Michael Haupt, michael.haupt@hpi.uni-potsdam.de
 * Software Architecture Group, Hasso Plattner Institute, Potsdam, Germany
 * http://www.hpi.uni-potsdam.de/swa/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package som.vmobjects;

import som.vm.Universe;
import som.vmobjects.storagestrategies.sarray.*;

public class SArray extends SAbstractObject {

  public SArray(long numElements) {
    strategy = Universe.current().getEmptyStrategy();
    ((EmptyStrategy) strategy).initialize(this, (int) numElements);
  }

  public SAbstractObject getIndexableField(long index) {
    return strategy.getIndexableField(this, (int) index);
  }

  public void setIndexableField(long index, SAbstractObject value) {
    strategy = strategy.setIndexableFieldMaybeTransition(this, (int) index, value);
  }

  public int getNumberOfIndexableFields() {
    return strategy.getNumberOfIndexableFields(this);
  }

  public SArray copyAndExtendWith(SAbstractObject value, final Universe universe) {
    // Allocate a new array which has one indexable field more than this
    // array
    SArray result = universe.newArray(getNumberOfIndexableFields() + 1);

    // Copy the indexable fields from this array to the new array
    copyIndexableFieldsTo(result);

    // Insert the given object as the last indexable field in the new array
    result.setIndexableField(getNumberOfIndexableFields(), value);

    return result;
  }

  protected void copyIndexableFieldsTo(SArray destination) {
    // Copy all indexable fields from this array to the destination array
    for (int i = 0; i < getNumberOfIndexableFields(); i++) {
      destination.setIndexableField(i, getIndexableField(i));
    }
  }

  @Override
  public SClass getSOMClass(final Universe universe) {
    return universe.arrayClass;
  }

  private SArrayStorageStrategy strategy;
  public Object storage;

}
