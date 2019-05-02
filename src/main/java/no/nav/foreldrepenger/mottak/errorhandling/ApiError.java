package no.nav.foreldrepenger.mottak.errorhandling;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiError {
    private final HttpStatus status;
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    @JsonFormat(with = WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
    private final List<String> messages;
    private final String uuid;

    ApiError(HttpStatus status, Throwable t) {
        this(status, t, null);
    }

    ApiError(HttpStatus status, Throwable t, List<Object> objects) {
        this(status, t, null, objects);
    }

    ApiError(HttpStatus status, Throwable t, String destination, List<Object> objects) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.messages = messages(t, objects);
        this.uuid = callId();
    }

    public String getUuid() {
        return uuid;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getMessages() {
        return messages;
    }

    private static List<String> messages(Throwable t, List<Object> objects) {
        List<Object> messages = Lists.newArrayList(objects);
        messages.add(getMostSpecificCause(t).getMessage());
        return messages.stream()
                .filter(s -> s != null)
                .map(Object::toString)
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[status=" + status + ", timestamp=" + timestamp + ", messages=" + messages
                + ", uuid=" + uuid + "]";
    }
}