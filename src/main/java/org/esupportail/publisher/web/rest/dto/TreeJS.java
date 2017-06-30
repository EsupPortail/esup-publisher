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
package org.esupportail.publisher.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by jgribonvald on 29/05/15.
 */
@Data
@EqualsAndHashCode(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeJS implements Serializable{


    private String id;

    private String text;

    private String Type;

    private Object children;

    private String parent;

    private State state = new State();

    private LiAttr li_attr = new LiAttr();

    private AAttr a_attr = new AAttr();

    @Data
    public class State {
        private boolean opened;
        private boolean disabled;
        private boolean selected;
        private boolean checked;
        private boolean undetermined;
    }

    @Data
    public class LiAttr {
        private String base;
        private boolean isLeaf;

        public boolean getIsLeaf() {
            return isLeaf;
        }
    }

    @Data
    public class AAttr {
        private String title;
        private Object model;

        public Object getModel() {
            return model;
        }
        public void setModel(Object model) {
            this.model = model;
        }
    }

    public Object getChildren() {
        return children;
    }

    public void setChildren(Object children) {
        this.children = children;
    }
}
