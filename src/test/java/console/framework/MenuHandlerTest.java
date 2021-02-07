package console.framework;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MenuHandlerTest {

    final MenuHandler menuHandler = new MenuHandler(
            "options:",
            List.of(
                    "sOme",
                    "zone",
                    "hoMe",
                    "done"
            )
    );

    @Test(expected = IllegalArgumentException.class)
    public void errorIfThereAreDuplicatedOptions() {
        new MenuHandler("options:", List.of("aaa", "aaa"));
    }

    @Test
    public void chooseOptionByNumber() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("2")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("hoMe", option);

        scenario.checkFinish();
    }

    @Test
    public void chooseOptionByExactMatching() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("?some")
                .read("confirm option '[0] - 'sOme'' (yes)")
                .type("yes")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("sOme", option);

        scenario.checkFinish();
    }

    @Test
    public void chooseOptionByExactMatchingAndDecline() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("?some")
                .read("confirm option '[0] - 'sOme'' (yes)")
                .type("no")
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("2")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("hoMe", option);

        scenario.checkFinish();
    }

    @Test
    public void enterInvalidOptionNumber() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
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
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
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
    public void enteringOfQueryWithoutMatchedOptionsCauseQueryReset() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("?irrelevant")
                .read("no options for this query")
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("3")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("done", option);

        scenario.checkFinish();
    }

    @Test
    public void enteringOfQueryWithMatchedOptionsCausesHidingOfUnmatchedOptions() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("?om")
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[2] - 'hoMe'")
                .read("enter option number to choose or ?query to filter options")
                .type("0")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("sOme", option);

        scenario.checkFinish();
    }

    @Test
    public void hiddenOptionCouldBeChosen() {
        final ConsoleScenario scenario = ConsoleScenario.builder()
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[1] - 'zone'")
                .read("[2] - 'hoMe'")
                .read("[3] - 'done'")
                .read("enter option number to choose or ?query to filter options")
                .type("?om")
                .read("options:")
                .read("[0] - 'sOme'")
                .read("[2] - 'hoMe'")
                .read("enter option number to choose or ?query to filter options")
                .type("1")
                .build();

        final String option = menuHandler.handle(scenario::readLine, scenario::writeLine);

        Assert.assertEquals("zone", option);

        scenario.checkFinish();
    }
}
