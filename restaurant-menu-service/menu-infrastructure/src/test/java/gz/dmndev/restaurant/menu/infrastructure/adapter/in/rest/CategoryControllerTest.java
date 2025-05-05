package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gz.dmndev.restaurant.menu.application.port.in.CreateCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.DeleteCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.GetCategoryUseCase;
import gz.dmndev.restaurant.menu.application.port.in.UpdateCategoryUseCase;
import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryResponse;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.mapper.CategoryRestMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(CategoryController.class)
@ContextConfiguration(
    classes = {
      CategoryController.class,
    })
class CategoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateCategoryUseCase createCategoryUseCase;

  @MockitoBean private GetCategoryUseCase getCategoryUseCase;

  @MockitoBean private UpdateCategoryUseCase updateCategoryUseCase;

  @MockitoBean private DeleteCategoryUseCase deleteCategoryUseCase;

  @MockitoBean private CategoryRestMapper mapper;

  private Category category;
  private CategoryRequest categoryRequest;

  @BeforeEach
  void setUp() {
    category =
        Category.builder()
            .id("category-1")
            .name("Appetizers")
            .description("Starters")
            .displayOrder(1)
            .active(true)
            .build();

    categoryRequest =
        CategoryRequest.builder()
            .name("Appetizers")
            .description("Starters")
            .displayOrder(1)
            .active(true)
            .build();
  }

  @Test
  void createCategory_ShouldReturnCreatedCategory() throws Exception {
    // Arrange
    when(mapper.toDomain(any(CategoryRequest.class))).thenReturn(category);
    when(createCategoryUseCase.createCategory(any(Category.class))).thenReturn(category);
    when(mapper.toResponse(any(Category.class)))
        .thenReturn(
            CategoryResponse.builder()
                .id("category-1")
                .name("Appetizers")
                .description("Starters")
                .displayOrder(1)
                .active(true)
                .build());

    // Act & Assert
    mockMvc
        .perform(
            post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("category-1"))
        .andExpect(jsonPath("$.name").value("Appetizers"));

    verify(createCategoryUseCase).createCategory(any(Category.class));
  }

  @Test
  void getCategoryById_WhenExists_ShouldReturnCategory() throws Exception {
    // Arrange
    when(getCategoryUseCase.getCategoryById("category-1")).thenReturn(Optional.of(category));
    when(mapper.toResponse(any(Category.class)))
        .thenReturn(
            CategoryResponse.builder()
                .id("category-1")
                .name("Appetizers")
                .description("Starters")
                .displayOrder(1)
                .active(true)
                .build());

    // Act & Assert
    mockMvc
        .perform(get("/categories/category-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("category-1"))
        .andExpect(jsonPath("$.name").value("Appetizers"));
  }

  @Test
  void getCategoryById_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    when(getCategoryUseCase.getCategoryById("non-existent")).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/categories/non-existent")).andExpect(status().isNotFound());
  }

  @Test
  void getAllCategories_ShouldReturnAllCategories() throws Exception {
    // Arrange
    Category category2 =
        Category.builder()
            .id("category-2")
            .name("Main Courses")
            .displayOrder(2)
            .active(true)
            .build();

    when(getCategoryUseCase.getAllCategories()).thenReturn(Arrays.asList(category, category2));
    when(mapper.toResponse(category))
        .thenReturn(
            CategoryResponse.builder()
                .id("category-1")
                .name("Appetizers")
                .displayOrder(1)
                .active(true)
                .build());
    when(mapper.toResponse(category2))
        .thenReturn(
            CategoryResponse.builder()
                .id("category-2")
                .name("Main Courses")
                .displayOrder(2)
                .active(true)
                .build());

    // Act & Assert
    mockMvc
        .perform(get("/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value("category-1"))
        .andExpect(jsonPath("$[1].id").value("category-2"));
  }

  @Test
  void getAllActiveCategories_ShouldReturnActiveCategories() throws Exception {
    // Arrange
    when(getCategoryUseCase.getAllActiveCategories())
        .thenReturn(Collections.singletonList(category));
    when(mapper.toResponse(any(Category.class)))
        .thenReturn(
            CategoryResponse.builder()
                .id("category-1")
                .name("Appetizers")
                .displayOrder(1)
                .active(true)
                .build());

    // Act & Assert
    mockMvc
        .perform(get("/categories?active=true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value("category-1"));
  }

  @Test
  void updateCategory_WhenExists_ShouldUpdateAndReturnCategory() throws Exception {
    // Arrange
    Category updatedCategory =
        Category.builder()
            .id("category-1")
            .name("Appetizers Updated")
            .displayOrder(1)
            .active(true)
            .build();

    when(getCategoryUseCase.getCategoryById("category-1")).thenReturn(Optional.of(category));

    when(mapper.updateDomainFromRequest(any(Category.class), any(CategoryRequest.class)))
        .thenReturn(updatedCategory);

    when(updateCategoryUseCase.updateCategory(any(Category.class))).thenReturn(updatedCategory);
    when(mapper.toResponse(any(Category.class)))
        .thenReturn(
            CategoryResponse.builder()
                .id("category-1")
                .name("Appetizers Updated")
                .displayOrder(1)
                .active(true)
                .build());

    // Act & Assert
    mockMvc
        .perform(
            put("/categories/category-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("category-1"))
        .andExpect(jsonPath("$.name").value("Appetizers Updated"));

    // Verificaciones adicionales
    verify(mapper).updateDomainFromRequest(any(Category.class), any(CategoryRequest.class));
    verify(updateCategoryUseCase).updateCategory(any(Category.class));
    verify(mapper).toResponse(any(Category.class));
  }

  @Test
  void updateCategory_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    when(getCategoryUseCase.getCategoryById("non-existent")).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc
        .perform(
            put("/categories/non-existent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
        .andExpect(status().isNotFound());

    verify(updateCategoryUseCase, never()).updateCategory(any(Category.class));
  }

  @Test
  void updateCategoryStatus_WhenExists_ShouldUpdateStatus() throws Exception {
    // Arrange
    when(updateCategoryUseCase.updateCategoryStatus("category-1", false)).thenReturn(category);
    when(mapper.toResponse(any(Category.class)))
        .thenReturn(
            CategoryResponse.builder()
                .id("category-1")
                .name("Appetizers")
                .displayOrder(1)
                .active(false)
                .build());

    // Act & Assert
    mockMvc
        .perform(patch("/categories/category-1/status?active=false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));

    verify(updateCategoryUseCase).updateCategoryStatus("category-1", false);
  }

  @Test
  void updateCategoryStatus_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    when(updateCategoryUseCase.updateCategoryStatus("non-existent", false))
        .thenThrow(new IllegalArgumentException("Category not found"));

    // Act & Assert
    mockMvc
        .perform(patch("/categories/non-existent/status?active=false"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteCategory_WhenExists_ShouldDeleteAndReturnNoContent() throws Exception {
    // Arrange
    doNothing().when(deleteCategoryUseCase).deleteCategory("category-1");

    // Act & Assert
    mockMvc.perform(delete("/categories/category-1")).andExpect(status().isNoContent());

    verify(deleteCategoryUseCase).deleteCategory("category-1");
  }

  @Test
  void deleteCategory_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    doThrow(new IllegalArgumentException("Category not found"))
        .when(deleteCategoryUseCase)
        .deleteCategory("non-existent");

    // Act & Assert
    mockMvc.perform(delete("/categories/non-existent")).andExpect(status().isNotFound());
  }

  @Test
  void deleteCategory_WhenHasItems_ShouldReturnConflict() throws Exception {
    // Arrange
    doThrow(new IllegalStateException("Cannot delete category with items"))
        .when(deleteCategoryUseCase)
        .deleteCategory("category-1");

    // Act & Assert
    mockMvc.perform(delete("/categories/category-1")).andExpect(status().isConflict());
  }
}
