/*
 * MIT License
 *
 * Copyright (c) 2021 1619kHz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.microspace.sqlstream;

import static io.microspace.sqlstream.SQLConstants.and;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author i1619kHz
 */
abstract class ConditionMergeBuilder<T, R extends Serializable, RType>
        extends AbstractConditionSQLBuilder<R, RType> implements JDBCResult<T> {

    protected final List<String> whereConditions = new ArrayList<>();

    protected void addEqCondition(String fieldName, Object value, String operator) {
        Objects.requireNonNull(fieldName, "fieldName cannot be set to null");
        Objects.requireNonNull(operator, "operator cannot be set to null");

        String matchSymbol = confirmMatchSymbol(value);
        String condition = String.format(fieldName + operator + matchSymbol, value);
        this.whereConditions.add(condition);
    }

    protected String confirmMatchSymbol(Object value) {
        String matchSymbol = "'%s'";
        if (value instanceof Number) {
            matchSymbol = "%d";
        }
        return matchSymbol;
    }

    protected void addNullCondition(String fieldName, String operator) {
        Objects.requireNonNull(fieldName, "fieldName cannot be set to null");
        Objects.requireNonNull(operator, "operator cannot be set to null");

        String condition = fieldName + operator;
        this.whereConditions.add(condition);
    }

    protected <V> void addBetweenCondition(String fieldName, V value1, V value2, String betweenOperator) {
        Objects.requireNonNull(fieldName, "fieldName cannot be set to null");
        Objects.requireNonNull(value1, "value1 cannot be set to null");
        Objects.requireNonNull(value2, "value2 cannot be set to null");

        String matchSymbol1 = confirmMatchSymbol(value1);
        String matchSymbol2 = confirmMatchSymbol(value2);

        this.whereConditions.add(String.format(fieldName +
                                               betweenOperator + matchSymbol1 + and + matchSymbol2, value1,
                                               value2));
    }
}
