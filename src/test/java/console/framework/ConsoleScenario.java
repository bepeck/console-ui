package console.framework;

import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

class ConsoleScenario {

    private final Queue<Event> queue = new LinkedList<>();

    public ConsoleScenario(final List<Event> events) {
        queue.addAll(events);
    }

    public String readLine() {
        final Event event = queue.poll();
        if (!(event instanceof TypeLine)) {
            Assert.fail("expected line reading but got " + event);
        }
        final String line = ((TypeLine) event).line;
        System.out.println(event);
        return line;
    }

    public void writeLine(String line) {
        final Event event = queue.poll();
        Assert.assertEquals(event, new WriteLine(line));
        System.out.println(event);
    }

    public void checkStep(String name) {
        final Event event = queue.poll();
        Assert.assertEquals(event, new Step(name));
        System.out.println(event);
    }

    void checkFinish() {
        Assert.assertTrue(queue.isEmpty());
    }

    static ConsoleScenarioBuilder builder() {
        return new ConsoleScenarioBuilder();
    }

    public static class ConsoleScenarioBuilder {

        private final List<Event> events = new LinkedList<>();

        ConsoleScenarioBuilder() {
        }

        public ConsoleScenarioBuilder type(String line) {
            events.add(new TypeLine(line));
            return this;
        }

        public ConsoleScenarioBuilder read(String line) {
            events.add(new WriteLine(line));
            return this;
        }

        public ConsoleScenarioBuilder step(String name) {
            events.add(new Step(name));
            return this;
        }

        public ConsoleScenario build() {
            return new ConsoleScenario(events);
        }
    }

    interface Event {
    }

    static class TypeLine implements Event {
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
            return "!! " + name;
        }
    }
}
