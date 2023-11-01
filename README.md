# Ricart-Agrawala_DS

## *Systemy Rozproszone : implementacja algorytmu Ricarta i Agrawali*

### Algorytm

**Algorytm Ricarta i Agrawali** jest zoptymalizowana wersją **algorytmu Lamporta**, która obywa się bez wiadomości **`ZWOLNIJ`** (*sekcję krytyczną*) poprzez sprytne połączenie ich z wiadomościami typu **`ODPOWIEDŹ`**.

Gdy proces `Pi` chce wejść do sekcji krytycznej, wysyła wiadomość z żądaniem oznaczoną znacznikiem czasowym do wszystkich procesów, które znajdują się w jego zbiorze żądań. W momencie gdy proces `Pj` otrzyma żądanie od procesu `Pi` , wysyła odpowiedź do procesu `Pi` pod warunkiem, że nie zachodzi jedna z następujących sytuacji:

1) proces `Pj` wykonuje sekcję krytyczną,

2) proces `Pj` żąda wykonania sekcji krytycznej, a znacznik czasowy jego żądania jest mniejszy niż znacznik czasowy żądania procesu `Pi`.

Jeżeli zachodzi, któraś z tych dwóch sytuacji, żądanie procesu `Pi` jest odkładane i trafia do kolejki żądań w procesie `Pj` .

Proces `Pi` wchodzi do sekcji krytycznej po otrzymaniu wiadomości **`ODPOWIEDŹ`** od wszystkich procesów, które są w jego zbiorze `Ri` . W chwili kiedy proces `Pi` kończy wykonywanie sekcji krytycznej wysyła odpowiedzi na wszystkie odłożone wcześniej żądania, a następnie usuwa je z kolejki.

Odpowiedzi na żądania procesu blokowane są tylko przez procesy, które ubiegają się o wejście do sekcji krytycznej i mają wyższy priorytet tzn. mniejszy znacznik czasowy. W ten sposób, gdy proces odsyła wiadomość typu odpowiedź na wszystkie odroczone żądania, proces, który ma kolejny najwyższy priorytet żądania, otrzymuje ostatnią niezbędną odpowiedź i wchodzi do sekcji krytycznej.

Innymi słowy sekcje krytyczne w algorytmie Ricarta i Agrawali wykonywane są w kolejności zgodnej z wartościami znaczników czasowych ich żądań. Na podobnej zasadzie funkcjonuje przetwarzanie kolejek ***FIFO***.

---

### Schemat blokowy
