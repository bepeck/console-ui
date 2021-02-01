package console.example;

import console.framework.Argument;
import console.framework.ArgumentAccessor;
import console.framework.Command;

import java.io.PrintStream;
import java.util.List;

public class SayHelloCommand implements Command {
    @Override
    public String getInvite() {
        return "say hello (no args)";
    }

    @Override
    public List<Argument<?>> getArguments() {
        return List.of();
    }

    @Override
    public void run(PrintStream out, ArgumentAccessor argumentAccessor) {
        out.println("Hello!");
    }
}
