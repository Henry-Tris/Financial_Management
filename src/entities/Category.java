package entities;

import java.util.Objects;

import enumEntities.BudgetBucket;

public class Category {

	private String title;
	private BudgetBucket bucket;
	
	public Category() {
		
	}

	public Category(String title, BudgetBucket bucket) {
		this.title = title;
		this.bucket = bucket;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BudgetBucket getBucket() {
		return bucket;
	}

	public void setBucket(BudgetBucket bucket) {
		this.bucket = bucket;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Objects.equals(title, other.title);
	}
}
