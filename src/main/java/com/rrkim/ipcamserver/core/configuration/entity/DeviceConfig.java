package com.rrkim.ipcamserver.core.configuration.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table
@ToString
public class DeviceConfig {

    @Id
    @Column(name = "config_id", unique = true)
    private String configId;

    @Column(name = "config_value", nullable = false, updatable = false)
    private String configValue;

}
