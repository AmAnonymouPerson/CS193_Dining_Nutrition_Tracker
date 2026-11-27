import com.google.gson.Gson;

import java.time.LocalDate;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String[] locations = {"Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor"};

        String locationName = selectLocation(scanner, locations);

        LocalDate date = selectDate(scanner);

        Gson parser = new Gson();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String link = "https://api.hfs.purdue.edu/menus/v2/locations/" + locationName + "/" + date;
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
                    Meal meal = location.Meals.get(i);
                    System.out.println((i + 1) + ": " + meal.Name + " (" + meal.Status + ")");
                    if (meal.Status.equals("Open")) {
                        oneOpenMeal = true;
                    }
                }
            }
            if (!oneOpenMeal) {
                System.out.println("No Open Meals");
            } else {
                Meal meal = selectMeal(scanner, location);

                meal.printMenu();
                ItemList itemList = new ItemList();
                while (true) {
                    System.out.println("Type 'options' to see options.");
                    String userInput = scanner.nextLine();
                    switch (userInput.toLowerCase()) {
                        case "options" -> {
                            System.out.println("Type 'view' to view an item's nutritional information.");
                            System.out.println("Type 'add' to add an item to your planned meal.");
                            System.out.println("Type 'print' to print the menu.");
                            System.out.println("Type 'exit' to close the application.");
                            System.out.println("Type 'meal' to see your meal so far.");
                            System.out.println("Type 'nutrition' to show your meal's nutrition.");
                            System.out.println("Type 'remove' to remove an item from your meal.");
                            System.out.println("Type 'servings' to change the servings of an item in your meal.");
                            //System.out.println("Type 'back' to go back to selecting a meal");
                        }
                        case "print" -> {
                            meal.printMenu();
                        }
                        case "exit" -> {
                            return;
                        } case "view" -> {
                            Item item = selectItem(scanner, meal);
                            try {
                                item.updateItem(parser, client);
                                if (item.Nutrition == null) {
                                    System.out.println("No nutrition data.");
                                } else {
                                    item.printNutritionData();
                                }
                            } catch (IOException e) {
                                System.out.println("IO exception. There was a network issue this time.");
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }
                        } case "add" -> {
                            Item item = selectItem(scanner, meal);
                            try {
                                item.updateItem(parser, client);
                                if (item.Nutrition == null) {
                                    System.out.println("No nutrition data. It will still be added to your meal.");
                                }
                            } catch (IOException e) {
                                System.out.println("IO exception. There was a network issue this time.");
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }
                            if (item.getServingSize() != null) {
                                double servings = selectServings(scanner, item);
                                itemList.addItem(item, servings, item.getServingSize());
                            } else {
                                System.out.println("This item has incomplete serving size information");
                                itemList.addItem(item, 1, item.getServingSize());
                            }
                        }
                        case "remove" -> {
                            if (itemList.itemEntries.isEmpty()) {
                                System.out.println("No items added yet.");
                                continue;
                            }
                            int i = selectItemEntryIndex(scanner, itemList);
                            itemList.itemEntries.remove(i);
                        }
                        case "servings" -> {
                            int i = selectItemEntryIndex(scanner, itemList);
                            ItemEntry itemEntry = itemList.itemEntries.get(i);
                            itemEntry.servings = selectServings(scanner, itemEntry.item);
                        }
                        case "nutrition" -> {
                            itemList.updateCalculations();
                            itemList.printListNutrition();
                        }
                        case "meal" -> {
                            itemList.printList();
                        }
                        default -> {
                            System.out.println("Unrecognized command");
                        }
                    }

                    //Change it so that it breaks only when the user enters exit.
                }
            }
        } else {
            System.out.println(response.statusCode());
        }
        scanner.close();
    }
    public static String selectLocation(Scanner scanner, String[] locations) {
        //Possibly tidy up the user input flow
        for (int i = 0; i < locations.length; i++) {
            System.out.println((i + 1) + ": " + locations[i]);
        }
        String locationName;
        System.out.println("Enter the number for the location you want to choose");
        while (true) {
            String userInput = scanner.nextLine();
            try {
                locationName = locations[Integer.parseInt(userInput) - 1];
                return locationName;
            } catch (NumberFormatException e) {
                System.out.println("Not a number");
            } catch (NullPointerException f) {
                System.out.println("Enter a number");
            } catch (ArrayIndexOutOfBoundsException g) {
                System.out.println("Invalid Index");
            }
        }
    }
    public static LocalDate selectDate(Scanner scanner) {
        while (true) {
            System.out.println("Enter a date in the format yyyy-MM-dd");
            String response = scanner.nextLine();
            try {
                return LocalDate.parse(response);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid Date");
            }
        }
    }
    public static Meal selectMeal(Scanner scanner, Location location) {
        while (true) {
            System.out.println("Type a number corresponding to the chosen meal");
            String userInput = scanner.nextLine();
            try {
                Meal meal = location.Meals.get(Integer.parseInt(userInput) - 1);
                if (meal == null || !meal.Status.equals("Open")) {
                    System.out.println("This meal session is closed. Pick another one");
                    continue;
                }
                return meal;
            } catch (NumberFormatException e) {
                System.out.println("Not a number");
            } catch (NullPointerException f) {
                System.out.println("Enter a number");
            } catch (IndexOutOfBoundsException g) {
                System.out.println("Invalid Index");
            }
        }
    }
    public static Item selectItem(Scanner scanner, Meal meal) {
        while (true) {
            System.out.println("Type the station number followed by the item number. Example: a2");
            String userInput = scanner.nextLine();
            try {
                int stationNumber = userInput.charAt(0) - 'a';
                int itemNumber = Integer.parseInt(userInput.substring(1)) - 1;
                return meal.Stations.get(stationNumber).Items.get(itemNumber);
            } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e) {
                System.out.println("Invalid Input");
            }

        }
    }
    public static double selectServings(Scanner scanner, Item item) {
        while (true) {
            String servingSize = item.getServingSize();
            System.out.println("This item has a serving size of '" + servingSize +"'.");
            System.out.println("Type the amount of servings you'd want.");
            String userInput = scanner.nextLine();
            try {
                return Double.parseDouble(userInput);
            } catch (NumberFormatException e) {
                System.out.println("Not a number.");
            } catch (NullPointerException e) {
                System.out.println("Enter a number.");
            }
        }
    }
    public static int selectItemEntryIndex(Scanner scanner, ItemList itemList) {
        ArrayList<ItemEntry> itemEntries = itemList.itemEntries;
        while (true) {
            itemList.printList();
            System.out.println("Type a number corresponding to the item you want to remove");
            String userInput = scanner.nextLine();
            try {
                int i = Integer.parseInt(userInput);
                ItemEntry itemEntry = itemEntries.get(i - 1);
                return i;
            } catch (NumberFormatException e) {
                System.out.println("Not a number");
            } catch (NullPointerException f) {
                System.out.println("Enter a number");
            } catch (IndexOutOfBoundsException g) {
                System.out.println("Invalid Index");
            }
        }

    }
}