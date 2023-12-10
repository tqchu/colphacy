package com.colphacy.dao;

import com.google.common.base.CaseFormat;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.PropertyAccessorFactory;

import java.time.ZoneOffset;
import java.util.List;

public class AliasToBeanResultTransformer implements ResultTransformer {

    private final Class<?> resultClass;

    public AliasToBeanResultTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            Object result = resultClass.newInstance();
            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                if (alias != null) {
                    // Convert snake_case to camelCase
                    String camelCaseAlias = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, alias);

                    // Check if the tuple element is of type java.sql.Timestamp
                    if (tuple[i] instanceof java.sql.Timestamp) {
                        // Convert java.sql.Timestamp to java.time.ZonedDateTime
                        // Your existing code
                        java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp) tuple[i];
                        java.time.Instant instant = sqlTimestamp.toInstant();
                        java.time.ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);

                        // Set the property value as ZonedDateTime
                        PropertyAccessorFactory.forDirectFieldAccess(result).setPropertyValue(camelCaseAlias, zonedDateTime);
                    } else if (tuple[i] instanceof java.sql.Date) {
                        // Convert java.sql.Date to java.time.LocalDate
                        java.sql.Date sqlDate = (java.sql.Date) tuple[i];
                        java.time.LocalDate localDate = sqlDate.toLocalDate();

                        // Set the property value as LocalDate
                        PropertyAccessorFactory.forDirectFieldAccess(result).setPropertyValue(camelCaseAlias, localDate);
                    } else {
                        // Set the property value as is
                        PropertyAccessorFactory.forDirectFieldAccess(result).setPropertyValue(camelCaseAlias, tuple[i]);
                    }
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }
}

