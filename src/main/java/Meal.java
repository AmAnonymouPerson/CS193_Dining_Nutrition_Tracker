import java.util.ArrayList;

public class Meal {
    String ID;
    String Name;
    Integer Order;
    String Status;
    String Type;
    Hours Hours;
    String Notes;
    ArrayList<Station> Stations;
    public void printMenu() {
        System.out.println("Menu: ");
        for (int i = 0; i < Stations.size(); i++) {
            Station station = Stations.get(i);
            char label = (char) ('a' + i);
            System.out.println("Station '" + label + "': " + station.Name);
            for (int j = 0; j < station.Items.size(); j++) {
                System.out.println("Item " + (j + 1) + ": " + station.Items.get(j).Name);
            }
        }
    }
}