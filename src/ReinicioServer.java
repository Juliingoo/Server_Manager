
import java.io.OutputStream;
import java.util.TimerTask;

public class ReinicioServer extends TimerTask {

    @Override
    public void run() {
        try {
            Main.enviarComandoServidor("say Servidor reiniciandose en 1 minuto. ¡Por favor, termina tus tareas!");

            Thread.sleep(60000);

            Main.enviarComandoServidor("stop");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
