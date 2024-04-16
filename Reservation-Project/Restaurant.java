import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Restaurant {

    private static final String LAYOUT_FILE = "code/Reservation-Project/layout.txt";
    private static final String GUEST_FILE = "code/Reservation-Project/guest_info.txt";

    // Reads the layout from the file and returns it as a Map
    public static Map<String, String[]> readLayout() {
        Map<String, String[]> layout = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LAYOUT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                layout.put(parts[0], new String[]{parts[1], parts[2]});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return layout;
    }

    // Updates the layout file with the given layout Map
    public static void updateLayout(Map<String, String[]> layout) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LAYOUT_FILE))) {
            for (Map.Entry<String, String[]> entry : layout.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue()[0] + "," + entry.getValue()[1] + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addPartyToWaitlist(String name, int size, String contact) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GUEST_FILE, true))) {
            writer.write(name + "," + size + "," + contact + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readWaitlist() {
        List<String[]> waitlist = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(GUEST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] partyDetails = line.split(",");
                waitlist.add(partyDetails);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return waitlist;
    }

    // Seats a party from the waitlist
    public static void seatParty() {
        List<String[]> waitlist = readWaitlist();
        Map<String, String[]> layout = readLayout();

        if (waitlist.isEmpty()) {
            System.out.println("The waitlist is empty.");
            return;
        }

        Iterator<String[]> iterator = waitlist.iterator();
        while (iterator.hasNext()) {
            String[] party = iterator.next();
            String partyName = party[0];
            int partySize = Integer.parseInt(party[1]);

            for (String tableNumber : layout.keySet()) {
                String[] tableDetails = layout.get(tableNumber);
                int tableSize = Integer.parseInt(tableDetails[0]);
                String tableOccupant = tableDetails[1];

                if (tableSize >= partySize && tableOccupant.equals("null")) {
                    // Seat the party at this table
                    layout.put(tableNumber, new String[]{String.valueOf(tableSize), partyName});
                    updateLayout(layout);

                    // Remove the party from the waitlist
                    iterator.remove();
                    updateWaitlist(waitlist);

                    System.out.println("Seated party " + partyName + " at table " + tableNumber);
                    return;
                }
            }
        }

        System.out.println("No suitable table available for any party on the waitlist.");
    }

    // Updates the waitlist file with the given list of parties
    public static void updateWaitlist(List<String[]> waitlist) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GUEST_FILE))) {
            for (String[] party : waitlist) {
                writer.write(party[0] + "," + party[1] + "," + party[2] + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clears a table and marks it as unoccupied
    public static void clearTable(String tableNumber) {
        Map<String, String[]> layout = readLayout();

        if (layout.containsKey(tableNumber)) {
            String[] tableDetails = layout.get(String.valueOf(tableNumber));
            tableDetails[1] = "null"; // Mark the table as unoccupied
            layout.put(tableNumber, tableDetails);
            updateLayout(layout);

            System.out.println("Table " + tableNumber + " has been cleared.");
        } else {
            System.out.println("Table " + tableNumber + " does not exist.");
        }
    }

    // Returns a list of parties on the waitlist
    public static List<String> viewWaitlist() {
        List<String[]> waitlist = readWaitlist();
        List<String> waitlistInfo = new ArrayList<>();

        for (String[] party : waitlist) {
            waitlistInfo.add("Party Name: " + party[0] + ", Size: " + party[1] + ", Contact: " + party[2]);
        }

        return waitlistInfo;
    }

    // Returns a list of tables that are currently occupied
    public static List<String> viewOccupiedTables() {
        Map<String, String[]> layout = readLayout();
        List<String> occupiedTables = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : layout.entrySet()) {
            String tableNumber = entry.getKey();
            String[] tableDetails = entry.getValue();
            if (!tableDetails[1].equals("null")) {
                occupiedTables.add("Table " + tableNumber + ": Occupied by " + tableDetails[1]);
            }
        }

        return occupiedTables;
    }

    public static void main(String[] args) {
        Restaurant r = new Restaurant();

        // Add parties to the waitlist
        r.addPartyToWaitlist("Juanita", 3, "246810");
        r.addPartyToWaitlist("George", 5, "121416");

        // View the occupied tables
        System.out.println("Occupied Tables:");
        for (String info : r.viewOccupiedTables()) {
            System.out.println(info);
        }

        // Clear a table
        System.out.println();
        r.clearTable("1");

        // Try to seat parties
        System.out.println();
        r.seatParty();
        r.seatParty();

        // View the updated waitlist and occupied tables
        System.out.println("\nUpdated Waitlist:");
        for (String info : r.viewWaitlist()) {
            System.out.println(info);
        }

        System.out.println("\nUpdated Occupied Tables:");
        for (String info : r.viewOccupiedTables()) {
            System.out.println(info);
        }
    }
}

