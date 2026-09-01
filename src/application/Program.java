package application;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Scanner;

import entities.Bills;
import entities.Category;
import entities.Deal;
import entities.Product;
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
				System.out.println(serviceReport.generateSummary());
				break;
			case 6:
				System.out.println("Capital de giro: R$ " + String.format("%.2f", serviceReport.workingCapital()));
				break;
			case 7:
				System.out.println(serviceReport.budgetSummary());
				break;
			case 8:
				Category mostExpensive = serviceReport.mostExpensiveCategory();
				if (mostExpensive != null) {
					System.out.println("Categoria que mais consome: " + mostExpensive.getTitle());
				} else {
					System.out.println("Nenhuma despesa registrada ainda.");
				}
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
		System.out.println("5 - Ver resumo financeiro");
		System.out.println("6 - Ver capital de giro");
		System.out.println("7 - Ver método 50/30/20");
		System.out.println("8 - Ver categoria que mais consome");
		System.out.println("0 - Sair");
		System.out.print("Escolha uma opção: ");
	}

	public static void addProduct() {
		System.out.print("Nome do produto: ");
		String name = sc.nextLine();

		System.out.print("Preço de custo: ");
		double costPrice = sc.nextDouble();

		System.out.print("Preço de venda: ");
		double salePrice = sc.nextDouble();

		System.out.print("Quantidade: ");
		int quantity = sc.nextInt();
		sc.nextLine();

		Product product = new Product(name, costPrice, salePrice, quantity);
		stock.addProduct(product);

		System.out.println("Produto adicionado com sucesso!");
	}

	public static void sellProduct() {
		System.out.print("Nome do produto: ");
		String name = sc.nextLine();

		Product product = stock.findProduct(name);
		if (product == null) {
			System.out.println("Produto não encontrado no estoque!");
			return;
		}

		System.out.print("Quantidade vendida: ");
		int quantity = sc.nextInt();
		sc.nextLine();

		boolean success = stock.sellProduct(name, quantity);
		if (!success) {
			System.out.println("Estoque insuficiente para essa venda!");
			return;
		}

		double totalValue = product.getSalePrice() * quantity;

		System.out.print("Nome da categoria: ");
		String categoryName = sc.nextLine();

		BudgetBucket bucket = askBudgetBucket();
		Category category = new Category(categoryName, bucket);

		Deal deal = new Deal(totalValue, LocalDate.now(), TypeStatus.RECEITA, "Venda - " + name, category,
				Context.BUSINESS, PaymentStatus.PAID);
		cashFlow.addDeal(deal);

		System.out.println("Venda registrada! Total: R$ " + String.format("%.2f", totalValue));
	}

	public static void addDeal() {
		System.out.print("Nome da movimentação: ");
		String name = sc.nextLine();

		System.out.print("Valor: ");
		double value = sc.nextDouble();
		sc.nextLine();

		System.out.print("Tipo (1 - Receita, 2 - Despesa): ");
		int typeChoice = sc.nextInt();
		sc.nextLine();
		TypeStatus type = (typeChoice == 1) ? TypeStatus.RECEITA : TypeStatus.DESPESA;

		System.out.print("Status (1 - Pago/Recebido, 2 - Pendente): ");
		int paymentChoice = sc.nextInt();
		sc.nextLine();
		PaymentStatus payment = (paymentChoice == 1) ? PaymentStatus.PAID : PaymentStatus.PENDENT;

		System.out.print("Contexto (1 - Pessoal, 2 - Empresarial): ");
		int contextChoice = sc.nextInt();
		sc.nextLine();
		Context context = (contextChoice == 1) ? Context.PERSONAL : Context.BUSINESS;

		System.out.print("Nome da categoria: ");
		String categoryName = sc.nextLine();
		BudgetBucket bucket = askBudgetBucket();
		Category category = new Category(categoryName, bucket);

		Deal deal = new Deal(value, LocalDate.now(), type, name, category, context, payment);
		cashFlow.addDeal(deal);

		System.out.println("Movimentação registrada com sucesso!");
	}

	public static void addBill() {
		System.out.print("Nome da conta: ");
		String name = sc.nextLine();

		System.out.print("Valor: ");
		double value = sc.nextDouble();
		sc.nextLine();

		System.out.print("Dias até o vencimento: ");
		int days = sc.nextInt();
		sc.nextLine();

		Bills bill = new Bills(name, value, LocalDate.now().plusDays(days), PaymentStatus.PENDENT);
		cashFlow.addBill(bill);

		System.out.println("Conta adicionada com sucesso!");
	}

	private static BudgetBucket askBudgetBucket() {
		System.out.print("Balde do orçamento (1 - Necessidade, 2 - Desejo, 3 - Investimento): ");
		int bucketChoice = sc.nextInt();
		sc.nextLine();

		if (bucketChoice == 2) {
			return BudgetBucket.DESEJO;
		} else if (bucketChoice == 3) {
			return BudgetBucket.INVESTIMENTO;
		} else {
			return BudgetBucket.NECESSIDADE;
		}
	}
}