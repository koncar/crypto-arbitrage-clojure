# Server
Serverski deo implementiran je kao REST veb servis. Kao bazu podataka koristi Mongo. Za razmenu podataka koriste se uglavnom JSON-i, uz jedan slučaj gde klijent može upload-ovati sliku, u tom slučaju u komunikaciji se koriste byte-ovi, odnosno binarni podaci.
Na serverskom delu u implementaciji korišćene su sledeće biblioteke:
1. clj-http - za komunikaciju sa API-ima izvan servera (API menjačnica priptovaluta, API za prikupljanje JSON-a liste zemalja...)
2. org.clojure/data.json - parsiranje JSON-a
3. com.novemberain/monger - biblioteka za rad sa Mongo bazom.
4. http-kit - startovanje rest servera
5. compojure - rutiranje u okviru servera
6. com.draines/postal - slanje email-a

API SERVERA:

#### 1. METODA: GET 

#### URL: /get-me/:id

OPIS: Prihvata ID kao neophodan parametar i vraća registrovanog korisnika u "users" kolekciji

#### 2. METODA: GET

#### URL: /get-countries

OPIS: Vraća sadržaj kolekcije "countries", sve podržane zemlje za registraciju

#### 3. METODA: GET

#### URL: /get-pairs

OPIS: Vraća sadržaj kolekcije "duplicate_pairs", sve podržane parove kriptovaluta za trgovinu.

#### 4. METODA: GET 

#### URL: /get-price-on-exchanges

OPIS: Cilj ove metode je da prikupi poslednje sačuvane cene svih podržanih parova na svim podržanim menjačnicama. Ovi podaci koriste se na klijentu za iscrtavanje grafikona.

#### 5. METODA: GET 

#### URL: /refresh-price-on-exchanges

OPIS: Cilj ove metode je da prikupi podatke o cenama sa svih podržanih menjačnica kriptovaluta i sa ih sačuva u bazu u okviru kolekcije "prices" koju prethodna metoda koristi. Izvršavanje ove metode je dugo, jer postoji komunikacija sa spoljnim serverima. Iz tog razloga razdvojene su metode gde se prikupljaju poslednje sačuvane cene iz baze i osvežavanje cena, kako bi klijenti koji se uloguju odmah imali prikazan grafikon, i ne bi morali da čekaju da prikupljanje podataka koje se vrši u okviru ove metode. Klijent koji izabere opciju da osveži cene i okine ovu metodu, dobiće sveže podatke, a ujedno će klijentima koji se posle toga uloguju omogućiti da imaju i oni sveže podatke.

#### 6. METODA: GET

#### URL: /get-inner-matrix/:a/:b

OPIS: Sastavlja podatke za tabelu. Sortira cene na menjačnicama za podržane parove valuta prosleđene kroz parametre :a i :b po opadajućem i rastućem redosledu. Na osnovu tih podataka sastavlja matricu koja se koristi u tabli na klijentu za prikaz podataka. 

#### 7. METODA: GET

#### URL: /get-ascending-price/:a/:b

OPIS: Vraća sortiranu cenu po menjačnicama u rastućem redosledu za valute :a i :b

#### 8. METODA: GET

#### URL: /get-descending-price/:a/:b

OPIS: Vraća sortiranu cenu po menjačnicama u opadajućem redosledu za valute :a i :b

#### 9. METODA: POST

#### URL: /populate-countries

OPIS: Poziva spoljašnji API: "https://restcountries.eu/rest/v2/all" i prikuplja JSON sa svim zamnjama. Obrađuje podatke i čuva dokument u odgovarajućem formatu u kolekciji "countries". 

	Za rad ove metode neophodan je body JSON: {	"password":"admin" } kako bismo imali određen nivo autentifikacije.

#### 10. METODA: POST

#### URL: /populate-exchanges

OPIS: Resetuje podržane menjačnice. Resetovanje se vrši prema hardcodovanim podacima, s obzirom da je potrebno određeno pisanje koda. Kako bi se dodala nova menjačnica potrebno je serverski obezbediti sledeće metode:

    1. PRIKUPLJANJE CENA SA SERVERA MENJAČNICE ZA PAR VALUTA
    2. PRIKUPLJANJE SVIH PODRŽANIH PAROVA VALUTA I DODAVANJE ISTIH U BAZU U ODGOVARAJUĆEM FORMATU
    3. DODATI DOKUMENT SA IMENOM, ID-JEM MENJAČNICE I WEBSITE-OM MENJAČNICE.

Ponavljanjem ovog procesa možemo imati mnogo podržanih menjačnica i time obogatiti sadržaj našeg sajta. Pravac daljeg razvoja vodi ka ovome.
    
    Za rad ove metode neophodan je body JSON: {	"password":"admin" } kako bismo imali određen nivo autentifikacije.

#### 11. METODA: POST

#### URL: /populate-pairs

OPIS: Prolazi kroz sve podržane parove svih menjačnica i resetuje podatke na odgovarajući način za dalje korišćenje
    
    Za rad ove metode neophodan je body JSON: {	"password":"admin" } kako bismo imali određen nivo autentifikacije.
    
#### 12. METODA: POST

#### URL: /update-me/:id

OPIS: Validira podatke, i vrši izmene nad korisnikom sa :id id-jem u "users" kolekciji. 

Primer JSON body dela: 

    
    {
	"email":"voste@email.com",
	"name":"stevan",
	"username":"voste",
	"password":"admin",
	"country":{
		"_id":"RS"
	},
	"admin_password":"admin_password"
    }

#### 13. METODA: POST

#### URL: /change-password/:id

OPIS: Validira podatke, i vrši izmenu šifre nad korisnikom sa :id id-jem u "users" kolekciji. 

Primer JSON body dela: 

    
    {
	"password":"novasifra",
    }
    

#### 14. METODA: POST

#### URL: /register

OPIS: Validira podatke, i vrši registraciju korisnika ubacivanjem odgovarajućeg dokumenta u "users" kolekciju.
Ukoliko je prosleđen parametar "admin_password": "admin_password", korisnik će se dalje na klijentu gledati kao admin i imaće određene opcije koje običan klijent nema.

Primer JSON body dela: 

    
    {
	"email":"voste@email.com",
	"name":"stevan",
	"username":"voste",
	"password":"admin",
	"country":{
		"_id":"RS"
	},
	"admin_password":"admin_password"
    }
    

#### 15. METODA: POST

#### URL: /save-blog-post/:id

OPIS: Validira podatke, i vrši čuvanje blog objave u kolekciju "blog_posts". 

Primer JSON body dela:

    
    {
	"title":"Ovo je prvi post",
	"description":"Ovde ne pisemo niocemu",
	"text":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    }
    

#### 16. METODA: POST

#### URL: /blog-post-thumbs-up/:id

OPIS: Povećava broj palčeva za post sa :id id-ijem. Potrebno je kroz body deo proslediti id korisnika kako bi se sačuvalo da je on već glasao za ovaj post. Po ponovnom glasanju ako se on nalazi u kolekciji da je glasao, omogućiće mu se glas. Ova opcija je isključena radi lakšeg testiranja.

Primer JSON body dela:

    
    {
	"id":"8949711a-d549-4d31-b660-be5fa284589b"
	}
	
#### 17. METODA: POST

#### URL: /blog-post-thumbs-up/:id

OPIS: Smanjuje broj palčeva za post sa :id id-ijem. Potrebno je kroz body deo proslediti id korisnika kako bi se sačuvalo da je on već glasao za ovaj post. Po ponovnom glasanju ako se on nalazi u kolekciji da je glasao, omogućiće mu se glas. Ova opcija je isključena radi lakšeg testiranja.

Primer JSON body dela:

    
    {
	"id":"8949711a-d549-4d31-b660-be5fa284589b"
	}
    
#### 18. METODA: POST

#### URL: /email-me

OPIS: Šalje email sa podacima iz body dela na skoncar@live.com sa email-a test@miracledojo.com čiji su SMTP podaci iskorišćeni.

Primer JSON body dela:

    
    {
	"name":"Stevan",
	"email":"skoncar@gmail.com",
	"subject":"testiranje",
	"message":"test poruka"
	}
    
#### 19. METODA: GET 

#### URL: /get-blog-posts

OPIS: Vraća sve blog objave

#### 20. METODA: GET 

#### URL: /get-blog-posts-sorted

OPIS: Vraća sve blog objave sortirane po broju palčeva

#### 21. METODA: GET 

#### URL: /get-my-blog-posts/:id

OPIS: Vraća sve blog objave korisnika sa :id id-jem.

#### 22. METODA: POST

#### URL: /upload-profile-picture/:id

OPIS: Preuzima binarne podatke i od njih sastavlja .jpg sliku koju čuva u folderu public/profilepictures sa nazivom :id.jpg
U ovom folderu su fajlovi dostupni klijentima kroz <ip adresa servera>:<port>/profilepictures/:id.jpg i koriste se na njihovim profilima za prikaz.

## Server instalacija
### Baza
Za rad servera potrebno je instalirati MongoDB. Nakon instalacije pokrenuti bazu sa "mongod" komandom.
U okviru foldera "db" nalazi se eksportovana baza koja se može importovati kako bi imala određene početne podatke.
    
    mongorestore -d monger-test <putanja do "db" direktorijuma>/db/monger-test
    
### Server

Kako bi se pokrenuo server, potrebno je instalirati Leinigen, a zatim u direktorijumu cryptoarbitrage pokrenuti komandom 
    
    lein run
    
pod uslovom da je Leinigen dodat u PATH.

U okviru servera postoje u "core.clj" fajlu pozvane metode "populate-exchange", "populate-countries" i "populate-pairs" čiji su podaci neophodni kako bi klijent radio. Ovakvo pozivanje je loša praksa, tako da bi ovo trebalo ukloniti nakon prvog pokretanja servera, ili pre pokretanja ako je restorovana baza. Takođe server se može pokrenuti bez ovih metoda, ali pre nego što se pokrene klijent potrebno je okinuti te tri metode iz nekog drugog REST klijenta.

Takođe potrebno je naglasiti da je za rad servera neophodan internet, kako koristi resurse sa istog.


## Server testiranje
Napisani su unit testovi za određene metode koje su neopodne za rad servera. Kako bi se testovi pokrenuli potrebno je izvršiti komandu 

    lein test
    
u cryptoarbitrage folderu.

## License

Copyright © Stevan Koncar

