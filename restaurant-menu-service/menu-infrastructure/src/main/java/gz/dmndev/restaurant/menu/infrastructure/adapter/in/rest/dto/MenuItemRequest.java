package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {
  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotNull(message = "Price is required")
  @Min(value = 0, message = "Price must be greater than or equal to zero")
  private BigDecimal price;

  @NotBlank(message = "Category ID is required")
  private String categoryId;

  private List<String> tags;

  private List<String> allergens;

  private boolean available = true;

  private String imageUrl;
}
