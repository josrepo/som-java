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


public class SArray extends SAbstractObject {

  public SArray(final SObject nilObject, long numElements) {
    this.nilObject = nilObject;
    strategy = new EmptyStrategy(nilObject, (int) numElements);
  }

  public SAbstractObject getIndexableField(long index) {
    return strategy.getIndexableField((int) index);
  }

  public void setIndexableField(long index, SAbstractObject value) {
    if (strategy.shouldStrategyChange(value)) {
      int numElements = strategy.getNumberOfIndexableFields();
      strategy = new AbstractObjectStrategy(nilObject, numElements);
    }

    strategy.setIndexableField((int) index, value);
  }

  public int getNumberOfIndexableFields() {
    return strategy.getNumberOfIndexableFields();
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

  private final SObject nilObject;
  private SArrayStorageStrategy strategy;

  private interface SArrayStorageStrategy {
    int getNumberOfIndexableFields();
    SAbstractObject getIndexableField(int index);
    void setIndexableField(int index, SAbstractObject value);
    boolean shouldStrategyChange(SAbstractObject value);
  }

  private static class EmptyStrategy implements SArrayStorageStrategy {

    private final SObject nilObject;
    private final int numElements;

    private EmptyStrategy(SObject nilObject, int numElements) {
      this.nilObject = nilObject;
      this.numElements = numElements;
    }

    @Override
    public int getNumberOfIndexableFields() {
      return numElements;
    }

    @Override
    public SAbstractObject getIndexableField(int index) {
      return nilObject;
    }

    @Override
    public void setIndexableField(int index, SAbstractObject value) {
      throw new UnsupportedOperationException("Cannot set indexable field on empty strategy.");
    }

    @Override
    public boolean shouldStrategyChange(SAbstractObject value) {
      return value != nilObject;
    }

  }

  private static final class AbstractObjectStrategy implements SArrayStorageStrategy {

    private final SAbstractObject[] indexableFields;

    private AbstractObjectStrategy(SObject nilObject, int numElements) {
      indexableFields = new SAbstractObject[numElements];

      for (int i = 0; i < numElements; i++) {
        indexableFields[i] = nilObject;
      }
    }

    @Override
    public int getNumberOfIndexableFields() {
      return indexableFields.length;
    }

    @Override
    public SAbstractObject getIndexableField(int index) {
      return indexableFields[index];
    }

    @Override
    public void setIndexableField(int index, SAbstractObject value) {
      indexableFields[index] = value;
    }

    @Override
    public boolean shouldStrategyChange(SAbstractObject value) {
      return false;
    }

  }

}
