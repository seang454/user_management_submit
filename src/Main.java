
import com.sun.net.httpserver.Request;
import lombok.Value;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
        import java.util.stream.Collectors;

class User {
    UUID uuid;
    String name;
    String email;
    boolean isDeleted;

    public User(String name, String email) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.isDeleted = false;
    }

    public void update(String name, String email, boolean isDeleted) {
        this.name = name;
        this.email = email;
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "UUID: " + uuid + " | Name: " + name + " | Email: " + email + " | isDeleted: " + isDeleted;
    }
}

public class Main {
    private static final String BOT_TOKEN = "8077478956:AAGndLUm5__WrE6zYn8rk3gf0fM84wMif6Y";
    private static final String CHAT_ID = "1707535552"; // Replace with the chat ID or username
    private static final String API_URL = "https://api.telegram.org/bot";
    private static final List<User> users = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    searchUserByUUID();
                    break;
                case 3:
                    updateUserByUUID();
                    break;
                case 4:
                    deleteUserByUUID();
                    break;
                case 5:
                    displayAllUsers();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
//    Table table= new Table(5,BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.ALL);
    public static void createNewUser(String userName) {
        System.out.println("Creating a new userName  " + userName);
        String message = "A new user "+userName+" has been created";
        sendTelegramNotification(message);
    }

    private static void showMenu() {
        System.out.println("\n===== User Management Console =====");
        System.out.println("1. Create User");
        System.out.println("2. Search User by UUID");
        System.out.println("3. Update User by UUID");
        System.out.println("4. Delete User by UUID");
        System.out.println("5. Display All Users");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }
    private static void createUser() {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        User newUser = new User(name, email);
        users.add(newUser);
        createNewUser(newUser.name);
    }
    private static void sendTelegramNotification(String userName) {
        try {
            // Create the API URL
            String urlString = API_URL + BOT_TOKEN + "/sendMessage";
            URL url = new URL(urlString);

            // Set up the HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create the JSON payload
            String jsonPayload = String.format(
                    "{\"chat_id\":\"%s\",\"text\":\"%s\"}",
                    CHAT_ID,
                    userName
            );

            // Write the JSON payload to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Send the request and check the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully!");
            } else {
                System.err.println("Failed to send message. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchUserByUUID() {
        System.out.print("Enter UUID to search: ");
        String uuidStr = scanner.nextLine();

        try {
            UUID uuid = UUID.fromString(uuidStr);
            Optional<User> userOpt = users.stream().filter(user -> user.uuid.equals(uuid)).findFirst();

            if (userOpt.isPresent()) {
                System.out.println(userOpt.get());
            } else {
                System.out.println("User not found with UUID: " + uuidStr);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private static void updateUserByUUID() {
        System.out.print("Enter UUID to update: ");
        String uuidStr = scanner.nextLine();

        try {
            UUID uuid = UUID.fromString(uuidStr);
            Optional<User> userOpt = users.stream().filter(user -> user.uuid.equals(uuid) && !user.isDeleted).findFirst(); // return true or false
            System.out.println(userOpt);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.print("Enter new Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter new Email: ");
                String email = scanner.nextLine();
                System.out.print("Set isDeleted to (true/false): ");
                boolean isDeleted = scanner.nextBoolean();
                scanner.nextLine();  // Consume newline

                user.update(name, email, isDeleted);
                System.out.println("User updated: " + user);
            } else {
                System.out.println("User not found with UUID: " + uuidStr);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private static void deleteUserByUUID() {
        System.out.print("Enter UUID to delete: ");
        String uuidStr = scanner.nextLine();

        try {
            UUID uuid = UUID.fromString(uuidStr);
            Optional<User> userOpt = users.stream().filter(user -> user.uuid.equals(uuid) && !user.isDeleted).findFirst();

            if (userOpt.isPresent()) {
                User user = userOpt.get(); // to get this user
                user.isDeleted = true;
                System.out.println("User deleted: " + user);
            } else {
                System.out.println("User not found with UUID: " + uuidStr);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private static void displayAllUsers() {
        System.out.println("\n===== Users List =====");
        String[] colunms = {"UUID", "NAME", "EMAIL", "ISDELECTED"};
//        Table table= new Table(4,BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.ALL); table we increase data when it is added

        List<User> activeUsers = users.stream()
                .filter(user -> !user.isDeleted)
                .collect(Collectors.toList());//to array
        int totalPages = (int) Math.ceil(activeUsers.size() / 5.0);
        int currentPage = 1;

        while (true) {
            Table table= new Table(4,BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.ALL); //it looks like as if we declare new table when we re-loop;
            int start = (currentPage - 1) * 5;
            int end = Math.min(currentPage * 5, activeUsers.size());
            List<User> pageUsers = activeUsers.subList(start, end);
//            System.out.println(pageUsers);

            System.out.println("Page " + currentPage + " of " + totalPages);
            for (int i=0;i<colunms.length;i++){
                table.addCell(colunms[i],new CellStyle(CellStyle.HorizontalAlign.center));
                table.setColumnWidth(i,40,40);
            }
            for (User user : pageUsers){
                table.addCell(user.uuid.toString(), new CellStyle(CellStyle.HorizontalAlign.center));
                table.addCell(user.name,new CellStyle(CellStyle.HorizontalAlign.center));
                table.addCell(user.email,new CellStyle(CellStyle.HorizontalAlign.center));
                table.addCell(String.valueOf(user.isDeleted),new CellStyle(CellStyle.HorizontalAlign.center));
            }
            System.out.println(table.render());
            System.out.print("\nPress 'n' for next page or 'e' to exit(break loop):");
            String choice;
            choice = new Scanner(System.in).nextLine();
            if(choice.equals("n")){
                while (true){
                    System.out.print("Choose page number :");
                    int page;
                    page = scanner.nextInt();
                    if (page!=currentPage && page<=totalPages) {
                        currentPage = page;
                        break;
                    } else {
                        System.out.println("You choose the current page or the non-exist page");
                        break;
                    }
                }
            }else if(choice.equals("e")||choice.equals("E")) {
                break;
            }
        }
    }
}
