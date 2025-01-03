package uk.gov.legislation.util;


import uk.gov.legislation.transform.simple.Level;

import static org.springframework.util.StringUtils.capitalize;

public class Labels {

    public static String make(Level level) {
        if ("Part".equals(level.name))
            return level.number;
        if ("Chapter".equals(level.name))
            return level.number;
        if ("Pblock".equals(level.name))
            return level.title;
        if ("PsubBlock".equals(level.name))
            return level.title;
        if ("P1".equals(level.name))
            return capitalize(level.id.replace('-', ' '));
        if ("Schedule".equals(level.name))
            return level.number;
        return level.name;
    }

}
