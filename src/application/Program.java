package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import entities.Bills;
import entities.Category;
import entities.Deal;
import entities.Product;
import entities.Target;
import entities.TaxRule;
import enumEntities.BudgetBucket;
import enumEntities.Context;
import enumEntities.PaymentStatus;
import enumEntities.TypeStatus;
import services.CashFlow;
import services.ServiceReport;
import services.Stock;

public class Program {

    static {
        Locale.setDefault(Locale.US);
    }

    private static Scanner sc = new Scanner(System.in);
    private static Stock stock = new Stock();
    private static CashFlow cashFlow = new CashFlow();
    private static ServiceReport serviceReport = new ServiceReport(cashFlow, stock);
    private static List<Target> targets = new ArrayList<>();
    private static TaxRule taxRule;

    private static final double HIGH_VALUE_THRESHOLD = 500.0;
    private static final String DIRECTORY = "C:\\Users\\Henry\\Documents\\Java\\Arquivos Financial Management\\";

    public static void main(String[] args) {

        loadData();

        boolean running = true;

        while (running) {
            showMainMenu();
            int option = readInt("");

            switch (option) {
                case 1:
                    stockMenu();
                    break;
                case 2:
                    transactionsMenu();
                    break;
                case 3:
                    reportsMenu();
                    break;
                case 4:
                    targetsMenu();
                    break;
                case 5:
                    taxMenu();
                    break;
                case 0:
                    running = false;
                    saveData();
                    System.out.println("Dados salvos. Encerrando o sistema...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }

        sc.close();
    }

    // ===== MENU PRINCIPAL =====

    private static void showMainMenu() {
        System.out.println("\n=== Controle Financeiro ===");
        System.out.println("1 - Estoque");
        System.out.println("2 - Movimentações Financeiras");
        System.out.println("3 - Relatórios");
        System.out.println("4 - Metas");
        System.out.println("5 - Imposto");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    // ===== SUBMENU: ESTOQUE =====

    private static void stockMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Estoque ===");
            System.out.println("1 - Adicionar produto");
            System.out.println("2 - Vender produto");
            System.out.println("3 - Descartar produto");
            System.out.println("4 - Ver estoque");
            System.out.println("0 - Voltar");
            int option = readInt("Escolha uma opção: ");

            switch (option) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    sellProduct();
                    break;
                case 3:
                    discardProduct();
                    break;
                case 4:
                    viewStock();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // ===== SUBMENU: MOVIMENTAÇÕES FINANCEIRAS =====

    private static void transactionsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Movimentações Financeiras ===");
            System.out.println("1 - Registrar movimentação genérica");
            System.out.println("2 - Adicionar conta a pagar");
            System.out.println("3 - Pagar conta pendente");
            System.out.println("0 - Voltar");
            int option = readInt("Escolha uma opção: ");

            switch (option) {
                case 1:
                    addDeal();
                    break;
                case 2:
                    addBill();
                    break;
                case 3:
                    payBill();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // ===== SUBMENU: RELATÓRIOS =====

    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Relatórios ===");
            System.out.println("1 - Resumo financeiro");
            System.out.println("2 - Capital de giro");
            System.out.println("3 - Método 50/30/20");
            System.out.println("4 - Ranking de categorias");
            System.out.println("0 - Voltar");
            int option = readInt("Escolha uma opção: ");

            switch (option) {
                case 1:
                    System.out.println(serviceReport.generateSummary());
                    break;
                case 2:
                    System.out.println("Capital de giro: R$ " + String.format("%.2f", serviceReport.workingCapital()));
                    break;
                case 3:
                    System.out.println(serviceReport.budgetSummary());
                    break;
                case 4:
                    viewCategoryRanking();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // ===== SUBMENU: METAS =====

    private static void targetsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Metas ===");
            System.out.println("1 - Criar meta");
            System.out.println("2 - Ver metas");
            System.out.println("3 - Atualizar progresso");
            System.out.println("0 - Voltar");
            int option = readInt("Escolha uma opção: ");

            switch (option) {
                case 1:
                    addTarget();
                    break;
                case 2:
                    viewTargets();
                    break;
                case 3:
                    updateTarget();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // ===== SUBMENU: IMPOSTO =====

    private static void taxMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Imposto ===");
            System.out.println("1 - Definir regra de imposto");
            System.out.println("2 - Calcular imposto");
            System.out.println("0 - Voltar");
            int option = readInt("Escolha uma opção: ");

            switch (option) {
                case 1:
                    setTaxRule();
                    break;
                case 2:
                    calculateTax();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // ===== MÉTODOS DE VALIDAÇÃO REUTILIZÁVEIS =====

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Digite um número inteiro.");
                sc.nextLine();
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = sc.nextDouble();
                sc.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Digite um número (use ponto para decimais).");
                sc.nextLine();
            }
        }
    }

    private static double readPositiveDouble(String prompt) {
        double value;
        do {
            value = readDouble(prompt);
            if (value <= 0) {
                System.out.println("Valor inválido! Digite um número maior que zero.");
            }
        } while (value <= 0);
        return value;
    }

    private static int readPositiveInt(String prompt) {
        int value;
        do {
            value = readInt(prompt);
            if (value <= 0) {
                System.out.println("Valor inválido! Digite um número maior que zero.");
            }
        } while (value <= 0);
        return value;
    }

    private static String readNonEmptyString(String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = sc.nextLine();
            if (value.trim().isEmpty()) {
                System.out.println("Este campo não pode ficar vazio!");
            }
        } while (value.trim().isEmpty());
        return value;
    }

    private static int readIntInRange(String prompt, int min, int max) {
        int value;
        do {
            value = readInt(prompt);
            if (value < min || value > max) {
                System.out.println("Opção inválida! Digite um número entre " + min + " e " + max + ".");
            }
        } while (value < min || value > max);
        return value;
    }

    private static String normalizeName(String name) {
        return name.trim().toUpperCase();
    }

    private static boolean confirm(String message) {
        System.out.print(message + " (S/N): ");
        String answer = sc.nextLine();
        return answer.equalsIgnoreCase("S");
    }

    private static boolean confirmValue(double value) {
        if (value > HIGH_VALUE_THRESHOLD) {
            System.out.println("Atenção: esse é um valor alto!");
        }
        return confirm("Confirma o valor de R$ " + String.format("%.2f", value) + "?");
    }

    // ===== AÇÕES: ESTOQUE =====

    public static void addProduct() {
        String name = normalizeName(readNonEmptyString("Nome do produto: "));
        double costPrice = readPositiveDouble("Preço de custo: ");
        double salePrice = readPositiveDouble("Preço de venda: ");
        int quantity = readPositiveInt("Quantidade: ");

        if (!confirmValue(costPrice * quantity)) {
            System.out.println("Operação cancelada.");
            return;
        }

        Product product = new Product(name, costPrice, salePrice, quantity);
        stock.addProduct(product);

        System.out.println("Produto adicionado com sucesso!");
    }

    public static void sellProduct() {
        String name = normalizeName(readNonEmptyString("Nome do produto: "));

        Product product = stock.findProduct(name);
        if (product == null) {
            System.out.println("Produto não encontrado no estoque!");
            return;
        }

        int quantity = readPositiveInt("Quantidade vendida: ");

        double totalValue = product.getSalePrice() * quantity;

        if (!confirmValue(totalValue)) {
            System.out.println("Operação cancelada.");
            return;
        }

        boolean success = stock.sellProduct(name, quantity);
        if (!success) {
            System.out.println("Estoque insuficiente para essa venda!");
            return;
        }

        String categoryName = normalizeName(readNonEmptyString("Nome da categoria: "));
        Category category = new Category(categoryName, null);

        Deal deal = new Deal(totalValue, LocalDate.now(), TypeStatus.RECEITA, "Venda - " + name, category,
                Context.BUSINESS, PaymentStatus.PAID);
        cashFlow.addDeal(deal);

        System.out.println("Venda registrada! Total: R$ " + String.format("%.2f", totalValue));
    }

    public static void discardProduct() {
        String name = normalizeName(readNonEmptyString("Nome do produto: "));

        Product product = stock.findProduct(name);
        if (product == null) {
            System.out.println("Produto não encontrado no estoque!");
            return;
        }

        int quantity = readPositiveInt("Quantidade a descartar: ");

        System.out.print("Motivo (ex: doação, perda, quebra): ");
        String reason = sc.nextLine();

        boolean success = stock.sellProduct(name, quantity);
        if (!success) {
            System.out.println("Estoque insuficiente para esse descarte!");
            return;
        }

        System.out.println("Descarte registrado: " + quantity + " unidade(s) de " + product.getName() + " (" + reason + ").");
    }

    public static void viewStock() {
        System.out.println("=== Estoque ===");
        boolean empty = true;

        for (Product product : stock.getAllProducts()) {
            empty = false;
            System.out.println(product.getName() + " - Custo: R$ " + String.format("%.2f", product.getCostPrice())
                    + " - Venda: R$ " + String.format("%.2f", product.getSalePrice())
                    + " - Quantidade: " + product.getQuantity());
        }

        if (empty) {
            System.out.println("Nenhum produto cadastrado ainda.");
        }
    }

    // ===== AÇÕES: MOVIMENTAÇÕES FINANCEIRAS =====

    public static void addDeal() {
        String name = readNonEmptyString("Nome da movimentação: ");
        double value = readPositiveDouble("Valor: ");

        if (!confirmValue(value)) {
            System.out.println("Operação cancelada.");
            return;
        }

        int typeChoice = readIntInRange("Tipo (1 - Receita, 2 - Despesa): ", 1, 2);
        TypeStatus type = (typeChoice == 1) ? TypeStatus.RECEITA : TypeStatus.DESPESA;

        int paymentChoice = readIntInRange("Status (1 - Pago/Recebido, 2 - Pendente): ", 1, 2);
        PaymentStatus payment = (paymentChoice == 1) ? PaymentStatus.PAID : PaymentStatus.PENDENT;

        int contextChoice = readIntInRange("Contexto (1 - Pessoal, 2 - Empresarial): ", 1, 2);
        Context context = (contextChoice == 1) ? Context.PERSONAL : Context.BUSINESS;

        String categoryName = normalizeName(readNonEmptyString("Nome da categoria: "));
        BudgetBucket bucket = askBudgetBucket();
        Category category = new Category(categoryName, bucket);

        Deal deal = new Deal(value, LocalDate.now(), type, name, category, context, payment);
        cashFlow.addDeal(deal);

        System.out.println("Movimentação registrada com sucesso!");
    }

    public static void addBill() {
        String name = readNonEmptyString("Nome da conta: ");
        double value = readPositiveDouble("Valor: ");

        if (!confirmValue(value)) {
            System.out.println("Operação cancelada.");
            return;
        }

        int days = readPositiveInt("Dias até o vencimento: ");

        Bills bill = new Bills(name, value, LocalDate.now().plusDays(days), PaymentStatus.PENDENT);
        cashFlow.addBill(bill);

        System.out.println("Conta adicionada com sucesso!");
    }

    public static void payBill() {
        List<Bills> pendingBills = new ArrayList<>();

        for (Bills bill : cashFlow.getBills()) {
            if (bill.getPayment() == PaymentStatus.PENDENT) {
                pendingBills.add(bill);
            }
        }

        if (pendingBills.isEmpty()) {
            System.out.println("Nenhuma conta pendente.");
            return;
        }

        System.out.println("=== Contas Pendentes ===");
        for (int i = 0; i < pendingBills.size(); i++) {
            Bills bill = pendingBills.get(i);
            System.out.println((i + 1) + " - " + bill.getName() + " - R$ " + String.format("%.2f", bill.getValue()));
        }

        int choice = readInt("Escolha o número da conta paga: ");

        if (choice < 1 || choice > pendingBills.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Bills chosen = pendingBills.get(choice - 1);
        chosen.setPayment(PaymentStatus.PAID);

        System.out.println("Conta marcada como paga!");
    }

    // ===== AÇÕES: RELATÓRIOS =====

    private static void viewCategoryRanking() {
        Map<Category, Double> expenses = serviceReport.expensesByCategory();

        List<Map.Entry<Category, Double>> list = new ArrayList<>(expenses.entrySet());
        Collections.sort(list, (a, b) -> b.getValue().compareTo(a.getValue()));

        if (list.isEmpty()) {
            System.out.println("Nenhuma despesa registrada ainda.");
            return;
        }

        System.out.println("=== Ranking de Categorias ===");
        for (Map.Entry<Category, Double> entry : list) {
            System.out.println(entry.getKey().getTitle() + " - R$ " + String.format("%.2f", entry.getValue()));
        }
    }

    // ===== AÇÕES: METAS (TARGET) =====

    public static void addTarget() {
        String goal = readNonEmptyString("Nome da meta: ");
        double targetValue = readPositiveDouble("Valor alvo: ");

        double currentValue = readDouble("Valor já guardado (0 se está começando agora): ");

        Target target = new Target(goal, targetValue, currentValue);
        targets.add(target);

        System.out.println("Meta criada com sucesso!");
    }

    public static void viewTargets() {
        if (targets.isEmpty()) {
            System.out.println("Nenhuma meta cadastrada ainda.");
            return;
        }

        System.out.println("=== Metas ===");
        for (int i = 0; i < targets.size(); i++) {
            Target target = targets.get(i);
            String status = target.isCompleted() ? "CONCLUÍDA" : "em andamento";
            System.out.println((i + 1) + " - " + target.getGoal()
                    + " | R$ " + String.format("%.2f", target.getCurrentValue())
                    + " de R$ " + String.format("%.2f", target.getTargetValue())
                    + " (" + String.format("%.1f", target.getProgressPercentage()) + "%) - " + status);
        }
    }

    public static void updateTarget() {
        if (targets.isEmpty()) {
            System.out.println("Nenhuma meta cadastrada ainda.");
            return;
        }

        viewTargets();

        int choice = readInt("Escolha o número da meta: ");

        if (choice < 1 || choice > targets.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Target target = targets.get(choice - 1);

        int action = readInt("1 - Adicionar valor / 2 - Retirar valor: ");

        double amount = readPositiveDouble("Valor: ");

        if (action == 2) {
            boolean success = target.withdraw(amount);
            if (!success) {
                System.out.println("Valor de retirada maior que o disponível na meta!");
                return;
            }
            System.out.println("Valor retirado com sucesso!");
        } else {
            target.addProgress(amount);
            System.out.println("Progresso atualizado com sucesso!");
        }
    }

    // ===== AÇÕES: IMPOSTO (TAXRULE) =====

    public static void setTaxRule() {
        String name = readNonEmptyString("Nome da regra de imposto: ");
        double percentage = readPositiveDouble("Percentual (ex: 6 para 6%): ");

        taxRule = new TaxRule(name, percentage);

        System.out.println("Regra de imposto definida com sucesso!");
    }

    public static void calculateTax() {
        if (taxRule == null) {
            System.out.println("Nenhuma regra de imposto definida ainda.");
            return;
        }

        double income = serviceReport.totalIncome();
        double tax = taxRule.calculateTax(income);

        System.out.println("Receita total: R$ " + String.format("%.2f", income));
        System.out.println("Regra aplicada: " + taxRule.getName() + " (" + taxRule.getPercentage() + "%)");
        System.out.println("Imposto devido: R$ " + String.format("%.2f", tax));
    }

    // ===== PERSISTÊNCIA =====

    private static void saveData() {
        try {
            File dir = new File(DIRECTORY);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            BufferedWriter productsWriter = new BufferedWriter(new FileWriter(DIRECTORY + "products.txt"));
            for (Product p : stock.getAllProducts()) {
                productsWriter.write(p.getName() + "," + p.getCostPrice() + "," + p.getSalePrice() + "," + p.getQuantity());
                productsWriter.newLine();
            }
            productsWriter.close();

            BufferedWriter dealsWriter = new BufferedWriter(new FileWriter(DIRECTORY + "deals.txt"));
            for (Deal d : cashFlow.getDeals()) {
                String bucket = (d.getCategory().getBucket() == null) ? "NULL" : d.getCategory().getBucket().toString();
                dealsWriter.write(d.getValue() + "," + d.getDate() + "," + d.getStatus() + "," + d.getName() + ","
                        + d.getCategory().getTitle() + "," + bucket + "," + d.getContext() + "," + d.getPayment());
                dealsWriter.newLine();
            }
            dealsWriter.close();

            BufferedWriter billsWriter = new BufferedWriter(new FileWriter(DIRECTORY + "bills.txt"));
            for (Bills b : cashFlow.getBills()) {
                billsWriter.write(b.getName() + "," + b.getValue() + "," + b.getLimit() + "," + b.getPayment());
                billsWriter.newLine();
            }
            billsWriter.close();

            BufferedWriter targetsWriter = new BufferedWriter(new FileWriter(DIRECTORY + "targets.txt"));
            for (Target t : targets) {
                targetsWriter.write(t.getGoal() + "," + t.getTargetValue() + "," + t.getCurrentValue());
                targetsWriter.newLine();
            }
            targetsWriter.close();

            BufferedWriter taxWriter = new BufferedWriter(new FileWriter(DIRECTORY + "taxrule.txt"));
            if (taxRule != null) {
                taxWriter.write(taxRule.getName() + "," + taxRule.getPercentage());
                taxWriter.newLine();
            }
            taxWriter.close();

        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    private static void loadData() {
        try {
            File productsFile = new File(DIRECTORY + "products.txt");
            if (productsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(productsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Product product = new Product(parts[0], Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2]), Integer.parseInt(parts[3]));
                    stock.addProduct(product);
                }
                reader.close();
            }

            File billsFile = new File(DIRECTORY + "bills.txt");
            if (billsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(billsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Bills bill = new Bills(parts[0], Double.parseDouble(parts[1]), LocalDate.parse(parts[2]),
                            PaymentStatus.valueOf(parts[3]));
                    cashFlow.addBill(bill);
                }
                reader.close();
            }

            File dealsFile = new File(DIRECTORY + "deals.txt");
            if (dealsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(dealsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    BudgetBucket bucket = parts[5].equals("NULL") ? null : BudgetBucket.valueOf(parts[5]);
                    Category category = new Category(parts[4], bucket);
                    Deal deal = new Deal(Double.parseDouble(parts[0]), LocalDate.parse(parts[1]),
                            TypeStatus.valueOf(parts[2]), parts[3], category,
                            Context.valueOf(parts[6]), PaymentStatus.valueOf(parts[7]));
                    cashFlow.addDeal(deal);
                }
                reader.close();
            }

            File targetsFile = new File(DIRECTORY + "targets.txt");
            if (targetsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(targetsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Target target = new Target(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                    targets.add(target);
                }
                reader.close();
            }

            File taxFile = new File(DIRECTORY + "taxrule.txt");
            if (taxFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(taxFile));
                String line = reader.readLine();
                if (line != null) {
                    String[] parts = line.split(",");
                    taxRule = new TaxRule(parts[0], Double.parseDouble(parts[1]));
                }
                reader.close();
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }

    private static BudgetBucket askBudgetBucket() {
        int bucketChoice = readIntInRange("Balde do orçamento (1 - Necessidade, 2 - Desejo, 3 - Investimento): ", 1, 3);

        if (bucketChoice == 2) {
            return BudgetBucket.DESEJO;
        } else if (bucketChoice == 3) {
            return BudgetBucket.INVESTIMENTO;
        } else {
            return BudgetBucket.NECESSIDADE;
        }
    }
}