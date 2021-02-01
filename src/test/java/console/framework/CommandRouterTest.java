package console.framework;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CommandRouterTest {
    @Test
    public void chooseCommandTest() {
        final ConsoleScenario scenario = new ConsoleScenario();

        scenario.read("enter 0 to command 1");
        scenario.read("enter 1 to command 2");
        scenario.type("1");
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
        final ConsoleScenario scenario = new ConsoleScenario();

        scenario.read("enter 0 to command 1");
        scenario.read("enter 1 to command 2");
        scenario.type("2");
        scenario.read("wrong command number");
        scenario.type("0");
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
        final ConsoleScenario scenario = new ConsoleScenario();

        scenario.read("enter 0 to command 1");
        scenario.read("enter 1 to command 2");
        scenario.type("ahaha");
        scenario.read("can't read command number");
        scenario.type("0");
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
        final ConsoleScenario scenario = new ConsoleScenario();

        scenario.read("enter 0 to command 1");
        scenario.read("enter 1 to command 2");
        scenario.type("0");
        scenario.step("perform command");
        scenario.read("failed: command is failed");

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
}