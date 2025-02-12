import java.io.*;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    static String rutaActual = System.getProperty("user.dir") + "/";
    static String nombreArchivoServer;
    static String inicioMensaje = "[Server Manager] - ";
    static String nombreFicheroProperties = "Server_Manager" + ".properties";
    static Thread hiloReinicioServer;
    static Thread hiloInputConsola;
    static Thread hiloOutputUsuario;
    static Thread hiloComprobadorProcesoServidor;
    static Thread hiloApagado;
    static TimerTask timerTaskReinicioServidor;
    static ProcessBuilder pbServidor;
    static Process procesoServidor;

    static String Xmx;
    static String Xms;
    static int rebootHour;
    static int rebootMinute;
    static int rebootSecond;


    public static void main(String[] args) {
        try {
            System.out.println(inicioMensaje + "Programa iniciado");
            ejecutar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ejecutar(){
        try {
            leerAjustes();

            pbServidor = new ProcessBuilder("java", "-Xms" + Xmx, "-Xmx" + Xmx, "-jar", rutaActual + nombreArchivoServer);
            pbServidor.redirectErrorStream(true);
            procesoServidor = pbServidor.start();

            iniciarInputOutput();
            iniciarReinicioServerProgramado();
            iniciarComprobadorProcesoServidor();

            Thread.sleep(10000);
            establecerPremisasApagado();

            enviarComandoServidor("say Servidor iniciado");


        }catch (Exception e){
            e.printStackTrace();
            System.out.println(inicioMensaje + "Se ha producir un error. Cerrando programa de manera segura");
            System.exit(0);
        }

    }

    static void leerAjustes(){
        comprobarProperties();

        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(nombreFicheroProperties)) {
            properties.load(fileInputStream);
            nombreArchivoServer = properties.getProperty("serverFileName");
            Xms = properties.getProperty("InitialRam(-Xms)");
            Xmx = properties.getProperty("MaxRam(-Xmx)");
            rebootHour = Integer.parseInt(properties.getProperty("rebootHour"));
            rebootMinute = Integer.parseInt(properties.getProperty("rebootMinute"));
            rebootSecond = Integer.parseInt(properties.getProperty("rebootSecond"));

        } catch (Exception e) {
            System.out.println("Error al leer ajustes: " + e.getMessage());
        }
    }

    static void comprobarProperties(){
        File fileProperties = new File(nombreFicheroProperties);
        try {
            if(!fileProperties.exists()) {
                System.out.println(inicioMensaje + "Archivo .properties del Manager no creado, creandolo...");
                Properties propertiesDefault = new Properties();
                propertiesDefault.setProperty("serverFileName", "server.jar");
                propertiesDefault.setProperty("InitialRam(-Xms)", "3G");
                propertiesDefault.setProperty("MaxRam(-Xmx)", "3G");
                propertiesDefault.setProperty("rebootHour", "6");
                propertiesDefault.setProperty("rebootMinute", "0");
                propertiesDefault.setProperty("rebootSecond", "0");

                FileOutputStream fosProperties = new FileOutputStream(nombreFicheroProperties);

                propertiesDefault.store(fosProperties, "Properties Server_Manager");

                System.out.println(inicioMensaje + "Archivo " + nombreFicheroProperties + " creado");
                System.out.println(inicioMensaje + "¡Por favor, configura el archivo " + nombreFicheroProperties + " antes de continuar!");
                System.exit(0);
            }
        }catch (Exception e){
            System.out.println("Error creando el archivo Server_Manager.properties: " + e.getMessage());
            System.exit(0);
        }
    }

    static void iniciarInputOutput(){
        InputConsola inputConsola = new InputConsola(procesoServidor);


        hiloInputConsola = new Thread(inputConsola);

        if(hiloInputConsola.isAlive()){
            hiloInputConsola.interrupt();
        }

        hiloInputConsola.start();


        OutputUsuario outputUsuario = new OutputUsuario();

        hiloOutputUsuario = new Thread(outputUsuario);

        if(hiloOutputUsuario.isAlive()){
            hiloOutputUsuario.interrupt();
        }

        hiloOutputUsuario.start();
    }

    static void iniciarComprobadorProcesoServidor(){

        ComprobadorProcesoServidor comprobadorProcesoServidor = new ComprobadorProcesoServidor(hiloOutputUsuario, hiloInputConsola, hiloReinicioServer, hiloComprobadorProcesoServidor, hiloApagado, procesoServidor, pbServidor);

        hiloComprobadorProcesoServidor = new Thread(comprobadorProcesoServidor);

        hiloComprobadorProcesoServidor.start();

    }

    static void iniciarReinicioServerProgramado(){

        Calendar horaProgramada = horaProgramada();

        if (horaProgramada.getTimeInMillis() <= System.currentTimeMillis()) {
            horaProgramada.add(Calendar.DATE, 1);
        }

        ReinicioServer reinicioServer = new ReinicioServer();

        hiloReinicioServer = new Thread(reinicioServer);

        Timer timerReinicioServer = new Timer();

        timerTaskReinicioServidor = new TimerTask() {
            @Override
            public void run() {
                hiloReinicioServer.start();
            }
        };

        timerReinicioServer.schedule(timerTaskReinicioServidor, horaProgramada.getTime());

        System.out.println(inicioMensaje + "Reinicio del servidor programado a las : " + horaProgramada.getTime());

    }

    static void establecerPremisasApagado(){

        ShutdownHookRunnable shutdownHookRunnable = new ShutdownHookRunnable();

        hiloApagado = new Thread(shutdownHookRunnable);

        Runtime.getRuntime().addShutdownHook(hiloApagado);

    }

    static void enviarComandoServidor(String comando){
        OutputStream outputStream = procesoServidor.getOutputStream();

        try {
            outputStream.write((comando + "\n").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    static Calendar horaProgramada(){
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, rebootHour);
        targetTime.set(Calendar.MINUTE, rebootMinute);
        targetTime.set(Calendar.SECOND, rebootSecond);
        return targetTime;
    }

}