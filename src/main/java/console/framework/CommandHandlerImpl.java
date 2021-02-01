package console.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandlerImpl implements CommandHandler {
    @Override
    public void handleCommand(final ConsoleReader reader, final ConsoleWriter writer, final Command command) {
        final List<Argument<?>> arguments = command.getArguments();
        final Map<Object, Object> argumentValues = new HashMap<>();
        for (final Argument<?> argument : arguments) {
            while (true) {
                printArgumentInvite(writer, argument);
                try {
                    argument.convert(reader, argumentValues::put);
                    break;
                } catch (final ArgumentCaptureException e) {
                    printInvalidArgumentError(writer, e);
                }
            }
        }
        command.run(writer, argumentValues::get);
    }

    private void printInvalidArgumentError(final ConsoleWriter writer, final ArgumentCaptureException e) {
        writer.writeLine("wrong argument value: " + e.getMessage());
    }

    private void printArgumentInvite(final ConsoleWriter writer, final Argument<?> argument) {
        writer.writeLine("enter " + argument.getInvite());
    }
}
