package no.nav.foreldrepenger.mottak.util;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

public final class URIUtil {
    private URIUtil() {
    }

    public static URI uri(URI base, String path) {
        return uri(base, path, null);
    }

    public static URI uri(URI base, String path, HttpHeaders queryParams) {
        return builder(base, path, queryParams)
                .build()
                .toUri();
    }

    public static UriComponentsBuilder builder(URI base, String path, HttpHeaders queryParams) {
        return UriComponentsBuilder
                .fromUri(base)
                .pathSegment(path)
                .queryParams(queryParams);
    }

    public static HttpHeaders queryParams(String key, String value) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, value);
        return queryParams;
    }

    public static HttpHeaders queryParams(String key, String value, String key1, String value1) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, value);
        queryParams.add(key1, value1);
        return queryParams;
    }
}
