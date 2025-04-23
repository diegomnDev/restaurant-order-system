package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gz.dmndev.restaurant.menu.application.port.in.CreateMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.DeleteMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.GetMenuItemUseCase;
import gz.dmndev.restaurant.menu.application.port.in.UpdateMenuItemUseCase;
import gz.dmndev.restaurant.menu.domain.model.Category;
import gz.dmndev.restaurant.menu.domain.model.MenuItem;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.CategoryResponse;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemRequest;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.dto.MenuItemResponse;
import gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.mapper.MenuItemRestMapper;
import java.math.BigDecimal;
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
@WebMvcTest(MenuItemController.class)
@ContextConfiguration(
    classes = {
      MenuItemController.class,
    })
class MenuItemControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateMenuItemUseCase createMenuItemUseCase;

  @MockitoBean private GetMenuItemUseCase getMenuItemUseCase;

  @MockitoBean private UpdateMenuItemUseCase updateMenuItemUseCase;

  @MockitoBean private DeleteMenuItemUseCase deleteMenuItemUseCase;

  @MockitoBean private MenuItemRestMapper mapper;

  private MenuItem menuItem;
  private MenuItemRequest menuItemRequest;
  private Category category;

  @BeforeEach
  void setUp() {
    category = Category.builder().id("category-1").name("Appetizers").active(true).build();

    menuItem =
        MenuItem.builder()
            .id("item-1")
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .category(category)
            .tags(Arrays.asList("vegetarian", "appetizer"))
            .available(true)
            .build();

    menuItemRequest =
        MenuItemRequest.builder()
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .categoryId("category-1")
            .tags(Arrays.asList("vegetarian", "appetizer"))
            .available(true)
            .build();
  }

  @Test
  void createMenuItem_ShouldReturnCreatedMenuItem() throws Exception {
    // Arrange
    when(mapper.toDomain(any(MenuItemRequest.class))).thenReturn(menuItem);
    when(createMenuItemUseCase.createMenuItem(any(MenuItem.class))).thenReturn(menuItem);

    MenuItemResponse response =
        MenuItemResponse.builder()
            .id("item-1")
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .category(
                CategoryResponse.builder().id("category-1").name("Appetizers").active(true).build())
            .tags(Arrays.asList("vegetarian", "appetizer"))
            .available(true)
            .build();

    when(mapper.toResponse(any(MenuItem.class))).thenReturn(response);

    // Act & Assert
    mockMvc
        .perform(
            post("/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuItemRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("item-1"))
        .andExpect(jsonPath("$.name").value("Spring Rolls"))
        .andExpect(jsonPath("$.category.id").value("category-1"));

    verify(createMenuItemUseCase).createMenuItem(any(MenuItem.class));
  }

  @Test
  void getMenuItemById_WhenExists_ShouldReturnMenuItem() throws Exception {
    // Arrange
    when(getMenuItemUseCase.getMenuItemById("item-1")).thenReturn(Optional.of(menuItem));

    MenuItemResponse response =
        MenuItemResponse.builder()
            .id("item-1")
            .name("Spring Rolls")
            .description("Vegetable spring rolls")
            .price(new BigDecimal("5.99"))
            .category(
                CategoryResponse.builder().id("category-1").name("Appetizers").active(true).build())
            .tags(Arrays.asList("vegetarian", "appetizer"))
            .available(true)
            .build();

    when(mapper.toResponse(any(MenuItem.class))).thenReturn(response);

    // Act & Assert
    mockMvc
        .perform(get("/menu-items/item-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("item-1"))
        .andExpect(jsonPath("$.name").value("Spring Rolls"))
        .andExpect(jsonPath("$.category.id").value("category-1"));
  }

  @Test
  void getMenuItemById_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    when(getMenuItemUseCase.getMenuItemById("non-existent")).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/menu-items/non-existent")).andExpect(status().isNotFound());
  }

  @Test
  void getAllMenuItems_ShouldReturnAllItems() throws Exception {
    // Arrange
    MenuItem menuItem2 =
        MenuItem.builder()
            .id("item-2")
            .name("Chicken Wings")
            .price(new BigDecimal("8.99"))
            .category(category)
            .available(true)
            .build();

    when(getMenuItemUseCase.getAllMenuItems()).thenReturn(Arrays.asList(menuItem, menuItem2));

    MenuItemResponse response1 =
        MenuItemResponse.builder()
            .id("item-1")
            .name("Spring Rolls")
            .price(new BigDecimal("5.99"))
            .category(CategoryResponse.builder().id("category-1").name("Appetizers").build())
            .available(true)
            .build();

    MenuItemResponse response2 =
        MenuItemResponse.builder()
            .id("item-2")
            .name("Chicken Wings")
            .price(new BigDecimal("8.99"))
            .category(CategoryResponse.builder().id("category-1").name("Appetizers").build())
            .available(true)
            .build();

    when(mapper.toResponse(menuItem)).thenReturn(response1);
    when(mapper.toResponse(menuItem2)).thenReturn(response2);

    // Act & Assert
    mockMvc
        .perform(get("/menu-items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value("item-1"))
        .andExpect(jsonPath("$[1].id").value("item-2"));
  }

  @Test
  void getMenuItemsByCategory_ShouldReturnCategoryItems() throws Exception {
    // Arrange
    when(getMenuItemUseCase.getMenuItemsByCategory("category-1"))
        .thenReturn(Collections.singletonList(menuItem));

    MenuItemResponse response =
        MenuItemResponse.builder()
            .id("item-1")
            .name("Spring Rolls")
            .price(new BigDecimal("5.99"))
            .category(CategoryResponse.builder().id("category-1").name("Appetizers").build())
            .available(true)
            .build();

    when(mapper.toResponse(any(MenuItem.class))).thenReturn(response);

    // Act & Assert
    mockMvc
        .perform(get("/menu-items?category=category-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value("item-1"));
  }

  @Test
  void searchMenuItems_ShouldReturnMatchingItems() throws Exception {
    // Arrange
    when(getMenuItemUseCase.searchMenuItems("Spring"))
        .thenReturn(Collections.singletonList(menuItem));

    MenuItemResponse response =
        MenuItemResponse.builder()
            .id("item-1")
            .name("Spring Rolls")
            .price(new BigDecimal("5.99"))
            .category(CategoryResponse.builder().id("category-1").name("Appetizers").build())
            .available(true)
            .build();

    when(mapper.toResponse(any(MenuItem.class))).thenReturn(response);

    // Act & Assert
    mockMvc
        .perform(get("/menu-items?search=Spring"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Spring Rolls"));
  }

  @Test
  void updateMenuItem_WhenExists_ShouldUpdateAndReturnMenuItem() throws Exception {
    // Arrange
    when(getMenuItemUseCase.getMenuItemById("item-1")).thenReturn(Optional.of(menuItem));
    when(mapper.updateDomainFromRequest(any(MenuItem.class), any(MenuItemRequest.class)))
        .thenReturn(menuItem);
    when(updateMenuItemUseCase.updateMenuItem(any(MenuItem.class))).thenReturn(menuItem);

    MenuItemResponse response =
        MenuItemResponse.builder()
            .id("item-1")
            .name("Spring Rolls Updated")
            .price(new BigDecimal("6.99"))
            .category(CategoryResponse.builder().id("category-1").name("Appetizers").build())
            .available(true)
            .build();

    when(mapper.toResponse(any(MenuItem.class))).thenReturn(response);

    // Act & Assert
    mockMvc
        .perform(
            put("/menu-items/item-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuItemRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("item-1"))
        .andExpect(jsonPath("$.name").value("Spring Rolls Updated"));

    verify(updateMenuItemUseCase).updateMenuItem(any(MenuItem.class));
  }

  @Test
  void updateMenuItem_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    when(getMenuItemUseCase.getMenuItemById("non-existent")).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc
        .perform(
            put("/menu-items/non-existent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuItemRequest)))
        .andExpect(status().isNotFound());

    verify(updateMenuItemUseCase, never()).updateMenuItem(any(MenuItem.class));
  }

  @Test
  void updateMenuItemAvailability_WhenExists_ShouldUpdateAvailability() throws Exception {
    // Arrange
    MenuItem updatedItem =
        MenuItem.builder()
            .id("item-1")
            .name("Spring Rolls")
            .price(new BigDecimal("5.99"))
            .category(category)
            .available(false) // Changed to false
            .build();

    when(updateMenuItemUseCase.updateMenuItemAvailability("item-1", false)).thenReturn(updatedItem);

    MenuItemResponse response =
        MenuItemResponse.builder()
            .id("item-1")
            .name("Spring Rolls")
            .price(new BigDecimal("5.99"))
            .category(CategoryResponse.builder().id("category-1").name("Appetizers").build())
            .available(false)
            .build();

    when(mapper.toResponse(any(MenuItem.class))).thenReturn(response);

    // Act & Assert
    mockMvc
        .perform(patch("/menu-items/item-1/availability?available=false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available").value(false));

    verify(updateMenuItemUseCase).updateMenuItemAvailability("item-1", false);
  }

  @Test
  void updateMenuItemAvailability_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    when(updateMenuItemUseCase.updateMenuItemAvailability("non-existent", false))
        .thenThrow(new IllegalArgumentException("Menu item not found"));

    // Act & Assert
    mockMvc
        .perform(patch("/menu-items/non-existent/availability?available=false"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteMenuItem_WhenExists_ShouldDeleteAndReturnNoContent() throws Exception {
    // Arrange
    doNothing().when(deleteMenuItemUseCase).deleteMenuItem("item-1");

    // Act & Assert
    mockMvc.perform(delete("/menu-items/item-1")).andExpect(status().isNoContent());

    verify(deleteMenuItemUseCase).deleteMenuItem("item-1");
  }

  @Test
  void deleteMenuItem_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    doThrow(new IllegalArgumentException("Menu item not found"))
        .when(deleteMenuItemUseCase)
        .deleteMenuItem("non-existent");

    // Act & Assert
    mockMvc.perform(delete("/menu-items/non-existent")).andExpect(status().isNotFound());
  }
}
