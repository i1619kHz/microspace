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

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * @author i1619kHz
 */
public enum SessionProtocol implements Comparable<SessionProtocol> {
    /**
     * HTTP - over TLS, HTTP/2 preferred.
     */
    HTTPS("https", true, false, 443),
    /**
     * HTTP - cleartext, HTTP/2 preferred.
     */
    HTTP("http", false, false, 80),
    /**
     * HTTP/1 - over TLS.
     */
    H1("h1", true, false, 443),
    /**
     * HTTP/1 - cleartext.
     */
    H1C("h1c", false, false, 80),
    /**
     * HTTP/2 - over TLS.
     */
    H2("h2", true, true, 443),
    /**
     * HTTP/2 - cleartext.
     */
    H2C("h2c", false, true, 80),
    /**
     * <a href="https://www.haproxy.org/download/1.8/doc/proxy-protocol.txt">PROXY protocol</a> - v1 or v2.
     */
    PROXY("proxy", false, false, 0);

    private static final Set<SessionProtocol> HTTP_VALUES = Sets.immutableEnumSet(HTTP, H1C, H2C);

    private static final Set<SessionProtocol> HTTPS_VALUES = Sets.immutableEnumSet(HTTPS, H1, H2);

    private static final Map<String, SessionProtocol> uriTextToProtocols;

    static {
        final ImmutableMap.Builder<String, SessionProtocol> builder = ImmutableMap.builder();
        for (SessionProtocol e : values()) {
            builder.put(e.uriText(), e);
        }
        uriTextToProtocols = builder.build();
    }

    private final String uriText;
    private final boolean useTls;
    private final boolean isMultiplex;
    private final int defaultPort;

    SessionProtocol(String uriText, boolean useTls, boolean isMultiplex, int defaultPort) {
        this.uriText = uriText;
        this.useTls = useTls;
        this.isMultiplex = isMultiplex;
        this.defaultPort = defaultPort;
    }

    /**
     * Returns the {@link SessionProtocol} with the specified {@link #uriText()}.
     *
     * @throws IllegalArgumentException if there's no such {@link SessionProtocol}
     */
    public static SessionProtocol of(String uriText) {
        uriText = Ascii.toLowerCase(requireNonNull(uriText, "uriText"));
        final SessionProtocol value = uriTextToProtocols.get(uriText);
        checkArgument(value != null, "unknown session protocol: ", uriText);
        return value;
    }

    /**
     * Finds the {@link SessionProtocol} with the specified {@link #uriText()}.
     */
    @Nullable
    public static SessionProtocol find(String uriText) {
        uriText = Ascii.toLowerCase(requireNonNull(uriText, "uriText"));
        return uriTextToProtocols.get(uriText);
    }

    /**
     * Returns an immutable {@link Set} that contains {@link #HTTP}, {@link #H1C} and {@link #H2C}.
     * Note that it does not contain HTTPS protocols such as {@link #HTTPS}, {@link #H1} and {@link #H2}.
     *
     * @see #httpsValues()
     */
    public static Set<SessionProtocol> httpValues() {
        return HTTP_VALUES;
    }

    /**
     * Returns an immutable {@link Set} that contains {@link #HTTPS}, {@link #H1} and {@link #H2}.
     * Note that it does not contain HTTP protocols such as {@link #HTTP}, {@link #H1C} and {@link #H2C}.
     *
     * @see #httpValues()
     */
    public static Set<SessionProtocol> httpsValues() {
        return HTTPS_VALUES;
    }

    /**
     * Returns {@code true} if and only if this protocol uses TLS as its transport-level security layer.
     */
    public boolean isTls() {
        return useTls;
    }

    /**
     * Returns the textual representation of this format
     */
    public String uriText() {
        return uriText;
    }

    /**
     * Returns {@code true} if and only if this protocol can multiplex a single transport-layer connection into
     * more than one stream.
     */
    public boolean isMultiplex() {
        return isMultiplex;
    }

    /**
     * Returns the default INET port number of this protocol.
     */
    public int defaultPort() {
        return defaultPort;
    }

    @Override
    public String toString() {
        return uriText;
    }
}
