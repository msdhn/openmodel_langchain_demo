package eu.msdhn.openmodel_langchain.demo.util;

import org.springframework.stereotype.Component;

@Component
public class JsonUtils {

    public String toJson(Object value) {
        return String.valueOf(value);
    }
}
