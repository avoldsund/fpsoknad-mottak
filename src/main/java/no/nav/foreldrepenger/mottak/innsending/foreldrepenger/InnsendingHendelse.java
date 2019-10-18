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
    private final String saksNr;
    private final LeveranseStatus leveranseStatus;
    private final SøknadType hendelseType;
    private final List<String> vedlegg;
    private final LocalDate førsteBehandlingsdato;

    public InnsendingHendelse(String aktørId, String dialogId, Kvittering kvittering, SøknadType hendelseType,
            List<String> vedlegg) {
        this.aktørId = aktørId;
        this.journalId = kvittering.getJournalId();
        this.referanseId = kvittering.getReferanseId();
        this.dialogId = dialogId;
        this.saksNr = kvittering.getSaksNr();
        this.leveranseStatus = kvittering.getLeveranseStatus();
        this.hendelseType = hendelseType;
        this.vedlegg = vedlegg;
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

    public String getSaksNr() {
        return saksNr;
    }

    public LeveranseStatus getLeveranseStatus() {
        return leveranseStatus;
    }

    public SøknadType getHendelseType() {
        return hendelseType;
    }

    public List<String> getVedlegg() {
        return vedlegg;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[aktørId=" + aktørId + ", journalId=" + journalId
                + ", referanseId=" + referanseId + ", saksNr=" + saksNr + ", leveranseStatus=" + leveranseStatus
                + ", hendelseType=" + hendelseType + ", vedlegg=" + vedlegg + ", førsteBehandlingsdato="
                + førsteBehandlingsdato
                + "]";
    }

}