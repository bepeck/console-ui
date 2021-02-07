package console.example;

import console.framework.MenuHandler;

import java.util.List;
import java.util.Scanner;

public class MenuHandlerShowCase {
    public static void main(String[] args) {
        new MenuHandler(List.of("хуй", "хуйня", "куй", "хаб")).handle(new Scanner(System.in)::nextLine, System.out::println);
    }
}
