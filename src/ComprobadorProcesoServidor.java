public class ComprobadorProcesoServidor implements Runnable{
    Thread hiloOutputUsuario;
    Thread hiloInputConsola;
    Thread hiloReinicioServer;
    Thread hiloComprobadorProcesoServidor;
    Thread hiloApagado;
    Process procesoServidor;
    ProcessBuilder pbServidor;

    static String inicioMensaje = "[Server Manager] - ";


    ComprobadorProcesoServidor(Thread hiloOutputUsuario, Thread hiloInputUsuario, Thread hiloReinicioServer, Thread hiloComprobadorProcesoServidor, Thread hiloApagado, Process procesoServidor, ProcessBuilder pbServidor){

        this.hiloOutputUsuario = hiloOutputUsuario;
        this.hiloInputConsola = hiloInputUsuario;
        this.hiloReinicioServer = hiloReinicioServer;
        this.hiloApagado = hiloApagado;
        this.hiloComprobadorProcesoServidor = hiloComprobadorProcesoServidor;
        this.procesoServidor = procesoServidor;
        this.pbServidor = pbServidor;

    }

    @Override
    public void run() {
        try {
        while(procesoServidor.isAlive()){
            Thread.sleep(5000);
            if(!procesoServidor.isAlive()){
                System.out.println(inicioMensaje + "Se ha detectado que el proceso del servidor no se está ejecutando");
                Thread.sleep(5000);

                if(!procesoServidor.isAlive()){
                    int segundosEnArrancar = 5;

                    System.out.println(inicioMensaje + "Volviendo a arrancar el servidor en " + segundosEnArrancar + " segundos");

                    Thread.sleep(segundosEnArrancar*1000);

                    hiloInputConsola.interrupt();

                    hiloReinicioServer.interrupt();

                    hiloOutputUsuario.interrupt();

                    Main.timerTaskReinicioServidor.cancel();

                    Runtime.getRuntime().removeShutdownHook(Main.hiloApagado);

                    Main.ejecutar();

                    break;


                }
            }
        }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
