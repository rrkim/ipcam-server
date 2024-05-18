package com.rrkim.ipcamserver.core.configuration.repository;

import com.rrkim.ipcamserver.core.configuration.entity.DeviceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DeviceConfigRepository")
public interface DeviceConfigRepository extends JpaRepository<DeviceConfig, Long> {
    DeviceConfig findByConfigId(String configId);
}
