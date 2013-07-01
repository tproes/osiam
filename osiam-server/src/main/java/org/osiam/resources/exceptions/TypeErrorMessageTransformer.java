package org.osiam.resources.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class was written to transform enum error messages from:
 * <p/>
 * No enum constant org.osiam.storage.entities.(\w+)Entity.[\w]+.(\w+)
 * <p/>
 * to
 * <p/>
 * \1 is not a valid \2 type
 */
public class TypeErrorMessageTransformer implements ErrorMessageTransformer {
    private static Pattern pattern =
            Pattern.compile("No enum constant (org\\.osiam\\.storage\\.entities\\.(\\w+)Entity)\\.(\\w+).(\\w+)");

    @Override
    public String transform(String message) {
        if (message == null) { return null; }
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            String test = loadEnumConstAsStringByClassName(matcher.group(1) + "$" + matcher.group(3));
            return matcher.group(4) + " is not a valid " + matcher.group(2) + " type only " + test.toString() +
                    " are allowed.";
        }
        return message;
    }

    /**
     * Loads an Enum and return its values combined as String.
     *
     * @param enumName, the full qualified name of the enum
     * @return the string values of the enum
     */
    private String loadEnumConstAsStringByClassName(String enumName) {
        try {
            Class<? extends Enum> clazz = (Class<? extends Enum>) Class.forName(enumName);
            Enum[] enumConstants = clazz.getEnumConstants();
            return combineEnumConstantsToString(enumConstants).substring(2);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Iterates through a given array of enums and combines each element to a string:
     * , <const.toString>
     *
     * @param enumConstants the array of enums to combine
     * @return combined string list of enums separated by comma
     */
    private String combineEnumConstantsToString(Enum[] enumConstants) {
        String result = "";
        for (Enum e : enumConstants) {
            result += ", " + e.toString();
        }
        return result;
    }
}
