import java.util.ArrayList;

public class Meals {
    String ID;
    String Name;
    Integer Order;
    String Status;
    String Type;
    String Notes;
    ArrayList<Stations> Stations;
    public void printMenu() {
        for (int i = 0; i < Stations.size(); i++) {
            Stations station = Stations.get(i);
            char label = (char) ('a' + i);
            System.out.println("Station '" + label + "': " + station.Name);
            for (int j = 0; j < station.Items.size(); j++) {
                System.out.println("Item " + (j + 1) + ": " + station.Items.get(j).Name);
            }
        }
    }
}
