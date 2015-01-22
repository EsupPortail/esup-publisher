package org.esupportail.publisher.utils;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * Uses the COMPRESSED row format in an InnoDB engine, needed for long index support with UTF-8
 * From @author Raymond Bourges
 */
public class MySQL5InnoDBCompressedDialect extends MySQL5InnoDBDialect {

    public MySQL5InnoDBCompressedDialect() {
        super();
    }

    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB ROW_FORMAT=COMPRESSED";
    }
}
