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

import som.interpreter.Frame;
import som.interpreter.Interpreter;
import som.vm.Universe;


public abstract class SAbstractObject {

  public abstract SClass getSOMClass(Universe universe);

  public void send(final String selectorString, final SAbstractObject[] arguments,
      final Universe universe, final Interpreter interpreter) {
    // Turn the selector string into a selector
    SSymbol selector = universe.symbolFor(selectorString);

    // Push the receiver onto the stack
    interpreter.getFrame().push(this);

    // Push the arguments onto the stack
    for (SAbstractObject arg : arguments) {
      interpreter.getFrame().push(arg);
    }

    // Lookup the invokable
    SInvokable invokable = getSOMClass(universe).lookupInvokable(selector);

    // Invoke the invokable
    invokable.invoke(interpreter.getFrame(), interpreter);
  }

  public void sendDoesNotUnderstand(final SSymbol selector,
      final Universe universe, final Interpreter interpreter) {
    interpreter.getFrame().printStackTrace();

    // Compute the number of arguments
    int numberOfArguments = selector.getNumberOfSignatureArguments();

    Frame frame = interpreter.getFrame();

    // Allocate an array with enough room to hold all arguments
    // except for the receiver, which is passed implicitly, as receiver of #dnu.
    SArray argumentsArray = universe.newArray(numberOfArguments - 1);

    // Remove all arguments and put them in the freshly allocated array
    for (int i = numberOfArguments - 2; i >= 0; i--) {
      argumentsArray.setIndexableField(i, frame.pop());
    }

    frame.pop(); // pop receiver

    SAbstractObject[] args = {selector, argumentsArray};
    send("doesNotUnderstand:arguments:", args, universe, interpreter);
  }

  public void sendUnknownGlobal(final SSymbol globalName,
      final Universe universe, final Interpreter interpreter) {
    SAbstractObject[] arguments = {globalName};
    send("unknownGlobal:", arguments, universe, interpreter);
  }

  public void sendEscapedBlock(final SBlock block, final Universe universe,
      final Interpreter interpreter) {
    SAbstractObject[] arguments = {block};
    send("escapedBlock:", arguments, universe, interpreter);
  }

  @Override
  public String toString() {
    return "a " + getSOMClass(Universe.current()).getName().getEmbeddedString();
  }
}
