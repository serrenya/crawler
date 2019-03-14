package repository.query;

public class QueryBuilder {
    public static String gnerateSelectQuery(String target) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT ");
        buffer.append(target);
        buffer.append(" FROM wikiparser");
        return buffer.toString();
    }

    public static String createInsertQueryWithUpdate() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("INSERT INTO wikipage");
        buffer.append(" (urlKey, siteIdentifier, url,");
        buffer.append(" page, statusCode, crawlDate, createDate)");
        buffer.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
        buffer.append(" ON DUPLICATE KEY UPDATE");
        buffer.append(" page= VALUES(page), crawlDate=VALUES(createDate)");
        return buffer.toString();
    }

    public static String createInsertQuery() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("INSERT INTO crawltime");
        buffer.append(" (startTime, endTime, siteIdentifier, url, performanceTime, queue)");
        buffer.append(" VALUES (?, ?, ?, ?, ?, ?)");
        return buffer.toString();
    }
}
