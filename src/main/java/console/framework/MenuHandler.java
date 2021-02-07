package console.framework;

import java.util.List;

public class MenuHandler {

    final List<String> options;

    public MenuHandler(
            final List<String> options
    ) {
        this.options = options;
    }

    public String handle(final ConsoleReader reader, final ConsoleWriter writer) {
        String query = "";

        while (true) {
            final String queryLower = query.toLowerCase();
            writer.writeLine("options:");
            int availableOptions = 0;
            int exactMatch = -1;
            for (int i = 0; i < options.size(); i++) {
                final String option = options.get(i);
                final String optionLower = option.toLowerCase();
                if (optionLower.contains(queryLower)) {
                    writer.writeLine(i + " " + option);
                    availableOptions++;
                    if (optionLower.equals(queryLower)) {
                        exactMatch = i;
                    }
                }
            }
            if (exactMatch != -1) {
                final String option = options.get(exactMatch);
                writer.writeLine("confirm option '" + option + "' (yes)");
                if (reader.readLine().equalsIgnoreCase("yes")) {
                    return option;
                }
            }
            if (availableOptions == 0) {
                writer.writeLine("no options for this query");
                query = "";
                continue;
            }
            while (true) {
                writer.writeLine("enter option number or option ?name to choose it");
                final String line = reader.readLine();
                if (line.startsWith("?")) {
                    query = line.substring(1);
                    break;
                } else {
                    try {
                        final int number = Integer.parseInt(line);
                        if (number >= 0 && number < options.size()) {
                            return options.get(number);
                        }
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                    writer.writeLine("invalid option number");
                }
            }
        }
    }
}
