package services;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import entities.Category;
import entities.Deal;
import entities.Product;
import enumEntities.BudgetBucket;
import enumEntities.TypeStatus;

public class ServiceReport {

	private CashFlow cashFlow;
	private Stock stock;

	public ServiceReport(CashFlow cashFlow, Stock stock) {
		this.cashFlow = cashFlow;
		this.stock = stock;
	}

	public double totalIncome() {
		double sum = 0.0;
		for (Deal deal : cashFlow.getDeals()) {
			if (deal.getStatus() == TypeStatus.RECEITA) {
				sum = sum + deal.getValue();
			}

		}
		return sum;
	}

	public Map<YearMonth, Double> monthlyExpenses() {
		Map<YearMonth, Double> result = new HashMap<>();

		for (Deal deal : cashFlow.getDeals()) {
			if (deal.getStatus() == TypeStatus.DESPESA) {
				YearMonth month = YearMonth.from(deal.getDate());
				double currentTotal = result.getOrDefault(month, 0.0);
				result.put(month, currentTotal + deal.getValue());
			}
		}

		return result;
	}

	public double compareMonths(YearMonth month1, YearMonth month2) {
		Map<YearMonth, Double> expenses = monthlyExpenses();
		double total1 = expenses.getOrDefault(month1, 0.0);
		double total2 = expenses.getOrDefault(month2, 0.0);
		return total2 - total1;
	}

	public Map<Category, Double> expensesByCategory() {
		Map<Category, Double> result = new HashMap<>();

		for (Deal deal : cashFlow.getDeals()) {
			if (deal.getStatus() == TypeStatus.DESPESA) {
				Category category = deal.getCategory();
				double currentTotal = result.getOrDefault(category, 0.0);
				result.put(category, currentTotal + deal.getValue());
			}
		}

		return result;
	}

	public Category mostExpensiveCategory() {
		Map<Category, Double> expenses = expensesByCategory();
		Category mostExpensive = null;
		double highestValue = 0.0;

		for (Category category : expenses.keySet()) {
			double value = expenses.get(category);
			if (mostExpensive == null || value > highestValue) {
				mostExpensive = category;
				highestValue = value;
			}
		}
		return mostExpensive;
	}

	public double percentageIncrease(YearMonth previousMonth, YearMonth currentMonth) {
		Map<YearMonth, Double> expense = monthlyExpenses();
		double previousTotal = expense.getOrDefault(previousMonth, 0.0);
		double currentTotal = expense.getOrDefault(currentMonth, 0.0);

		if (previousTotal == 0) {
			return 0.0;
		}

		return ((currentTotal - previousTotal) / previousTotal) * 100;
	}

	public String checkAlert(YearMonth previousMonth, YearMonth currentMonth, double thresholdPercentage) {
		double increase = percentageIncrease(previousMonth, currentMonth);

		if (increase > thresholdPercentage) {
			return "Alerta: seus gastos aumentaram " + String.format("%.2f", increase)
					+ "% em relação ao mês anterior.";
		}

		return "Gastos dentro do esperado.";
	}

	public double stockValue() {
		double total = 0;

		for (Product product : stock.getAllProducts()) {
			total += product.getCostPrice() * product.getQuantity();
		}

		return total;
	}

	public double workingCapital() {
		double currentAssets = cashFlow.currentBalance() + stockValue();
		double currentLiabilities = cashFlow.expectedExpenses();
		return currentAssets - currentLiabilities;
	}

	public Map<BudgetBucket, Double> expensesByBucket() {
		Map<BudgetBucket, Double> result = new HashMap<>();

		for (Deal deal : cashFlow.getDeals()) {
			if (deal.getStatus() == TypeStatus.DESPESA) {
				BudgetBucket bucket = deal.getCategory().getBucket();
				double currentTotal = result.getOrDefault(bucket, 0.0);
				result.put(bucket, currentTotal + deal.getValue());
			}
		}

		return result;
	}

	public String budgetSummary() {
		double income = totalIncome();
		Map<BudgetBucket, Double> expenses = expensesByBucket();

		double necessidades = expenses.getOrDefault(BudgetBucket.NECESSIDADE, 0.0);
		double desejos = expenses.getOrDefault(BudgetBucket.DESEJO, 0.0);
		double investimento = expenses.getOrDefault(BudgetBucket.INVESTIMENTO, 0.0);

		StringBuilder summary = new StringBuilder();
		summary.append("=== Método 50/30/20 ===\n");
		summary.append("Necessidades: R$ ").append(String.format("%.2f", necessidades)).append(" (recomendado: R$ ")
				.append(String.format("%.2f", income * 0.50)).append(")\n");
		summary.append("Desejos: R$ ").append(String.format("%.2f", desejos)).append(" (recomendado: R$ ")
				.append(String.format("%.2f", income * 0.30)).append(")\n");
		summary.append("Investimento: R$ ").append(String.format("%.2f", investimento)).append(" (recomendado: R$ ")
				.append(String.format("%.2f", income * 0.20)).append(")\n");

		return summary.toString();
	}

	public double availableToSpend() {
		return cashFlow.currentBalance() + cashFlow.expectedIncome() - cashFlow.expectedExpenses();
	}

	public String generateSummary() {
		StringBuilder summary = new StringBuilder();

		summary.append("=== Resumo Financeiro ===\n");
		summary.append("Saldo atual: R$ ").append(String.format("%.2f", cashFlow.currentBalance())).append("\n");
		summary.append("Previsão de entrada: R$ ").append(String.format("%.2f", cashFlow.expectedIncome()))
				.append("\n");
		summary.append("Previsão de saída: R$ ").append(String.format("%.2f", cashFlow.expectedExpenses()))
				.append("\n");
		summary.append("Disponível para gastar: R$ ").append(String.format("%.2f", availableToSpend())).append("\n");

		Category mostExpensive = mostExpensiveCategory();
		if (mostExpensive != null) {
			summary.append("Categoria que mais consome: ").append(mostExpensive.getTitle()).append("\n");
		}

		return summary.toString();
	}
}
