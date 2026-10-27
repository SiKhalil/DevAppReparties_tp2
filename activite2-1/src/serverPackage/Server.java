package serverPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class Server {
    private static final int PORT = 1234;

    private static InetAddress adresseReelle() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                continue;
            }
            Enumeration<InetAddress> adresses = ni.getInetAddresses();
            while (adresses.hasMoreElements()) {
                InetAddress a = adresses.nextElement();
                if (a instanceof Inet4Address && a.isSiteLocalAddress()) {
                    return a;
                }
            }
        }
        return InetAddress.getLocalHost();
    }

    private static String formater(double valeur) {
        if (valeur == Math.rint(valeur) && Math.abs(valeur) < 1e15) {
            return String.valueOf((long) valeur);
        }
        return String.valueOf(valeur);
    }

    private static String calculer(String a, String operateur, String b) {
        try {
            double x = Double.parseDouble(a);
            double y = Double.parseDouble(b);
            switch (operateur) {
                case "+":
                    return formater(x + y);
                case "-":
                    return formater(x - y);
                case "*":
                    return formater(x * y);
                case "/":
                    if (y == 0) {
                        return "Erreur : division par zéro";
                    }
                    return formater(x / y);
                default:
                    return "Erreur : opérateur inconnu";
            }
        } catch (NumberFormatException e) {
            return "Erreur : opérande invalide";
        }
    }

    public static void main(String[] args) {
        try {
            InetAddress adresse = args.length > 0 ? InetAddress.getByName(args[0]) : adresseReelle();
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(adresse, PORT));
            System.out.println("Je suis un serveur en attente la connexion d'un client ");
            System.out.println("Adresse IP : " + adresse.getHostAddress() + " - Port : " + PORT);
            Socket socket = serverSocket.accept();
            System.out.println("un client est connecté : " + socket.getInetAddress().getHostAddress());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            while (true) {
                String a = in.readLine();
                if (a == null || a.equals("FIN")) {
                    break;
                }
                String operateur = in.readLine();
                String b = in.readLine();
                if (operateur == null || b == null) {
                    break;
                }
                String resultat = calculer(a, operateur, b);
                System.out.println("Reçu : " + a + " " + operateur + " " + b + " -> " + resultat);
                out.println(resultat);
            }
            in.close();
            out.close();
            socket.close();
            serverSocket.close();
            System.out.println("Connexion fermée");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
