package console.framework;

import java.util.List;

public interface Command {

    String getInvite();

    List<Argument<?>> getArguments();

    void run(ConsoleWriter writer, ArgumentAccessor argumentAccessor);

}
