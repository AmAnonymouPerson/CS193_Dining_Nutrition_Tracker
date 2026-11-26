public class Nutrient {
    String Name;
    Double Value;
    String LabelValue;
    String DailyValue;
    Integer Ordinal;
    boolean nutritionReady = true;
    public String toString() {
        return Name + ", " + Value + ", " + LabelValue + ", " + DailyValue + ", " + Ordinal;
    }
    public boolean equals(Object o) {
        if (o instanceof Nutrient n) {
            return n.Name.equalsIgnoreCase(this.Name);
        }
        return false;
    }
    public void updateLabel() {
        switch (Name) {
            case "Calories", "Calories from fat" -> {
                    LabelValue = "kcal";
            }
            case "Total fat", "Saturated fat", "Total Carbohydrate",
                 "Sugar", "Added Sugar", "Dietary Fiber", "Protein" -> {
                LabelValue = "g";
            }
            case "Cholesterol", "Sodium", "Calcium", "Iron" -> {
                    LabelValue = "mg";
            }
            default -> {}
        }
    }
}