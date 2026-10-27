<<<<<<< HEAD
# TP2-DAR2026
=======
# TP2-DAR2026 : Les sockets TCP, échange de chaînes de caractères et d'objets

Fattoum Mohamed Khalil, LSI3 1.2, Développement d'applications réparties

## Structure

- `activite2-1/` : échange de chaînes de caractères (opération complète)
- `activite2-2/` : échange d'objets sérialisés (`Operation` et `Resultat`)

Les deux applications reprennent la version de l'activité 1-3 du TP1 : le serveur se lie à son adresse IP réelle et le client reçoit l'adresse IP du serveur.

## Compilation et exécution

```
cd activite2-2
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out serverPackage.Server
java -cp out clientPackage.Client <adresse IP du serveur>
```

Le même principe s'applique à l'activité 2-1.

## Activité 2-1 : échange de chaînes de caractères

### Étape 1 : espace de travail
Capture : répertoire `FattoumMohamedKhalil_LSI3_DevAppReparties`
![espace de travail](captures/a21_espace.png)

### Étape 2 : flux de traitement de CC
Le client saisit une opération (par exemple `55 * 25`). Une expression régulière vérifie la syntaxe avant tout envoi : `25 ** 55` et `abc + 3` sont refusées côté client. Si la syntaxe est correcte, le client envoie séparément, avec `PrintWriter.println`, le premier opérande, l'opérateur et le deuxième opérande. Le serveur les lit avec `BufferedReader.readLine`, calcule le résultat (addition, soustraction, multiplication, division) et renvoie le résultat sous forme de chaîne. La division par zéro est signalée par un message d'erreur. La saisie de `FIN` ferme proprement la connexion des deux côtés.

Capture : console du serveur
![serveur](captures/a21_serveur.png)

Capture : console du client
![client](captures/a21_client.png)

Interprétation : les flux `InputStreamReader` et `OutputStreamWriter` utilisent l'encodage UTF-8 pour que les chaînes soient identiques sur les deux machines. Chaque élément de l'opération occupe une ligne, ce qui permet d'échanger plus d'un octet à la fois.

## Activité 2-2 : échange d'objets

La classe `Operation` (opérande 1, opérateur, opérande 2) et la classe `Resultat` (valeur ou message d'erreur) implémentent `Serializable`. Elles sont placées dans le package `commonPackage`, partagé par le client et le serveur.

Le client envoie un objet `Operation` avec `ObjectOutputStream`. Le serveur le reçoit avec `ObjectInputStream`, récupère les données, calcule, puis renvoie un objet `Resultat`. Le client lit et affiche le résultat. L'envoi d'un objet `null` ferme proprement la connexion.

Les deux côtés créent d'abord l'`ObjectOutputStream` et le vident avec `flush()` avant de créer l'`ObjectInputStream`, afin d'éviter un blocage lors de l'échange des en-têtes de flux.

Capture : console du serveur
![serveur](captures/a22_serveur.png)

Capture : console du client
![client](captures/a22_client.png)


```
>>>>>>> 5f5085d (TP2 DAR2026)
