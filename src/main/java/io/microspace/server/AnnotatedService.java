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
package io.microspace.server;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.MoreObjects;

import io.microspace.server.annotation.ExceptionHandlerFunction;
import io.microspace.server.annotation.RequestConverterFunction;
import io.microspace.server.annotation.ResponseConverterFunction;

/**
 * @author i1619kHz
 */
final class AnnotatedService implements HttpService {
    private final Object target;
    private final Method method;
    private final boolean needToUseBlockingTaskExecutor;
    private final Map<String, Set<String>> addedHeaders;
    private final List<RequestConverterFunction> requestConverterFunctions;
    private final List<ResponseConverterFunction> responseConverterFunctions;
    private final List<ExceptionHandlerFunction> exceptionHandlerFunctions;

    AnnotatedService(Object target, Method method,
                     boolean needToUseBlockingTaskExecutor,
                     Map<String, Set<String>> addedHeaders,
                     List<RequestConverterFunction> requestConverterFunctions,
                     List<ResponseConverterFunction> responseConverterFunctions,
                     List<ExceptionHandlerFunction> exceptionHandlerFunctions) {
        requireNonNull(target, "target");
        requireNonNull(method, "method");
        requireNonNull(addedHeaders, "addedHeaders");
        requireNonNull(requestConverterFunctions, "requestConverterFunctions");
        requireNonNull(responseConverterFunctions, "responseConverterFunctions");
        requireNonNull(exceptionHandlerFunctions, "exceptionHandlerFunctions");
        this.target = target;
        this.method = method;
        this.addedHeaders = addedHeaders;
        this.needToUseBlockingTaskExecutor = needToUseBlockingTaskExecutor;
        this.requestConverterFunctions = requestConverterFunctions;
        this.responseConverterFunctions = responseConverterFunctions;
        this.exceptionHandlerFunctions = exceptionHandlerFunctions;
    }

    @Override
    public HttpResponse serve(Request request) {
        try {
            Object invoke = method.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return HttpResponse.of("11");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("target", target)
                          .add("method", method)
                          .toString();
    }
}
