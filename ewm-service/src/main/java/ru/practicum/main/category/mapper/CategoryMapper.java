package ru.practicum.main.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.model.Category;

@Component
public class CategoryMapper {

    public Category toEntity(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
