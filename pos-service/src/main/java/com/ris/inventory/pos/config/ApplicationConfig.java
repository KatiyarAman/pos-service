package com.ris.inventory.pos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("config")
public class ApplicationConfig {

    @Value("${ris.server.ip}")
    private String serverIp;

    @Value("${ris.gateway.server.port}")
    private String serverPort;

    @Value("${ris.inventory.download.path}")
    private String downloadPath;

    public String getDownloadPath() {
        return downloadPath;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getServerIp() {
        return serverIp;
    }
}
