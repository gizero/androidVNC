# Sideband Information Protocol

"Sideband Information Protocol" è un protocollo studiato per coadiuvare una connessione VNC e trasferire informazioni aggiuntive utili per migliorare l'esperienza utente sul client VNC, con particolare riferimento alla possibilità di utilizzare la tastiera nativa su client VNC per Android.

# Principali caratteristiche
  - il contenuto informativo è costituito da dati codificati in formato [JSON](http://www.json.org/)
  - il trasporto avviene su connessione di tipo [WebSocket](https://tools.ietf.org/html/rfc6455)

# Messaggi di protocollo

  - server --> client : il fuoco è su un elemento di testo che attualmente contiene "text"
```json
{
	"type": "editable",
	"text": "string"
}
```

 - client --> server : è stato premuto il tasto "key" sulla tastiera
```json
{
    "type": "keypressed",
	"key": 0x65
}
```

# Implementazione server (C++)
Il server può essere realizzato utilizzando le classi QT5 WebSocket Server (http://doc.qt.io/qt-5/qwebsocketserver.html)

# Implementazione client (Java)
Il client deve poter essere utilizzato nel progetto Android vnc client. Viene quindi proposta una implementazione in Java che utizza le seguenti librerie:

  - [Java-WebSocket](http://tootallnate.github.io/Java-WebSocket/)
  - [JSONObject](https://developer.android.com/reference/org/json/JSONObject.html)