package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

	public static void main(String[] args) {

		boolean running = true;

		while (running) {
			showMenu();
			int option = sc.nextInt();
			sc.nextLine();

			switch (option) {
			case 1:
				addProduct();
				break;
			case 2:
				sellProduct();
				break;
			case 3:
				addDeal();
				break;
			case 4:
				addBill();
				break;
			case 5:
				payBill();
				break;
			case 6:
				viewStock();
				break;
			case 7:
				System.out.println(serviceReport.generateSummary());
				break;
			case 8:
				System.out.println("Capital de giro: R$ " + String.format("%.2f", serviceReport.workingCapital()));
				break;
			case 9:
				System.out.println(serviceReport.budgetSummary());
				break;
			case 10:
				viewCategoryRanking();
				break;
			case 11:
				addTarget();
				break;
			case 12:
				viewTargets();
				break;
			case 13:
				updateTarget();
				break;
			case 14:
				setTaxRule();
				break;
			case 15:
				calculateTax();
				break;
			case 0:
				running = false;
				System.out.println("Encerrando o sistema...");
				break;
			default:
				System.out.println("Opção inválida.");
			}
		}

		sc.close();
	}

	public static void showMenu() {
		System.out.println("\n=== Controle Financeiro ===");
		System.out.println("1 - Adicionar produto ao estoque");
		System.out.println("2 - Registrar venda de produto");
		System.out.println("3 - Registrar movimentação genérica");
		System.out.println("4 - Adicionar conta a pagar");
		System.out.println("5 - Pagar conta pendente");
		System.out.println("6 - Ver estoque");
		System.out.println("7 - Ver resumo financeiro");
		System.out.println("8 - Ver capital de giro");
		System.out.println("9 - Ver método 50/30/20");
		System.out.println("10 - Ver ranking de categorias");
		System.out.println("11 - Criar meta");
		System.out.println("12 - Ver metas");
		System.out.println("13 - Atualizar progresso de uma meta");
		System.out.println("14 - Definir regra de imposto");
		System.out.println("15 - Calcular imposto sobre a receita");
		System.out.println("0 - Sair");
		System.out.print("Escolha uma opção: ");
	}

	// ===== MÉTODOS DE VALIDAÇÃO REUTILIZÁVEIS =====

	private static double readPositiveDouble(String prompt) {
		double value;
		do {
			System.out.print(prompt);
			value = sc.nextDouble();
			sc.nextLine();
			if (value <= 0) {
				System.out.println("Valor inválido! Digite um número maior que zero.");
			}
		} while (value <= 0);
		return value;
	}

	private static int readPositiveInt(String prompt) {
		int value;
		do {
			System.out.print(prompt);
			value = sc.nextInt();
			sc.nextLine();
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
			System.out.print(prompt);
			value = sc.nextInt();
			sc.nextLine();
			if (value < min || value > max) {
				System.out.println("Opção inválida! Digite um número entre " + min + " e " + max + ".");
			}
		} while (value < min || value > max);
		return value;
	}

	private static String normalizeCategoryName(String name) {
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

	// ===== OPÇÕES DO MENU =====

	public static void addProduct() {
		String name = readNonEmptyString("Nome do produto: ");
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
		String name = readNonEmptyString("Nome do produto: ");

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

		String categoryName = normalizeCategoryName(readNonEmptyString("Nome da categoria: "));
		Category category = new Category(categoryName, null);

		Deal deal = new Deal(totalValue, LocalDate.now(), TypeStatus.RECEITA, "Venda - " + name, category,
				Context.BUSINESS, PaymentStatus.PAID);
		cashFlow.addDeal(deal);

		System.out.println("Venda registrada! Total: R$ " + String.format("%.2f", totalValue));
	}

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

		String categoryName = normalizeCategoryName(readNonEmptyString("Nome da categoria: "));
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

		System.out.print("Escolha o número da conta paga: ");
		int choice = sc.nextInt();
		sc.nextLine();

		if (choice < 1 || choice > pendingBills.size()) {
			System.out.println("Opção inválida.");
			return;
		}

		Bills chosen = pendingBills.get(choice - 1);
		chosen.setPayment(PaymentStatus.PAID);

		System.out.println("Conta marcada como paga!");
	}

	public static void viewStock() {
		System.out.println("=== Estoque ===");
		boolean empty = true;

		for (Product product : stock.getAllProducts()) {
			empty = false;
			System.out.println(product.getName() + " - Custo: R$ " + String.format("%.2f", product.getCostPrice())
					+ " - Venda: R$ " + String.format("%.2f", product.getSalePrice()) + " - Quantidade: "
					+ product.getQuantity());
		}

		if (empty) {
			System.out.println("Nenhum produto cadastrado ainda.");
		}
	}

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

	// ===== METAS (TARGET) =====

	public static void addTarget() {
		String goal = readNonEmptyString("Nome da meta: ");
		double targetValue = readPositiveDouble("Valor alvo: ");

		System.out.print("Valor já guardado (0 se está começando agora): ");
		double currentValue = sc.nextDouble();
		sc.nextLine();

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
			System.out.println(
					(i + 1) + " - " + target.getGoal() + " | R$ " + String.format("%.2f", target.getCurrentValue())
							+ " de R$ " + String.format("%.2f", target.getTargetValue()) + " ("
							+ String.format("%.1f", target.getProgressPercentage()) + "%) - " + status);
		}
	}

	public static void updateTarget() {
		if (targets.isEmpty()) {
			System.out.println("Nenhuma meta cadastrada ainda.");
			return;
		}

		viewTargets();

		System.out.print("Escolha o número da meta: ");
		int choice = sc.nextInt();
		sc.nextLine();

		if (choice < 1 || choice > targets.size()) {
			System.out.println("Opção inválida.");
			return;
		}

		Target target = targets.get(choice - 1);

		System.out.print("1 - Adicionar valor / 2 - Retirar valor: ");
		int action = sc.nextInt();
		sc.nextLine();

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

	// ===== IMPOSTO (TAXRULE) =====

	public static void setTaxRule() {
		String name = readNonEmptyString("Nome da regra de imposto: ");
		double percentage = readPositiveDouble("Percentual (ex: 6 para 6%): ");

		taxRule = new TaxRule(name, percentage);

		System.out.println("Regra de imposto definida com sucesso!");
	}

	public static void calculateTax() {
		if (taxRule == null) {
			System.out.println("Nenhuma regra de imposto definida ainda. Use a opção 14 primeiro.");
			return;
		}

		double income = serviceReport.totalIncome();
		double tax = taxRule.calculateTax(income);

		System.out.println("Receita total: R$ " + String.format("%.2f", income));
		System.out.println("Regra aplicada: " + taxRule.getName() + " (" + taxRule.getPercentage() + "%)");
		System.out.println("Imposto devido: R$ " + String.format("%.2f", tax));
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