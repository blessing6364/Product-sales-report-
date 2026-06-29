import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

// ─────────────────────────────────────────────
// Interface: raw calculation contract
// ─────────────────────────────────────────────
interface Product {
    int    totalSales(int[] productSales);   // Sum of all sales
    double averageSales(int[] productSales); // Mean of all sales
    int    maxSale(int[] productSales);      // Highest single sale
    int    minSale(int[] productSales);      // Lowest single sale
}

// ─────────────────────────────────────────────
// Interface: aggregated reporting contract
// ─────────────────────────────────────────────
interface ProductSalesInterface {
    int[]  getProductSales();       // Returns the raw sales array
    int    getTotalSales();         // Sum of all sales
    double getAverageSales();       // Mean of all sales
    int    getSalesOverLimit();     // Count of sales above the limit
    int    getSalesUnderLimit();    // Count of sales below the limit
    int    getProductsProcessed();  // Number of years processed (3 products/year)
}

// ─────────────────────────────────────────────
// Concrete class: implements both interfaces
// ─────────────────────────────────────────────
class ProductSales implements Product, ProductSalesInterface {

    private final int[] sales;
    private final int   salesLimit = 500;

    public ProductSales(int[] sales) {
        if (sales == null || sales.length == 0)
            throw new IllegalArgumentException("Sales data cannot be null or empty.");
        this.sales = sales;
    }

    // ── Product interface ──────────────────────

    @Override
    public int totalSales(int[] productSales) {
        int total = 0;
        for (int s : productSales) total += s;
        return total;
    }

    @Override
    public double averageSales(int[] productSales) {
        return (double) totalSales(productSales) / productSales.length;
    }

    @Override
    public int maxSale(int[] productSales) {
        int max = productSales[0];
        for (int s : productSales) if (s > max) max = s;
        return max;
    }

    @Override
    public int minSale(int[] productSales) {
        int min = productSales[0];
        for (int s : productSales) if (s < min) min = s;
        return min;
    }

    // ── ProductSalesInterface ──────────────────

    @Override public int[]  getProductSales()      { return sales; }
    @Override public int    getTotalSales()         { return totalSales(sales); }
    @Override public double getAverageSales()       { return averageSales(sales); }
    @Override public int    getProductsProcessed()  { return sales.length / 3; }

    @Override
    public int getSalesOverLimit() {
        int count = 0;
        for (int s : sales) if (s > salesLimit) count++;
        return count;
    }

    @Override
    public int getSalesUnderLimit() {
        int count = 0;
        for (int s : sales) if (s < salesLimit) count++;
        return count;
    }
}

// ─────────────────────────────────────────────
// Console report — demonstrates Product interface
// ─────────────────────────────────────────────
class ProductSalesReport {

    static void run() {
        // 2D array: each row = one year, each column = one product
        int[][] salesData = {
            {300, 150, 700},  // Year 1: Product 1, 2, 3
            {250, 200, 600}   // Year 2: Product 1, 2, 3
        };

        // Flatten 2D → 1D for calculation methods
        int[] allSales = new int[salesData.length * salesData[0].length];
        int index = 0;
        for (int[] year : salesData)
            for (int sale : year)
                allSales[index++] = sale;

        ProductSales ps = new ProductSales(allSales);

        String divider = "─".repeat(35);
        System.out.println("\n" + divider);
        System.out.println("   PRODUCT SALES REPORT - 2025");
        System.out.println(divider);
        System.out.printf("  %-20s %d%n",   "Total Sales:",   ps.getTotalSales());
        System.out.printf("  %-20s %.0f%n", "Average Sales:", ps.getAverageSales());
        System.out.printf("  %-20s %d%n",   "Maximum Sale:",  ps.maxSale(allSales));
        System.out.printf("  %-20s %d%n",   "Minimum Sale:",  ps.minSale(allSales));
        System.out.println(divider);
    }
}

// ─────────────────────────────────────────────
// GUI — demonstrates ProductSalesInterface
// ─────────────────────────────────────────────
public class ProductSalesApp extends JFrame {

    private final JTextArea textArea;
    private final JLabel    yearsLabel;
    private final ProductSales ps;

    public ProductSalesApp() {
        setTitle("Product Sales Application");
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        int[] salesData = {300, 150, 700, 250, 200, 600};
        ps = new ProductSales(salesData);

        // ── Centre: scrollable text area ────────
        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // ── South: status label ─────────────────
        yearsLabel = new JLabel("Years Processed: —");
        yearsLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(yearsLabel, BorderLayout.SOUTH);

        // ── North: buttons ──────────────────────
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JButton loadBtn = new JButton("Load Product Data");
        JButton saveBtn = new JButton("Save Product Data");
        loadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(loadBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // ── Menu bar ────────────────────────────
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu toolsMenu  = new JMenu("Tools");
        JMenuItem loadMenu  = new JMenuItem("Load Product Data");
        JMenuItem saveMenu  = new JMenuItem("Save Product Data");
        JMenuItem clearMenu = new JMenuItem("Clear");
        toolsMenu.add(loadMenu);
        toolsMenu.add(saveMenu);
        toolsMenu.add(clearMenu);
        menuBar.add(toolsMenu);

        setJMenuBar(menuBar);

        // ── Shared action listeners ─────────────

        ActionListener loadAction = e -> {
            textArea.setText("");
            textArea.append(String.format("%-22s %d%n",   "Total Sales:",       ps.getTotalSales()));
            textArea.append(String.format("%-22s %.0f%n", "Average Sales:",     ps.getAverageSales()));
            textArea.append(String.format("%-22s %d%n",   "Sales Over Limit:",  ps.getSalesOverLimit()));
            textArea.append(String.format("%-22s %d%n",   "Sales Under Limit:", ps.getSalesUnderLimit()));
            yearsLabel.setText("Years Processed: " + ps.getProductsProcessed());
        };

        ActionListener saveAction = e -> {
            try (PrintWriter pw = new PrintWriter("data.txt")) {
                pw.println("DATA LOG");
                pw.printf("%-22s %d%n",   "Total Sales:",       ps.getTotalSales());
                pw.printf("%-22s %.0f%n", "Average Sales:",     ps.getAverageSales());
                pw.printf("%-22s %d%n",   "Sales Over Limit:",  ps.getSalesOverLimit());
                pw.printf("%-22s %d%n",   "Sales Under Limit:", ps.getSalesUnderLimit());
                pw.printf("%-22s %d%n",   "Years Processed:",   ps.getProductsProcessed());
                JOptionPane.showMessageDialog(this, "Data saved to data.txt");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        loadBtn.addActionListener(loadAction);
        loadMenu.addActionListener(loadAction);
        saveBtn.addActionListener(saveAction);
        saveMenu.addActionListener(saveAction);
        clearMenu.addActionListener(e -> {
            textArea.setText("");
            yearsLabel.setText("Years Processed: —");
        });
        exitItem.addActionListener(e -> System.exit(0));
    }

    // ─────────────────────────────────────────
    // Entry point — runs console report then GUI
    // ─────────────────────────────────────────
    public static void main(String[] args) {
        ProductSalesReport.run(); // Console output first
        SwingUtilities.invokeLater(() -> new ProductSalesApp().setVisible(true));
    }
}
