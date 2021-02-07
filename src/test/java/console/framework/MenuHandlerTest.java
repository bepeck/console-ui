package console.framework;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MenuHandlerTest {

    final MenuHandler menuHandler = new MenuHandler("options:", List.of("some", "zone", "home", "done"));

    @Test
    public void chooseOption() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'some'")
                .read("[1] - 'zone'")
                .read("[2] - 'home'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("2")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("home", option);

        scenario.checkFinish();
    }

    @Test
    public void enterInvalidOptionNumber() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'some'")
                .read("[1] - 'zone'")
                .read("[2] - 'home'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("100500")
                .read("invalid option number")
                .read("enter option number to choose or ?query to filter options")
                .type("1")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("zone", option);

        scenario.checkFinish();
    }

    @Test
    public void enterNotNumberOption() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'some'")
                .read("[1] - 'zone'")
                .read("[2] - 'home'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("not_number_option")
                .read("invalid option number")
                .read("enter option number to choose or ?query to filter options")
                .type("1")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("zone", option);

        scenario.checkFinish();
    }

    @Test
    public void enterQueryWithoutMatchedOptionsCauseQueryReset() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'some'")
                .read("[1] - 'zone'")
                .read("[2] - 'home'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("?irrelevant")
                .read("no options for this query")
                .read("options:")
                .read("[0] - 'some'")
                .read("[1] - 'zone'")
                .read("[2] - 'home'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("3")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("done", option);

        scenario.checkFinish();
    }
}
