import java.util.*;
import java.io.*;

public class UserInterface {
    private static UserInterface userInterface;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static final int EXIT = 0;
    private static final int ADD_CLIENT = 1;
    private static final int ADD_PRODUCT = 2;
    private static final int VIEW_CLIENTS = 3;
    private static final int VIEW_PRODUCTS = 4;
    private static final int ADD_TO_WISHLIST = 5;
    private static final int PLACE_ORDER = 6;
    private static final int RECEIVE_PAYMENT = 7;
    private static final int RECEIVE_SHIPMENT = 8;
    private static final int VIEW_INVOICES = 9;
    private static final int SAVE = 10;
    private static final int RETRIEVE = 11;
    private static final int HELP = 12;

    private UserInterface() {
        if (yesOrNo("Look for saved data and use it?")) {
            retrieve();
        } else {
            warehouse = Warehouse.instance();
        }
    }

    public static UserInterface instance() {
        if (userInterface == null) {
            return userInterface = new UserInterface();
        } else {
            return userInterface;
        }
    }

    public String getToken(String prompt) {
        do {
            try {
                System.out.println(prompt);
                String line = reader.readLine();
                StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }
            } catch (IOException ioe) {
                System.exit(0);
            }
        } while (true);
    }

    private boolean yesOrNo(String prompt) {
        String more = getToken(prompt + " (Y|y)[es] or anything else for no");
        return more.charAt(0) == 'y' || more.charAt(0) == 'Y';
    }

    public int getNumber(String prompt) {
        do {
            try {
                String item = getToken(prompt);
                return Integer.parseInt(item);
            } catch (NumberFormatException nfe) {
                System.out.println("Please input a number.");
            }
        } while (true);
    }

    public void help() {
        System.out.println("Enter a number between 0 and 12 as explained below:");
        System.out.println(EXIT + " to Exit\n");
        System.out.println(ADD_CLIENT + " to add a client");
        System.out.println(ADD_PRODUCT + " to add a product");
        System.out.println(VIEW_CLIENTS + " to view all clients");
        System.out.println(VIEW_PRODUCTS + " to view all products");
        System.out.println(ADD_TO_WISHLIST + " to add products to a client's wishlist");
        System.out.println(PLACE_ORDER + " to place an order for a client");
        System.out.println(RECEIVE_PAYMENT + " to record a payment for a client");
        System.out.println(RECEIVE_SHIPMENT + " to receive a product shipment");
        System.out.println(VIEW_INVOICES + " to view all invoices for a client");
        System.out.println(SAVE + " to save data");
        System.out.println(RETRIEVE + " to retrieve data");
        System.out.println(HELP + " for help");
    }

    public void addClient() {
        String firstName = getToken("Enter client first name");
        String lastName = getToken("Enter client last name");
        String address = getToken("Enter client address");
        String phone = getToken("Enter client phone");
        String clientID = IdServer.instance().getClientId();

        Client client = warehouse.addClient(clientID, firstName, lastName, address, phone);
        if (client == null) {
            System.out.println("Could not add client.");
        } else {
            System.out.println("Client added: " + client);
        }
    }

    public void addProduct() {
        String name = getToken("Enter product name");
        double price = Double.parseDouble(getToken("Enter product price"));
        int quantity = Integer.parseInt(getToken("Enter product quantity"));
        String productID = IdServer.instance().getProductId();

        Product product = warehouse.addProduct(name, productID, price, quantity);
        if (product == null) {
            System.out.println("Could not add product.");
        } else {
            System.out.println("Product added: " + product);
        }
    }

    public void viewClients() {
        Iterator<Client> clients = warehouse.getClients();
        System.out.println("\nClients:");
        while (clients.hasNext()) {
            Client client = clients.next();
            System.out.println(client);
        }
    }

    public void viewProducts() {
        Iterator<Product> products = warehouse.getProducts();
        System.out.println("\nProducts:");
        while (products.hasNext()) {
            Product product = products.next();
            System.out.println(product);
        }
    }

    public void addToWishlist() {
        String clientID = getToken("Enter client ID");
        String productID = getToken("Enter product ID");
        int quantity = getNumber("Enter quantity");

        String result = warehouse.addToWishList(clientID, productID, quantity);
        System.out.println(result);
    }

    public void placeOrder() {
        String clientID = getToken("Enter client ID");
        String result = warehouse.processClientOrder(clientID); // Matches the processClientOrder method in ClientList
        System.out.println(result);
    }

    public void receivePayment() {
        String clientID = getToken("Enter client ID");
        float amount = Float.parseFloat(getToken("Enter payment amount"));
        String result = warehouse.receivePayment(clientID, amount); // Assuming receivePayment is handled similarly
        System.out.println(result);
    }

    public void receiveShipment() {
        String productID = getToken("Enter product ID");
        int quantity = getNumber("Enter shipment quantity");
        String result = warehouse.receiveShipment(productID, quantity);
        System.out.println(result);
    }

    public void viewInvoices() {
        String clientID = getToken("Enter client ID");
        warehouse.printInvoices(clientID);
    }

    private void save() {
        if (warehouse.save()) {
            System.out.println("The warehouse has been successfully saved.");
        } else {
            System.out.println("There was an error saving the warehouse.");
        }
    }

    private void retrieve() {
        Warehouse tempWarehouse = Warehouse.retrieve();
        if (tempWarehouse != null) {
            warehouse = tempWarehouse;
            System.out.println("The warehouse has been successfully retrieved.");
        } else {
            System.out.println("No saved warehouse data found. Starting fresh.");
            warehouse = Warehouse.instance();
        }
    }

    public void process() {
        int command;
        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case ADD_CLIENT:
                    addClient();
                    break;
                case ADD_PRODUCT:
                    addProduct();
                    break;
                case VIEW_CLIENTS:
                    viewClients();
                    break;
                case VIEW_PRODUCTS:
                    viewProducts();
                    break;
                case ADD_TO_WISHLIST:
                    addToWishlist();
                    break;
                case PLACE_ORDER:
                    placeOrder();
                    break;
                case RECEIVE_PAYMENT:
                    receivePayment();
                    break;
                case RECEIVE_SHIPMENT:
                    receiveShipment();
                    break;
                case VIEW_INVOICES:
                    viewInvoices();
                    break;
                case SAVE:
                    save();
                    break;
                case RETRIEVE:
                    retrieve();
                    break;
                case HELP:
                    help();
                    break;
                default:
                    System.out.println("Invalid command. Try again.");
            }
        }
    }

    public static void main(String[] args) {
        UserInterface.instance().process();
    }
}
