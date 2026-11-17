package corbaClient;

import corbaBanque.Compte;
import corbaBanque.IBanqueRemote;
import corbaBanque.IBanqueRemoteHelper;

import javax.naming.Context;
import javax.naming.InitialContext;

public class BanqueClient {

    public static void main(String[] args) {
        try {
            // a. Récupérer les propriétés de l'annuaire JNDI (jndi.properties sur le classpath)
            Context ctx = new InitialContext();

            // b. Récupérer la référence de l'objet distant
            org.omg.CORBA.Object ref = (org.omg.CORBA.Object) ctx.lookup("banque");

            // c. Convertir la référence vers IBanqueRemote : création du Stub
            IBanqueRemote stub = IBanqueRemoteHelper.narrow(ref);

            // d. Appels distants : test de chaque méthode
            System.out.println("=== Création des comptes ===");
            stub.creerCompte(new Compte(1, 1000f));
            stub.creerCompte(new Compte(2, 2500f));
            stub.creerCompte(new Compte(3, 300f));

            System.out.println("\n=== Versement de 500 sur le compte 1 ===");
            stub.verser(500f, 1);
            afficher(stub.getCompte(1));

            System.out.println("\n=== Retrait de 200 sur le compte 2 ===");
            stub.retirer(200f, 2);
            afficher(stub.getCompte(2));

            System.out.println("\n=== Consultation du compte 3 ===");
            afficher(stub.getCompte(3));

            System.out.println("\n=== Liste de tous les comptes ===");
            for (Compte c : stub.getComptes()) {
                afficher(c);
            }

            System.out.println("\n=== Conversion 100 EUR -> DT ===");
            System.out.println("100 EUR = " + stub.conversion(100f) + " DT");

            System.out.println("\n=== Test d'erreur : retrait trop important sur le compte 3 ===");
            try {
                stub.retirer(5000f, 3);
            } catch (org.omg.CORBA.SystemException e) {
                System.out.println("Exception reçue du serveur : " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void afficher(Compte c) {
        System.out.println("Compte n° " + c.code + " | solde = " + c.solde);
    }
}
