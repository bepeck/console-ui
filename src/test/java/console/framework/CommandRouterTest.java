package console.framework;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static console.framework.ConsoleScenario.builder;
import static org.junit.Assert.fail;

public class CommandRouterTest {

    @Test
    public void chooseAndRunCommandTest() {
        final ConsoleScenario scenario = builder()
                .read("enter 0 to command 1")
                .read("enter 1 to command 2")
                .type("1")
                .step("perform command")
                .build();

        final ConsoleReader reader = scenario::readLine;
        final ConsoleWriter writer = scenario::writeLine;

        final Command<?> command1 = newCommand("command 1");
        final Command<?> command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                new CommandHandler() {
                    @Override
                    public <ARGS> void handleCommand(ConsoleReader r, ConsoleWriter w, Command<ARGS> command) {
                        Assert.assertSame(reader, r);
                        Assert.assertSame(writer, w);
                        Assert.assertSame(command2, command);

                        scenario.checkStep("perform command");
                    }
                }
        );

        commandRouter.handleCommands(
                reader,
                writer
        );

        scenario.checkFinish();
    }

    @Test
    public void chooseNonExistingCommandTest() {
        final ConsoleScenario scenario = builder()
                .read("enter 0 to command 1")
                .read("enter 1 to command 2")
                .type("2")
                .read("wrong command number")
                .type("0")
                .step("perform command")
                .build();

        final Command<?> command1 = newCommand("command 1");
        final Command<?> command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                new CommandHandler() {
                    @Override
                    public <ARGS> void handleCommand(ConsoleReader r, ConsoleWriter w, Command<ARGS> command) {
                        Assert.assertSame(command1, command);

                        scenario.checkStep("perform command");
                    }
                }
        );

        commandRouter.handleCommands(
                scenario::readLine,
                scenario::writeLine
        );

        scenario.checkFinish();
    }

    @Test
    public void enterInvalidStringAsCommandNumber() {
        final ConsoleScenario scenario = builder()
                .read("enter 0 to command 1")
                .read("enter 1 to command 2")
                .type("not a number")
                .read("can't read command number")
                .type("0")
                .step("perform command")
                .build();

        final Command<?> command1 = newCommand("command 1");
        final Command<?> command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                new CommandHandler() {
                    @Override
                    public <ARGS> void handleCommand(ConsoleReader r, ConsoleWriter w, Command<ARGS> command) {
                        Assert.assertSame(command1, command);

                        scenario.checkStep("perform command");
                    }
                }
        );

        commandRouter.handleCommands(
                scenario::readLine,
                scenario::writeLine
        );

        scenario.checkFinish();
    }

    @Test
    public void handleCommandException() {
        final ConsoleScenario scenario = builder()
                .read("enter 0 to command 1")
                .read("enter 1 to command 2")
                .type("0")
                .step("perform command")
                .read("failed: command is failed")
                .build();

        final Command<?> command1 = newCommand("command 1");
        final Command<?> command2 = newCommand("command 2");
        final CommandRouter commandRouter = new CommandRouter(
                List.of(
                        command1,
                        command2
                ),
                new CommandHandler() {
                    @Override
                    public <ARGS> void handleCommand(ConsoleReader r, ConsoleWriter w, Command<ARGS> command) {
                        Assert.assertSame(command1, command);

                        scenario.checkStep("perform command");

                        throw new RuntimeException("command is failed");
                    }
                }
        );

        try {
            commandRouter.handleCommands(
                    scenario::readLine,
                    scenario::writeLine
            );
        } catch (Exception e) {
            Assert.assertEquals("command is failed", e.getMessage());
            Assert.assertEquals(RuntimeException.class, e.getClass());
        }

        scenario.checkFinish();
    }

    @Test
    public void doNotAllowDuplicatedInvites() {
        try {
            new CommandRouter(
                    List.of(
                            newCommand("command 1"),
                            newCommand("command 1")
                    ),
                    new CommandHandler() {
                        @Override
                        public <ARGS> void handleCommand(ConsoleReader r, ConsoleWriter w, Command<ARGS> command) {
                            throw new UnsupportedOperationException();
                        }
                    }
            );
            fail("noway");
        } catch (final IllegalArgumentException e) {
            Assert.assertEquals("duplicated invites", e.getMessage());
        }
    }

    private Command<?> newCommand(String name) {
        return new Command<Void>() {
            @Override
            public String getInvite() {
                return name;
            }

            @Override
            public List<Argument<Void>> getArguments() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void run(final ConsoleWriter writer, final Void args) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Void newArgumentCollector() {
                throw new UnsupportedOperationException();
            }
        };
    }
}