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

import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class HasIpRangeExpressionCreator {

    private final Set<String> ipRanges;
    @Getter
    private final String expression;

    public HasIpRangeExpressionCreator(Set<String> ipRanges) {
        this.ipRanges = ipRanges;
        this.expression = createHasIpRangeExpression(ipRanges);
        log.info("Constructed {}", this);
    }

    private String createHasIpRangeExpression(final Set<String> ipRanges) {
        return ipRanges.stream()
                .collect(Collectors.joining("') or hasIpAddress('", "hasIpAddress('","')"));
    }
}