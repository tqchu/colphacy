package com.colphacy.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String name;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String packing;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String manufacturer;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String brandOrigin;

    @Length(max = 255)
    private String indications;

    @Length(max = 255)
    private String shortDescription;

    private String fullDescription;

    @NotNull
    @NotBlank
    @Length(max = 50)
    private String registrationNumber;

    @NotNull
    @Size(min = 1)
    @OneToMany(mappedBy = "product", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    })
    @JsonManagedReference
    private List<Ingredient> ingredients = new ArrayList<>();

    @NotNull
    @NotBlank
    private String uses;

    @NotNull
    @NotBlank
    private String usage;


    private String sideEffects;


    private String notes;

    @NotNull
    @NotBlank
    private String storage;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @NotNull
    @Size(min = 1)
    @OneToMany(mappedBy = "product", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    })
    @JsonManagedReference
    private List<ProductUnit> productUnits = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    })
    @JsonManagedReference
    private List<ProductImage> images = new ArrayList<>();

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setProduct(this);
    }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setProduct(null);
    }

    public void addProductUnit(ProductUnit productUnit) {
        productUnits.add(productUnit);
        productUnit.setProduct(this);
    }

    public void removeProductUnit(ProductUnit productUnit) {
        productUnits.remove(productUnit);
        productUnit.setProduct(null);
    }

    public void addProductImage(ProductImage productImage) {
        images.add(productImage);
        productImage.setProduct(this);
    }

    public void removeProductImage(ProductImage productImage) {
        images.remove(productImage);
        productImage.setProduct(null);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            addIngredient(ingredient);
        }
    }

    public void setProductUnits(List<ProductUnit> productUnits) {
        this.productUnits = new ArrayList<>();

        for (ProductUnit productUnit : productUnits) {
            addProductUnit(productUnit);
        }
    }

    public void setImages(List<ProductImage> images) {
        this.images = new ArrayList<>();

        for (ProductImage productImage : images) {
            addProductImage(productImage);
        }
    }

}
