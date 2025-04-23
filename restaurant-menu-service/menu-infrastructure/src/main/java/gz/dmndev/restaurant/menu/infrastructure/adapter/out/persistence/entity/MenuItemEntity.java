package gz.dmndev.restaurant.menu.infrastructure.adapter.out.persistence.entity;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "menu_items")
public class MenuItemEntity {
  @Id private String id;
  private String name;
  private String description;
  private BigDecimal price;

  @Field("category_id")
  private String categoryId;

  private List<String> tags;
  private List<String> allergens;
  private boolean available;

  @Field("image_url")
  private String imageUrl;
}
