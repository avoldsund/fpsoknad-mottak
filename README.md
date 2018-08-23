fpsoknad-mottak
================

Mottar søknader om foreldrepenger og engangsstønad fra frontend og sender dem videre inn i NAV for behandling.

# Komme i gang

### For å kjøre lokalt:

Start no.nav.foreldrepenger.mottak.MottakApplicationLocal

Default konfigurasjon er lagt i application.yaml.

### For å kjøre i et internt testmiljø med registre tilgjengelig: 
 
Få tak i en Java truststore med gyldige sertifikater for aktuelt miljø.

`java -jar fpsoknad-mottak-<version>.jar -Djavax.net.ssl.trustStore=/path/til/truststore -Djavax.net.ssl.trustStorePassword=........`

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes til:

* nav.team.bris@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #bris.