import java.util.ArrayList;

public class ItemList {
    ArrayList<ItemEntry> itemEntries = new ArrayList<>();
    ArrayList<Nutrient> nutrients = new ArrayList<>();
    public void addItem(Item item, double servings, String servingSize) {
        ItemEntry itemEntry = new ItemEntry(item, servings, servingSize);
        itemEntries.add(itemEntry);
    }
    public void updateCalculations() {
        for (int i = 0; i < itemEntries.size(); i++) {
            ItemEntry itemEntry = itemEntries.get(i);
            Item item = itemEntry.item;
            for (int j = 0; j < item.Nutrition.size(); j++) {
                Nutrient itemNutrient = item.Nutrition.get(j);
                for (int k = 0; k < nutrients.size(); k++) {
                    Nutrient listNutrient = nutrients.get(k);
                    if (itemNutrient.equals(listNutrient)) {
                        if (itemNutrient.Value != null) {
                            listNutrient.Value += (itemEntry.servings * itemNutrient.Value);
                            break;
                        }
                    } else {
                        nutrients.add(itemNutrient);
                    }
                }
            }
        }
    }
    public void printList() {
        for (int i = 0; i < itemEntries.size(); i++) {
            ItemEntry itemEntry = itemEntries.get(i);
            String itemDescriptor = (i + 1) + ": " + itemEntry.item.Name;
            String servingDescriptor = (itemEntry.servingSize == null ?
                    " (No Serving size given)" :
                    " (" + itemEntry.servingSize + ") " + String.format("%.2f", itemEntry.servings));
            System.out.println(itemDescriptor + servingDescriptor);
        }
    }
    public void printListNutrition() {
        for (int i = 0; i < nutrients.size(); i++) {
            Nutrient nutrient = nutrients.get(i);
            nutrient.updateLabel();
            System.out.println((i + 1) + ": " + nutrient.Name + " (" + nutrient.LabelValue + ")");
        }
    }

}