package com.yudu.rest.request.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;
import java.util.Map;

/**
 * @author rjm
 */
public class QueryStringBuilder
{
    private static final String TIMESTAMP_KEY = "timestamp";

    private String rawQueryString;

    public QueryStringBuilder(String rawQueryString) {
        this.rawQueryString = removeLeadingQuestionMark(rawQueryString);
    }

    /**
     * @return The query string with a timestamp parameter added and with all parameters sorted into alphabetical order.
     */
    public String canonicalQueryString() {
        return buildQueryString(splitQuery());
    }

    /**
     * @return The query string with a timestamp parameter added.
     */
    public String queryStringWithTimeStamp() {
        StringBuilder builder = new StringBuilder(rawQueryString);
        if (!rawQueryString.isEmpty())
            builder.append('&');
        builder.append(TIMESTAMP_KEY).append('=').append(epochTime());
        return builder.toString();
    }

    /**
     * To make the parsing simpler.
     */
    private String removeLeadingQuestionMark(String queryString) {
        if (StringUtils.isEmpty(queryString))
            return "";
        if (queryString.startsWith("?"))
            return queryString.substring(1, queryString.length());
        return queryString;
    }

    /**
     * Split the query string into individual parameters and then into key/value pairs.
     */
    private Multimap<String, String> splitQuery() {
        // TreeMultimap automatically sorts the entries by their natural ordering (lexicographic for Strings).
        Multimap<String, String> parameters = TreeMultimap.create();
        String[] pairs = queryStringWithTimeStamp().split("&");
        for (String pair : pairs) {
            int index = pair.indexOf("=");
            String key = index > 0 ? pair.substring(0, index) : pair;
            String value = index > 0 && pair.length() > index + 1 ? pair.substring(index + 1) : null;
            parameters.put(key, value);
        }
        return parameters;
    }

    /**
     * Rebuild the key=value pairs from the sorted multimap
     */
    private List<String> buildKeyValuePairs(Multimap<String, String> parameters) {
        List<String> requestParamPairs = Lists.newArrayList();
        for (Map.Entry<String, String> parameter : parameters.entries())
        {
            if (parameter.getValue() != null)
            {
                requestParamPairs.add(String.format("%s=%s", parameter.getKey(), parameter.getValue()));
            }
            else
            {
                requestParamPairs.add(parameter.getKey());
            }
        }
        return requestParamPairs;
    }

    /**
     * Reassemble the query string from the key=value pairs.
     */
    private String buildQueryString(Multimap<String, String> parameters)
    {
        List<String> requestParamPairs = buildKeyValuePairs(parameters);
        if (requestParamPairs.isEmpty())
        {
            return "";
        }
        else
        {
            return "?" + StringUtils.join(requestParamPairs, "&");
        }
    }

    /**
     * @return The current unix epoch time in seconds.
     */
    private Long epochTime() {
        return new DateTime(DateTimeZone.UTC).getMillis() / 1000L;
    }
}
