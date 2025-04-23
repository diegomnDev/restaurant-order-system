package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
  private String id;
  private String name;
  private String description;
  private int displayOrder;
  private boolean active;
}
