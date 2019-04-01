
package calculadoraservidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class CalculadoraServidor{

    public static void main(String[] args) {
        String puertoS = JOptionPane.showInputDialog("Introduce el puerto");
        int puerto = Integer.parseInt(puertoS);

        try{
            ServerSocket serverSocket = new ServerSocket(puerto);
            System.out.println("Arrancado el servidor");
            while (true) {
                Socket newSocket = serverSocket.accept();
                new Calculadora(newSocket).start();
            }
        }catch(IOException ex) {
            System.out.println("Error al recibir conexiones");
        }
    }
}

class Calculadora extends Thread {
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    public Calculadora(Socket socket) throws IOException {
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }
  
    @Override
    public void run(){

        try{
            byte[] mensaje = new byte[20];
            byte[] signo = new byte[1];
            byte[] mensaje2 = new byte[20];

            //Leemos los dos números y el signo
            is.read(mensaje);
            System.out.println("Mensaje recibido: " + new String(mensaje));
            String numero1 = new String(mensaje);

            float num1 = Float.parseFloat(numero1);

            is.read(signo);
            System.out.println("Mensaje recibido: " + new String(signo));
            String signo1 = new String(signo);
            System.out.println(signo1);
            is.read(mensaje2);

            System.out.println("Mensaje recibido: " + new String(mensaje2));
            String numero2 = new String(mensaje2);
            float num2 = Float.parseFloat(numero2);

            float total = 0;
            //Se realiza el cálculo según el signo introducido
            if (signo1.equalsIgnoreCase("+")){
                total = num1 + num2;
            }else if(signo1.equalsIgnoreCase("-")){
                total = num1 - num2;
            }else if(signo1.equalsIgnoreCase("x")){
                total = num1 * num2;
            }else if(signo1.equalsIgnoreCase("/")){
                total = num1 / num2;   
            }else if(signo1.equalsIgnoreCase("r")){
                if(num1<=-1){
                    System.out.println("No se puede calcular la raíz cuadrada de números negativos");
                }else{
                    total = (float)Math.sqrt(num1); 
                }    
            }else{
                System.out.println("Símbolo incorrecto");
            }

            System.out.println("Enviando mensaje");
            String resultado = "" + total;

            // Devolvemos el resultado al cliente
            os.write(resultado.getBytes());
        }catch(IOException ex){
            System.out.println("Error de conexión");
        }finally{
            try{
                socket.close();
            }catch(IOException ex){
                System.out.println("Error al cerrar la conexión");
            }
        }
    }
}