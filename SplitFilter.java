import java.io.*;


/**
 * Un filtru de disociere are un port de intrare si doua porturi de iesire
 * El citeste date de la portul de intrare si le transfera la unul din porturile de iesire
 * conform unei anumite conditii.
 * Aici datele sunt trimise la primul port de iesire cand specializarea studentului 
 * este conforma cu o specializare data.
 */
public class SplitFilter extends Filter {

    /**
     * Portul de intrare.
     **/
    protected BufferedReader pInput;

    /**
     * Primul port de iesire.
     **/
    protected BufferedWriter pOutput1;

    /**
     * Al doilea port de iesire.
     **/
    protected BufferedWriter pOutput2;

    /**
     * Specializarea dupa care se face disocierea.
     **/
    protected String sProgram;

    /**
     * Construirea unui filtru pentru curs cu un nume dat. 
     * Porturile de intrare si de iesire sunt impachetate intr-un flux de caractere buffer-at
     * Aceasta permite utilizarea de diferite mecanisme de I/E dar si utilizarea de conducte (pipes).
     * De exemplu, un fisier poate fi conectat direct la un filtru.
     *
     * @param sName    sirul ce reprezinta numele filtrului
     * @param pInput   portul de intrare al acestui filtru
     * @param pOutput1 primul (accepted) port de iesire al acestui filtru
     * @param pOutput2 al doilea (rejected) port de iesire al acestui filtru
     * @param iCourse  numele specializarii, utilizat pentru disociere
     */
    public SplitFilter(String sName, BufferedReader pInput, 
                       BufferedWriter pOutput1, BufferedWriter pOutput2, String sProgram) {
        // Executarea constructorului clasei parinte .
        super(sName);

        // Initializarea porturilor de intrare si iesire.
        this.pInput   = pInput;
        this.pOutput1 = pOutput1;
        this.pOutput2 = pOutput2;

        // Setarea numelui specializarii, utilzat pentru disociere.
        this.sProgram = sProgram;
    }

    /**
     * Precizeaza daca datele sunt disponibile pe portul de intrare.
     *
     * @return <code>true</code> daca si numai daca acest filtru poate citi date de la portul de intrare.
     * @throws IOException
     */
    protected boolean ready() throws IOException {
        return this.pInput.ready();
    }

    /**
     * Citeste datele disponibile de la portul de intrare si le transfera la unul din porturile de iesire
     * daca studentul reprezentat de datele citire (o inregistrare a unui student) este 
     * inscris la specializarea al carui nume a fost dat ca parametru de intrare la crearea acestui filtru,
     * atunci datele citite sunt scrise la primul (accepted) port de iesire.
     * Altfel, datele citire sunt scrise la al doilea (rejected) port de iesire.
     *
     * @throws IOException
     */
    protected void work() throws IOException {
        // Citeste o inregistrare a unui student, de la portul de intrare.
        Student objStudent = new Student(this.pInput.readLine());

        // Selecteaza portul de iesire pe care se va scrie.
        BufferedWriter pOutput = objStudent.isAffiliatedWith(this.sProgram) ? this.pOutput1 
                                                                            : this.pOutput2;

        // Scrie inregistrarea corespunzatoare studentului, la portul de iesire selectat.
        pOutput.write(objStudent.toString());
        pOutput.newLine();
        pOutput.flush();
    }
}
