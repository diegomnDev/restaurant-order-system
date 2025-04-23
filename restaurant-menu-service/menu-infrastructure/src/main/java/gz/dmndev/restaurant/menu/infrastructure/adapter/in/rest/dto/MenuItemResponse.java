package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto;

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
public class MenuItemResponse {
  private String id;
  private String name;
  private String description;
  private BigDecimal price;
  private CategoryResponse category;
  private List<String> tags;
  private List<String> allergens;
  private boolean available;
  private String imageUrl;
}
