package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
  @NotBlank(message = "Name is required")
  private String name;

  private String description;

  @NotNull(message = "Display order is required")
  private Integer displayOrder;

  private boolean active = true;
}
