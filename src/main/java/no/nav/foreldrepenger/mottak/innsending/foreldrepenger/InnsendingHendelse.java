package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.LeveranseStatus;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;

public class InnsendingHendelse {

    private final String aktørId;
    private final String journalId;
    private final String referanseId;
    private final String dialogId;
    private final String saksnummer;
    private final LeveranseStatus leveranseStatus;
    private final SøknadType hendelse;
    private final List<String> opplastedeVedlegg;
    private final List<String> ikkeOpplastedeVedlegg;

    private final LocalDate førsteBehandlingsdato;

    public InnsendingHendelse(String aktørId, String dialogId, Kvittering kvittering, Konvolutt konvolutt) {
        this.aktørId = aktørId;
        this.journalId = kvittering.getJournalId();
        this.referanseId = kvittering.getReferanseId();
        this.dialogId = dialogId;
        this.saksnummer = kvittering.getSaksNr();
        this.leveranseStatus = kvittering.getLeveranseStatus();
        this.hendelse = konvolutt.getType();
        this.opplastedeVedlegg = konvolutt.getOpplastedeVedlegg();
        this.ikkeOpplastedeVedlegg = konvolutt.getIkkeOpplastedeVedlegg();
        this.førsteBehandlingsdato = kvittering.getFørsteInntektsmeldingDag();
    }

    public String getDialogId() {
        return dialogId;
    }

    public LocalDate getFørsteBehandlingsdato() {
        return førsteBehandlingsdato;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getJournalId() {
        return journalId;
    }

    public String getReferanseId() {
        return referanseId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public LeveranseStatus getLeveranseStatus() {
        return leveranseStatus;
    }

    public SøknadType getHendelse() {
        return hendelse;
    }

    public List<String> getOpplastedeVedlegg() {
        return opplastedeVedlegg;
    }

    public List<String> getIkkeOpplastedeVedlegg() {
        return ikkeOpplastedeVedlegg;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[aktørId=" + aktørId + ", journalId=" + journalId + ", referanseId="
                + referanseId + ", dialogId=" + dialogId + ", saksnummer=" + saksnummer + ", leveranseStatus="
                + leveranseStatus
                + ", hendelseType=" + hendelse + ", opplastedeVedlegg=" + opplastedeVedlegg
                + ", ikkeOpplastedeVedlegg=" + ikkeOpplastedeVedlegg + ", førsteBehandlingsdato="
                + førsteBehandlingsdato + "]";
    }

}
