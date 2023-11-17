package com.colphacy.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ImportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "import_id")
    private Import anImport;

    @NotNull
    @OneToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @NotNull
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull(message = "Phải có thông tin về số lượng")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull
    private Integer baseQuantity;

    @NotNull
    private LocalDate expirationDate;

    @NotNull
    @Positive(message = "Giá nhập phải lớn hơn 0")
    private Double importPrice;
}
