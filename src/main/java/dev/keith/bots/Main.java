package dev.keith.bots;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    static {
        try {
            Class.forName("dev.keith.bots.Config");
            Class.forName("dev.keith.database.helpers.SQLHelper");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        if (!Objects.equals(Config.token, "")) {
            DiscordBot.startBot(Config.token);
        } else {
            try(Scanner s = new Scanner(System.in)) {
                System.out.print("Please insert token: ");
                DiscordBot.startBot(s.nextLine());
            }
        }
    }
}
