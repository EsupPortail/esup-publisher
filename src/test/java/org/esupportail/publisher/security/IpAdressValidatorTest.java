/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.security;

import java.util.Arrays;

import org.esupportail.publisher.config.bean.validator.IpAddressValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IpAdressValidatorTest {

    IpAddressValidator validator  = new IpAddressValidator();

    @Test
    public void testIpValid() {
        String[] ips = {
                "::1",
                "127.0.0.1",
                "192.168.1.1",
                "192.168.1.1/20",
                "255.255.255.255",
                "2001:0db8:0000:0000:0000:ff00:0042:8329"
        };
        Assertions.assertTrue(validator.isValid(Arrays.asList(ips), null),
                "List of IPs are not validated");
    }

    @Test
    public void testIpNotValid() {
        String[] ips = {
                "127.0.0:1",
                "192.168.1.260",
                "192.168.1.1/48",
                "255.255.255.256",
                "2001:0db8:0000:0000:0000::ff00:0042:8329"
        };

        // should test one by one the list of ips to get which one doesn't validate
        for (String ip: ips) {
            Assertions.assertFalse(validator.isValid(ip, null), String.format("IP %s is not validated", ip));
        }
    }
}