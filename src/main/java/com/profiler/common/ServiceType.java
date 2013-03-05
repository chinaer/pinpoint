package com.profiler.common;

import java.util.HashMap;
import java.util.Map;

public enum ServiceType {

    UNKNOWN((short) 0, "UNKNOWN", false, true, Histogram.NORMAL),
    UNKNOWN_CLOUD((short) 1, "UNKNOWN_CLOUD", false, true, Histogram.NORMAL),
    CLIENT((short) 2, "CLIENT", false, false, Histogram.NORMAL),

    // WAS류 1000번 부터 시작
    TOMCAT((short) 1010, "TOMCAT", false, true, Histogram.NORMAL),
    BLOC((short) 1020, "BLOC", false, true, Histogram.NORMAL),

    // DB 2000
    UNKNOWN_DB((short) 2050, "UNKNOWN_DB", true, false, Histogram.NORMAL),
    UNKNOWN_DB_EXECUTE_QUERY((short) 2051, "UNKNOWN_DB", true, false, Histogram.NORMAL),

    MYSQL((short) 2100, "MYSQL", true, false, Histogram.NORMAL),
    MYSQL_EXECUTE_QUERY((short) 2101, "MYSQL", true, true, Histogram.NORMAL),

    MSSQL((short) 2200, "MSSQL", true, false, Histogram.NORMAL),
    MSSQL_EXECUTE_QUERY((short) 2201, "MSSQL", true, true, Histogram.NORMAL),

    ORACLE((short) 2300, "ORACLE", true, false, Histogram.NORMAL),
    ORACLE_EXECUTE_QUERY((short) 2301, "ORACLE", true, true, Histogram.NORMAL),

    CUBRID((short) 2400, "CUBRID", true, false, Histogram.NORMAL),
    CUBRID_EXECUTE_QUERY((short) 2401, "CUBRID", true, true, Histogram.NORMAL),

    // TODO internal method를 여기에 넣기 애매하긴 하나.. 일단 그대로 둠.
    INTERNAL_METHOD((short) 5000, "INTERNAL_METHOD", false, false, Histogram.NORMAL),

    SPRING((short) 5050, "SPRING", false, false, Histogram.NORMAL),
    SPRING_MVC((short) 5051, "SPRING", false, false, Histogram.NORMAL),

    // memory cache  8000
    MEMCACHED((short) 8050, "MEMCACHED", true, true, Histogram.FAST),
    ARCUS((short) 8100, "ARCUS", true, true, Histogram.FAST),

    // connector류
    HTTP_CLIENT((short) 9050, "HTTP_CLIENT", false, true, Histogram.NORMAL);

    private final short code;
    private final String desc;
    private final boolean terminal;
    private final boolean recordStatistics;
    private final Histogram histogram;

    ServiceType(short code, String desc, boolean terminal, boolean recordStatistics, Histogram histogram) {
        this.code = code;
        this.desc = desc;
        this.terminal = terminal;
        this.histogram = histogram;
        this.recordStatistics = recordStatistics;
    }

    public static ServiceType parse(String desc) {
        ServiceType[] values = ServiceType.values();
        for (ServiceType type : values) {
            if (type.desc.equals(desc)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public boolean isInternalMethod() {
    	return this == INTERNAL_METHOD;
    }

    public boolean isRpcClient() {
        return code >= 9000 && code < 10000;
    }

    public boolean isIndexable() {
        return !terminal && !isRpcClient() && code > 1000;
    }

    public boolean isRecordStatistics() {
        return recordStatistics;
    }

    public boolean isUnknown() {
        return this == ServiceType.UNKNOWN || this == ServiceType.UNKNOWN_CLOUD;
    }

    public short getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public Histogram getHistogram() {
        return histogram;
    }


    @Override
    public String toString() {
        return desc;
    }

    public static ServiceType findServiceType(short code) {
        ServiceType serviceType = CODE_LOOKUP_TABLE.get(code);
        if (serviceType == null) {
            return UNKNOWN;
        }
        return serviceType;
    }


    private static final Map<Short, ServiceType> CODE_LOOKUP_TABLE = new HashMap<Short, ServiceType>();

    static {
        initializeLookupTable();
    }

    public static void initializeLookupTable() {
        ServiceType[] values = ServiceType.values();
        for (ServiceType serviceType : values) {
            ServiceType check = CODE_LOOKUP_TABLE.put(serviceType.code, serviceType);
            if (check != null) {
                throw new IllegalStateException("duplicated code found. code:" + serviceType.code);
            }
        }
    }
}
