package console.framework;

import org.junit.Assert;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

class ConsoleScenario {

    final Queue<Event> queue = new LinkedList<>();

    void type(String line) {
        queue.add(new TypeLine(line));
    }

    void read(String line) {
        queue.add(new WriteLine(line));
    }

    void step(String name) {
        queue.add(new Step(name));
    }

    final ConsoleReader reader = () -> {
        final Event event = queue.poll();
        if (!(event instanceof TypeLine)) {
            Assert.fail("expected line reading but got " + event);
        }
        final String line = ((TypeLine) event).line;
        System.out.println(event);
        return line;
    };

    final ConsoleWriter writer = (line) -> {
        final Event event = queue.poll();
        Assert.assertEquals(event, new WriteLine(line));
        System.out.println(event);
    };

    final Consumer<String> stepChecker = (name) -> {
        final Event event = queue.poll();
        Assert.assertEquals(event, new Step(name));
        System.out.println(event);
    };

    void checkFinish() {
        Assert.assertTrue("", queue.isEmpty());
    }

    interface Event {
    }

    public static class TypeLine implements Event {
        public final String line;

        TypeLine(String line) {
            this.line = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeLine readLine = (TypeLine) o;
            return Objects.equals(line, readLine.line);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line);
        }

        @Override
        public String toString() {
            return "<< '" + line + "'";
        }
    }

    static class WriteLine implements Event {
        final String line;

        WriteLine(String line) {
            this.line = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WriteLine writeLine = (WriteLine) o;
            return Objects.equals(line, writeLine.line);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line);
        }

        @Override
        public String toString() {
            return ">> '" + line + "'";
        }
    }

    static class Step implements Event {
        final String name;

        Step(String line) {
            this.name = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Step step = (Step) o;
            return Objects.equals(name, step.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "do " + name;
        }
    }
}
