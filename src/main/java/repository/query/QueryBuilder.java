package repository.query;

public class QueryBuilder {
    public static String createSelectQueryWithLikeStatement (String target){
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT ");
        buffer.append(target);
        buffer.append(" FROM wikiparser");
        buffer.append(" where url like ?");
       return buffer.toString();
    }

    public static String createInsertQueryWithUpdate(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("INSERT INTO wikipagedata");
        buffer.append(" (urlKey, siteIdentifier, url,");
        buffer.append(" page, statusCode, crawlDate, createDate)");
        buffer.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
        buffer.append(" ON DUPLICATE KEY UPDATE");
        buffer.append(" page= VALUES(page), crawlDate=VALUES(createDate)");
        return buffer.toString();
    }
}
