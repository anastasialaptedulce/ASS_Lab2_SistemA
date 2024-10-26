import java.io.*;
import java.util.*;

public class SystemMain {

    /**
     * Creaza componentele si porneste sistemul.
     * Sunt asteptati trei parametrii de intrare:
     * primul: numele fisierului de intrare ce contine inregistrarile corespunzatoare studentilor candidati,
     * al doilea: numele fisierului de iesire ce va contine inregistrarile studentilor acceptati,
     * al treilea: numele fisierului de iesire ce va contine inregistrarile studentilor respinsi.
     *
     * @param args array cu parametrii de intrare
     */
    public static void main(String[] args) {
        // Verificarea numarului parametrilor de intrare.
        if (args.length != 3) {  // Modificare: trebuie sa avem 3 parametri.
            System.out.println("Numar incorect de parametri");
            System.out.println("Utilizare corecta: java SystemMain <fisier_de_intrare> <fisier_de_iesire_acceptati> <fisier_de_iesire_respinsi>");
            System.exit(1);
        }

        // Verificarea existentei fisierului de intrare.
        if (!new File(args[0]).exists()) {
            System.out.println("Could not find " + args[0]);
            System.exit(1);
        }

        // Verificarea existentei directorului parinte al fisierului de iesire pentru studentii acceptati.
        File parentFileAcceptati = new File(args[1]).getAbsoluteFile().getParentFile();
        if (!parentFileAcceptati.exists() && !parentFileAcceptati.mkdir()) {
            System.out.println("Nu s-a putut crea directorul parinte " + args[1]);
            System.exit(1);
        }

        // Verificarea existentei directorului parinte al fisierului de iesire pentru studentii respinsi.
        File parentFileRespinsi = new File(args[2]).getAbsoluteFile().getParentFile();
        if (!parentFileRespinsi.exists() && !parentFileRespinsi.mkdir()) {
            System.out.println("Nu s-a putut crea directorul parinte " + args[2]);
            System.exit(1);
        }

        try {
            // _____________________________________________________________________
            // Crearea si legarea componentelor si conectorilor
            // _____________________________________________________________________

            // Crearea conductelor (de fapt, a rolurilor).
            System.out.println("Controller: Creare conectori (roluri)...");

            PipedWriter objTemp;
            BufferedWriter roleISSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleISSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonISSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonISSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleISAcceptedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleISAcceptedSync = new BufferedReader(new PipedReader(objTemp));

            BufferedWriter roleNonISAcceptedSource = new BufferedWriter(objTemp = new PipedWriter());
            BufferedReader roleNonISAcceptedSync = new BufferedReader(new PipedReader(objTemp));

            BufferedReader roleInputFileSync = new BufferedReader(new FileReader(args[0]));
            BufferedWriter roleOutputFileSource = new BufferedWriter(new FileWriter(args[1]));

            // Crearea fisierului pentru intrările respinse folosind al treilea parametru.
            BufferedWriter rejectedFileSource = new BufferedWriter(new FileWriter(args[2]));

            // Crearea filtrelor
            System.out.println("Controller: Creare componente ...");
            SplitFilter filterSplitIS = new SplitFilter("IS", roleInputFileSync, roleISSource, roleNonISSource, "IS", rejectedFileSource);
            CourseFilter filterScreen17651 = new CourseFilter("17651", roleISSync, roleISAcceptedSource, 17651);
            CourseFilter filterScreen21701 = new CourseFilter("21701", roleNonISSync, roleNonISAcceptedSource, 21701);
            MergeFilter filterMergeAccepted = new MergeFilter("Accepted", roleISAcceptedSync, roleNonISAcceptedSync, roleOutputFileSource);

            // _____________________________________________________________________
            // Executarea sistemului
            // _____________________________________________________________________

            // Start all filters.
            System.out.println("Controller: Pornire filtre ...");
            filterSplitIS.start();
            filterScreen17651.start();
            filterScreen21701.start();
            filterMergeAccepted.start();

            // Asteapta pana la terminarea datelor de pe lanturile conductelor si filtrelor.
            while (roleInputFileSync.ready() || filterSplitIS.busy()
                    || roleISSync.ready() || filterScreen17651.busy() || roleISAcceptedSync.ready()
                    || roleNonISSync.ready() || filterScreen21701.busy() || roleNonISAcceptedSync.ready()
                    || filterMergeAccepted.busy()) {
                System.out.print('.');
                Thread.yield();
            }

            // Colectarea si sortarea studentilor acceptati.
            System.out.println("\nController: Sortarea studentilor acceptati ...");
            List<Student> acceptedStudents = new ArrayList<>();
            while (roleISAcceptedSync.ready()) {
                acceptedStudents.add(new Student(roleISAcceptedSync.readLine()));
            }
            while (roleNonISAcceptedSync.ready()) {
                acceptedStudents.add(new Student(roleNonISAcceptedSync.readLine()));
            }

            // Sortare dupa nume.
            Collections.sort(acceptedStudents, Comparator.comparing(Student::getName));

            // Scrierea studentilor acceptati sortati in fisierul de iesire.
            for (Student student : acceptedStudents) {
                roleOutputFileSource.write(student.getName() + " " + student.getProgram());
                roleOutputFileSource.newLine();
            }
            roleOutputFileSource.flush();

            // _____________________________________________________________________
            // Curatarea sistemului
            // _____________________________________________________________________

            // Inchiderea fisierelor si curatarea componentelor.
            System.out.println("Controller: Distrugerea tuturor componentelor ...");
            filterSplitIS.interrupt();
            filterScreen17651.interrupt();
            filterScreen21701.interrupt();
            filterMergeAccepted.interrupt();

            // Inchiderea fisierelor.
            roleInputFileSync.close();
            roleOutputFileSource.close();
            rejectedFileSource.close();
            roleISSource.close();
            roleISSync.close();
            roleNonISSource.close();
            roleNonISSync.close();
            roleISAcceptedSource.close();
            roleISAcceptedSync.close();
            roleNonISAcceptedSource.close();
            roleNonISAcceptedSync.close();
        } catch (Exception e) {
            // Afisarea de informatii pentru debugging.
            System.out.println("Exceptie aparuta in SystemMain.");
            e.printStackTrace();
            System.exit(1);
        }

        // Final!
        System.out.println("Controller: Final!");
    }
}
