package core.game.logic;

import core.game.entities.BaseMonster;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.entities.Projectile;

public class EntitySpawner {
    String baseClass;
    Properties properties;

    public EntitySpawner (){}

    public EntitySpawner (String baseClass, Properties properties) {
        this.baseClass = baseClass;
        this.properties = properties;
        System.out.println(baseClass + "flags: " + properties.flags);
    }

    public Entity spawnEntity(Entity.Position pos, int tag) {
        switch (baseClass) {
            case "Entity":
                return new Entity(
                    properties.name,
                    properties.health,
                    pos,
                    properties.speed,
                    properties.width,
                    properties.height,
                    properties.states,
                    tag,
                    properties.flags
                );

            case "BaseMonster":
                return new BaseMonster(
                    properties.name,
                    properties.health,
                    pos,
                    properties.speed,
                    properties.width,
                    properties.height,
                    properties.states,
                    tag,
                    properties.flags,
                    properties.monsterSounds
                );

            case "Projectile":
                return new Projectile(
                    properties.name,
                    pos,
                    properties.speed,
                    properties.width,
                    properties.height,
                    properties.states,
                    null, //A projectile spawned by the map, for whatever reason, will have no owner
                    properties.projDamage,
                    properties.flags
                );

            case "PlayerPawn":
                return new PlayerPawn(
                    properties.name,
                    properties.health,
                    pos,
                    properties.speed,
                    properties.width,
                    properties.height,
                    properties.states,
                    tag,
                    properties.flags,
                    properties.playerSounds
                );
        }

        return null;
    }

    public Projectile spawnProjectile(Entity.Position pos, Entity owner) {
        return new Projectile(
                properties.name,
                pos,
                properties.speed,
                properties.width,
                properties.height,
                properties.states,
                owner,
                properties.projDamage,
                properties.flags
        );
    }
}
