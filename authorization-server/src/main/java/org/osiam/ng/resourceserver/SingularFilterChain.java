/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osiam.ng.resourceserver;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingularFilterChain implements FilterChain {
    static final Pattern SINGULAR_CHAIN_PATTERN = Pattern.compile("(\\S+) ("+Constraints.createOrConstraints()+")[ ]??(\\S*)");
    private final String key;
    private final Constraints constraint;
    private final String value;

    public SingularFilterChain(String chain) {
        Matcher matcher = SINGULAR_CHAIN_PATTERN.matcher(chain);
        if (!matcher.matches()) { throw new IllegalArgumentException(chain + " is not a SingularFilterChain."); }
        this.key = matcher.group(1);
        this.constraint = Constraints.fromString.get(matcher.group(2));
        this.value = matcher.group(3);

    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Constraints getConstraint() {
        return constraint;
    }

    @Override
    public String getValue() {
        return value;
    }

    public enum Constraints {
        EQUALS("eq"),
        CONTAINS("co"),
        STARTS_WITH("sw"),
        PRESENT("pr"),
        GREATER_THAN("gt"),
        GREATER_EQUALS("ge"),
        LESS_THAN("lt"),
        LESS_EQUALS("le");
        static Map<String, Constraints> fromString = new ConcurrentHashMap<>();

        static {
            for (Constraints k : values()) { fromString.put(k.constraint, k); }
        }

        private final String constraint;


        Constraints(String constraint) {
            this.constraint = constraint;
        }

        static String createOrConstraints() {
            StringBuilder sb = new StringBuilder();
            for (Constraints k : values()) {
                if (sb.length() != 0) { sb.append("|");}
                sb.append(k.constraint);
            }
            return sb.toString();

        }


    }
}