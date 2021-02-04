package console.framework;

import java.util.List;

public interface Command<ARGS> {

    String getInvite();

    List<Argument<ARGS>> getArguments();

    void run(ConsoleWriter writer, ARGS args);

    ARGS newArgumentCollector();
}
