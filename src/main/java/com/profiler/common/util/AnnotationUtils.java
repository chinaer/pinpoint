package com.profiler.common.util;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.profiler.common.AnnotationKey;
import com.profiler.common.ServiceType;
import com.profiler.common.bo.AnnotationBo;
import com.profiler.common.bo.Span;

public class AnnotationUtils {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss SSS";

    public static String longToDateStr(long date, String fmt) {
        SimpleDateFormat format = new SimpleDateFormat((fmt == null) ? FORMAT : fmt);
        return format.format(new Date(date));
    }

    public static String longLongToUUID(long mostTraceId, long leastTraceId) {
        return new UUID(mostTraceId, leastTraceId).toString();
    }

    private static Comparator<AnnotationBo> annotationKeyComparator = new Comparator<AnnotationBo>() {
        @Override
        public int compare(AnnotationBo a1, AnnotationBo a2) {
            return a1.getKey() - a2.getKey();
        }
    };

    public static void sortAnnotationListByKey(Span span) {
        List<AnnotationBo> list = span.getAnnotationBoList();
        Collections.sort(list, annotationKeyComparator);
    }

    public static Object getDisplayMethod(Span span) {
        List<AnnotationBo> list = span.getAnnotationBoList();
        int index = Collections.binarySearch(list, AnnotationKey.API, AnnotationBo.AnnotationBoComparator);

        if (index > -1) {
            return list.get(index).getValue();
        }

        if (span.getServiceType() == ServiceType.ARCUS || span.getServiceType() == ServiceType.MEMCACHED) {
            return span.getRpc(); // return OperationImpl
        }

        return null;
    }

    public static Object getDisplayArgument(Span span) {
        List<AnnotationBo> list = span.getAnnotationBoList();
        int index = -1;
        if (span.getServiceType() == ServiceType.ARCUS || span.getServiceType() == ServiceType.MEMCACHED) {
            index = Collections.binarySearch(list, AnnotationKey.ARCUS_COMMAND, AnnotationBo.AnnotationBoComparator);
        }

        if (span.getServiceType() == ServiceType.HTTP_CLIENT) {
            index = Collections.binarySearch(list, AnnotationKey.HTTP_URL, AnnotationBo.AnnotationBoComparator);
        }

        if (span.getServiceType() == ServiceType.TOMCAT) {
            index = Collections.binarySearch(list, AnnotationKey.HTTP_URL, AnnotationBo.AnnotationBoComparator);
        }
        
        if (span.getServiceType() == ServiceType.MYSQL) {
        	index = Collections.binarySearch(list, AnnotationKey.ARGS0, AnnotationBo.AnnotationBoComparator);
        }

        if (index > -1) {
            return list.get(index).getValue();
        }

        return null;
    }
}
