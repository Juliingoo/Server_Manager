public class ShutdownHookRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println(Main.inicioMensaje + "El programa está cerrándose. Enviando comando de apagado al servidor...");
        try {
            Main.hiloComprobadorProcesoServidor.interrupt();
            Main.enviarComandoServidor("stop");

            while(Main.procesoServidor.isAlive()){
                System.out.println(Main.inicioMensaje + "Esperando a que el servidor se apague...");
                Thread.sleep(3000);
            }

            System.out.println(Main.inicioMensaje + "Servidor detenido correctamente. Adios.");
        }catch (Exception e){
            System.out.println(Main.inicioMensaje + e.getMessage());
        }
    }
}
