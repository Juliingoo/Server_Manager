import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputConsola implements Runnable{
    Process procesoServidor;

    InputConsola(Process procesoServidor){
        this.procesoServidor = procesoServidor;
    }

    @Override
    public void run() {
        System.out.println(Main.inicioMensaje + "Input iniciado");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(procesoServidor.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Imprime lo que el servidor está enviando a la consola
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
