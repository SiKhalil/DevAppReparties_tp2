package serverPackage;

import commonPackage.Operation;
import commonPackage.Resultat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
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

    private static Resultat calculer(Operation operation) {
        double x = operation.getOperande1();
        double y = operation.getOperande2();
        switch (operation.getOperateur()) {
            case '+':
                return new Resultat(x + y);
            case '-':
                return new Resultat(x - y);
            case '*':
                return new Resultat(x * y);
            case '/':
                if (y == 0) {
                    return new Resultat("Erreur : division par zéro");
                }
                return new Resultat(x / y);
            default:
                return new Resultat("Erreur : opérateur inconnu");
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
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Object recu = in.readObject();
                if (!(recu instanceof Operation)) {
                    break;
                }
                Operation operation = (Operation) recu;
                Resultat resultat = calculer(operation);
                System.out.println("Reçu : " + operation.getOperande1() + " " + operation.getOperateur() + " " + operation.getOperande2() + " -> " + resultat);
                out.writeUnshared(resultat);
                out.flush();
            }
            in.close();
            out.close();
            socket.close();
            serverSocket.close();
            System.out.println("Connexion fermée");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
