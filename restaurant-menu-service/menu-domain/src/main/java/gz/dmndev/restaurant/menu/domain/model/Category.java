package gz.dmndev.restaurant.menu.domain.model;

public record Category(
    String id, String name, String description, int displayOrder, boolean active) {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String id;
    private String name;
    private String description;
    private int displayOrder;
    private boolean active;

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

    public Builder displayOrder(int displayOrder) {
      this.displayOrder = displayOrder;
      return this;
    }

    public Builder active(boolean active) {
      this.active = active;
      return this;
    }

    public Category build() {
      return new Category(id, name, description, displayOrder, active);
    }
  }
}
