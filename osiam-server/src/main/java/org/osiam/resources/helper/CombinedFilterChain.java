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

package org.osiam.resources.helper;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CombinedFilterChain implements FilterChain {
    // will be used to identify combined filter chain, the expression can be expressed in bracelets or without.
    //e.q.: title pr or userType eq Intern, title pr or (userType eq Intern and userName pr)
    // The first block is term1, the second is combinedWith and the third is term2.
    // Bracelets will be cut off for further processing.
    static final Pattern COMBINED_FILTER_CHAIN =
            Pattern.compile("(?i)[\\(]{0,1}([\\S ]+?)[\\)]{0,1} (and|or) [\\(]{0,1}([\\S ]+?)[\\)]{0,1}");
    private final FilterChain term1;
    private final Combiner combinedWith;
    private final FilterChain term2;
    private FilterParser filterParser = new FilterParser();

    public CombinedFilterChain(String chain) {
        Matcher matcher = COMBINED_FILTER_CHAIN.matcher(chain);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(chain + " is not a CombinedFilterChain.");
        }
        this.term1 = filterParser.parse(matcher.group(1));
        this.combinedWith = Combiner.valueOf(matcher.group(2).toUpperCase());
        this.term2 = filterParser.parse(matcher.group(3));
    }

    @Override
    public Criterion buildCriterion() {
        if (combinedWith == Combiner.AND) {
            return Restrictions.and(term1.buildCriterion(), term2.buildCriterion());
        }
        return Restrictions.or(term1.buildCriterion(), term2.buildCriterion());

    }


    public enum Combiner {
        AND,
        OR;
    }
}
