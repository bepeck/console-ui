package console.framework;

import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConsoleScenario {

    private final Queue<Event> queue = new LinkedList<>();

    ConsoleScenario(final List<Event> events) {
        queue.addAll(events);
    }

    public String readLine() {
        final Event event = queue.poll();
        if (!(event instanceof UserEnteredLineEvent)) {
            Assert.fail("expected line reading but got " + event);
        }
        final String line = ((UserEnteredLineEvent) event).line;
        System.out.println(event);
        return line;
    }

    public void writeLine(String line) {
        final Event event = queue.poll();
        assertEquals(event, new AppPrintedLineEvent(line));
        System.out.println(event);
    }

    public void checkStep(String name) {
        final Event event = queue.poll();
        assertEquals(event, new StepEvent(name));
        System.out.println(event);
    }

    void checkFinish() {
        assertTrue(queue.isEmpty());
    }

    static ConsoleScenarioBuilder builder() {
        return new ConsoleScenarioBuilder();
    }

    public static class ConsoleScenarioBuilder {

        private final List<Event> events = new LinkedList<>();

        ConsoleScenarioBuilder() {
        }

        public ConsoleScenarioBuilder type(String line) {
            events.add(new UserEnteredLineEvent(line));
            return this;
        }

        public ConsoleScenarioBuilder read(String line) {
            events.add(new AppPrintedLineEvent(line));
            return this;
        }

        public ConsoleScenarioBuilder step(String name) {
            events.add(new StepEvent(name));
            return this;
        }

        public ConsoleScenario build() {
            return new ConsoleScenario(events);
        }
    }

    interface Event {
    }

    static class UserEnteredLineEvent implements Event {
        final String line;

        UserEnteredLineEvent(String line) {
            this.line = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserEnteredLineEvent readLine = (UserEnteredLineEvent) o;
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

    static class AppPrintedLineEvent implements Event {
        final String line;

        AppPrintedLineEvent(String line) {
            this.line = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppPrintedLineEvent writeLine = (AppPrintedLineEvent) o;
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

    static class StepEvent implements Event {
        final String name;

        StepEvent(String line) {
            this.name = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StepEvent step = (StepEvent) o;
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
