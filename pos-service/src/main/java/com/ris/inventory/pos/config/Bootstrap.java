package com.ris.inventory.pos.config;

import com.ris.inventory.pos.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    @Autowired
    private DiscoveryService discoveryService;

    @Override
    public void run(String... args) throws Exception {
        discoveryService.createMockInventory();
    }
}
