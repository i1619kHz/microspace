package io.microspace.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static io.microspace.server.SessionProtocol.H1;
import static io.microspace.server.SessionProtocol.H1C;
import static io.microspace.server.SessionProtocol.H2;
import static io.microspace.server.SessionProtocol.H2C;
import static io.microspace.server.SessionProtocol.HTTP;
import static io.microspace.server.SessionProtocol.HTTPS;
import static io.microspace.server.SessionProtocol.PROXY;
import static java.util.Objects.requireNonNull;

/**
 * @author i1619kHz
 */
public final class ServerPort implements Comparable<ServerPort> {
  private final InetSocketAddress localAddress;
  private final Set<SessionProtocol> protocols;
  private final String comparisonStr;

  public ServerPort(InetSocketAddress localAddress, SessionProtocol... protocols) {
    this(localAddress, ImmutableSet.copyOf(requireNonNull(protocols, "protocols")));
  }

  public ServerPort(int port, SessionProtocol... protocols) {
    this(port, ImmutableSet.copyOf(requireNonNull(protocols, "protocols")));
  }

  public ServerPort(int port, Iterable<SessionProtocol> protocols) {
    this(new InetSocketAddress(port), protocols);
  }

  public ServerPort(InetSocketAddress localAddress, Iterable<SessionProtocol> protocols) {
    // Try to resolve the localAddress if not resolved yet.
    if (requireNonNull(localAddress, "localAddress").isUnresolved()) {
      try {
        localAddress = new InetSocketAddress(
            InetAddress.getByName(localAddress.getHostString()),
            localAddress.getPort());
      } catch (UnknownHostException e) {
        throw new IllegalArgumentException("unresolved localAddress: " + localAddress, e);
      }
    }
    requireNonNull(protocols, "protocols");
    this.localAddress = localAddress;
    this.protocols = Sets.immutableEnumSet(protocols);

    checkArgument(!this.protocols.isEmpty(),
        "protocols: %s (must not be empty)", this.protocols);
    checkArgument(this.protocols.contains(HTTP) || this.protocols.contains(HTTPS),
        "protocols: %s (must contain HTTP or HTTPS)", this.protocols);
    checkArgument(this.protocols.stream().allMatch(p -> p == HTTP || p == HTTPS || p == PROXY),
        "protocols: %s (must not contain other than %s, %s or %s)",
        this.protocols, HTTP, HTTPS, PROXY);

    this.comparisonStr = localAddress.getAddress().getHostAddress() + '/' +
        localAddress.getPort() + '/' + protocols;
  }

  /**
   * Returns the local address this {@link ServerPort} listens to.
   */
  public InetSocketAddress localAddress() {
    return localAddress;
  }

  /**
   * Returns the {@link SessionProtocol}s this {@link ServerPort} uses.
   */
  public Set<SessionProtocol> protocols() {
    return protocols;
  }

  /**
   * Returns whether there is a {@link SessionProtocol} which is over TLS.
   */
  public boolean hasTls() {
    return protocols.stream().anyMatch(SessionProtocol::isTls);
  }

  /**
   * Returns whether the {@link SessionProtocol#HTTP}, {@link SessionProtocol#H1C} or
   * {@link SessionProtocol#H2C} is in the list of {@link SessionProtocol}s.
   */
  public boolean hasHttp() {
    return hasProtocol(HTTP) || hasProtocol(H1C) || hasProtocol(H2C);
  }

  /**
   * Returns whether the {@link SessionProtocol#HTTPS}, {@link SessionProtocol#H1} or
   * {@link SessionProtocol#H2} is in the list of {@link SessionProtocol}s.
   */
  public boolean hasHttps() {
    return hasProtocol(HTTPS) || hasProtocol(H1) || hasProtocol(H2);
  }

  /**
   * Returns whether the {@link SessionProtocol#PROXY} is in the list of {@link SessionProtocol}s.
   */
  public boolean hasProxyProtocol() {
    return hasProtocol(PROXY);
  }

  /**
   * Returns whether the specified {@code protocol} is in the list of {@link SessionProtocol}s.
   */
  public boolean hasProtocol(SessionProtocol protocol) {
    return protocols.contains(requireNonNull(protocol, "protocol"));
  }

  @Override
  public int compareTo(@NotNull ServerPort o) {
    return comparisonStr.compareTo(o.comparisonStr);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("localAddress", localAddress)
        .add("protocols", protocols)
        .add("comparisonStr", comparisonStr)
        .toString();
  }
}
