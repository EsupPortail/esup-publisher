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
package org.esupportail.publisher.web.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.esupportail.publisher.web.rest.util.ISO8601LocalDateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.util.RFC822LocalDateTimeXmlAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class DateAdaptersTest {
    private final String RFC822_DATE = "Sun, 9 Feb 2020 11:10:12 +0100";
    private final String ISO8601_DATE = "2020-02-09T11:10:12+01:00";

    @Test
    public void testRFC822(){
        Instant mydate = Instant.parse("2020-02-09T10:10:12.000Z");
        RFC822LocalDateTimeXmlAdapter adapter = new RFC822LocalDateTimeXmlAdapter();
        assertThat(RFC822_DATE, equalTo(adapter.marshal(mydate)));

        assertThat(mydate, equalTo(adapter.unmarshal(RFC822_DATE)));
    }

    @Test
    public void testISO8601() throws Exception {
        Instant mydate = Instant.parse("2020-02-09T10:10:12Z");
        ISO8601LocalDateTimeXmlAdapter adapter = new ISO8601LocalDateTimeXmlAdapter();
        assertThat(ISO8601_DATE, equalTo(adapter.marshal(mydate)));

        assertThat(mydate, equalTo(adapter.unmarshal(ISO8601_DATE)));
    }

    @Test
    public void testLocalDateToISO8601() throws Exception {
        ISO8601LocalDateTimeXmlAdapter adapter = new ISO8601LocalDateTimeXmlAdapter();
        Instant mydate = LocalDate.of(2020,02,10).atStartOfDay(ZoneId.systemDefault()).toInstant();
        assertThat("2020-02-10T00:00:00+01:00", equalTo(adapter.marshal(mydate)));
    }

}