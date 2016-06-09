package org.esupportail.publisher.service.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Component("appIpVariableHolder")
public class IpVariableHolder {

    @Value("${app.authorizedServices.ipRange}")
    private String ipRange;

    public String getIpRange() {
        return ipRange;
    }
}
