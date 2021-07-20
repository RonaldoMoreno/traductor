package com.mycompany.traductor2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class servidor {

    public static void main(String[] args) {

        try {
            System.out.println("Iniciado el servidor...");

            //Escuchando en el puerto 5001
            DatagramSocket ServerUdp = new DatagramSocket(5001);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket datos = new DatagramPacket(buffer, buffer.length);

                //Recibo la respuesta como BYTES
                ServerUdp.receive(datos);

                DataTR ConvTR = (DataTR) aobj(datos.getData());
                
                System.out.println(ConvTR.palabra);
           

                //Hago llamdo a la funcion Busc_Trad para realizar la traduccion
                DataTR Recv_Trad = Busc_Trad(ConvTR);
               
                //Convierto el objeto que obtuve de la traduccion
             
                byte[] mensaje= abytes(Recv_Trad);
                
                //Creo un nuevo Datagrama
                DatagramPacket responde = new DatagramPacket(mensaje, mensaje.length, datos.getAddress(), datos.getPort());

                //Envia la traduccion
                ServerUdp.send(responde);
              
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Esta funcion realizara la busqueda y traduccion
     private static DataTR Busc_Trad(DataTR Conv_TR) {
        //Creo un nuevo objeto de la clase dataTR
         DataTR objeto = new DataTR();
        //fichero donde guardo las palabras
        File fichero = new File("traductor.txt");
        boolean error = false;

        try {
            //Leo el fichero
            BufferedReader br = new BufferedReader(new FileReader(fichero));
            String linea = "";
            //Establezco un limite 
            int limite = 0;

            while ((linea = br.readLine()) != null) {
                
                if (linea.contains(Conv_TR.palabra)) {
                    //El limite sera hasta llegar a los : de cada linea del fichero
                    limite = linea.indexOf(":");
                    objeto.tipo = 1;
                    error = true;

                    if (Conv_TR.tipo == 0) {
                        //DE INGLES A ESPANOL
                        objeto.palabra = linea.substring(limite + 1, linea.length());
                       
                    } else {
                        //DE ESPANOL A INGLES
                    
                          objeto.palabra = linea.substring(0, limite);
                    }

                    break;
                }
            }

            //Si error es falso entonces no se contro la palabra por lo cual tipo es igual a 2
            if (!error) {
                objeto.tipo = 2;
                objeto.palabra = "No se encontro esta palabra";
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        //Retornamos un Objeto
        return objeto;
    }
     
     public static byte[] abytes(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }
      
      public static Object aobj(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return o.readObject();
            }
        }
    }
    

}