package com.colphacy.dao;

import com.colphacy.dto.product.ProductSimpleDTO;
import com.colphacy.dto.review.ReviewAdminListViewDTO;
import com.colphacy.dto.review.ReviewReplyAdminListViewDTO;
import com.google.common.base.CaseFormat;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.PropertyAccessorFactory;

import java.lang.reflect.Field;
import java.math.BigInteger;
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

                    if (resultClass == ReviewAdminListViewDTO.class) {
                        if (camelCaseAlias.startsWith("product")) {
                            setProductProperty(result, camelCaseAlias, tuple[i]);
                        } else if (camelCaseAlias.startsWith("replyReview") || camelCaseAlias.startsWith("employee")) {
                            setRepliedReviewProperty(result, camelCaseAlias, tuple[i]);
                        } else {
                            setStandardProperty(tuple[i], result, camelCaseAlias);
                        }
                    } else {
                        setStandardProperty(tuple[i], result, camelCaseAlias);
                    }
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setStandardProperty(Object value, Object result, String camelCaseAlias) {
        // Check if the tuple element is of type java.sql.Timestamp
        if (value instanceof java.sql.Timestamp) {
            // Convert java.sql.Timestamp to java.time.ZonedDateTime
            // Your existing code
            java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp) value;
            java.time.Instant instant = sqlTimestamp.toInstant();
            java.time.ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);

            // Set the property value as ZonedDateTime
            PropertyAccessorFactory.forDirectFieldAccess(result).setPropertyValue(camelCaseAlias, zonedDateTime);
        } else if (value instanceof java.sql.Date) {
            // Convert java.sql.Date to java.time.LocalDate
            java.sql.Date sqlDate = (java.sql.Date) value;
            java.time.LocalDate localDate = sqlDate.toLocalDate();

            // Set the property value as LocalDate
            PropertyAccessorFactory.forDirectFieldAccess(result).setPropertyValue(camelCaseAlias, localDate);
        } else {
            // Set the property value as is
            PropertyAccessorFactory.forDirectFieldAccess(result).setPropertyValue(camelCaseAlias, value);
        }
    }

    @Override
    public List transformList(List collection) {
        return collection;
    }

    @SuppressWarnings("rawtypes")
    private void setProductProperty(Object resultInstance, String alias, Object value) throws NoSuchFieldException, IllegalAccessException {
        // Special handling for the 'product' property
        Field field = resultClass.getDeclaredField("product");
        field.setAccessible(true);

        ProductSimpleDTO productDTO = (ProductSimpleDTO) field.get(resultInstance);
        if (productDTO == null) {
            productDTO = new ProductSimpleDTO();
            field.set(resultInstance, productDTO);
        }

        if (alias.equals("productId")) {
            productDTO.setId(((BigInteger) value).longValue());
        } else if (alias.equals("productName")) {
            productDTO.setName((String) value);
        } else if (alias.equals("productImage")) {
            productDTO.setImage((String) value);
        }
    }

    @SuppressWarnings("rawtypes")
    private void setRepliedReviewProperty(Object resultInstance, String alias, Object value) throws NoSuchFieldException, IllegalAccessException {
        // Special handling for the 'product' property
        Field field = resultClass.getDeclaredField("repliedReview");
        field.setAccessible(true);

        ReviewReplyAdminListViewDTO reviewReplyAdminListViewDTO = (ReviewReplyAdminListViewDTO) field.get(resultInstance);
        if (reviewReplyAdminListViewDTO == null) {
            reviewReplyAdminListViewDTO = new ReviewReplyAdminListViewDTO();
            field.set(resultInstance, reviewReplyAdminListViewDTO);
        }

        if (value != null) {
            if (alias.equals("replyReviewId")) {
                reviewReplyAdminListViewDTO.setId(((BigInteger) value).longValue());
            } else if (alias.equals("replyReviewContent")) {
                reviewReplyAdminListViewDTO.setContent((String) value);
            } else if (alias.equals("replyReviewCreatedTime")) {
                java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp) value;
                java.time.Instant instant = sqlTimestamp.toInstant();
                java.time.ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
                reviewReplyAdminListViewDTO.setCreatedTime(zonedDateTime);
            } else if (alias.equals("employeeId")) {
                reviewReplyAdminListViewDTO.setEmployeeId(((BigInteger) value).longValue());
            } else if (alias.equals("employeeName")) {
                reviewReplyAdminListViewDTO.setEmployeeName((String) value);
            }
        } else {
            // Set repliedReview to null when there is no replied review
            field.set(resultInstance, null);
        }
    }
}

