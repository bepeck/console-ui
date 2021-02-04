package console.framework;

import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CommandHandlerTest {

    @Test
    public void happyPath() {
        ConsoleScenario scenario = ConsoleScenario.builder()
                .read("enter hui")
                .type("hui")
                .read("enter 0 or 1")
                .type("0")
                .step("invoke command")
                .read("command invoked")
                .build();

        final String key0 = UUID.randomUUID().toString();
        final String key1 = UUID.randomUUID().toString();

        new CommandHandlerImpl().handleCommand(
                scenario::readLine,
                scenario::writeLine,
                new Command() {

                    @Override
                    public String getInvite() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public List<Argument<?>> getArguments() {
                        return List.of(
                                new Argument<Boolean>() {

                                    @Override
                                    public String getInvite() {
                                        return "hui";
                                    }

                                    @Override
                                    public void convert(
                                            final ConsoleReader reader,
                                            final ArgumentCollector argumentCollector
                                    ) {
                                        final String line = reader.readLine();
                                        assertEquals("hui", line);
                                        argumentCollector.add(key0, true);
                                    }

                                    @Override
                                    public Boolean resolve(final ArgumentAccessor arguments) {
                                        throw new UnsupportedOperationException();
                                    }
                                },
                                new Argument<Integer>() {

                                    @Override
                                    public String getInvite() {
                                        return "0 or 1";
                                    }

                                    @Override
                                    public void convert(
                                            final ConsoleReader reader,
                                            final ArgumentCollector argumentCollector
                                    ) {
                                        final String line = reader.readLine();
                                        assertEquals("0", line);
                                        argumentCollector.add(key1, Integer.parseInt(line));
                                    }

                                    @Override
                                    public Integer resolve(final ArgumentAccessor arguments) {
                                        throw new UnsupportedOperationException();
                                    }
                                }
                        );
                    }

                    @Override
                    public void run(final ConsoleWriter writer, final ArgumentAccessor argumentAccessor) {
                        scenario.checkStep("invoke command");

                        assertEquals(true, argumentAccessor.get(key0));
                        assertEquals(0, argumentAccessor.get(key1));

                        writer.writeLine("command invoked");
                    }
                }
        );

        scenario.checkFinish();
    }

    @Test
    public void enterInvalidValue() {
        ConsoleScenario scenario = ConsoleScenario.builder()
                .read("enter 0")
                .type("hui")
                .read("wrong argument value: 0 is expected")
                .read("enter 0")
                .type("0")
                .step("invoke command")
                .read("command invoked")
                .build();

        final String key1 = UUID.randomUUID().toString();

        new CommandHandlerImpl().handleCommand(
                scenario::readLine,
                scenario::writeLine,
                new Command() {

                    @Override
                    public String getInvite() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public List<Argument<?>> getArguments() {
                        return List.of(
                                new Argument<String>() {

                                    @Override
                                    public String getInvite() {
                                        return "0";
                                    }

                                    @Override
                                    public void convert(
                                            final ConsoleReader reader,
                                            final ArgumentCollector argumentCollector
                                    ) throws ArgumentCaptureException {
                                        final String line = reader.readLine();
                                        if (line.equals("0")) {
                                            argumentCollector.add(key1, line);
                                        } else {
                                            throw new ArgumentCaptureException("0 is expected");
                                        }
                                    }

                                    @Override
                                    public String resolve(final ArgumentAccessor arguments) {
                                        throw new UnsupportedOperationException();
                                    }
                                }
                        );
                    }

                    @Override
                    public void run(final ConsoleWriter writer, final ArgumentAccessor argumentAccessor) {
                        scenario.checkStep("invoke command");

                        assertEquals("0", argumentAccessor.get(key1));

                        writer.writeLine("command invoked");
                    }
                }
        );

        scenario.checkFinish();
    }
}