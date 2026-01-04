package com.merchant.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "crops")
@Data
@NoArgsConstructor
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal pricePerKg;

    @Column(nullable = false)
    private Double quantityAvailable; // in Kg

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;
}
