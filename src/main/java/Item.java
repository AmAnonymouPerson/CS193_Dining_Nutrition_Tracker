import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Item {
    ArrayList<Nutrient> Nutrition;
    String Ingredients;
    String ID;
    String Name;
    Boolean IsVegetarian;
    Boolean NutritionReady;
    ArrayList<Allergen> Allergens;
    boolean isUpdated;
    public void updateItem(Gson parser, HttpClient client) throws IOException, InterruptedException {
        if (isUpdated) {
            return;
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String link = "https://api.hfs.purdue.edu/menus/v2/items/" + ID;
        builder.uri(URI.create(link));
        builder.GET();
        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Item item = parser.fromJson(response.body(), Item.class);
            this.Nutrition = item.Nutrition;
            this.Ingredients = item.Ingredients;
            isUpdated = true;
        } else {
            throw new IOException("Request failed: " + response.statusCode());
        }
    }
    public void printNutritionData() {
        System.out.println("Item: " + Name);
        for (int i = 0; i < Nutrition.size(); i++) {
            if (Nutrition.get(i) != null) {
                System.out.println((i + 1) + ": " + Nutrition.get(i).toString());
            }
        }
    }
    public String getServingSize() {
        for (Nutrient nutrient : Nutrition) {
            if (nutrient.Name.equalsIgnoreCase("Serving Size")) {
                return nutrient.LabelValue;
            }
        }
        return null;
    }
}