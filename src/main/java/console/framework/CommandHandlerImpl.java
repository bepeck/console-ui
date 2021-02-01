package console.framework;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandHandlerImpl implements CommandHandler {
    @Override
    public void handleCommand(final Scanner in, final PrintStream out, final Command command) {
        final List<Argument<?>> arguments = command.getArguments();
        final Map<Object, Object> argumentValues = new HashMap<>();
        for (final Argument<?> argument : arguments) {
            while (true) {
                printArgumentInvite(out, argument);
                try {
                    argument.convert(in, argumentValues::put);
                    break;
                } catch (final ArgumentCaptureException e) {
                    printInvalidArgumentError(out, e);
                }
            }
        }
        command.run(out, argumentValues::get);
    }

    private void printInvalidArgumentError(final PrintStream out, final ArgumentCaptureException e) {
        out.println("wrong argument value: " + e.getMessage());
    }

    private void printArgumentInvite(final PrintStream out, final Argument<?> argument) {
        out.println("enter " + argument.getInvite());
    }
}
