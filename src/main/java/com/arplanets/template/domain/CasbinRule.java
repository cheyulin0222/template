package com.arplanets.template.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "casbin_rule")
public class CasbinRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ptype;

    private String v0;

    private String v1;

    private String v2;

    private String v3;

    private String v4;

    private String v5;
}
