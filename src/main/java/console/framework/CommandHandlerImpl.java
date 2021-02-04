package console.framework;

import java.util.List;

public class CommandHandlerImpl implements CommandHandler {
    @Override
    public <ARGS> void handleCommand(final ConsoleReader reader, final ConsoleWriter writer, final Command<ARGS> command) {
        final List<Argument<ARGS>> arguments = command.getArguments();
        final ARGS args = command.newArgumentCollector();
        for (final Argument<ARGS> argument : arguments) {
            while (true) {
                printArgumentInvite(writer, argument);
                try {
                    argument.convert(reader, args);
                    break;
                } catch (final ArgumentCaptureException e) {
                    printInvalidArgumentError(writer, e);
                }
            }
        }
        command.run(writer, args);
    }

    private void printInvalidArgumentError(final ConsoleWriter writer, final ArgumentCaptureException e) {
        writer.writeLine("wrong argument value: " + e.getMessage());
    }

    private void printArgumentInvite(final ConsoleWriter writer, final Argument<?> argument) {
        writer.writeLine("enter " + argument.getInvite());
    }
}
