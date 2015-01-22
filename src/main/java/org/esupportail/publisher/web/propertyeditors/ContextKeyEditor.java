package org.esupportail.publisher.web.propertyeditors;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 04/11/15.
 */
@Slf4j
public class ContextKeyEditor extends PropertyEditorSupport {

    private static final Pattern pattern = Pattern.compile(".*\"keyId\":(?<id>\\p{Digit}+),\\s*\"keyType\":\"(?<type>\\p{Upper}+)\".*");

    private static final String patternList = "\\},\\s*\\{";

    public ContextKeyEditor() {
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        log.debug("Editor of contextKey with text in entry : {}", text);
        final String[] texts = text.split(patternList);

        if (texts.length > 0) {
            List<ContextKey> ctxs = new ArrayList<>();
            log.debug("group count found {}", Lists.newArrayList(texts));
            for (int ind = 0;ind < texts.length; ind++) {
                ContextKey ctx = extract(texts[ind]);
                if (ctx != null) {
                    ctxs.add(ctx);
                }
            }

            setValue(ctxs);
        } else {
            setValue(null);
        }
    }


    private ContextKey extract(String text) throws IllegalArgumentException {
        Matcher matcher = pattern.matcher(text);
        ContextKey ctxKey = null;
        if (matcher.find()) {
            log.debug("Pattern matching object id={}, type={}",matcher.group("id"), matcher.group("type"));
            ctxKey = new ContextKey(Long.parseLong(matcher.group("id")), ContextType.valueOf(matcher.group("type")));
        }
        return ctxKey;
    }
}
