# Eventsystem och seperation

## Intro
Ett eventsystem blev implementerat i dagarna eftersom en högre grad av seperation var nödvändig, inte minst för att viss vy-kod residerade i modellen, där den inte borde vara, men också för att försöka bryta ut nätverket ännu mer.

## Hur det fungerar

### I PlayerInput.java (vy)
När en spelare fysiskt trycker på Mouse1 så avfyrar input-klassen ett event till sina lyssnare

```java
if(mKeys.get(MapKeys.SHOOT)) {
    EventHandler.events.notify(new ViewEvent(EventType.SHOOT));
}
```

### Lyssnare: NetworkGameScreen.java (controller) och GameScreen.java (controller)

```java
public class NetworkGameScreen implements ViewListener, ModelListener, NetworkListener {
...
}

public class GameScreen implements ViewListener, ModelListener {
...
}

```

### I vy-lyssnare
Lyssnaren tar emot eventet och slussar vidare till modellen

```java
@Override
public void inputEvent(EventType type) {
    switch (type) {
        case SHOOT:
            // Just notify the model
            mClientWorldModel.getPlayer().shoot();
            break;
    }
}
```

### I modellen
Modellen får reda på att spelaren vill skjuta och kollar mot sina regler om han är tillåten att göra så (för närvarande inga direkta regler) och meddelar då åter lyssnare av model-event om att spelaren faktiskt har skjutit.

```java
public void shoot() {
    // If can shoot
    if(!mHasShot) {
        mHasShot = true;
        EventHandler.events.notify(new ModelEvent(EventType.SHOOT));
    }
}
```

### I modell-lyssnare
Tar emot ett event från modellen och först nu testas kollisionen i modellen, ett skott ritas ut i vyn samt vid nätverk, meddelar servern att man skjutit.

```java
@Override
public void playerEvent(EventType type) {
    switch (type) {
        case SHOOT:
            Vector3[] collide = mClientWorldModel.getCamera().getShootRay();
            Vector3[] visual = mClientWorldModel.getCamera().getVisualRepresentationShoot();

            // The collision
            mClientWorldModel.checkShootCollision(collide);

            // The rendering & sound
            mWorldView.shoot(visual);

            // The network, notify the server that we have shot
            mClient.sendTcp(new ShootPacket(mClient.getId(), collide[0], collide[1], visual[0], visual[1], visual[2], visual[3]));
            break;
    }
}
```

### I nätverks-lyssnare
Tar emot ett paket, exempelvis att en extern spelare skjutit och agerar därefter (ritar ut).

```java
@Override
public void networkEvent(Packet packet) {

    if(packet instanceof ShootPacket) {
        ShootPacket sp = (ShootPacket) packet;

        // The rendering and sound
        mWorldView.shoot(new Vector3[]{sp.vFrom, sp.vTo, sp.vFrom2, sp.vTo2});
    }
}
```