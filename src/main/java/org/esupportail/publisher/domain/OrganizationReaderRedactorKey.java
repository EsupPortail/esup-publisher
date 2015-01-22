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
package org.esupportail.publisher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 25 juin 2014
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrganizationReaderRedactorKey implements Serializable {

    /** */
    private static final long serialVersionUID = -8117536629158742023L;

    /** The organization. */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JsonManagedReference("organization-key")
    @JoinColumn(name="entity_id")
    private Organization organization;
    /** The reader. */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JsonManagedReference("reader-key")
    @JoinColumn(name="reader_id")
    private Reader reader;
    /** The Redactor. */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JsonManagedReference("redactor-key")
    @JoinColumn(name="redactor_id")
    private Redactor redactor;

}
