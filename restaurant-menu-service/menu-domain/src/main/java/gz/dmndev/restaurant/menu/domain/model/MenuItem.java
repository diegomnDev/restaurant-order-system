package gz.dmndev.restaurant.menu.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record MenuItem(
    String id,
    String name,
    String description,
    BigDecimal price,
    Category category,
    List<String> tags,
    List<String> allergens,
    boolean available,
    String imageUrl) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private List<String> tags;
    private List<String> allergens;
    private boolean available;
    private String imageUrl;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder price(BigDecimal price) {
      this.price = price;
      return this;
    }

    public Builder category(Category category) {
      this.category = category;
      return this;
    }

    public Builder tags(List<String> tags) {
      this.tags = tags;
      return this;
    }

    public Builder allergens(List<String> allergens) {
      this.allergens = allergens;
      return this;
    }

    public Builder available(boolean available) {
      this.available = available;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public MenuItem build() {
      return new MenuItem(
          id, name, description, price, category, tags, allergens, available, imageUrl);
    }
  }
}
