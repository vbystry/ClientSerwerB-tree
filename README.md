# ClientSerwerB-tree

Implementacja algorytmów drzewa binarnego w technologii klient-serwer.

Klient jest okienową aplikacją umożliwiającą użytkownikowi wysyłania do serwera poleceń dotyczących:

-dodania nowego drzewa

-dodania elementu do istniejącego drzewa

-usunięcie elementu z istniejącego drzewa

-wyświetlenie istniejącego drzewa

Klient wysyła do serwera odpowiednie komendy do obsługi drzewa, przechowywanie oraz operacje na drzewach wykonywane są na serwerze.

Nowe drzewo użytkownik tworzy przy użyciu MenuBar'a poprzez wybranie typu drzewa:

![image](./MenuBar.png)

Przykładowy wygląd klienta:

![image](./ClientExample.png)

Wywołanie akcji dodania nowego elementu spodowuje wywołanie następujacego okna dialogowego:

![image](./AddDialogExample.png)

Przykłady drzew wyświetlonych po wywołaniu akcji wyświetlenia drzewa:

![image](./IntTreeExample1.png)
![image](./StringTreeExample.png)

Wywołanie akcji usunięcia elementu spowoduje wywołanie odpowiedniego okna dialogowego, przykładowo dla pierwszego drzewa mamy:

![image](./DeleteDialogExample.png)

![image](./IntTreeExample2.png)
