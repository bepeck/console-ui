package console.example;

import console.framework.MenuHandler;

import java.util.List;
import java.util.Scanner;

public class MenuHandlerShowCase {
    public static void main(final String[] args) {
        final MenuHandler menuHandler = new MenuHandler(List.of("some", "zone", "home", "done"));
        final String option = menuHandler.handle(new Scanner(System.in)::nextLine, System.out::println);
        System.out.println("chosen: " + option);
    }
}
