# Klijent

Klijent je Single Page Aplikacija napisana u ClojureScript jeziku i usko je povezana sa servera. Projekat je kreiran ako FigWheel projekat i sadrži sledeće biblioteke:

1. [org.clojure/core.async] - biblioteka koja omogućava asinhrono programiranje i korišćenje asinhronih blokova
2. [reagent] - biblioteka napisana nad React.JS frameworkom, koristi takozvani Hiccup tip podataka kako bi prikazao HTML i sadrži metode koje okidaju rad sa React.JS
3. [secretary] - biblioteka za rutiranje u okviru klijenta.
4. [re-frame] - framework nad reagent-om
5. [re-com] - kolekcija UI elemenata reagent-a
6. [figwheel-sidecar] - pristup REPL-u iz IntelliJ Idea okruženja
7. [cljs-http] - biblioteka za slanje http poziva ka serveru
8. [hickory "0.7.1"] - biblioteka za translaciju HTML-a u Hiccup format

Projekat sadrži Boostrap 4 biblioteku za responive layout, i css skin za boje.

U maloj količini korišćene su i određene JavaScript biblioteke, koje se pozivaju iz ClojureScript-a:
1. JQuery
2. Highchart
3. Trix.js

### Komponente i stranice

Organizacija koda za klijentski deo odrađena je kroz komponente, gde svaka stranica ustvari sadrži skup komponenti.
Komponente koje se ponavljaju na svakoj od stranica:
- Komponenta "comp_messages" služi za prikaz svih poruka kako serverskih, tako i klijentskih obaveštenja.
- Navbar komponenta "comp_menus" sadrži meni, u okviru kog postoji određen nivo autorizacije, i admin ima pristup određenim stranicama kojima klijent nema pristup.
- Footer komponenta sadrži pored određenih informacija o projektu i formu koja salje podatke na server koji zatim sastavlja email od tih podataka i salje ga na skoncar@live.com adresu.

Klijentske stranice:
#### 1. /

Home page

Sadrži formu za login i registraciju korisnika, navbar, footer, i landing page panel. Komponenta za registraciju popunjava padajući meni za zemlje na osnovu podataka sa servera.

#### 2. /home

Home-logged page

Nakon login-a ili registracije dostupna je ova stranica na kojoj se nalazi grafikon za prikaz cena, tabela za prikaz krugova, navbar i footer. Tabela je popunjana podacima na osnovu matrice koju preuzima sa servera. Grafikon takođe koristi podatke sa servera, a biblioteka koja je korišćena jeste Highchart.js.
#### 3. /me

Prikaz profila korisnika. Ovoj stranici se pristupa iz menija ili klikom na link u blog objavi. Ukoliko je profil logovanog korisnika postoje opcije promena šifre, upload profilne slike i izmena određenih podataka o profilu.

#### 4. /blog

Izlistava sve blog objave. Postoji sekcija svih i sekcija od 5 najpopularnijih objava(objava sa najviše palčeva),

#### 5. /write-blog

Samo admin ima pristup ovoj stranici. Sadrži formu za kačenje blog objave. Invokujemo trix.js biblioteku za polje koje formira Rich HTML text. On se u bazi čuva kao string, a za konačni prikaz korišćena je biblioteka hickory za konvertovanje tog stringa u Hiccup format koji koristi reagent engine. 

#### 6. /post/:id

Sadrži otvorenu blog objavu. Sa ove strance može se glasati palac gore, palac dole.

## Klijent instalacija

Kako bi klijent radio, potrebno je prvo pokrenuti server, a zatim i klijenta sledećim komandama:

Prvo pokrenemo REPL:

	lein repl

A zatim u REPL-u pokrenemo komande:

	(use 'figwheel-sidecar.repl-api)
	(start-figwheel!) ;; <-- fetches configuration 
	(cljs-repl)
	
## Licenca

Copyright © Stevan Koncar