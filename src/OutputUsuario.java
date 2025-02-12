import java.util.Scanner;

public class OutputUsuario implements Runnable{

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)){

            System.out.println(Main.inicioMensaje + "Output iniciado");

            while (scanner.hasNextLine()) {
                String comando = scanner.nextLine();

                if (comando.equalsIgnoreCase("salir") || comando.equalsIgnoreCase("stop")) {
                    System.exit(0);
                } else {
                    Main.enviarComandoServidor(comando);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

