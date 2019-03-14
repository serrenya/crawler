package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.WikiPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonParser {
    private static Logger logger = LoggerFactory.getLogger(JsonParser.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public static String convertJson(WikiPage page){
        String pageData = null; //TODO null X!!!
        try {
            pageData =  mapper.writeValueAsString(page);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return pageData;
    }

    public static <T> T convertObject(String source, Class<T> destination){
        try {
            return mapper.readValue(source,destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
