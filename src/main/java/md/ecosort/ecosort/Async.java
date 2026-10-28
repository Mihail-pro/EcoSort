package md.ecosort.ecosort;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import md.ecosort.ecosort.repository.DataException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

// Rulează operațiile cu baza de date în afara JavaFX Application Thread,
// apoi revine pe firul interfeței cu rezultatul sau cu mesajul de eroare.
public final class Async {

    private static final System.Logger LOG = System.getLogger(Async.class.getName());

    // Un singur fir: operațiile se execută în ordinea în care au fost cerute.
    private static final ExecutorService FIR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "ecosort-db");
        t.setDaemon(true);
        return t;
    });

    private Async() { }

    public static <T> void executa(Callable<T> lucru, Consumer<T> laSucces) {
        executa(lucru, laSucces, Async::arataEroare);
    }

    public static <T> void executa(Callable<T> lucru, Consumer<T> laSucces, Consumer<String> laEroare) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return lucru.call();
            }
        };
        task.setOnSucceeded(e -> laSucces.accept(task.getValue()));
        task.setOnFailed(e -> laEroare.accept(mesaj(task.getException())));
        FIR.execute(task);
    }

    // Doar mesajele scrise pentru utilizator sunt afișate; restul se jurnalizează.
    public static String mesaj(Throwable e) {
        if (e instanceof DataException || e instanceof IllegalArgumentException
                || e instanceof IllegalStateException || e instanceof SecurityException) {
            return e.getMessage();
        }
        LOG.log(System.Logger.Level.ERROR, "Eroare neașteptată", e);
        return "A apărut o eroare neașteptată. Încercați din nou.";
    }

    public static void arataEroare(String mesaj) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mesaj);
        alerta.setHeaderText("Operația nu a putut fi efectuată");
        alerta.show();
    }
}
