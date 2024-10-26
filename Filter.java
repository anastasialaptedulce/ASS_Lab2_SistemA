import java.io.*;

/**
 * Clasa defineste un schelet de filtru care citeste date de la unul sau mai multe porturi de intrare
 * si scrie date la unul sau mai multe porturi de iesire.
 * Porturile sunt legate la rolurile conductelor ce conecteaza filtrele.
 * Aceasta informatie de legare nu este o preocupare directa a filtrelor individuale.
 * Un filtru nu este constient de prezenta altor filtre sau conducte.
 *  
 * Clasa defineste ciclul de viata al filtrului: cand filtrul este activ si cand se termina. 
 * (Obs. Un obiect <code>Filter</code> este, de asemenea, un fir de executie.) 
 * Detaliile specifice despre porturi si operatii trebuie specificate concret in
 * definitiile subclaselor ce mostenesc aceasta clasa. Acesta este motivul pentru care clasa este abstracta, 
 * deci nu poate fi instantiata.

 */
abstract public class Filter extends Thread {

    /**
     * Precizeaza daca filtrul este in curs de prelucrare de date.
     */
    protected boolean bBusy;

    /**
     * Construieste un schelet de filtru cu numele dat. Un filtru nou este initial inactiv.
     *
     * @param sName sirul ce reprezinta numele filtrului
     */
    public Filter(String sName) {
        // Asignarea numelui filtrului la numele firului de executie.
        super.setName(sName);

        // Initializarea starii filtrului (initial "inactiv").
        this.bBusy = false;
    }

    /**
     * Precizeaza daca filtrul este in curs de prelucrare de date.
     *
     * @return <code>true</code> daca si numai daca filtrul este in curs de prelucrare de date.
     */
    public boolean busy() {
        return this.bBusy;
    }

    /**
     * Activitatea firului de executie al acestui filtru. Filtrul citeste date cand sunt disponibile la portul de intrare
     * si scrie date la portul de iesire. In aceasta perioada indicatorul "busy" este "on".
     * Intreruperea unui filtru determina terminarea sa.
     */
    public void run() {
        try {
            while (!super.isInterrupted()) {
                if (this.ready()) {
                    this.bBusy = true;
                    this.work();
                    this.bBusy = false;
                } else {
                    // Preda controlul pentru planificarea altui fir de executie.
                    super.yield();
                }
            }
        } catch (Exception e) {
            // Afiseaza informatiile de depanare si se termina.
            System.out.println("Failure running " + super.getName() + " filter.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Precizeaza daca datele sunt disponibile pe portul de intrare.
     *
     * @return <code>true</code> daca si numai daca acest filtru poate sa citeasca date de la porturile de intrare.
     * @throws IOException
     */
    abstract protected boolean ready() throws IOException;

    /**
     * Citeste datele disponibile de la porturile de intrare 
     * si scie date noi la porurile de iesire.
     *
     * @throws IOException
     */
    abstract protected void work() throws IOException;
}
