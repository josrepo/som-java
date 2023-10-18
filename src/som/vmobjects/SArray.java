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
    strategy = new EmptyStrategy(nilObject, (int) numElements);
  }

  public SAbstractObject getIndexableField(long index) {
    return strategy.getIndexableField((int) index);
  }

  public void setIndexableField(long index, SAbstractObject value) {
    strategy = strategy.setIndexableFieldMaybeTransition((int) index, value);
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

  private SArrayStorageStrategy strategy;

  private interface SArrayStorageStrategy {
    int getNumberOfIndexableFields();
    SAbstractObject getIndexableField(int index);
    SArrayStorageStrategy setIndexableFieldMaybeTransition(int index, SAbstractObject value);
  }

  private static final class EmptyStrategy implements SArrayStorageStrategy {

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
    public SArrayStorageStrategy setIndexableFieldMaybeTransition(int index, SAbstractObject value) {
      if (value == nilObject) {
        return this;
      }

      if (value instanceof SInteger) { // FIXME: Not a fan of instanceof
        final long embeddedInteger = ((SInteger) value).getEmbeddedInteger();

        if (embeddedInteger != IntegerStrategy.EMPTY_SLOT) {
          final IntegerStrategy strategy = new IntegerStrategy(numElements);
          strategy.setIndexableFieldNoTransition(index, embeddedInteger);
          return strategy;
        }
      }

      final AbstractObjectStrategy strategy = new AbstractObjectStrategy(nilObject, numElements);
      strategy.setIndexableFieldNoTransition(index, value);
      return strategy;
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

    private AbstractObjectStrategy(SObject nilObject, long[] elements) {
      indexableFields = new SAbstractObject[elements.length];

      for (int i = 0; i < elements.length; i++) {
        indexableFields[i] = elements[i] == IntegerStrategy.EMPTY_SLOT ? nilObject : SInteger.getInteger(elements[i]);
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
    public SArrayStorageStrategy setIndexableFieldMaybeTransition(int index, SAbstractObject value) {
      indexableFields[index] = value;
      return this;
    }

    public void setIndexableFieldNoTransition(int index, SAbstractObject value) {
      indexableFields[index] = value;
    }

  }

  private static final class IntegerStrategy implements SArrayStorageStrategy {

    // Magic value used to indicate an empty element
    // Array is transitioned to an AbstractObjectStrategy if the magic value is ever inserted
    public static final long EMPTY_SLOT = Long.MIN_VALUE + 2L;
    private final long[] indexableFields;

    private IntegerStrategy(int numElements) {
      indexableFields = new long[numElements];

      for (int i = 0; i < numElements; i++) {
        indexableFields[i] = EMPTY_SLOT;
      }
    }

    @Override
    public int getNumberOfIndexableFields() {
      return indexableFields.length;
    }

    @Override
    public SAbstractObject getIndexableField(int index) {
      return SInteger.getInteger(indexableFields[index]);
    }

    @Override
    public SArrayStorageStrategy setIndexableFieldMaybeTransition(int index, SAbstractObject value) {
      if (value instanceof SInteger) {
        final long embeddedInteger = ((SInteger) value).getEmbeddedInteger();

        if (embeddedInteger != EMPTY_SLOT) {
          indexableFields[index] = embeddedInteger;
          return this;
        }
      }

      final AbstractObjectStrategy strategy = new AbstractObjectStrategy(Universe.current().nilObject, indexableFields);
      strategy.setIndexableFieldNoTransition(index, value);
      return strategy;
    }

    public void setIndexableFieldNoTransition(int index, long value) {
      indexableFields[index] = value;
    }

  }

}
