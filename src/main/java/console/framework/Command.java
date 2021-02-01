package console.framework;

import java.io.PrintStream;
import java.util.List;

public interface Command {

    String getInvite();

    List<Argument<?>> getArguments();

    void run(PrintStream out, ArgumentAccessor argumentAccessor);

}
