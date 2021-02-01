package console.framework;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

public class CommandRouterTest {
    @Test
    public void chooseCommandTest() {
        final Scenario scenario = new Scenario();

        scenario.out("enter 0 to command 1");
        scenario.out("enter 1 to command 2");
        scenario.in("1");
        scenario.step("perform command");

        final Command command1 = newCommand("command 1");
        final Command command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                (reader, writer, command) -> {
                    Assert.assertSame(scenario.reader, reader);
                    Assert.assertSame(scenario.writer, writer);
                    Assert.assertSame(command2, command);

                    scenario.stepChecker.accept("perform command");
                }
        );

        commandRouter.handleCommands(
                scenario.reader,
                scenario.writer
        );

        scenario.checkFinish();
    }

    @Test
    public void chooseNonExistingCommandTest() {
        final Scenario scenario = new Scenario();

        scenario.out("enter 0 to command 1");
        scenario.out("enter 1 to command 2");
        scenario.in("2");
        scenario.out("wrong command number");
        scenario.in("0");
        scenario.step("perform command");

        final Command command1 = newCommand("command 1");
        final Command command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                (reader, writer, command) -> {
                    Assert.assertSame(scenario.reader, reader);
                    Assert.assertSame(scenario.writer, writer);
                    Assert.assertSame(command1, command);

                    scenario.stepChecker.accept("perform command");
                }
        );

        commandRouter.handleCommands(
                scenario.reader,
                scenario.writer
        );

        scenario.checkFinish();
    }

    @Test
    public void enterInvalidStringAsCommandNumber() {
        final Scenario scenario = new Scenario();

        scenario.out("enter 0 to command 1");
        scenario.out("enter 1 to command 2");
        scenario.in("ahaha");
        scenario.out("can't read command number");
        scenario.in("0");
        scenario.step("perform command");

        final Command command1 = newCommand("command 1");
        final Command command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                (reader, writer, command) -> {
                    Assert.assertSame(scenario.reader, reader);
                    Assert.assertSame(scenario.writer, writer);
                    Assert.assertSame(command1, command);

                    scenario.stepChecker.accept("perform command");
                }
        );

        commandRouter.handleCommands(
                scenario.reader,
                scenario.writer
        );

        scenario.checkFinish();
    }

    @Test
    public void handleCommandException() {
        final Scenario scenario = new Scenario();

        scenario.out("enter 0 to command 1");
        scenario.out("enter 1 to command 2");
        scenario.in("0");
        scenario.step("perform command");
        scenario.out("failed: command is failed");

        final Command command1 = newCommand("command 1");
        final Command command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                (reader, writer, command) -> {
                    Assert.assertSame(scenario.reader, reader);
                    Assert.assertSame(scenario.writer, writer);
                    Assert.assertSame(command1, command);

                    scenario.stepChecker.accept("perform command");

                    throw new RuntimeException("command is failed");
                }
        );

        try {
            commandRouter.handleCommands(
                    scenario.reader,
                    scenario.writer
            );
        } catch (Exception e) {
            Assert.assertEquals("command is failed", e.getMessage());
            Assert.assertEquals(RuntimeException.class, e.getClass());
        }

        scenario.checkFinish();
    }

    private Command newCommand(String name) {
        return new Command() {
            @Override
            public String getInvite() {
                return name;
            }

            @Override
            public List<Argument<?>> getArguments() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void run(ConsoleWriter writer, ArgumentAccessor argumentAccessor) {
                throw new UnsupportedOperationException();
            }
        };
    }

    static class Scenario {

        final Queue<Event> queue = new LinkedList<>();

        void in(String line) {
            queue.add(new ReadLine(line));
        }

        void out(String line) {
            queue.add(new WriteLine(line));
        }

        void step(String name) {
            queue.add(new Step(name));
        }

        final ConsoleReader reader = () -> {
            final Event event = queue.poll();
            if (!(event instanceof ReadLine)) {
                Assert.fail("expected line reading but got " + event);
            }
            final String line = ((ReadLine) event).line;
            System.out.println("<<" + line);
            return line;
        };

        final ConsoleWriter writer = (line) -> {
            final Event event = queue.poll();
            Assert.assertEquals(event, new WriteLine(line));
            System.out.println(">>" + line);
        };

        final Consumer<String> stepChecker = (name) -> {
            final Event event = queue.poll();
            Assert.assertEquals(event, new Step(name));
            System.out.println("!!" + name);
        };

        void checkFinish() {
            Assert.assertTrue("", queue.isEmpty());
        }
    }

    interface Event {

    }

    public static class ReadLine implements Event {
        public final String line;

        ReadLine(String line) {
            this.line = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReadLine readLine = (ReadLine) o;
            return Objects.equals(line, readLine.line);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line);
        }

        @Override
        public String toString() {
            return "ReadLine{" +
                    "line='" + line + '\'' +
                    '}';
        }
    }

    public static class WriteLine implements Event {
        public final String line;

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
            return "WriteLine{" +
                    "line='" + line + '\'' +
                    '}';
        }
    }

    public static class Step implements Event {
        public final String name;

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
            return "Step{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}