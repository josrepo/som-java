package som.primitives;

import som.interpreter.Frame;
import som.interpreter.Interpreter;
import som.vm.Universe;
import som.vmobjects.SAbstractObject;
import som.vmobjects.SBlock;
import som.vmobjects.SInteger;
import som.vmobjects.SPrimitive;
import som.vmobjects.SVector;

public class VectorPrimitives extends Primitives {

  public VectorPrimitives(final Universe universe) {
    super(universe);
  }

  @Override
  public void installPrimitives() {
    installInstancePrimitive(new SPrimitive("initialize:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SInteger length = (SInteger) frame.pop();
        frame.pop(); // not required
        frame.push(universe.newVector(length.getEmbeddedInteger()));
      }
    });

    installInstancePrimitive(new SPrimitive("append:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SAbstractObject value = frame.pop();
        SVector self = (SVector) frame.pop();
        self.setLastIndexableField(value);
        frame.push(self);
      }
    });

    installInstancePrimitive(new SPrimitive("at:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SInteger index = (SInteger) frame.pop();
        SVector self = (SVector) frame.pop();
        frame.push(self.getIndexableField(index.getEmbeddedInteger()));
      }
    });

    installInstancePrimitive(new SPrimitive("at:put:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SAbstractObject value = frame.pop();
        SInteger index = (SInteger) frame.pop();
        SVector self = (SVector) frame.getStackElement(0);
        self.setIndexableField(index.getEmbeddedInteger(), value);
      }
    });

    installInstancePrimitive(new SPrimitive("first", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(self.getFirstIndexableField(universe.nilObject));
      }
    });

    installInstancePrimitive(new SPrimitive("last", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(self.getLastIndexableField(universe.nilObject));
      }
    });

    installInstancePrimitive(new SPrimitive("do:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SBlock block = (SBlock) frame.pop();
        SVector self = (SVector) frame.pop();

        final SBlock.Evaluation eval = (SBlock.Evaluation) SBlock.getEvaluationPrimitive(block.getMethod().getNumberOfArguments(), universe);
        frame.push(block);

        for (int i = self.getFirstIndex(); i < self.getLastIndex() - 1; i++) {
          frame.push(self.getIndexableField(i));
          eval.invoke(frame, universe.getInterpreter());
          frame.pop();
        }
      }
    });

    installInstancePrimitive(new SPrimitive("doIndexes:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SBlock block = (SBlock) frame.pop();
        SVector self = (SVector) frame.pop();

        final SBlock.Evaluation eval = (SBlock.Evaluation) SBlock.getEvaluationPrimitive(block.getMethod().getNumberOfArguments(), universe);
        frame.push(block);

        for (int i = 1; i <= self.getLastIndex() - self.getFirstIndex(); i++) {
          frame.push(SInteger.getInteger(i));
          eval.invoke(frame, universe.getInterpreter());
          frame.pop();
        }
//
//        // send to: last - first do: block

//        SInteger.getInteger(1).send("to:", new SAbstractObject[] {SInteger.getInteger(self.getLastIndex() - self.getFirstIndex())}, universe, universe.getInterpreter());
//        frame.pop().send("do:", new SAbstractObject[]{block}, universe, universe.getInterpreter());
      }
    });

    installInstancePrimitive(new SPrimitive("remove", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(self.removeLastElement(universe.nilObject));
      }
    });

    installInstancePrimitive(new SPrimitive("remove:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SAbstractObject object = frame.pop();
        SVector self = (SVector) frame.pop();
        frame.push(self.removeElement(object, universe));
      }
    });

    installInstancePrimitive(new SPrimitive("indexOf:", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SAbstractObject object = frame.pop();
        SVector self = (SVector) frame.pop();
        frame.push(SInteger.getInteger(self.getIndexOfElement(object)));
      }
    });

    installInstancePrimitive(new SPrimitive("isEmpty", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(self.isEmpty(universe));
      }
    });

    installInstancePrimitive(new SPrimitive("size", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(SInteger.getInteger(self.getSize()));
      }
    });

    installInstancePrimitive(new SPrimitive("capacity", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(SInteger.getInteger(self.getCapacity()));
      }
    });

    installInstancePrimitive(new SPrimitive("asArray", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(self.asArray());
      }
    });

    installInstancePrimitive(new SPrimitive("removeFirst", universe) {
      @Override
      public void invoke(Frame frame, Interpreter interpreter) {
        SVector self = (SVector) frame.pop();
        frame.push(self.removeFirstElement(universe.nilObject));
      }
    });
  }

}
