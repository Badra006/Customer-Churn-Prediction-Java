import java.io.*;
import java.util.*;

public class CustomerChurnPrediction {

    static class Customer {
        double tenure, charges, calls;
        int churn;

        Customer(double tenure, double charges, double calls, int churn) {
            this.tenure = tenure;
            this.charges = charges;
            this.calls = calls;
            this.churn = churn;
        }
    }

    static double distance(Customer a, Customer b) {
        return Math.sqrt(
            Math.pow(a.tenure - b.tenure, 2) +
            Math.pow(a.charges - b.charges, 2) +
            Math.pow(a.calls - b.calls, 2)
        );
    }

    static int predict(Customer test, ArrayList<Customer> train, int k) {

        ArrayList<Customer> sorted = new ArrayList<>(train);

        sorted.sort((a, b) ->
            Double.compare(distance(test, a), distance(test, b)));

        int churn = 0;
        int stay = 0;

        for (int i = 0; i < k && i < sorted.size(); i++) {
            if (sorted.get(i).churn == 1)
                churn++;
            else
                stay++;
        }

        return churn > stay ? 1 : 0;
    }

    static ArrayList<Customer> loadData() throws Exception {

        ArrayList<Customer> data = new ArrayList<>();

        Scanner file = new Scanner(new File("customer_churn.csv"));

        file.nextLine();

        while (file.hasNextLine()) {

            String[] row = file.nextLine().split(",");

            data.add(new Customer(
                Double.parseDouble(row[0]),
                Double.parseDouble(row[1]),
                Double.parseDouble(row[2]),
                Integer.parseInt(row[3])
            ));
        }

        file.close();

        return data;
    }

    static void datasetSummary(ArrayList<Customer> data) {

        int churned = 0;

        for (Customer c : data) {
            if (c.churn == 1)
                churned++;
        }

        double rate = churned * 100.0 / data.size();

        System.out.println("\n=== DATASET SUMMARY ===");
        System.out.println("Total Customers : " + data.size());
        System.out.println("Churned         : " + churned);
        System.out.println("Stayed          : " + (data.size() - churned));
        System.out.printf("Churn Rate      : %.2f%%\n", rate);
    }

    static void testModel(ArrayList<Customer> data) {

        int trainSize = (int)(data.size() * 0.7);

        ArrayList<Customer> train =
            new ArrayList<>(data.subList(0, trainSize));

        ArrayList<Customer> test =
            new ArrayList<>(data.subList(trainSize, data.size()));

        int correct = 0;

        for (Customer c : test) {

            int prediction = predict(c, train, 3);

            if (prediction == c.churn)
                correct++;
        }

        double accuracy = correct * 100.0 / test.size();

        System.out.println("\n=== MODEL PERFORMANCE ===");
        System.out.println("Training Data : " + train.size());
        System.out.println("Testing Data  : " + test.size());
        System.out.printf("Accuracy      : %.2f%%\n", accuracy);
    }

    static void predictCustomer(ArrayList<Customer> data) {

        int trainSize = (int)(data.size() * 0.7);

        ArrayList<Customer> train =
            new ArrayList<>(data.subList(0, trainSize));

        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== NEW CUSTOMER ===");

        System.out.print("Enter tenure (months): ");
        double tenure = sc.nextDouble();

        System.out.print("Enter monthly charges: ");
        double charges = sc.nextDouble();

        System.out.print("Enter support calls: ");
        double calls = sc.nextDouble();

        Customer customer =
            new Customer(tenure, charges, calls, 0);

        int result = predict(customer, train, 3);

        System.out.println("\n==========================");

        if (result == 1)
            System.out.println("Prediction: CUSTOMER WILL CHURN");
        else
            System.out.println("Prediction: CUSTOMER WILL STAY");

        System.out.println("==========================");
    }

    public static void main(String[] args) {

        try {

            ArrayList<Customer> data = loadData();

            Scanner sc = new Scanner(System.in);

            while (true) {

                System.out.println("\n================================");
                System.out.println("   CUSTOMER CHURN PREDICTION");
                System.out.println("================================");

                System.out.println("1. View Dataset Summary");
                System.out.println("2. Test Model Accuracy");
                System.out.println("3. Predict New Customer");
                System.out.println("4. Exit");

                System.out.print("\nEnter your choice: ");

                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        datasetSummary(data);
                        break;

                    case 2:
                        testModel(data);
                        break;

                    case 3:
                        predictCustomer(data);
                        break;

                    case 4:
                        System.out.println("Thank you!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {

            System.out.println(
                "Error: " + e.getMessage()
            );
        }
    }
}
