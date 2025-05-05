package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest;

import gz.dmndev.restaurant.menu.application.port.in.CreateCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.DeleteCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.GetCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.UpdateCategoryUseCase;
import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryResponse;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.mapper.CategoryRestMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "API for category management")
public class CategoryController {

  private final CreateCategoryUseCase createCategoryUseCase;
  private final GetCategoryUseCase getCategoryUseCase;
  private final UpdateCategoryUseCase updateCategoryUseCase;
  private final DeleteCategoryUseCase deleteCategoryUseCase;
  private final CategoryRestMapper mapper;

  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(
      @Valid @RequestBody CategoryRequest request) {
    Category category = mapper.toDomain(request);
    Category createdCategory = createCategoryUseCase.createCategory(category);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(createdCategory));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
    return getCategoryUseCase
        .getCategoryById(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> getAllCategories(
      @RequestParam(value = "active", required = false) Boolean active) {

    List<Category> categories;
    if (active != null && active) {
      categories = getCategoryUseCase.getAllActiveCategories();
    } else {
      categories = getCategoryUseCase.getAllCategories();
    }

    List<CategoryResponse> response =
        categories.stream().map(mapper::toResponse).collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable String id, @Valid @RequestBody CategoryRequest request) {

    return getCategoryUseCase
        .getCategoryById(id)
        .map(
            existingCategory -> {
              existingCategory = mapper.updateDomainFromRequest(existingCategory, request);
              Category updatedCategory = updateCategoryUseCase.updateCategory(existingCategory);
              return ResponseEntity.ok(mapper.toResponse(updatedCategory));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<CategoryResponse> updateCategoryStatus(
      @PathVariable String id, @RequestParam("active") boolean active) {

    try {
      Category updatedCategory = updateCategoryUseCase.updateCategoryStatus(id, active);
      return ResponseEntity.ok(mapper.toResponse(updatedCategory));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
    try {
      deleteCategoryUseCase.deleteCategory(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
  }
}
