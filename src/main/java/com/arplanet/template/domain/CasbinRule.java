package com.arplanet.template.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CasbinRule {

    @Id
    private Long id;

    private String pType;

    private String v0;

    private String v1;

    private String v2;

    private String v3;

    private String v4;

    private String v5;
}
