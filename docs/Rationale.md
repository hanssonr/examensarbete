# Beslut och vägskäl

## Java
Java valdes över andra språk eftersom det först och främst är det språk som vi känner oss tryggast i. Dessutom fungerar Java väl när det kommer till mätning av kod och snabb och säker refactoring.

## LibGDX

## KryoNet eller eget

## NIO vs. IO
När det var dags att börja skriva på nätverksimplementationen ställdes vi kanske inför vårat största val hittills nämligen vilket av Javas APIer för nätverk som vi skulle använda.
Skillnaden dem emellan är kanske, rent teknisk, inte allt för stor – [1] IO är Stream oriented samt blockerar sina trådar, medan NIO är Buffer oriented, non blocking samt använder
sig av Selectors. Rent kodmässigt är det dock ett val som kommer ha stor inverkan, och därför tog det oss ett tag att slutligen bestämma oss.
Egentligen, efter lite efterforskning så borde vi insett att IO torde vara det rätta valet för våran applikation. Då NIO verkar vara att föredra
och framförallt lyser när [1] applikationen ska handskas med många (hundra eller tusentals) anslutningar samtidigt medan IO fungerar bäst när applikationen
ska handskas med få anslutningar som skickar mycket data hela tiden, vilket är så nära på en beskrivning av våran applikation som man kan komma.
Det som dessutom talar för IOs favör är att det generellt sett verkar vara det som rent APImässigt är enklast att använda, både enligt oss efter idogt prototypande och enligt andra [2].
Detta grusade dessvärre våra planer att använda KryoNet [3] som grundpelare, då det är det biblioteket som vi tidigare använt oss utav.

Länkar aktiva i och med 4/3 2014
[1]: http://tutorials.jenkov.com/java-nio/nio-vs-io.html
[2]: http://stackoverflow.com/questions/12892536/how-to-choose-java-nio-vs-io
[3]: https://github.com/EsotericSoftware/kryonet

## UDP vs. TCP
Egentligen var tanken från början att helt ignorera TCP och skriva funktioner över UDP för att få samma funktionalitet.
Men när arbetet framför oss är så pass mycket och ibland överväldigande så valde vi ändå att implementera funktionalitet för TCP, så att vår klient
och server kan skicka viktiga meddelanden mellan varandra. Till exempel när en ny klient vill ansluta.

## Skicka egna paket
Har legat i åtanke ett tag men engagemanget har inte varit där ännu. Först använde vi oss enbart av enums, vilket blev lite krångligare än vi från
början insett. För att kunna skicka egna paket från exempelvis ClientWorldModel och ServerWorldModel så vart användaren tvungen att följa vissa regler.

### Först var det enums

Dels skriva en egen enum som implementerade IPacketType och registrera den i PacketManager..

```java
public enum MyPacketType implements Packet.IPacketType {
    TEST_PACKET, PLAYER_JOIN, REQUEST_INITIAL_STATE;

    private int id;
    private MyPacketType() { this.id = PacketManager.register(this); }

    @Override
    public int getId() { return this.id; }
}
```

.. och dels skriva en klass som representerade paketet..

```java
public class TestPacket extends Packet {
    public TestPacket() {
        super(MyPacketType.TEST_PACKET, 2);
    }
}
```

.. först därefter kunde man if-else mellan de olika inkommande egna paketen..

```java
    @Override
    public void received(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        IPacketType type = PacketManager.getPacketById(buffer.get());
        int id = type.getId();

        if(id == TEST_PACKET.getId()) {
            Log.debug("ClientWorldModel", "SUCCESS!!! TESTPACKET RECEIVED!!!");
        } else {
            Log.debug("ClientWorldModel", "Unhandled packet");
        }
    }
```

.. fungerade det? Javisst, men det kändes inte helt intuitivt och aningen krångligt. Därför sökte vi nya lösningar.

### Sen var det instanceof
Efter några förslag och lite tid så kom vi fram till den (för närvarande) slutgiltliga lösningen

Användaren skriver, precis som innan en egen klass för varje paket. Den enda
restriktionen är att det måste finnas en tom konstruktor..

```java
public class Testpacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE;

    public TestPacket() {}

    public TestPacket(int id) {
        super(TestPacket.class, PACKET_SIZE);
        mBuffer.putInt(id);
    }
}
```

.. samt, någonstans, registrera den nya klassen ..

```java
PacketManager.getInstance().registerPacket(TestPacket.class);
```

.. och därefter skicka och ta emot sina egna paket. Smutt!

```java
    @Override
    public void received(Object obj) {
        if (obj instanceof TestPacket) {
            Log.debug("ClientWorldModel", "Success! Testpacket received");
        }
    }
```

### Förbättringar
Några små förbättringar har sedan gjorts till just paket-hanteringen, bland annat behöver inte användaren längre specifiera hur stort ett paket ska vara utan det räcker med super(KlassNamn.class); samt super.putInt(1); och super.ready();.

## ID på GameObject-klasser för nätverkssynkronisering
En smärre tankenöt som egentligen, rent spontant, inte har något bra svar eller lösning. Åtminstone inte från vår sida. Egentligen är själva huvudproblemet av en mer filosofisk karaktär än ett mer praktiskt sådant.

När en spelare vill, i det här fallet, kasta en granat på klienten i en multiplayer-miljö så får controllen reda på det. Redan här vill vi egentligen skicka ett meddelande till servern som kollar mot reglerna.
```java
public void inputEvent(EventType type) {
...
    case GRENADE:
        // Check against the rules on server
        mClient.sendTcp(new GrenadeCreatePacket(mClient.getId(), pos, dir));
        break;
    }
}
```

På servern i sin tur kollas reglerna och en granat kastas vilket meddelas på klienterna
```java
// A player wants to throw a grenade!
ExternalPlayer ep = getPlayer(gp.clientId);
ep.grenadeThrow();
mServer.sendToAllTCPExcept(new GrenadeCreatePacket(0, gcp.position, gcp.direction), con);
```

Som ritar ut granaten och vill få uppdateringarn från servern kontinuerligt om granatens position och riktning
```java
GrenadeCreatePacket gcp = (GrenadeCreatePacket) packet;
mClientWorldModel.addGrenade(gcp.position, gcp.direction);
mWorldView.getGrenadeRenderer().addGrenade(mClientWorldModel.getGrenades().get(mClientWorldModel.getGrenades().size()-1));
```

(Allt representeras inte i koden just nu utan det är mer pseudo) men själva problemet kommer vid fallet när klienten hela tiden vill få uppdateringar om granaten. Detta blir lite problematiskt i och med att en
granat representeras som en Model-klass som är helt ovetandes om något nätverk över huvudtaget och synkroniseringen av en sådan när en klient får uppdatering från servern blir, om inte omöjlig, näst intill utan ett ID
i klassen som representerar en granat. Och lägger vi till ett id på en model-klass så blir den helt plötsligt en mixad klass, vilka vi eftersträvar att minska på. En paradox som blir svårhanterad i dess teoretiska anda,
eftersom det egentligen inte rör sig om ett praktiskt problem.

Lösningen? Efter mycket huvudbry så måste vi nog ändå lägga till idn på GameObject-klasser vilket i sin tur kommer leda till att många klasser går från ha en concern till att ha två, det vill säga mixade, som i
sin tur kommer strula till det med mätningarna.

### Fortsatta problem i samma område
Det vi stötte på i samma veva som ovanstående var dels att EventSystemet skickar event både till Client och Server eftersom de båda lyssnar på model-event, här är väl lösningen något slags mellanting då den koden
fick flyttas runt lite tills en slags symfoni hittades. Det andra problemet blev med trådar och fysikmotorer, med C++-kraschar i Java-miljö och annat trevligt. Som tur är har vi varit med om det i ett tidigare
projekt och kunde därför relativt snabbt lösa det (när kroppas skapas från olika trådar och fysik-världen är inne i sin step-funktion).

Det som slutligen kan sägas om implementationen av kroppar som rör sig och har regler är att det inte är helt enkelt att få till en synkronisering tillsammans med seperation.

