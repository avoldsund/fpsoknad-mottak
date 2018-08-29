package no.nav.foreldrepenger.lookup.rest.fpinfo;

import java.net.URI;

public class RemoteUnavailableException extends RuntimeException {

    public RemoteUnavailableException(Throwable t) {
        this((String) null, t);
    }

    public RemoteUnavailableException(String msg, Throwable t) {
        super(msg, t);
    }

    public RemoteUnavailableException(URI uri, Throwable e) {
        this(uri.toString(), e);
    }

}
