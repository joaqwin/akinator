package org.example;

import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.io.*;
import java.sql.*;
import java.util.Scanner;
public class Main {
    public static String mostrarSQL(Connection cnDB) throws SQLException {
        Statement statementDB = cnDB.createStatement();
        ResultSet rs = statementDB.executeQuery("SELECT * from Netlabbers");

        ResultSetMetaData rsmdDB = rs.getMetaData();
        int i;
        int numColumnas = rsmdDB.getColumnCount();
        System.out.println(numColumnas);
        String numero = "";
        while (rs.next()) {
            for (i = 1; i <= numColumnas; i++) {
                numero += rs.getString(i) + " ";
            }
            numero += "\n";
        }

        return numero;
    }
    public static void main(String[] args) throws SQLException {
        try {
            //me conecto a mysql y creo la base de datos.

            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/", "joaqwin", "bubaloo");
            Statement stmt = cn.createStatement();
            stmt.execute("CREATE database proyectoDB");
            cn.close();
            // Importo mi base de datos respaldada a la base de datos creada.

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("mysql", "-u", "joaqwin", "--password=bubaloo", "proyectoDB", "-e", "SOURCE /home/usuario/Escritorio/respaldoDB.sql");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            //

            //Comienza el proyecto
            System.out.println("Empecemos a jugar. Piensa en un personaje y luego responde mis preguntas. Intentaré adivinar en quién estás pensando.");
            Connection cnDB = DriverManager.getConnection("jdbc:mysql://localhost/proyectoDB", "joaqwin", "bubaloo");
            //Creo el scanner para leer luego las respuestas del usuario.
            Scanner scanner = new Scanner(System.in);
            Scanner newScanner = new Scanner(System.in);

            //Obtengo el numero de columnas. Esto para saber cuántas preguntas hacer.
            Statement stmtNombreColumnas = cnDB.createStatement();
            ResultSet rsNombreColumnas = stmtNombreColumnas.executeQuery("SELECT * from Netlabbers");
            ResultSetMetaData rsmdNombreColumnas = rsNombreColumnas.getMetaData();
            int numColumnas = rsmdNombreColumnas.getColumnCount();
            int i = 4;
            String pregunta ="";
            String genero ="";
            int añoIngreso =0;
            String academy ="";
            String signo="";
            String lentes="";
            int numFilas = 0;
            //Comienzo con las preguntas hasta que quede una entidad.
            while ((i <= numColumnas) && (numFilas != 1)) {
                pregunta = rsmdNombreColumnas.getColumnName(i);
                switch (pregunta){
                    case "Genero":
                        System.out.println("¿De qué género es tu personaje?");
                        genero = scanner.nextLine();
                        Statement statementBorrarMujeres = cnDB.createStatement();
                        statementBorrarMujeres.executeUpdate("DELETE from Netlabbers where Genero <> " + "'"+genero+"'");
                        break;

                    case "AÑO_ING":
                        System.out.println("¿En qué año ingresó a Netlabs?");
                        añoIngreso = scanner.nextInt();
                        Statement stmtAñoIngreso = cnDB.createStatement();
                        stmtAñoIngreso.executeUpdate("DELETE from Netlabbers where AÑO_ING <> " + añoIngreso);
                        break;

                    case "Academy":
                        System.out.println("¿Participó del academy?");
                        academy = newScanner.nextLine();
                        Statement stmtAcademy = cnDB.createStatement();
                        stmtAcademy.executeUpdate("DELETE from Netlabbers where Academy <> "+ "'"+academy+"'");
                        break;

                    case "Signo":
                        System.out.println("¿De qué signo es?");
                        signo = newScanner.nextLine();
                        Statement stmtSigno = cnDB.createStatement();
                        stmtSigno.executeUpdate("DELETE from Netlabbers where Signo <> "+ "'"+signo+"'");
                        break;

                    case "Lentes":
                        System.out.println("¿Usa lentes?");
                        lentes = newScanner.nextLine();
                        Statement stmtLentes = cnDB.createStatement();
                        stmtLentes.executeUpdate("DELETE from Netlabbers where Lentes <> "+"'"+lentes+"'");
                        break;
                }
                Statement statementObtenerNumFilas = cnDB.createStatement();
                ResultSet rsNumFilas = statementObtenerNumFilas.executeQuery("SELECT COUNT(*) from Netlabbers");
                if(rsNumFilas.next()){
                    numFilas = rsNumFilas.getInt(1);
                }
                i += 1;
            }

            //Si el num de filas es 1, significa que ya adivinó qué personaje estaba pensando el usuario. Entonces lo printea.
            if (numFilas == 1){
                Statement adivinarPersona = cnDB.createStatement();
                ResultSet rsAdPer = adivinarPersona.executeQuery("SELECT Nombre, Apellido from Netlabbers");
                rsAdPer.next();
                System.out.println("Estás pensando en: "+ rsAdPer.getString(1)+ " "+ rsAdPer.getString(2));
            }else {

                ProcessBuilder processB = new ProcessBuilder();
                processB.command("mysql", "-u", "joaqwin", "--password=bubaloo", "proyectoDB", "-e", "SOURCE /home/usuario/Escritorio/respaldoDB.sql");
                Process process1 = processB.start();
                int exitCode1 = process1.waitFor();

                System.out.println("No he podido adivinar en quién estás pensando, por favor dime su nombre y luego su apellido.");
                String nombre = newScanner.nextLine();
                String apellido = newScanner.nextLine();
                Statement stmtInsertarPersona = cnDB.createStatement();
                stmtInsertarPersona.executeUpdate("INSERT into Netlabbers VALUES(id, "+"'"+nombre+"', "+"'"+apellido+"', "+"'"+genero+"', "+añoIngreso+", "+"'"+academy+"', "+"'"+signo+"', "+"'"+lentes+"')");

                ProcessBuilder processExport = new ProcessBuilder();
                processExport.command("mysqldump", "-u", "joaqwin", "--password=bubaloo", "proyectoDB");
                processExport.redirectOutput(new File("/home/usuario/Escritorio/respaldoDB.sql"));
                Process processWait = processExport.start();
                int exitCode2 = processWait.waitFor();
            }

            //Dropeo database para luego poder importar. Cierro conexión.
            Statement statementBorrarDB = cnDB.createStatement();
            statementBorrarDB.executeUpdate("drop database proyectoDB");
            cnDB.close();

        } catch (SQLException e) {
            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/", "joaqwin", "bubaloo");
            Statement statementBorrarDB = cn.createStatement();
            statementBorrarDB.executeUpdate("drop database proyectoDB");
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //} catch (InterruptedException e) {
            //throw new RuntimeException(e);
        //}
    }
}
