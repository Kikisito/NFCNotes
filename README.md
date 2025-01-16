# NFCNotes

NFCNotes is a PaperMC plugin compatible with Spigot that allows you to withdraw money using notes. Those notes can also be used to redeem money.

## Requirements

- [Java 21](https://www.azul.com/downloads/)
- [Vault](https://github.com/MilkBowl/Vault)
- [PaperMC](https://papermc.io/). Older versions up to 1.13 may work, but they are not supported. Only the latest Minecraft version is supported.

## Build NFCNotes

To build NFCNotes, you just need to run `mvn clean package` using Maven.

## NFCNotes Events

NFCNotes includes two events: WithdrawEvent and DepositEvent. If you want to use them, just include NFCNotes in your plugin. We strongly recommend Maven.

```
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.Kikisito</groupId>
            <artifactId>NFCNotes</artifactId>
            <version>master</version>
        </dependency>
    </dependencies>
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[GPL 3.0](https://choosealicense.com/licenses/gpl-3.0/)
