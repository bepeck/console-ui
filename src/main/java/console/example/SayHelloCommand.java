package console.example;

import console.framework.Argument;
import console.framework.Command;
import console.framework.ConsoleWriter;

import java.util.List;

public class SayHelloCommand implements Command<Void> {
    @Override
    public String getInvite() {
        return "say hello (no args)";
    }

    @Override
    public List<Argument<Void>> getArguments() {
        return List.of();
    }

    @Override
    public void run(final ConsoleWriter writer, final Void args) {
        writer.writeLine("Hello!");
    }

    @Override
    public Void newArgumentCollector() {
        return null;
    }
}
