import com.google.gson.Gson;

import java.time.LocalDate;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String[] locations = {"Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor"};
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < locations.length; i++) {
            System.out.println((i + 1) + ": " + locations[i]);
        }
        String locationName;
        System.out.println("Enter the number for the location you want to choose");
        while (true) {
            String userInput = scanner.nextLine();
            try {
                locationName = locations[Integer.parseInt(userInput) - 1];
                break;
            } catch (NumberFormatException e) {
                System.out.println("Not a number");
            } catch (NullPointerException f) {
                System.out.println("Enter a number");
            } catch (ArrayIndexOutOfBoundsException g) {
                System.out.println("Invalid Index");
            }
        }
        LocalDate date;
        while (true) {
            System.out.println("Enter a date in the format yyyy-MM-dd");
            String response = scanner.nextLine();
            try {
                date = LocalDate.parse(response);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid Date");
            }
        }
        Gson parser = new Gson();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String link = "https://api.hfs.purdue.edu/menus/v2/locations/" + locationName + "/" + date.toString();
        builder.uri(URI.create(link));
        builder.GET();
        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Location location = parser.fromJson(response.body(), Location.class);
            boolean oneOpenMeal = false;
            if (location.Meals != null) {
                for (int i = 0; i < location.Meals.size(); i++) {
                    //Check for if a meal is null
                    Meals meal = location.Meals.get(i);
                    System.out.println((i + 1) + ": " + meal.Name + " (" + meal.Status + ")");
                    if (meal.Status.equals("Open")) {
                        oneOpenMeal = true;
                    }
                }
            }
            Meals meal;
            if (!oneOpenMeal) {
                System.out.println("No Open Meals");
            } else {
                while (true) {
                    System.out.println("Type a number corresponding to the chosen meal");
                    String userInput = scanner.nextLine();
                    try {
                        meal = location.Meals.get(Integer.parseInt(userInput) - 1);
                        if (!meal.Status.equals("Open")) {
                            System.out.println("This meal session is closed. Pick another one");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Not a number");
                    } catch (NullPointerException f) {
                        System.out.println("Enter a number");
                    } catch (IndexOutOfBoundsException g) {
                        System.out.println("Invalid Index");
                    }
                }
                System.out.println("Menu: ");
                meal.printMenu();

                Item item;
                while (true) {
                    System.out.println("Select an item to see the nutrition information of.");
                    System.out.println("Type the station character followed by item number.");
                    System.out.println("'EXIT' to close the application and 'print' to print the menu again");
                    String userInput = scanner.nextLine();
                    if (userInput.equalsIgnoreCase("exit")) {
                        break;
                    } else if (userInput.equalsIgnoreCase("print")) {
                        meal.printMenu();
                        continue;
                    }
                    try {
                        int stationNumber = userInput.charAt(0) - 'a';
                        int itemNumber = Integer.parseInt(userInput.substring(1)) - 1;
                        item = meal.Stations.get(stationNumber).Items.get(itemNumber);
                    } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e) {
                        System.out.println("Invalid Input");
                        continue;
                    }
                    try {
                        item.updateItem(parser, client);
                        if (item.Nutrition == null) {
                            System.out.println("No nutrition data.");
                            continue;
                        }
                    } catch (IOException | InterruptedException e) {
                        System.out.println("IO exception. There was a network issue this time.");
                        continue;
                    }
                    item.printNutritionData();
                    //Change it so that it breaks only when the user enters exit.
                }
            }
        } else {
            System.out.println(response.statusCode());
        }
        scanner.close();
    }
}
