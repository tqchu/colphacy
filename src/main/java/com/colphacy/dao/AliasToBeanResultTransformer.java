package com.colphacy.dao;

import com.google.common.base.CaseFormat;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.PropertyAccessorFactory;

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
                    PropertyAccessorFactory.forDirectFieldAccess(result).setPropertyValue(camelCaseAlias, tuple[i]);
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

