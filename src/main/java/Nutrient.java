public class Nutrient {
    String Name;
    Double Value;
    String LabelValue;
    String DailyValue;
    Integer Ordinal;
    public String toString() {
        return Name + ", " + Value + ", " + LabelValue + ", " + DailyValue + ", " + Ordinal;
    }
}
