import java.io.*;

/**
 * Filtrul are un port de intrare si un port de iesire
 * Filtrul citeste date de la portul de intrare si le transfera la portul de iesire daca este 
 * indeplinita o anumita conditie.
 * In acest caz datele sunt transferate daca studentul a absolvit un curs dat.
 */
public class CourseFilter extends Filter {

    /**
     * Portul de intrare.
     **/
    protected BufferedReader pInput;

    /**
     * Portul de iesire.
     **/
    protected BufferedWriter pOutput;
 
    /**
     * Numarul cursului
     **/
    protected int iCourse;

    /**
     * Construirea unui filtru pentru curs cu un nume dat. 
     * Porturile de intrare si de iesire sunt impachetate intr-un flux de caractere buffer-at
     * Aceasta permite utilizarea de diferite mecanisme de I/E dar si utilizarea de conducte (pipes).
     * De exemplu, un fisier poate fi conectat direct la un filtru.
     *
     * @param sName   sirul ce reprezinta numele filtrului
     * @param pInput  portul de intrare al acestui filtru
     * @param pOutput portul de iesire al acestui filtru
     * @param iCourse numarul cursului
     */
    public CourseFilter(String sName, 
                        BufferedReader pInput, BufferedWriter pOutput, int iCourse) {
        // Executarea constructorului din clasa parinte.
        super(sName);

        // Initializarea porturilor de intrare si iesire.
        this.pInput  = pInput;
        this.pOutput = pOutput;

        // Setarea numarului cursului.
        this.iCourse = iCourse;
    }

    /**
     * Indica disponibilitatea datelor pe portul de intrare.
     *
     * @return <code>true</code> daca si numai daca acest filtru poate citi date de la portul de intrare.
     * @throws IOException
     */
    protected boolean ready() throws IOException {
        return this.pInput.ready();
    }

    /**
     * Citeste datele disponibile de la portul de intrare si le scrie la portul de iesire 
     * daca studentul reprezentat de datele citite a absolvit cursul 
     * dat ca parametru de intrare la crearea acestui filtru.
     *
     * @throws IOException
     */
    protected void work() throws IOException {
        // Citeste o inregistrare corespunzatoare unui student, de la portul de intrare.
        Student objStudent = new Student(this.pInput.readLine());

        // Scrie inregistrarea corespunzatoare studentului la portul de iesire daca este indeplinita conditia.
        if (objStudent.hasCompleted(this.iCourse)) {
            this.pOutput.write(objStudent.toString());
            this.pOutput.newLine();
            this.pOutput.flush();
        }
    }
}
