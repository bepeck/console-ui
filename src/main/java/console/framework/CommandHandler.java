package console.framework;

import java.io.PrintStream;
import java.util.Scanner;

interface CommandHandler {
    void handleCommand(Scanner in, PrintStream out, Command command);
}
