package console.framework;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        new CommandHandlerImpl().handleCommand(
                scenario::readLine,
                scenario::writeLine,
                new Command<HappyPathArgs>() {

                    @Override
                    public String getInvite() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public List<Argument<HappyPathArgs>> getArguments() {
                        return List.of(
                                new Argument<>() {

                                    @Override
                                    public String getInvite() {
                                        return "hui";
                                    }

                                    @Override
                                    public void convert(
                                            final ConsoleReader reader,
                                            final HappyPathArgs args
                                    ) {
                                        final String line = reader.readLine();
                                        assertEquals("hui", line);
                                        args.hui = true;
                                    }
                                },
                                new Argument<>() {

                                    @Override
                                    public String getInvite() {
                                        return "0 or 1";
                                    }

                                    @Override
                                    public void convert(
                                            final ConsoleReader reader,
                                            final HappyPathArgs args
                                    ) {
                                        final String line = reader.readLine();
                                        assertEquals("0", line);
                                        args.zeroOrOne = Integer.parseInt(line);
                                    }
                                }
                        );
                    }

                    @Override
                    public void run(final ConsoleWriter writer, final HappyPathArgs args) {
                        scenario.checkStep("invoke command");

                        assertTrue(args.hui);
                        assertEquals(0, args.zeroOrOne);

                        writer.writeLine("command invoked");
                    }

                    @Override
                    public HappyPathArgs newArgumentCollector() {
                        return new HappyPathArgs();
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

        new CommandHandlerImpl().handleCommand(
                scenario::readLine,
                scenario::writeLine,
                new Command<EnterInvalidValueArgs>() {

                    @Override
                    public String getInvite() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public List<Argument<EnterInvalidValueArgs>> getArguments() {
                        return List.of(
                                new Argument<>() {
                                    @Override
                                    public String getInvite() {
                                        return "0";
                                    }

                                    @Override
                                    public void convert(
                                            final ConsoleReader reader,
                                            final EnterInvalidValueArgs args
                                    ) throws ArgumentCaptureException {
                                        final String line = reader.readLine();
                                        if (line.equals("0")) {
                                            args.zero = line;
                                        } else {
                                            throw new ArgumentCaptureException("0 is expected");
                                        }
                                    }
                                }
                        );
                    }

                    @Override
                    public EnterInvalidValueArgs newArgumentCollector() {
                        return new EnterInvalidValueArgs();
                    }

                    @Override
                    public void run(final ConsoleWriter writer, final EnterInvalidValueArgs args) {
                        scenario.checkStep("invoke command");

                        assertEquals("0", args.zero);

                        writer.writeLine("command invoked");
                    }
                }
        );

        scenario.checkFinish();
    }

    static class HappyPathArgs {
        boolean hui;
        int zeroOrOne;
    }

    static class EnterInvalidValueArgs {
        String zero;
    }
}