package no.nav.foreldrepenger.lookup.rest.fpinfo;

import lombok.Data;

@Data
public class Behandling {

    private final String status;
    private final String type;
    private final String tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
}
